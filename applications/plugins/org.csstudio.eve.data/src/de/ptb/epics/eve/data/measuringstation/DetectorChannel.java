/*
 * Copyright (c) 2001, 2007 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * This class represents a detector channel.
 *
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 */
public class DetectorChannel extends AbstractMainPhaseDevice {
	
	/**
	 * The Function from which the value of this detector channel is read from.
	 */
	private Function read;
	
	/**
	 * Constructs an empty <code>DetectorChannel</code>.
	 */
	public DetectorChannel() {
		this.read = new Function();
	}
	
	/**
	 * Constructs an <code>DetectorChannel</code> with a given Function object.
	 * 
	 * @param read a <code>Function</code>
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public DetectorChannel(final Function read) {
		if(read == null) {
			throw new IllegalArgumentException(
					"The parameter 'read' must not be null!");
		}
		this.read = read;	
	}
	
	/**
	 * Returns the <code>Function</code>.
	 * 
	 * @return the <code>Function</code>
	 */
	public Function getRead() {
		return this.read;
	}

	/**
	 * Sets the function of the detector channel.
	 * 
	 * @param read A Function object.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setRead(final Function read) {
		if(read == null) {
			throw new IllegalArgumentException(
					"The parameter 'read' must not be null!");
		}
		this.read = read;
	}
	
	/**
	 * Returns the parent as detector.
	 * 
	 * @return a Detector or null.
	 */
	public Detector getDetector() {
		return (Detector)this.getParent();
	}
	
	/**
	 * Checks if this class can really be the parent of this device. 
	 * 
	 * @param parent The parent that should be set. In this case only a 
	 * 		   Detector will fit.
	 * @throws ParentNotAllowedException if parent not a Detector
	 */
	@Override
	protected void setParent(final AbstractDevice parent) 
						throws ParentNotAllowedException {
		if(parent != null) {
			if(!(parent instanceof Detector)) {
				throw new ParentNotAllowedException(
						"Your class is directly or indirectly inhereting " +
						"from AbstractMainPhaseDevice. The parent of an " +
						"AbstractMainPhaseDevice can only be an " +
						"AbstractClassedDevice. Please fix your implementation " +
						"to check this constraint!" );
			}
		}
		super.setParent(parent);
	}

	/**
	 * 
	 * @return a hash
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * Checks whether argument and calling object are equal.
	 * 
	 * @param obj the <code>Object</code> that should be checked
	 * @return <code>true</code> if objects are equal,
	 * 			<code>false</code> otherwise
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
	 * Clones the current detector channel.
	 * 
	 * @return a clone of the calling <code>DetectorChannel</code> 
	 */
	@Override
	public Object clone() {
		final DetectorChannel detectorChannel = new DetectorChannel();
		
		detectorChannel.read = (Function)
			(this.read!=null?this.read.clone():null);
		
		detectorChannel.setClassName(this.getClassName());
		detectorChannel.setTrigger((Function)
				(this.getTrigger()!=null?this.getTrigger().clone():null));
		detectorChannel.setName(this.getName());
		detectorChannel.setId(this.getID());
		detectorChannel.setUnit((Unit)
				(this.getUnit()!=null?this.getUnit().clone():null));
		
		for(final Option option : this.getOptions()) {
//	TODO, wegnehmen:		this.add( (Option)option.clone() );
			detectorChannel.add((Option)option.clone());
		}	
		return detectorChannel;
	}
}