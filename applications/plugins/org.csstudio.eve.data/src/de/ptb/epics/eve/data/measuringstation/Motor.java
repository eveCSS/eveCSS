package de.ptb.epics.eve.data.measuringstation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * This class represents a Motor. The main purpose of this class is the 
 * management of the motor axis.
 * 
 * @see de.ptb.epics.eve.data.measuringstation.MotorAxis
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 */
public class Motor extends AbstractMainPhaseDevice {

	// a List holding all axis of this motor
	private List<MotorAxis> axis;
	
	/**
	 * Constructs an empty Motor.
	 */
	public Motor() {
		this.axis = new ArrayList<MotorAxis>();
	}
	
	/**
	 * Returns a copy of the internal List, that is holding the MotorAxis
	 * objects. The list is sorted alphabetically.
	 * 
	 * @return A List of MotorAxis objects of the Motor.
	 */
	public List<MotorAxis> getAxes() {	
		List<MotorAxis> axes = new ArrayList<MotorAxis>(this.axis);
		Collections.sort(axes);
		return axes;
	}
	
	/**
	 * Returns an Iterator over the internal list of MotorAxis objects.
	 * 
	 * @return An Iterator object.
	 */
	public Iterator<MotorAxis> axisIterator() {
		return this.axis.iterator();
	}

	/**
	 * Adds a MotorAxis to this Motor and sets the Motor as the Parent of the
	 * MotorAxis.
	 * 
	 * @param axis The MotorAxis object, that should be added.
	 * @return TRUE if the axis has been added.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 * @throws IllegalArgumentException if axis not allowed for this motor
	 */
	public boolean add(final MotorAxis axis) {
		if(axis == null) {
			throw new IllegalArgumentException(
					"The parameter 'axis' must not be null!");
		}
		
		final boolean result = this.axis.add(axis);
		if(result) {
			try {
				axis.setParent( this );
			} catch( final ParentNotAllowedException e ) {
				this.axis.remove( axis );
				throw new IllegalArgumentException("The axis you have passend " +
						"didn't accept the Motor as parent. " +
						"Please check your implementation!", e);				
			}
		}
		return result;
	}

	/**
	 * Removes a MotorAxis from this Motor.
	 * 
	 * @param axis A MotorAxis object.
	 * @return <code>true</code> if the MotorAxis has been removed.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public boolean remove(final MotorAxis axis) {
		if(axis == null) {
			throw new IllegalArgumentException(
					"The parameter 'axis' must not be null!");
		}
		final boolean result = this.axis.remove(axis);
		if(result) {
			try {
				axis.setParent(null);
			} catch(final ParentNotAllowedException e) { }
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() {
		final Motor motor = new Motor();
		for(final MotorAxis axis : this.axis) {
			motor.add((MotorAxis)axis.clone());
		}
		motor.setClassName(this.getClassName());
		motor.setTrigger((Function)
				(this.getTrigger()!=null?this.getTrigger().clone():null));
		this.setName(this.getName()); // 
		motor.setName(this.getName());
		motor.setId(this.getID());
		motor.setUnit((Unit)
				(this.getUnit()!=null?this.getUnit().clone():null));
		for(final Option option : this.getOptions()) {
			motor.add((Option)option.clone());
		}
		return motor;
	}
}