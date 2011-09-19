package de.ptb.epics.eve.viewer.messages;

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
	public void update();

	/**
	 * Gets called by the 
	 * {@link de.ptb.epics.eve.viewer.messages.IMessagesContainerUpdateProvider} 
	 * the implementing object is registered to when a new message arrived.
	 * 
	 * @param viewerMessage the newly arrived message
	 */
	public void addElement(ViewerMessage viewerMessage);
}