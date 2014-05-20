package de.ptb.epics.eve.data.scandescription;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeFilter;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * Delegate to manage options monitored in a scan description.
 * <p>
 * Depending on {@link #getType()} 
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class MonitorDelegate implements IModelUpdateListener {
	public static final String TYPE_PROP = "type";
	
	private ScanDescription delegator;
	private MonitorOption type;
	private List<Option> monitors;
	
	private PropertyChangeSupport propertyChangeSupport;
	
	/**
	 * Constructor.
	 */
	public MonitorDelegate(ScanDescription delegator) {
		this.delegator = delegator;
		this.type = MonitorOption.NONE;
		this.monitors = new ArrayList<Option>();
		
		this.delegator.addModelUpdateListener(this);
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * @return the type
	 */
	public MonitorOption getType() {
		return type;
	}

	/**
	 * Sets the monitoring type as in
	 * {@link de.ptb.epics.eve.data.scandescription.MonitorOption}.
	 * <p>
	 * Does nothing if set type is equal to the given type.
	 * 
	 * @param type the type to set
	 */
	public void setType(MonitorOption type) {
		if (this.type.equals(type)) {
			return;
		}
		MonitorOption oldType = this.type;
		this.type = type;
		
		switch (type) {
		case AS_IN_DEVICE_DEFINITION:
			this.monitorDeviceDefinition();
			break;
		case CUSTOM:
			this.monitorCustom();
			break;
		case NONE:
			this.monitorClear();
			break;
		case USED_IN_SCAN:
			this.monitorScanDescription();
			break;
		default:
			break;
		}
		
		this.propertyChangeSupport.firePropertyChange(
				MonitorDelegate.TYPE_PROP, oldType, this.type);
	}
	
	private void monitorClear() {
		this.monitors.clear();
	}
	
	private void monitorDeviceDefinition() {
		this.monitorClear();
		
		IMeasuringStation measuringStation = this.delegator
				.getMeasuringStation();
		for (Detector d : measuringStation.getDetectors()) {
			for (Option o : d.getOptions()) {
				if(!o.isMonitor()) {
					continue;
				}
				this.monitors.add(o);
			}
			for (DetectorChannel ch : d.getChannels()) {
				for (Option o : ch.getOptions()) {
					if(!o.isMonitor()) {
						continue;
					}
					this.monitors.add(o);
				}
			}
		}

		for (Motor m : measuringStation.getMotors()) {
			for (Option o : m.getOptions()) {
				if(!o.isMonitor()) {
					continue;
				}
				this.monitors.add(o);
			}
			for (MotorAxis ma : m.getAxes()) {
				for (Option o : ma.getOptions()) {
					if(!o.isMonitor()) {
						continue;
					}
					this.monitors.add(o);
				}
			}
		}

		for (Device dev : measuringStation.getDevices()) {
			for (Option o : dev.getOptions()) {
				if(!o.isMonitor()) {
					continue;
				}
				this.monitors.add(o);
			}
		}
	}
	
	private void monitorScanDescription() {
		this.monitorClear();
		
		ExcludeFilter filteredStation = new ExcludeFilter();
		filteredStation.setSource(this.delegator.getMeasuringStation());
		filteredStation.excludeUnusedDevices(this.delegator);
		
		for (Detector d : filteredStation.getDetectors()) {
			for (Option o : d.getOptions()) {
				if(!o.isMonitor()) {
					continue;
				}
				this.monitors.add(o);
			}
			for (DetectorChannel ch : d.getChannels()) {
				for (Option o : ch.getOptions()) {
					if(!o.isMonitor()) {
						continue;
					}
					this.monitors.add(o);
				}
			}
		}		

		for (Motor m : filteredStation.getMotors()) {
			for (Option o : m.getOptions()) {
				if(!o.isMonitor()) {
					continue;
				}
				this.monitors.add(o);
			}
			for (MotorAxis ma : m.getAxes()) {
				for (Option o : ma.getOptions()) {
					if(!o.isMonitor()) {
						continue;
					}
					this.monitors.add(o);
				}
			}
		}

		for (Device dev : filteredStation.getDevices()) {
			for (Option o : dev.getOptions()) {
				if(!o.isMonitor()) {
					continue;
				}
				this.monitors.add(o);
			}
		}
	}
	
	private void monitorCustom() {
		this.monitorClear();
	}
	
	/**
	 * @return the monitors
	 */
	public List<Option> getMonitors() {
		return monitors;
	}

	/**
	 * Add option that should be monitored.
	 * 
	 * @param o the option to monitor
	 * @throws UnsupportedOperationException
	 *             if {@link #getType()} is not
	 *             {@link de.ptb.epics.eve.data.scandescription.MonitorOption#CUSTOM}
	 */
	public void add(Option o) throws UnsupportedOperationException {
		if (!this.type.equals(MonitorOption.CUSTOM)) {
			throw new UnsupportedOperationException(
					"monitoring devices is not set to custom!");
		}
		this.monitors.add(o);
	}
	
	/**
	 * Disable monitoring of the given option.
	 * @param o the option monitoring should be stopped from
	 * @throws UnsupportedOperationException
	 *             if {@link #getType()} is not
	 *             {@link de.ptb.epics.eve.data.scandescription.MonitorOption#CUSTOM}
	 */
	public void remove(Option o) throws UnsupportedOperationException {
		if (!this.type.equals(MonitorOption.CUSTOM)) {
			throw new UnsupportedOperationException(
					"monitoring devices is not set to custom!");
		}
		this.monitors.remove(o);
	}
	
	/**
	 * 
	 * 
	 * @param property
	 * @param listener
	 */
	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	/**
	 * 
	 * 
	 * @param property
	 * @param listener
	 */
	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		if (this.type.equals(MonitorOption.USED_IN_SCAN)) {
			this.monitorScanDescription();
			this.propertyChangeSupport.firePropertyChange(
					ScanDescription.MONITOR_OPTIONS_LIST_PROP, null, monitors);
		}
	}
}