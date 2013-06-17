package de.ptb.epics.eve.viewer.views.plotview;

/**
 * @author Marcus Michalsky
 * @since 1.13
 */
public class AlwaysUpdateStrategy implements UpdateStrategy {

	/**
	 * {@inheritDoc}
	 * <p>
	 * Always returns true.
	 */
	@Override
	public boolean update(long timeOfLastSample, int sampleSizeOfLastUpdate,
			int sampleSize) {
		return true;
	}
}