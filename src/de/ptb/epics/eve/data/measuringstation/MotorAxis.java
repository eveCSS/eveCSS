/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 *  This class represents a motor axis at a measuring station.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.4
 */
public class MotorAxis extends AbstractMainPhaseDevice {

	/**
	 * The position function.
	 */
	private Function position;
	
	/**
	 * The status function.
	 */
	private Function status;
	
	/**
	 * The goto of the motor axis.
	 */
	private Function gotoAdvisor;
	
	/**
	 * The stop trigger.
	 */
	private Function stop;
	
	/**
	 * deadband: accepted deviation from target position
	 */
	private Function deadband;
	
	/**
	 * offset between user and system coordinates
	 */
	private Function offset;
	
	/**
	 * value used for tweak forward / reverse
	 */
	private Function tweakValue;
	
	/**
	 * tweak forward
	 */
	private Function tweakForward;
	
	/**
	 * tweak reverse
	 */
	private Function tweakReverse;
	
	/**
	 * This constructor constructs a new MotorAxis object. 
	 *
	 */
	public MotorAxis() {
		this( new Function(), null, new Function(), new Function() );
	}
	
	/**
	 * This constructor constructs a new MotorAxis object with specific values.
	 * 
	 * @param position The position function of the motor axis. Must not be null.
	 * @param status The status function of the motor axis. Can be null.
	 * @param gotoAdvisor The goto of the motor axis. Must not be null.
	 * @param stop the stop trigger of the motor axis. Must not be null.
	 */
	public MotorAxis( final Function position, final Function status, final Function gotoAdvisor, final Function stop ) {
		if( position == null ) {
			throw new IllegalArgumentException( "The parameter 'position' must not be null!" );
		}
		if( gotoAdvisor == null ) {
			throw new IllegalArgumentException( "The parameter 'gotoAdvisor' must not be null!" );
		}
		if( stop == null ) {
			throw new IllegalArgumentException( "The parameter 'stop' must not be null!" );
		}
		
		this.position = position;
		this.status = status;
		this.gotoAdvisor = gotoAdvisor;
		this.stop = stop;
		
	}
	
	/**
	 * Give back the position function of the motor axis.
	 * 
	 * @return A Function object. Never returns null.
	 */
	public Function getPosition() {
		return this.position;
	}
	
	/**
	 * Gives back the status function of the motor axis.
	 * 
	 * @return A Function object or null if it's not set.
	 */
	public Function getStatus() {
		return this.status;
	}
	
	/**
	 * Gives back the goto function of the motor axis.
	 * 
	 * @return A Function object or null if it's not set.
	 */
	public Function getGoto() {
		return this.gotoAdvisor;
	}
	
	/**
	 * Gives back the stop function of the motor axis.
	 * 
	 * @return A Function object or null if it's not set.
	 */
	public Function getStop() {
		return this.stop;
	}

	/**
	 * This method sets the goto function of the motor axis.
	 * 
	 * @param gotoAdvisor The new goto function for the motor axis. Must not be null.
	 */
	public void setGoto( final Function gotoAdvisor ) {
		if( gotoAdvisor == null ) {
			throw new IllegalArgumentException( "The parameter 'gotoAdvisor' must not be null!" );
		}
		this.gotoAdvisor = gotoAdvisor;
	}

	/**
	 * This method sets the position function of the motor axis.
	 * 
	 * @param position The new position function for the motor axis. Must not be null.
	 */
	public void setPosition( final Function position ) {
		if( position == null ) {
			throw new IllegalArgumentException( "The parameter 'position' must not be null!" );
		}
		this.position = position;
	}

	/**
	 * This method sets the status function of the motor axis.
	 * 
	 * @param status The new status function for the motor axis.
	 */
	public void setStatus( final Function status ) {
		this.status = status;
	}

	/**
	 * This method sets the stop function of the motor axis.
	 * 
	 * @param stop The new stop function for the motor axis. Must not be null.
	 */
	public void setStop( final Function stop ) {
		if( stop == null ) {
			throw new IllegalArgumentException( "The parameter 'stop' must not be null!" );
		}
		this.stop = stop;
	}

	/**
	 * This method is overriding the setParent Method of the super class. This method contains some
	 * checks if this class can really be the parent of this device. It will throw a ParentNotAllowedException
	 * if there was passes a wrong device type.
	 * 
	 * @param parent The parent that should be settet. 
	 */
	@Override
	protected void setParent( final AbstractDevice parent ) throws ParentNotAllowedException {
		if( parent != null ) {
			if( !( parent instanceof Motor ) ) {
				throw new ParentNotAllowedException( "Your class is directly or indirectly inhereting from AbstractMainPhaseDevice. The parent of an AbstractMainPhaseDevice can only be a AbstractClassedDevice. Please fix your implementation to check this constraint!" );
			}
		}
		super.setParent( parent );
	}
	
	/**
	 * Finds out if a value is possible under the constraints of this MotorAxis object.
	 * 
	 * @param value The value that should be checked
	 * @return Returns true if the value fits the constrains, false if not.
	 */
	public boolean isValuePossible( final String value ) {
		if( value == null ) {
			throw new IllegalArgumentException( "The parameter value must not be null" );
		}
		
		if( this.gotoAdvisor != null ) {
			return this.gotoAdvisor.isValuePossible( value );
		} else {
			return this.position.isValuePossible( value );
		}
	}

	/**
	 * Return a well-formatted string with a valid value for the datatype.
	 * If value can not be converted, return a default value
	 * 
	 * @param value The value that will be formatted.
	 * @return a well-formatted string with a valid value
	 */
	public String formatValueDefault( final String value ) {
		if( this.gotoAdvisor != null ) {
			return this.gotoAdvisor.formatValueDefault( value );
		} else {
			return this.position.formatValueDefault( value );
		}
	}

	/**
	 * Return a well-formatted string with a valid value for the datatype.
	 * If value can not be converted, return null
	 * 
	 * @param value The value that will be formatted.
	 * @return a well-formatted string or null
	 */
	public String formatValue( final String value ) {
		if( this.gotoAdvisor != null ) {
			return this.gotoAdvisor.formatValue( value );
		} else {
			return this.position.formatValue( value );
		}
	}

	/**
	 * Return a well-formatted string with a valid value for the datatype.
	 * If value can not be converted, return a default value
	 * 
	 * @return a well-formatted string with a valid default value
	 */
	public String getDefaultValue() {
		if( this.gotoAdvisor != null ) {
			return this.gotoAdvisor.getDefaultValue();
		} else {
			return this.position.getDefaultValue();
		}
	}
	
	/**
	 * return the datatype of the goto access or the position access, if
	 * we don't have a goto access
	 * 
	 * @return the datatype of this axis
	 */
	public DataTypes getType() {
		if( this.gotoAdvisor != null ) {
			return this.gotoAdvisor.getType();
		} else {
			return this.position.getType();
		}
	}

	/**
	 * This method sets the Function for the offset
	 * 
	 * @param offset The Function for the offset.
	 */
	public void setOffset( final Function offset ) {
		this.offset = offset;
	}

	/**
	 * This method gives back the Function for the offset
	 * 
	 * @return The Function for the offset.
	 */
	public Function getOffset() {
		return this.offset;
	}

	/**
	 * This method sets the Function for the deadband
	 * 
	 * @param deadband The Function for the deadband.
	 */
	public void setDeadband( final Function deadband ) {
		this.deadband = deadband;
	}

	/**
	 * This method gives back the Function for the deadband
	 * 
	 * @return The Function for the deadband.
	 */
	public Function getDeadband() {
		return this.deadband;
	}

	/**
	 * This method sets the Function for the tweakValue
	 * 
	 * @param tweakValue The Function for the tweakValue.
	 */
	public void setTweakValue( final Function tweakValue ) {
		this.tweakValue = tweakValue;
	}

	/**
	 * This method gives back the Function for the tweakValue
	 * 
	 * @return The Function for the tweakValue.
	 */
	public Function getTweakValue() {
		return this.tweakValue;
	}

	/**
	 * This method sets the Function for the tweakForward
	 * 
	 * @param tweakForward The Function for the tweakForward.
	 */
	public void setTweakForward( final Function tweakForward ) {
		this.tweakForward = tweakForward;
	}

	/**
	 * This method gives back the Function for the tweakForward
	 * 
	 * @return The Function for the tweakForward.
	 */
	public Function getTweakForward() {
		return this.tweakForward;
	}

	/**
	 * This method sets the Function for the tweakReverse
	 * 
	 * @param tweakReverse The Function for the tweakReverse.
	 */
	public void setTweakReverse( final Function tweakReverse ) {
		this.tweakReverse = tweakReverse;
	}

	/**
	 * This method gives back the Function for the tweakReverse
	 * 
	 * @return The Function for the tweakReverse.
	 */
	public Function getTweakReverse() {
		return this.tweakReverse;
	}
}
