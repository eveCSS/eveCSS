package de.ptb.epics.eve.viewer.messages;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * <code>MessageContainer</code> contains a list of messages and functions as 
 * an update provider.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class MessagesContainer implements IMessagesContainerUpdateProvider {

	private final List<ViewerMessage> messages;
	private final List<IMessagesContainerUpdateListener> listener;
	
	/**
	 * Constructs a <code>MessagesContainer</code>.
	 */
	public MessagesContainer() {
		this.messages = new LinkedList<ViewerMessage>();
		this.listener = new ArrayList<IMessagesContainerUpdateListener>();
	}
	
	/**
	 * Adds a {@link de.ptb.epics.eve.viewer.messages.ViewerMessage} to the 
	 * message container.
	 * 
	 * @param viewerMessage the 
	 * 		  {@link de.ptb.epics.eve.viewer.messages.ViewerMessage}
	 */
	public void addMessage(final ViewerMessage viewerMessage) {
		this.messages.add(0, viewerMessage);
		final Iterator<IMessagesContainerUpdateListener> it = 
				this.listener.iterator();
		while(it.hasNext()) {
			it.next().addElement(viewerMessage);
		}
	}
	
	/**
	 * Clears all messages.
	 */
	public void clear() {
		this.messages.clear(); 
		final Iterator<IMessagesContainerUpdateListener> it = 
				this.listener.iterator();
		while(it.hasNext()) {
			it.next().update();
		}
	}
	
	/**
	 * Adds all contained messages to the given list.
	 * 
	 * @param target the list the viewer messages should be added to
	 */
	public void getList(final List<ViewerMessage> target) {
		target.clear();
		target.addAll(this.messages);
	}
	
	/**
	 * Returns a list of viewer messages.
	 *  
	 * @return a list of {@link de.ptb.epics.eve.viewer.messages.ViewerMessage}s
	 */
	public List<ViewerMessage> getList() {
		final List<ViewerMessage> target = new ArrayList<ViewerMessage>();
		this.getList(target);
		return target;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addMessagesContainerUpdateListener(
			final IMessagesContainerUpdateListener listener) {
		this.listener.add(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeMessagesContainerUpdateListener(
			final IMessagesContainerUpdateListener listener) {
		this.listener.remove(listener);		
	}
}