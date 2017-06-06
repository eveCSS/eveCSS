package de.ptb.epics.eve.data.measuringstation.filter;

import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.AbstractMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * Represents the base for all filters of measuring station descriptions.
 * The filter itself is a measuring station description that can be used
 * in all parts of the application.
 * <p>
 * The filter mechanism is implemented in the updateEvent Method.
 * <p>
 * If this Filter is not used anymore (should be Garbage Collected) it is 
 * necessary to call {@link MeasuringStationFilter#setSource(IMeasuringStation)}
 * with <code>null</code> argument.
 * 
 * @author Stephan Rehfeld
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public abstract class MeasuringStationFilter extends AbstractMeasuringStation implements 
									IModelUpdateListener {
	private IMeasuringStation source;
	protected final List<AbstractDevice> excludeList;
	protected final List<IModelUpdateListener> modelUpdateListener;
	
	/**
	 * default constructor
	 */
	public MeasuringStationFilter() {
		super();
		this.excludeList = new ArrayList<AbstractDevice>();
		this.modelUpdateListener = new ArrayList<IModelUpdateListener>();
	}
	
	/**
	 * Constructs a <code>MeasuringStationFilter</code>.
	 * 
	 * @param source the measuring station the filter is based on
	 */
	public MeasuringStationFilter(final IMeasuringStation source) {
		this();
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
	 * <p>
	 * If this Filter is not used anymore (should be Garbage Collected) it is 
	 * necessary to call this method with <code>null</code> argument.
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
	
	/**
	 * {@inheritDoc}
	 */
	public String getVersion() {
		return this.getSource() != null
				? this.getSource().getVersion()
				: "";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLoadedFileName(){
		return this.getSource() != null
				? this.getSource().getLoadedFileName()
				: "";
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String getSchemaFileName(){
		return this.getSource() != null
				? this.getSource().getSchemaFileName()
				: "";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.getSource().getName();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.modelUpdateListener.add(modelUpdateListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.modelUpdateListener.remove(modelUpdateListener);
	}
}