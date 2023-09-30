/*
 * Copyright (c) 2019 Aman Nougrahiya, V Krishna Nandivada, IIT Madras.
 * This file is a part of the project IMOP, licensed under the MIT license.
 * See LICENSE.md for the full text of the license.
 * 
 * The above notice shall be included in all copies or substantial
 * portions of this file.
 */
package elasticBarrier;

import java.util.HashSet;
import java.util.Set;

import imop.ast.node.external.*;
import imop.ast.node.internal.*;
import imop.baseVisitor.cfgTraversals.GJNoArguDepthFirstCFG;
import imop.lib.cfg.info.ForStatementCFGInfo;
import imop.lib.util.Misc;

public class MustStructWritesGetter {
	public static Set<String> getMustStructWrites(Node n) {
		if (!(n instanceof Expression)) {
			n = Misc.getCFGNodeFor(n);
		}
		MustStructWritesGetterInternal fsswgi = new MustStructWritesGetterInternal();
		return n.accept(fsswgi);

	}

	/**
	 * This returns a Set of variables that are For Sure (unconditionally) written
	 * by the visited nodes.
	 * Assumption: All loops will execute at least once.
	 */
	private static class MustStructWritesGetterInternal extends GJNoArguDepthFirstCFG<Set<String>> {
		public Set<String> initProcess(Node n) {
			return null;
		}

		public Set<String> endProcess(Node n) {
			return null;
		}

		/**
		 * @deprecated
		 * @param n
		 * @return
		 */
		@Deprecated
		public Set<String> processCalls(Node n) {
			return null;
		}

		/**
		 * Special Node
		 */
		public Set<String> visit(BeginNode n) {
			return new HashSet<>();
		}

		/**
		 * Special Node
		 */
		public Set<String> visit(EndNode n) {
			return new HashSet<>();
		}

		/**
		 * f0 ::= ( DeclarationSpecifiers() )?
		 * f1 ::= Declarator()
		 * f2 ::= ( DeclarationList() )?
		 * f3 ::= CompoundStatement()
		 */
		public Set<String> visit(FunctionDefinition n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= DeclarationSpecifiers()
		 * f1 ::= ( InitDeclaratorList() )?
		 * f2 ::= ";"
		 */
		public Set<String> visit(Declaration n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= DeclarationSpecifiers()
		 * f1 ::= ParameterAbstraction()
		 */
		public Set<String> visit(ParameterDeclaration n) {
			assert (false);
			return null;
		}

		/**
		 * f0 ::= "#"
		 * f1 ::= <UNKNOWN_CPP>
		 */
		public Set<String> visit(UnknownCpp n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= ParallelDirective()
		 * f2 ::= Statement()
		 */
		public Set<String> visit(ParallelConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= "#"
		 * f1 ::= <PRAGMA>
		 * f2 ::= <UNKNOWN_CPP>
		 */
		public Set<String> visit(UnknownPragma n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= <IF>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 */
		public Set<String> visit(IfClause n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= <NUM_THREADS>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 */
		public Set<String> visit(NumThreadsClause n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= ForDirective()
		 * f2 ::= OmpForHeader()
		 * f3 ::= Statement()
		 */
		public Set<String> visit(ForConstruct n) {
			Set<String> _ret = new HashSet<>();
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
		public Set<String> visit(OmpForInitExpression n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= OmpForLTCondition()
		 * | OmpForLECondition()
		 * | OmpForGTCondition()
		 * | OmpForGECondition()
		 */
		public Set<String> visit(OmpForCondition n) {
			return StructStringGetter.getStructWriteStrings(n);
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
		public Set<String> visit(OmpForReinitExpression n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <SECTIONS>
		 * f2 ::= NowaitDataClauseList()
		 * f3 ::= OmpEol()
		 * f4 ::= SectionsScope()
		 */
		public Set<String> visit(SectionsConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <SINGLE>
		 * f2 ::= SingleClauseList()
		 * f3 ::= OmpEol()
		 * f4 ::= Statement()
		 */
		public Set<String> visit(SingleConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <TASK>
		 * f2 ::= ( TaskClause() )*
		 * f3 ::= OmpEol()
		 * f4 ::= Statement()
		 */
		public Set<String> visit(TaskConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= <FINAL>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 */
		public Set<String> visit(FinalClause n) {
			return StructStringGetter.getStructWriteStrings(n);
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
		public Set<String> visit(ParallelForConstruct n) {
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
		public Set<String> visit(ParallelSectionsConstruct n) {
			assert (false);
			return null;
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <MASTER>
		 * f2 ::= OmpEol()
		 * f3 ::= Statement()
		 */
		public Set<String> visit(MasterConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <CRITICAL>
		 * f2 ::= ( RegionPhrase() )?
		 * f3 ::= OmpEol()
		 * f4 ::= Statement()
		 */
		public Set<String> visit(CriticalConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <ATOMIC>
		 * f2 ::= ( AtomicClause() )?
		 * f3 ::= OmpEol()
		 * f4 ::= ExpressionStatement()
		 */
		public Set<String> visit(AtomicConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <FLUSH>
		 * f2 ::= ( FlushVars() )?
		 * f3 ::= OmpEol()
		 */
		public Set<String> visit(FlushDirective n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <ORDERED>
		 * f2 ::= OmpEol()
		 * f3 ::= Statement()
		 */
		public Set<String> visit(OrderedConstruct n) {
			return n.getInfo().getCFGInfo().getBody().accept(this);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <BARRIER>
		 * f2 ::= OmpEol()
		 */
		public Set<String> visit(BarrierDirective n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <TASKWAIT>
		 * f2 ::= OmpEol()
		 */
		public Set<String> visit(TaskwaitDirective n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <TASKYIELD>
		 * f2 ::= OmpEol()
		 */
		public Set<String> visit(TaskyieldDirective n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= ( Expression() )?
		 * f1 ::= ";"
		 */
		public Set<String> visit(ExpressionStatement n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= "{"
		 * f1 ::= ( CompoundStatementElement() )*
		 * f2 ::= "}"
		 */
		public Set<String> visit(CompoundStatement n) {
			Set<String> _ret = new HashSet<>();
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
		public Set<String> visit(IfStatement n) {
			Set<String> _ret = new HashSet<>();
			if (n.getInfo().getCFGInfo().hasElseBody()) {
				Set<String> clThen = n.getInfo().getCFGInfo().getThenBody().accept(this);
				Set<String> clElse = n.getInfo().getCFGInfo().getElseBody().accept(this);
				for (String c : clThen) {
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
		public Set<String> visit(SwitchStatement n) {
			Misc.warnDueToLackOfFeature("Assuming that there are no unconditional writes in SwitchStmt", n);
			return new HashSet<>();
		}

		/**
		 * f0 ::= <WHILE>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 * f4 ::= Statement()
		 */
		public Set<String> visit(WhileStatement n) {
			Set<String> _ret = n.getInfo().getCFGInfo().getPredicate().accept(this);
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
		public Set<String> visit(DoStatement n) {
			Set<String> _ret = n.getInfo().getCFGInfo().getPredicate().accept(this);
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
		public Set<String> visit(ForStatement n) {
			Set<String> _ret = new HashSet<>();
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
		public Set<String> visit(GotoStatement n) {
			return new HashSet<>();
		}

		/**
		 * f0 ::= <CONTINUE>
		 * f1 ::= ";"
		 */
		public Set<String> visit(ContinueStatement n) {
			return new HashSet<>();
		}

		/**
		 * f0 ::= <BREAK>
		 * f1 ::= ";"
		 */
		public Set<String> visit(BreakStatement n) {
			return new HashSet<>();
		}

		/**
		 * f0 ::= <RETURN>
		 * f1 ::= ( Expression() )?
		 * f2 ::= ";"
		 */
		public Set<String> visit(ReturnStatement n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		/**
		 * f0 ::= AssignmentExpression()
		 * f1 ::= ( "," AssignmentExpression() )*
		 */
		public Set<String> visit(Expression n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		public Set<String> visit(DummyFlushDirective n) {
			return new HashSet<>();
		}

		public Set<String> visit(PreCallNode n) {
			return new HashSet<>();
		}

		public Set<String> visit(PostCallNode n) {
			return StructStringGetter.getStructWriteStrings(n);
		}

		public Set<String> visit(CallStatement n) {
			Set<String> _ret = new HashSet<>();
			boolean firstSite = true;
			for (FunctionDefinition fd : n.getInfo().getCalledDefinitions()) {
				if (firstSite) {
					_ret.addAll(fd.accept(this));
					firstSite = false;
					continue;
				}

				Set<String> tempSet = Misc.setIntersection(_ret, fd.accept(this));
				_ret.clear();
				_ret.addAll(tempSet);
			}
			return _ret;
		}
	}

}