package de.ptb.epics.eve.util.math.range;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Marcus Michalsky
 * @since 1.28
 */
public class BigDecimalRange extends Range<BigDecimal> {
	private static final Logger LOGGER = Logger.getLogger(
			BigDecimalRange.class.getName());
	
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
			try {
				if (from.compareTo(to) < 0) {
					this.step = to.subtract(from).divide(n);
				} else {
					this.step = from.subtract(to).divide(n, RoundingMode.HALF_UP);
				}
			} catch(ArithmeticException e) {
				LOGGER.warn(e.getMessage() + ", rounding");
				if (from.compareTo(to) < 0) {
					this.step = to.subtract(from).divide(n, MathContext.DECIMAL64);
				} else {
					this.step = from.subtract(to).divide(n, MathContext.DECIMAL64);
				}
			}
		}
		if (this.isInfinite()) {
			this.negateStepwidth();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<BigDecimal> getValues() {
		List<BigDecimal> values = new ArrayList<>();
		if (step.compareTo(BigDecimal.ZERO) == 0) {
			if (from.compareTo(to) == 0) {
				values.add(from);
			} else {
				values.add(from);
				values.add(to);
			}
			return values;
		}
		values.add(from);
		BigDecimal d = from.plus();
		if (from.compareTo(to) < 0) {
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isInfinite() {
		return ((this.from.compareTo(this.to) < 0 && 
				this.from.add(this.step).compareTo(this.from) < 0)
			|| (this.from.compareTo(this.to) > 0 &&
				this.from.subtract(this.step).compareTo(this.from) > 0));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void negateStepwidth() {
		this.step = this.step.negate();
	}
}