package de.ptb.epics.eve.data.scandescription;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessage;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessageEnum;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;


/**
 * Because a pause event have one more field, that is indicating that all operations
 * will be continue if the conditions of event are not true anymore, PauseEvent extends
 * ControlEvent to provide the additional field.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class PauseEvent extends ControlEvent {

	/*
	 * The attribute.
	 */
	private boolean continueIfFalse;

	/**
	 * Constructs a <code>PauseEvent</code>.
	 * 
	 * @param type The type of the pause event.
	 */
	public PauseEvent(final EventTypes type) {
		super(type);
		this.continueIfFalse = false;
	}

	/**
	 * Better Constructor.
	 * 
	 * @param type see {@link de.ptb.epics.eve.data.EventTypes}
	 * @param event the event
	 * @param id the id
	 * @since 1.1
	 */
	public PauseEvent(final EventTypes type, Event event, String id) {
		super(type, event, id);
System.out.println("\tPauseEvent wird angelegt, event: " + event.getName());
System.out.println("\t\tgetMonitor().getDataType.getType: " + event.getMonitor().getDataType().getType());
		this.continueIfFalse = false;
	}
	
	/**
	 * Checks whether the attribute is set.
	 * 
	 * @return <code>true</code> if the attribute is set, 
	 * 		   <code>false</code> otherwise
	 */
	public boolean isContinueIfFalse() {
		return continueIfFalse;
	}
	
	/**
	 * Sets the attribute.
	 * 
	 * @param continueIfFalse <code>true</code> if continue, 
	 * 		  				  <code>false</code> otherwise
	 */
	public void setContinueIfFalse(boolean continueIfFalse) {
		this.continueIfFalse = continueIfFalse;
		updateListeners();
	}
	
	/*
	 * 
	 */
	private void updateListeners()
	{
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(this.modelUpdateListener);
		
		Iterator<IModelUpdateListener> it = list.iterator();
		
		while(it.hasNext()) {
			it.next().updateEvent(new ModelUpdateEvent(this, 
				new ControlEventMessage(this, ControlEventMessageEnum.UPDATED)));
		}
	}	
}