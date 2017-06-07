package de.ptb.epics.eve.ecp1.helper.progresstracker;

/**
 * Represents a progress indicator by current and maximum position.
 * 
 * @author Marcus Michalsky
 * @since 1.28
 */
public class Progress {
	private static final int MAXIMUM_MIN_VALUE = 1;
	private Integer current;
	private Integer maximum;
	
	/**
	 * Constructs a new progress with maximum steps.
	 * @param maximum the amount of steps
	 * @throws IllegalArgumentException if maximum is less than one
	 */
	public Progress(int maximum) {
		if (maximum < Progress.MAXIMUM_MIN_VALUE) {
			throw new IllegalArgumentException("maximum must be at least 1.");
		}
		this.current = null;
		this.maximum = maximum;
	}

	/**
	 * Returns the current progress (amount of processed positions) or <code>null</code> if none available.
	 * 
	 * @return the current
	 */
	public Integer getCurrent() {
		return current;
	}

	/**
	 * @param current the current to set
	 */
	public void setCurrent(Integer current) {
		this.current = current;
	}

	/**
	 * @return the maximum
	 */
	public Integer getMaximum() {
		return maximum;
	}
}