package de.ptb.epics.eve.viewer.views.messagesview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.ecp1.client.model.Error;
import de.ptb.epics.eve.viewer.XMLDispatcher;

/**
 * @author Marcus Michalsky
 * @since 1.21
 */
public class MessageList implements IMessageList {
	public static final String SOURCE_MAX_WIDTH_PROP = "sourceMaxWidth";
	public static final String MESSAGE_MAX_WIDTH_PROP = "messageMaxWidth";
	
	private static final int SOURCE_MAX_WIDTH_DEFAULT = 0;
	private static final int MESSAGE_MAX_WIDTH_DEFAULT = 0;
	
	private static final Logger LOGGER = Logger.getLogger(MessageList.class
			.getName());
	
	private ObservableList messageList;
	
	private Realm realm;
	
	private int sourceMaxWidth;
	private int messageMaxWidth;
	private String sourceMaxWidthString;
	private String messageMaxWidthString;
	
	
	private PropertyChangeSupport propertyChangeSupport;
	
	/**
	 * Constructor.
	 * 
	 * @param realm the realm the add operation should be executed in
	 */
	public MessageList(Realm realm) {
		this.realm = realm;
		this.messageList = new WritableList(realm);
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.sourceMaxWidth = MessageList.SOURCE_MAX_WIDTH_DEFAULT;
		this.messageMaxWidth = MessageList.MESSAGE_MAX_WIDTH_DEFAULT;
		this.sourceMaxWidthString = "";
		this.messageMaxWidthString = "";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final ViewerMessage message) {
		final int newSourceWidth = message.getMessageSource().toString().length();
		final int newMessageWidth = message.getMessage().toString().length();
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("adding message: ["
					+ message.getMessageSource().toString() + "] -> "
					+ message.getMessage().toString());
		}
		
		this.realm.asyncExec(new Runnable() {
			@Override public void run() {
				messageList.add(message);
				
				if (newSourceWidth > sourceMaxWidth) {
					sourceMaxWidthString = message.getMessageSource().toString();
					propertyChangeSupport.firePropertyChange(
							MessageList.SOURCE_MAX_WIDTH_PROP, sourceMaxWidth,
							newSourceWidth);
					sourceMaxWidth = newSourceWidth;
				}
				
				if (newMessageWidth > messageMaxWidth) {
					messageMaxWidthString = message.getMessage();
					propertyChangeSupport.firePropertyChange(
							MessageList.MESSAGE_MAX_WIDTH_PROP, messageMaxWidth, 
							newMessageWidth);
					messageMaxWidth = newMessageWidth;
				}
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		this.realm.asyncExec(new Runnable() {
			@Override public void run() {
			messageList.clear();
			}
		});
		this.sourceMaxWidth = 0;
		this.messageMaxWidth = 0;
	}
	
	/**
	 * Returns the maximum width source of the list.
	 * @return the maximum width source of the list
	 * @since 1.22
	 */
	@Override
	public String getSourceString() {
		return this.sourceMaxWidthString;
	}
	
	/**
	 * Returns the maximum width message of the list.
	 * @return the maximum width message of the list
	 * @since 1.22
	 */
	@Override
	public String getMessageString() {
		return this.messageMaxWidthString;
	}
	
	/**
	 * {@ineritDoc}
	 */
	@Override
	public ObservableList getList() {
		return this.messageList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void errorOccured(final Error error) {
		this.add(new ViewerMessage(error));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(XMLDispatcher.DEVICE_DEFINITION_PROP)) {
			if (e.getNewValue() instanceof IMeasuringStation) {
				if (this.messageList.size() > 200) {
					this.realm.asyncExec(new Runnable() {
						
						/**
						 * {@inheritDoc}
						 */
						@Override
						public void run() {
							messageList.clear();
						}
					});
					LOGGER.debug(
							"new scan arrived & msg count > 200 -> clear all");
				} else {
					LOGGER.debug(
							"new scan arrived but lt 200 msgs -> ignore");
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(property, listener);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(property,
				listener);
	}
}