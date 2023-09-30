package elasticBarrier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.sound.midi.SysexMessage;
import javax.swing.plaf.synth.SynthOptionPaneUI;

import imop.ast.info.cfgNodeInfo.FunctionDefinitionInfo;
import imop.ast.node.external.BreakStatement;
import imop.ast.node.external.CompoundStatement;
import imop.ast.node.external.CriticalConstruct;
import imop.ast.node.external.DoStatement;
import imop.ast.node.external.ExpressionStatement;
import imop.ast.node.external.ForConstruct;
import imop.ast.node.external.ForStatement;
import imop.ast.node.external.FunctionDefinition;
import imop.ast.node.external.IfStatement;
import imop.ast.node.external.IterationStatement;
import imop.ast.node.external.Node;
import imop.ast.node.external.ParameterDeclaration;
import imop.ast.node.external.Statement;
import imop.ast.node.external.SwitchStatement;
import imop.ast.node.external.TranslationUnit;
import imop.ast.node.external.WhileStatement;
import imop.ast.node.internal.BeginNode;
import imop.ast.node.internal.CallStatement;
import imop.ast.node.internal.Scopeable;
import imop.lib.analysis.flowanalysis.Cell;
import imop.lib.analysis.flowanalysis.Definition;
import imop.lib.analysis.flowanalysis.FreeVariable;
import imop.lib.cfg.info.ForStatementCFGInfo;
import imop.lib.transform.BasicTransform;
import imop.lib.transform.simplify.ParallelConstructExpander;
import imop.lib.transform.simplify.RedundantSynchronizationRemovalForYA;
import imop.lib.transform.updater.NodeRemover;
import imop.lib.transform.updater.NodeReplacer;
import imop.lib.util.CellList;
import imop.lib.util.CellSet;
import imop.lib.util.DumpSnapshot;
import imop.lib.util.Misc;
import imop.parser.Program;

public class WorkOnImop {

	public static enum CompPhase {
		ELASTRUCT
	}

	public static CompPhase compilationPhase;
	public static double deltaForWorkDiv = 0.8;
	public static String deltaName;
	public static String totalThreadsName;
	public static void main(String[] args) {
		Program.parseNormalizeInput(args);
		Utilities.addAllMissingStandardDefinitions();

		if (WorkOnImop.compilationPhase == null) {
			WorkOnImop.compilationPhase = CompPhase.ELASTRUCT; // Manually set to pick the phase.
		}
		switch (compilationPhase) {

		case ELASTRUCT:
			ElasticOptimizationDriver.elasticStructureGenerator();
			break;
		default:
			break;

		}

		System.exit(0);

		for (ForConstruct forConstruct : Misc.getInheritedEnclosee(Program.getRoot(), ForConstruct.class)) {
			System.out.println(forConstruct);
			System.out.println(CostExpressionGenerator.getCostExpression(forConstruct.getInfo().getCFGInfo().getBody(),
					new CostExpressionArgument()));
//			CostExpressionReturn cer = forConstruct.getInfo().getCFGInfo().getBody()
//					.accept(new CostExpressionGenerator(), new CostExpressionArgument());
//			System.out.println(cer.code);
			System.out.println("///////////////***********/////////////");
//		for (ForStatement forConstruct : Misc.getInheritedEnclosee(Program.getRoot(), ForStatement.class)) {
//			System.out.println("?#########\n"+forConstruct);
//			CostExpressionReturn cer = forConstruct.getInfo().getCFGInfo().getBody()
//					.accept(new CostExpressionGenerator());
//			System.out.println("Code: " + cer.code);
//			System.out.println("Cost: " + cer.cost);

//			CellSet cs = new CellSet();
//			System.out.println("=======================================");
//			System.out.println(forConstruct.getInfo().getAccesses());
//			for (ExpressionStatement expS: Misc.getInheritedEnclosee(forConstruct, ExpressionStatement.class)) {
//				System.out.println("\n" + expS);
//				System.out.println("rlns: " + Utilities.readLocalNonStruct(expS));
//				System.out.println("wlns: " + Utilities.writeLocalNonStruct(expS));
//				System.out.println("rins: " + Utilities.readInnerNonStruct(expS));
//				System.out.println("wins: " + Utilities.writeInnerNonStruct(expS));
//				System.out.println("rons: " + Utilities.readOuterNonStruct(expS));
//				System.out.println("wons: " + Utilities.writeOuterNonStruct(expS));
//				System.out.println("ros: " + Utilities.readOuterStruct(expS));
//				System.out.println("wos: " + Utilities.writeOuterStruct(expS));
//				System.out.println("===============");
//			}
//			for (IterationStatement it: Misc.getInheritedEnclosee(forConstruct, IterationStatement.class)) {
//				System.out.println("\n\n\n" + it);
//				System.out.println("Reads: " + StructStringGetter.getStructReadStrings(it));
//				System.out.println("Writes: " + StructStringGetter.getStructWriteStrings(it));
//				System.out.println("===============");
//			}

//			System.out.println(CostExpressionGenerator.insertWLLoop(forConstruct, forConstruct, "WLI"));

//			

//			for (Node n: Misc.getInheritedEnclosee(forConstruct, Statement.class)) {
//				System.out.println(n + ":" + Utilities.isDuplicableInDifferentScope(n));
//				System.out.println("=========================");
//			}
		}

//		String d = Utilities.getIndependentCostForLabel("LIndependent_start_0");
//		String d = Utilities.getFracCostForIfLabel("LabelIf0");
//		for (FunctionDefinition funcDef : Program.getRoot().getInfo().getAllFunctionDefinitions()) {
//			for (ParameterDeclaration param : funcDef.getInfo().getCFGInfo().getParameterDeclarationList()) {
//				System.out.println("Reaching definitions for " + param + ": ");
//				for (Definition def : param.getInfo().getReachingDefinitions()) {
//					System.out.println(def);
//				}
//				System.out.println("----\n");
//			}
//		}
//		System.exit(0);
//		for (Cell c : Program.getRoot().getInfo().getUsedCells()) {
//			System.out.println(c);
//		}
//		System.exit(0);
//		ElasticOptimization.putLabelAcrossIndependentCode();
//		Utilities.addGetWTimeDeclaration();
//		ElasticOptimization.putLabelPrintfTimer();
//		System.exit(0);

//		getLoopsInsideForConstructs();
//		System.out.println(Program.getRoot());
//		System.exit(0);
//		ElasticOptimization.elasticStructureGenerator();
//		addIMsuiteTimers();
	}

	public static int iterateTillStatementOrMaster(int startInd, CompoundStatement cs, List<Node> statementList) {
		List<Node> elementList = cs.getInfo().getCFGInfo().getElementList();
		int endInd = elementList.size();

		if (startInd >= endInd) {
			return startInd;
		}

		Node currElem = elementList.get(startInd);
		String currStr = currElem.accept(new ElasticStructurePrinter(), 0);
	
		while (currStr.trim().equals("S") || currStr.trim().equals("Master S")) {
			statementList.add(currElem);
			startInd++;
			if (startInd >= endInd) {
				break;
			}
			currElem = elementList.get(startInd);
			currStr = currElem.accept(new ElasticStructurePrinter(), 0);
		}

		return startInd;
	}

	public static void printTheStructure() {
		ElasticStructurePrinter esp = new ElasticStructurePrinter();
		String retStr = ("\n===========================PRINTING THE STRUCTURE==========================\n");

		for (FunctionDefinition fd : Misc.getInheritedEnclosee(Program.getRoot(), FunctionDefinition.class)) {
			String fdStr = fd.accept(esp, 0);
			if (fdStr.trim().equals("S") || fdStr.trim().isBlank()) {
				continue;
			}
			retStr += fdStr + "\n";
			retStr += ("============================\n");
		}

		DumpSnapshot.printToFile(retStr, Program.fileName + "-structure.txt");
		System.out.println("\t\tSuccessfully generated the structure.\n");
	}

	public static void getLoopsInsideForConstructs() {
		for (ForConstruct fc : Misc.getExactPostOrderEnclosee(Program.getRoot(), ForConstruct.class)) {
			for (Node n : fc.getInfo().getCFGInfo().getIntraTaskCFGLeafContents()) {
				if (n instanceof BeginNode) {
					BeginNode bn = (BeginNode) n;
					Node parentBn = bn.getParent();

					if (parentBn instanceof ForStatement) {
						System.out.println("\n\n\n%%%%%%%%%%%%%%");
//						System.out.println(Utilities.insertCountExpressionforFor((ForStatement) parentBn));
//						System.out.println(parentBn);
					}
				}
			}
		}
		DumpSnapshot.forceDumpRoot("countExp");
	}

	public static void checkDoabilityForFC() {
		System.out.println("hyper aman");
		for (ForConstruct fc : Misc.getInheritedEnclosee(Program.getRoot(), ForConstruct.class)) {
			if (!Utilities.doabilityCheckForStartEnd(fc)) {
				System.out.println(fc + "\n==================================\n");
			}
		}
	}
}
