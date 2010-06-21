/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.errors.ControlEventError;
import de.ptb.epics.eve.data.scandescription.errors.ControlEventErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessage;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessageEnum;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * A ControlEvent is used to encapsulate a normal event and specify on which
 * conditions it should be activated. 
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class ControlEvent implements IModelUpdateListener, IModelUpdateProvider, IModelErrorProvider {

	/**
	 * The event.
	 */
	
	private Event event;
	/**
	 * The conditions.
	 */
	
	private Limit limit;
	
	/**
	 * The event type.
	 */
	private EventTypes eventType;
	
	/**
	 * The event id.
	 */
	private String eventId;
	
	
	/**
	 * A list of model update listener.
	 */
	protected List< IModelUpdateListener > modelUpdateListener;
	
	
	/**
	 * This constructor constructs a new ControlEvent.
	 *
	 * @type The type of the control event.
	 */
	public ControlEvent( final EventTypes type ) {
		this.eventType = type;
		this.modelUpdateListener = new ArrayList< IModelUpdateListener >();
		this.limit = new Limit();
		this.limit.addModelUpdateListener( this );
		if( (type != EventTypes.MONITOR) && (type != EventTypes.DETECTOR)) {
			event = new Event( type ); 
		}
	}
	
	/**
	 * This method returns the type of the control event.
	 * 
	 * @return
	 */
	public EventTypes getEventType() {
		return eventType;
	}

	/**
	 * Gives back the event that is encapsulated by this ControlEvent.
	 * 
	 * @return This method returns the encapsulated event.
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * Sets the event that should be controlled by this ControlEvent.
	 * 
	 * @param event The event that should be controlled by this ControlEvent.
	 */
	public void setEvent( final Event event) {
		if (event != null){
			this.event = event;
			Iterator<IModelUpdateListener> it = this.modelUpdateListener.iterator();
			this.limit.setValue( this.limit.getValue() );
			while( it.hasNext() ) {
				it.next().updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( this, ControlEventMessageEnum.UPDATED ) ) );
			}
		}
	}

	/**
	 * Gives back the Limit object of this ControlEvent. All manipulations
	 * should be done with this object.
	 * 
	 * @return The Limit object. Never returns null.
	 */
	public Limit getLimit() {
		return limit;
	}
	
	/**
	 * This method return the id of the control event.
	 * 
	 * @return Returns the id of the control event.
	 */
	public String getDeviceId () {
		String device = eventId;
		return device;
	}

	/**
	 * This method return the id of the device from the control event.
	 * 
	 * @return Returns the id of the device.
	 */
	public String getId () {
		return eventId;
	}
	
	/**
	 * This method sets the id of this control event.
	 * 
	 * @param id The new id of this control event.
	 */
	public void setId( final String id) {
		eventId = id;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener#updateEvent(de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent)
	 */
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		if( modelUpdateEvent.getSender() == this.limit ) {	
			final Iterator<IModelUpdateListener> it = this.modelUpdateListener.iterator();
			while( it.hasNext() ) {
				it.next().updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( this, ControlEventMessageEnum.UPDATED ) ) );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider#addModelUpdateListener(de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener)
	 */
	public boolean addModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.modelUpdateListener.add( modelUpdateListener );
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider#removeModelUpdateListener(de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener)
	 */
	public boolean removeModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.modelUpdateListener.remove( modelUpdateListener );
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider#getModelErrors()
	 */
	@Override
	public List< IModelError > getModelErrors() {
		final List< IModelError > errorList = new ArrayList< IModelError >();
		if( this.event.getType() == EventTypes.MONITOR && this.event.getMonitor().getAccess() != null && !this.event.getMonitor().getAccess().isValuePossible( this.limit.getValue() ) ) {
			errorList.add( new ControlEventError( this, ControlEventErrorTypes.VALUE_NOT_POSSIBLE ) );
		}
		return errorList;
	}
		
}
