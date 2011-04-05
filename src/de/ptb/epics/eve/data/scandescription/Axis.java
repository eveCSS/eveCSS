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

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.AxisErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class describes a behavior of an axis during a main phase of a
 * Scan Modul.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.4
 */
public class Axis extends AbstractMainPhaseBehavior {

	
	/**
	 * The step function of this axis.
	 */
	private Stepfunctions stepfunction;

	/**
	 * If a position file is used, the path to it will be saved in this attribute.
	 */
	private String positionfile;
	
	/**
	 * If neither a positionfile or a position plugin is used, the start value will
	 * be saved in this attribute.
	 */
	private String start;
	
	/**
	 * If neither a positionfile or a position plugin is used, the stop value will
	 * be saved in this attribute.
	 */
	private String stop;
	
	/**
	 * If neither a positionfile or a position plugin is used, the stepwidth value will
	 * be saved in this attribute.
	 */
	private String stepwidth;
	
	/**
	 *  stepcount.
	 */
	private double stepcount;
	
	/**
	 * Indicates if the position datas are absolute or relative to axis position at the beginning of the scan module.
	 */
	private PositionMode positionMode = PositionMode.ABSOLUTE;
	
	/**
	 * The optional position list of this axis.
	 */
	private String positionlist;
	
	/**
	 * This attribute indicates if this axis is the main axis of the scan module.
	 */
	private boolean isMainAxis = false;
	private ScanModule scanModul;
	
	/**
	 * The optional plug in controller of this axis.
	 */
	private PluginController positionPluginController;
	
	public Axis( final ScanModule scanModul ) {
		if( scanModul == null ) {
			throw new IllegalArgumentException( "The parameter 'scanModul' must not be null!" );
		}
		this.scanModul = scanModul;
	}
	
	public ScanModule getScanModul() {
		return this.scanModul;
	}
	/**
	 * Gives back the position file if one is setted.
	 * 
	 * @return A String-object, containg the location of the position file or null if it's not set.
	 */
	public String getPositionfile() {
		return this.positionfile;
	}

	/**
	 * Sets the location of a position file.
	 * 
	 * @param positionfile A String object, containing the location of a position File or null if a positionfile is not used.
	 */
	public void setPositionfile( final String positionfile ) {
		this.positionfile = positionfile;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * This method returns the controller for the position plugin if a plugin is used to control this axis.
	 * 
	 * @return The controller for the position plugin or 'null' if it's not set.
	 */
	public PluginController getPositionPluginController() {
		return this.positionPluginController;
	}

	/**
	 * This methods sets the plugin controller to control the axis.
	 * 
	 * @param positionPluginController The plugin controller to control the axis. Maybe 'null'
	 */
	public void setPositionPluginController( final PluginController positionPluginController ) {
		if( this.positionPluginController != null ) {
			this.positionPluginController.removeModelUpdateListener( this );
		}
		this.positionPluginController = positionPluginController;
		if( this.positionPluginController != null ) {
			this.positionPluginController.addModelUpdateListener( this );
		}

		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the start value, so where the axis should begin at the
	 * beginning of a ScanModul.
	 * 
	 * @return A String object containing the start position or null.
	 */
	public String getStart() {
		return this.start;
	}

	/**
	 * Sets the start value, so where the Axis should start at the begin
	 * of the ScanModul.
	 * 
	 * @param start The start value that should be used or null if it's not used.
	 */
	public void setStart( final String start ) {
		this.start = start;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the stepfunction that will be used to determine the positions of
	 * the axis while running the Scan Modul. 
	 * 
	 * @return A String object, that contains the stepfunction.
	 */
	public String getStepfunctionString() {
		return Stepfunctions.stepfunctionToString( stepfunction );
	}

	/**
	 * return the stepfunction 
	 * 
	 * @return stepfunction enum.
	 */
	public Stepfunctions getStepfunctionEnum() {
		return stepfunction;
	}

	/**
	 * Sets the stepfunction that is used for moving the axis.
	 *  
	 * @param stepfunctionString A Stringobject containing the name of the stepfunction that should be used. Must not be null!
	 */
	public void setStepfunction( final String stepfunctionString ) {
		if( stepfunctionString == null ) {
			throw new IllegalArgumentException( "The parameter stepfunction must not be null!" );
		}
		this.stepfunction = Stepfunctions.stepfunctionToEnum( stepfunctionString );
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the stepwidth, so how far the axis is moving at every step.
	 * 
	 * @return A String object, containg the stepwidth or null if it's not used.
	 */
	public String getStepwidth() {
		return this.stepwidth;
	}

	/**
	 * Sets the stepwidth.
	 * 
	 * @param stepwidth A String object containing the stepwidth or null if it's not used.
	 */
	public void setStepwidth( final String stepwidth ) {
		this.stepwidth = stepwidth;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the position where the axis should finally stop during the ScanModul.
	 * 
	 * @return Returns a String object that contains the final position of the axis or gives back null.
	 */
	public String getStop() {
		return this.stop;
	}

	/**
	 * Sets the stop position of the motor axis.
	 * 
	 * @param stop A String object or null.
	 */
	public void setStop( final String stop ) {
		this.stop = stop;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * Gives back the motor axis that is controlles by this behavior.
	 * 
	 * @return The motor axis that is controlled by this behavior.
	 */
	public MotorAxis getMotorAxis() {
		return (MotorAxis)this.abstractDevice;
	}
	
	/**
	 * Sets the motor axis, that is controlles by this behavior.
	 * 
	 * @param motorAxis The motor axis that is controlled by this behavior.
	 */
	public void setMotorAxis( final MotorAxis motorAxis ) {
		this.abstractDevice = motorAxis;

		this.setStepfunction("Add");
		String formattedText = this.getDefaultValue();
		this.setStart(formattedText);
		this.setStop(formattedText);
		this.setStepwidth("0");
		
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * This method gives back the stepcount.
	 * 
	 * @return The amount of steps.
	 */
	public double getStepCount() {
		return this.stepcount;
	}
	
	/**
	 * This method sets the stepcount.
	 * 
	 * @param stepcount number of steps.
	 */
	public void setStepCount( final double stepcount ) {
		this.stepcount = stepcount;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}
	
	/**
	 * This method returns the position list if it's set.
	 * 
	 * @return Returns the position list or 'null' if it's set.
	 */
	public String getPositionlist() {
		return this.positionlist;
	}
	
	/**
	 * This method sets the position list.
	 * 
	 * @param positionlist The new position list or 'null'.
	 */
	public void setPositionlist( final String positionlist ) {
		this.positionlist = positionlist;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back if this axis is the main axis of the scan module.
	 * 
	 * @return Gives back 'true' if this axis is the main axis of the scan module and 'false' if not.
	 */
	public boolean isMainAxis() {
		return this.isMainAxis;
	}

	/**
	 * Sets if this axis is the main axis of the scan module.
	 * 
	 * @param isMainAxis Pass 'true' is this axis is the main axis of the scan module and 'false' if not.
	 */
	public void setMainAxis( final boolean isMainAxis ) {
		this.isMainAxis = isMainAxis;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * Gives back the postition mode for this axis.
	 * 
	 * @return The position mode for this axis.
	 */
	public PositionMode getPositionMode() {
		return this.positionMode;
	}

	/**
	 * Sets the position mode of this axis.
	 * 
	 * @param frameOfReference The position mode for this axis.
	 */
	public void setPositionMode( final PositionMode positionMode ) {
		this.positionMode = positionMode;
		Iterator< IModelUpdateListener > it = this.modelUpdateListener.iterator();
		while( it.hasNext() ) {
			it.next().updateEvent( new ModelUpdateEvent( this, null ) );
		}
	}

	/**
	 * This method finds out if a value is possible for this. behavior. It can
	 * be used to precheck the values for start, stop and stepwidth.
	 * 
	 * @param value The value that should be checked. must not be null.
	 * @return Gives back 'true' if the value is possible and 'false' if not.
	 */
	public boolean isValuePossible( final String value ) {
		return this.getMotorAxis().isValuePossible( value );
	}
	
	/**
	 * Return a well-formatted string with a valid value for the datatype.
	 * If value can not be converted, return a default value
	 * 
	 * @param value The value that will be formatted.
	 * @return a well-formatted string with a valid value
	 */
	public String formatValueDefault( final String value ) {
		return this.getMotorAxis().formatValueDefault( value );
	}

	/**
	 * Return a well-formatted string with a valid value for the datatype.
	 * If value can not be converted, return null
	 * 
	 * @param value The value that will be formatted.
	 * @return a well-formatted string or null
	 */
	public String formatValue( final String value ) {
		return this.getMotorAxis().formatValue( value );
	}

	/**
	 * Return a well-formatted string with a valid value for the datatype.
	 * If value can not be converted, return a default value
	 * 
	 * @return a well-formatted string with a valid default value
	 */
	public String getDefaultValue() {
		return this.getMotorAxis().getDefaultValue( );
	}
	
	/**
	 * This method returns the type of the data type of the motor axis.
	 * 
	 * @return The data type of the motor axis.
	 */
	public DataTypes getType() {
		return this.getMotorAxis().getType();
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider#getModelErrors()
	 */
	@Override
	public List<IModelError> getModelErrors() {
		final List< IModelError > errorList = new ArrayList< IModelError >();
		if( this.stepfunction == Stepfunctions.ADD || this.stepfunction == Stepfunctions.MULTIPLY ) {
			if( this.start == null || this.start.equals( "" ) ) {
				errorList.add( new AxisError( this, AxisErrorTypes.START_NOT_SET ) );
			} else if( !this.getMotorAxis().isValuePossible( this.start ) ) {
				errorList.add( new AxisError( this, AxisErrorTypes.START_VALUE_NOT_POSSIBLE ) );
			}
			if( this.stop == null || this.stop.equals( "" ) ) {
				errorList.add( new AxisError( this, AxisErrorTypes.STOP_NOT_SET ) );
			} else if( !this.getMotorAxis().isValuePossible( this.stop ) ) {
				errorList.add( new AxisError( this, AxisErrorTypes.STOP_VALUE_NOT_POSSIBLE ) );
			}
			if( this.stepwidth == null || this.stepwidth.equals( "" ) ) {
				errorList.add( new AxisError( this, AxisErrorTypes.STEPWIDTH_NOT_SET ) );
			}
			
		} else if( this.stepfunction == Stepfunctions.FILE ) {
			if( this.positionfile == null || this.positionfile.equals( "" ) ) {
				errorList.add( new AxisError( this, AxisErrorTypes.FILENAME_NOT_SET ) );
			}
		} else if( this.stepfunction == Stepfunctions.POSITIONLIST ) {
			if( this.positionlist == null || this.positionlist.equals( "" ) ) {
				errorList.add( new AxisError( this, AxisErrorTypes.POSITIONLIST_NOT_SET ) );
			}
		} else if( this.stepfunction == Stepfunctions.PLUGIN ) {
			if( this.positionPluginController != null ) {
				errorList.addAll( this.positionPluginController.getModelErrors() );
				if (this.getPositionPluginController().getModelErrors().size() > 0 ) {
					errorList.add( new AxisError( this, AxisErrorTypes.PLUGIN_ERROR ) );
				}
			}
		}
		return errorList;
	}

}
