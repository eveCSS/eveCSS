package de.ptb.epics.eve.viewer.views.messagesview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.Viewer;

/**
 * LevelFilter filters messages according to their severity.
 * Set filter settings define whether messages with a certain severity 
 * (or above) are shown.
 * 
 * @author Marcus Michalsky
 * @since 1.21
 */
public class LevelFilter extends MessageFilter {
	private static final Logger LOGGER = Logger.getLogger(LevelFilter.class
			.getName());
	
	/**
	 * Constructor.
	 * 
	 * @param filterSettings the filter settings
	 */
	public LevelFilter(FilterSettings filterSettings) {
		super(filterSettings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		ViewerMessage message = (ViewerMessage)element;
		if (this.getFilterSettings().getMessageThreshold()
				.compareTo(message.getMessageType()) > 0) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Message Level (" + message.getMessageType().
						toString() + ") < Filter Threshold (" + this.
						getFilterSettings().getMessageThreshold().toString() + 
						") -> hide");
			}
			return false;
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Message Level (" + message.getMessageType().
					toString() + ") >= Filter Threshold (" + this.
					getFilterSettings().getMessageThreshold().toString() + 
					") -> show");
		}
		return true;
	}
}