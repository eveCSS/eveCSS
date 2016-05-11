package de.ptb.epics.eve.viewer.views.engineview;

/**
 * Represents the engine state by providing button availability.
 * 
 * @author Marcus Michalsky
 * @since 1.25
 */
public interface EngineState {
	
	/**
	 * Returns whether the start button is enabled
	 * @return <code>true</code> if the start button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	boolean isStart();
	
	/**
	 * Returns whether the kill button is enabled
	 * @return <code>true</code> if the kill button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	boolean isKill();
	
	/**
	 * Returns whether the connect button is enabled
	 * @return <code>true</code> if the connect button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	boolean isConnect();
	
	/**
	 * Returns whether the disconnect button is enabled
	 * @return <code>true</code> if the disconnect button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	boolean isDisconnect();
	
	/**
	 * Returns whether the play button is enabled
	 * @return <code>true</code> if the play button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	boolean isPlay();
	
	/**
	 * Returns whether the pause button is enabled
	 * @return <code>true</code> if the pause button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	boolean isPause();
	
	/**
	 * Returns whether the stop button is enabled
	 * @return <code>true</code> if the stop button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	boolean isStop();
	
	/**
	 * Returns whether the skip button is enabled
	 * @return <code>true</code> if the skip button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	boolean isSkip();
	
	/**
	 * Returns whether the halt button is enabled
	 * @return <code>true</code> if the halt button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	boolean isHalt();
	
	/**
	 * Returns whether the trigger button is enabled
	 * @return <code>true</code> if the trigger button is enabled, 
	 * 		<code>false</code> otherwise
	 */
	boolean isTrigger();
}