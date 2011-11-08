package de.ptb.epics.eve.viewer.views.devicesview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;

/**
 * <code>TreeViewerFilterClasses</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class TreeViewerFilterClasses extends ViewerFilter {

	private boolean motorToggle;
	private boolean detectorToggle;
	private boolean deviceToggle;
	
	/**
	 * Constructs a ViewerFilter for Classes.
	 * 
	 * @param motorToggle the current state of the motor filter
	 * @param detectorToggle the current state of the detector filter
	 * @param deviceToggle the current state of the device filter
	 */
	public TreeViewerFilterClasses(boolean motorToggle, 
									boolean detectorToggle, 
									boolean deviceToggle) {
		this.motorToggle = motorToggle;
		this.detectorToggle = detectorToggle;
		this.deviceToggle = deviceToggle;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(!(element instanceof String)) {
			return true;
		}
		Object[] children = ((ITreeContentProvider)((TreeViewer)viewer).
					getContentProvider()).getChildren(element);
		for(Object o : children) {
			if (!motorToggle && 
				(o instanceof Motor || o instanceof MotorAxis)) {
					return true;
			}
			if (!detectorToggle && 
				(o instanceof Detector || o instanceof DetectorChannel)) {
					return true;
			}
			if (!deviceToggle && o instanceof Device) {
				return true;
			}
		}
		return false;
	}
}