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
		// TODO Auto-generated method stub
		/*
		 * /----- nacheinander in den Plot geschrieben. /
		 * /----- ab 500 Punkten werden je 10 Punkte geplottet /
		 * /----- ab 2500 Punkten werden je 50 Punkte geplottet /
		 * /----- ab 5000 Punkten werden je 100 Punkte geplottet /
		 * /----- ab 12500 Punkten werden je 250 Punkte geplottet /
		 */
		return false;
	}
}