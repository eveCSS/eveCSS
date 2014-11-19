package de.ptb.epics.eve.viewer.views.messagesview;

import java.beans.PropertyChangeListener;

import org.eclipse.core.databinding.observable.list.ObservableList;

import de.ptb.epics.eve.ecp1.client.interfaces.IErrorListener;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.21
 */
public interface IMessageList extends IErrorListener, PropertyChangeListener {

	/**
	 * Adds a message to the List.
	 * 
	 * @param message the message to add
	 */
	public abstract void add(ViewerMessage message);

	/**
	 * Returns the list of messages.
	 * 
	 * @return the list of messages
	 */
	public abstract ObservableList getList();

	/**
	 * Clears the list.
	 */
	public abstract void clear();

	/**
	 * @see 
	 */
	public abstract void addPropertyChangeListener(String property, 
	PropertyChangeListener listener);

	/**
	 * @see 
	 */
	public abstract void removePropertyChangeListener(String property,
			PropertyChangeListener listener);
}