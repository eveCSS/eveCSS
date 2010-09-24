package de.ptb.epics.eve.data.measuringstation.filter;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;


/**
 * This class represents the base for all filter of measuring station descriptions.
 * The filter it self represents a measuring station description that can be used
 * in all parts of the application.
 * 
 * The filter mechanism is implemented in the updateEvent Method.
 * 
 * @author Stephan Rehfeld
 *
 */
public abstract class MeasuringStationFilter implements IMeasuringStation, IModelUpdateListener {

	/**
	 * The source measuring station to filter. 
	 * 
	 */
	private IMeasuringStation source;
	
	/**
	 * The constructor
	 */
	public MeasuringStationFilter() {
	}
	
	/**
	 * The constructor with a given source.
	 * 
	 * @param source The given source.
	 */
	public MeasuringStationFilter( final IMeasuringStation source ) {
		this.source = source;
		if( source != null ) {
			this.source.addModelUpdateListener( this );
		}
	}
	
	/**
	 * This method returns the current source.
	 * 
	 * @return
	 */
	public IMeasuringStation getSource() {
		return this.source;
	}
	
	/**
	 * Sets the current source. If the currents source is changes an update
	 * event will indicate a change at the current filter and will be propagated
	 * to a following filter.
	 * 
	 * @param source
	 */
	public void setSource( final IMeasuringStation source ) {
		if( this.source != null ) {
			this.source.removeModelUpdateListener( this );
		}
		this.source = source;
		if( this.source != null ) {
			this.source.addModelUpdateListener( this );
		}
		this.updateEvent( new ModelUpdateEvent( this, null ) );
	}
	
}
