package de.ptb.epics.eve.data.measuringstation;

import java.util.List;

/**
 * The base of all devices that can be used during the main phase of a scan 
 * module like motor axis and detector channels.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 */
public abstract class AbstractMainPhaseDevice extends AbstractDevice {
	
	/**
	 * The attribute that is holding the class-name of the device.
	 */
	private String className;
	
	/**
	 * The trigger of the device.
	 */
	private Function trigger;
	
	/**
	 * Should be called to construct an empty 
	 * <code>AbstractMainPhaseDevice</code> The trigger will be set to null.
	 */
	public AbstractMainPhaseDevice() {
		super();
		this.trigger = null;
		this.className = "";
	}
	
	/**
	 * Should be called to construct an <code>AbstractMainPhaseDevice</code> 
	 * with a given <code>trigger</code>.
	 *  
	 * @param trigger a <code>Trigger</code>
	 */
	public AbstractMainPhaseDevice(final Function trigger) {
		super();
		this.trigger = trigger;
	}
	
	/**
	 * Constructs a new, very specific device with all attributes.
	 * 
	 * @param name the name of the device
	 * @param id the id of the device
	 * @param unit the code>Unit</code> of the device
	 * @param options a <code>List</code> of <code>Option</code>s of the device
	 * 			(use 'null' for no options)
	 * @param parent the parent of this device
	 * @param trigger a <code>Trigger</code>
	 * @throws IllegalArgumentException if name or id are <code>null</code>
	 * @throws IllegalArgumentException if parent has an illegal type
	 */
	public AbstractMainPhaseDevice(final String name, final String id,
									final Unit unit, final List<Option> options,
									final AbstractMainPhaseDevice parent, 
									final Function trigger) {
		super(name, id, unit, options, parent);
		this.trigger = trigger;
	}
	
	
	/**
	 * Returns the <code>Trigger</code> of the device.
	 * 
	 * @return the <code>Trigger</code> of the device
	 */
	public Function getTrigger() {
		return this.trigger;
	}

	/**
	 * Sets the <code>Trigger</code> of the device.
	 * 
	 * @param trigger the <code>Trigger</code> of the device
	 */
	public void setTrigger(final Function trigger) {
		this.trigger = trigger;
	}
	
	/**
	 * Returns the class name of the device.
	 * 
	 * @return a <code>String</code> containing the name of the class of 
	 * 			the device
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * Sets the class name of the device.
	 * 
	 * @param className a <code>String</code> that contains the class name
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setClassName(final String className) {
		if(className == null) {
			throw new IllegalArgumentException(
					"The parameter 'className' must not be null!");
		}
		this.className = className;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if(this == obj) {
			return true;
		}
		if(!super.equals(obj)) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		final AbstractMainPhaseDevice other = (AbstractMainPhaseDevice)obj;
		if(className == null) {
			if(other.className != null) {
				return false;
			}
		} else if(!className.equals( other.className)) {
			return false;
		}
		return true;
	}
}