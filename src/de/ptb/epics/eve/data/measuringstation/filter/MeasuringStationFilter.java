package de.ptb.epics.eve.data.measuringstation.filter;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

public abstract class MeasuringStationFilter implements IMeasuringStation, IModelUpdateListener {

	private IMeasuringStation source;
	
	public MeasuringStationFilter() {
	}
	
	public MeasuringStationFilter( final IMeasuringStation source ) {
		this.source = source;
		if( source != null ) {
			this.source.addModelUpdateListener( this );
		}
	}
	
	public IMeasuringStation getSource() {
		return this.source;
	}
	
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
