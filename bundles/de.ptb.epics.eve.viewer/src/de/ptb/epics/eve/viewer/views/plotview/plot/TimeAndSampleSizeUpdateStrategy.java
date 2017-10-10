package de.ptb.epics.eve.viewer.views.plotview.plot;


/**
 * @author Marcus Michalsky
 * @since 1.13
 */
public class TimeAndSampleSizeUpdateStrategy implements UpdateStrategy {

	private UpdateStrategy timeUpdateStrategy;
	private UpdateStrategy sampleSizeUpdateStrategy;
	
	/**
	 * 
	 * @param delay
	 */
	public TimeAndSampleSizeUpdateStrategy(long delay) {
		this.timeUpdateStrategy = new TimeUpdateStrategy(delay);
		this.sampleSizeUpdateStrategy = new SampleSizeUpdateStrategy();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean update(long timeOfLastSample, int sampleSizeOfLastUpdate,
			int sampleSize) {
		return this.timeUpdateStrategy.update(timeOfLastSample,
				sampleSizeOfLastUpdate, sampleSize)
				|| this.sampleSizeUpdateStrategy.update(timeOfLastSample,
						sampleSizeOfLastUpdate, sampleSize);
	}
}