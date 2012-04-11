package de.ptb.epics.eve.data.measuringstation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.measuringstation.exceptions.ParentNotAllowedException;

/**
 * <code>AbstractDevice</code> is the base of all devices in the data model. 
 * It describes the measuring station (e.g. motors, detectors). 
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
public abstract class AbstractDevice implements Comparable<AbstractDevice> {
	
	private String name;
	private String id;
	private Unit unit;
	private AbstractDevice parent;
	private final List<Option> options;
	private String fullIdentifier = null;
		
	/**
	 * Constructs a blank <code>AbstractDevice</code>.
	 */
	protected AbstractDevice() {
		this( "", "", null, null, null);
	}
	
	/**
	 * Constructs an <code>AbstractDevice</code> with given name, id, unit and 
	 * options.
	 * 
	 * @param name the name of the device
	 * @param id the id of the device
	 * @param unit the unit of the device
	 * @param options a <code>List</code> of <code>Option</code>s of the device
	 * 		   (use 'null' for no options)
	 * @param parent the parent of the device
	 * @throws IllegalArgumentException if name or id is <code>null</code>
	 * @throws IllegalArgumentException if parent has an illegal type
	 */
	public AbstractDevice(final String name, final String id, final Unit unit,
			final List<Option> options, final AbstractDevice parent) {

		if(name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		if(id == null) {
			throw new IllegalArgumentException(
					"The parameter 'id' must not be null!");
		}
		this.name = name;
		this.id = id;
		this.unit = unit;
		if(options != null) {
			this.options = new ArrayList<Option>(options);
		} else {
			this.options = new ArrayList<Option>();
		}
		try {
			this.setParent(parent);
		} catch(ParentNotAllowedException e) {
			throw new IllegalArgumentException(
					"The parameter 'parent' had an illegal Type.", e);	
		}
	}
	
	/**
	 * Returns the full identifier of a device as follows:
	 * Every device is addressable by it's full identifier consisting of its 
	 * own name, and the name of its parent device. If the device has no name, 
	 * the id is used to build the full identifier. So the full identifier of 
	 * a(n) ...<br>
	 * <br>
	 * ... motor will be: Motor-Name (Motor-Id).<br>
	 * ... motor axis will be: Motor-Name Axis-Name (Axis-Id).<br>
	 * ... detector will be: Detector-Name (Detector-Id).<br>
	 * ... detector channel will be: Detector-Name Channel-Name (Channel-Id).
	 * <br>
	 * ... option of a motor will be: Motor-Name Option-Name (Option-Id).
	 * <br>
	 * ... option of a motor axis will be: Motor-Name Axis-Name Option-Name 
	 * (Option-Id).<br>
	 * ... option of an detector will be: Detector-Name Option-Name (Option-Id).
	 * <br>
	 * ... option of an detector channel will be: Detector-Name Channel-Name
	 * Option-Name (Option-Id).<br>
	 * ... device will be: Device-Name (Device-Id).
	 * 
	 * @return A <code>String</code> that contains the full identifier of the 
	 * 			device
	 */
	public String getFullIdentifyer() {
		
		if(this.fullIdentifier == null) {
			StringBuffer fullIdentifyer = new StringBuffer();
		
			if (this.parent!=null) {
				if (this.parent.parent!=null) {
					if (!this.parent.parent.getName().equals(""))
						fullIdentifyer.append( this.parent.parent.getName());
				}
				if (!this.parent.getName().equals("")){
					if (!fullIdentifyer.toString().equals( "" )) {
						fullIdentifyer.append( " " );
						fullIdentifyer.append( this.parent.getName());
					}
					else
						fullIdentifyer.append( this.parent.getName());
				}
			}
			if (!this.getName().equals("")) {
				if (fullIdentifyer.toString().equals( "" )) {
					fullIdentifyer.append( " " );
					fullIdentifyer.append( this.getName());
				}
				else
					fullIdentifyer.append( this.getName());
			}

			if (!fullIdentifyer.equals( "" ))
				fullIdentifyer.append( "  ( ");
			else
				fullIdentifyer.append( "( ");
			
			fullIdentifyer.append( this.getID());
			fullIdentifyer.append( " )");
			this.fullIdentifier = fullIdentifyer.toString();
		}
		return this.fullIdentifier;
	}
	
	/**
	 * Returns the name of the device or its id if the name is an empty 
	 * {@link java.lang.String}.
	 * 
	 * @return the name of the device or its id if the name is empty
	 */
	public String getName() {
		if(this.name.equals("")) return this.id;
		return this.name;
	}
	
	/**
	 * Returns the id of the device.
	 * 
	 * @return  A String object that contains the id of the device.
	 */
	public String getID() {
		return this.id;
	}
	
	/**
	 * Returns the unit of the device.
	 * 
	 * @return A Unit object or null if it's not set.
	 * @see de.ptb.epics.eve.data.measuringstation.Unit
	 */
	public Unit getUnit() {
		return this.unit;
	}
	
	/**
	 * Returns the parent of the device.
	 * 
	 * @return The parent of the device. (null if none)
	 */
	public AbstractDevice getParent() {
		return this.parent;
	}
	
	/**
	 * Returns a copy of the internal options list.
	 * 
	 * @return A list of Option objects.
	 */
	public List<Option> getOptions() {
		List<Option> options = new ArrayList<Option>(this.options);
		Collections.sort(options);
		return options;
	}

	/**
	 * Returns an <code>Iterator</code> over the internal <code>List</code> of 
	 * <code>Option</code>s.
	 * 
	 * @return An Iterator<Option> object over the internal options list.
	 */
	public Iterator<Option> optionIterator() {
		return this.options.iterator();
	}
	
	/**
	 * Sets the ID of the Device.
	 * 
	 * @param id A <code>String</code>, that contains the id of the device.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setId(final String id) {
		if(id == null) {
			throw new IllegalArgumentException(
					"The parameter 'id' must not be null!");
		}
		this.id = id;
	}
	
	/**
	 * Adds an Option to the device.
	 * 
	 * @param option The Option that should be added.
	 * @return <code>true</code> if the <code>Option</code> was added,
	 * 			<code>false</code> otherwise 
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 * @throws IllegalArgumentException if option not possible for device
	 */
	public boolean add(final Option option) {
		if(option == null) {
			throw new IllegalArgumentException(
					"The parameter 'option' must not be null!");
		}
		// set the device as parent for the Option 
		// (for building the full identifier) and 
		// add it to an internal list.
		try {
			option.setParent(this);
		} catch(ParentNotAllowedException e) {
			throw new IllegalArgumentException("Your option does not accept " +
					"this kind of device as parent. " +
					"Please check you implementation!", e);
		}
		return options.add(option);
	}

	/**
	 * Removes an Option from the device. 
	 * 
	 * @param option The <code>Option</code> that should be removed.
	 * @return <code>true</code>if <code>Option</code> was removed,
	 * 			<code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 * @throws IllegalArgumentException 
	 */
	public boolean remove(final Option option) {
		if(option == null) {
			throw new IllegalArgumentException(
					"The parameter 'option' must not be null!");
		}
		// sets the parent of the Option to 'null' and
		// removes it from the internal list
		// parent will not be set to null if the option were
		// never part of the device.
		final boolean result = options.remove(option);
		if(result) {
			try {
				option.setParent(null);
			} catch (ParentNotAllowedException e) {
				throw new IllegalArgumentException("Your option has not " +
						"accepted to set the parent to null. " +
						"Please check your implementation!", e );
				// TODO describe/explain Exception in JavaDoc
			}
		}
		return result; 
	}

	/**
	 * Sets the name of the device.
	 * 
	 * @param name A <code>String</code> that contains the name of the device
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setName(final String name) {
		if(name == null) {
			throw new IllegalArgumentException(
					"The parameters 'name' must not be null!");
		}
		this.name = name;
	}

	/**
	 * Sets the unit of the device.
	 * 
	 * @param unit A Unit object.
	 */
	public void setUnit(final Unit unit) {
		this.unit = unit;
	}

	/**
	 * Sets the parent of this device. Override this method to control
	 * which types are allowed. After checking call the method of the 
	 * super class.
	 * 
	 * @param parent
	 * @throws ParentNotAllowedException
	 */
	protected void setParent(final AbstractDevice parent) 
								throws ParentNotAllowedException {
		if ( parent != null)
			this.parent = parent;
			// TODO nothing is thrown here, but a throw is declared
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Checks if argument and calling object are equal.
	 * 
	 * @param obj the <code>Object</code> that should be checked
	 * @return <code>true</code> if objects are equal,
	 * 			<code>false</code> otherwise
	 */
	@Override
	public boolean equals(final Object obj) {
		if(this == obj)
			return true;
		
		if(obj == null) 
			return false;
		
		if(getClass() != obj.getClass())
			return false;
		
		final AbstractDevice other = (AbstractDevice)obj;
		
		if(id == null) {
			if(other.id != null) {
				return false;
			}
		} else if(!id.equals(other.id)) {
			return false;
		}
		if(name == null) {
			if(other.name != null) {
				return false;
			}
		} else if(!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(AbstractDevice other) {
		return this.getName().toLowerCase()
				.compareTo(other.getName().toLowerCase());
	}
}