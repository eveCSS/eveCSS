package de.ptb.epics.eve.util.math.range;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class DoubleRange extends Range<Double> {
	public static final String DOUBLE_REGEXP = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?";
	
	public static final String DOUBLE_RANGE_REGEXP = 
			DoubleRange.DOUBLE_REGEXP + 
			":" +
			DoubleRange.DOUBLE_REGEXP + 
			"(:" + 
			DoubleRange.DOUBLE_REGEXP + 
			"|/" + 
			DoubleRange.DOUBLE_REGEXP + 
			")?";
	
	public static final String DOUBLE_RANGE_REPEATED_REGEXP = 
			DoubleRange.DOUBLE_RANGE_REGEXP + 
			"([\\s]*,[\\s]*" +
			DoubleRange.DOUBLE_RANGE_REGEXP +
			")*";
	
	/**
	 * Creates an integer range from from to to with stepwidth step
	 * @param from the start value
	 * @param step the stepwidth
	 * @param to the end value
	 * @throws IllegalArgumentException if to is less than from
	 */
	public DoubleRange(Double from, Double step, Double to) {
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
	public DoubleRange(String expression) {
		if (expression.split("/").length == 1) {
			// expression is either j:i:k or j:k
			if (expression.split(":").length == 2) {
				// expressions is j:k
				String[] jk = expression.split(":");
				this.from = Double.parseDouble(jk[0]);
				this.step = 1.0;
				this.to = Double.parseDouble(jk[1]);
			} else {
				// expression is j:i:k
				String[] jik = expression.split(":");
				this.from = Double.parseDouble(jik[0]);
				this.step = Double.parseDouble(jik[1]);
				this.to = Double.parseDouble(jik[2]);
				
				if (this.isInfinite()) {
					this.negateStepwidth();
				}
			}
		} else {
			// expression is j:k/N
			String[] jkN = expression.split("/");
			double n = Double.parseDouble(jkN[1]);
			String[] jk = jkN[0].split(":");
			this.from = Double.parseDouble(jk[0]);
			this.to = Double.parseDouble(jk[1]);
			this.step = (this.to - this.from) / n;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Double> getValues() {
		List<Double> values = new ArrayList<>();
		double d = from;
		values.add(d);
		if (from < to) {
			while (d + step <= to) {
				values.add(d + step);
				d = d + step;
			}
		} else {
			while (d - step >= to) {
				values.add(d - step);
				d = d - step;
			}
		}
		if (!values.get(values.size()-1).equals(to)) {
			values.add(to);
		}
		return values;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isInfinite() {
		return (((this.from < this.to) && (this.from + this.step < this.from)) 
			|| ((this.from > this.to) && (this.from - this.step > this.from))); 
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void negateStepwidth() {
		this.step = -this.step;
	}
}
