/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * This class representes a detector channel.
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
	 * This constructor constructs a new DetectorChannel.
	 *
	 */
	public DetectorChannel() {
		this.read = new Function();
	}
	
	/**
	 * This constructor construct a new DetectorChannel with a given Function object.
	 * 
	 * @param read A Function. Must not be null.
	 */
	public DetectorChannel( final Function read ) {
		if( read == null ) {
			throw new IllegalArgumentException( "The parameter 'read' must not be null!" );
		}
		this.read = read;
		
	}
	
	/**
	 * Gives back the function of this detector channel
	 * 
	 * @return A Function object. Never returns null.
	 */
	public Function getRead() {
		return this.read;
	}

	/**
	 * Sets the function of this detector channel
	 * 
	 * @param read A Function object. Must not be null.
	 */
	public void setRead( final Function read ) {
		if( read == null ) {
			throw new IllegalArgumentException( "The parameter 'read' must not be null!" );
		}
		this.read = read;
	}
	
	/**
	 * This method gives back the parent as detector.
	 * 
	 * @return A Detector or null.
	 */
	public Detector getDetector() {
		return (Detector)this.getParent();
	}
	
	/**
	 * This method is overriding the setParent Method of the super class. This method contains some
	 * checks if this class can really be the parent of this device. It will throw a ParentNotAllowedException
	 * if there was passes a wrong device type. In this case, only a Detector is allowed as parent.
	 * 
	 * @param parent The parent that should be settet. In this case only a Detector will fit.
	 */
	@Override
	protected void setParent( final AbstractDevice parent ) throws ParentNotAllowedException {
		if( parent != null ) {
			if( !( parent instanceof Detector ) ) {
				throw new ParentNotAllowedException( "Your class is directly or indirectly inhereting from AbstractMainPhaseDevice. The parent of an AbstractMainPhaseDevice can only be a AbstractClassedDevice. Please fix your implementation to check this constraint!" );
			}
		}
		super.setParent( parent );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		return result;
	}

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
		DetectorChannel other = (DetectorChannel) obj;
		
		return true;
	}
	
	@Override
	public Object clone() {
		final DetectorChannel detectorChannel = new DetectorChannel();
		
		detectorChannel.read = (Function)(this.read!=null?this.read.clone():null);
		
		detectorChannel.setClassName( this.getClassName() );
		detectorChannel.setTrigger( (Function)(this.getTrigger()!=null?this.getTrigger().clone():null));
		detectorChannel.setName( this.getName() );
		detectorChannel.setId( this.getID() );
		detectorChannel.setUnit( (Unit)(this.getUnit()!=null?this.getUnit().clone():null) );
		
		for( final Option option : this.getOptions() ) {
//			this.add( (Option)option.clone() );
			detectorChannel.add( (Option)option.clone() );
		}
		
		return detectorChannel;
	}
}
