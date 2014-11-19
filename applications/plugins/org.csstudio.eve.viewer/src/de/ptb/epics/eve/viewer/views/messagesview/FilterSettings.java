package de.ptb.epics.eve.viewer.views.messagesview;

import org.apache.log4j.Logger;

/**
 * @author Marcus Michalsky
 * @since 1.21
 */
public class FilterSettings {
	private boolean showEngineMessages;
	private boolean showViewerMessages;
	
	private Levels messageThreshold;

	/**
	 * Constructs new filter settings with default values.
	 */
	public FilterSettings() {
		this.showEngineMessages = true;
		this.showViewerMessages = true;
		
		if (Logger.getLogger(FilterSettings.class.getName()).isDebugEnabled()) {
			this.messageThreshold = Levels.DEBUG;
		} else {
			this.messageThreshold = Levels.MINOR;
		}
	}
	
	/**
	 * @return the showEngineMessages
	 */
	public boolean isShowEngineMessages() {
		return showEngineMessages;
	}

	/**
	 * @param showEngineMessages the showEngineMessages to set
	 */
	public void setShowEngineMessages(boolean showEngineMessages) {
		this.showEngineMessages = showEngineMessages;
	}

	/**
	 * @return the showViewerMessages
	 */
	public boolean isShowViewerMessages() {
		return showViewerMessages;
	}

	/**
	 * @param showViewerMessages the showViewerMessages to set
	 */
	public void setShowViewerMessages(boolean showViewerMessages) {
		this.showViewerMessages = showViewerMessages;
	}

	/**
	 * @return the messageThreshold
	 */
	public Levels getMessageThreshold() {
		return messageThreshold;
	}

	/**
	 * @param messageThreshold the messageThreshold to set
	 */
	public void setMessageThreshold(Levels messageThreshold) {
		this.messageThreshold = messageThreshold;
	}
}