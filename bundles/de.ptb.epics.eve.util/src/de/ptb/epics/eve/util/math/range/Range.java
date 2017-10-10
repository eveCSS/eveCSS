package de.ptb.epics.eve.util.math.range;

import java.util.List;

/**
 * A range is defined by a starting value (from), an optional stepwidth (step) and 
 * a stop value (to).
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
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (T value : this.getValues()) {
			buffer.append(value + ", ");
		}
		return buffer.substring(0, buffer.length()-2);
	}
}