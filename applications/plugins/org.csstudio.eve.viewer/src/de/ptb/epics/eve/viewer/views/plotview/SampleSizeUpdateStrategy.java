package de.ptb.epics.eve.viewer.views.plotview;

/**
 * @author Marcus Michalsky
 * @since 1.13
 */
public class SampleSizeUpdateStrategy implements UpdateStrategy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean update(long timeOfLastSample, int sampleSizeOfLastUpdate,
			int sampleSize) {
		/*
		 * /----- nacheinander in den Plot geschrieben. /
		 * /----- ab 500 Punkten werden je 10 Punkte geplottet /
		 * /----- ab 2500 Punkten werden je 50 Punkte geplottet /
		 * /----- ab 5000 Punkten werden je 100 Punkte geplottet /
		 * /----- ab 12500 Punkten werden je 250 Punkte geplottet /
		 */
		if (sampleSizeOfLastUpdate >= 12500) {
			return (sampleSize -12500) % 250 == 0;
		} else if (sampleSizeOfLastUpdate >= 5000) {
			return (sampleSize -5000) % 100 == 0;
		} else if (sampleSizeOfLastUpdate >= 2500) {
			return (sampleSize -2500) % 50 == 0;
		} else if (sampleSizeOfLastUpdate >= 500) {
			return (sampleSize -500) % 10 == 0;
		}
		return false;
	}
}