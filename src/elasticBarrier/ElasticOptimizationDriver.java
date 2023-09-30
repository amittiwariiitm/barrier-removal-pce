package elasticBarrier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import imop.Main;
import imop.ast.node.external.BarrierDirective;
import imop.ast.node.external.CompoundStatement;
import imop.ast.node.external.Declaration;
import imop.ast.node.external.ForConstruct;
import imop.ast.node.external.ForStatement;
import imop.ast.node.external.FunctionDefinition;
import imop.ast.node.external.IfStatement;
import imop.ast.node.external.Node;
import imop.ast.node.external.ParallelConstruct;
import imop.ast.node.external.ReturnStatement;
import imop.ast.node.external.Statement;
import imop.ast.node.internal.PreCallNode;
import imop.lib.builder.Builder;
import imop.lib.cfg.CFGLinkFinder;
import imop.lib.cfg.link.node.CFGLink;
import imop.lib.cfg.link.node.CompoundElementLink;
import imop.lib.transform.BasicTransform;
import imop.lib.transform.simplify.FunctionInliner;
import imop.lib.transform.simplify.ParallelConstructExpander;
import imop.lib.transform.simplify.RedundantSynchronizationRemoval;
import imop.lib.transform.simplify.RedundantSynchronizationRemovalForYA;
import imop.lib.transform.updater.InsertImmediatePredecessor;
import imop.lib.transform.updater.InsertImmediateSuccessor;
import imop.lib.util.DumpSnapshot;
import imop.lib.util.Misc;
import imop.parser.FrontEnd;
import imop.parser.Program;

public class ElasticOptimizationDriver {
	static int id = 0;
	static String labelSubstr = "LStartEnd";

	public static void elasticStructureGenerator() {
		System.err.println("Pass: Removing declarations for unused elements.");
		Program.getRoot().getInfo().removeUnusedElements();

		System.err.println("Pass: Merging parallel regions.");
		ParallelConstructExpander.mergeParallelRegions(Program.getRoot());

		System.err.println("Pass: Removing redundant barriers.");
		RedundantSynchronizationRemovalForYA.removeBarriers(Program.getRoot());
//		DumpSnapshot.forceDumpRoot("debug1");

		System.err.println("Pass: Inlining function-calls, selectively. (See notes on BarrElim.)");
		FunctionDefinition mainFunc = Program.getRoot().getInfo().getMainFunction();
		FunctionInliner.inline(mainFunc);
//		DumpSnapshot.forceDumpRoot("debug2");

		System.err.println("Pass: Removing declarations for unused elements.");
		Program.getRoot().getInfo().removeUnusedElements();

		System.err.println("Pass: Merging parallel regions.");
		ParallelConstructExpander.mergeParallelRegions(Program.getRoot());
//		DumpSnapshot.forceDumpRoot("debug3");

		System.err.println("Pass: Removing redundant barriers.");
		RedundantSynchronizationRemovalForYA.removeBarriers(Program.getRoot());

		System.err.println("Pass: Removing declarations for unused elements.");
		Program.getRoot().getInfo().removeUnusedElements();

		System.err.println("Pass: Removing redundant scoping.");
		Program.getRoot().getInfo().removeExtraScopes();

		System.err.println("Pass: Inlining function-calls, selectively. (See notes on BarrElim.)");
		mainFunc = Program.getRoot().getInfo().getMainFunction();
		FunctionInliner.inline(mainFunc);
//		DumpSnapshot.forceDumpRoot("debug4");

		System.err.println("Pass: Removing declarations for unused elements.");
		Program.getRoot().getInfo().removeUnusedElements();

		System.err.println("Pass: Merging parallel regions.");
		ParallelConstructExpander.mergeParallelRegions(Program.getRoot());
//		DumpSnapshot.forceDumpRoot("debug5");

		System.err.println("Pass: Removing redundant barriers.");
		RedundantSynchronizationRemovalForYA.removeBarriers(Program.getRoot());

		System.err.println("Pass: Removing declarations for unused elements.");
		Program.getRoot().getInfo().removeUnusedElements();

		System.err.println("Pass: Removing redundant scoping.");
		Program.getRoot().getInfo().removeExtraScopes();

		System.err.println("Pass: Removing empty constructs.");
		BasicTransform.removeEmptyConstructs(Program.getRoot());

		DumpSnapshot.forceDumpRoot("elastruct");
		System.out.println("\t\tSuccessfully elastructured. Yay!\n");
		System.exit(0);
	}

	public static void startEndConverterSensitive() {
		for (ForConstruct fc : Misc.getInheritedPostOrderEnclosee(Program.getRoot(), ForConstruct.class)) {
			CostExpressionGeneratorSensitive.convertToStartEndSensitive(fc);
		}
		RedundantSynchronizationRemovalForYA.removeBarriers(Program.getRoot());
		System.out.println("\t\tSuccessfully generated start-end-sensitive. Yay!\n");
		DumpSnapshot.forceDumpRoot("startendsensitive");
		System.exit(0);
	}

}
