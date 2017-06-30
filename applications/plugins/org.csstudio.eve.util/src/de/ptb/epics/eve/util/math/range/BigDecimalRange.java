package de.ptb.epics.eve.util.math.range;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class BigDecimalRange extends Range<BigDecimal> {

	public BigDecimalRange(String expression) {
		if (expression.split("/").length == 1) {
			// expression is either j:i:k or j:k
			if (expression.split(":").length == 2) {
				// expressions is j:k
				String[] jk = expression.split(":");
				this.from = new BigDecimal(jk[0]);
				this.step = new BigDecimal("1.0");
				this.to = new BigDecimal(jk[1]);
			} else {
				// expression is j:i:k
				String[] jik = expression.split(":");
				this.from = new BigDecimal(jik[0]);
				this.step = new BigDecimal(jik[1]);
				this.to = new BigDecimal(jik[2]);
			}
		} else {
			// expression is j:k/N
			String[] jkN = expression.split("/");
			BigDecimal n = new BigDecimal(jkN[1]);
			String[] jk = jkN[0].split(":");
			this.from = new BigDecimal(jk[0]);
			this.to = new BigDecimal(jk[1]);
			this.step = to.subtract(from).divide(n);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BigDecimal> getValues() {
		List<BigDecimal> values = new ArrayList<>();
		values.add(from);
		BigDecimal d = from.plus();
		if (from.compareTo(to) == -1) {
			while (d.add(step).compareTo(to) < 1) {
				values.add(d.add(step));
				d = d.add(step);
			}
		} else if (from.compareTo(to) >= 0) {
			while (d.subtract(step).compareTo(to) > -1) {
				values.add(d.subtract(step));
				d = d.subtract(step);
			}
		}
		if (values.get(values.size()-1).compareTo(to) != 0) {
			values.add(to);
		}
		return values;
	}
}