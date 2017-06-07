package de.ptb.epics.eve.viewer.views.messagesview;

import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.viewer.MessageSource;

/**
 * SourceFilter filters messages according to the source they originated.
 * Set filter settings define whether messages from a certain source are 
 * shown.
 * 
 * @author Marcus Michalsky
 * @since 1.21
 */
public class SourceFilter extends MessageFilter {
	
	/**
	 * Constructor.
	 * 
	 * @param filterSettings the filter settings
	 */
	public SourceFilter(FilterSettings filterSettings) {
		super(filterSettings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		ViewerMessage message = (ViewerMessage)element;
		if (message.getMessageSource().equals(MessageSource.VIEWER)) {
			if (!this.getFilterSettings().isShowViewerMessages()) {
				return false;
			}
		} else {
			if (!this.getFilterSettings().isShowEngineMessages()) {
				return false;
			}
		}
		return true;
	}
}