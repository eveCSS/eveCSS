package de.ptb.epics.eve.editor.dialogs.monitoroptions;

import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.editor.Activator;

/**
 * <code>DevicesViewTreeViewerLabelProvider</code> is the label provider of the 
 * tree viewer defined in 
 * {@link de.ptb.epics.eve.viewer.views.devicesview.DevicesView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class TreeViewerLabelProvider implements ILabelProvider {

	private final Image motorImage = 
			Activator.getDefault().getImageRegistry().get("MOTOR");
	private final Image axisImage = 
			Activator.getDefault().getImageRegistry().get("AXIS");
	private final Image detectorImage = 
			Activator.getDefault().getImageRegistry().get("DETECTOR");
	private final Image channelImage = 
			Activator.getDefault().getImageRegistry().get("CHANNEL");
	private final Image deviceImage = 
			Activator.getDefault().getImageRegistry().get("DEVICE");
	private final Image classImage = 
			Activator.getDefault().getImageRegistry().get("CLASS");
	private final Image devicesImage = 
			Activator.getDefault().getImageRegistry().get("DEVICES");
	private final Image motorsAxesImage = 
			Activator.getDefault().getImageRegistry().get("MOTORSAXES");
	private final Image detectorsChannelsImage = 
			Activator.getDefault().getImageRegistry().get("DETECTORSCHANNELS");
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage(final Object element) {
		if(element instanceof Motor) {
			return this.motorImage;
		} else if(element instanceof MotorAxis) {
			return this.axisImage;
		} else if(element instanceof Detector) {
			return this.detectorImage;
		} else if(element instanceof DetectorChannel) {
			return this.channelImage;
		} else if(element instanceof Device) {
			return this.deviceImage;
		} else if(element instanceof String) {
			return this.classImage;
		} else if(element instanceof List<?>) {
			if(!((List<?>) element).isEmpty()) {
				if (((List<?>) element).get(0) instanceof Motor ||
					((List<?>) element).get(0) instanceof MotorAxis) {
					return motorsAxesImage;
				} else if (((List<?>) element).get(0) instanceof Detector) {
					return detectorsChannelsImage;
				} else if(((List<?>) element).get(0) instanceof Device) {
					return devicesImage;
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String getText(final Object element) {
		if(element instanceof List<?>) {
			if(((List<Object>)element).size() == 0) {
				return null;
			}
			Object obj = ((List<Object>)element).get(0);
			if(obj instanceof Motor || obj instanceof MotorAxis) {
				return "Motors & Axes";
			} else if(obj instanceof Detector || obj instanceof DetectorChannel) {
				return "Detectors & Channels";
			} else if(obj instanceof Device) {
				return "Devices";
			}
		} else if(element instanceof String) {
				return (String)element;
		} else if(element instanceof AbstractDevice) {
			final AbstractDevice device = (AbstractDevice)element;
			String label = device.getName();
			if (label.length() == 0) {
				label = device.getID();
			}
			return label;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(final ILabelProviderListener listener) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(final ILabelProviderListener listener) {
	}
}