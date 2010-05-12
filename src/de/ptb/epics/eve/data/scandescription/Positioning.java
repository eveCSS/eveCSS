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

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PositioningError;
import de.ptb.epics.eve.data.scandescription.errors.PositioningErrorTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class represents a positioning behavior.
 * 
 * A position occurs after a scan. A motor axis is moved to a position, calculated by detector data.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class Positioning extends AbstractBehavior {

	/**
	 * The detector channel that delivers data to calculate the position of the motor axis.
	 */
	private DetectorChannel detectorChannel;
	
	/**
	 * The detector channel that is used to normalize the detector data.
	 */
	private DetectorChannel normalization;
	
	/**
	 * The plug in controller for the plug in that calculates the motor position with the detector data.
	 */
	private PluginController pluginController;
	
	/**
	 * This constructor creates a new Positioning object.
	 */
	public Positioning() {
		this( null );
	}

	/**
	 * This constructor creates a new Positioning with a given motor axis.
	 * 
	 * @param motorAxis The motor axis whose position is modified.
	 */
	public Positioning( final MotorAxis motorAxis ) {
		this( motorAxis, null );
	}
	
	/**
	 * This constructors creates a new Positioning with a given motor axis and detector channel.
	 * 
	 * @param motorAxis The motor axis whose position is modified. 
	 * @param detectorChannel The detector channel that is is used to calculate the new position.
	 */
	public Positioning( final MotorAxis motorAxis, final DetectorChannel detectorChannel ) {
		this( motorAxis, detectorChannel, null );
	}
	
	/**
	 * This constructors creates a new Positioning with a given motor axis, detector channel, and detector channel for normalization.
	 * 
	 * @param motorAxis The motor axis whose position is modified. 
	 * @param detectorChannel The detector channel that is is used to calculate the new position.
	 * @param normalization The detector channel that is used to normalize the values of the other detector channel.
	 */
	public Positioning( final MotorAxis motorAxis, final DetectorChannel detectorChannel, final DetectorChannel normalization ) {
		this( motorAxis, detectorChannel, normalization, null );
	}
	
	/**
	 * This constructors creates a new Positioning with a given motor axis, detector channel, detector channel for normalization, and a plug in.
	 * 
	 * @param motorAxis The motor axis whose position is modified. 
	 * @param detectorChannel The detector channel that is is used to calculate the new position.
	 * @param normalization The detector channel that is used to normalize the values of the other detector channel.
	 * @param plugin The plug in that calculates the new position for the motor axis.
	 */
	public Positioning( final MotorAxis motorAxis, final DetectorChannel detectorChannel, final DetectorChannel normalization, final PlugIn plugin ) {
		this.abstractDevice = motorAxis;
		this.detectorChannel = detectorChannel;
		this.normalization = normalization;
		this.pluginController = new PluginController();
		if( plugin != null ) {
			this.pluginController.setPlugin( plugin );
		}
		
		this.pluginController.addModelUpdateListener( this );
	}

	/**
	 * This method returns the detector channel of this Positioning.
	 * 
	 * @return The detector channel of this positioning.
	 */
	public DetectorChannel getDetectorChannel() {
		return this.detectorChannel;
	}

	/**
	 * This method sets the detector channel of this positioning.
	 * 
	 * @param detectorChannel The detector channel of this positioning.
	 */
	public void setDetectorChannel( final DetectorChannel detectorChannel ) {
		this.detectorChannel = detectorChannel;
		final Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * This methods returns the detector channel that is used to normalize the values of the other detector channel.
	 * 
	 * @return The detector channel that used nor normalize the values of the other detector channel.
	 */
	public DetectorChannel getNormalization() {
		return this.normalization;
	}

	/**
	 * This method sets the detector channel that is used to normalize the values of the other detector channel.
	 * 
	 * @param normalization The other detector channel that is used to normalize the values of the other detector channel.
	 */
	public void setNormalization( final DetectorChannel normalization ) {
		this.normalization = normalization;
		final Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * This method returns the plug in controller that is used to control the plug in that calculates the motor axis position out of the detect channel data.
	 * 
	 * @return The plug in controller that controles the plug in that calculates the new motor axis position.
	 */
	public PluginController getPluginController() {
		return this.pluginController;
	}
	
	/**
	 * This method return the controlled motor axis.
	 * 
	 * @return The controlled motor axis.
	 */
	public MotorAxis getMotorAxis() {
		return (MotorAxis)this.abstractDevice;
	}
	
	/**
	 * This method sets the controlled motor axis.
	 * 
	 * @param motorAxis The controlled motor axis.
	 */
	public void setMotorAxis( final MotorAxis motorAxis ) {
		this.abstractDevice = motorAxis;
		final Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider#getModelErrors()
	 */
	@Override
	public List< IModelError > getModelErrors() {
		final List< IModelError > modelErrors = new ArrayList< IModelError >();
		if( this.detectorChannel == null ) {
			modelErrors.add( new PositioningError( this, PositioningErrorTypes.NO_DETECTOR_CHANNEL_SET ) );
		}
		modelErrors.addAll( this.pluginController.getModelErrors() );
		return modelErrors;
	}
}
