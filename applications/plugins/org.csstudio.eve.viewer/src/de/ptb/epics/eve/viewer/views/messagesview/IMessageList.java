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
	void add(ViewerMessage message);

	/**
	 * Returns the list of messages.
	 * 
	 * @return the list of messages
	 */
	ObservableList getList();

	/**
	 * Clears the list.
	 */
	void clear();

	/**
	 * Returns the maximum width source of the list.
	 * @return the maximum width source of the list
	 * @since 1.22
	 */
	String getMessageString();

	/**
	 * Returns the maximum width message of the list.
	 * @return the maximum width message of the list
	 * @since 1.22
	 */
	String getSourceString();
	
	/**
	 * @see 
	 */
	void addPropertyChangeListener(String property, PropertyChangeListener listener);

	/**
	 * @see 
	 */
	void removePropertyChangeListener(String property, PropertyChangeListener listener);
}