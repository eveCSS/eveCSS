package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.Option;

/**
 * Delegate to manage options monitored in a scan description.
 * <p>
 * Depending on {@link #getType()} 
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class MonitorDelegate {
	private ScanDescription delegator;
	private MonitorOption type;
	private List<Option> monitors;
	
	/**
	 * Constructor.
	 */
	public MonitorDelegate(ScanDescription delegator) {
		this.delegator = delegator;
		this.type = MonitorOption.NONE;
		this.monitors = new ArrayList<>();
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
		this.type = type;
		
		switch (type) {
		case AS_IN_DEVICE_DEFINITION:
			this.monitors.addAll(this.delegator.getMeasuringStation()
					.getMonitorOptions());
			break;
		case CUSTOM:
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
	public void add(Option o) {
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
	public void remove(Option o) {
		if (!this.type.equals(MonitorOption.CUSTOM)) {
			throw new UnsupportedOperationException(
					"monitoring devices is not set to custom!");
		}
		this.monitors.remove(o);
	}
	
	/**
	 * Removes all monitor options.
	 * @since 1.30
	 */
	public void removeAll() {
		if (!this.type.equals(MonitorOption.CUSTOM)) {
			throw new UnsupportedOperationException(
					"monitoring devices is not set to custom!");
		}
		this.monitors.clear();
	}
	
	/**
	 * @since 1.31
	 */
	public void update() {
		if (this.type.equals(MonitorOption.USED_IN_SCAN)) {
			this.monitors.clear();
			this.monitors.addAll(this.delegator.getMonitorOptions());
		}
	}
}