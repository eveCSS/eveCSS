package de.ptb.epics.eve.viewer.views.plotview;

import java.util.Calendar;

/**
 * @author Marcus Michalsky
 * @since 1.13
 */
public class TimeUpdateStrategy implements UpdateStrategy {
	private long delay;
	
	/**
	 * 
	 * @param delay
	 */
	public TimeUpdateStrategy(long delay) {
		this.delay = delay;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean update(long timeOfLastSample, int sampleSizeOfLastUpdate,
			int sampleSize) {
		if (Calendar.getInstance().getTimeInMillis() - timeOfLastSample > delay) {
			return true;
		}
		return false;
	}
}