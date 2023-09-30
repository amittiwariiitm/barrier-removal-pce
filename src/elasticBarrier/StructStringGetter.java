package elasticBarrier;

import java.util.HashSet;
import java.util.Set;

import imop.ast.node.external.ANDExpression;
import imop.ast.node.external.APostfixOperation;
import imop.ast.node.external.AdditiveExpression;
import imop.ast.node.external.AdditiveMinusExpression;
import imop.ast.node.external.AdditiveOptionalExpression;
import imop.ast.node.external.AdditivePlusExpression;
import imop.ast.node.external.ArgumentList;
import imop.ast.node.external.ArrowId;
import imop.ast.node.external.AssignmentExpression;
import imop.ast.node.external.BracketExpression;
import imop.ast.node.external.CastExpression;
import imop.ast.node.external.CastExpressionTyped;
import imop.ast.node.external.ConditionalExpression;
import imop.ast.node.external.Constant;
import imop.ast.node.external.Declaration;
import imop.ast.node.external.Declarator;
import imop.ast.node.external.DotId;
import imop.ast.node.external.EqualExpression;
import imop.ast.node.external.EqualOptionalExpression;
import imop.ast.node.external.EqualityExpression;
import imop.ast.node.external.ExclusiveORExpression;
import imop.ast.node.external.Expression;
import imop.ast.node.external.ExpressionClosed;
import imop.ast.node.external.ExpressionList;
import imop.ast.node.external.ExpressionStatement;
import imop.ast.node.external.FinalClause;
import imop.ast.node.external.IfClause;
import imop.ast.node.external.InclusiveORExpression;
import imop.ast.node.external.InitDeclarator;
import imop.ast.node.external.Initializer;
import imop.ast.node.external.LogicalANDExpression;
import imop.ast.node.external.LogicalORExpression;
import imop.ast.node.external.MinusMinus;
import imop.ast.node.external.MultiplicativeDivExpression;
import imop.ast.node.external.MultiplicativeExpression;
import imop.ast.node.external.MultiplicativeModExpression;
import imop.ast.node.external.MultiplicativeMultiExpression;
import imop.ast.node.external.MultiplicativeOptionalExpression;
import imop.ast.node.external.Node;
import imop.ast.node.external.NodeListOptional;
import imop.ast.node.external.NodeSequence;
import imop.ast.node.external.NodeToken;
import imop.ast.node.external.NonConditionalExpression;
import imop.ast.node.external.NonEqualExpression;
import imop.ast.node.external.NumThreadsClause;
import imop.ast.node.external.OmpForAdditive;
import imop.ast.node.external.OmpForGECondition;
import imop.ast.node.external.OmpForGTCondition;
import imop.ast.node.external.OmpForInitExpression;
import imop.ast.node.external.OmpForLECondition;
import imop.ast.node.external.OmpForLTCondition;
import imop.ast.node.external.OmpForMultiplicative;
import imop.ast.node.external.OmpForSubtractive;
import imop.ast.node.external.ParameterDeclaration;
import imop.ast.node.external.PlusPlus;
import imop.ast.node.external.PostDecrementId;
import imop.ast.node.external.PostIncrementId;
import imop.ast.node.external.PostfixExpression;
import imop.ast.node.external.PreDecrementId;
import imop.ast.node.external.PreIncrementId;
import imop.ast.node.external.PrimaryExpression;
import imop.ast.node.external.RelationalExpression;
import imop.ast.node.external.RelationalGEExpression;
import imop.ast.node.external.RelationalGTExpression;
import imop.ast.node.external.RelationalLEExpression;
import imop.ast.node.external.RelationalLTExpression;
import imop.ast.node.external.RelationalOptionalExpression;
import imop.ast.node.external.ReturnStatement;
import imop.ast.node.external.ShiftExpression;
import imop.ast.node.external.ShiftLeftExpression;
import imop.ast.node.external.ShiftOptionalExpression;
import imop.ast.node.external.ShiftRightExpression;
import imop.ast.node.external.ShortAssignMinus;
import imop.ast.node.external.ShortAssignPlus;
import imop.ast.node.external.SizeofTypeName;
import imop.ast.node.external.SizeofUnaryExpression;
import imop.ast.node.external.UnaryCastExpression;
import imop.ast.node.external.UnaryExpression;
import imop.ast.node.external.UnaryExpressionPreDecrement;
import imop.ast.node.external.UnaryExpressionPreIncrement;
import imop.ast.node.external.UnarySizeofExpression;
import imop.ast.node.internal.CallStatement;
import imop.ast.node.internal.PostCallNode;
import imop.ast.node.internal.PreCallNode;
import imop.ast.node.internal.SimplePrimaryExpression;
import imop.baseVisitor.GJNoArguDepthFirstProcess;
import imop.lib.analysis.flowanalysis.Cell;
import imop.lib.getter.CellAccessGetter;
import imop.lib.util.CellList;
import imop.lib.util.Misc;

public class StructStringGetter {
	public static Set<String> getStructReadStrings(Node node) {
		Set<String> readList = new HashSet<String>();
		if (node instanceof Expression) {
			Node cfgLeaf = Misc.getCFGNodeFor(node);
			assert (cfgLeaf != null);
			StructAccessGetter accessGetter = new StructAccessGetter(cfgLeaf);
			Set<String> unknownList = node.accept(accessGetter);
			if (unknownList != null) {
				readList.addAll(unknownList);
			}
			readList.addAll(accessGetter.structAccessReadList);
		} else {
			node = Misc.getCFGNodeFor(node);
			if (node == null) {
				Misc.warnDueToLackOfFeature("Cannot obtain reads for non-executable statements.", node);
				return null;
			}
			for (Node leafContent : node.getInfo().getCFGInfo().getIntraTaskCFGLeafContents()) {
				StructAccessGetter accessGetter = new StructAccessGetter(leafContent);
				Set<String> unknownList = leafContent.accept(accessGetter);
				if (unknownList != null) {
					readList.addAll(unknownList);
				}
				readList.addAll(accessGetter.structAccessReadList);
			}
		}
		return readList;
	}

	public static Set<String> getStructWriteStrings(Node node) {
		Set<String> writeList = new HashSet<>();
		StructAccessGetter accessGetter;
		if (node instanceof Expression) {
			Node cfgLeaf = Misc.getCFGNodeFor(node);
			assert (cfgLeaf != null);
			accessGetter = new StructAccessGetter(cfgLeaf);
			node.accept(accessGetter);
			writeList.addAll(accessGetter.structAccessWriteList);
		} else {
			node = Misc.getCFGNodeFor(node);
			if (node == null) {
				Misc.warnDueToLackOfFeature("Cannot obtain writes for non-executable statements.", node);
				return null;
			}
			for (Node leafContent : node.getInfo().getCFGInfo().getIntraTaskCFGLeafContents()) {
				accessGetter = new StructAccessGetter(leafContent);
				leafContent.accept(accessGetter);
				writeList.addAll(accessGetter.structAccessWriteList);
			}
		}
		return writeList;
	}

	private static class StructAccessGetter extends GJNoArguDepthFirstProcess<Set<String>> {
		public Set<String> structAccessReadList = new HashSet<>();
		public Set<String> structAccessWriteList = new HashSet<>();
		protected final Node cfgLeaf;

		public StructAccessGetter(Node leaf) {
			this.cfgLeaf = leaf;
		}

		public void addReads(Set<String> structStringSet) {
			if (structStringSet == null || structStringSet.isEmpty()) {
				return;
			}
			this.structAccessReadList.addAll(structStringSet);
		}

		public void addWrites(Set<String> structStringSet) {
			if (structStringSet == null || structStringSet.isEmpty()) {
				return;
			}
			this.structAccessWriteList.addAll(structStringSet);
		}

		@Override
		public Set<String> visit(NodeToken n) {
			return null;
		}

		/**
		 * f0 ::= DeclarationSpecifiers()
		 * f1 ::= ( InitDeclaratorList() )?
		 * f2 ::= ";"
		 */
		@Override
		public Set<String> visit(Declaration n) {
			Set<String> _ret = null;
			n.getF1().accept(this);
			return _ret;
		}

		/**
		 * f0 ::= Declarator()
		 * f1 ::= ( "=" Initializer() )?
		 */
		@Override
		public Set<String> visit(InitDeclarator n) {
			if (n.getF1().getNode() != null) {
				addReads(((NodeSequence) n.getF1().getNode()).getNodes().get(1).accept(this));
			}
			return null;
		}

		/**
		 * f0 ::= ( Pointer() )?
		 * f1 ::= DirectDeclarator()
		 */
		@Override
		public Set<String> visit(Declarator n) {
			return null;
		}

		/**
		 * f0 ::= DeclarationSpecifiers()
		 * f1 ::= ParameterAbstraction()
		 */
		@Override
		public Set<String> visit(ParameterDeclaration n) {
			return null;
		}

		/**
		 * f0 ::= AssignmentExpression()
		 * | ArrayInitializer()
		 */
		@Override
		public Set<String> visit(Initializer n) {
			if (n.getF0().getChoice() instanceof AssignmentExpression) {
				return n.getF0().getChoice().accept(this);
			} else {
				for (Initializer init : Misc.getExactEnclosee(n.getF0().getChoice(), Initializer.class)) {
					addReads(init.accept(this));
				}
				return null;
			}
		}

		/**
		 * f0 ::= <IF>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 */
		@Override
		public Set<String> visit(IfClause n) {
			addReads(n.getF2().accept(this));
			return null;
		}

		/**
		 * f0 ::= <NUM_THREADS>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 */
		@Override
		public Set<String> visit(NumThreadsClause n) {
			addReads(n.getF2().accept(this));
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= "="
		 * f2 ::= Expression()
		 */
		@Override
		public Set<String> visit(OmpForInitExpression n) {
			addReads(n.getF2().accept(this));
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= "<"
		 * f2 ::= Expression()
		 */
		@Override
		public Set<String> visit(OmpForLTCondition n) {
			addReads(n.getF2().accept(this));
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= "<="
		 * f2 ::= Expression()
		 */
		@Override
		public Set<String> visit(OmpForLECondition n) {
			addReads(n.getF2().accept(this));
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= ">"
		 * f2 ::= Expression()
		 */
		@Override
		public Set<String> visit(OmpForGTCondition n) {
			addReads(n.getF2().accept(this));
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= ">="
		 * f2 ::= Expression()
		 */
		@Override
		public Set<String> visit(OmpForGECondition n) {
			addReads(n.getF2().accept(this));
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= "++"
		 */
		@Override
		public Set<String> visit(PostIncrementId n) {
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= "--"
		 */
		@Override
		public Set<String> visit(PostDecrementId n) {
			return null;
		}

		/**
		 * f0 ::= "++"
		 * f1 ::= <IDENTIFIER>
		 */
		@Override
		public Set<String> visit(PreIncrementId n) {
			return null;
		}

		/**
		 * f0 ::= "--"
		 * f1 ::= <IDENTIFIER>
		 */
		@Override
		public Set<String> visit(PreDecrementId n) {
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= "+="
		 * f2 ::= Expression()
		 */
		@Override
		public Set<String> visit(ShortAssignPlus n) {
			addReads(n.getF2().accept(this));
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= "-="
		 * f2 ::= Expression()
		 */
		@Override
		public Set<String> visit(ShortAssignMinus n) {
			addReads(n.getF2().accept(this));
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= "="
		 * f2 ::= <IDENTIFIER>
		 * f3 ::= "+"
		 * f4 ::= AdditiveExpression()
		 */
		@Override
		public Set<String> visit(OmpForAdditive n) {
			addReads(n.getF4().accept(this));
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= "="
		 * f2 ::= <IDENTIFIER>
		 * f3 ::= "-"
		 * f4 ::= AdditiveExpression()
		 */
		@Override
		public Set<String> visit(OmpForSubtractive n) {
			addReads(n.getF4().accept(this));
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * f1 ::= "="
		 * f2 ::= MultiplicativeExpression()
		 * f3 ::= "+"
		 * f4 ::= <IDENTIFIER>
		 */
		@Override
		public Set<String> visit(OmpForMultiplicative n) {
			addReads(n.getF2().accept(this));
			return null;
		}

		/**
		 * f0 ::= <FINAL>
		 * f1 ::= "("
		 * f2 ::= Expression()
		 * f3 ::= ")"
		 */
		@Override
		public Set<String> visit(FinalClause n) {
			addReads(n.getF2().accept(this));
			return null;
		}

		/**
		 * f0 ::= ( Expression() )?
		 * f1 ::= ";"
		 */
		@Override
		public Set<String> visit(ExpressionStatement n) {
			if (n.getF0().present()) {
				addReads(n.getF0().getNode().accept(this));
			}
			return null;
		}

		/**
		 * f0 ::= <RETURN>
		 * f1 ::= ( Expression() )?
		 * f2 ::= ";"
		 */
		@Override
		public Set<String> visit(ReturnStatement n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= AssignmentExpression()
		 * f1 ::= ( "," AssignmentExpression() )*
		 */
		@Override
		public Set<String> visit(Expression n) {
			assert (n.getExpF1().getNodes().isEmpty());
			return n.getExpF0().accept(this);
		}

		/**
		 * f0 ::= NonConditionalExpression()
		 * | ConditionalExpression()
		 */
		@Override
		public Set<String> visit(AssignmentExpression n) {
			return n.getF0().accept(this);
		}

		/**
		 * f0 ::= UnaryExpression()
		 * f1 ::= AssignmentOperator()
		 * f2 ::= AssignmentExpression()
		 */
		@Override
		public Set<String> visit(NonConditionalExpression n) {
			// UnaryExpression may either be an lvalue, or not,
			// but in this case, it must be an lvalue since it is on LHS of assignment
			Set<String> ss0 = n.getF0().accept(this);
			String operator = ((NodeToken) n.getF1().getF0().getChoice()).getTokenImage();
			if (operator.equals("=")) {
				// UnaryExpression is only written to
				addWrites(ss0);
				// addWrites(symList);
			} else {
				// UnaryExpression is both read and written
				addReads(ss0);
				addWrites(ss0);
			}
			addReads(n.getF2().accept(this));
			return null;
		}

		/**
		 * f0 ::= LogicalORExpression()
		 * f1 ::= ( "?" Expression() ":" ConditionalExpression() )?
		 */
		@Override
		public Set<String> visit(ConditionalExpression n) {
			if (n.getF1().getNode() == null) {
				return n.getF0().accept(this);
			} else {
				NodeSequence seq = (NodeSequence) n.getF1().getNode();
				Expression exp = (Expression) seq.getNodes().get(1);
				ConditionalExpression condExp = (ConditionalExpression) seq.getNodes().get(3);

				addReads(n.getF0().accept(this));
				addReads(exp.accept(this));
				addReads(condExp.accept(this));

				return null;
			}
		}

		/**
		 * f0 ::= LogicalANDExpression()
		 * f1 ::= ( "||" LogicalORExpression() )?
		 */
		@Override
		public Set<String> visit(LogicalORExpression n) {
			if (n.getF1().getNode() == null) {
				return n.getF0().accept(this);
			} else {
				addReads(n.getF0().accept(this));
				addReads(((NodeSequence) n.getF1().getNode()).getNodes().get(1).accept(this));
				return null;
			}
		}

		/**
		 * f0 ::= InclusiveORExpression()
		 * f1 ::= ( "&&" LogicalANDExpression() )?
		 */
		@Override
		public Set<String> visit(LogicalANDExpression n) {
			if (n.getF1().getNode() == null) {
				return n.getF0().accept(this);
			} else {
				addReads(n.getF0().accept(this));
				addReads(((NodeSequence) n.getF1().getNode()).getNodes().get(1).accept(this));
				return null;
			}
		}

		/**
		 * f0 ::= ExclusiveORExpression()
		 * f1 ::= ( "|" InclusiveORExpression() )?
		 */
		@Override
		public Set<String> visit(InclusiveORExpression n) {
			if (n.getF1().getNode() == null) {
				return n.getF0().accept(this);
			} else {
				addReads(n.getF0().accept(this));
				addReads(((NodeSequence) n.getF1().getNode()).getNodes().get(1).accept(this));
				return null;
			}
		}

		/**
		 * f0 ::= ANDExpression()
		 * f1 ::= ( "^" ExclusiveORExpression() )?
		 */
		@Override
		public Set<String> visit(ExclusiveORExpression n) {
			if (n.getF1().getNode() == null) {
				return n.getF0().accept(this);
			} else {
				addReads(n.getF0().accept(this));
				addReads(((NodeSequence) n.getF1().getNode()).getNodes().get(1).accept(this));
				return null;
			}
		}

		/**
		 * f0 ::= EqualityExpression()
		 * f1 ::= ( "&" ANDExpression() )?
		 */
		@Override
		public Set<String> visit(ANDExpression n) {
			if (n.getF1().getNode() == null) {
				return n.getF0().accept(this);
			} else {
				addReads(n.getF0().accept(this));
				addReads(((NodeSequence) n.getF1().getNode()).getNodes().get(1).accept(this));
				return null;
			}
		}

		/**
		 * f0 ::= RelationalExpression()
		 * f1 ::= ( EqualOptionalExpression() )?
		 */
		@Override
		public Set<String> visit(EqualityExpression n) {
			if (n.getF1().getNode() == null) {
				return n.getF0().accept(this);
			} else {
				addReads(n.getF0().accept(this));
				n.getF1().getNode().accept(this);
				return null;
			}
		}

		/**
		 * f0 ::= EqualExpression()
		 * | NonEqualExpression()
		 */
		@Override
		public Set<String> visit(EqualOptionalExpression n) {
			return n.getF0().accept(this);
		}

		/**
		 * f0 ::= "=="
		 * f1 ::= EqualityExpression()
		 */
		@Override
		public Set<String> visit(EqualExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= "!="
		 * f1 ::= EqualityExpression()
		 */
		@Override
		public Set<String> visit(NonEqualExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= ShiftExpression()
		 * f1 ::= ( RelationalOptionalExpression() )?
		 */
		@Override
		public Set<String> visit(RelationalExpression n) {
			if (n.getRelExpF1().getNode() == null) {
				return n.getRelExpF0().accept(this);
			} else {
				addReads(n.getRelExpF0().accept(this));
				n.getRelExpF1().getNode().accept(this);
				return null;
			}
		}

		/**
		 * f0 ::= RelationalLTExpression()
		 * | RelationalGTExpression()
		 * | RelationalLEExpression()
		 * | RelationalGEExpression()
		 */
		@Override
		public Set<String> visit(RelationalOptionalExpression n) {
			return n.getF0().accept(this);
		}

		/**
		 * f0 ::= "<"
		 * f1 ::= RelationalExpression()
		 */
		@Override
		public Set<String> visit(RelationalLTExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= ">"
		 * f1 ::= RelationalExpression()
		 */
		@Override
		public Set<String> visit(RelationalGTExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= "<="
		 * f1 ::= RelationalExpression()
		 */
		@Override
		public Set<String> visit(RelationalLEExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= ">="
		 * f1 ::= RelationalExpression()
		 */
		@Override
		public Set<String> visit(RelationalGEExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= AdditiveExpression()
		 * f1 ::= ( ShiftOptionalExpression() )?
		 */
		@Override
		public Set<String> visit(ShiftExpression n) {
			if (n.getF1().getNode() == null) {
				return n.getF0().accept(this);
			} else {
				addReads(n.getF0().accept(this));
				n.getF1().getNode().accept(this);
				return null;
			}
		}

		/**
		 * f0 ::= ShiftLeftExpression()
		 * | ShiftRightExpression()
		 */
		@Override
		public Set<String> visit(ShiftOptionalExpression n) {
			return n.getF0().accept(this);
		}

		/**
		 * f0 ::= ">>"
		 * f1 ::= ShiftExpression()
		 */
		@Override
		public Set<String> visit(ShiftLeftExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= "<<"
		 * f1 ::= ShiftExpression()
		 */
		@Override
		public Set<String> visit(ShiftRightExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= MultiplicativeExpression()
		 * f1 ::= ( AdditiveOptionalExpression() )?
		 */
		@Override
		public Set<String> visit(AdditiveExpression n) {
			if (n.getF1().getNode() == null) {
				return n.getF0().accept(this);
			} else {
				addReads(n.getF0().accept(this));
				n.getF1().getNode().accept(this);
				return null;
			}
		}

		/**
		 * f0 ::= AdditivePlusExpression()
		 * | AdditiveMinusExpression()
		 */
		@Override
		public Set<String> visit(AdditiveOptionalExpression n) {
			return n.getF0().accept(this);
		}

		/**
		 * f0 ::= "+"
		 * f1 ::= AdditiveExpression()
		 */
		@Override
		public Set<String> visit(AdditivePlusExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= "-"
		 * f1 ::= AdditiveExpression()
		 */
		@Override
		public Set<String> visit(AdditiveMinusExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= CastExpression()
		 * f1 ::= ( MultiplicativeOptionalExpression() )?
		 */
		@Override
		public Set<String> visit(MultiplicativeExpression n) {
			if (n.getF1().getNode() == null) {
				return n.getF0().accept(this);
			} else {
				addReads(n.getF0().accept(this));
				n.getF1().getNode().accept(this);
				return null;
			}
		}

		/**
		 * f0 ::= MultiplicativeMultiExpression()
		 * | MultiplicativeDivExpression()
		 * | MultiplicativeModExpression()
		 */
		@Override
		public Set<String> visit(MultiplicativeOptionalExpression n) {
			return n.getF0().accept(this);
		}

		/**
		 * f0 ::= "*"
		 * f1 ::= MultiplicativeExpression()
		 */
		@Override
		public Set<String> visit(MultiplicativeMultiExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= "/"
		 * f1 ::= MultiplicativeExpression()
		 */
		@Override
		public Set<String> visit(MultiplicativeDivExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= "%"
		 * f1 ::= MultiplicativeExpression()
		 */
		@Override
		public Set<String> visit(MultiplicativeModExpression n) {
			addReads(n.getF1().accept(this));
			return null;
		}

		/**
		 * f0 ::= CastExpressionTyped()
		 * | UnaryExpression()
		 */
		@Override
		public Set<String> visit(CastExpression n) {
			return n.getF0().accept(this);
		}

		/**
		 * f0 ::= "("
		 * f1 ::= TypeName()
		 * f2 ::= ")"
		 * f3 ::= CastExpression()
		 */
		@Override
		public Set<String> visit(CastExpressionTyped n) {
			addReads(n.getF3().accept(this));
			return null;
		}

		/**
		 * f0 ::= UnaryExpressionPreIncrement()
		 * | UnaryExpressionPreDecrement()
		 * | UnarySizeofExpression()
		 * | UnaryCastExpression()
		 * | PostfixExpression()
		 */
		@Override
		public Set<String> visit(UnaryExpression n) {
			return n.getF0().accept(this);
		}

		/**
		 * f0 ::= "++"
		 * f1 ::= UnaryExpression()
		 */
		@Override
		public Set<String> visit(UnaryExpressionPreIncrement n) {
			Set<String> symList = n.getF1().accept(this);
			addReads(symList);
			addWrites(symList);
			return null;
		}

		/**
		 * f0 ::= "--"
		 * f1 ::= UnaryExpression()
		 */
		@Override
		public Set<String> visit(UnaryExpressionPreDecrement n) {
			Set<String> symList = n.getF1().accept(this);
			addReads(symList);
			addWrites(symList);
			return null;
		}

		/**
		 * f0 ::= UnaryOperator()
		 * f1 ::= CastExpression()
		 */
		@Override
		public Set<String> visit(UnaryCastExpression n) {
			Set<String> ss = n.getF1().accept(this);
			if (ss == null) {
				return null;
			}
			String operator = ((NodeToken) n.getF0().getF0().getChoice()).getTokenImage();
			switch (operator) {
			case "&":
				return null;
			case "*":
				if (!ss.isEmpty()) {
					Misc.exitDueToLackOfFeature("Not handling * on Set<String>.");
				}
				return null;
			case "+":
			case "-":
			case "~":
			case "!":
				addReads(ss);
				return null;
			default:
				return null;
			}
		}

		/**
		 * f0 ::= SizeofTypeName()
		 * | SizeofUnaryExpression()
		 */
		@Override
		public Set<String> visit(UnarySizeofExpression n) {
			// Expression is not evaluated when present as the argument
			// to sizeof operator.
			return null;
		}

		/**
		 * f0 ::= <SIZEOF>
		 * f1 ::= UnaryExpression()
		 */
		@Override
		public Set<String> visit(SizeofUnaryExpression n) {
			// Expression is not evaluated when present as the argument to sizeof expression
			return null;
		}

		/**
		 * f0 ::= <SIZEOF>
		 * f1 ::= "("
		 * f2 ::= TypeName()
		 * f3 ::= ")"
		 */
		@Override
		public Set<String> visit(SizeofTypeName n) {
			return null;
		}

		/**
		 * f0 ::= PrimaryExpression()
		 * f1 ::= PostfixOperationsList()
		 */
		@Override
		public Set<String> visit(PostfixExpression n) {
			Set<String> ss = n.getF0().accept(this);
			CellList cl = null;
			boolean convertedToSS; // meaning that the expression traversed so far has Struct Access
			if (ss == null || ss.isEmpty()) {
				convertedToSS = false;
				ss = new HashSet<>();
				cl = CellAccessGetter.getLocationsOf(n.getF0());
				if (cl == null) {
					cl = new CellList();
				}
			} else {
				convertedToSS = true;
			}
			NodeListOptional nodeList = n.getF1().getF0();
			if (nodeList.getNodes().isEmpty()) {
				return ss;
			} else {
				int size = nodeList.getNodes().size();
				for (int i = 0; i < size; i++) {
					Node opNode = ((APostfixOperation) nodeList.getNodes().get(i)).getF0().getChoice();

					if (convertedToSS) {
						if (opNode instanceof BracketExpression) {
							// (p->i)[e] or (p.i)[r]
							BracketExpression bExp = ((BracketExpression) opNode);
							addReads(bExp.getF1().accept(this));
						} else if (opNode instanceof ArgumentList) {
							assert (false);
							return null;
						} else if (opNode instanceof DotId) {
							// (p->i).a or p.i.a
							Set<String> newSS = new HashSet<>();
							for (String s : ss) {
								newSS.add(s + "." + ((DotId) opNode).getF1());
							}
							ss = newSS;
						} else if (opNode instanceof ArrowId) {
							Misc.exitDueToLackOfFeature("Not handling -> on Set<String>.");
						} else if (opNode instanceof PlusPlus) {
							addReads(ss);
							addWrites(ss);
							return null;
						} else if (opNode instanceof MinusMinus) {
							addReads(ss);
							addWrites(ss);
							return null;
						} else {
							assert (false);
						}
					} else {
						if (opNode instanceof BracketExpression) {
							BracketExpression bExp = ((BracketExpression) opNode);
							addReads(bExp.getF1().accept(this));

							CellList newCl = new CellList();
							for (Cell c : cl) {
								newCl.addAll(c.getPointsTo(cfgLeaf));
							}
							cl = newCl;
						} else if (opNode instanceof ArgumentList) {
							assert (false);
							return null;
						} else if (opNode instanceof DotId) {
							for (Cell c : cl) {
								ss.add(c.toString() + "." + ((DotId) opNode).getF1());
							}
							convertedToSS = true;
						} else if (opNode instanceof ArrowId) {
							CellList newCl = new CellList();
							for (Cell c : cl) {
								newCl.addAll(c.getPointsTo(cfgLeaf));
							}
							cl = newCl;
							for (Cell c : cl) {
								ss.add(c.toString() + "." + ((ArrowId) opNode).getF1());
							}
							convertedToSS = true;
						} else if (opNode instanceof PlusPlus) {
							return null;
						} else if (opNode instanceof MinusMinus) {
							return null;
						} else {
							assert (false);
						}
					}
				}
				return ss;
			}
		}

		/**
		 * f0 ::= "["
		 * f1 ::= Expression()
		 * f2 ::= "]"
		 */
		@Override
		public Set<String> visit(BracketExpression n) {
			assert (false);
			return null;
		}

		/**
		 * f0 ::= "("
		 * f1 ::= ( ExpressionList() )?
		 * f2 ::= ")"
		 */
		@Override
		public Set<String> visit(ArgumentList n) {
			// Since we have performed expression simplification,
			// we won't enter this operator.
			assert (false);
			// n.getF1().accept(this);
			return null;
		}

		/**
		 * f0 ::= <IDENTIFIER>
		 * | Constant()
		 * | ExpressionClosed()
		 */
		@Override
		public Set<String> visit(PrimaryExpression n) {
			if (n.getF0().getChoice() instanceof NodeToken) {
				return null;
			} else if (n.getF0().getChoice() instanceof Constant) {
				return null;
			} else if (n.getF0().getChoice() instanceof ExpressionClosed) {
				return n.getF0().getChoice().accept(this);
			} else {
				assert (false);
				return null;
			}
		}

		/**
		 * f0 ::= "("
		 * f1 ::= Expression()
		 * f2 ::= ")"
		 */
		@Override
		public Set<String> visit(ExpressionClosed n) {
			return n.getF1().accept(this);
		}

		/**
		 * f0 ::= AssignmentExpression()
		 * f1 ::= ( "," AssignmentExpression() )*
		 */
		@Override
		public Set<String> visit(ExpressionList n) {
			// Since we have performed expression simplification,
			// we won't enter this operator.
			assert (false);
			return null;
		}

		/**
		 * f0 ::= <INTEGER_LITERAL>
		 * | <FLOATING_POINT_LITERAL>
		 * | <CHARACTER_LITERAL>
		 * | ( <STRING_LITERAL> )+
		 */
		@Override
		public Set<String> visit(Constant n) {
			return null;
		}

		@Override
		public Set<String> visit(CallStatement n) {
			// AccessGetter should be called only on leaf nodes or their parts.
			// CallStatement is NOT a leaf node now.
			assert (false);
			return null;
		}

		@Override
		public Set<String> visit(PreCallNode n) {
			return null;
		}

		@Override
		public Set<String> visit(PostCallNode n) {
			return null;
		}

		@Override
		public Set<String> visit(SimplePrimaryExpression n) {
			return null;
		}

	}
}
