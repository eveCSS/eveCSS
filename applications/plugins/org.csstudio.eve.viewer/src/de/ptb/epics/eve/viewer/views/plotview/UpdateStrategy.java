package de.ptb.epics.eve.viewer.views.plotview;

/**
 * @author Marcus Michalsky
 * @since 1.13
 */
public interface UpdateStrategy {
	
	/**
	 * 
	 * @param timeOfLastSample
	 * @param sampleSizeOfLastUpdate 
	 * @param sampleSize
	 * @return
	 */
	boolean update(long timeOfLastSample, int sampleSizeOfLastUpdate,
			int sampleSize);
}