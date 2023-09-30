/*
 * Copyright (c) 2019 Aman Nougrahiya, V Krishna Nandivada, IIT Madras.
 * This file is a part of the project IMOP, licensed under the MIT license.
 * See LICENSE.md for the full text of the license.
 * 
 * The above notice shall be included in all copies or substantial
 * portions of this file.
 */
package elasticBarrier;

import imop.ast.node.external.*;
import imop.ast.node.internal.*;
import imop.baseVisitor.cfgTraversals.GJNoArguDepthFirstCFG;
import imop.lib.analysis.flowanalysis.Cell;
import imop.lib.cfg.info.ForStatementCFGInfo;
import imop.lib.util.CellSet;
import imop.lib.util.Misc;

public class MustNonStructWritesGetter {
	public static CellSet getMustNonStructWrites(Node n) {
		if (!(n instanceof Expression)) {
			n = Misc.getCFGNodeFor(n);
		}
		return n.accept(new MustNonStructWritesGetterInternal());
	}

	/**
	 * This returns a Set of variables that are For Sure (unconditionally) written
	 * by the visited nodes.
	 * Assumption: All loops will execute at least once.
	 */
	private static class MustNonStructWritesGetterInternal extends GJNoArguDepthFirstCFG<CellSet> {
		public CellSet initProcess(Node n) {
			return null;
		}

		public CellSet endProcess(Node n) {
			return null;
		}

		/**
		 * @deprecated
		 * @param n
		 * @return
		 */
		@Deprecated
		public CellSet processCalls(Node n) {
			return null;
		}

		/**
		 * Special Node
		 */
		public CellSet visit(BeginNode n) {
			return new CellSet();
		}

		/**
		 * Special Node
		 */
		public CellSet visit(EndNode n) {
			return new CellSet();
		}

		/**
		 * f0 ::= ( DeclarationSpecifiers() )?
		 * f1 ::= Declarator()
		 * f2 ::= ( DeclarationList() )?
		 * f3 ::= CompoundStatement()
		 */
		public CellSet visit(FunctionDefinition n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= DeclarationSpecifiers()
		 * f1 ::= ( InitDeclaratorList() )?
		 * f2 ::= ";"
		 */
		public CellSet visit(Declaration n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= DeclarationSpecifiers()
		 * f1 ::= ParameterAbstraction()
		 */
		public CellSet visit(ParameterDeclaration n) {
			assert (false);
			return null;
		}

		/**
		 * f0 ::= "#"
		 * f1 ::= <UNKNOWN_CPP>
		 */
		public CellSet visit(UnknownCpp n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= ParallelDirective()
		 * f2 ::= Statement()
		 */
		public CellSet visit(ParallelConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= "#"
		 * f1 ::= <PRAGMA>
		 * f2 ::= <UNKNOWN_CPP>
		 */
		public CellSet visit(UnknownPragma n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= <IF>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 */
		public CellSet visit(IfClause n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= <NUM_THREADS>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 */
		public CellSet visit(NumThreadsClause n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= ForDirective()
		 * f2 ::= OmpForHeader()
		 * f3 ::= Statement()
		 */
		public CellSet visit(ForConstruct n) {
			CellSet _ret = new CellSet();
			_ret.addAll(n.getInfo().getCFGInfo().getInitExpression().accept(this));
			_ret.addAll(n.getInfo().getCFGInfo().getForConditionExpression().accept(this));
			_ret.addAll(n.getInfo().getCFGInfo().getReinitExpression().accept(this));
			_ret.addAll(n.getInfo().getCFGInfo().getBody().accept(this));
			return _ret;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= "="
		 * f2 ::= Expression()
		 */
		public CellSet visit(OmpForInitExpression n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= OmpForLTCondition()
		 * | OmpForLECondition()
		 * | OmpForGTCondition()
		 * | OmpForGECondition()
		 */
		public CellSet visit(OmpForCondition n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= PostIncrementId()
		 * | PostDecrementId()
		 * | PreIncrementId()
		 * | PreDecrementId()
		 * | ShortAssignPlus()
		 * | ShortAssignMinus()
		 * | OmpForAdditive()
		 * | OmpForSubtractive()
		 * | OmpForMultiplicative()
		 */
		public CellSet visit(OmpForReinitExpression n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <SECTIONS>
		 * f2 ::= NowaitDataClauseList()
		 * f3 ::= OmpEol()
		 * f4 ::= SectionsScope()
		 */
		public CellSet visit(SectionsConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <SINGLE>
		 * f2 ::= SingleClauseList()
		 * f3 ::= OmpEol()
		 * f4 ::= Statement()
		 */
		public CellSet visit(SingleConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <TASK>
		 * f2 ::= ( TaskClause() )*
		 * f3 ::= OmpEol()
		 * f4 ::= Statement()
		 */
		public CellSet visit(TaskConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= <FINAL>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 */
		public CellSet visit(FinalClause n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <PARALLEL>
		 * f2 ::= <FOR>
		 * f3 ::= UniqueParallelOrUniqueForOrDataClauseList()
		 * f4 ::= OmpEol()
		 * f5 ::= OmpForHeader()
		 * f6 ::= Statement()
		 */
		public CellSet visit(ParallelForConstruct n) {
			assert (false);
			return null;
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <PARALLEL>
		 * f2 ::= <SECTIONS>
		 * f3 ::= UniqueParallelOrDataClauseList()
		 * f4 ::= OmpEol()
		 * f5 ::= SectionsScope()
		 */
		public CellSet visit(ParallelSectionsConstruct n) {
			assert (false);
			return null;
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <MASTER>
		 * f2 ::= OmpEol()
		 * f3 ::= Statement()
		 */
		public CellSet visit(MasterConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <CRITICAL>
		 * f2 ::= ( RegionPhrase() )?
		 * f3 ::= OmpEol()
		 * f4 ::= Statement()
		 */
		public CellSet visit(CriticalConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <ATOMIC>
		 * f2 ::= ( AtomicClause() )?
		 * f3 ::= OmpEol()
		 * f4 ::= ExpressionStatement()
		 */
		public CellSet visit(AtomicConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <FLUSH>
		 * f2 ::= ( FlushVars() )?
		 * f3 ::= OmpEol()
		 */
		public CellSet visit(FlushDirective n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <ORDERED>
		 * f2 ::= OmpEol()
		 * f3 ::= Statement()
		 */
		public CellSet visit(OrderedConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <BARRIER>
		 * f2 ::= OmpEol()
		 */
		public CellSet visit(BarrierDirective n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <TASKWAIT>
		 * f2 ::= OmpEol()
		 */
		public CellSet visit(TaskwaitDirective n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <TASKYIELD>
		 * f2 ::= OmpEol()
		 */
		public CellSet visit(TaskyieldDirective n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= ( Expression() )?
		 * f1 ::= ";"
		 */
		public CellSet visit(ExpressionStatement n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= "{"
		 * f1 ::= ( CompoundStatementElement() )*
		 * f2 ::= "}"
		 */
		public CellSet visit(CompoundStatement n) {
			CellSet _ret = new CellSet();
			for (Node elem : n.getInfo().getCFGInfo().getElementList()) {
				_ret.addAll(elem.accept(this));
			}
			return _ret;
		}

		/**
		 * f0 ::= <IF>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 * f4 ::= Statement()
		 * f5 ::= ( <ELSE> Statement() )?
		 */
		public CellSet visit(IfStatement n) {
			CellSet _ret = new CellSet();
			if (n.getInfo().getCFGInfo().hasElseBody()) {
				CellSet clThen = n.getInfo().getCFGInfo().getThenBody().accept(this);
				CellSet clElse = n.getInfo().getCFGInfo().getElseBody().accept(this);
				for (Cell c : clThen) {
					if (clElse.contains(c)) {
						_ret.add(c);
					}
				}
			}
			_ret.addAll(n.getInfo().getCFGInfo().getPredicate().accept(this));
			return _ret;
		}

		/**
		 * f0 ::= <SWITCH>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 * f4 ::= Statement()
		 */
		public CellSet visit(SwitchStatement n) {
			Misc.warnDueToLackOfFeature("Assuming that there are no unconditional writes in SwitchStmt", n);
			return new CellSet();
		}

		/**
		 * f0 ::= <WHILE>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 * f4 ::= Statement()
		 */
		public CellSet visit(WhileStatement n) {
			CellSet _ret = n.getInfo().getCFGInfo().getPredicate().accept(this);
			_ret.addAll(n.getInfo().getCFGInfo().getBody().accept(this));
			return _ret;
		}

		/**
		 * f0 ::= <DO>
		 * f1 ::= Statement()
		 * f2 ::= <WHILE>
		 * f3 ::= "("
		 * f4 ::= Expression()
		 * f5 ::= ")"
		 * f6 ::= ";"
		 */
		public CellSet visit(DoStatement n) {
			CellSet _ret = n.getInfo().getCFGInfo().getPredicate().accept(this);
			_ret.addAll(n.getInfo().getCFGInfo().getBody().accept(this));
			return _ret;
		}

		/**
		 * f0 ::= <FOR>
		 * f1 ::= "("
		 * f2 ::= ( Expression() )?
		 * f3 ::= ";"
		 * f4 ::= ( Expression() )?
		 * f5 ::= ";"
		 * f6 ::= ( Expression() )?
		 * f7 ::= ")"
		 * f8 ::= Statement()
		 */
		public CellSet visit(ForStatement n) {
			CellSet _ret = new CellSet();
			ForStatementCFGInfo forCFG = n.getInfo().getCFGInfo();
			if (forCFG.hasInitExpression()) {
				_ret.addAll(forCFG.getInitExpression().accept(this));
			}
			if (forCFG.hasTerminationExpression()) {
				_ret.addAll(forCFG.getTerminationExpression().accept(this));
			}
			if (forCFG.hasStepExpression()) {
				_ret.addAll(forCFG.getStepExpression().accept(this));
			}
			_ret.addAll(forCFG.getBody().accept(this));
			return _ret;
		}

		/**
		 * f0 ::= <GOTO>
		 * f1 ::= <IDENTIFIER>
		 * f2 ::= ";"
		 */
		public CellSet visit(GotoStatement n) {
			return new CellSet();
		}

		/**
		 * f0 ::= <CONTINUE>
		 * f1 ::= ";"
		 */
		public CellSet visit(ContinueStatement n) {
			return new CellSet();
		}

		/**
		 * f0 ::= <BREAK>
		 * f1 ::= ";"
		 */
		public CellSet visit(BreakStatement n) {
			return new CellSet();
		}

		/**
		 * f0 ::= <RETURN>
		 * f1 ::= ( Expression() )?
		 * f2 ::= ";"
		 */
		public CellSet visit(ReturnStatement n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		/**
		 * f0 ::= AssignmentExpression()
		 * f1 ::= ( "," AssignmentExpression() )*
		 */
		public CellSet visit(Expression n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		public CellSet visit(DummyFlushDirective n) {
			return new CellSet();
		}

		public CellSet visit(PreCallNode n) {
			return new CellSet();
		}

		public CellSet visit(PostCallNode n) {
			return Utilities.getNonStructSet(n.getInfo().getWrites(),
					MustStructWritesGetter.getMustStructWrites(n));
		}

		public CellSet visit(CallStatement n) {
			CellSet _ret = new CellSet();
			boolean firstSite = true;
			for (FunctionDefinition fd : n.getInfo().getCalledDefinitions()) {
				if (firstSite) {
					_ret.addAll(fd.accept(this));
					firstSite = false;
					continue;
				}

				CellSet tempSet = Misc.setIntersection(_ret, fd.accept(this));
				_ret.clear();
				_ret.addAll(tempSet);
			}
			return _ret;
		}
	}

}