package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * This class represents a detector channel.
 *
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
public class DetectorChannel extends AbstractMainPhaseDevice implements Cloneable {
	
	/**
	 * The Function from which the value of this detector channel is read from.
	 */
	private Function read;
	
	private Function stop;
	private Function status;
	
	private boolean deferred;
	private boolean saveValue;
	
	/**
	 * Constructs an empty <code>DetectorChannel</code>.
	 */
	public DetectorChannel() {
		this.read = new Function();
		this.stop = new Function();
		this.status = new Function();
		this.deferred = false;
		this.saveValue = true;
	}
	
	/**
	 * Constructs an <code>DetectorChannel</code> with a given Function object.
	 * 
	 * @param read a <code>Function</code>
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public DetectorChannel(final Function read) {
		this();
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
	 * @return the stop
	 */
	public Function getStop() {
		return stop;
	}

	/**
	 * @param stop the stop to set
	 */
	public void setStop(Function stop) {
		this.stop = stop;
	}

	/**
	 * @return the status
	 */
	public Function getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Function status) {
		this.status = status;
	}
	
	/**
	 * @return the deferred
	 * @since 1.22
	 */
	public boolean isDeferred() {
		return deferred;
	}

	/**
	 * @param deferred the deferred to set
	 * @since 1.22
	 */
	public void setDeferred(boolean deferred) {
		this.deferred = deferred;
	}

	/**
	 * @return the saveValue
	 * @since 1.22
	 */
	public boolean isSaveValue() {
		return saveValue;
	}

	/**
	 * @param saveValue the saveValue to set
	 * @since 1.22
	 */
	public void setSaveValue(boolean saveValue) {
		this.saveValue = saveValue;
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
	 * {@inheritDoc}
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
		final DetectorChannel detectorChannel = new DetectorChannel();
		
		detectorChannel.read = (Function) (this.read != null ? this.read
				.clone() : null);
		detectorChannel.stop = (Function) (this.stop != null ? this.stop
				.clone() : null);
		detectorChannel.status = (Function) (this.status != null ? this.status
				.clone() : null);
		
		detectorChannel.setClassName(this.getClassName());
		detectorChannel.setTrigger((Function) (this.getTrigger() != null ? this
				.getTrigger().clone() : null));
		this.setName(this.getName());
		detectorChannel.setName(this.getName());
		detectorChannel.setId(this.getID());
		detectorChannel.setUnit((Unit) (this.getUnit() != null ? this.getUnit()
				.clone() : null));
		detectorChannel.setDeferred(this.isDeferred());
		detectorChannel.setSaveValue(this.isSaveValue());
		
		for(final Option option : this.getOptions()) {
			detectorChannel.add((Option)option.clone());
		}
		return detectorChannel;
	}
}