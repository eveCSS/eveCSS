/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.errors.ChannelError;
import de.ptb.epics.eve.data.scandescription.errors.ChannelErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessage;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessageEnum;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class describes the behavior of a detector during the main phase.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class Channel extends AbstractMainPhaseBehavior {

	/**
	 * This attribute controls how often the detector should be read to make
	 * an average value. Voreinstellung ist 1.
	 */
	private int averageCount = 1;
	
	/**
	 * The parent scan module of this channel.
	 */
	private ScanModule parentScanModul;
	
	/**
	 * The max deviation of this channel.
	 */
	private double maxDeviation = Double.NEGATIVE_INFINITY;
	
	/**
	 * The minimum for this channel.
	 */
	private double minumum = Double.NEGATIVE_INFINITY;
	
	/**
	 * The maximum attempts for reading the channel.
	 */
	private int maxAttempts = Integer.MIN_VALUE;

	/**
	 * A flag if a trigger of this detector must be confirmed.
	 */
	private boolean confirmTrigger = false;
	
	/**
	 * A flag for repeat on redo.
	 */
	private boolean repeatOnRedo = false;
	
	/**
	 * The detector ready event.
	 */
	private Event detectorReadyEvent;

	/**
	 * A list of the ControlEvents, that holds the configuration for the redo events.
	 * Beides hinzugefügt analog zu ScanModule.java (Hartmut 19.11.09)
	 */
	private List< ControlEvent > redoEvents;
	
	/**
	 * This control event manager controls the redo events.
	 */
	private ControlEventManager redoControlEventManager;
	
	/**
	 * A List that is holding all object that needs to get an update message if this object was updated.
	 */
	private List< IModelUpdateListener > updateListener;

	/**
	 * This constructor creates a new channel.
	 * 
	 * @param parentScanModul The parent scan module. Must not be 'null'.
	 */
	public Channel( final ScanModule parentScanModul ) {
		if( parentScanModul == null ) {
			throw new IllegalArgumentException( "The parameter 'parentScanModul' must not be null!" );

		}
		this.parentScanModul = parentScanModul;

		this.redoEvents = new ArrayList< ControlEvent >();
		this.redoControlEventManager = new ControlEventManager( this, this.redoEvents, ControlEventTypes.CONTROL_EVENT );
		this.redoControlEventManager.addModelUpdateListener( this );
		this.updateListener = new ArrayList< IModelUpdateListener>();

	}

	/**
	 * Adds a redo event to the detector. 
	 * 
	 * @param redoEvent The redo event that should be added to the scan module.
	 * @return Gives back 'true' if the event has been added and false if not.
	 */
	public boolean addRedoEvent( final ControlEvent redoEvent ) {
		if( this.redoEvents.add( redoEvent ) ) {
			final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
			while( updateIterator.hasNext() ) {
				updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
			}
			redoEvent.addModelUpdateListener( this.redoControlEventManager );
			this.redoControlEventManager.updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( redoEvent, ControlEventMessageEnum.ADDED ) ) );
			return true;
		} 
		return false;
	}

	/**
	 * Removes a redo event from the scan module.
	 * 
	 * @param redoEvent The redo event that should be removed from the scan module.
	 * @return Gives back 'true' if the event has been removed and false if not.
	 */
	public boolean removeRedoEvent( final ControlEvent redoEvent ) {
		if( this.redoEvents.remove( redoEvent ) ) {
			final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
			while( updateIterator.hasNext() ) {
				updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
			}
			this.redoControlEventManager.updateEvent( new ModelUpdateEvent( this, new ControlEventMessage( redoEvent, ControlEventMessageEnum.REMOVED ) ) );
			redoEvent.removeModelUpdateListener( this.redoControlEventManager );
			return true;
		} 
		return false;
	}

	/**
	 * This method returns an iterator over all redo events.
	 * 
	 * @return 
	 */
	public Iterator< ControlEvent > getRedoEventsIterator() {
		return this.redoEvents.iterator();
	}
	
	/**
	 * Gives back how often the detector should be read to make an average
	 * result for the measuring.
	 * 
	 * @return An non negative integer.
	 */
	public int getAverageCount() {
		return this.averageCount;
	}
	
	/**
	 * Sets how often the detector should be read to make an average result
	 * for the measuring.
	 * 
	 * @param averageCount How often the detector should be read. Must be at least 0.
	 */
	public void setAverageCount( final int averageCount ) {
		if( averageCount < 0 ) {
			throw new IllegalArgumentException( "The average must be larger than 0." );
		}
		this.averageCount = averageCount;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * This method returns the parent scan module of this channel.
	 * 
	 * @return The parent scan module.
	 */
	public ScanModule getParentScanModul() {
		return this.parentScanModul;
	}

	/**
	 * This method returns if a trigger of this detector must be confirmed.
	 * 
	 * @return Returns 'true' if a trigger of this trigger must be confirmed and 'false' if not.
	 */
	public boolean isConfirmTrigger() {
		return confirmTrigger;
	}

	/**
	 * This method sets if a trigger of this channel must be confirmed.
	 * 
	 * @param confirmTrigger Pass 'true' if a trigger of this channel must be confirmed and 'false' if not.
	 */
	public void setConfirmTrigger( final boolean confirmTrigger ) {
		this.confirmTrigger = confirmTrigger;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * This method returns the maximum attempts to read the detector.
	 * 
	 * @return The maximum attempts to read the detector.
	 */
	public int getMaxAttempts() {
		return maxAttempts;
	}

	/**
	 * This method sets the maximum attempts to read the detector.
	 * 
	 * @param maxAttempts The maximum attempts to read the detector.
	 */
	public void setMaxAttempts( final int maxAttempts ) {
		this.maxAttempts = maxAttempts;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * This method returns the maximum deviation between the taken values.
	 *  
	 * @return The maximum deviation.
	 */
	public double getMaxDeviation() {
		return this.maxDeviation;
	}

	/**
	 * This method sets the maximum deviation.
	 * 
	 * @param maxDeviation The new maximimum deviation.
	 */
	public void setMaxDeviation( final double maxDeviation ) {
		this.maxDeviation = maxDeviation;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * This method returns the minimum.
	 * 
	 * @return The minimum for this channel.
	 */
	public double getMinumum() {
		return this.minumum;
	}

	/**
	 * This method sets the minimum for this channel.
	 * 
	 * @param minumum The new minimum for this channel.
	 */
	public void setMinumum( final double minumum ) {
		this.minumum = minumum;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * This method returns if the channel should repeat on redo.
	 * 
	 * @return Returns 'true' if the channel repeats reading on a redo event.
	 */
	public boolean isRepeatOnRedo() {
		return repeatOnRedo;
	}

	/**
	 * This methods sets if the read of the detector should be repeated on a redo event.
	 * 
	 * @param repeatOnRedo Pass 'true' if the detector read should be repeated on a redo event.
	 */
	public void setRepeatOnRedo( final boolean repeatOnRedo ) {
		this.repeatOnRedo = repeatOnRedo;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * This method returns the detector ready event.
	 * 
	 * @return The detector ready event.
	 */
	public Event getDetectorReadyEvent() {
		return detectorReadyEvent;
	}
	
	/**
	 * 
	 * @return true if channel sends an ready event
	 */
	public Boolean hasReadyEvent() {
		if (detectorReadyEvent != null )
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param detectorReadyEvent add a detectorReadyEvent
	 */
	public void setDetectorReadyEvent( final Event detectorReadyEvent ) {
		this.detectorReadyEvent = detectorReadyEvent;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * This method returns 
	 * 
	 * @return
	 */
	public ControlEventManager getRedoControlEventManager() {
		return redoControlEventManager;
	}
	
	/**
	 * Sets the detector channel that will be controlled by this behavior.
	 * 
	 * @param detectorChannel The detector channel that will be controlles by this behavior.
	 */
	public void setDetectorChannel( final DetectorChannel detectorChannel ) {
		this.abstractDevice = detectorChannel;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Gives back the detector channel that is controlled by this behavior.
	 * 
	 * @return The detector channel that is controlled by this behavior.
	 */
	public DetectorChannel getDetectorChannel() {
		return (DetectorChannel)this.abstractDevice;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider#getModelErrors()
	 */
	@Override
	public List<IModelError> getModelErrors() {
		final List< IModelError > modelErrors = new ArrayList< IModelError >();
		// TODO von Hartmut: maxDeviation und minimum liefern keine ChannelErrors mehr!
		if( this.maxDeviation != Double.NEGATIVE_INFINITY  && !this.getDetectorChannel().getRead().isValuePossible( "" + this.maxDeviation ) ) {
			modelErrors.add( new ChannelError( this, ChannelErrorTypes.MAX_DEVIATION_NOT_POSSIBLE ) );
		}
		if( this.minumum != Double.NEGATIVE_INFINITY  && !this.getDetectorChannel().getRead().isValuePossible( "" + this.minumum ) ) {
			modelErrors.add( new ChannelError( this, ChannelErrorTypes.MINIMUM_NOT_POSSIBLE ) );
		}
		if( this.redoControlEventManager.getModelErrors().size() > 0 ) {
			modelErrors.addAll( this.redoControlEventManager.getModelErrors() );
			modelErrors.add( new ChannelError( this, ChannelErrorTypes.PLUGIN_ERROR ) );
		}
		return modelErrors;
	}
}
