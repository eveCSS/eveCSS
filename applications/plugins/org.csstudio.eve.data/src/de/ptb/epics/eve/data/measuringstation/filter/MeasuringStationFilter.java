package de.ptb.epics.eve.data.measuringstation.filter;

import de.ptb.epics.eve.data.measuringstation.AbstractMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * Represents the base for all filters of measuring station descriptions.
 * The filter itself is a measuring station description that can be used
 * in all parts of the application.
 * 
 * The filter mechanism is implemented in the updateEvent Method.
 * 
 * @author Stephan Rehfeld
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public abstract class MeasuringStationFilter extends AbstractMeasuringStation implements 
									IMeasuringStation, IModelUpdateListener {

	// the measuring station the filter is based on
	private IMeasuringStation source;
	
	/**
	 * default constructor
	 */
	public MeasuringStationFilter() {
	}
	
	/**
	 * Constructs a <code>MeasuringStationFilter</code>.
	 * 
	 * @param source the measuring station the filter is based on
	 */
	public MeasuringStationFilter(final IMeasuringStation source) {
		this.source = source;
		if(source != null) {
			this.source.addModelUpdateListener(this);
		}
	}
	
	/**
	 * Returns the measuring station the filter is based on.
	 * 
	 * @return the measuring station the filter is based on
	 */
	public IMeasuringStation getSource() {
		return this.source;
	}
	
	/**
	 * Sets the measuring station the filter should be based on.
	 * 
	 * @param source the measuring station the filter should be based on
	 */
	public void setSource(final IMeasuringStation source) {
		if(this.source != null) {
			this.source.removeModelUpdateListener(this);
		}
		this.source = source;
		if(this.source != null) {
			this.source.addModelUpdateListener(this);
		}
		this.updateEvent(new ModelUpdateEvent(this, null));
	}
}