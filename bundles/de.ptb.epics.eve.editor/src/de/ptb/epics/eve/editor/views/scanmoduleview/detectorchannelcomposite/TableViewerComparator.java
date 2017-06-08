package de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite;

import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.util.ui.jface.ViewerComparator;

/**
 * @author Marcus Michalsky
 * @since 1.5
 */
public class TableViewerComparator extends ViewerComparator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		/*
		 * return direction * ((((Axis) e1).getMotorAxis().getName()).
				compareTo(((Axis) e2).getMotorAxis().getName()));
		 */
		return direction * ((Channel) e1).getDetectorChannel().getName()
				.compareTo(((Channel) e2).getDetectorChannel().getName());
	}
}