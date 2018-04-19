package de.ptb.epics.eve.util.math.range;

import java.util.List;

/**
 * A range is defined by a starting value (from), an optional stepwidth (step) 
 * and a stop value (to).
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.28
 */
public abstract class Range<T extends Number> {
	protected T from;
	protected T step;
	protected T to;
	
	/**
	 * Returns a list containing all values of the range.
	 * @return a list containing all values of the range
	 */
	public abstract List<T> getValues();
	
	/**
	 * Returns whether the defined range is infinite. A range is infinite if 
	 * one of the following is true:
	 * <ul>
	 *   <li>from < to && (from + step) < from</li>
	 *   <li>from > to && (from - step) > from</li>
	 * </ul>
	 * @return <code>true</code> if the defined range is infinite, 
	 * <code>false</code> otherwise
	 * @since 1.29.4
	 */
	protected abstract boolean isInfinite();
	
	/**
	 * Negates step.
	 * 
	 * Should only be applied during construction of the object to maintain 
	 * its immutable state. Otherwise a once given evaluation of isInfinite 
	 * may become invalid.
	 * @since 1.29.4
	 */
	protected abstract void negateStepwidth();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		for (T value : this.getValues()) {
			buffer.append(value + ", ");
		}
		return buffer.substring(0, buffer.length()-2);
	}
}