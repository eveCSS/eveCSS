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
 * This is the base of all devices in the Scan Module Editor model. It describes
 * the measuring station (e.g. motors, detectors). 
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.5
 * 
 */
public abstract class AbstractDevice {
	
	/**
	 * The name of the device.
	 */
	private String name;
	
	/**
	 * The id of the device.
	 */
	private String id;
	
	/**
	 * The unit of the device.
	 */
	private Unit unit;
	
	/**
	 * The parent of this device.
	 */
	private AbstractDevice parent;
	
	/**
	 * A <code>List</code>, that contains all <code>Option</code>s of the device.
	 */
	private final List<Option> options;
	
	// TODO define what the fullIdentifier is
	/**
	 * 
	 */
	private String fullIdentifier = null;
		
	/**
	 * This constructor is used by inheriting classes to construct a blank 
	 * <code>AbstractDevice</code> with an empty name, empty id, null unit and 
	 * empty list of options.
	 */
	protected AbstractDevice() {
		this( "", "", null, null, null);
	}
	
	/**
	 * This constructor is used by inheriting classes to construct a new
	 * <code>AbstractDevice</code> with given name, id, unit and options. 
	 * The <code>List</code> of <code>Option</code>s will be a copy of the 
	 * given list. The parent will not be set directly, it is using the 
	 * setParent Method. If you override
	 * this method,you can control which types are allowed an which not.
	 * 
	 * @param name The name of the device.
	 * @param id The id of the device.
	 * @param unit The unit of the device.
	 * @param options A list of options of the Device.
	 * 		   (use 'null' for no options)
	 * @param parent The parent of this device.
	 * @throws IllegalArgumentException if name or id are <code>null</code>
	 * @throws IllegalArgumentException if parent has an illegal type
	 */
	public AbstractDevice(final String name, final String id, 
						   final Unit unit, final List<Option> options, 
						   final AbstractDevice parent) {

		if( name == null ) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		if( id == null ) {
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
	 * This method is very fundamental for the model of the hierarchy of all
	 * devices inside of the measuring station description. Every device is 
	 * addressable by it's full identifier consisting of it's own name, and the
	 * name of it's parent device. If the device has no name, the id is used to
	 * build the full identifier. So The full identifier of a(n) ...<br>
	 * <br>
	 * ... motor will be: Motor-Name (Motor-Id).<br>
	 * ... motor-axis will be: Motor-Name Axis-Name (Axis-Id).<br>
	 * ... detector will be: Detector-Name (Detector-Id).<br>
	 * ... detector-channel will be: Detector-Name Channel-Name (Channel-Id).
	 * <br>
	 * ... option of an motor will be: Motor-Name Option-Name (Option-Id).
	 * <br>
	 * ... option of an motor-axis will be: Motor-Name Axis-Name Option-Name 
	 * (Option-Id).<br>
	 * ... option of an detector will be: Detector-Name Option-Name (Option-Id).
	 * <br>
	 * ... option of an detector-channel will be: Detector-Name Channel-Name
	 * Option-Name (Option-Id).<br>
	 * ... device will be: Device-Name (Device-Id).<br>
	 * <br>
	 * 
	 * @return A String object that contains the full identifier of the device.
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
	 * Returns the name of the device.
	 * 
	 * @return A String object that contains the name of the device.
	 */
	public String getName() {
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
	 * @see de.ptb.epics.eve.data.measuringstation.Unit
	 * @return A Unit object or null if it's not set.
	 */
	public Unit getUnit() {
		return this.unit;
	}
	
	/**
	 * Returns the parent of this device.
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
		return new ArrayList<Option>(this.options);
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
	 * @exception IllegalArgumentException if the argument is <code>null</code>
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
	 * 
	 * @return a fancy number yet has to be explained
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
	 * @return <code>true</code> if objects are equal,
	 * 			<code>false</code> otherwise.
	 */
	@Override
	public boolean equals(final Object obj) {
		// TODO explain why and how
		
		if(this == obj)
			return true;
		
		if(obj == null) 
			return false;
		
		if(getClass() != obj.getClass())
			return false;
		
		final AbstractDevice other = (AbstractDevice)obj;
		
		if( id == null ) {
			if( other.id != null ) {
				return false;
			}
		} else if( !id.equals( other.id ) ) {
			return false;
		}
		if( name == null ) {
			if( other.name != null ) {
				return false;
			}
		} else if( !name.equals( other.name ) ) {
			return false;
		}	
		return true;
	}
}