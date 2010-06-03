/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PlotWindowError;
import de.ptb.epics.eve.data.scandescription.errors.PlotWindowErrorTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class decribes a plot window.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class PlotWindow implements IModelUpdateListener, IModelUpdateProvider {

	/**
	 * The motor axis that should be used for the values for the x-axis.
	 */
	private MotorAxis xAxis;
	
	/**
	 * A List that contains all yAxis.
	 */
	private List< YAxis > yAxis;
	
	/**
	 * The id of the plot window.
	 */
	private int id;
	
	/**
	 * The plot mode of the plot window.
	 */
	private PlotModes mode;
	
	/**
	 * Trigger if the plot window should be initialized or not.
	 */
	private boolean init;
	
	/**
	 * A List that is holding all object that needs to get an update message if this object was updated.
	 */
	private List< IModelUpdateListener > updateListener;
	
	/**
	 * This constructor constructs a new PlotWindow.
	 *
	 */
	public PlotWindow() {
		this.updateListener = new ArrayList< IModelUpdateListener >();
		this.yAxis = new ArrayList< YAxis >();
		this.mode = PlotModes.LINEAR;
	}
	
	/**
	 * Gives back the id of the plot window.
	 * 
	 * @return
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Sets the id of the PlotWindow.
	 * 
	 * @param id An integer larger than 0.
	 */
	public void setId( final int id ) {
		if( id < 1 ) {
			throw new IllegalArgumentException( "The parameter 'id' must be larger than 0." );
		}
		this.id = id;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
		
	}
	
	/**
	 * Gives back if the plot window will be initialized or not.
	 * 
	 * @return True or false.
	 */
	public boolean isInit() {
		return this.init;
	}
	
	/**
	 * Sets if the plot windows should be initialized.
	 * 
	 * @param init Pass 'true' if the plot window should be initialized.
	 */
	public void setInit( final boolean init ) {
		this.init = init;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Gives back the plot mode of this plot window.
	 * 
	 * @return The plot mode of this plot window. Never returns null.
	 */
	public PlotModes getMode() {
		return this.mode;
	}
	
	/**
	 * Sets the plot mode of this plot window.
	 * 
	 * @param mode The plot mode of this plot window. Must not be null!
	 */
	public void setMode( final PlotModes mode ) {
		if( mode == null ) {
			throw new IllegalArgumentException( "The parameter 'mode' must not be null!" );
		}
		this.mode = mode;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Gives back the motor axis that is used for the x-axis of the plot window.
	 * 
	 * @return The motor axis that is used for the x-axis.
	 */
	public MotorAxis getXAxis() {
		return this.xAxis;
	}
	
	/**
	 * Sets the motor axis that should be used for the x-axis.
	 * 
	 * @param axis The motor axis that should be used for the x-axis or null.
	 */
	public void setXAxis( final MotorAxis axis ) {
		this.xAxis = axis;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * This method adds a y-axis to the plot window.
	 * 
	 * @param yAxis The y-axis to add. must not be null.
	 */
	public void addYAxis( final YAxis yAxis ) {
		if( yAxis == null ) {
			throw new IllegalArgumentException( "The parameter 'yAxis' must not be null!" );
		}
		yAxis.addModelUpdateListener( this );
		this.yAxis.add( yAxis );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * This method removes a y-axis from the plot window.
	 * 
	 * @param yAxis The y-axis to remove. Must not be null!
	 */
	public void removeYAxis( final YAxis yAxis ) {
		if( yAxis == null ) {
			throw new IllegalArgumentException( "The parameter 'yAxis' must not be null!" );
		}
		yAxis.removeModelUpdateListener( this );
		this.yAxis.remove( yAxis );
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * This method returns an iterator over all y-axes.
	 * 
	 * @return An iterator over all y-axes.
	 */
	public Iterator< YAxis > getYAxisIterator() {
		return this.yAxis.iterator();
	}
	/**
	 * get a list of y-axes
	 * 
	 * @return a list of y-Axes
	 */
	public List<YAxis> getYAxes() {
		return new ArrayList<YAxis>( this.yAxis );
	}

	/**
	 * This method return the amount of y-axes.
	 * 
	 * @return The amount of y-axes of this plot window.
	 */
	public int getYAxisAmount() {
		return this.yAxis.size();
	}

	/**
	 * This method removes all y-axes of this plot window.
	 * 
	 */
	public void clearYAxis() {
		Iterator< YAxis > it = this.yAxis.iterator();
		while( it.hasNext() ) {
			YAxis yAxis = it.next();
			yAxis.removeModelUpdateListener( this );
		}
		this.yAxis.clear();
		
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener#updateEvent(de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent)
	 */
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		final Iterator< IModelUpdateListener > it = this.updateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, modelUpdateEvent ) );
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider#addModelUpdateListener(de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener)
	 */
	public boolean addModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.updateListener.add( modelUpdateListener );
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider#removeModelUpdateListener(de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener)
	 */
	public boolean removeModelUpdateListener( final IModelUpdateListener modelUpdateListener ) {
		return this.updateListener.remove( modelUpdateListener );
	}

	public List< IModelError> getModelErrors() {
		final List< IModelError > modelErrors = new ArrayList< IModelError >();
		if( this.xAxis == null ) {
			modelErrors.add( new PlotWindowError( this, PlotWindowErrorTypes.NO_X_AXIS_SET ) );
		}
		if( this.getYAxisAmount() == 0 ) {
			modelErrors.add( new PlotWindowError( this, PlotWindowErrorTypes.NO_Y_AXIS_SET ) );
		}
		
		return modelErrors;
	}
	
}
