package de.ptb.epics.eve.viewer.messages;

/**
 * <code>IMessagesContainerUpdateProvider</code> should be implemented by 
 * {@link de.ptb.epics.eve.viewer.messages.MessagesContainer} which need the 
 * ability to inform interested parties about changes (e.g. incoming messages).
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public interface IMessagesContainerUpdateProvider {
	
	/**
	 * Adds a listener to the update provider which will be informed when a 
	 * message arrived.
	 * 
	 * @param listener the 
	 * {@link de.ptb.epics.eve.viewer.messages.IMessagesContainerUpdateListener}
	 * that should be added
	 */
	public void addMessagesContainerUpdateListener( 
			final IMessagesContainerUpdateListener listener);
	
	/**
	 * Removes an Update Listener form the message container update provider.
	 * 
	 * @param listener the 
	 * {@link de.ptb.epics.eve.viewer.messages.IMessagesContainerUpdateListener}
	 * that should be removed
	 */
	public void removeMessagesContainerUpdateListener(
			final IMessagesContainerUpdateListener listener);
}
