package elasticBarrier;

import java.util.HashSet;
import java.util.Set;

import imop.lib.util.CellSet;

/**
 * Argument type to be used by CostExpression generator for the case where we
 * are struct-member sensitive.
 * 
 * @author kingpin
 *
 */
public class CostExpressionArgumentSensitive {
	CellSet mustWrites; // fsw
	Set<String> mustStructWrites; //fssw
	CellSet openReads; // opr
	Set<String> openStructReads; //opsr

	public CostExpressionArgumentSensitive() {
		mustWrites = new CellSet();
		mustStructWrites = new HashSet<>();
		openReads = new CellSet();
		openStructReads = new HashSet<>();
	}

	public CostExpressionArgumentSensitive(CostExpressionArgumentSensitive cea) {
		mustWrites = new CellSet(cea.mustWrites);
		mustStructWrites = new HashSet<>(cea.mustStructWrites);
		openReads = new CellSet(cea.openReads);
		openStructReads = new HashSet<>(cea.openStructReads);
	}
}
