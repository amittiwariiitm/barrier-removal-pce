/*
 * Copyright (c) 2019 Aman Nougrahiya, V Krishna Nandivada, IIT Madras.
 * This file is a part of the project IMOP, licensed under the MIT license.
 * See LICENSE.md for the full text of the license.
 * 
 * The above notice shall be included in all copies or substantial
 * portions of this file.
 */
package elasticBarrier;

import java.util.ArrayList;
import java.util.List;

import imop.ast.annotation.Label;
import imop.ast.annotation.SimpleLabel;
import imop.ast.node.external.*;
import imop.ast.node.internal.*;
import imop.baseVisitor.cfgTraversals.GJDepthFirstCFG;
import imop.baseVisitor.cfgTraversals.GJNoArguDepthFirstCFG;
import imop.lib.analysis.flowanalysis.Cell;
import imop.lib.analysis.flowanalysis.Symbol;
import imop.lib.cfg.CFGLinkFinder;
import imop.lib.cfg.link.node.CompoundElementLink;

/**
 * Provides default methods for processing all the CFG nodes.
 * Traversal shall be defined as per the requirements.
 * Note: This visitor is just for convenience in handling all the CFGs.
 */
public class ElasticStructurePrinter extends GJDepthFirstCFG<String, Integer> {

	/**
	 * Given a newline and "tab" number of tabs before "s", this method returns the
	 * resulting string.
	 * 
	 * @param tab
	 * @param s
	 * 
	 * @return
	 */
	private String tn(int tab, String s) {
		String ret = "\n";
		for (int i = 0; i < tab; i++) {
			ret += "  ";
		}
		ret += s;
		return ret;
	}

	@Override
	public String initProcess(Node n, Integer tab) {
		return "";
	}

	@Override
	public String endProcess(Node n, Integer tab) {
		return "";
	}

	/**
	 * @deprecated
	 * @param n
	 */
	@Deprecated
	public String processCalls(Node n, Integer tab) {
		return "";
	}

	/**
	 * Special Node
	 */
	@Override
	public String visit(BeginNode n, Integer tab) {
		return "";
	}

	/**
	 * Special Node
	 */
	@Override
	public String visit(EndNode n, Integer tab) {
		return "";
	}

	/**
	 * f0 ::= ( DeclarationSpecifiers() )?
	 * f1 ::= Declarator()
	 * f2 ::= ( DeclarationList() )?
	 * f3 ::= CompoundStatement()
	 */
	@Override
	public String visit(FunctionDefinition n, Integer tab) {
		String bodyStr = n.getInfo().getCFGInfo().getBody().accept(this, tab);
		if (bodyStr.trim().equals("S")) {
			return tn(tab, "S");
		}
		String ret;
		ret = tn(tab, n.getInfo().getFunctionName());
		ret += "() " + bodyStr;
		return ret;
	}

	/**
	 * f0 ::= DeclarationSpecifiers()
	 * f1 ::= ( InitDeclaratorList() )?
	 * f2 ::= ";"
	 */
	@Override
	public String visit(Declaration n, Integer tab) {
		return tn(tab, "S");
	}

	/**
	 * f0 ::= DeclarationSpecifiers()
	 * f1 ::= ParameterAbstraction()
	 */
	@Override
	public String visit(ParameterDeclaration n, Integer tab) {
		assert (false);
		return "S";
	}

	/**
	 * f0 ::= "#"
	 * f1 ::= <UNKNOWN_CPP>
	 */
	@Override
	public String visit(UnknownCpp n, Integer tab) {
		return tn(tab, "S");
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= ParallelDirective()
	 * f2 ::= Statement()
	 */
	@Override
	public String visit(ParallelConstruct n, Integer tab) {
		String ret = tn(tab, "ParCons ");
		ret += n.getInfo().getCFGInfo().getBody().accept(this, tab);
		return ret;
	}

	/**
	 * f0 ::= "#"
	 * f1 ::= <PRAGMA>
	 * f2 ::= <UNKNOWN_CPP>
	 */
	@Override
	public String visit(UnknownPragma n, Integer tab) {
		return tn(tab, "S");
	}

	/**
	 * f0 ::= <IF>
	 * f1 ::= "("
	 * f2 ::= Expression()
	 * f3 ::= ")"
	 */
	@Override
	public String visit(IfClause n, Integer tab) {
		assert (false);
		return "S";
	}

	/**
	 * f0 ::= <NUM_THREADS>
	 * f1 ::= "("
	 * f2 ::= Expression()
	 * f3 ::= ")"
	 */
	@Override
	public String visit(NumThreadsClause n, Integer tab) {
		assert (false);
		return "S";
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= ForDirective()
	 * f2 ::= OmpForHeader()
	 * f3 ::= Statement()
	 */
	@Override
	public String visit(ForConstruct n, Integer tab) {
		// Check if this is a WLLoop.
		String labName = "";
		CompoundElementLink cel = (CompoundElementLink) CFGLinkFinder.getCFGLinkFor(n);
		if (cel.getIndex() != 0) {
			Node elem = ((CompoundStatement) cel.getEnclosingNode()).getInfo().getCFGInfo().getElementList()
					.get(cel.getIndex() - 1);
			if (elem instanceof Statement) {
				Statement elemStmt = (Statement) elem;
				for (Label lab : elemStmt.getInfo().getLabelAnnotations()) {
					if (lab instanceof SimpleLabel) {
						SimpleLabel sl = (SimpleLabel) lab;
						if (sl.getLabelName().contains("WLLoop")) {
							labName = sl.getLabelName();
							break;
						}
					}
				}
			}
		}
		
		
		String ret;
		if (!labName.isBlank()) {
			ret = tn(tab, labName + ": ForCons ");
		} else {
			ret = tn(tab, "ForCons ");
		}
		
		ret += n.getInfo().getCFGInfo().getBody().accept(this, tab);
		return ret;
	}

	/**
	 * f0 ::= <IDENTIFIER>
	 * f1 ::= "="
	 * f2 ::= Expression()
	 */
	@Override
	public String visit(OmpForInitExpression n, Integer tab) {
		assert (false);
		return "S";
	}

	/**
	 * f0 ::= OmpForLTCondition()
	 * | OmpForLECondition()
	 * | OmpForGTCondition()
	 * | OmpForGECondition()
	 */
	@Override
	public String visit(OmpForCondition n, Integer tab) {
		assert (false);
		return "S";
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
	public String visit(OmpForReinitExpression n, Integer tab) {
		assert (false);
		return "S";
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= <SECTIONS>
	 * f2 ::= NowaitDataClauseList()
	 * f3 ::= OmpEol()
	 * f4 ::= SectionsScope()
	 */
	@Override
	public String visit(SectionsConstruct n, Integer tab) {
		String ret = tn(tab, "Sections ");
		ret += n.getInfo().getCFGInfo().getBody().accept(this, tab);
		return ret;
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= <SINGLE>
	 * f2 ::= SingleClauseList()
	 * f3 ::= OmpEol()
	 * f4 ::= Statement()
	 */
	@Override
	public String visit(SingleConstruct n, Integer tab) {
		String ret = tn(tab, "Single ");
		ret += n.getInfo().getCFGInfo().getBody().accept(this, tab);
		return ret;
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= <TASK>
	 * f2 ::= ( TaskClause() )*
	 * f3 ::= OmpEol()
	 * f4 ::= Statement()
	 */
	@Override
	public String visit(TaskConstruct n, Integer tab) {
		String ret = tn(tab, "Task ");
		ret += n.getInfo().getCFGInfo().getBody().accept(this, tab);
		return ret;
	}

	/**
	 * f0 ::= <FINAL>
	 * f1 ::= "("
	 * f2 ::= Expression()
	 * f3 ::= ")"
	 */
	@Override
	public String visit(FinalClause n, Integer tab) {
		assert (false);
		return "S";
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
	public String visit(ParallelForConstruct n, Integer tab) {
		assert (false);
		return "S";
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
	public String visit(ParallelSectionsConstruct n, Integer tab) {
		assert (false);
		return "S";
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= <MASTER>
	 * f2 ::= OmpEol()
	 * f3 ::= Statement()
	 */
	@Override
	public String visit(MasterConstruct n, Integer tab) {
		String ret = tn(tab, "Master ");
		ret += n.getInfo().getCFGInfo().getBody().accept(this, tab);
		return ret;
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= <CRITICAL>
	 * f2 ::= ( RegionPhrase() )?
	 * f3 ::= OmpEol()
	 * f4 ::= Statement()
	 */
	@Override
	public String visit(CriticalConstruct n, Integer tab) {
		String ret = tn(tab, "Critical ");
		ret += n.getInfo().getCFGInfo().getBody().accept(this, tab);
		return ret;
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= <ATOMIC>
	 * f2 ::= ( AtomicClause() )?
	 * f3 ::= OmpEol()
	 * f4 ::= ExpressionStatement()
	 */
	@Override
	public String visit(AtomicConstruct n, Integer tab) {
		return tn(tab, "atomic");
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= <FLUSH>
	 * f2 ::= ( FlushVars() )?
	 * f3 ::= OmpEol()
	 */
	@Override
	public String visit(FlushDirective n, Integer tab) {
		return tn(tab, "S");
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= <ORDERED>
	 * f2 ::= OmpEol()
	 * f3 ::= Statement()
	 */
	@Override
	public String visit(OrderedConstruct n, Integer tab) {
		String ret = tn(tab, "Ordered ");
		ret += n.getInfo().getCFGInfo().getBody().accept(this, tab);
		return ret;
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= <BARRIER>
	 * f2 ::= OmpEol()
	 */
	@Override
	public String visit(BarrierDirective n, Integer tab) {
		return tn(tab, "== barrier ==");
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= <TASKWAIT>
	 * f2 ::= OmpEol()
	 */
	@Override
	public String visit(TaskwaitDirective n, Integer tab) {
		return tn(tab, "S");
	}

	/**
	 * f0 ::= OmpPragma()
	 * f1 ::= <TASKYIELD>
	 * f2 ::= OmpEol()
	 */
	@Override
	public String visit(TaskyieldDirective n, Integer tab) {
		return tn(tab, "S");
	}

	/**
	 * f0 ::= ( Expression() )?
	 * f1 ::= ";"
	 */
	@Override
	public String visit(ExpressionStatement n, Integer tab) {
		return tn(tab, "S");
	}

	/**
	 * f0 ::= "{"
	 * f1 ::= ( CompoundStatementElement() )*
	 * f2 ::= "}"
	 */
	@Override
	public String visit(CompoundStatement n, Integer tab) {
		List<String> elemList = new ArrayList<>();
		boolean foundNonS = false;
		for (Node elem : n.getInfo().getCFGInfo().getElementList()) {
			String elemStr = elem.accept(this, tab + 1);
			elemList.add(elemStr);
			if (!elemStr.trim().equals("S") && !elemStr.trim().isBlank()) {
				foundNonS = true;
			}
		}

		if (!foundNonS) {
			if (CFGLinkFinder.getCFGLinkFor(n) instanceof CompoundElementLink) {
				return tn(tab, "S");
			} else {
				return "S";
			}
		}

		String ret;
		if (CFGLinkFinder.getCFGLinkFor(n) instanceof CompoundElementLink) {
			ret = tn(tab, "{");
		} else {
			ret = "{";
		}

		for (int i = 0; i < elemList.size(); i++) {
			String elemStr = elemList.get(i);
			ret += elemStr;
			if (elemStr.trim().equals("S")) {
				for (int j = i + 1; j < elemList.size(); j++) {
					if (!elemList.get(j).trim().equals("S") && !elemList.get(j).isBlank()) {
						break;
					}
					i = j;
				}
			}
		}
		ret += tn(tab, "}");
		return ret;
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
	public String visit(IfStatement n, Integer tab) {
		boolean foundNonS = false;
		String sThen = n.getInfo().getCFGInfo().getThenBody().accept(this, tab);
		if (!sThen.trim().equals("S") && !sThen.trim().isBlank()) {
			foundNonS = true;
		}
		String sElse = "";
		if (n.getInfo().getCFGInfo().hasElseBody()) {
			sElse = n.getInfo().getCFGInfo().getElseBody().accept(this, tab);
			if (!sElse.trim().equals("S") && !sElse.trim().isBlank()) {
				foundNonS = true;
			}
		}

		if (!foundNonS) {
			return tn(tab, "S");
		}

		String ret = tn(tab, "If ") + sThen;
		if (n.getInfo().getCFGInfo().hasElseBody()) {
			ret += " Else " + sElse;
		}
		return ret;
	}

	/**
	 * f0 ::= <SWITCH>
	 * f1 ::= "("
	 * f2 ::= Expression()
	 * f3 ::= ")"
	 * f4 ::= Statement()
	 */
	@Override
	public String visit(SwitchStatement n, Integer tab) {
		String bodyStr = n.getInfo().getCFGInfo().getBody().accept(this, tab);
		if (bodyStr.trim().equals("S") || bodyStr.trim().isBlank()) {
			return tn(tab, "S");
		}
		return tn(tab, "Switch ") + bodyStr;
	}

	/**
	 * f0 ::= <WHILE>
	 * f1 ::= "("
	 * f2 ::= Expression()
	 * f3 ::= ")"
	 * f4 ::= Statement()
	 */
	@Override
	public String visit(WhileStatement n, Integer tab) {
		String body = n.getInfo().getCFGInfo().getBody().accept(this, tab);
		if (body.trim().equals("S") || body.trim().isBlank()) {
			return tn(tab, "S");
		}
		String ret = tn(tab, "While ");
		ret += body;
		return ret;
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
	public String visit(DoStatement n, Integer tab) {
		String bodyStr = n.getInfo().getCFGInfo().getBody().accept(this, tab);
		if (bodyStr.trim().equals("S") || bodyStr.trim().isBlank()) {
			return tn(tab, "S");
		}
		return tn(tab, "DoWhile ") + bodyStr;
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
	public String visit(ForStatement n, Integer tab) {
		// Check if this was a start-end loop.
		String labName = "";
		for (Label lab : n.getInfo().getLabelAnnotations()) {
			if (lab instanceof SimpleLabel) {
				SimpleLabel sl = (SimpleLabel) lab;
				if (sl.getLabelName().contains("StartEndLoop")) {
					labName = sl.getLabelName();
					break;
				}
			}
		}
		String bodyStr = n.getInfo().getCFGInfo().getBody().accept(this, tab);
		if (bodyStr.trim().equals("S") || bodyStr.trim().isBlank()) {
			if (!labName.isEmpty()) {
				return tn(tab, labName + ": S");
			} else {
				return tn(tab, "S");
			}
		}
		String forStr;
		if (!labName.isEmpty()) {
			forStr = tn(tab, labName + ": For ") + bodyStr;
		} else { 
			forStr = tn(tab, "For ") + bodyStr;
		}
		return forStr;
	}

	/**
	 * f0 ::= <GOTO>
	 * f1 ::= <IDENTIFIER>
	 * f2 ::= ";"
	 */
	@Override
	public String visit(GotoStatement n, Integer tab) {
		return tn(tab, "S");
	}

	/**
	 * f0 ::= <CONTINUE>
	 * f1 ::= ";"
	 */
	@Override
	public String visit(ContinueStatement n, Integer tab) {
		return tn(tab, "S");
	}

	/**
	 * f0 ::= <BREAK>
	 * f1 ::= ";"
	 */
	@Override
	public String visit(BreakStatement n, Integer tab) {
		return tn(tab, "S");
	}

	/**
	 * f0 ::= <RETURN>
	 * f1 ::= ( Expression() )?
	 * f2 ::= ";"
	 */
	@Override
	public String visit(ReturnStatement n, Integer tab) {
		return tn(tab, "S");
	}

	/**
	 * f0 ::= AssignmentExpression()
	 * f1 ::= ( "," AssignmentExpression() )*
	 */
	@Override
	public String visit(Expression n, Integer tab) {
		assert (false);
		return "S";
	}

	@Override
	public String visit(DummyFlushDirective n, Integer tab) {
		return tn(tab, "S");
	}

	@Override
	public String visit(PreCallNode n, Integer tab) {
		assert (false);
		return "S";
	}

	@Override
	public String visit(PostCallNode n, Integer tab) {
		assert (false);
		return "S";
	}

	@Override
	public String visit(CallStatement n, Integer tab) {
		List<FunctionDefinition> fdList = n.getInfo().getCalledDefinitions();
		if (fdList.isEmpty()) {
			return tn(tab, "S");
		}
		assert (fdList.size() == 1);
		FunctionDefinition fd = fdList.get(0);
		String fBody = fd.accept(this, 0);
		if (fBody.trim().equals("S") || fBody.trim().isBlank()) {
			return tn(tab, "S");
		}
		Cell c = n.getInfo().getFunctionDesignator();
		if (c instanceof Symbol) {
			return tn(tab, ((Symbol) c).getName() + "();");
		}
		return "";
	}

}
