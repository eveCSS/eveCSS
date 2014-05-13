package de.ptb.epics.eve.data.measuringstation;

import java.util.Map;

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

}