package elasticBarrier;

import java.util.List;

import imop.ast.node.external.BarrierDirective;
import imop.ast.node.external.ForConstruct;
import imop.ast.node.external.ForStatement;
import imop.ast.node.external.Node;

public class ElasticPattern {
	BarrierDirective barr1;
	List<Node> statementList1;
	ForStatement startEndLoop1;
	List<Node> statementList2;
	ForConstruct wlLoop2;
	List<Node> statementList3;
	BarrierDirective barr2;
	List<Node> statementList4;
	ForStatement startEndLoop2;

	@Override
	public String toString() {
		return "ElasticPattern [barr1=" + barr1 + ", statementList1=" + statementList1 + ", startEndLoop1="
				+ startEndLoop1 + ", statementList2=" + statementList2 + ", wlLoop2=" + wlLoop2 + ", statementList3="
				+ statementList3 + ", barr2=" + barr2 + ", statementList4=" + statementList4 + ", startEndLoop2="
				+ startEndLoop2 + "]";
	}

	public ElasticPattern(BarrierDirective barr1, List<Node> statementList1, ForStatement startEndLoop1,
			List<Node> statementList2, ForConstruct wlLoop2, List<Node> statementList3, BarrierDirective barr2,
			List<Node> statementList4, ForStatement startEndLoop2) {
		this.barr1 = barr1;
		this.statementList1 = statementList1;
		this.startEndLoop1 = startEndLoop1;
		this.statementList2 = statementList2;
		this.wlLoop2 = wlLoop2;
		this.statementList3 = statementList3;
		this.barr2 = barr2;
		this.statementList4 = statementList4;
		this.startEndLoop2 = startEndLoop2;
	}

}
