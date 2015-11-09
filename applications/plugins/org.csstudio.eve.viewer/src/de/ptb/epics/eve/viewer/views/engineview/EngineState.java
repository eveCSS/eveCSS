package de.ptb.epics.eve.viewer.views.engineview;

/**
 * Represents the engine state by providing button availability.
 * 
 * @author Marcus Michalsky
 * @since 1.25
 */
public interface EngineState {
	
	/**
	 * Returns whether the play button is enabled
	 * @return <code>true</code> if the play button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	public boolean isPlay();
	
	/**
	 * Returns whether the pause button is enabled
	 * @return <code>true</code> if the pause button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	public boolean isPause();
	
	/**
	 * Returns whether the stop button is enabled
	 * @return <code>true</code> if the stop button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	public boolean isStop();
	
	/**
	 * Returns whether the skip button is enabled
	 * @return <code>true</code> if the skip button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	public boolean isSkip();
	
	/**
	 * Returns whether the halt button is enabled
	 * @return <code>true</code> if the halt button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	public boolean isHalt();
	
	/**
	 * Returns whether the trigger button is enabled
	 * @return <code>true</code> if the trigger button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	public boolean isTrigger();
}