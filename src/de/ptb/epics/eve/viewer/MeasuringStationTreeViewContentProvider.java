package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.measuringstation.Selections;

public class MeasuringStationTreeViewContentProvider implements ITreeContentProvider {

	private IMeasuringStation measuringStation;
	
	private List<Device> devices;
	
	private List<Motor> motors;
	
	private List<Detector> detectors;
	
	
	private List<MotorAxis> motorAxis;
	
	private List<DetectorChannel> detectorChannels;

	public Object[] getChildren( final Object parentElement ) {
		if( parentElement instanceof IMeasuringStation ) {
//			final MeasuringStation measuringStation = (MeasuringStation)parentElement;
//			List<Object> returnList = new ArrayList<Object>();
//			
//			if (measuringStation.getClassNameList().size() > 0){
//				returnList.addAll( measuringStation.getClassNameList() );
//			}
//			if( measuringStation.getMotors().size() > 0List<Object>/				this.motors = measuringStation.getMotors();
//				for (Motor motor : measuringStation.getMotors()) {
//					if (motor.getClassName().length() > 0) this.motors.remove(motor);
//				}
//				returnList.add( this.motors );
//			}
//			
//			if( measuringStation.getDetectors().size() > 0 ) {
//				this.detectors = measuringStation.getDetectors();
//				for (Detector detector : measuringStation.getDetectors()) {
//					if (detector.getClassName().length() > 0) this.detectors.remove(detector);
//				}
//				returnList.add( this.detectors );
//			}
//			
//			if( measuringStation.getDevices().size() > 0 ) {
//				this.devices = measuringStation.getDevices();
//				for (Device device : measuringStation.getDevices()) {
//					if (device.getClassName().length() > 0) this.devices.remove(device);
//				}
//				returnList.add( this.devices );
//			}
//			return returnList.toArray();
		}
		else if( parentElement instanceof String ) {
			return measuringStation.getDeviceList((String) parentElement).toArray();
		}
		else if( parentElement instanceof List<?> ) {
			Object object = ((List<?>)parentElement).get(0);
			if (object instanceof Motor) return motors.toArray();
			else if (object instanceof Detector) return detectors.toArray();
			else if (object instanceof Device) return devices.toArray();
		}
		else if( parentElement instanceof Motor ) {
			List returnList = new ArrayList();
			final Motor motor = (Motor)parentElement;
			final List< MotorAxis > axis = motor.getAxes();
			for( final MotorAxis a : axis ) {
				if( a.getClassName().equals( "" ) ) {
					returnList.add( a );
				}
			}
			/*if( motor.getOptions().size() > 0 ) {
				returnList.addAll( motor.getOptions() );
			}*/
			return returnList.toArray();
		}
		else if( parentElement instanceof Detector ) {
			List returnList = new ArrayList();
			final Detector detector = (Detector)parentElement;
			final List< DetectorChannel > channels = detector.getChannels();
			for( final DetectorChannel c : channels ) {
				if( c.getClassName().equals( "" ) ) {
					returnList.add( c );
				}
			}
			/*if( detector.getOptions().size() > 0 ) {
				returnList.addAll( detector.getOptions() );
			}*/
			return returnList.toArray();
		}
		/*else if( parentElement instanceof Device ) {
			List<Object> returnList = new ArrayList<Object>();
			final Device device = (Device)parentElement;
			if( device.getOptions().size() > 0 ) {
				returnList.addAll( device.getOptions() );
			}
			return returnList.toArray();
		} */
		return null;
	}

	public Object getParent( final Object element ) {

//		if( element instanceof List ) {
//			return this.measuringStation;
//		} else if( element instanceof Motor ) {
//			return this.motors;
//		} else if( element instanceof Detector ) {
//			return this.detectors;
//		} else if( element instanceof Device ) {
//			return this.devices;
//		} else if( element instanceof MotorAxis ) {
//			final MotorAxis motorAxis = (MotorAxis)element;
//			return motorAxis.getParent();
//		} else if( element instanceof DetectorChannel ) {
//			final DetectorChannel detectorChannel = (DetectorChannel)element;
//			return detectorChannel.getParent();
//		} else if( element instanceof Option ) {
//			final Option option = (Option)element;
//			return option.getParent();
//		}
		return null;
	}

	public boolean hasChildren( final Object element ) {
		if( element instanceof IMeasuringStation ) {
			final IMeasuringStation measuringStation = (IMeasuringStation)element;
			return measuringStation.getMotors().size() > 0 || measuringStation.getDetectors().size() > 0 || measuringStation.getDevices().size() > 0 || measuringStation.getEvents().size() > 0; 
		} else if( element instanceof List ) {
			return (((List)element).size() > 0);
		} else if( element instanceof Motor ) {
			final Motor motor = (Motor)element;
			return motor.getAxes().size() > 0 || motor.getOptions().size() > 0;
		} else if( element instanceof Detector ) {
			final Detector detector = (Detector)element;
			return detector.getChannels().size() > 0 || detector.getOptions().size() > 0;
		} else if( element instanceof Device ) {
			return false;
		} else if( element instanceof MotorAxis ) {
			return false;
		} else if( element instanceof DetectorChannel ) {
			return false;
		}
		else if( element instanceof String ) {
			if (measuringStation.getDeviceList((String)element).size() > 0)
				return true;
			else
				return false;
		}
		return false;
	}

	public Object[] getElements( final Object inputElement ) {
		if( inputElement instanceof IMeasuringStation ) {
			final IMeasuringStation measuringStation = (IMeasuringStation)inputElement;
			List<Object> returnList = new ArrayList<Object>();
			if (measuringStation.getClassNameList().size() > 0){
				returnList.addAll( measuringStation.getClassNameList() );
			}
			if( measuringStation.getMotors().size() > 0 ) {
				this.motors = new ArrayList<Motor>(measuringStation.getMotors());
				for (Motor motor : measuringStation.getMotors()) {
					if (motor.getClassName().length() > 0) this.motors.remove(motor);
				}
				if (!motors.isEmpty()) returnList.add( motors );
			}
			
			if( measuringStation.getDetectors().size() > 0 ) {
				this.detectors = new ArrayList<Detector>(measuringStation.getDetectors());
				for (Detector detector : measuringStation.getDetectors()) {
					if (detector.getClassName().length() > 0) this.detectors.remove(detector);
				}
				if (!detectors.isEmpty()) returnList.add( this.detectors );
			}
			
			if( measuringStation.getDevices().size() > 0 ) {
				this.devices = new ArrayList<Device>(measuringStation.getDevices());
				for (Device device : measuringStation.getDevices()) {
					if (device.getClassName().length() > 0) this.devices.remove(device);
				}
				if (!devices.isEmpty()) returnList.add( this.devices );
			}
			return returnList.toArray();	
		} 
//		else if( inputElement instanceof List ) {
//			return ((List)inputElement).toArray();
//		} else if( inputElement instanceof Motor ) {
//			List returnList = new ArrayList();
//			final Motor motor = (Motor)inputElement;
//			if( motor.getAxis().size() > 0 ) {
//				returnList.addAll( motor.getAxis() );
//			} 
//			return returnList.toArray();
//		} else if( inputElement instanceof Detector ) {
//			List returnList = new ArrayList();
//			final Detector detector = (Detector)inputElement;
//			if( detector.getChannels().size() > 0 ) {
//				returnList.addAll( detector.getChannels() );
//			} 
//			return returnList.toArray();
//		}
		return null;
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput ) {
		this.measuringStation = (IMeasuringStation)newInput;

	}

}
