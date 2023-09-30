package elasticBarrier;

import java.util.List;
import java.util.Set;

import imop.ast.node.external.Node;
import imop.ast.node.external.Statement;

public class CostExpressionReturn {
	List<Node> code;
	String costVarName;
	double costVal;
	Set<String> pendingDeclarationNames;

	public CostExpressionReturn(List<Node> code, String costVarName, Set<String> pendingDeclarationNames) {
		this.code = code;
		this.costVarName = costVarName;
		this.pendingDeclarationNames = pendingDeclarationNames;
	}

	public CostExpressionReturn(List<Node> code, double costVal, Set<String> pendingDeclarationNames) {
		this.code = code;
		this.costVal = costVal;
		this.pendingDeclarationNames = pendingDeclarationNames;
	}

	@Override
	public String toString() {
		return "CostExpressionReturn [code=" + code + ", costVarName=" + costVarName + "]";
	}
	
	public boolean isAVal() {
		return costVarName == null;
	}
	
	public boolean isAVar() {
		return costVarName != null; 
	}

}
