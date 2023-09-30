package elasticBarrier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import imop.ast.node.external.BarrierDirective;
import imop.ast.node.external.CompoundStatement;
import imop.ast.node.external.Declaration;
import imop.ast.node.external.DoStatement;
import imop.ast.node.external.Expression;
import imop.ast.node.external.ExpressionStatement;
import imop.ast.node.external.ForStatement;
import imop.ast.node.external.Initializer;
import imop.ast.node.external.MasterConstruct;
import imop.ast.node.external.Node;
import imop.ast.node.external.ParallelConstruct;
import imop.ast.node.external.Statement;
import imop.ast.node.external.WhileStatement;
import imop.lib.builder.Builder;
import imop.lib.transform.BasicTransform;
import imop.lib.transform.simplify.ParallelConstructExpander;
import imop.lib.transform.updater.InsertImmediatePredecessor;
import imop.lib.transform.updater.InsertImmediateSuccessor;
import imop.lib.transform.updater.NodeRemover;
import imop.lib.transform.updater.NodeReplacer;
import imop.lib.util.Misc;
import imop.parser.FrontEnd;

/**
 * Contains methods used to convert benchmark programs to a suitable structure
 * for elastic barrier optimization.
 * 
 * @author kingpin
 *
 */
public class GeneralStructure {

	public static boolean takeOutDeclarationsBetweenIterAndPop(ParallelConstruct parCons) {
		// Step 0: If Dec variable intersects with an already existing variable outside
		// while scope, then return.
		CompoundStatement iterBody = (CompoundStatement) Misc.getEnclosingCFGNonLeafNode(parCons);
		Node node = Misc.getEnclosingCFGNonLeafNode(iterBody);

		if (!(node instanceof WhileStatement) && !(node instanceof ForStatement) && !(node instanceof DoStatement)) {
//			System.err.println("Iteration Statement Not Found");
			return false;
		}

		Statement iterStmt = (Statement) node;
		CompoundStatement outsideIterBody = (CompoundStatement) Misc.getEnclosingCFGNonLeafNode(iterStmt);
		Set<String> outsideST = outsideIterBody.getInfo().getSymbolTable().keySet();
		Boolean noDec = true;
		Set<Declaration> renameDecList = new HashSet<>();

		for (Node n : iterBody.getInfo().getCFGInfo().getElementList()) {
			if (!(n instanceof Declaration)) {
				if (n instanceof ParallelConstruct) {
					break;
				} else {
					return false;
				}
			}
			noDec = false;
			Declaration dec = (Declaration) n;
			if (!dec.getInfo().getIDNameList().isEmpty() && outsideST.contains(dec.getInfo().getDeclaredName())) {
				renameDecList.add(dec);
			}
		}

		if (noDec) {
			return false;
		}

		// Iterating over decl to be renamed
		for (Declaration rDec : renameDecList) {
			String oldName = rDec.getInfo().getDeclaredName();
			String newName = Builder.getNewTempName(oldName);
			Declaration renamedDec = Misc.getRenamedDeclarationWithRenamedInit(rDec, newName, new HashMap<>());
			int startIndex = iterBody.getInfo().getCFGInfo().getElementList().indexOf(rDec);
			NodeReplacer.replaceNodes(rDec, renamedDec);
			HashMap<String, String> rMap = new HashMap<>();
			for (int i = startIndex + 1; i < iterBody.getInfo().getCFGInfo().getElementList().size(); i++) {
				rMap.clear();
				rMap.put(oldName, newName);
				Node rNode = iterBody.getInfo().getCFGInfo().getElementList().get(i);
				BasicTransform.renameFreeNamesInNode(rNode, rMap);
			}
		}
		if (!iterBody.getInfo().getCFGInfo().getElementList().contains(parCons)) {
			return true;
		}

		for (Node d : iterBody.getInfo().getCFGInfo().getElementList()) {
			if (d instanceof Declaration) {
				Declaration dec = (Declaration) d;
				Initializer init = dec.getInfo().getInitializer();

				// Create reqDec.
				Declaration reqDec;

				if (init != null) {
					int equalToIndex = dec.toString().indexOf('=');
					String reqDecStr = dec.toString().substring(0, equalToIndex);
					reqDec = FrontEnd.parseAndNormalize(reqDecStr + ";", Declaration.class);

				} else {
					reqDec = dec;
				}

				// Step 1: If Dec with initializer
				if (init != null) {
					String lhs = dec.getInfo().getDeclaredName();
					String rhs = init.toString();
					String expStr = lhs + " = " + rhs + ";";
					// Step 1a: Take out the Expression Statement
					ExpressionStatement expStmt = FrontEnd.parseAndNormalize(expStr, ExpressionStatement.class);

					// Step 1b: Put it inside the #pop inside a #pom
					MasterConstruct ompMaster = ParallelConstructExpander.setAndGetFirstMaster(parCons);
					CompoundStatement masterBody = (CompoundStatement) ompMaster.getInfo().getCFGInfo().getBody();
					masterBody.getInfo().getCFGInfo().addElement(0, expStmt);
				}

				// Step 2: Take the Dec outside the IterStatement
				NodeRemover.removeNode(dec);
				InsertImmediatePredecessor.insert(iterStmt, reqDec);

			} else {
				break;
			}
		}
//		if (ParallelConstructExpander.debugCnt == 12) {
//			System.out.println("OUTER: ");
//			System.out.println(outsideIterBody);
//		}

		return true;
	}

	public static boolean swapForAndParallel(ParallelConstruct parCons) {

		// Step 1: If CS of for has more than 1 element, it isn't the desired form
		CompoundStatement forBodyOld = (CompoundStatement) Misc.getEnclosingCFGNonLeafNode(parCons);
		if (forBodyOld.getInfo().getCFGInfo().getElementList().size() != 1) {
			System.err.println("More than 1 element inside For");
			return false;
		}

		Node forNode = Misc.getEnclosingCFGNonLeafNode(forBodyOld);
		if (!(forNode instanceof ForStatement)) {
			System.err.println("No For Found");
			return false;
		}
		ForStatement forStmt = (ForStatement) forNode;

		// Step 2: If parCons has any clause, return.
		if (!parCons.getInfo().getOmpClauseList().isEmpty()) {
			System.err.println("Clause Found");
			return false;
		}

		// Step 3: If parCons has any declaration at the parCons body level with
		// the same name as accesses in for init, predicate, or step, return.
		CompoundStatement parConsBody = (CompoundStatement) parCons.getInfo().getCFGInfo().getBody();
		Set<String> forNames = Misc.getFreeNames(forStmt.getInfo().getCFGInfo().getInitExpression());
		forNames.addAll(Misc.getFreeNames(forStmt.getInfo().getCFGInfo().getTerminationExpression()));
		forNames.addAll(Misc.getFreeNames(forStmt.getInfo().getCFGInfo().getStepExpression()));

		Set<String> parNames = parConsBody.getInfo().getSymbolTable().keySet();
		Set<String> conflictingNames = Misc.setIntersection(parNames, forNames);

		if (!conflictingNames.isEmpty()) {
			System.out.println("Scope Binding Conflicts, Applying Renaming");
			HashMap<String, String> rMap = new HashMap<>();

			for (String cName : conflictingNames) {
				for (int j = 0; j < parConsBody.getInfo().getCFGInfo().getElementList().size(); j++) {
					List<Node> parBodyList = parConsBody.getInfo().getCFGInfo().getElementList();
					Node n = parBodyList.get(j);
					if (n instanceof Declaration) {
						Declaration dec = (Declaration) n;
						String decName = dec.getInfo().getDeclaredName();
						if (decName == cName) {
							String rName = Builder.getNewTempName(decName);
							rMap.clear();
							rMap.put(decName, rName);
							Declaration newDec = Misc.getRenamedDeclarationWithRenamedInit(dec, rName, rMap);
							NodeReplacer.replaceNodes(dec, newDec);
							parBodyList = parConsBody.getInfo().getCFGInfo().getElementList();

							int startIndex = parBodyList.indexOf(newDec);

							for (int i = startIndex + 1; i < parBodyList.size(); i++) {
								Node m = parBodyList.get(i);
								BasicTransform.renameFreeNamesInNode(m, rMap);
							}
						}
					}
				}
			}
			return swapForAndParallel(parCons);
		}

		// Step 4: If for has a label over it, return.
		if (forStmt.getInfo().hasLabelAnnotations()) {
			System.err.println("Label found at a WhileStatement");
			return false;
		}
		// Step 5: Swap rest cases

		String predVar = Builder.getNewTempName("condVar");
		Declaration predDec = FrontEnd.parseAndNormalize("int " + predVar + ";", Declaration.class);

		MasterConstruct ompMaster = FrontEnd.parseAndNormalize("#pragma omp master\n{}", MasterConstruct.class);
		CompoundStatement masterBody = (CompoundStatement) ompMaster.getInfo().getCFGInfo().getBody();
		BarrierDirective ompBarrier = FrontEnd.parseAndNormalize("#pragma omp barrier", BarrierDirective.class);
		ParallelConstruct parConsNew = FrontEnd.parseAndNormalize("#pragma omp parallel\n{}", ParallelConstruct.class);
		CompoundStatement parConsBodyNew = (CompoundStatement) parConsNew.getInfo().getCFGInfo().getBody();

		String exp1 = forStmt.getInfo().getCFGInfo().getInitExpression() + ";";
		String exp3 = forStmt.getInfo().getCFGInfo().getStepExpression() + ";";
		ExpressionStatement eStmt1 = FrontEnd.parseAndNormalize(exp1, ExpressionStatement.class);
		ExpressionStatement eStmt3 = FrontEnd.parseAndNormalize(exp3, ExpressionStatement.class);

		InsertImmediatePredecessor.insert(forStmt, parConsNew);
		parConsBodyNew.getInfo().getCFGInfo().addAtLast(ompMaster);
		InsertImmediatePredecessor.insert(parConsNew, predDec);

		String exp2 = predVar + " = (" + forStmt.getInfo().getCFGInfo().getTerminationExpression() + ");";
		ExpressionStatement eStmt2 = FrontEnd.parseAndNormalize(exp2, ExpressionStatement.class);

		masterBody.getInfo().getCFGInfo().addAtLast(eStmt1);
		masterBody.getInfo().getCFGInfo().addAtLast(eStmt2);

		InsertImmediateSuccessor.insert(ompMaster, ompBarrier);

		ForStatement forStmtNew = FrontEnd.parseAndNormalize("for (;" + predVar + ";)\n{}", ForStatement.class);
		CompoundStatement forBodyNew = (CompoundStatement) forStmtNew.getInfo().getCFGInfo().getBody();
		InsertImmediateSuccessor.insert(ompBarrier, forStmtNew);

		NodeRemover.removeNode(forNode);

		forBodyNew.getInfo().getCFGInfo().addAtLast(parConsBody);

		MasterConstruct ompMaster2 = FrontEnd.parseAndNormalize("#pragma omp master\n{}", MasterConstruct.class);
		CompoundStatement masterBody2 = (CompoundStatement) ompMaster2.getInfo().getCFGInfo().getBody();
		BarrierDirective ompBarrier2 = FrontEnd.parseAndNormalize("#pragma omp barrier", BarrierDirective.class);
		BarrierDirective ompBarrier3 = FrontEnd.parseAndNormalize("#pragma omp barrier", BarrierDirective.class);

		forBodyNew.getInfo().getCFGInfo().addAtLast(ompBarrier2);
		forBodyNew.getInfo().getCFGInfo().addAtLast(ompMaster2);
		forBodyNew.getInfo().getCFGInfo().addAtLast(ompBarrier3);

		masterBody2.getInfo().getCFGInfo().addAtLast(eStmt3);
		eStmt2 = FrontEnd.parseAndNormalize(exp2, ExpressionStatement.class);
		masterBody2.getInfo().getCFGInfo().addAtLast(eStmt2);

		return true;
	}

	public static boolean swapWhileAndParallel(ParallelConstruct parCons) {
		// Step 1: If CS of while has more than 1 element, it isn't the desired form

		CompoundStatement whileBody = (CompoundStatement) Misc.getEnclosingCFGNonLeafNode(parCons);
		Node wsNode = Misc.getEnclosingCFGNonLeafNode(whileBody);
		if (!(wsNode instanceof WhileStatement)) {
			System.err.println("No While Found");
			return false;
		}

		WhileStatement wsStmt = (WhileStatement) wsNode;
		if (whileBody.getInfo().getCFGInfo().getElementList().size() > 1) {

			System.err.println("More than 1 element inside While");
			return false;
		}
		// Step 2: If parCons has any clause, return.

		if (!parCons.getInfo().getOmpClauseList().isEmpty()) {
			System.err.println("Clause Found");
			return false;
		}

		// Step 3: If parCons has any declaration at the parCons body level with
		// the same name as accesses in while predicate, return.

		CompoundStatement parConsBody = (CompoundStatement) parCons.getInfo().getCFGInfo().getBody();
		Set<String> whileNames = Misc.getFreeNames(wsStmt.getInfo().getCFGInfo().getPredicate());
		Set<String> parNames = parConsBody.getInfo().getSymbolTable().keySet();
		Set<String> conflictingNames = Misc.setIntersection(parNames, whileNames);

		if (!conflictingNames.isEmpty()) {
			System.out.println("Scope Binding Conflicts, Applying Renaming");
			HashMap<String, String> rMap = new HashMap<>();

			for (String cName : conflictingNames) {
				for (int j = 0; j < parConsBody.getInfo().getCFGInfo().getElementList().size(); j++) {
					List<Node> parBodyList = parConsBody.getInfo().getCFGInfo().getElementList();
					Node n = parBodyList.get(j);
					if (n instanceof Declaration) {
						Declaration dec = (Declaration) n;
						String decName = dec.getInfo().getDeclaredName();
						if (decName == cName) {
							String rName = Builder.getNewTempName(decName);
							rMap.clear();
							rMap.put(decName, rName);
							Declaration newDec = Misc.getRenamedDeclarationWithRenamedInit(dec, rName, rMap);
							parBodyList = parConsBody.getInfo().getCFGInfo().getElementList();
							int startIndex = parBodyList.indexOf(newDec);

							for (int i = startIndex + 1; i < parBodyList.size(); i++) {
								Node m = parBodyList.get(i);
								BasicTransform.renameFreeNamesInNode(m, rMap);
							}
						}
					}
				}
			}
			return swapWhileAndParallel(parCons);
		}

		// Step 4: If while has a label over it, return.
		if (wsStmt.getInfo().hasLabelAnnotations()) {
			System.err.println("Label found at a WhileStatement");
			return false;
		}
		// Step 5: Swap rest cases

		String predVar = Builder.getNewTempName("condVar");
		Declaration decVar = FrontEnd.parseAndNormalize("int " + predVar + ";", Declaration.class);
		ParallelConstruct parConsNew = FrontEnd.parseAndNormalize("#pragma omp parallel\n{}", ParallelConstruct.class);
		InsertImmediatePredecessor.insert(wsStmt, decVar);
		InsertImmediatePredecessor.insert(wsStmt, parConsNew);

		MasterConstruct masterCons = FrontEnd.parseAndNormalize("#pragma omp master\n{}", MasterConstruct.class);
		CompoundStatement parBodyNew = (CompoundStatement) parConsNew.getInfo().getCFGInfo().getBody();
		parBodyNew.getInfo().getCFGInfo().addElement(0, masterCons);
		CompoundStatement masterBody = (CompoundStatement) masterCons.getInfo().getCFGInfo().getBody();
		Expression whilePred = wsStmt.getInfo().getCFGInfo().getPredicate();
		Statement condStmt = FrontEnd.parseAndNormalize(predVar + " = (" + whilePred + ");", Statement.class);
		masterBody.getInfo().getCFGInfo().addElement(0, condStmt);

		Statement ompBarrier = FrontEnd.parseAndNormalize("#pragma omp barrier\n", Statement.class);
		InsertImmediateSuccessor.insert(masterCons, ompBarrier);

		NodeRemover.removeNode(wsStmt);
		InsertImmediateSuccessor.insert(ompBarrier, wsStmt);

		Expression exp = FrontEnd.parseAndNormalize(predVar, Expression.class);
		wsStmt.getInfo().getCFGInfo().setPredicate(exp);

		NodeRemover.removeNode(parCons);
		whileBody.getInfo().getCFGInfo().addAtLast(parConsBody);

		Statement ompBarrier2 = FrontEnd.parseAndNormalize("#pragma omp barrier\n", Statement.class);
		whileBody.getInfo().getCFGInfo().addAtLast(ompBarrier2);

		MasterConstruct masterCons2 = FrontEnd.parseAndNormalize("#pragma omp master\n{}", MasterConstruct.class);
		whileBody.getInfo().getCFGInfo().addAtLast(masterCons2);
		CompoundStatement masterBody2 = (CompoundStatement) masterCons2.getInfo().getCFGInfo().getBody();
		Statement condStmt2 = FrontEnd.parseAndNormalize(predVar + " = (" + whilePred + ");", Statement.class);
		masterBody2.getInfo().getCFGInfo().addAtLast(condStmt2);

		Statement ompBarrier3 = FrontEnd.parseAndNormalize("#pragma omp barrier\n", Statement.class);
		InsertImmediateSuccessor.insert(masterCons2, ompBarrier3);

		return true;
	}

	public static boolean swapDoWhileAndParallel(ParallelConstruct parCons) {

		// Step 1: If CS of do has more than 1 element, it isn't the desired form

		CompoundStatement doBody = (CompoundStatement) Misc.getEnclosingCFGNonLeafNode(parCons);
		CompoundStatement parConsBody = (CompoundStatement) parCons.getInfo().getCFGInfo().getBody();
		CompoundStatement parConsBody2 = FrontEnd.parseAndNormalize(parConsBody.toString(), CompoundStatement.class);

		Node doNode = Misc.getEnclosingCFGNonLeafNode(doBody);
		if (!(doNode instanceof DoStatement)) {
			System.err.println("No DoWhile Found");
			return false;
		}

		DoStatement doStmt = (DoStatement) doNode;
		if (doBody.getInfo().getCFGInfo().getElementList().size() > 1) {
			System.err.println("More than 1 element inside DoWhile");
			return false;
		}
		// Step 2: If parCons has any clause, return.

		if (!parCons.getInfo().getOmpClauseList().isEmpty()) {
			System.err.println("Clause Found");
			return false;
		}

		// Step 3: If parCons has any declaration at the parCons body level with
		// the same name as accesses in while predicate, return.

		Set<String> doNames = Misc.getFreeNames(doStmt.getInfo().getCFGInfo().getPredicate());
		Set<String> parNames = parConsBody.getInfo().getSymbolTable().keySet();
		// Set<String> conflictingNames = Misc.setIntersection(parNames, doNames);
		//
		// if (!conflictingNames.isEmpty()) {
		// System.out.println("Scope Binding Conflicts, Applying Renaming");
		// HashMap<String, String> rMap = new HashMap<>();
		//
		// for (String cName : conflictingNames) {
		// for (int j = 0; j <
		// parConsBody.getInfo().getCFGInfo().getElementList().size(); j++) {
		// List<Node> parBodyList = parConsBody.getInfo().getCFGInfo().getElementList();
		// Node n = parBodyList.get(j);
		// if (n instanceof Declaration) {
		// Declaration dec = (Declaration) n;
		// String decName = dec.getInfo().getDeclaredName();
		// if (decName == cName) {
		// String rName = Builder.getNewTempName(decName);
		// rMap.clear();
		// rMap.put(decName, rName);
		// Declaration newDec = Misc.getRenamedDeclarationWithRenamedInit(dec, rName,
		// rMap);
		// parBodyList = parConsBody.getInfo().getCFGInfo().getElementList();
		// int startIndex = parBodyList.indexOf(newDec);
		//
		// for (int i = startIndex + 1; i < parBodyList.size(); i++) {
		// Node m = parBodyList.get(i);
		// BasicTransform.renameFreeNamesInNode(m, rMap);
		// }
		// }
		// }
		// }
		// }
		// return swapDoWhileAndParallel(parCons);
		// }

		if (!Misc.setIntersection(doNames, parNames).isEmpty()) {
			System.out.println("ScopeIssue");
			return false;
		}

		// Step 4: If do has a label over it, return.
		if (doStmt.getInfo().hasLabelAnnotations()) {
			System.err.println("Label found at a DoWhileStatement");
			return false;
		}
		// Step 5: Swap rest cases

		// Step 5a: Create a variable to hold while predicate
		String predVar = Builder.getNewTempName("condVar");
		Declaration decVar = FrontEnd.parseAndNormalize("int " + predVar + ";", Declaration.class);

		// Step 5b: Create a new #pop to insert the modified while inside it.
		ParallelConstruct parConsNew = FrontEnd.parseAndNormalize("#pragma omp parallel\n{}", ParallelConstruct.class);
		CompoundStatement parBodyNew = (CompoundStatement) parConsNew.getInfo().getCFGInfo().getBody();

		// Step 5c: Insert both "int x" & #pop before original DoStatement
		InsertImmediatePredecessor.insert(doStmt, decVar);
		InsertImmediatePredecessor.insert(doStmt, parConsNew);

		// Step 5d: Remove the existing DoStatement from the program
		NodeRemover.removeNode(doStmt);

		// Step 5e: Insert original S1 followed by #pob
		parBodyNew.getInfo().getCFGInfo().addElement(0, parConsBody);
		BarrierDirective ompBarrier4 = FrontEnd.parseAndNormalize("#pragma omp barrier\n", BarrierDirective.class);
		InsertImmediateSuccessor.insert(parConsBody, ompBarrier4);

		// Step 5e: Create a new #pom & insert it after the previous #pob
		MasterConstruct masterCons = FrontEnd.parseAndNormalize("#pragma omp master\n{}", MasterConstruct.class);
		CompoundStatement masterBody = (CompoundStatement) masterCons.getInfo().getCFGInfo().getBody();
		InsertImmediateSuccessor.insert(ompBarrier4, masterCons);

		// Step 5f: Create a ExpStatement "x = cond" & insert at the 0 index in the #pom
		Expression whilePred = doStmt.getInfo().getCFGInfo().getPredicate();
		Statement condStmt = FrontEnd.parseAndNormalize(predVar + " = (" + whilePred + ");", Statement.class);
		masterBody.getInfo().getCFGInfo().addElement(0, condStmt);

		// Step 5g: Create & insert a #pob after the #pom
		BarrierDirective ompBarrier = FrontEnd.parseAndNormalize("#pragma omp barrier\n", BarrierDirective.class);
		InsertImmediateSuccessor.insert(masterCons, ompBarrier);

		/*
		 * Summary: Inside the new #pop we inserted in order
		 * Original S1 body
		 * #pob
		 * #pom {x=cond}
		 * #pob
		 */

		// Step 5h: Create a new WhileStatement & again insert the above 4 items inside
		// the new while.
		WhileStatement wStmtNew = FrontEnd.parseAndNormalize("while(" + predVar + ")\n{}", WhileStatement.class);
		CompoundStatement wStmtBodyNew = (CompoundStatement) wStmtNew.getInfo().getCFGInfo().getBody();
		InsertImmediateSuccessor.insert(ompBarrier, wStmtNew);

		BarrierDirective ompBarrier2 = FrontEnd.parseAndNormalize("#pragma omp barrier\n", BarrierDirective.class);
		BarrierDirective ompBarrier3 = FrontEnd.parseAndNormalize("#pragma omp barrier\n", BarrierDirective.class);
		MasterConstruct masterCons2 = FrontEnd.parseAndNormalize("#pragma omp master\n{}", MasterConstruct.class);

		wStmtBodyNew.getInfo().getCFGInfo().addElement(0, parConsBody2);
		wStmtBodyNew.getInfo().getCFGInfo().addAtLast(ompBarrier2);
		wStmtBodyNew.getInfo().getCFGInfo().addAtLast(masterCons2);
		wStmtBodyNew.getInfo().getCFGInfo().addAtLast(ompBarrier3);

		CompoundStatement masterBody2 = (CompoundStatement) masterCons2.getInfo().getCFGInfo().getBody();
		Statement condStmt2 = FrontEnd.parseAndNormalize(predVar + " = (" + whilePred + ");", Statement.class);
		masterBody2.getInfo().getCFGInfo().addElement(0, condStmt2);

		// TODO LABEL
		return true;
	}

}
