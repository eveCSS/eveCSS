package de.ptb.epics.eve.ecp1.client.interfaces;

public interface IPlayController {

	/**
	 * 
	 */
	void start();

	/**
	 * 
	 */
	void stop();

	/**
	 * 
	 */
	void pause();

	/**
	 * 
	 */
	void breakExecution();

	/**
	 * 
	 */
	void halt();

	/**
	 * 
	 */
	void shutdownEngine();

	/**
	 * 
	 * @param liveComment
	 */
	void addLiveComment(final String liveComment);
}