package de.ptb.epics.eve.viewer.views.messages;

/**
 * <code>IMessagesContainerUpdateListener</code> 
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public interface IMessagesContainerUpdateListener {
	
	/**
	 *  ? update ?
	 */
	void update();

	/**
	 * Gets called by the 
	 * {@link de.ptb.epics.eve.viewer.views.messages.IMessagesContainerUpdateProvider} 
	 * the implementing object is registered to when a new message arrived.
	 * 
	 * @param viewerMessage the newly arrived message
	 */
	void addElement(ViewerMessage viewerMessage);
}