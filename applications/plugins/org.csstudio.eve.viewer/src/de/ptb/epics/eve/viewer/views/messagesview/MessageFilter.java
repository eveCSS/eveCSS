package de.ptb.epics.eve.viewer.views.messagesview;

import org.eclipse.jface.viewers.ViewerFilter;

/**
 * @author Marcus Michalsky
 * @since 1.21
 */
public abstract class MessageFilter extends ViewerFilter {
	private final FilterSettings filterSettings;
	
	public MessageFilter(FilterSettings filterSettings) {
		this.filterSettings = filterSettings;
	}

	/**
	 * @return the filterSettings
	 */
	public FilterSettings getFilterSettings() {
		return filterSettings;
	}
}