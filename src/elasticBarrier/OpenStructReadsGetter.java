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
import imop.baseVisitor.cfgTraversals.GJDepthFirstCFG;
import imop.lib.analysis.flowanalysis.Cell;
import imop.lib.cfg.info.ForStatementCFGInfo;
import imop.lib.util.CellList;
import imop.lib.util.CellSet;
import imop.lib.util.Misc;

public class OpenStructReadsGetter {

	public static CellSet getOpenNonStructReads(Node node, CellSet mustWrites, Set<String> mustStructWrites) {
		if (!(node instanceof Expression)) {
			node = Misc.getCFGNodeFor(node);
		}
		CellSet normalOpenReads = OpenReadsGetter.getOpenReads(node, mustWrites);
		CellList normalOpenReadsList = new CellList();
		for (Cell c : normalOpenReads) {
			normalOpenReadsList.add(c);
		}
		Set<String> structOpenReads = OpenStructReadsGetter.getOpenStructReads(node, mustStructWrites);
		return Utilities.getNonStructSet(normalOpenReadsList, structOpenReads);
	}

	public static Set<String> getOpenStructReads(Node node, Set<String> mustStructWrites) {
		if (node instanceof Expression) {
			OpenReadsStructGetterInternal accessGetter = new OpenReadsStructGetterInternal();
			return node.accept(accessGetter, mustStructWrites);
		} else {
			node = Misc.getCFGNodeFor(node);
			if (node == null) {
				Misc.warnDueToLackOfFeature("Cannot obtain reads for non-executable statements.", node);
				return null;
			}
			OpenReadsStructGetterInternal accessGetter = new OpenReadsStructGetterInternal();
			return node.accept(accessGetter, mustStructWrites);
		}
	}

	/**
	 * Provides default methods for processing all the CFG nodes.
	 * Traversal shall be defined as per the requirements.
	 * Note: This visitor is just for convenience in handling all the CFGs.
	 */
	private static class OpenReadsStructGetterInternal extends GJDepthFirstCFG<Set<String>, Set<String>> {
		@Override
		public Set<String> initProcess(Node n, Set<String> mustStructWrites) {
			return null;
		}

		@Override
		public Set<String> endProcess(Node n, Set<String> mustStructWrites) {
			return null;
		}

		/**
		 * @deprecated
		 * @param n
		 * @param mustStructWrites
		 * @return
		 */
		@Deprecated
		public Set<String> processCalls(Node n, Set<String> mustStructWrites) {
			assert (false);
			return null;
		}

		/**
		 * Special Node
		 */
		@Override
		public Set<String> visit(BeginNode n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * Special Node
		 */
		@Override
		public Set<String> visit(EndNode n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= ( DeclarationSpecifiers() )?
		 * f1 ::= Declarator()
		 * f2 ::= ( DeclarationList() )?
		 * f3 ::= CompoundStatement()
		 */
		@Override
		public Set<String> visit(FunctionDefinition n, Set<String> mustStructWrites) {
			return n.getInfo().getCFGInfo().getBody().accept(this, mustStructWrites);
		}

		/**
		 * f0 ::= DeclarationSpecifiers()
		 * f1 ::= ( InitDeclaratorList() )?
		 * f2 ::= ";"
		 */
		@Override
		public Set<String> visit(Declaration n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= DeclarationSpecifiers()
		 * f1 ::= ParameterAbstraction()
		 */
		@Override
		public Set<String> visit(ParameterDeclaration n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= "#"
		 * f1 ::= <UNKNOWN_CPP>
		 */
		@Override
		public Set<String> visit(UnknownCpp n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= ParallelDirective()
		 * f2 ::= Statement()
		 */
		@Override
		public Set<String> visit(ParallelConstruct n, Set<String> mustStructWrites) {
			return n.getInfo().getCFGInfo().getBody().accept(this, mustStructWrites);
		}

		/**
		 * f0 ::= "#"
		 * f1 ::= <PRAGMA>
		 * f2 ::= <UNKNOWN_CPP>
		 */
		@Override
		public Set<String> visit(UnknownPragma n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= <IF>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 */
		@Override
		public Set<String> visit(IfClause n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= <NUM_THREADS>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 */
		@Override
		public Set<String> visit(NumThreadsClause n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= ForDirective()
		 * f2 ::= OmpForHeader()
		 * f3 ::= Statement()
		 */
		@Override
		public Set<String> visit(ForConstruct n, Set<String> mustStructWrites) {
			Set<String> fssw = new HashSet<String>(mustStructWrites);

			Set<String> _ret = n.getInfo().getCFGInfo().getInitExpression().accept(this, fssw);
			fssw.addAll(MustStructWritesGetter.getMustStructWrites(n.getInfo().getCFGInfo().getInitExpression()));

			_ret.addAll(n.getInfo().getCFGInfo().getForConditionExpression().accept(this, fssw));
			fssw.addAll(MustStructWritesGetter.getMustStructWrites(n.getInfo().getCFGInfo().getForConditionExpression()));

			_ret.addAll(n.getInfo().getCFGInfo().getBody().accept(this, fssw));
			fssw.addAll(MustStructWritesGetter.getMustStructWrites(n.getInfo().getCFGInfo().getBody()));

			_ret.addAll(n.getInfo().getCFGInfo().getReinitExpression().accept(this, fssw));
			fssw.addAll(MustStructWritesGetter.getMustStructWrites(n.getInfo().getCFGInfo().getReinitExpression()));

			return _ret;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= "="
		 * f2 ::= Expression()
		 */
		@Override
		public Set<String> visit(OmpForInitExpression n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= OmpForLTCondition()
		 * | OmpForLECondition()
		 * | OmpForGTCondition()
		 * | OmpForGECondition()
		 */
		@Override
		public Set<String> visit(OmpForCondition n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
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
		@Override
		public Set<String> visit(OmpForReinitExpression n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <SECTIONS>
		 * f2 ::= NowaitDataClauseList()
		 * f3 ::= OmpEol()
		 * f4 ::= SectionsScope()
		 */
		@Override
		public Set<String> visit(SectionsConstruct n, Set<String> mustStructWrites) {
			return n.getInfo().getCFGInfo().getBody().accept(this, mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <SINGLE>
		 * f2 ::= SingleClauseList()
		 * f3 ::= OmpEol()
		 * f4 ::= Statement()
		 */
		@Override
		public Set<String> visit(SingleConstruct n, Set<String> mustStructWrites) {
			return n.getInfo().getCFGInfo().getBody().accept(this, mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <TASK>
		 * f2 ::= ( TaskClause() )*
		 * f3 ::= OmpEol()
		 * f4 ::= Statement()
		 */
		@Override
		public Set<String> visit(TaskConstruct n, Set<String> mustStructWrites) {
			return n.getInfo().getCFGInfo().getBody().accept(this, mustStructWrites);
		}

		/**
		 * f0 ::= <FINAL>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 */
		@Override
		public Set<String> visit(FinalClause n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
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
		@Override
		public Set<String> visit(ParallelForConstruct n, Set<String> mustStructWrites) {
			assert (false);
			return n.getInfo().getCFGInfo().getBody().accept(this, mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <PARALLEL>
		 * f2 ::= <SECTIONS>
		 * f3 ::= UniqueParallelOrDataClauseList()
		 * f4 ::= OmpEol()
		 * f5 ::= SectionsScope()
		 */
		@Override
		public Set<String> visit(ParallelSectionsConstruct n, Set<String> mustStructWrites) {
			assert (false);
			return n.getInfo().getCFGInfo().getBody().accept(this, mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <MASTER>
		 * f2 ::= OmpEol()
		 * f3 ::= Statement()
		 */
		@Override
		public Set<String> visit(MasterConstruct n, Set<String> mustStructWrites) {
			return n.getInfo().getCFGInfo().getBody().accept(this, mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <CRITICAL>
		 * f2 ::= ( RegionPhrase() )?
		 * f3 ::= OmpEol()
		 * f4 ::= Statement()
		 */
		@Override
		public Set<String> visit(CriticalConstruct n, Set<String> mustStructWrites) {
			return n.getInfo().getCFGInfo().getBody().accept(this, mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <ATOMIC>
		 * f2 ::= ( AtomicClause() )?
		 * f3 ::= OmpEol()
		 * f4 ::= ExpressionStatement()
		 */
		@Override
		public Set<String> visit(AtomicConstruct n, Set<String> mustStructWrites) {
			return n.getInfo().getCFGInfo().getBody().accept(this, mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <FLUSH>
		 * f2 ::= ( FlushVars() )?
		 * f3 ::= OmpEol()
		 */
		@Override
		public Set<String> visit(FlushDirective n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <ORDERED>
		 * f2 ::= OmpEol()
		 * f3 ::= Statement()
		 */
		@Override
		public Set<String> visit(OrderedConstruct n, Set<String> mustStructWrites) {
			return n.getInfo().getCFGInfo().getBody().accept(this, mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <BARRIER>
		 * f2 ::= OmpEol()
		 */
		@Override
		public Set<String> visit(BarrierDirective n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <TASKWAIT>
		 * f2 ::= OmpEol()
		 */
		@Override
		public Set<String> visit(TaskwaitDirective n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= OmpPragma()
		 * f1 ::= <TASKYIELD>
		 * f2 ::= OmpEol()
		 */
		@Override
		public Set<String> visit(TaskyieldDirective n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= ( Expression() )?
		 * f1 ::= ";"
		 */
		@Override
		public Set<String> visit(ExpressionStatement n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= "{"
		 * f1 ::= ( CompoundStatementElement() )*
		 * f2 ::= "}"
		 */
		@Override
		public Set<String> visit(CompoundStatement n, Set<String> mustStructWrites) {
			Set<String> fssw = new HashSet<String>(mustStructWrites);
			Set<String> _ret = new HashSet<String>();

			for (Node elem : n.getInfo().getCFGInfo().getElementList()) {
				_ret.addAll(elem.accept(this, fssw));
				fssw.addAll(MustStructWritesGetter.getMustStructWrites(elem));
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
		@Override
		public Set<String> visit(IfStatement n, Set<String> mustStructWrites) {
			Set<String> fssw = new HashSet<String>(mustStructWrites);

			Set<String> _ret = n.getInfo().getCFGInfo().getPredicate().accept(this, fssw);
			fssw.addAll(MustStructWritesGetter.getMustStructWrites(n.getInfo().getCFGInfo().getPredicate()));

			_ret.addAll(n.getInfo().getCFGInfo().getThenBody().accept(this, fssw));

			if (n.getInfo().getCFGInfo().hasElseBody()) {
				_ret.addAll(n.getInfo().getCFGInfo().getElseBody().accept(this, fssw));
			}
			return _ret;
		}

		/**
		 * f0 ::= <SWITCH>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 * f4 ::= Statement()
		 */
		@Override
		public Set<String> visit(SwitchStatement n, Set<String> mustStructWrites) {
			Misc.warnDueToLackOfFeature("Not Handling Switch Stmt", n);
			return new HashSet<String>();
		}

		/**
		 * f0 ::= <WHILE>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 * f4 ::= Statement()
		 */
		@Override
		public Set<String> visit(WhileStatement n, Set<String> mustStructWrites) {
			Set<String> fssw = new HashSet<String>(mustStructWrites);
			Set<String> _ret = new HashSet<String>();

			_ret.addAll(n.getInfo().getCFGInfo().getPredicate().accept(this, fssw));
			fssw.addAll(MustStructWritesGetter.getMustStructWrites(n.getInfo().getCFGInfo().getPredicate()));

			_ret.addAll(n.getInfo().getCFGInfo().getBody().accept(this, fssw));
			fssw.addAll(MustStructWritesGetter.getMustStructWrites(n.getInfo().getCFGInfo().getBody()));

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
		@Override
		public Set<String> visit(DoStatement n, Set<String> mustStructWrites) {
			Set<String> fssw = new HashSet<String>(mustStructWrites);
			Set<String> _ret = new HashSet<String>();

			_ret.addAll(n.getInfo().getCFGInfo().getBody().accept(this, fssw));
			fssw.addAll(MustStructWritesGetter.getMustStructWrites(n.getInfo().getCFGInfo().getBody()));

			_ret.addAll(n.getInfo().getCFGInfo().getPredicate().accept(this, fssw));
			fssw.addAll(MustStructWritesGetter.getMustStructWrites(n.getInfo().getCFGInfo().getPredicate()));

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
		@Override
		public Set<String> visit(ForStatement n, Set<String> mustStructWrites) {
			Set<String> fssw = new HashSet<String>(mustStructWrites);

			ForStatementCFGInfo forCFG = n.getInfo().getCFGInfo();
			Set<String> _ret = new HashSet<String>();

			if (forCFG.hasInitExpression()) {
				_ret.addAll(forCFG.getInitExpression().accept(this, fssw));
				fssw.addAll(MustStructWritesGetter.getMustStructWrites(forCFG.getInitExpression()));
			}

			if (forCFG.hasTerminationExpression()) {
				_ret.addAll(forCFG.getTerminationExpression().accept(this, fssw));
				fssw.addAll(MustStructWritesGetter.getMustStructWrites(forCFG.getTerminationExpression()));
			}

			_ret.addAll(forCFG.getBody().accept(this, fssw));
			fssw.addAll(MustStructWritesGetter.getMustStructWrites(forCFG.getBody()));

			if (forCFG.hasStepExpression()) {
				_ret.addAll(forCFG.getStepExpression().accept(this, fssw));
				fssw.addAll(MustStructWritesGetter.getMustStructWrites(forCFG.getStepExpression()));
			}

			return _ret;
		}

		/**
		 * f0 ::= <GOTO>
		 * f1 ::= <IDENTIFIER>
		 * f2 ::= ";"
		 */
		@Override
		public Set<String> visit(GotoStatement n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= <CONTINUE>
		 * f1 ::= ";"
		 */
		@Override
		public Set<String> visit(ContinueStatement n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= <BREAK>
		 * f1 ::= ";"
		 */
		@Override
		public Set<String> visit(BreakStatement n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= <RETURN>
		 * f1 ::= ( Expression() )?
		 * f2 ::= ";"
		 */
		@Override
		public Set<String> visit(ReturnStatement n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		/**
		 * f0 ::= AssignmentExpression()
		 * f1 ::= ( "," AssignmentExpression() )*
		 */
		@Override
		public Set<String> visit(Expression n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		@Override
		public Set<String> visit(DummyFlushDirective n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		@Override
		public Set<String> visit(PreCallNode n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		@Override
		public Set<String> visit(PostCallNode n, Set<String> mustStructWrites) {
			return Misc.setMinus((StructStringGetter.getStructReadStrings(n)), mustStructWrites);
		}

		@Override
		public Set<String> visit(CallStatement n, Set<String> mustStructWrites) {
			Set<String> fssw = new HashSet<String>(mustStructWrites);
			Set<String> _ret = n.getInfo().getCFGInfo().getPreCallNode().accept(this, fssw);

			for (FunctionDefinition fd : n.getInfo().getCalledDefinitions()) {
				_ret.addAll(fd.accept(this, fssw));
			}
			return _ret;
		}
	}
}