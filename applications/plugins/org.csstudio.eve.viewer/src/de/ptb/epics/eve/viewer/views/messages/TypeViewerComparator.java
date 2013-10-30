package de.ptb.epics.eve.viewer.views.messages;

import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.util.jface.ViewerComparator;

/**
 * @author Marcus Michalsky
 * @since 1.4
 */
public class TypeViewerComparator extends ViewerComparator {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int category(Object element) {
		ViewerMessage msg = (ViewerMessage)element;
		switch(msg.getMessageType()) {
		case FATAL:
			return 0;
		case ERROR:
			return 1;
		case MINOR:
			return 2;
		case INFO:
			return 3;
		case DEBUG:
			return 4;
		case SYSTEM:
			return 5;
		}
		return super.category(element);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		// first level of sorting is category
		// if equal, sort by date (ViewerMessage compareTo)
		if (this.category(e1) != this.category(e2)) {
			return this.category(e1) - this.category(e2);
		}
		return ((ViewerMessage)e1).compareTo((ViewerMessage)e2);
	}
}