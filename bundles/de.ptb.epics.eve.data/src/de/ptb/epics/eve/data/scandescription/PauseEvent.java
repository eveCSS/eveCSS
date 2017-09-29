package de.ptb.epics.eve.data.scandescription;

import java.util.concurrent.CopyOnWriteArrayList;

import de.ptb.epics.eve.data.EventActions;
import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessage;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessageEnum;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class PauseEvent extends ControlEvent {

	/** @since 1.2 */
	private EventActions eventAction;
	
	/**
	 * Constructs a <code>PauseEvent</code>.
	 * 
	 * @param type The type of the pause event.
	 */
	public PauseEvent(final EventTypes type) {
		super(type);
		switch(type) {
		case DETECTOR:
		case SCHEDULE:
			this.eventAction = EventActions.ON;
			break;
		case MONITOR:
			this.eventAction = EventActions.ONOFF;
			break;
		}
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
		switch(type) {
		case DETECTOR:
		case SCHEDULE:
			this.eventAction = EventActions.ON;
			break;
		case MONITOR:
			this.eventAction = EventActions.ONOFF;
			break;
		}
	}

	/**
	 * Copy Constructor.
	 * 
	 * @param event the event to be copied
	 * @return a copy of the given event
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public static PauseEvent newInstance(PauseEvent event) {
		PauseEvent newPauseEvent = new PauseEvent(event.getEventType(),
				event.getEvent(), event.getDeviceId()); // TODO ?
		newPauseEvent.getLimit().setType(event.getLimit().getType());
		newPauseEvent.getLimit().setComparison(event.getLimit().getComparison());
		newPauseEvent.getLimit().setValue(event.getLimit().getValue());
		newPauseEvent.eventAction = event.eventAction;
		return newPauseEvent;
	}
	
	/**
	 * @return the eventAction
	 * @since 1.2
	 */
	public EventActions getEventAction() {
		return eventAction;
	}

	/**
	 * @param eventAction the eventAction to set
	 * @since 1.2
	 * @throws IllegalArgumentException
	 * 			if <code>eventAction</code> is
	 * 			{@link de.ptb.epics.eve.data.EventActions#ONOFF} and event
	 * 			type is not {@link de.ptb.epics.eve.data.EventTypes#MONITOR}
	 */
	public void setEventAction(EventActions eventAction) {
		if (eventAction.equals(EventActions.ONOFF)
				&& !this.eventType.equals(EventTypes.MONITOR)) {
			throw new IllegalArgumentException(
					"Event action 'ONOFF' is only valid for events of type 'MONITOR'");
		}
		this.eventAction = eventAction;
		this.updateListeners();
	}

	/*
	 * 
	 */
	private void updateListeners() {
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(this.modelUpdateListener);
		
		for(IModelUpdateListener imul : list) {
			imul.updateEvent(new ModelUpdateEvent(this,
					new ControlEventMessage(this,
							ControlEventMessageEnum.UPDATED)));
		}
	}
}