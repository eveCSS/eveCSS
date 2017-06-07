package de.ptb.epics.eve.viewer.views.messagesview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.util.jface.ViewerComparator;

/**
 * @author Marcus Michalsky
 * @since 1.21
 */
public class TimeViewerComparator extends ViewerComparator {
	private static final Logger LOGGER = Logger
			.getLogger(TimeViewerComparator.class.getName());
	
	@Override
	public int category(Object element) {
		// TODO Auto-generated method stub
		return 0; //super.category(element);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		ViewerMessage m1 = (ViewerMessage) e1;
		ViewerMessage m2 = (ViewerMessage) e2;
		LOGGER.debug(m1.getMessage() + " (" + m1.getTimeStamp().nsec() + ") - " + 
 m2.getMessage() + " (" + m2.getTimeStamp().nsec() + ") -> "
				+ m1.compareTo(m2));
		return -1 * m1.compareTo(m2);
	}
}