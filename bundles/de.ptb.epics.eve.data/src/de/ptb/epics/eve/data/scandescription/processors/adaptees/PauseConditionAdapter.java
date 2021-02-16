package de.ptb.epics.eve.data.scandescription.processors.adaptees;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.PauseCondition;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseConditionAdapter extends 
		XmlAdapter<PauseCondition, PauseConditionAdaptee> {
	private IMeasuringStation measuringStation;
	
	public PauseConditionAdapter(IMeasuringStation measuringStation) {
		this.measuringStation = measuringStation;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public PauseConditionAdaptee unmarshal(PauseCondition pauseCondition) 
			throws Exception {
		PauseConditionAdaptee adaptee = new PauseConditionAdaptee();
		adaptee.setId(pauseCondition.getDevice().getID());
		adaptee.setOperator(pauseCondition.getOperator());
		adaptee.setPauseType(pauseCondition.getType());
		adaptee.setPauseLimit(pauseCondition.getPauseLimit());
		if (pauseCondition.hasContinueLimit()) {
			adaptee.setContinueType(pauseCondition.getType());
			adaptee.setContinueLimit(pauseCondition.getContinueLimit());
		} else {
			adaptee.setContinueType(null);
			adaptee.setContinueLimit(null);
		}
		return adaptee;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PauseCondition marshal(PauseConditionAdaptee adaptee) throws Exception {
		PauseCondition pauseCondition = new PauseCondition(
			this.measuringStation.getAbstractDeviceById(adaptee.getId()));
		pauseCondition.setOperator(adaptee.getOperator());
		pauseCondition.setPauseLimit(adaptee.getPauseLimit());
		if (adaptee.getContinueLimit() != null) {
			pauseCondition.setContinueLimit(adaptee.getContinueLimit());
		}
		return pauseCondition;
	}
}
