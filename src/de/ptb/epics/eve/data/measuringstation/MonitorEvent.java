/*
 * Copyright (c) 2001, 2007 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.MethodTypes;
import de.ptb.epics.eve.data.TypeValue;
import de.ptb.epics.eve.data.DataTypes;

/**
 * This class represents an <code>Event</code> that is defined inside of a 
 * measuring station description. It also provides a mechanism to get connected 
 * with a scan module and represents a start event.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 */
public class MonitorEvent extends AbstractNothingNullTypeValueAccessContainer {

	/**
	 * The name of the MonitorEvent.
	 */
	private String name;
	
	/**
	 * The id of the MonitorEvent.
	 */
	private String id;

	/**
	 * Constructs a <code>MonitorEvent</code> with a <code>String</code> data 
	 * type, an empty monitor process variable and empty name.
	 */
	public MonitorEvent() {
		this(new Access(MethodTypes.GETCB), 
			  new TypeValue(DataTypes.STRING), "", "");
	}
	
	/**
	 * Constructs a <code>MonitorEvent</code> with a given value (dataType) and 
	 * a given access. 
	 * 
	 * @param access specifies the <code>Access</code> of the 
	 * 		   <code>MonitorEvent</code>. Must not be null and the the method 
	 * 		   type of the access must be Monitor.
	 * @param dataType specifies the data type of this 
	 * 		   <code>MonitorEvent</code>. Must not be null.
	 */
	public MonitorEvent(final Access access, final TypeValue dataType) {
		this(access, dataType, "", "");
	}
	
	/**
	 * Constructs a <code>MonitorEvent</code> with given name, id and type.
	 * 
	 * @param name a <code>String</code> containing the name of the 
	 * 		   <code>MonitorEvent</code>. Must not be null.
	 * @param id a <code>String</code> containing the id of the 
	 * 		   <code>MonitorEvent</code>. Must not be null.
	 * @param type the type of the <code>MonitorEvent</code>. Must not be null!
	 */
	public MonitorEvent(final String name, final String id) {
		this(new Access(MethodTypes.GETCB), 
			  new TypeValue(DataTypes.STRING), name, id);
	}
	
	/**
	 * Constructs a <code>MonitorEvent</code> with given data type (value), 
	 * process variable, name, id and MonitorEvent type.
	 * 
	 * @param dataType specifies the data type of this 
	 * 		   <code>MonitorEvent</code>. Must not be null.
	 * @param access specifies the <code>Access</code> of the 
	 * 		   <code>MonitorEvent</code>. Must not be null and the method type 
	 * 			of the <code>Access</code> must be Monitor.
	 * @param name a <code>String</code> containing the name of the 
	 * 			<code>MonitorEvent</code>. Must not be null.
	 * @param id a <code>String</code> containing the id of the 
	 * 			<code>MonitorEvent</code>. Must not be null.
	 */
	public MonitorEvent(final Access access, final TypeValue dataType, 
						 final String name, final String id) {
		super(access, dataType);
		if(id == null) {
			throw new IllegalArgumentException(
					"The parameter 'id' must not be null!");
		}
		this.name = name;
		this.id = id;
		
	}
	
	/**
	 * Returns the name of the <code>MonitorEvent</code>.
	 * 
	 * @return a <code>String</code> containing the name of the 
	 * 			<code>MonitorEvent</code>. Never returns null.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the id of the <code>MonitorEvent<code>.
	 * 
	 * @return the id
	 */
	public String getID() {
		return this.id;
	}
	
	/**
	 * Sets the id of this MonitorEvent. The id is a unique identifier of this 
	 * <code>MonitorEvent</code> inside a measuring station.
	 * 
	 * @param id a <code>String</code> containing the name of the 
	 * 			<code>MonitorEvent</code>. Must not be null!
	 */
	public void setId(final String id) {
		if(id == null) {
			throw new IllegalArgumentException(
					"The parameter 'id' must not be null!");
		}
		this.id = id;
	}

	/**
	 * Sets the name of this <code>MonitorEvent</code>.
	 * 
	 * @param name a <code>String</code> containing the name of the 
	 * 			<code>MonitorEvent</code>. Must not be null!
	 */
	public void setName(final String name) {
		if(name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		this.name = name;
	}

	/*
	 * Sets the function that is used to communicate with the event trought the control system,
	 * 
	 * @param value The function that is used to communicate with the event trough the control system.
	 */
	//public void setValue( final Function value ) {
	//	this.value = value;
	//}

	/*
	 * This method gives back the function that is used to communicate with the monitor event,
	 * 
	 * @return The Function that is used to communicate with the MonitorEvent.
	 */
	//public Function getValue() {
	//	return this.value;
//	}


	/**
	 * Sets the <code>Access</code> of the <code>MonitorEvent</code>.
	 * 
	 * @param access an <code>Access</code>. Must not be null.
	 */
	@Override
	public void setAccess(final Access access) {
		super.setAccess(access);
	}

	/**
	 * @return a hash
	 */
	@Override
	public int hashCode() {
		// TODO Explain !!!!!!!!!!!!!!!!!!
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Checks whether the argument and calling object are equal.
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
		MonitorEvent other = (MonitorEvent) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}	
}