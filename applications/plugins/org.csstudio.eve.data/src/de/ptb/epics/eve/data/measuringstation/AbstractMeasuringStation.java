package de.ptb.epics.eve.data.measuringstation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  
 * @author Hartmut Scherr
 */
public abstract class AbstractMeasuringStation implements IMeasuringStation {
	// a Map, that makes all motor axis available by their ids
	protected Map<String, MotorAxis> motorAxisMap;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, MotorAxis> getMotorAxes() {
		return motorAxisMap;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.19
	 * @author Marcus Michalsky
	 */
	@Override
	public List<Option> getMonitorOptions() {
		Set<Option> monitors = new HashSet<Option>();
		for(Detector d : this.getDetectors()) {
			for(Option o : d.getOptions()) {
				if(o.isMonitor()) {
					monitors.add(o);
				}
			}
			for(DetectorChannel ch : d.getChannels()) {
				for(Option o : ch.getOptions()) {
					if(o.isMonitor()) {
						monitors.add(o);
					}
				}
			}
		}
		for(Motor m : this.getMotors()) {
			for(Option o : m.getOptions()) {
				if(o.isMonitor()) {
					monitors.add(o);
				}
			}
			for(MotorAxis ma : m.getAxes()) {
				for(Option o : ma.getOptions()) {
					if(o.isMonitor()) {
						monitors.add(o);
					}
				}
			}
		}
		for(Device dev : this.getDevices()) {
			for(Option o : dev.getOptions()) {
				if(o.isMonitor()) {
					monitors.add(o);
				}
			}
		}
		List<Option> monitorList = new ArrayList<Option>(monitors);
		Collections.sort(monitorList);
		return monitorList;
	}
}