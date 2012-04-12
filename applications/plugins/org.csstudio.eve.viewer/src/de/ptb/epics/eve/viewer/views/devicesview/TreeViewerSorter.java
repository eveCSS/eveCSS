package de.ptb.epics.eve.viewer.views.devicesview;

import java.util.List;

import org.eclipse.jface.viewers.ViewerSorter;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;

/**
 * @author Marcus Michalsky
 * @since 1.2
 */
public class TreeViewerSorter extends ViewerSorter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int category(Object element) {
		if (element instanceof String) {
			return 0;
		} else if (element instanceof List<?>) {
			List<?> list = (List<?>) element;
			if (list.get(0) instanceof Motor
					|| list.get(0) instanceof MotorAxis) {
				return 1;
			} else if (list.get(0) instanceof Detector
					|| list.get(0) instanceof DetectorChannel) {
				return 2;
			} else if (list.get(0) instanceof Device) {
				return 3;
			}
		}
		return super.category(element);
	}
}