package de.ptb.epics.eve.data.scandescription.updatenotification;

import de.ptb.epics.eve.data.scandescription.ControlEvent;

/**
 * 
 * @author ?
 */
public class ControlEventMessage {

	private ControlEvent object;
	private ControlEventMessageEnum operation;
	
	/**
	 * Constructor.
	 * 
	 * @param object
	 * @param operation
	 */
	public ControlEventMessage(final ControlEvent object, 
			final ControlEventMessageEnum operation) {
		this.object = object;
		this.operation = operation;
	}

	/**
	 * 
	 * @return
	 */
	public ControlEvent getObject() {
		return object;
	}

	/**
	 * 
	 * @return
	 */
	public ControlEventMessageEnum getOperation() {
		return operation;
	}
}