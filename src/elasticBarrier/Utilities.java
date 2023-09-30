package elasticBarrier;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import imop.ParseCheckMain;
import imop.ast.node.external.APostfixOperation;
import imop.ast.node.external.BarrierDirective;
import imop.ast.node.external.CompoundStatement;
import imop.ast.node.external.CriticalConstruct;
import imop.ast.node.external.Declaration;
import imop.ast.node.external.DotId;
import imop.ast.node.external.Expression;
import imop.ast.node.external.ExpressionStatement;
import imop.ast.node.external.ForConstruct;
import imop.ast.node.external.ForStatement;
import imop.ast.node.external.FunctionDefinition;
import imop.ast.node.external.Node;
import imop.ast.node.external.OmpForAdditive;
import imop.ast.node.external.OmpForCondition;
import imop.ast.node.external.OmpForGECondition;
import imop.ast.node.external.OmpForGTCondition;
import imop.ast.node.external.OmpForInitExpression;
import imop.ast.node.external.OmpForLECondition;
import imop.ast.node.external.OmpForLTCondition;
import imop.ast.node.external.OmpForMultiplicative;
import imop.ast.node.external.OmpForReinitExpression;
import imop.ast.node.external.OmpForSubtractive;
import imop.ast.node.external.ParameterDeclaration;
import imop.ast.node.external.PostDecrementId;
import imop.ast.node.external.PostIncrementId;
import imop.ast.node.external.PostfixExpression;
import imop.ast.node.external.PostfixOperationsList;
import imop.ast.node.external.PreDecrementId;
import imop.ast.node.external.PreIncrementId;
import imop.ast.node.external.ReturnStatement;
import imop.ast.node.external.ShortAssignMinus;
import imop.ast.node.external.ShortAssignPlus;
import imop.ast.node.external.Statement;
import imop.ast.node.external.TranslationUnit;
import imop.ast.node.internal.BeginNode;
import imop.ast.node.internal.CallStatement;
import imop.ast.node.internal.PostCallNode;
import imop.ast.node.internal.PreCallNode;
import imop.ast.node.internal.Scopeable;
import imop.ast.node.internal.SimplePrimaryExpression;
import imop.lib.analysis.flowanalysis.AddressCell;
import imop.lib.analysis.flowanalysis.Cell;
import imop.lib.analysis.flowanalysis.Definition;
import imop.lib.analysis.flowanalysis.Symbol;
import imop.lib.builder.Builder;
import imop.lib.cfg.CFGLinkFinder;
import imop.lib.cfg.link.node.CFGLink;
import imop.lib.cfg.link.node.CompoundElementLink;
import imop.lib.cfg.link.node.CriticalBeginLink;
import imop.lib.cfg.link.node.CriticalBodyLink;
import imop.lib.cfg.link.node.DoBodyLink;
import imop.lib.cfg.link.node.ForBodyLink;
import imop.lib.cfg.link.node.FunctionBodyLink;
import imop.lib.cfg.link.node.FunctionParameterLink;
import imop.lib.cfg.link.node.IfElseBodyLink;
import imop.lib.cfg.link.node.IfThenBodyLink;
import imop.lib.cfg.link.node.MasterBeginLink;
import imop.lib.cfg.link.node.MasterBodyLink;
import imop.lib.cfg.link.node.OmpForBodyLink;
import imop.lib.cfg.link.node.OrderedBodyLink;
import imop.lib.cfg.link.node.ParallelBodyLink;
import imop.lib.cfg.link.node.SectionsSectionBodyLink;
import imop.lib.cfg.link.node.SingleBeginLink;
import imop.lib.cfg.link.node.SingleBodyLink;
import imop.lib.cfg.link.node.SwitchBodyLink;
import imop.lib.cfg.link.node.TaskBodyLink;
import imop.lib.cfg.link.node.WhileBodyLink;
import imop.lib.transform.updater.InsertImmediatePredecessor;
import imop.lib.transform.updater.NodeRemover;
import imop.lib.util.CellList;
import imop.lib.util.CellSet;
import imop.lib.util.Misc;
import imop.parser.FrontEnd;
import imop.parser.Program;

public class Utilities {

	private static CellSet inputDependentCellSet = null;
	private static Set<Node> visitedNodes = new HashSet<>();

	public static int findIndexOf(String str) {
		int i = -1;
		for (Node elemOfTu : Program.getRoot().getF0().getNodes()) {
			i++;
			if (elemOfTu.toString().trim().equals(str)) {
				return i;
			}
		}
		return -1;
	}

	private static boolean doabilityRecursive(ForConstruct fc, int index, CompoundStatement cs) {
		int lastIndexOfcs = cs.getInfo().getCFGInfo().getElementList().size() - 1;
		if (index <= lastIndexOfcs) {
			for (int i = index; i <= lastIndexOfcs; i++) {
				Node n = cs.getInfo().getCFGInfo().getElementList().get(i);

				for (Node m : n.getInfo().getCFGInfo().getIntraTaskCFGLeafContents()) {
					if ((m instanceof BeginNode) && (m.getParent() instanceof ForConstruct)) {
						ForConstruct nextFC = (ForConstruct) m.getParent();
						if (Utilities.areTwoNodesConflicting(fc, nextFC)) {
							Misc.warnDueToLackOfFeature(
									"Unable to generate start-end for the first for-construct below, "
											+ "due to a conflict with the next for-construct in the same phase: "
											+ fc.toString() + "=========" + nextFC.toString(),
									fc);
							return false;
						}
					}
				}

				if (n instanceof BarrierDirective || n.getInfo().hasBarrierInCFG()) {
					return true;
				}

			}
		}
		// Go to the parent of this cs recursively.
		CFGLink enclosingLink = CFGLinkFinder.getCFGLinkFor(cs);
		if (enclosingLink instanceof CompoundElementLink) {
			CompoundElementLink cel = (CompoundElementLink) enclosingLink;
			return doabilityRecursive(fc, cel.getIndex() + 1, (CompoundStatement) cel.getEnclosingNode());
		} else if (enclosingLink instanceof CriticalBodyLink || enclosingLink instanceof MasterBodyLink
				|| enclosingLink instanceof OrderedBodyLink || enclosingLink instanceof TaskBodyLink
				|| enclosingLink instanceof IfElseBodyLink || enclosingLink instanceof IfThenBodyLink
				|| enclosingLink instanceof SwitchBodyLink) {
			Node nn = enclosingLink.getEnclosingNode();
			CompoundElementLink cel = (CompoundElementLink) CFGLinkFinder.getCFGLinkFor(nn);
			return doabilityRecursive(fc, cel.getIndex() + 1, cel.enclosingNonLeafNode);
		} else if (enclosingLink instanceof DoBodyLink) {
			return false;
		} else if (enclosingLink instanceof ForBodyLink) {
			return false;
		} else if (enclosingLink instanceof FunctionBodyLink) {
			FunctionDefinition fd = (FunctionDefinition) enclosingLink.getEnclosingNode();
			for (CallStatement cstmt : fd.getInfo().getCallersOfThis()) {
				CompoundElementLink cel = (CompoundElementLink) CFGLinkFinder.getCFGLinkFor(cstmt);
				if (!doabilityRecursive(fc, cel.getIndex() + 1, cel.enclosingNonLeafNode)) {
					return false;
				}
			}
			return true;
		} else if (enclosingLink instanceof ParallelBodyLink) {
			return true;
		} else if (enclosingLink instanceof WhileBodyLink) {
			return false;
		} else {
			assert (false) : "Unknown encloser found for the body: " + cs;
			return true;
		}
	}

	public static boolean doabilityCheckForStartEnd(ForConstruct fc) {
		// True : pass
		// False: fail

		CompoundElementLink cel = (CompoundElementLink) CFGLinkFinder.getCFGLinkFor(fc);
		int index = cel.getIndex();
		CompoundStatement cs = (CompoundStatement) cel.getEnclosingNode();
		return doabilityRecursive(fc, index + 1, cs);
	}

	public static void emitElasticCode() {
		int lid = 0;
		/*
		 * Input Pattern:
		 * WLLoop1
		 * ....
		 * B1
		 * start1Calc
		 * end1Calc
		 * ....
		 * Loop1
		 * WLLoop2
		 * ....
		 * B2
		 * start2Calc
		 * end2Calc
		 * ....
		 * Loop2
		 * 
		 * Global Declarations:
		 * double *threadRemWLArray;
		 * int *threadProgress;
		 * int *threadStartId;
		 * int totalThreads;
		 * int maxChunkSize = 750;
		 * float safeScopeFraction;
		 * float scopeLimit;
		 * double progressCheckpointInterval;
		 * double remWLCheckpointInterval;
		 * char doneInit;
		 * double sleepFrac = 0.1;
		 * 
		 * Local Declarations:
		 * int *neighIndex;
		 * int *neightTID;
		 * int *neighLabel;
		 * double *depArrayLoop2;
		 * 
		 * STEPS:
		 * Step 1: Add before B1:
		 * ....#pragma omp master
		 * ....{
		 * ....doneInit = 0;
		 * ....}
		 */

		/*
		 * 
		 * Step 2: After end1Calc:
		 * ....neighIndex = (int *) malloc(sizeof(int) * maxChunkSize);
		 * ....neighTID = (int *) malloc(sizeof(int) * maxChunkSize);
		 * ....neighLabel = (int *) malloc(sizeof(int) * maxChunkSize);
		 * ....depArrayLoop2 = (int *) malloc(sizeof(int) * maxChunkSize);
		 */

		String eStr2 = "neighIndex = (int *) malloc(sizeof(int) * maxChunkSize);";
		eStr2 += "neighTID = (int *) malloc(sizeof(int) * maxChunkSize);";
		eStr2 += "neighLabel = (int *) malloc(sizeof(int) * maxChunkSize);";
		eStr2 += "depArrayLoop = (int *) malloc(sizeof(int) * maxChunkSize);";

		/*
		 * Step 3: Add elastic-initialization code after Step 2:
		 * ....threadRemWLArray[tid] = 0;
		 * ....for (int i = myStart1; i <= myEnd1; i++) {
		 * ........threadRemWLArray[tid] += WLArrayLoop1[i];
		 * ....}
		 * ....threadProgress = myStart1 - 1;
		 * ....threadStartId = myStart1;
		 * ....threadRemWLArray[tid] += (myEnd1 - myStart1 + 1) * WLI2COST;
		 * 
		 */

		String estr3 = "threadRemWLArray[tid] = 0;";
		estr3 += "int i;";
		estr3 += "for (i = myStart" + lid + "; i <= myEnd" + lid + "; i++) {";
		estr3 += "\tthreadRemWLArray[tid] += WLArrayLoop" + lid + ";";
		estr3 += "}";
		estr3 += "threadProgress = myStart" + lid + "- 1;";
		estr3 += "threadStartId = myStart" + lid + ";";
		estr3 += "threadRemWLArray[tid] += (myEnd1" + lid + "- myStart" + lid + " + 1) * WLICOST" + lid + ";";

		/*
		 * Step 4: Add after Step 3:
		 * ....#pragma omp atomic update
		 * ....doneInit = doneInit + 1
		 * 
		 */

		String estr4 = "#pragma omp atomic update";
		estr4 += "\tdoneInit = doneInit + 1";
		/*
		 * Step 5: Add after Step 4:
		 * ....double progressSum = progressCheckpointInterval;
		 * ....double remWLSum = remWLCheckpointInterval;
		 * 
		 */

		String estr5 = "double progressSum = progressCheckpointInterval;";
		estr5 += "double remWLSum = remWLCheckpointInterval;";

		/*
		 * Step 6: Add at the end of Loop1's body:
		 * ....remWLSum -= WLArrayLoop1[i];
		 * ....if (remWLSum <= 0.0) {
		 * ........#pragma omp atomic update
		 * ........threadRemWLArray[tid] -= (remWLCheckpointInterval - remWLSum);
		 * ........remWLSum = remWLCheckpointInterval;
		 * ....}
		 * ....progressSum -= WLArrayLoop1[i];
		 * ....if (progressSum <= 0.0) {
		 * ........#pragma omp atomic write
		 * ........threadProgress[tid] = i;
		 * ........progressSum = progressCheckpointInterval;
		 * ....}
		 * 
		 */
		String estr6 = "remWLSum -= WLArrayLoop" + lid + "[i];";
		estr6 += "if (remWLSum <= 0.0) {";
		estr6 += "\t#pragma omp atomic update";
		estr6 += "\tthreadRemWLArray[tid] -= (remWLCheckpointInterval - remWLSum);";
		estr6 += "\tremWLSum = remWLCheckpointInterval;";
		estr6 += "}";
		estr6 += "progressSum -= WLArrayLoop" + lid + "[i];";
		estr6 += "if (progressSum <= 0.0) {";
		estr6 += "\t#pragma omp atomic write";
		estr6 += "\tthreadProgress[tid] = i;";
		estr6 += "\tprogressSum = progressCheckpointInterval;";
		estr6 += "}";

		/*
		 * Step 7: Add after Loop1:
		 * ....if (progressSum < progressCheckpointInterval) {
		 * ........#pragma omp atomic write
		 * ........threadProgress[tid] = myEnd1;
		 * ....}
		 * ....if (remWLSum < remWLCheckpointInterval) {
		 * ........#pragma omp atomic update
		 * ........threadRemWLArray[tid] -= (remWLCheckpointInterval - remWLSum);
		 * ....}
		 * 
		 */

		String estr7 = "if (progressSum < progressCheckpointInterval) {";
		estr7 += "\t#pragma omp atomic write";
		estr7 += "\tthreadProgress[tid] = myEnd1;";
		estr7 += "}";
		estr7 += "if (remWLSum < remWLCheckpointInterval) {";
		estr7 += "\t#pragma omp atomic update";
		estr7 += "\tthreadRemWLArray[tid] -= (remWLCheckpointInterval - remWLSum);";
		estr7 += "}";

		/*
		 * Step 8: After Step 7:
		 * ....if (N == M) {startE2 = myStart1; endE2 = myEnd1;}
		 * ....else {startE2 = getStaticStart(M, tid, totalThreads); endE2 =
		 * getStaticEnd(M, tid, totalThreads);}
		 * 
		 */

		String estr8 = "if (N == M) {startE" + lid + 1 + " = myStart" + lid + "; endE" + lid + 1 + " = myEnd" + lid
				+ ";}";
		estr8 += "else {startE" + lid + 1 + " = getStaticStart(M, tid, totalThreads); " + "endE =" + lid + 1
				+ "getStaticEnd(M, tid, totalThreads);}";

		/*
		 * Step 9: After Step 8:
		 * ....remWLSum = remWLCheckpointInterval;
		 * 
		 */

		String estr9 = "remWLSum = remWLCheckpointInterval;";

		/*
		 * Step 10: Add at the end of WLLoop2 body:
		 * ....depArrayLoop2[i - startE2] = ... (to be generalized)
		 * ....neighIndex[i - startE2] = -1;
		 * ....neighTID[i - startE2] = -1;
		 * ....neighLabel[i - startE2] = -1;
		 * ....remWLSum -= WLI2COST;
		 * ....if (remWLSum <= 0.0) {
		 * ........#pragma omp atomic update
		 * ........threadRemWLArray[tid] -= (remWLCheckpointInterval - remWLSum);
		 * ........remWLSum = remWLCheckpointInterval;
		 * ....}
		 * 
		 */

		String estr10 = "depArrayLoop[i - startE" + lid + 1 + "] = ... (to be generalized)";
		estr10 += "neighIndex[i - startE" + lid + 1 + "] = -1;";
		estr10 += "neighTID[i - startE" + lid + 1 + "] = -1;";
		estr10 += "neighLabel[i - startE" + lid + 1 + "] = -1;";
		estr10 += "remWLSum -= WLI2COST;";
		estr10 += "if (remWLSum <= 0.0) {";
		estr10 += "#pragma omp atomic update";
		estr10 += "threadRemWLArray[tid] -= (remWLCheckpointInterval - remWLSum);";
		estr10 += "remWLSum = remWLCheckpointInterval;";
		estr10 += "}";

		/*
		 * Step 11: Insert after WLLoop2:
		 * ....if (remWLSum < remWLCheckpointInterval) {
		 * ........#pragma omp atomic update
		 * ........threadRemWLArray[tid] -= (remWLCheckpointInterval - remWLSum);
		 * ....}
		 * 
		 */

		/*
		 * Step 12: Add code for waiting for initialization of elastic data-structures
		 * by all threads, after Step 11:
		 * ....char localDone;
		 * ....do {
		 * ........#pragma omp atomic read
		 * ........localDone = doneInit;
		 * ....} while (localDone != totalThreads);
		 * 
		 */

		/*
		 * Step 13: Add after Step 12:
		 * ....double myScope = getScope();
		 * ....double safeScope = myScope * safeScopeFraction();
		 * ....int contNode = startE2 - 1;
		 * ....int seen2or3 = 0;
		 * ....int eIter = startE2;
		 * 
		 */

		/*
		 * Step 14: Add a new while-loop after Step 13:
		 * ....while (safeScope > scopeLimit) {
		 * ........char executedAtleastOne = 0;
		 * ........char skippedAtleastOneDueToDepCheckFailure = 0;
		 * ........double oldSafeScope = safeScope;
		 * ....}
		 * 
		 */

		/*
		 * Step 15: Insert a new for-loop at the end of while-loop's body from Step 14:
		 * ....for (; eIter <= endE2; eIter++) {
		 * ........// 1. Already-done check
		 * ........// 2. Inner-scopewise check
		 * ........// 3. Precheck
		 * ........// 4. Dependency-wise check
		 * ........// 5. Actual execution
		 * ........// 6. Scope check
		 * ....}
		 * 
		 */

		/*
		 * Step 16: Add following code inside for-loop from Step 15:
		 * ....// 1. Already-done check
		 * ....if (WLArrayLoop2[eIter] <= 0.0) {
		 * ........if (!seen2Or3) {contNode = eIter;}
		 * ........continue;
		 * ....}
		 * ....// 2. Inner-scopewise check
		 * ....if ((safeScope - depArrayLoop2[eIter - startE2] - WLArrayLoop2[eIter]) <
		 * scopeLimit) {
		 * ........seen2Or3 = 1;
		 * ........continue;
		 * ....}
		 * ....// 3. Precheck
		 * ....int ngIndex = neighIndex[eIter - startE2];
		 * ....if (ngIndex != -1) {
		 * ........int neighThread = neighTID[eIter-startE2];
		 * ........int ngThreadProgress;
		 * ........#pragma omp atomic read
		 * ........ngThreadProgress = threadProgress[neighThread];
		 * ........if (neighLabel[eIter - startE2] > ngThreadProgress) {
		 * ............skippedAtLeastOneDueToDepCheckFailure = 1;
		 * ............seen2Or3 = 1;
		 * ............continue;
		 * ........} else {ngIndex++;}
		 * ....}
		 * ....// 4. Dependency-wise check (to be generalized; following code is for
		 * kcommittee)
		 * ....int doWork = 1;
		 * ....int en;
		 * ....for (en = ngIndex; en < curr->degree; en++) {
		 * ........int ngId = neighbour -> label;
		 * ........int nStart, ngtid = -1;
		 * ........for (nStart = 0; nStart < totalThreads - 1; nStart++) {
		 * ............if (ngId >= threadStartId[nStart] && ngId <
		 * threadStartId[nStart+1]) {
		 * ................ngtid = nStart;
		 * ................break;
		 * ............}
		 * ........}
		 * ........if (ngtid == -1) {
		 * ............ngtid = nStart;
		 * ........}
		 * ........if (ngtid == tid) {
		 * ............continue;
		 * ........}
		 * ........int ngThreadProgress;
		 * ........#pragma omp atomic read
		 * ........ngThreadProgress = threadProgress[ngtid];
		 * ........if (ngId > ngThreadProgress) {
		 * ............doWork = 0;
		 * ............neighIndex[eIter-startE2] = en;
		 * ............neighTID[eIter-startE2] = ngtid;
		 * ............neighLabel[eIter-startE2] = ngId;
		 * ............break;
		 * ........}
		 * ....}
		 * ....// 5. Actual Execution
		 * .......(add from Loop2)
		 * ....// 5.5 safeScope -= WLArrayLoop2[eIter]
		 * ...........WLArrayLoop2[eIter] = 0.0;
		 * ....// 6. Scope check
		 * ....if (safeScope < scopeLimit) {break;}
		 * 
		 */

		/*
		 * Step 17: Add outside for-loop, at the end of while-loop:
		 * ....if (!executedAtLeastOne && !skippedAtleastOneDueToDepCheckFailure)
		 * {break;}
		 * ....int reachedEnd = 0;
		 * ....if (eIter == endE2 + 1) {
		 * ........reachedEnd = 1;
		 * ........seen2Or3 = 0;
		 * ........eIter = contNode + 1;
		 * ....}
		 * ....if (safeScope != oldSafeScope) {
		 * ........myScope = getScope();
		 * ........safeScope = myScope * safeScopeFraction;
		 * ........safeScope -= GETSCOPECOST;
		 * ........if (reachedEnd) {
		 * ............usleep(sleepFrac * safeScope/1000);
		 * ........}
		 * ....}
		 * 
		 */

		/*
		 * Step 18: Add following at the beginning of Loop2:
		 * ....if (WLArrayLoop2[i] <= 0.0) {
		 * ........continue;
		 * ....}
		 * 
		 */

	}

	public static boolean isDuplicableInDifferentScope(Node n) {

		for (Node node : n.getInfo().getCFGInfo().getIntraTaskCFGLeafContents()) {
			if (node instanceof PreCallNode) {
				PreCallNode pcn = (PreCallNode) node;
				CallStatement cs = pcn.getParent();
				Symbol funcSym = (Symbol) cs.getInfo().getFunctionDesignator();
				if (funcSym.getName().equals("fprintf") || funcSym.getName().equals("printf")
						|| funcSym.getName().equals("malloc")) {
					return false;
				}
			}
		}
		CellSet rns = Utilities.readOuterNonStruct(n);
		CellSet wns = Utilities.writeOuterNonStruct(n);
		Set<String> rs = Utilities.readOuterStruct(n);
		Set<String> ws = Utilities.writeOuterStruct(n);
		boolean nsConflict = Misc.doIntersect(rns, wns);
		boolean sConflict = Misc.doIntersect(rs, ws);

		return !nsConflict && !sConflict;
	}

	/**
	 * Obtains a set of symbols that are declared inside the scope present
	 * lexically inside the given node.
	 * 
	 * @param node
	 * @return
	 */
	public static CellSet getLocals(Node node) {
		CellSet allLocalSymbols = new CellSet();
		for (Scopeable scope : node.getInfo().getLexicallyEnclosedScopesInclusive()) {
			if (scope instanceof CompoundStatement) {
				allLocalSymbols.addAll(((CompoundStatement) scope).getInfo().getSymbolTable().values());
			} else if (scope instanceof FunctionDefinition) {
				allLocalSymbols.addAll(((FunctionDefinition) scope).getInfo().getSymbolTable().values());
			} else if (scope instanceof TranslationUnit) {
				allLocalSymbols.addAll(((TranslationUnit) scope).getInfo().getSymbolTable().values());
			}
		}
		return allLocalSymbols;
	}

	/**
	 * Obtains the set of symbols that are declared in the enclosing scope of the
	 * given node.
	 * 
	 * @param node
	 * @return
	 */
	public static CellSet getInners(Node node) {
		CellSet allInnerSymbols = new CellSet();
		CompoundStatement scope = (CompoundStatement) Misc.getEnclosingBlock(node);
		allInnerSymbols.addAll(scope.getInfo().getSymbolTable().values());
		return allInnerSymbols;
	}

	public static CellSet readLocalNonStruct(Node n) {
		CellList readList = n.getInfo().getReads();
		CellSet localList = Utilities.getLocals(n);
		return Misc.setIntersection(new CellSet(readList), localList);
	}

	public static CellSet writeLocalNonStruct(Node n) {
		CellList writeList = n.getInfo().getWrites();
		CellSet localList = Utilities.getLocals(n);
		return Misc.setIntersection(new CellSet(writeList), localList);
	}

	public static CellSet readInnerNonStruct(Node n) {
		CellList readList = n.getInfo().getReads();
		CellSet innerList = Utilities.getInners(n);
		return Misc.setIntersection(new CellSet(readList), innerList);
	}

	public static CellSet writeInnerNonStruct(Node n) {
		CellList writeList = n.getInfo().getWrites();
		CellSet innerList = Utilities.getInners(n);
		return Misc.setIntersection(new CellSet(writeList), innerList);
	}

	public static CellSet readOuterNonStruct(Node n) {
		CellSet nonStructSet = getNonStructSet(n.getInfo().getReads(), StructStringGetter.getStructReadStrings(n));
		CellSet nonStructOuterSet = Misc.setMinus(nonStructSet, getLocals(n));
		return Misc.setMinus(nonStructOuterSet, getInners(n));
	}

	public static CellSet writeOuterNonStruct(Node n) {
		CellSet nonStructSet = getNonStructSet(n.getInfo().getWrites(), StructStringGetter.getStructWriteStrings(n));
		CellSet nonStructOuterSet = Misc.setMinus(nonStructSet, getLocals(n));
		return Misc.setMinus(nonStructOuterSet, getInners(n));
	}

	public static Set<String> readLocalStruct(Node n) {
		return new HashSet<>();
	}

	public static Set<String> writeLocalStruct(Node n) {
		return new HashSet<>();
	}

	public static Set<String> readInnerStruct(Node n) {
		return new HashSet<>();
	}

	public static Set<String> writeInnerStruct(Node n) {
		return new HashSet<>();
	}

	public static Set<String> readOuterStruct(Node n) {
		return StructStringGetter.getStructReadStrings(n);
	}

	public static Set<String> writeOuterStruct(Node n) {
		return StructStringGetter.getStructWriteStrings(n);
	}

	public static boolean areTwoNodesConflicting(Node n1, Node n2) {
		CellList readListOne = n1.getInfo().getReads();
		CellList readListTwo = n2.getInfo().getReads();
		Set<String> readListStructOne = StructStringGetter.getStructReadStrings(n1);
		Set<String> readListStructTwo = StructStringGetter.getStructReadStrings(n2);

		CellList writeListOne = n1.getInfo().getWrites();
		CellList writeListTwo = n2.getInfo().getWrites();
		Set<String> writeListStructOne = StructStringGetter.getStructWriteStrings(n1);
		Set<String> writeListStructTwo = StructStringGetter.getStructWriteStrings(n2);

		boolean rwConflict = doConflict(readListOne, readListStructOne, writeListTwo, writeListStructTwo);
		if (rwConflict) {
			return rwConflict;
		}
		boolean wrConflict = doConflict(writeListOne, writeListStructOne, readListTwo, readListStructTwo);
		if (wrConflict) {
			return wrConflict;
		}
		boolean wwConflict = doConflict(writeListOne, writeListStructOne, writeListTwo, writeListStructTwo);
		return wwConflict;
	}

	private static boolean doConflict(CellList firstCellList, Set<String> firstSetString, CellList secondCellList,
			Set<String> secondSetString) {
		CellSet firstNonStructSet = getNonStructSet(firstCellList, firstSetString);
		CellSet secondNonStructSet = getNonStructSet(secondCellList, secondSetString);

		boolean nonStructConflict = Misc.doIntersect(firstNonStructSet, secondNonStructSet);
		if (nonStructConflict) {
			return nonStructConflict;
		}
		boolean structConflict = Misc.doIntersect(firstSetString, secondSetString);
		return structConflict;
	}

	public static CellSet getNonStructSet(CellSet cellSet, Set<String> setString) {
		CellList cellList = new CellList();
		for (Cell c : cellSet) {
			cellList.add(c);
		}
		return Utilities.getNonStructSet(cellList, setString);
	}

	public static CellSet getNonStructSet(CellList cellList, Set<String> setString) {
		CellSet cellSet = new CellSet();

		for (Cell c : cellList) {
			String cString = c.toString();
			boolean found = false;
			for (String st : setString) {
				if (st.startsWith(cString)) {
					found = true;
					break;
				}
			}
			if (!found) {
				cellSet.add(c);
			}
		}
		return cellSet;
	}

	public Set<String> getLHSStringsOfDot(DotId dotId) {
		Set<String> returnSet = new HashSet<>();
		APostfixOperation enclosingAPostxixOp = Misc.getEnclosingNode(dotId, APostfixOperation.class);
		PostfixOperationsList enclosingPostfixOperationsList = Misc.getEnclosingNode(dotId,
				PostfixOperationsList.class);
		int index = enclosingPostfixOperationsList.getF0().getNodes().indexOf(enclosingAPostxixOp);
		PostfixExpression enclosingPostfixExpression = Misc.getEnclosingNode(dotId, PostfixExpression.class);

		Set<String> lastLevelStr = new HashSet<>();
		for (Cell cell : enclosingPostfixExpression.getInfo().getLocationsOf()) {
			lastLevelStr.add(cell.toString());

		}
		Set<String> thisLevelStr = new HashSet<>();
		String lhsStr = enclosingPostfixExpression.getF0().toString();
		int i = -1;
		for (Node postFixOp : enclosingPostfixOperationsList.getF0().getNodes()) {
			i++;
			if (i == index) {
				break;
			}
			// Check if code is...
			lhsStr += postFixOp.toString();
			lastLevelStr = thisLevelStr;
			thisLevelStr.clear();
		}
		System.out.println(lhsStr);
		return returnSet;
	}

	public static double getIndependentCostForLabel(String labelName) {
		String fileName = Program.fileName;
		String commandStr = "cd ../avgIndependentCosts ; cat *$(echo " + fileName + " | cut -f1 -d-)* | grep "
				+ labelName + " | cut -f2 -d' '";
		String[] command = { "bash", "-c", commandStr };

		try {
			Process proc = Runtime.getRuntime().exec(command);
			proc.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while (reader.ready()) {
				return Double.parseDouble(reader.readLine());
			}
		} catch (Exception e) {
			Misc.exitDueToError("Could not find the value for independent cost of " + labelName);
		}
		return 0.0;
	}

	public static boolean lastCallToReturnIndicesOfConflictingElementsWasSafe = true;

	/**
	 * While moving returned CostExpressionReturn just above the given #pof to a new
	 * WLArray #pof, storing indices of elements inside the CompoundStmt to be taken
	 * out along with the CostExpressionReturn
	 * 
	 * @param cs
	 * @param cer
	 * @param elemIndex
	 * @return
	 */
	@Deprecated
	public static List<Integer> returnIndicesOfConflictingElements(CompoundStatement cs, CostExpressionReturn cer,
			int elemIndex) {
		List<Integer> indexList = new ArrayList<>();
		List<Node> elemList = cs.getInfo().getCFGInfo().getElementList();
		CellSet readSetOfCode = new CellSet();
		Statement code = (Statement) cer.code.get(0); // ERROR: This was cer.code; change accordingly.
		cs.getInfo().getCFGInfo().addElement(elemIndex, code);
		readSetOfCode.addAll(code.getInfo().getReads());
		NodeRemover.removeNode(code);

		for (int id = elemIndex - 1; id >= 0; id--) {
			Node conflictElem = elemList.get(id);
			// Check Conflicts
			if (!Misc.setIntersection(readSetOfCode, new CellSet(conflictElem.getInfo().getWrites())).isEmpty()) {
				if (!(conflictElem instanceof Declaration || conflictElem instanceof ExpressionStatement
						|| conflictElem instanceof CallStatement)) {
					Misc.warnDueToLackOfFeature("Not handling conflicts with a "
							+ conflictElem.getClass().getSimpleName() + "\n Readset: " + readSetOfCode
							+ "\n WriteSetOfElem: " + new CellSet(conflictElem.getInfo().getWrites()), conflictElem);
					Utilities.lastCallToReturnIndicesOfConflictingElementsWasSafe = false;
				}
				indexList.add(id);
				readSetOfCode.addAll(conflictElem.getInfo().getReads());
			}
		}
		Utilities.lastCallToReturnIndicesOfConflictingElementsWasSafe = true;
		return indexList;
	}

	public static double getFracCostForIfLabel(String labelName) {
		String fileName = Program.fileName;
		String commandStr = "cd ../ifFreqIndependentCosts ; cat *$(echo " + fileName + " | cut -f1 -d-)* | grep "
				+ labelName + " | cut -f2 -d' '";
		String[] command = { "bash", "-c", commandStr };

		try {
			Process proc = Runtime.getRuntime().exec(command);
			proc.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			while (reader.ready()) {
				return Double.parseDouble(reader.readLine());
			}
		} catch (Exception e) {
			Misc.exitDueToError("Could not find the value for independent cost of " + labelName);
		}
		return 0.0;
	}

	public static CellSet getInputDependentCellSet() {
		if (inputDependentCellSet == null) {
			inputDependentCellSet = new CellSet();

			// sscanf & fscanf case
			for (CallStatement cs : Misc.getExactEnclosee(Program.getRoot(), CallStatement.class)) {
				for (Cell c : cs.getInfo().getCalledSymbols()) {
					Symbol sym = (Symbol) c;

					if (sym.getName().equals("fscanf") || sym.getName().equals("sscanf")) {
//						System.out.println(cs);
//						System.out.println(sym);
//						System.out.println("===========");

						PreCallNode pcn = cs.getInfo().getCFGInfo().getPreCallNode();

						int i = -1;
						for (SimplePrimaryExpression arg : pcn.getArgumentList()) {
							i++;
							if (i < 2) {
								continue;
							}

							if (arg.isAConstant()) {
								continue;
							}

							Symbol argSym = Misc.getSymbolEntry(arg.getString(), pcn);

							/*
							 * NEW CODE BELOW
							 */
							for (Cell cell : argSym.getPointsTo(pcn)) {
								inputDependentCellSet.add(cell);
							}
							/*
							 * NEW CODE ABOVE
							 */
							// OLD CODE BELOW: Does not suffice.
//							for (Definition def : pcn.getInfo().getReachingDefinitions()) {
//								if (def.getCell() != argSym) {
//									continue;
//								}
//								for (Cell defRhsVar : def.getDefiningNode().getInfo().getReads()) {
//									if (!(defRhsVar instanceof AddressCell)) {
//										continue;
//									}
//									AddressCell defRhsVarAddressCell = (AddressCell) defRhsVar;
//									Cell defRhsVarCell = defRhsVarAddressCell.getPointedElement();
//									Symbol defRhsVarSym;
//									if (defRhsVarCell instanceof Symbol) {
//										defRhsVarSym = (Symbol) defRhsVarCell;
//										inputDependentSymbolSet.add(defRhsVarSym);
//									}
//								}
//							}
						}
					}
				}
			}

			// argv, argc case
			FunctionDefinition mainFunc = Program.getRoot().getInfo().getMainFunction();
			for (ParameterDeclaration param : mainFunc.getInfo().getCFGInfo().getParameterDeclarationList()) {
				inputDependentCellSet.add(param.getInfo().getDeclaredSymbol());
			}
//			System.out.println(inputDependentCellSet);
		}
		return inputDependentCellSet;
	}

	public static void addAllMissingStandardDefinitions() {
		Utilities.addCeil();
		Utilities.addFclose();
		Utilities.addFopen();
		Utilities.addFprintf();
		Utilities.addFscanf();
		Utilities.addFree();
		Utilities.addMalloc();
		Utilities.addOmpGetNumThreads();
		Utilities.addOmpGetThreadNum();
		Utilities.addOmpGetWTime();
		Utilities.addPrintf();
		Utilities.addRand();
		Utilities.addRealloc();
		Utilities.addUsleep();
	}

	private static void addUsleep() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("usleep")) {
			return;
		}
		System.err.println("Adding declaration for usleep.");
		Declaration declaration = FrontEnd.parseAndNormalize("int usleep(unsigned int);", Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declaration);
	}
	

	public static void addOmpGetWTime() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("omp_get_wtime")) {
			return;
		}
		System.err.println("Adding declaration for omp_get_wtime.");
		Declaration declaration = FrontEnd.parseAndNormalize("extern double omp_get_wtime(void);", Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declaration);
	}

	public static void addOmpGetNumThreads() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("omp_get_num_threads")) {
			return;
		}

		System.err.println("Adding declaration for omp_get_num_threads.");
		Declaration declaration = FrontEnd.parseAndNormalize("extern int omp_get_num_threads(void);",
				Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declaration);
	}

	public static void addOmpGetThreadNum() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("omp_get_thread_num")) {
			return;
		}

		System.err.println("Adding declaration for omp_get_thread_num.");
		Declaration declaration = FrontEnd.parseAndNormalize("extern int omp_get_thread_num(void);", Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declaration);
	}

	public static void addFprintf() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("fprintf")) {
			return;
		}
		if (!Program.getRoot().getInfo().getTypedefTable().keySet().contains("FILE")) {
			Declaration declOfIOFILE = FrontEnd.parseAndNormalize("struct _IO_FILE;", Declaration.class);
			Declaration declOfFILE = FrontEnd.parseAndNormalize("typedef struct _IO_FILE FILE;", Declaration.class);
			Builder.addDeclarationBeforeFirstFunction(declOfIOFILE);
			Builder.addDeclarationBeforeFirstFunction(declOfFILE);
			System.err.println("Adding declaration for _IO_FILE.");
			System.err.println("Adding declaration for FILE.");
		}
		System.err.println("Adding declaration for fprintf.");
		Declaration declFprintf = FrontEnd.parseAndNormalize(
				"extern int fprintf (FILE *__restrict __stream, const char *__restrict __format, ...);",
				Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declFprintf);

	}

	public static void addFopen() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("fopen")) {
			return;
		}
		if (!Program.getRoot().getInfo().getTypedefTable().keySet().contains("FILE")) {
			Declaration declOfIOFILE = FrontEnd.parseAndNormalize("struct _IO_FILE;", Declaration.class);
			Declaration declOfFILE = FrontEnd.parseAndNormalize("typedef struct _IO_FILE FILE;", Declaration.class);
			Builder.addDeclarationBeforeFirstFunction(declOfIOFILE);
			Builder.addDeclarationBeforeFirstFunction(declOfFILE);
			System.err.println("Adding declaration for _IO_FILE.");
			System.err.println("Adding declaration for FILE.");
		}

		System.err.println("Adding declaration for fopen.");
		Declaration declaration = FrontEnd.parseAndNormalize("FILE *fopen(const char *, const char *);",
				Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declaration);
	}

	public static void addFclose() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("fclose")) {
			return;
		}

		if (!Program.getRoot().getInfo().getTypedefTable().keySet().contains("FILE")) {
			Declaration declOfIOFILE = FrontEnd.parseAndNormalize("struct _IO_FILE;", Declaration.class);
			Declaration declOfFILE = FrontEnd.parseAndNormalize("typedef struct _IO_FILE FILE;", Declaration.class);
			Builder.addDeclarationBeforeFirstFunction(declOfIOFILE);
			Builder.addDeclarationBeforeFirstFunction(declOfFILE);
			System.err.println("Adding declaration for _IO_FILE.");
			System.err.println("Adding declaration for FILE.");
		}
		System.err.println("Adding declaration for fclose.");
		Declaration declaration = FrontEnd.parseAndNormalize("int fclose(FILE *);", Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declaration);
	}

	public static void addFscanf() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("fscanf")) {
			return;
		}
		if (!Program.getRoot().getInfo().getTypedefTable().keySet().contains("FILE")) {
			Declaration declOfIOFILE = FrontEnd.parseAndNormalize("struct _IO_FILE;", Declaration.class);
			Declaration declOfFILE = FrontEnd.parseAndNormalize("typedef struct _IO_FILE FILE;", Declaration.class);
			Builder.addDeclarationBeforeFirstFunction(declOfIOFILE);
			Builder.addDeclarationBeforeFirstFunction(declOfFILE);
			System.err.println("Adding declaration for _IO_FILE.");
			System.err.println("Adding declaration for FILE.");
		}

		System.err.println("Adding declaration for fscanf.");
		Declaration declaration = FrontEnd.parseAndNormalize("int fscanf(FILE *, const char *, ...);",
				Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declaration);
	}

	public static void addMalloc() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("malloc")) {
			return;
		}
		if (!Program.getRoot().getInfo().getTypedefTable().keySet().contains("size_t")) {
			Declaration sizeTDecl = FrontEnd.parseAndNormalize("typedef long unsigned int size_t;", Declaration.class);
			Builder.addDeclarationBeforeFirstFunction(sizeTDecl);
			System.err.println("Adding declaration for size_t.");
		}

		System.err.println("Adding declaration for malloc.");
		Declaration declaration = FrontEnd.parseAndNormalize("void *malloc(size_t __size);", Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declaration);
	}

	public static void addCeil() {
		if (!Program.getRoot().getInfo().getSymbolTable().keySet().contains("ceil")) {
			System.err.println("Adding declaration for ceil.");
			Declaration declaration = FrontEnd.parseAndNormalize("extern double ceil(double);", Declaration.class);
			Builder.addDeclarationBeforeFirstFunction(declaration);
		}
//		if (!Program.getRoot().getInfo().getSymbolTable().keySet().contains("max")) {
//			Declaration declaration = FrontEnd.parseAndNormalize("extern double omp_get_wtime(void);",
//					Declaration.class);
//			Builder.addDeclarationBeforeFirstFunction(declaration);
//		}
//		if (!Program.getRoot().getInfo().getSymbolTable().keySet().contains("min")) {
//			Declaration declaration = FrontEnd.parseAndNormalize("extern double omp_get_wtime(void);",
//					Declaration.class);
//			Builder.addDeclarationBeforeFirstFunction(declaration);
//		}

	}

	public static void addPrintf() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("printf")) {
			return;
		}
		System.err.println("Adding declaration for printf.");

		Declaration declaration = FrontEnd.parseAndNormalize("int printf(const char *, ...);", Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declaration);
	}

	public static void addRealloc() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("realloc")) {
			return;
		}
		if (!Program.getRoot().getInfo().getTypedefTable().keySet().contains("size_t")) {
			Declaration sizeTDecl = FrontEnd.parseAndNormalize("typedef long unsigned int size_t;", Declaration.class);
			Builder.addDeclarationBeforeFirstFunction(sizeTDecl);
			System.err.println("Adding declaration for size_t.");
		}

		System.err.println("Adding declaration for realloc.");
		Declaration declaration = FrontEnd.parseAndNormalize("void *realloc(void *, size_t);", Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declaration);
	}

	public static void addFree() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("free")) {
			return;
		}
		System.err.println("Adding declaration for free.");

		Declaration declaration = FrontEnd.parseAndNormalize("void free(void *);", Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declaration);
	}

	public static void addRand() {
		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("rand")) {
			return;
		}
		System.err.println("Adding declaration for rand.");

		Declaration declaration = FrontEnd.parseAndNormalize("int rand(void);", Declaration.class);
		Builder.addDeclarationBeforeFirstFunction(declaration);
	}

//	public static void addExit() {
//		if (Program.getRoot().getInfo().getSymbolTable().keySet().contains("exit")) {
//			return;
//		}
//
//		Declaration declGetWtime = FrontEnd.parseAndNormalize("extern double omp_get_wtime(void);", Declaration.class);
//		Builder.addDeclarationBeforeFirstFunction(declGetWtime);
//	}

	public static boolean areTwoForHeadersEquivalent(ForStatement fs1, ForStatement fs2) {
		OmpForInitExpression ofi1 = Utilities.getOmpForInit(fs1.getInfo().getCFGInfo().getInitExpression());
		OmpForInitExpression ofi2 = Utilities.getOmpForInit(fs2.getInfo().getCFGInfo().getInitExpression());
		OmpForReinitExpression ofri1 = Utilities.getOmpForReinit(fs1.getInfo().getCFGInfo().getStepExpression());
		OmpForReinitExpression ofri2 = Utilities.getOmpForReinit(fs2.getInfo().getCFGInfo().getStepExpression());
		OmpForCondition ofc1 = Utilities.getOmpForCondition(fs1.getInfo().getCFGInfo().getTerminationExpression());
		OmpForCondition ofc2 = Utilities.getOmpForCondition(fs2.getInfo().getCFGInfo().getTerminationExpression());
		if (!ofi1.getF2().toString().trim().equals(ofi2.getF2().toString().trim())) {
			return false;
		}
		if (ofc1.getF0().getChoice() instanceof OmpForLTCondition) {
			if (!(ofc2.getF0().getChoice() instanceof OmpForLTCondition)) {
				return false;
			}
			Expression e1 = ((OmpForLTCondition) ofc1.getF0().getChoice()).getF2();
			Expression e2 = ((OmpForLTCondition) ofc2.getF0().getChoice()).getF2();
			if (!e1.toString().trim().equals(e2.toString().trim())) {
				return false;
			}
		} else if (ofc1.getF0().getChoice() instanceof OmpForLECondition) {
			if (!(ofc2.getF0().getChoice() instanceof OmpForLECondition)) {
				return false;
			}
			Expression e1 = ((OmpForLECondition) ofc1.getF0().getChoice()).getF2();
			Expression e2 = ((OmpForLECondition) ofc2.getF0().getChoice()).getF2();
			if (!e1.toString().trim().equals(e2.toString().trim())) {
				return false;
			}

		} else if (ofc1.getF0().getChoice() instanceof OmpForGTCondition) {
			if (!(ofc2.getF0().getChoice() instanceof OmpForGTCondition)) {
				return false;
			}
			Expression e1 = ((OmpForGTCondition) ofc1.getF0().getChoice()).getF2();
			Expression e2 = ((OmpForGTCondition) ofc2.getF0().getChoice()).getF2();
			if (!e1.toString().trim().equals(e2.toString().trim())) {
				return false;
			}

		} else {
			// ofc1 is OmpForGECondition
			if (!(ofc2.getF0().getChoice() instanceof OmpForGECondition)) {
				return false;
			}
			Expression e1 = ((OmpForGECondition) ofc1.getF0().getChoice()).getF2();
			Expression e2 = ((OmpForGECondition) ofc2.getF0().getChoice()).getF2();
			if (!e1.toString().trim().equals(e2.toString().trim())) {
				return false;
			}

		}

		if (ofri1.getOmpForReinitF0().getChoice() instanceof PostIncrementId) {
			if (!(ofri2.getOmpForReinitF0().getChoice() instanceof PostIncrementId)) {
				return false;
			}
		} else if (ofri1.getOmpForReinitF0().getChoice() instanceof PostDecrementId) {
			if (!(ofri2.getOmpForReinitF0().getChoice() instanceof PostDecrementId)) {
				return false;
			}
		} else if (ofri1.getOmpForReinitF0().getChoice() instanceof PreIncrementId) {
			if (!(ofri2.getOmpForReinitF0().getChoice() instanceof PreIncrementId)) {
				return false;
			}
		} else if (ofri1.getOmpForReinitF0().getChoice() instanceof PreDecrementId) {
			if (!(ofri2.getOmpForReinitF0().getChoice() instanceof PreDecrementId)) {
				return false;
			}
		} else if (ofri1.getOmpForReinitF0().getChoice() instanceof ShortAssignPlus) {
			if (!(ofri2.getOmpForReinitF0().getChoice() instanceof ShortAssignPlus)) {
				return false;
			}
			Expression e1 = ((ShortAssignPlus) ofri1.getOmpForReinitF0().getChoice()).getF2();
			Expression e2 = ((ShortAssignPlus) ofri2.getOmpForReinitF0().getChoice()).getF2();
			if (!e1.toString().trim().equals(e2.toString().trim())) {
				return false;
			}
		} else if (ofri1.getOmpForReinitF0().getChoice() instanceof ShortAssignMinus) {
			if (!(ofri2.getOmpForReinitF0().getChoice() instanceof ShortAssignMinus)) {
				return false;
			}
			Expression e1 = ((ShortAssignMinus) ofri1.getOmpForReinitF0().getChoice()).getF2();
			Expression e2 = ((ShortAssignMinus) ofri2.getOmpForReinitF0().getChoice()).getF2();
			if (!e1.toString().trim().equals(e2.toString().trim())) {
				return false;
			}
		} else if (ofri1.getOmpForReinitF0().getChoice() instanceof OmpForAdditive) {
			if (!(ofri2.getOmpForReinitF0().getChoice() instanceof OmpForAdditive)) {
				return false;
			}
			Expression e1 = ((OmpForAdditive) ofri1.getOmpForReinitF0().getChoice()).getF4();
			Expression e2 = ((OmpForAdditive) ofri2.getOmpForReinitF0().getChoice()).getF4();
			if (!e1.toString().trim().equals(e2.toString().trim())) {
				return false;
			}

		} else if (ofri1.getOmpForReinitF0().getChoice() instanceof OmpForSubtractive) {
			if (!(ofri2.getOmpForReinitF0().getChoice() instanceof OmpForSubtractive)) {
				return false;
			}
			Expression e1 = ((OmpForSubtractive) ofri1.getOmpForReinitF0().getChoice()).getF4();
			Expression e2 = ((OmpForSubtractive) ofri2.getOmpForReinitF0().getChoice()).getF4();
			if (!e1.toString().trim().equals(e2.toString().trim())) {
				return false;
			}
		} else if (ofri1.getOmpForReinitF0().getChoice() instanceof OmpForMultiplicative) {
			if (!(ofri2.getOmpForReinitF0().getChoice() instanceof OmpForMultiplicative)) {
				return false;
			}
			Expression e1 = ((OmpForMultiplicative) ofri1.getOmpForReinitF0().getChoice()).getF2();
			Expression e2 = ((OmpForMultiplicative) ofri2.getOmpForReinitF0().getChoice()).getF2();
			if (!e1.toString().trim().equals(e2.toString().trim())) {
				return false;
			}

		}
		return true;
	}

	/**
	 * Step 1: Check whether expression is parseable as Omp For expression.
	 * Step 2: If yes, return the Rhs of it.
	 * 
	 * @param fs
	 * @return
	 */
	public static Expression getRhsOfForInit(ForStatement fs) {
		OmpForInitExpression ofi = Utilities.getOmpForInit(fs.getInfo().getCFGInfo().getInitExpression());
		if (ofi == null) {
			return null;
		}
		return ofi.getF2();
	}

	/**
	 * Step 1: Check whether expression is parseable as Omp For expression.
	 * Step 2: If yes, return the Rhs of it.
	 * 
	 * @param fs
	 * @return
	 */
	public static Expression getRhsOfForStep(ForStatement fs) {
		OmpForReinitExpression ofri = Utilities.getOmpForReinit(fs.getInfo().getCFGInfo().getStepExpression());
		if (ofri == null) {
			return null;
		}

		String v3Str;
		Node reinitNode = ofri.getOmpForReinitF0().getChoice();

		if (reinitNode instanceof PostIncrementId) {
			PostIncrementId pi = (PostIncrementId) reinitNode;
			v3Str = "1";
		} else if (reinitNode instanceof PreIncrementId) {
			PostDecrementId pd = (PostDecrementId) reinitNode;
			v3Str = "1";
		} else if (reinitNode instanceof OmpForAdditive) {
			// Assuming in "id = id + Addexp", both "id" represent the same variable.
			OmpForAdditive oa = (OmpForAdditive) reinitNode;
			v3Str = oa.getF4().toString();
		} else if (reinitNode instanceof ShortAssignPlus) {
			ShortAssignPlus sp = (ShortAssignPlus) reinitNode;
			v3Str = sp.getF2().toString();
		} else if (reinitNode instanceof PreDecrementId) {
			PreDecrementId pd = (PreDecrementId) reinitNode;
			v3Str = "1";
		} else if (reinitNode instanceof PostDecrementId) {
			PostDecrementId pd = (PostDecrementId) reinitNode;
			v3Str = "1";
		} else if (reinitNode instanceof ShortAssignMinus) {
			ShortAssignMinus sm = (ShortAssignMinus) reinitNode;
			v3Str = sm.getF2().toString();
		} else if (reinitNode instanceof OmpForSubtractive) {
			// Assuming in "id = id - Addexp", both "id" represent the same variable.
			OmpForSubtractive os = (OmpForSubtractive) reinitNode;
			v3Str = os.getF4().toString();
		} else {
			v3Str = null;
			assert (false) : "Multiplicative Expression Not Handled";
			System.exit(0);
		}
		return FrontEnd.parseAndNormalize(v3Str, Expression.class);
	}

	/**
	 * Step 1: Check whether expression is parseable as Omp For expression.
	 * Step 2: If yes, return the Rhs of it.
	 * 
	 * @param fs
	 * @return
	 */
	public static Expression getRhsOfForCondition(ForStatement fs) {
		OmpForCondition ofc = Utilities.getOmpForCondition(fs.getInfo().getCFGInfo().getTerminationExpression());

		if (ofc == null) {
			return null;
		}

		Node condNode = ofc.getF0().getChoice();
		Expression v2;

		if (condNode instanceof OmpForLTCondition) {
			v2 = ((OmpForLTCondition) condNode).getF2();
		} else if (condNode instanceof OmpForLECondition) {
			v2 = ((OmpForLECondition) condNode).getF2();
		} else if (condNode instanceof OmpForGTCondition) {
			v2 = ((OmpForGTCondition) condNode).getF2();
		} else {
			// GE case
			v2 = ((OmpForGECondition) condNode).getF2();
		}
		return v2;
	}

	public static OmpForInitExpression getOmpForInit(Expression exp) {
		OmpForInitExpression ofi;
		try {
			ofi = FrontEnd.parseAndNormalize(exp.toString(), OmpForInitExpression.class, false);
		} catch (Exception e) {
			// System.err.println(e.getClass().getSimpleName()+" occurred at exp: " + exp);
			return null;
		}
		return ofi;
	}

	public static OmpForCondition getOmpForCondition(Expression exp) {
		OmpForCondition ofc;
		try {
			ofc = FrontEnd.parseAndNormalize(exp.toString(), OmpForCondition.class, false);
		} catch (Exception e) {
			// System.err.println(e.getClass().getSimpleName()+" occurred at exp: " + exp);
			return null;
		}
		return ofc;
	}

	public static OmpForReinitExpression getOmpForReinit(Expression exp) {
		OmpForReinitExpression ofri;
		try {
			ofri = FrontEnd.parseAndNormalize(exp.toString(), OmpForReinitExpression.class, false);
		} catch (Exception e) {
			// System.err.println(e.getClass().getSimpleName()+" occurred at exp: " + exp);
			return null;
		}
		return ofri;
	}

	private static boolean isVVIDNRecursive(Cell cell, Node node) {
		if (visitedNodes.contains(node)) {
			return true;
		}
		visitedNodes.add(node);

		for (Definition rd : node.getInfo().getReachingDefinitions()) {
			if (rd.getCell() != cell) {
				continue;
			}
			CellList cList;
			// x = foo(a,b,c...)
			if (rd.getDefiningNode() instanceof PostCallNode) {
				cList = new CellList();
				PostCallNode postCN = (PostCallNode) rd.getDefiningNode();
				PreCallNode preCN = postCN.getParent().getPreCallNode();

				// Adding all the reads from the PreCallNode ArgumentList
				for (SimplePrimaryExpression spe : preCN.getArgumentList()) {
					if (spe.isAConstant()) {
						continue;
					}
					cList.add(Misc.getSymbolEntry(spe.getString(), preCN));
				}

				CallStatement callStmt = postCN.getParent();

				// Adding all the reads from the called functions' return
				for (FunctionDefinition calledFunc : callStmt.getInfo().getCalledDefinitions()) {
					for (ReturnStatement retStmt : Misc.getExactEnclosee(calledFunc, ReturnStatement.class)) {
						cList.addAll(retStmt.getInfo().getReads());
					}
				}
				// void foo(int a, int b) || foo(a,b)
			} else if (rd.getDefiningNode() instanceof ParameterDeclaration) {
				// Default Case
				cList = new CellList();
				ParameterDeclaration paramDecl = (ParameterDeclaration) rd.getDefiningNode();
				FunctionDefinition funcDef = Misc.getEnclosingFunction(paramDecl);
				if (funcDef == Program.getRoot().getInfo().getMainFunction()) {
					return true;
				}
				int indexOfParam = funcDef.getInfo().getCFGInfo().getParameterDeclarationList().indexOf(paramDecl);

				for (CallStatement callStmt : funcDef.getInfo().getCallersOfThis()) {
					PreCallNode preCN = callStmt.getPreCallNode();
					SimplePrimaryExpression spe = preCN.getArgumentList().get(indexOfParam);

					if (spe.isAConstant())
						continue;
					cList.add(Misc.getSymbolEntry(spe.getString(), preCN));
				}
			} else {
				cList = rd.getDefiningNode().getInfo().getReads();
			}
			for (Cell readVar : cList) {
				if (!(readVar instanceof Symbol)) {
					continue;
				}
				if (getInputDependentCellSet().contains(readVar)) {
					return true;
				}
				if (isVVIDNRecursive((Symbol) readVar, rd.getDefiningNode())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isVariableValueInputDependentAtNode(Cell cell, Node n) {
		if (!Misc.isCFGLeafNode(n)) {
			Misc.warnDueToLackOfFeature("Not handling a CFG non-leaf node", n);
			return true;
		}

		visitedNodes.clear();
		return isVVIDNRecursive(cell, n);
//		return true;
	}

	public static CostExpressionReturn getCountforForHeader(Node fs) {
		String countVar = Builder.getNewTempName("iterCount");
		OmpForInitExpression ofi;
		OmpForCondition ofc;
		OmpForReinitExpression ofri;
		if (fs instanceof ForStatement) {
			ofi = getOmpForInit(((ForStatement) fs).getInfo().getCFGInfo().getInitExpression());
			ofc = getOmpForCondition(((ForStatement) fs).getInfo().getCFGInfo().getTerminationExpression());
			ofri = getOmpForReinit(((ForStatement) fs).getInfo().getCFGInfo().getStepExpression());
		} else if (fs instanceof ForConstruct) {
			ForConstruct fc = (ForConstruct) fs;
			ofi = fc.getInfo().getCFGInfo().getInitExpression();
			ofc = fc.getInfo().getCFGInfo().getForConditionExpression();
			ofri = fc.getInfo().getCFGInfo().getReinitExpression();
		} else {
			assert (false) : "Cannot invoke getCountForHeader with non-for-loops.";
			ofi = null;
			ofc = null;
			ofri = null;
		}

		if (ofi == null || ofc == null || ofri == null) {
			System.out.println("Unknown Iterations");
			return null;
		}

		String checkStr = "";

		System.err.println("Iteration Count Deducible");
		Expression v1 = ofi.getF2();

		Node condNode = ofc.getF0().getChoice();
		Expression v2;

		if (condNode instanceof OmpForLTCondition) {
			v2 = ((OmpForLTCondition) condNode).getF2();
		} else if (condNode instanceof OmpForLECondition) {
			v2 = ((OmpForLECondition) condNode).getF2();
		} else if (condNode instanceof OmpForGTCondition) {
			v2 = ((OmpForGTCondition) condNode).getF2();
		} else {
			// GE case
			v2 = ((OmpForGECondition) condNode).getF2();
		}

		String v3Str;
		Node reinitNode = ofri.getOmpForReinitF0().getChoice();

		int sign;

		if (reinitNode instanceof PostIncrementId) {
			sign = 0;
			v3Str = "1";
		} else if (reinitNode instanceof PreIncrementId) {
			sign = 0;
			v3Str = "1";
		} else if (reinitNode instanceof OmpForAdditive) {
			// Assuming in "id = id + Addexp", both "id" represent the same variable.
			OmpForAdditive oa = (OmpForAdditive) reinitNode;
			sign = 0;
			v3Str = oa.getF4().toString();
		} else if (reinitNode instanceof ShortAssignPlus) {
			ShortAssignPlus sp = (ShortAssignPlus) reinitNode;
			sign = 0;
			v3Str = sp.getF2().toString();
		} else if (reinitNode instanceof PreDecrementId) {
			sign = 1;
			v3Str = "1";
		} else if (reinitNode instanceof PostDecrementId) {
			sign = 1;
			v3Str = "1";
		} else if (reinitNode instanceof ShortAssignMinus) {
			ShortAssignMinus sm = (ShortAssignMinus) reinitNode;
			sign = 1;
			v3Str = sm.getF2().toString();
		} else if (reinitNode instanceof OmpForSubtractive) {
			// Assuming in "id = id - Addexp", both "id" represent the same variable.
			OmpForSubtractive os = (OmpForSubtractive) reinitNode;
			sign = 1;
			v3Str = os.getF4().toString();
		} else {
			v3Str = null;
			sign = -1;
			assert (false) : "Multiplicative Expression Not Handled";
			System.exit(0);
		}
		if (sign == 1) {
			v3Str = "-(" + v3Str + ")";
		}

		v1 = FrontEnd.parseAndNormalize(v1.toString(), Expression.class);
		v2 = FrontEnd.parseAndNormalize(v2.toString(), Expression.class);
		boolean isV1Constant = Misc.hasConstantIntValue(v1);
		boolean isV2Constant = Misc.hasConstantIntValue(v2);
		Expression v3 = FrontEnd.parseAndNormalize(v3Str, Expression.class);
		boolean isV3Constant = Misc.hasConstantIntValue(v3);

		if (condNode instanceof OmpForLTCondition) {
			if (!isV1Constant || !isV2Constant) {
				// v1, v2 as variables
				if (!isV3Constant) {
					// v3 as variable
					checkStr += "if ((" + v1 + ")>=(" + v2 + ")) {\n" + countVar + "= 0; \n } ";
					String ceilArg = "((" + v2 + ")-(" + v1 + "))/" + "(" + v3 + ")";
					String tempCeil = Builder.getNewTempName("tceil");
					Declaration tempCeilDec = FrontEnd.parseAndNormalize("int " + tempCeil + " = " + ceilArg + ";\n",
							Declaration.class);
					String ceilFuncStr = countVar + " = ceil(" + tempCeil + ");";
					Statement ceilFuncStmt = FrontEnd.parseAndNormalize(ceilFuncStr, Statement.class);
					checkStr += "else {\n" + "if ((" + v3 + ")>0) {\n" + tempCeilDec + "\n" + ceilFuncStmt
							+ "\n} else {\n" + countVar + "= 2147483647;\n}\n}";
//					System.out.println("1.1");
//					System.out.println(fs);
				} else {
					// v3 as constant
					int v3Val = (int) Misc.getConstantValue(v3);
					if (v3Val > 0) {
						// v3 > 0
						checkStr += "if ((" + v1 + ")>=(" + v2 + ")) {\n" + countVar + "= 0; \n } ";
						String ceilArg = "((" + v2 + ")-(" + v1 + "))/" + "(" + v3Val + ")";
						String tempCeil = Builder.getNewTempName("tceil");
						Declaration tempCeilDec = FrontEnd
								.parseAndNormalize("int " + tempCeil + " = " + ceilArg + ";\n", Declaration.class);
						String ceilFuncStr = countVar + " = ceil(" + tempCeil + ");";
						Statement ceilFuncStmt = FrontEnd.parseAndNormalize(ceilFuncStr, Statement.class);
						checkStr += "else {\n" + tempCeilDec + "\n" + ceilFuncStmt + "\n}";
//						System.out.println("1.2");
//						System.out.println(fs);
					} else {
						// v3 <= 0
						checkStr += "if ((" + v1 + ")>=(" + v2 + ")) {\n" + countVar + "= 0; \n } ";
						checkStr += "else {\n" + countVar + "= 2147483647;\n}";
//						System.out.println("1.3");
//						System.out.println(fs);
					}
				}
			} else {
				// v1, v2 as constants
				int v1Val = (int) Misc.getConstantValue(v1);
				int v2Val = (int) Misc.getConstantValue(v2);

				if (!isV3Constant) {
					// v3 as variable
					if (v1Val >= v2Val) {
						// v1 >= v2
						checkStr += countVar + " = 0; ";
//						System.out.println("1.4");
//						System.out.println(fs);
					} else {
						// v1 < v2
						int temp = v2Val - v1Val;
						String ceilArg = temp + "/" + "(" + v3 + ")";
						String tempCeil = Builder.getNewTempName("tceil");
						Declaration tempCeilDec = FrontEnd
								.parseAndNormalize("int " + tempCeil + " = " + ceilArg + ";\n", Declaration.class);
						String ceilFuncStr = countVar + " = ceil(" + tempCeil + ");";
						Statement ceilFuncStmt = FrontEnd.parseAndNormalize(ceilFuncStr, Statement.class);
						checkStr += "if ((" + v3 + ")>0) {\n" + tempCeilDec + "\n" + ceilFuncStmt + "\n} else {\n"
								+ countVar + "= 2147483647;\n}";
//						System.out.println("1.5");
//						System.out.println(fs);
					}
				} else {
					// v3 as constant
					int v3Val = (int) Misc.getConstantValue(v3);
					if (v1Val >= v2Val) {
						// v1 >= v2
						checkStr += countVar + " = 0; ";
//						System.out.println("1.6");
//						System.out.println(fs);
					} else {
						// v1 < v2
						int temp = (int) Math.ceil((v2Val - v1Val) / v3Val);
						if (v3Val > 0) {
							// v3 > 0
							checkStr += countVar + " = " + temp + ";";
//							System.out.println("1.7");
//							System.out.println(fs);
						} else {
							// v3 <= 0
							checkStr += countVar + "= 2147483647;";
//							System.out.println("1.8");
//							System.out.println(fs);
						}
					}
				}
			}

		} else if (condNode instanceof OmpForLECondition) {
			if (!isV1Constant || !isV2Constant) {
				// v1, v2 as variables
				if (!isV3Constant) {
					// v3 as variable
					checkStr += "if ((" + v1 + ")>(" + v2 + ")) {\n" + countVar + "= 0; \n } ";
					String ceilArg = "((" + v2 + ")-(" + v1 + ") + 1)/" + "(" + v3 + ")";
					String tempCeil = Builder.getNewTempName("tceil");
					Declaration tempCeilDec = FrontEnd.parseAndNormalize("int " + tempCeil + " = " + ceilArg + ";\n",
							Declaration.class);
					String ceilFuncStr = countVar + " = ceil(" + tempCeil + ");";
					Statement ceilFuncStmt = FrontEnd.parseAndNormalize(ceilFuncStr, Statement.class);
					checkStr += "else {\n" + "if ((" + v3 + ")>0) {\n" + tempCeilDec + "\n" + ceilFuncStmt
							+ "\n} else {\n" + countVar + "= 2147483647;\n}\n}";
//					System.out.println("2.1");
//					System.out.println(fs);
				} else {
					// v3 as constant
					int v3Val = (int) Misc.getConstantValue(v3);
					if (v3Val > 0) {
						// v3 > 0
						checkStr += "if ((" + v1 + ")>(" + v2 + ")) {\n" + countVar + "= 0; \n } ";
						String ceilArg = "((" + v2 + ")-(" + v1 + ") + 1)/" + v3Val;
						String tempCeil = Builder.getNewTempName("tceil");
						Declaration tempCeilDec = FrontEnd
								.parseAndNormalize("int " + tempCeil + " = " + ceilArg + ";\n", Declaration.class);
						String ceilFuncStr = countVar + " = ceil(" + tempCeil + ");";
						Statement ceilFuncStmt = FrontEnd.parseAndNormalize(ceilFuncStr, Statement.class);
						checkStr += "else {\n" + tempCeilDec + "\n" + ceilFuncStmt + "\n}";
//						System.out.println("2.2");
//						System.out.println(fs);
					} else {
						// v3 <= 0
						checkStr += "if ((" + v1 + ")>(" + v2 + ")) {\n" + countVar + "= 0; \n } ";
						checkStr += "else {\n" + countVar + "= 2147483647;\n}";
//						System.out.println("2.3");
//						System.out.println(fs);
					}
				}
			} else {
				// v1, v2 as constants
				int v1Val = (int) Misc.getConstantValue(v1);
				int v2Val = (int) Misc.getConstantValue(v2);

				if (!isV3Constant) {
					// v3 as variable
					if (v1Val > v2Val) {
						// v1 > v2
						checkStr += countVar + " = 0; ";
//						System.out.println("2.4");
//						System.out.println(fs);
					} else {
						// v1 <= v2
						int temp = v2Val - v1Val + 1;
						String ceilArg = temp + "/" + "(" + v3 + ")";
						String tempCeil = Builder.getNewTempName("tceil");
						Declaration tempCeilDec = FrontEnd
								.parseAndNormalize("int " + tempCeil + " = " + ceilArg + ";\n", Declaration.class);
						String ceilFuncStr = countVar + " = ceil(" + tempCeil + ");";
						Statement ceilFuncStmt = FrontEnd.parseAndNormalize(ceilFuncStr, Statement.class);
						checkStr += "if ((" + v3 + ")>0) {\n" + tempCeilDec + "\n" + ceilFuncStmt + "\n} else {\n"
								+ countVar + "= 2147483647;\n}";
//						System.out.println("2.5");
//						System.out.println(fs);
					}
				} else {
					// v3 as constant
					int v3Val = (int) Misc.getConstantValue(v3);
					if (v1Val > v2Val) {
						// v1 > v2
						checkStr += countVar + " = 0; ";
//						System.out.println("2.6");
//						System.out.println(fs);
					} else {
						int temp = (int) Math.ceil((v2Val - v1Val + 1) / v3Val);
						if (v3Val > 0) {
							// v1 <= v2
							// v3 > 0
							checkStr += countVar + "= " + temp + ";";
//							System.out.println("2.7");
//							System.out.println(fs);
						} else {
							// v3 <= 0
							checkStr += countVar + "= 2147483647;";
//							System.out.println("2.8");
//							System.out.println(fs);
						}
					}

				}
			}
		} else if (condNode instanceof OmpForGTCondition) {
			if (!isV1Constant || !isV2Constant) {
				if (!isV3Constant) {
					// v3 as variable
					checkStr += "if ((" + v1 + ")<=(" + v2 + ")) {\n" + countVar + "= 0; \n } ";
					String ceilArg = "((" + v1 + ")-(" + v2 + "))/" + "(" + v3 + ")";
					String tempCeil = Builder.getNewTempName("tceil");
					Declaration tempCeilDec = FrontEnd.parseAndNormalize("int " + tempCeil + " = " + ceilArg + ";\n",
							Declaration.class);
					String ceilFuncStr = countVar + " = ceil(" + tempCeil + ");";
					Statement ceilFuncStmt = FrontEnd.parseAndNormalize(ceilFuncStr, Statement.class);
					checkStr += "else {\n" + "if ((" + v3 + ")<0) {\n" + tempCeilDec + "\n" + ceilFuncStmt
							+ "\n} else {\n" + countVar + "= 2147483647;\n}\n}";
//					System.out.println("3.1");
//					System.out.println(fs);
				} else {
					// v3 as constant
					int v3Val = (int) Misc.getConstantValue(v3);
					if (v3Val < 0) {
						// v3 < 0
						checkStr += "if ((" + v1 + ")<=(" + v2 + ")) {\n" + countVar + "= 0; \n } ";
						String ceilArg = "((" + v1 + ")-(" + v2 + "))/" + v3Val;
						String tempCeil = Builder.getNewTempName("tceil");
						Declaration tempCeilDec = FrontEnd
								.parseAndNormalize("int " + tempCeil + " = " + ceilArg + ";\n", Declaration.class);
						String ceilFuncStr = countVar + " = ceil(" + tempCeil + ");";
						Statement ceilFuncStmt = FrontEnd.parseAndNormalize(ceilFuncStr, Statement.class);
						checkStr += "else {\n" + tempCeilDec + "\n" + ceilFuncStmt + "\n}";
//						System.out.println("3.2");
//						System.out.println(fs);
					} else {
						// v3 >= 0
						checkStr += "if ((" + v1 + ")<=(" + v2 + ")) {\n" + countVar + "= 0; \n } ";
						checkStr += "else {\n" + countVar + "= 2147483647;\n}";
//						System.out.println("3.3");
//						System.out.println(fs);
					}
				}
			} else {
				// v1, v2 as constants
				int v1Val = (int) Misc.getConstantValue(v1);
				int v2Val = (int) Misc.getConstantValue(v2);

				if (!isV3Constant) {
					// v3 as variable
					if (v1Val <= v2Val) {
						// v1 <= v2
						checkStr += countVar + " = 0; ";
//						System.out.println("3.4");
//						System.out.println(fs);
					} else {
						// v1 > v2
						int temp = v1Val - v2Val;
						String ceilArg = temp + "/" + "(" + v3 + ")";
						String tempCeil = Builder.getNewTempName("tceil");
						Declaration tempCeilDec = FrontEnd
								.parseAndNormalize("int " + tempCeil + " = " + ceilArg + ";\n", Declaration.class);
						String ceilFuncStr = countVar + " = ceil(" + tempCeil + ");";
						Statement ceilFuncStmt = FrontEnd.parseAndNormalize(ceilFuncStr, Statement.class);
						checkStr += "if ((" + v3 + ")<0) {\n" + tempCeilDec + "\n" + ceilFuncStmt + "\n} else {\n"
								+ countVar + "= 2147483647;\n}";
//						System.out.println("3.5");
//						System.out.println(fs);
					}
				} else {
					// v3 as constant
					int v3Val = (int) Misc.getConstantValue(v3);
					if (v1Val <= v2Val) {
						// v1 <= v2
						checkStr += countVar + " = 0; ";
//						System.out.println("3.6");
//						System.out.println(fs);
					} else {
						// v1 > v2
						if (v3Val < 0) {
							// v3 < 0
							int temp = (int) Math.ceil((v1Val - v2Val) / v3Val);
							checkStr += countVar + "= " + temp + ";";
//							System.out.println("3.7");
//							System.out.println(fs);
						} else {
							// v3 >= 0
							checkStr += countVar + "= 2147483647;";
//							System.out.println("3.8");
//							System.out.println(fs);
						}
					}

				}
			}
		} else {
			if (!isV1Constant || !isV2Constant) {
				// v1, v2 as variables
				if (!isV3Constant) {
					// v3 as variable
					checkStr += "if ((" + v1 + ")<(" + v2 + ")) {\n" + countVar + "= 0; \n } ";
					String ceilArg = "((" + v1 + ")-(" + v2 + ") +1 )/" + "(" + v3 + ");";
					String tempCeil = Builder.getNewTempName("tceil");
					Declaration tempCeilDec = FrontEnd.parseAndNormalize("int " + tempCeil + " = " + ceilArg + ";\n",
							Declaration.class);
					String ceilFuncStr = countVar + " = ceil(" + tempCeil + ");";
					Statement ceilFuncStmt = FrontEnd.parseAndNormalize(ceilFuncStr, Statement.class);
					checkStr += "else {\n" + "if ((" + v3 + ")<0) {\n" + tempCeilDec + "\n" + ceilFuncStmt
							+ "\n} else {\n" + countVar + "= 2147483647;\n}\n}";
//					System.out.println("4.1");
//					System.out.println(fs);
				} else {
					// v3 as constant
					int v3Val = (int) Misc.getConstantValue(v3);
					if (v3Val < 0) {
						// v3 < 0
						checkStr += "if ((" + v1 + ")<(" + v2 + ")) {\n" + countVar + "= 0; \n } ";
						String ceilArg = "((" + v1 + ")-(" + v2 + ") + 1)/" + v3Val;
						String tempCeil = Builder.getNewTempName("tceil");
						Declaration tempCeilDec = FrontEnd
								.parseAndNormalize("int " + tempCeil + " = " + ceilArg + ";\n", Declaration.class);
						String ceilFuncStr = countVar + " = ceil(" + tempCeil + ");";
						Statement ceilFuncStmt = FrontEnd.parseAndNormalize(ceilFuncStr, Statement.class);
						checkStr += "else {\n" + tempCeilDec + "\n" + ceilFuncStmt + "\n}";
//						System.out.println("4.2");
//						System.out.println(fs);
					} else {
						// v3 >= 0
						checkStr += "if ((" + v1 + ")<(" + v2 + ")) {\n" + countVar + "= 0; \n } ";
						checkStr += "else {\n" + countVar + "= 2147483647;\n}";
//						System.out.println("4.3");
//						System.out.println(fs);
					}
				}
			} else {
				// v1, v2 as constants
				int v1Val = (int) Misc.getConstantValue(v1);
				int v2Val = (int) Misc.getConstantValue(v2);

				if (!isV3Constant) {
					// v3 as variable
					if (v1Val < v2Val) {
						// v1 < v2
						checkStr += countVar + " = 0; ";
//						System.out.println("4.4");
//						System.out.println(fs);
					} else {
						// v1 >= v2
						int temp = v1Val - v2Val + 1;
						String ceilArg = temp + "/" + "(" + v3 + ")";
						String tempCeil = Builder.getNewTempName("tceil");
						Declaration tempCeilDec = FrontEnd
								.parseAndNormalize("int " + tempCeil + " = " + ceilArg + ";\n", Declaration.class);
						String ceilFuncStr = countVar + " = ceil(" + tempCeil + ");";
						Statement ceilFuncStmt = FrontEnd.parseAndNormalize(ceilFuncStr, Statement.class);
						checkStr += "if ((" + v3 + ")<0) {\n" + tempCeilDec + "\n" + ceilFuncStmt + "\n} else {\n"
								+ countVar + "= 2147483647;\n}";
//						System.out.println("4.5");
//						System.out.println(fs);
					}
				} else {
					// v3 as constant
					int v3Val = (int) Misc.getConstantValue(v3);
					if (v1Val < v2Val) {
						// v1 < v2
						checkStr += countVar + " = 0; ";
//						System.out.println("4.6");
//						System.out.println(fs);
					} else {
						// v1 >= v2
						int temp = (int) Math.ceil((v1Val - v2Val + 1) / v3Val);
						if (v3Val < 0) {
							// v3 < 0
							checkStr += countVar + "= " + temp + ";";
//							System.out.println("4.7");
//							System.out.println(fs);
						} else {
							// v3 >= 0
							checkStr += countVar + "= 2147483647;";
//							System.out.println("4.8");
//							System.out.println(fs);
						}
					}

				}
			}
		}

		Statement stmt = FrontEnd.parseAndNormalize(checkStr, Statement.class);
		List<Node> stmtList = new ArrayList<>();
		stmtList.add(stmt);
		Set<String> penDecSet = new HashSet<>();
		penDecSet.add(countVar);
		CostExpressionReturn cer = new CostExpressionReturn(stmtList, countVar, penDecSet);
		if (Utilities.doesCERConflictWithNode(cer, fs, fs.getInfo().getCFGInfo().getBody())) {
			Misc.exitDueToError("Variables in count expression are written inside the loop: " + fs);
		}

		return cer;
	}

	public static boolean doesCERConflictWithNode(CostExpressionReturn cer, Node insertionPoint, Node node) {
		CellSet retSet = new CellSet();
		for (Node elem : cer.code) {
			CompoundElementLink ceLink = (CompoundElementLink) CFGLinkFinder.getCFGLinkFor(insertionPoint);
			ceLink.enclosingNonLeafNode.getInfo().getCFGInfo().addElement(ceLink.getIndex(), elem);
			// TODO: Check this for a bug where node is not node anymore.
			if (Utilities.areTwoNodesConflicting(elem, node)) {
				return true;
			}
			NodeRemover.removeNode(elem);
		}
		return false;
	}

}
