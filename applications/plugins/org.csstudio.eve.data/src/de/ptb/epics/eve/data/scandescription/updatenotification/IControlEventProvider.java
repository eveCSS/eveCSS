package de.ptb.epics.eve.data.scandescription.updatenotification;

import java.util.List;

import de.ptb.epics.eve.data.scandescription.ControlEvent;

/**
 * 
 * @author ?
 */
public interface IControlEventProvider extends IModelUpdateProvider,
		IModelUpdateListener {

	/**
	 * 
	 * @return
	 */
	List<? extends ControlEvent> getControlEventsList();
}