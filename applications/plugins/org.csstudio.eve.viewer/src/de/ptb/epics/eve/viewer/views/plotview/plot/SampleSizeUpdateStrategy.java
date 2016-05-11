package de.ptb.epics.eve.viewer.views.plotview.plot;


/**
 * SampleSizeUpdateStrategy decides whether to update upon the number of 
 * samples. A larger sample size results in a less frequent update to save 
 * resources.
 * 
 * @author Marcus Michalsky
 * @since 1.13
 */
public class SampleSizeUpdateStrategy implements UpdateStrategy {
	private static final int SAMPLE_SIZE_THRESHOLD_1 = 100;
	private static final int SAMPLE_SIZE_THRESHOLD_2 = 500;
	private static final int SAMPLE_SIZE_THRESHOLD_3 = 2500;
	private static final int SAMPLE_SIZE_THRESHOLD_4 = 5000;
	private static final int SAMPLE_SIZE_THRESHOLD_5 = 12500;
	
	private static final int SAMPLE_COUNT_1 = 5;
	private static final int SAMPLE_COUNT_2 = 10;
	private static final int SAMPLE_COUNT_3 = 50;
	private static final int SAMPLE_COUNT_4 = 100;
	private static final int SAMPLE_COUNT_5 = 250;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean update(long timeOfLastSample, int sampleSizeOfLastUpdate,
			int sampleSize) {
		/*
		 * /----- ab 100 Punkten werden je 5 Punkte geplottet /
		 * /----- ab 500 Punkten werden je 10 Punkte geplottet /
		 * /----- ab 2500 Punkten werden je 50 Punkte geplottet /
		 * /----- ab 5000 Punkten werden je 100 Punkte geplottet /
		 * /----- ab 12500 Punkten werden je 250 Punkte geplottet /
		 */
		if (sampleSizeOfLastUpdate >= SAMPLE_SIZE_THRESHOLD_5) {
			return (sampleSize - SAMPLE_SIZE_THRESHOLD_5) % SAMPLE_COUNT_5 == 0;
		} else if (sampleSizeOfLastUpdate >= SAMPLE_SIZE_THRESHOLD_4) {
			return (sampleSize - SAMPLE_SIZE_THRESHOLD_4) % SAMPLE_COUNT_4 == 0;
		} else if (sampleSizeOfLastUpdate >= SAMPLE_SIZE_THRESHOLD_3) {
			return (sampleSize - SAMPLE_SIZE_THRESHOLD_3) % SAMPLE_COUNT_3 == 0;
		} else if (sampleSizeOfLastUpdate >= SAMPLE_SIZE_THRESHOLD_2) {
			return (sampleSize - SAMPLE_SIZE_THRESHOLD_2) % SAMPLE_COUNT_2 == 0;
		} else if (sampleSizeOfLastUpdate >= SAMPLE_SIZE_THRESHOLD_1) {
			return (sampleSize - SAMPLE_SIZE_THRESHOLD_1) % SAMPLE_COUNT_1 == 0;
		}
		return true;
	}
}