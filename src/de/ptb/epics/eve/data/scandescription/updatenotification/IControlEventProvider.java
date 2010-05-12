/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription.updatenotification;

import java.util.List;

import de.ptb.epics.eve.data.scandescription.ControlEvent;

public interface IControlEventProvider extends IModelUpdateProvider, IModelUpdateListener {

	public List<? extends ControlEvent> getControlEventsList();
		
}
