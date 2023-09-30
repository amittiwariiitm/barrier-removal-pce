package elasticBarrier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import imop.Main;
import imop.ast.node.external.BarrierDirective;
import imop.ast.node.external.BreakStatement;
import imop.ast.node.external.CompoundStatement;
import imop.ast.node.external.CriticalConstruct;
import imop.ast.node.external.Declaration;
import imop.ast.node.external.DoStatement;
import imop.ast.node.external.Expression;
import imop.ast.node.external.ForConstruct;
import imop.ast.node.external.ForStatement;
import imop.ast.node.external.FunctionDefinition;
import imop.ast.node.external.IfStatement;
import imop.ast.node.external.MasterConstruct;
import imop.ast.node.external.Node;
import imop.ast.node.external.ParallelConstruct;
import imop.ast.node.external.Statement;
import imop.ast.node.external.SwitchStatement;
import imop.ast.node.external.TranslationUnit;
import imop.ast.node.external.WhileStatement;
import imop.ast.node.internal.Scopeable;
import imop.lib.builder.Builder;
import imop.lib.cfg.info.ForStatementCFGInfo;
import imop.lib.transform.updater.InsertImmediatePredecessor;
import imop.lib.transform.updater.InsertImmediateSuccessor;
import imop.lib.transform.updater.NodeReplacer;
import imop.lib.util.DumpSnapshot;
import imop.lib.util.Misc;
import imop.parser.FrontEnd;
import imop.parser.Program;

public class Practice {

	public static void addTimer() {
	
		for (ForConstruct fc : Misc.getInheritedEnclosee(Program.getRoot(), ForConstruct.class)) {
	
			String s = Builder.getNewTempName("t");
			String start = "double " + s + ";";
			Declaration dec = FrontEnd.parseAndNormalize(start, Declaration.class);
			InsertImmediatePredecessor.insert(fc, dec);
			String timer = s + " = omp_get_wtime();";
			Statement cs = FrontEnd.parseAndNormalize(timer, Statement.class);
			InsertImmediateSuccessor.insert(dec, cs);
			String s2 = Builder.getNewTempName("t");
			String end = "double " + s2 + ";";
			Declaration decend = FrontEnd.parseAndNormalize(end, Declaration.class);
			InsertImmediateSuccessor.insert(dec, decend);
			String timer2 = s2 + " = omp_get_wtime();";
			Statement cs2 = FrontEnd.parseAndNormalize(timer2, Statement.class);
			InsertImmediateSuccessor.insert(fc, cs2);
			String s3 = Builder.getNewTempName("t");
			String diff = "double " + s3 + ";";
			Declaration diffDeclare = FrontEnd.parseAndNormalize(diff, Declaration.class);
			InsertImmediateSuccessor.insert(decend, diffDeclare);
			String stmt = s3 + " = " + s2 + " - " + s + ";";
			Statement diffStmt = FrontEnd.parseAndNormalize(stmt, Statement.class);
			InsertImmediateSuccessor.insert(cs2, diffStmt);
			String print = "printf(\"time diff " + s + " : %d\"," + s3 + ");";
			Statement printStmt = FrontEnd.parseAndNormalize(print, Statement.class);
			InsertImmediateSuccessor.insert(diffStmt, printStmt);
	
		}
	}

	public static void loopInterchange() {
		for (ForStatement fs : Misc.getInheritedEnclosee(Program.getRoot(), ForStatement.class)) {
			CompoundStatement cs = (CompoundStatement) fs.getInfo().getCFGInfo().getBody();
	
			List<Node> elist = cs.getInfo().getCFGInfo().getElementList();
	
			if (elist.size() != 1 || !(elist.get(0) instanceof ForStatement)) {
				continue;
			}
			// System.out.println(fs);
			ForStatementCFGInfo innerForCFG = (ForStatementCFGInfo) elist.get(0).getInfo().getCFGInfo();
			ForStatementCFGInfo outerForCFG = (ForStatementCFGInfo) fs.getInfo().getCFGInfo();
	
			String init = innerForCFG.getInitExpression().toString();
			String term = innerForCFG.getTerminationExpression().toString();
			String step = innerForCFG.getStepExpression().toString();
			String innerFor = "for(" + init + ";" + term + ";" + step + ")";
	
			String init0 = outerForCFG.getInitExpression().toString();
			String term0 = outerForCFG.getTerminationExpression().toString();
			String step0 = outerForCFG.getStepExpression().toString();
			String outerFor = "for(" + init0 + ";" + term0 + ";" + step0 + ")";
	
			String body = innerForCFG.getBody().toString();
	
			String finalString = innerFor + "{\n " + outerFor + body + "}";
			Statement interchangedFor = FrontEnd.parseAndNormalize(finalString, Statement.class);
			NodeReplacer.replaceNodes(fs, interchangedFor);
			System.out.println(interchangedFor);
		}
	
		DumpSnapshot.forceDumpRoot("interchnge");
	}

	public static void convertWhileToDoWhile(WhileStatement ws) {
		Statement body = ws.getInfo().getCFGInfo().getBody();
		Expression cond = ws.getInfo().getCFGInfo().getPredicate();
	
		String unrolledDoWhileStr = "do{" + "if(!(" + cond + ")){break;}" + body + "}" + "while(1);";
		String unrolledForStr = "for(;" + cond + ";)" + body;
		System.out.println(unrolledDoWhileStr);
		System.out.println(unrolledForStr);
	
	}

	public static void printUnrolledWhile(WhileStatement s, int unrollCount) {
	
		Statement body = s.getInfo().getCFGInfo().getBody();
		Expression cond = s.getInfo().getCFGInfo().getPredicate();
	
		String unrolledWhileStr = "while(" + cond + "){" + body + "if(!(" + cond + ")){break;}"
				+ "\n#pragma omp barrier\n" + body + "}";
		WhileStatement unrolledWhileStmt = FrontEnd.parseAndNormalize(unrolledWhileStr, WhileStatement.class);
		System.out.println(unrolledWhileStmt);
	
	}

	public static Set<Node> printAllLoopsContainingStatement(Statement n) {
		Set<Node> loopSet = new HashSet<>();
		Node loop = Misc.getEnclosingLoopOrForConstruct(n);
	
		while (loop != null) {
			loopSet.add(loop);
			loop = Misc.getEnclosingLoopOrForConstruct(loop);
		}
	
		return loopSet;
	}

	public static void printFunctionContainingStatement(Statement n) {
		FunctionDefinition fd = Misc.getEnclosingFunction(n);
	
		if (fd != null) {
			System.out.println(fd.getInfo().getFunctionName());
		}
	}

	public static void statementInsideParallelConstruct(Statement n) {
		ParallelConstruct pc = Misc.getEnclosingNode(n, ParallelConstruct.class);
	
		if (pc != null) {
			System.out.println(pc);
		}
	}

	public static void twoStatementsBelongToSameCritical(Node n, Node m) {
		CriticalConstruct ccn = Misc.getEnclosingNode(n, CriticalConstruct.class);
		CriticalConstruct ccm = Misc.getEnclosingNode(m, CriticalConstruct.class);
	
		if (ccn == null || ccm == null) {
			return;
		}
	
		if (ccn == ccm) {
			System.out.println(ccn);
		}
	}

	public static void printAllImmediateLoopsWithBreak() {
		for (BreakStatement breaker : Misc.getExactEnclosee(Program.getRoot(), BreakStatement.class)) {
			Node encloser = Misc.getEnclosingLoopOrSwitch(breaker);
	
			if (!(encloser instanceof SwitchStatement)) {
				System.out.println(encloser);
			}
		}
	}

	public static void printAllEnclosingBlocks(Node n) {
		Scopeable blocker;
		blocker = Misc.getEnclosingBlock(n);
	
		if (blocker == null || blocker instanceof TranslationUnit || blocker instanceof FunctionDefinition)
			return;
	
		System.out.println(blocker);
		printAllEnclosingBlocks((Node) blocker);
	}

	public static void printAllSerialLoopsInPostOrder() {
		Set<Class<? extends Node>> loopSet = new HashSet<>();
		loopSet.add(WhileStatement.class);
		loopSet.add(ForStatement.class);
		loopSet.add(DoStatement.class);
	
		for (Node stmt : Misc.getInheritedPostOrderEnclosee(Program.getRoot(), loopSet)) {
			System.out.println(stmt);
		}
	}

	public static void printAllSerialLoops() {
		Set<Class<? extends Node>> loopSet = new HashSet<>();
		loopSet.add(WhileStatement.class);
		loopSet.add(ForStatement.class);
		loopSet.add(DoStatement.class);
	
		for (Node stmt : Misc.getInheritedEnclosee(Program.getRoot(), loopSet)) {
			System.out.println(stmt);
		}
	}

	public static void whileInsideIf() {
	
		for (IfStatement is : Misc.getInheritedEnclosee(Program.getRoot(), IfStatement.class)) {
			if (!Misc.getInheritedEnclosee(is, WhileStatement.class).isEmpty()) {
				System.out.println(is);
			}
		}
	
	}

	public static void printWhileAndUnrollAndPrintToFile() {
		int i = 0;
	
		for (WhileStatement w : Misc.getInheritedEnclosee(Program.getRoot(), WhileStatement.class)) {
			w.getInfo().unrollLoop(3);
			DumpSnapshot.printToFile(w, "w" + i);
			System.out.println(w);
			i++;
		}
	}

	public static void giveStatementsForLabel(String label) {
	
		for (FunctionDefinition func : Program.getRoot().getInfo().getAllFunctionDefinitions()) {
			System.out.println(func.getInfo().getStatementWithLabel(label));
		}
	}

	public static void main(String[] args) {
			System.out.println("sjbc");
			Main.totalTime = System.nanoTime();
			Program.parseNormalizeInput(args);
	//		System.out.println(Program.getRoot());
	//		System.out.println(Program.getRoot().getInfo().getMainFunction());
	//		DumpSnapshot.forceDumpRoot("bat");
	//		DumpSnapshot.printToFile(Program.getRoot().getInfo().getMainFunction(), "bowl.i");
	
	//		FunctionDefinitionInfo fInfo = Program.getRoot().getInfo().getMainFunction().getInfo();
	
	//		for(WhileStatement ws : Misc.getExactEnclosee(Program.getRoot(), WhileStatement.class)) {
	//		}
	//		FunctionDefinition mainFunc = Program.getRoot().getInfo().getMainFunction();
	//		String printRes = "printf(\"Hi\");";
	//		Statement cs = FrontEnd.parseAndNormalize(printRes, Statement.class);
	//		System.out.println(cs);
	//		addTimer();
			TimingProfiler.addIMsuiteTimers();
			DumpSnapshot.forceDumpRoot("tid-timer");
	//		DriverModule.mergeParRegs();
	
		}

	public static MasterConstruct setAndGetLastMaster(ParallelConstruct parCons) {
		CompoundStatement popBody = (CompoundStatement) parCons.getInfo().getCFGInfo().getBody();
	
		List<Node> elemList = popBody.getInfo().getCFGInfo().getElementList();
		int size = elemList.size();
	
		if (size <= 0) {
			BarrierDirective bd = FrontEnd.parseAndNormalize("#pragma omp barrier\n", BarrierDirective.class);
			MasterConstruct mc = FrontEnd.parseAndNormalize("#pragma omp master\n{}", MasterConstruct.class);
			popBody.getInfo().getCFGInfo().addAtLast(bd);
			popBody.getInfo().getCFGInfo().addAtLast(mc);
			return mc;
		}
	
		Node lastNode = elemList.get(size - 1);
	
		if (lastNode instanceof MasterConstruct) {
			if (size == 1)
				return (MasterConstruct) lastNode;
			if (elemList.get(size - 2) instanceof BarrierDirective) {
				return (MasterConstruct) lastNode;
			} else {
				BarrierDirective bd = FrontEnd.parseAndNormalize("#pragma omp barrier\n", BarrierDirective.class);
				MasterConstruct mc = FrontEnd.parseAndNormalize("#pragma omp master\n{}", MasterConstruct.class);
				popBody.getInfo().getCFGInfo().addAtLast(bd);
				popBody.getInfo().getCFGInfo().addAtLast(mc);
				return mc;
			}
		} else if (lastNode instanceof BarrierDirective) {
			MasterConstruct mc = FrontEnd.parseAndNormalize("#pragma omp master\n{}", MasterConstruct.class);
			popBody.getInfo().getCFGInfo().addAtLast(mc);
			return mc;
		} else {
			BarrierDirective bd = FrontEnd.parseAndNormalize("#pragma omp barrier\n", BarrierDirective.class);
			MasterConstruct mc = FrontEnd.parseAndNormalize("#pragma omp master\n{}", MasterConstruct.class);
			popBody.getInfo().getCFGInfo().addAtLast(bd);
			popBody.getInfo().getCFGInfo().addAtLast(mc);
			return mc;
		}
	}

}
