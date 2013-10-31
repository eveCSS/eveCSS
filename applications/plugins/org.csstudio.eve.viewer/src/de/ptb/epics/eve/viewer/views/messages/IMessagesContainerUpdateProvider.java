package de.ptb.epics.eve.viewer.views.messages;

/**
 * <code>IMessagesContainerUpdateProvider</code> should be implemented by 
 * {@link de.ptb.epics.eve.viewer.views.messages.MessagesContainer} which need the 
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
	 * {@link de.ptb.epics.eve.viewer.views.messages.IMessagesContainerUpdateListener}
	 * that should be added
	 */
	void addMessagesContainerUpdateListener( 
			final IMessagesContainerUpdateListener listener);
	
	/**
	 * Removes an Update Listener form the message container update provider.
	 * 
	 * @param listener the 
	 * {@link de.ptb.epics.eve.viewer.views.messages.IMessagesContainerUpdateListener}
	 * that should be removed
	 */
	void removeMessagesContainerUpdateListener(
			final IMessagesContainerUpdateListener listener);
}
