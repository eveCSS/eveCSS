package de.ptb.epics.eve.viewer.views.devicesview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.AbstractMainPhaseDevice;
import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * <code>DevicesViewTreeViewerContentProvider</code> is the content provider of 
 * the tree viewer defined in 
 * {@link de.ptb.epics.eve.viewer.views.devicesview.DevicesView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class TreeViewerContentProvider 
						implements ITreeContentProvider, IModelUpdateListener {

	// the input of the tree viewer
	private IMeasuringStation measuringStation;

	// a reference to the viewer this provider is assigned to
	private Viewer viewer;
	
	// lists of devices (by group) without class names
	private List<AbstractMainPhaseDevice> motorsAndAxesWithoutClassName;
	private List<AbstractMainPhaseDevice> detectorsAndChannelsWithoutClassName;
	private List<AbstractPrePostscanDevice> devicesWithoutClassName;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		/*
		 * if the input is a measuring station the tree viewer gets the 
		 * following root elements:
		 *  - all classes
		 *  - all motors without a class name and all axes without a class name 
		 *     (IF their motor has a non-empty class)
		 *  - all detectors without a class name and all channels without a 
		 *     class name (IF their detector has a non empty class)
		 *  - all devices without a class name
		 */
		
		if(!(inputElement instanceof IMeasuringStation)) {
			return null;
		}
		
		// input is actually a measuring station
		final IMeasuringStation measuringStation = 
				(IMeasuringStation)inputElement;
		
		// the list the root elements are added to
		List<Object> returnList = new ArrayList<Object>();
		
		// add all classes as elements
		if (!measuringStation.getClassNameList().isEmpty()) {
			returnList.addAll(measuringStation.getClassNameList());
		}
		
		if(!motorsAndAxesWithoutClassName.isEmpty()) {
			returnList.add(motorsAndAxesWithoutClassName);
		}
		if(!detectorsAndChannelsWithoutClassName.isEmpty()) {
			returnList.add(detectorsAndChannelsWithoutClassName);
		}
		if(!devicesWithoutClassName.isEmpty()) {
			returnList.add(devicesWithoutClassName);
		}
		
		return returnList.toArray();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof String) {
			// add (nearly) all devices of the given class (see below)...
			List<AbstractDevice> classDevices = measuringStation.
					getDeviceList((String)parentElement);
			List<AbstractDevice> returnList = new ArrayList<AbstractDevice>();
			for(AbstractDevice ad : classDevices) {
				if(ad instanceof Device) {
					returnList.add(ad);
					continue;
				}
				if(ad instanceof Motor || ad instanceof Detector) {
					returnList.add(ad);
					continue;
				}
				// ...for axes and channels we have to check if their parents 
				// are also in this class:
				// if they are they are not added hence they would occur twice
				if(!((AbstractMainPhaseDevice)ad).getClassName().equals(
					((AbstractMainPhaseDevice)(ad.getParent())).getClassName())) {
						returnList.add(ad);
				}
			}
			return returnList.toArray();
		} else if(parentElement instanceof Motor) {
			List<MotorAxis> returnList = new ArrayList<MotorAxis>();
			Motor m = (Motor)parentElement;
			for(MotorAxis ma : m.getAxes()) {
				if(ma.getClassName().equals(m.getClassName())) {
					returnList.add(ma);
				}
			}
			return returnList.toArray();
		} else if(parentElement instanceof Detector) {
			List<DetectorChannel> returnList = new ArrayList<DetectorChannel>();
			Detector d = (Detector)parentElement;
			for(DetectorChannel ch : d.getChannels()) {
				if(ch.getClassName().equals(d.getClassName())) {
					returnList.add(ch);
				}
			}
			return returnList.toArray();
		} else if(parentElement instanceof List<?>) {
			// return all motors/detectors/devices without a class name
			Object object = ((List<?>)parentElement).get(0);
			if(object instanceof Motor || object instanceof MotorAxis) {
				return motorsAndAxesWithoutClassName.toArray();
			} else if(object instanceof Detector || object instanceof DetectorChannel) {
				return detectorsAndChannelsWithoutClassName.toArray();
			} else if(object instanceof Device) {
				return devicesWithoutClassName.toArray();
			}
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof String) {
			// a class has children if it has any devices
			return (measuringStation.getDeviceList((String)element).size() > 0);
		} else if(element instanceof Motor) {
			// a motor has children if it has axes that have the same class
			for(MotorAxis ma : ((Motor)element).getAxes()) {
				if(ma.getClassName().equals(((Motor)element).getClassName())) {
					return true;
				}
			}
		} else if(element instanceof Detector) {
			// a detector has children if it has channels that have the same class
			for(DetectorChannel ch : ((Detector)element).getChannels()) {
				if(ch.getClassName().equals(((Detector)element).getClassName())) {
					return true;
				}
			}
		} else if (element instanceof MotorAxis) {
			// a motor axis has no children
			return false;
		} else if(element instanceof DetectorChannel) {
			// a detector channel has no children
			return false;
		} else if(element instanceof Device) {
			// a device has no children
			return false;
		} else if(element instanceof List<?>) {
			// a list of motors, detectors or devices has children if it is not empty
			return (!((List<?>)element).isEmpty());
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParent(Object element) {
		if(element instanceof List<?>) {
			return this.measuringStation;
		} else if(element instanceof Motor) {
			if(((Motor)element).getClassName().isEmpty()) {
				return this.motorsAndAxesWithoutClassName;
			} 
			return ((Motor)element).getClassName();
		} else if(element instanceof Detector) {
			if(((Detector)element).getClassName().isEmpty()) {
				return this.detectorsAndChannelsWithoutClassName;
			}
			return ((Detector)element).getClassName();
		} else if(element instanceof Device) {
			if(((Device)element).getClassName().isEmpty()) {
				return this.devicesWithoutClassName;
			}
			return ((Device)element).getClassName();
		} else if(element instanceof MotorAxis) {
			return ((MotorAxis)element).getParent();
		} else if(element instanceof DetectorChannel) {
			return ((DetectorChannel)element).getParent();
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
	public void inputChanged(final Viewer viewer, final Object oldInput, 
			final Object newInput) {
		if(measuringStation != null) {
			measuringStation.removeModelUpdateListener(this);
		}
		measuringStation = (IMeasuringStation)newInput;
		if(measuringStation != null) {
			measuringStation.addModelUpdateListener(this);
		}
		
		this.viewer = viewer;
		
		// a new measuring station was set -> collect devices without class names
		updateDeviceLists();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		updateDeviceLists();
		viewer.refresh();
	}

	/*
	 * helper function which builds lists of motors, detectors and devices 
	 * that don't have class names
	 */
	private void updateDeviceLists() {
		// reinitialize list of motors and axes
		motorsAndAxesWithoutClassName = new ArrayList<AbstractMainPhaseDevice>();
		// add all motors without class names
		for(Motor m : measuringStation.getMotors()) {
			if(m.getClassName().isEmpty()) {
				motorsAndAxesWithoutClassName.add(m);
			}
		}
		// iterate again to ensure that axes are shown after motors in the tree
		for(Motor m : measuringStation.getMotors()) {
			// add all motor axes without class names IF (and only if) their 
			// parent (motor) has a different one (otherwise they appear as a 
			// child of them through the getChildren() method...
			for(MotorAxis ma : m.getAxes()) {
				if (ma.getClassName().isEmpty() && !(m.getClassName().isEmpty())) {
					motorsAndAxesWithoutClassName.add(ma);
				}
			}
		}
		
		// reinitialize list of detectors and channels
		detectorsAndChannelsWithoutClassName = new ArrayList<AbstractMainPhaseDevice>();
		// add all detectors without class names
		for(Detector d : measuringStation.getDetectors()) {
			if(d.getClassName().isEmpty()) {
				detectorsAndChannelsWithoutClassName.add(d);
			}
		}
		// iterate again to ensure that channels are shown after detectors
		for(Detector d : measuringStation.getDetectors()) {
			// add all detector channels without class names IF (and only if) 
			// their parent (detector) has a different one (otherwise they 
			// appear as a child of them through the getChildren() method...
			for(DetectorChannel ch : d.getChannels()) {
				if(ch.getClassName().isEmpty() && !d.getClassName().isEmpty()) {
					detectorsAndChannelsWithoutClassName.add(ch);
				}
			}
		}
		
		// reinitialize list of devices
		devicesWithoutClassName = new ArrayList<AbstractPrePostscanDevice>();
		for(Device dev : measuringStation.getDevices()) {
			if(dev.getClassName().isEmpty()) {
				devicesWithoutClassName.add(dev);
			}
		}
	}
}