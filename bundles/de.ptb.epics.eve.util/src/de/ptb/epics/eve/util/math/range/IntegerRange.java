package de.ptb.epics.eve.util.math.range;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class IntegerRange extends Range<Integer> {
	public static final String INTEGER_REGEXP = "([+-]?[1-9]\\d*|0)";
	
	public static final String INTEGER_RANGE_REGEXP = 
			IntegerRange.INTEGER_REGEXP + 
			":" + 
			IntegerRange.INTEGER_REGEXP + 
			"(:" + 
			IntegerRange.INTEGER_REGEXP + 
			"|/" + 
			IntegerRange.INTEGER_REGEXP + 
			")?";
	
	public static final String INTEGER_RANGE_REPEATED_REGEXP =
			IntegerRange.INTEGER_RANGE_REGEXP + 
			"([\\s]*,[\\s]*" + 
			IntegerRange.INTEGER_RANGE_REGEXP +
			")*";
	
	/**
	 * Creates an integer range from from to to with stepwidth step
	 * @param from the start value
	 * @param step the stepwidth
	 * @param to the end value
	 * @throws IllegalArgumentException if to is less than from
	 */
	public IntegerRange(Integer from, Integer step, Integer to) {
		this.from = from;
		this.step = step;
		this.to = to;
	}
	
	/**
	 * Creates a new range with values parsed from the given expression 
	 * which could have the following form:
	 * 
	 * <ul>
	 *  <li><code>j:i:k</code> - with j=start, i=stepwidth, k=stop</li>
	 *  <li><code>j:k</code> - with j=start, k=stop and stepwidth=1</li>
	 *  <li><code>j:k/N</code> - with j=start, k=stop and N=number of elements with equal stepwidth
	 * </ul>
	 * 
	 * @param expression a formatted string (i.e. <code>j:i:k</code>, <code>j:k</code> or <code>j:k/N</code>)
	 */
	public IntegerRange(String expression) {
		if (expression.split("/").length == 1) {
			// expression is either j:i:k or j:k
			if (expression.split(":").length == 2) {
				// expressions is j:k
				String[] jk = expression.split(":");
				this.from = Integer.parseInt(jk[0]);
				this.step = 1;
				this.to = Integer.parseInt(jk[1]);
			} else {
				// expression is j:i:k
				String[] jik = expression.split(":");
				this.from = Integer.parseInt(jik[0]);
				this.step = Integer.parseInt(jik[1]);
				this.to = Integer.parseInt(jik[2]);
			}
		} else {
			// expression is j:k/N
			String[] jkN = expression.split("/");
			int n = Integer.parseInt(jkN[1]);
			String[] jk = jkN[0].split(":");
			this.from = Integer.parseInt(jk[0]);
			this.to = Integer.parseInt(jk[1]);
			this.step = (this.to - this.from) / n;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Integer> getValues() {
		List<Integer> values = new ArrayList<Integer>();
		int i = from;
		values.add(new Integer(i));
		if (from < to) {
			while (i + step <= to) {
				values.add(new Integer(i + step));
				i = i + step;
			}
		} else {
			while (i - step >= to) {
				values.add(new Integer(i - step));
				i = i - step;
			}
		}
		if (!values.get(values.size()-1).equals(to)) {
			values.add(to);
		}
		return values;
	}
}