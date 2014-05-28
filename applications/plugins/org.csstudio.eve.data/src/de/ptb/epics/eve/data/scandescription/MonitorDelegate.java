package de.ptb.epics.eve.data.scandescription;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.Option;
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
			this.monitors.addAll(this.delegator.getMeasuringStation()
					.getMonitorOptions());
			break;
		case CUSTOM:
			this.monitors.clear();
			break;
		case NONE:
			this.monitors.clear();
			break;
		case USED_IN_SCAN:
			this.monitors.addAll(this.delegator.getMonitorOptions());
			break;
		default:
			break;
		}
		
		this.propertyChangeSupport.firePropertyChange(
				MonitorDelegate.TYPE_PROP, oldType, this.type);
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
			this.monitors.clear();
			this.monitors.addAll(this.delegator.getMonitorOptions());
			this.propertyChangeSupport.firePropertyChange(
					ScanDescription.MONITOR_OPTIONS_LIST_PROP, null, monitors);
		}
	}
}