package de.ptb.epics.eve.viewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.measuringstation.Selections;

public class MeasuringStationTreeViewContentProvider implements ITreeContentProvider {

	private MeasuringStation measuringStation;
	
	private List<Event> events;
	
	private List<Device> devices;
	
	private List<Motor> motors;
	
	private List<Detector> detectors;
	
	public Object[] getChildren( final Object parentElement ) {
		if( parentElement instanceof MeasuringStation ) {
			final MeasuringStation measuringStation = (MeasuringStation)parentElement;
			List returnList = new ArrayList();
			if( measuringStation.getMotors().size() > 0 ) {
				this.motors = measuringStation.getMotors();
				returnList.add( this.motors );
			}
			
			if( measuringStation.getDetectors().size() > 0 ) {
				this.detectors = measuringStation.getDetectors();
				returnList.add( this.detectors );
			}
			
			if( measuringStation.getDevices().size() > 0 ) {
				this.devices = measuringStation.getDevices();
				returnList.add( this.devices );
			}
			
			if( measuringStation.getEvents().size() > 0 ) {
				this.events = measuringStation.getEvents();
				returnList.add( this.events );
			}
			return returnList.toArray();
			
		} else if( parentElement instanceof List ) {
			return ((List)parentElement).toArray();
		} else if( parentElement instanceof Motor ) {
			List returnList = new ArrayList();
			final Motor motor = (Motor)parentElement;
			if( motor.getAxis().size() > 0 ) {
				returnList.addAll( motor.getAxis() );
			} else if( motor.getOptions().size() > 0 ) {
				returnList.addAll( motor.getOptions() );
			}
			return returnList.toArray();
		} else if( parentElement instanceof Detector ) {
			List returnList = new ArrayList();
			final Detector detector = (Detector)parentElement;
			if( detector.getChannels().size() > 0 ) {
				returnList.addAll( detector.getChannels() );
			} else if( detector.getOptions().size() > 0 ) {
				returnList.addAll( detector.getOptions() );
			}
			return returnList.toArray();
		} else if( parentElement instanceof Device ) {
			List returnList = new ArrayList();
			final Device device = (Device)parentElement;
			if( device.getOptions().size() > 0 ) {
				returnList.addAll( device.getOptions() );
			}
			return returnList.toArray();
		} else if( parentElement instanceof MotorAxis ) {
			return ((MotorAxis)parentElement).getOptions().toArray();
		} else if( parentElement instanceof DetectorChannel ) {
			return ((DetectorChannel)parentElement).getOptions().toArray();
		}
		return null;
	}

	public Object getParent( final Object element ) {
		if( element instanceof List ) {
			return this.measuringStation;
		} else if( element instanceof Motor ) {
			return this.motors;
		} else if( element instanceof Detector ) {
			return this.detectors;
		} else if( element instanceof Device ) {
			return this.devices;
		} else if( element instanceof Event ) {
			return this.events;
		} else if( element instanceof MotorAxis ) {
			final MotorAxis motorAxis = (MotorAxis)element;
			return motorAxis.getParent();
		} else if( element instanceof DetectorChannel ) {
			final DetectorChannel detectorChannel = (DetectorChannel)element;
			return detectorChannel.getParent();
		} else if( element instanceof Option ) {
			final Option option = (Option)element;
			return option.getParent();
		}
		return null;
	}

	public boolean hasChildren( final Object element ) {
		if( element instanceof MeasuringStation ) {
			final MeasuringStation measuringStation = (MeasuringStation)element;
			return measuringStation.getMotors().size() > 0 || measuringStation.getDetectors().size() > 0 || measuringStation.getDevices().size() > 0 || measuringStation.getEvents().size() > 0; 
		} else if( element instanceof List ) {
			return true;
		} else if( element instanceof Motor ) {
			final Motor motor = (Motor)element;
			return motor.getAxis().size() > 0 || motor.getOptions().size() > 0;
		} else if( element instanceof Detector ) {
			final Detector detector = (Detector)element;
			return detector.getChannels().size() > 0 || detector.getOptions().size() > 0;
		} else if( element instanceof Device ) {
			final Device device = (Device)element;
			return device.getOptions().size() > 0;
		} else if( element instanceof MotorAxis ) {
			final MotorAxis motorAxis = (MotorAxis)element;
			return motorAxis.getOptions().size() > 0;
		} else if( element instanceof DetectorChannel ) {
			final DetectorChannel detectorChannel = (DetectorChannel)element;
			return detectorChannel.getOptions().size() > 0;
		}
		return false;
	}

	public Object[] getElements( final Object inputElement ) {
		if( inputElement instanceof MeasuringStation ) {
			final MeasuringStation measuringStation = (MeasuringStation)inputElement;
			List returnList = new ArrayList();
			if( measuringStation.getMotors().size() > 0 ) {
				this.motors = measuringStation.getMotors();
				returnList.add( this.motors );
			}
			
			if( measuringStation.getDetectors().size() > 0 ) {
				this.detectors = measuringStation.getDetectors();
				returnList.add( this.detectors );
			}
			
			if( measuringStation.getDevices().size() > 0 ) {
				this.devices = measuringStation.getDevices();
				returnList.add( this.devices );
			}
			
			if( measuringStation.getEvents().size() > 0 ) {
				this.events = measuringStation.getEvents();
				returnList.add( this.events );
			}
			return returnList.toArray();
			
		} else if( inputElement instanceof List ) {
			return ((List)inputElement).toArray();
		} else if( inputElement instanceof Motor ) {
			List returnList = new ArrayList();
			final Motor motor = (Motor)inputElement;
			if( motor.getAxis().size() > 0 ) {
				returnList.addAll( motor.getAxis() );
			} else if( motor.getOptions().size() > 0 ) {
				returnList.addAll( motor.getOptions() );
			}
			return returnList.toArray();
		} else if( inputElement instanceof Detector ) {
			List returnList = new ArrayList();
			final Detector detector = (Detector)inputElement;
			if( detector.getChannels().size() > 0 ) {
				returnList.addAll( detector.getChannels() );
			} else if( detector.getOptions().size() > 0 ) {
				returnList.addAll( detector.getOptions() );
			}
			return returnList.toArray();
		} else if( inputElement instanceof Device ) {
			List returnList = new ArrayList();
			final Device device = (Device)inputElement;
			if( device.getOptions().size() > 0 ) {
				returnList.addAll( device.getOptions() );
			}
			return returnList.toArray();
		} else if( inputElement instanceof MotorAxis ) {
			return ((MotorAxis)inputElement).getOptions().toArray();
		} else if( inputElement instanceof DetectorChannel ) {
			return ((DetectorChannel)inputElement).getOptions().toArray();
		}
		return null;
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged( final Viewer viewer, final Object oldInput, final Object newInput ) {
		this.measuringStation = (MeasuringStation)newInput;

	}

}
