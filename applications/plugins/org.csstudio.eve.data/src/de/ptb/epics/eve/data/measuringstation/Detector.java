/*
 * Copyright (c) 2001, 2007 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * This class represents a detector on a measuring station.
 * The implementation of this class is managing the detector 
 * channels a detector can have.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.4
 */
public class Detector extends AbstractMainPhaseDevice {

	/**
	 * A List holding the detector channels
	 */
	private final List<DetectorChannel> channels;

	/**
	 * Constructs a new Detector with an empty list of detectorChannels inside.
	 *
	 */
	public Detector() {
		this.channels = new ArrayList<DetectorChannel>();
	}
	
	/**
	 * Returns a copy of the DetectorChannel list
	 * 
	 * @return An ArrayList<DetectorChannel> object
	 */
	public List<DetectorChannel> getChannels() {
		return new ArrayList<DetectorChannel>(channels);
	}
	
	/**
	 * Returns an Iterator over the List of Detector Channels.
	 * 
	 * @return An Iterator<DetectorChannel> object.
	 */
	public Iterator<DetectorChannel> channelIterator() {
		return this.channels.iterator();
	}

	/**
	 * Adds a detector channel to this detector.
	 * 
	 * @param detectorChannel A DetectorChannel object
	 * @return <code>true</code> if the channel was added,
	 * 			<code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public boolean add(final DetectorChannel detectorChannel) {
		if(detectorChannel == null) {
			throw new IllegalArgumentException(
					"The parameter 'detectorChannel' must not be null!");
		}
		try {
			detectorChannel.setParent(this);
		} catch (ParentNotAllowedException e) {
			e.printStackTrace();
			return false;
		}
		return channels.add(detectorChannel);
	}

	/**
	 * Removes a detector channel from this detector.
	 * 
	 * @param detectorChannel a <code>DetectorChannel</code>
	 * @return <code>true</code> if the channel was removed,
	 * 			<code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public boolean remove(final DetectorChannel detectorChannel) {
		if(detectorChannel == null) {
			throw new IllegalArgumentException(
					"The parameter 'detectorChannel' must not be null!");
		}
		final boolean result = channels.remove(detectorChannel); 
		if(result) {
			try {
				detectorChannel.setParent(null);
			} catch (ParentNotAllowedException e) {
				e.printStackTrace();
				return false;
			}
		}
		return result;
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
	 * Checks whether the argument and calling object are equal.
	 * 
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
	 * Clones the current Detector.
	 * 
	 * @return a clone of the calling <code>Detector</code>
	 */
	@Override
	public Object clone() {
		final Detector detector = new Detector();
		for(final DetectorChannel channel : this.channels) {
			detector.add((DetectorChannel)channel.clone());
		}
		detector.setClassName(this.getClassName());
		detector.setTrigger((Function)
				(this.getTrigger()!=null?this.getTrigger().clone():null));
		detector.setName(this.getName());
		detector.setId(this.getID());
		detector.setUnit((Unit)
				(this.getUnit()!=null?this.getUnit().clone():null));
		
		for(final Option option : this.getOptions()) {
//	TODO, wegnehmen:		this.add( (Option)option.clone() );
			detector.add((Option)option.clone());
		}
		return detector;
	}
}