package de.ptb.epics.eve.viewer.messages;

/**
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public interface IMessagesContainerUpdateListener {
	
	/**
	 *  update
	 */
	public void update();

	/**
	 * adds an element 
	 * 
	 * @param viewerMessage the viewer message
	 */
	public void addElement(ViewerMessage viewerMessage);
}