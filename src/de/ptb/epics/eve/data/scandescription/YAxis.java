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

import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
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
public class YAxis implements IModelUpdateListener, IModelUpdateProvider {

	/**
	 * The detector channel that will be used for the value of the y-Axis.
	 */
	private DetectorChannel detectorChannel;
	
	/**
	 * The plot mode of the y-Axis
	 */
	private PlotModes mode = PlotModes.LINEAR;
	
	/**
	 * A detector channel that should be used to normalize the value.
	 */
	private DetectorChannel normalizeChannel;
	
	/**
	 * The linestyle
	 */
	private String linestyle = "";
	
	/**
	 * The style of the points
	 */
	private String markstyle = "";
	
	/**
	 * The color.
	 */
	private String color = "";

	/**
	 * A List that is holding all object that needs to get an update message if this object was updated.
	 */
	private List< IModelUpdateListener > updateListener;
	
	/**
	 * Gives back the color of the line and markpoints of this axis.
	 * 
	 * @return The color of the line and markpoints.
	 */
	public String getColor() {
		return this.color;
	}

	/**
	 * This constructor creates a new y axis.
	 */
	public YAxis() {
		this.updateListener = new ArrayList< IModelUpdateListener >();
	}
	
	/**
	 * Sets the color of the line and markpoints of this axis.
	 * 
	 * @param color The color of the line an markpoints.
	 */
	public void setColor( final String color ) {
		this.color = color;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the stype of the line.
	 * 
	 * @return The style of the line.
	 */
	public String getLinestyle() {
		return this.linestyle;
	}

	/**
	 * Sets the style of the line.
	 * 
	 * @param linestyle The style of the line.
	 */
	public void setLinestyle( final String linestyle ) {
		this.linestyle = linestyle;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the style of the markpoints.
	 * 
	 * @return The style of the markponints.
	 */
	public String getMarkstyle() {
		return this.markstyle;
	}

	/**
	 * Sets the style of the markpoints.
	 * 
	 * @param markstyle The style of the mark points
	 */
	public void setMarkstyle( final String markstyle ) {
		this.markstyle = markstyle;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the plot mode of the axis.
	 * 
	 * @return The plot mode of the axis.
	 */
	public PlotModes getMode() {
		return this.mode;
	}

	/**
	 * Sets the plot mode of the axis.
	 * 
	 * @param mode The plot mode of the axis.
	 */
	public void setMode( final PlotModes mode ) {
		this.mode = mode;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the detector channel that is used for this axis.
	 * 
	 * @return The detector channel what is used
	 */
	public DetectorChannel getDetectorChannel() {
		return detectorChannel;
	}

	/**
	 * Sets the detector channel that will be used to get the values for the axis.
	 * @param detectorChannel
	 */
	public void setDetectorChannel( final DetectorChannel detectorChannel ) {
		this.detectorChannel = detectorChannel;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the detector channel that is used for normalization.
	 * 
	 * @return The detector channel that is used for normalization.
	 */
	public DetectorChannel getNormalizeChannel() {
		return normalizeChannel;
	}

	/**
	 * Clear the detector channel that is used for normalization.
	 * 
	 */
	public void clearNormalizeChannel() {
		this.normalizeChannel = null;
	}

	/**
	 * Sets the detector channel that is used for normalization.
	 * 
	 * @param normalizeChannel The detector channel that is used for normalization.
	 */
	public void setNormalizeChannel( final DetectorChannel normalizeChannel ) {
		this.normalizeChannel = normalizeChannel;
		final Iterator<IModelUpdateListener> updateIterator = this.updateListener.iterator();
		while( updateIterator.hasNext() ) {
			updateIterator.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
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
	
}
