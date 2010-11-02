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
 * This class represents a detector on a measuring station. The implementation of this class
 * is managing the detector channels a detecor can have.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.4
 */
public class Detector extends AbstractMainPhaseDevice {

	/**
	 * A private List to hold the detector channels of this detector.
	 */
	private final List<DetectorChannel> channels;

	/**
	 * Constructs a new Detector with an empty list of detectorChannels inside. This constructor
	 * id calling the default constructor of the super class.
	 *
	 */
	public Detector() {
		this.channels = new ArrayList<DetectorChannel>();
	}
	
	/**
	 * Gives back a copy of the internal ArrayList, that contains the detector channels. Notice that
	 * you'll only get a copy. To manipulate the list use the methods of this class.
	 * 
	 * @return A ArrayList<DetectorChannel> object
	 */
	public List<DetectorChannel> getChannels() {
		return new ArrayList<DetectorChannel>( channels );
	}
	
	/**
	 * Gives back an iterator over the internal list of channels.
	 * 
	 * @return A Iterator<DetectorChannel> object. Never returns null.
	 */
	public Iterator<DetectorChannel> channelIterator() {
		return this.channels.iterator();
	}

	/**
	 * Adds a detector channel to this detector.
	 * 
	 * @param detectorChannel A DetectorChannel object. Must not be null!
	 * @return Returns 'true' if the detector channel has been added to the detector an the detector was able to set him self as the parent of the channel.
	 */
	public boolean add( final DetectorChannel detectorChannel ) {
		if( detectorChannel == null ) {
			throw new IllegalArgumentException( "The parameter 'detectorChannel' must not be null!" );
		}
		try {
			detectorChannel.setParent( this );
		} catch (ParentNotAllowedException e) {
			e.printStackTrace();
			return false;
		}
		return channels.add( detectorChannel );
	}

	/**
	 * Removes a detector channel from this detector.
	 * 
	 * @param detectorChannel A DetectorChannel object. Must not be null!
	 * @return Returns 'true' if the detector channel has removed from the detector an the detector was able to set the parent of the channel to null.
	 */
	public boolean remove( final DetectorChannel detectorChannel ) {
		if( detectorChannel == null ) {
			throw new IllegalArgumentException( "The parameter 'detectorChannel' must not be null!" );
		}
		final boolean result = channels.remove( detectorChannel ); 
		if( result ) {
			try {
				detectorChannel.setParent( null );
			} catch (ParentNotAllowedException e) {
				e.printStackTrace();
				return false;
			}
		}
		return result;
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
		Detector other = (Detector) obj;
		
		return true;
	}
		
	@Override
	public Object clone() {
		final Detector detector = new Detector();
		for( final DetectorChannel channel : this.channels ) {
			detector.add( (DetectorChannel)channel.clone() );
		}
		detector.setClassName( this.getClassName() );
		detector.setTrigger( (Function)(this.getTrigger()!=null?this.getTrigger().clone():null));
		detector.setName( this.getName() );
		detector.setId( this.getID() );
		detector.setUnit( (Unit)(this.getUnit()!=null?this.getUnit().clone():null) );
		
		for( final Option option : this.getOptions() ) {
//			this.add( (Option)option.clone() );
			detector.add( (Option)option.clone() );
		}
		return detector;
	}
}
