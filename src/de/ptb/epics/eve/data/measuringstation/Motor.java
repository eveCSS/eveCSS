/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * This class represents a Motor. The main purpose of this class is the management
 * of the motor axis.
 * 
 * @see de.ptb.epics.eve.data.measuringstation.MotorAxis
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 */
public class Motor extends AbstractMainPhaseDevice {

	/**
	 * A List, that is holding all axis of this motor
	 */
	private List<MotorAxis> axis;
	
	
	/**
	 * This constructor constructs a and empty Motor.
	 *
	 */
	public Motor() {
		this.axis = new ArrayList<MotorAxis>();
	}
	
	/**
	 * Gives back a copy of the internal List, that is holding the MotorAxis objects.
	 * 
	 * @return A List, that with the MotorAxis object of the Motor. Never returns null.
	 */
	public List<MotorAxis> getAxis() {	
		return new ArrayList<MotorAxis>( this.axis );
	}
	
	/**
	 * Gives back a Iterator over the internal list of MotorAxis objects.
	 * 
	 * @return A Iterator object. Never returns null.
	 */
	public Iterator<MotorAxis> axisIterator() {
		return this.axis.iterator();
	}

	/**
	 * Adds a MotorAxis to this Motor and sets the Motor as the Parent of the MotorAxis.
	 * 
	 * @param axis The MotorAxis object, that should be added. Must not be null.
	 * @return Gives back 'true' if the axis has been added.
	 */
	public boolean add( final MotorAxis axis ) {
		if( axis == null ) {
			throw new IllegalArgumentException( "The parameter 'axis' must not be null!" );
		}
		
		final boolean result = this.axis.add( axis );
		if( result ) {
			try {
				axis.setParent( this );
			} catch( final ParentNotAllowedException e ) {
				this.axis.remove( axis );
				throw new IllegalArgumentException( "The axis you have passend didn't accepted the Motor as parent. Please check you implementation!", e);				
			}
		}
		return result;
	}

	/**
	 * Removes a MotorAxis from this Motor.
	 * 
	 * @param axis A MotorAxis object. Must not be null!
	 * @return Gives back 'true' if the MotorAxis has been removed.
	 */
	public boolean remove( final MotorAxis axis ) {
		if( axis == null ) {
			throw new IllegalArgumentException( "The parameter 'axis' must not be null!" );
		}
		final boolean result = this.axis.remove( axis );
		if( result ) {
			try {
				axis.setParent( null );
			} catch( final ParentNotAllowedException e ) {
				
			}
		}
		return result;
	}	
		
}
