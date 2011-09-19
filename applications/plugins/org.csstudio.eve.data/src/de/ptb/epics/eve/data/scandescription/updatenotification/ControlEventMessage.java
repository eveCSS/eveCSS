/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription.updatenotification;

import de.ptb.epics.eve.data.scandescription.ControlEvent;

public class ControlEventMessage {

	private ControlEvent object;
	private ControlEventMessageEnum operation;
	
	public ControlEventMessage( final ControlEvent object, final ControlEventMessageEnum operation) {
		this.object = object;
		this.operation = operation;
	}

	public ControlEvent getObject() {
		return object;
	}

	public ControlEventMessageEnum getOperation() {
		return operation;
	}
	
	
	
}
