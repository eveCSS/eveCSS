/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class describes a scan. It is the main container of the Chains and all other
 * components of a scan description.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class ScanDescription implements IModelUpdateProvider, IModelUpdateListener, IModelErrorProvider {

	/**
	 * version of the scandescription.
	 */
	private int inputVersion;
	
	/**
	 * The input revision.
	 */
	private int inputRevision;
	
	/**
	 * The input modification
	 */
	private int inputModification;
	
	/**
	 * our output schema version
	 */
	public static final String outputVersion = "0.3.7";
	
	/**
	 * A List that is holding all chains of the scan description.
	 */
	private List<Chain> chains;
	
	/**
	 * A Map, that is mapping all ids of the events to the Event objects.
	 * No doubles are allowed
	 */
	private Map< String, Event > eventsMap;
	
	/**
	 * A List of listeners that will be notified if something was updated.
	 */
	private List< IModelUpdateListener > modelUpdateListener;
	
	/**
	 * The measuring station that is used by this scan description.
	 */
	private final IMeasuringStation measuringStation;
	
	/**
	 * This constrcutor constructs a new scan description and adds the S0 start event
	 * to it's own events list.
	 *
	 */
	public ScanDescription( final IMeasuringStation measuringStation ) {
		super();
		this.chains = new ArrayList<Chain>();
		//this.events = new ArrayList<Event>();
		this.eventsMap = new HashMap< String, Event >();
		this.modelUpdateListener = new ArrayList< IModelUpdateListener >();
		// default start event
		Event s0 = new Event(EventTypes.SCHEDULE);
		s0.setName("Start");
		this.add( s0 );
		this.measuringStation = measuringStation;
		
	}

	/**
	 * This method adds a chain to the scan description. 
	 * 
	 * @param chain The chain that should be added to the scan description.
	 * @return Returns true if the chain was added and false if not.
	 */
	public boolean add( final Chain chain ) {
		chain.setScanDescription( this );
		boolean returnValue = chains.add( chain );
		chain.addModelUpdateListener( this );
		final Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
		return returnValue;
	}

	/**
	 * This method removes a chain from the scan description.
	 * 
	 * @param chain The chain that should be removed from the scan description.
	 * @return Returns true if the chain has been removed als false if not.
	 */
	public boolean remove( final Chain chain ) {
		boolean returnValue = chains.remove( chain );
		chain.removeModelUpdateListener( this );
		final Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
		return returnValue;
	}
	
	/**
	 * This method removes a event from the scan description.
	 * 
	 * @param event The event that should be removed from the scan description.
	 * @return Returns true if the event has been removed als false if not.
	*/
	public boolean remove( final Event event ) {
		//boolean returnValue = events.remove( event );
		boolean returnValue = this.eventsMap.containsValue(event);
		final Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
		this.eventsMap.remove( event.getID() );
		//TODO
		// we loop through chains and collect all ControlEvents
		// this should be done easier
		List<ControlEvent> eventList = new ArrayList<ControlEvent>();
		for (Chain loopChain : chains) {
			removeControlEventIfNotInList(loopChain.getBreakControlEventManager(), event);
			removeControlEventIfNotInList(loopChain.getStartControlEventManager(), event);
			removeControlEventIfNotInList(loopChain.getStopControlEventManager(), event);
			removeControlEventIfNotInList(loopChain.getRedoControlEventManager(), event);
			removeControlEventIfNotInList(loopChain.getPauseControlEventManager(), event);
			for (ScanModul loopScanModule : loopChain.getScanModuls() ){
				removeControlEventIfNotInList(loopScanModule.getBreakControlEventManager(), event);
				removeControlEventIfNotInList(loopScanModule.getRedoControlEventManager(), event);
				removeControlEventIfNotInList(loopScanModule.getTriggerControlEventManager(), event);
				removeControlEventIfNotInList(loopScanModule.getPauseControlEventManager(), event);
			}
		}
		// if a controlEvent uses the event, remove the ControlEvent
		for (ControlEvent cevent : eventList) {
			Event embeddedEvent = cevent.getEvent();
			if (embeddedEvent != null){
				if (embeddedEvent == event) cevent.updateEvent(new ModelUpdateEvent( this, null));
			}
		}
		return returnValue;
	}

	/**
	 * This method removes a control event if it is not longer in the list.
	 * 
	 * @param manager The control event manager.
	 * @param event The event.
	 */
	private void removeControlEventIfNotInList( final ControlEventManager manager, final Event event ){
		final List< ? extends ControlEvent> eventList = manager.getControlEventsList();
		// if a controlEvent uses the event, remove the ControlEvent
		for( ControlEvent cevent : eventList ) {
			final Event embeddedEvent = cevent.getEvent();
			if( embeddedEvent != null ){
				if( embeddedEvent == event ) 
					manager.removeControlEvent( cevent );
			}
		}
	}
	/**
	 * Gives back the version of the scan description.
	 * 
	 * @return The version of the scan description.
	 */
	public String getVersion() {
		return String.valueOf(inputVersion) + "." + String.valueOf(inputRevision) + "." + String.valueOf(inputModification);
	}

	/**
	 * Sets the version of the scan description.
	 * 
	 * @param version The version of the scan description.
	 */
	public void setVersion( final String version ) {
		String[] versionArray = version.split("\\.");
		if (versionArray.length == 3){
			inputVersion =  Integer.parseInt(versionArray[0]);
			inputRevision =  Integer.parseInt(versionArray[1]);
			inputModification =  Integer.parseInt(versionArray[2]);
		}
	}
	
	/**
	 * This method gives back a copy of the internal list, that is holding the chains.
	 * 
	 * @return A copy of the internal list that is holding the chain. Never returns null.
	 */
	public List<Chain> getChains() {
		return new ArrayList<Chain>( this.chains );
	}

	/**
	 * return the chain with the specified id.
	 * 
	 * @return the chain with the given id or null, if such a chain does not exist.
	 */
	public Chain getChain(int chainId) {
		Chain retChain = null;
		for (Chain chain : this.chains) {
			if (chain.getId() == chainId) retChain = chain;
		}
		return retChain;
	}

	/**
	 * This method adds a event to the scan description. 
	 * 
	 * @param event The event that should be added to the scan description.
	 * @return Returns true if the event was added and false if not.
	 */
	public boolean add( final Event event ) {
		this.eventsMap.put( event.getID(), event );
		//boolean returnValue = events.add( event );
		final Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}	
		return true;
	}

	/**
	 * Gives back the correpondenting Event for the given id.
	 * 
	 * @param id A id of a event.
	 * @return The Event or null if it was not found.
	 */
	public Event getEventById( final String id ) {
		return this.eventsMap.get( id );
	}
	
	/**
	 * Convenience method 
	 * 
	 * @param id A id of a event.
	 * @return true if successful
	 */
	public boolean removeEventById( final String id ) {
		return remove( getEventById(id) );
	}
	/**
	 * returns a default start event for chains without startevent tag
	 * this is a hack to not break existing code
	 * @return the default StartEvent
	 */
	public Event getDefaultStartEvent() {
		return this.getEventById("S-0-0-E");
	}
	/**
	 * Gives back a copy of the internal list, that is holding all events.
	 * 
	 * @return A copy of the internal list that is holding all events. Never returns null!
	 */
	public List< Event > getEvents() {
		return new ArrayList< Event >( this.eventsMap.values() );
	}
	
	/**
	 * This method returns the used measuring station of this scan description.
	 * 
	 * @return The used measuring station. Never returns 'null'.
	 */
	public IMeasuringStation getMeasuringStation() {
		return this.measuringStation;
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener#updateEvent(de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent)
	 */
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		final Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			final IModelUpdateListener modelUpdateListener = it.next();
			modelUpdateListener.updateEvent( new ModelUpdateEvent( this, modelUpdateEvent ) ); 
			//it.next().updateEvent( new ModelUpdateEvent( this, modelUpdateEvent ) );
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

	@Override
	public List<IModelError> getModelErrors() {
		final List< IModelError > errorList = new ArrayList< IModelError >();
		final Iterator< Chain > it = this.chains.iterator();
		while( it.hasNext() ) {
			errorList.addAll( it.next().getModelErrors() );
		}
		return errorList;
	}
	
}
