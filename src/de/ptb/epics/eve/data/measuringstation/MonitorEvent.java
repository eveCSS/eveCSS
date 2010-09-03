/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.MethodTypes;
import de.ptb.epics.eve.data.TypeValue;
import de.ptb.epics.eve.data.DataTypes;

/**
 * This class represents a Event that is defined inside of a measuring station description.
 * It also provides a mechanism to get connected with a scan modul and represents a start event.
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
	 * This constructor construct a new MonitorEvent object with a string datatype, a empty
	 * monitor process variable and empty name.
	 *
	 */
	public MonitorEvent() {
		this( new Access( MethodTypes.GETCB ), new TypeValue( DataTypes.STRING ), "", "" );
	}
	
	/**
	 * This constructor constructs a new MonitorEvent with a given value (dataType) and a
	 * given access. 
	 * 
	 * @param dataType This paremeter is specifiying the data type of this MonitorEvent. Must not be null.
	 * @param access This paramter is specifying the access of the MonitorEvent. Must not be null and the the method type of the access must be Monitor.
	 */
	public MonitorEvent( final Access access, final TypeValue dataType ) {
		this( access, dataType, "", "" );
	}
	
	/**
	 * This constructor constructs a new MonitorEvent with given name, id an MonitorEvent type.
	 * 
	 * @param name A String objects containing the name of the MonitorEvent. Must not be null.
	 * @param id A String objects containing the id of the MonitorEvent. Must not be null.
	 * @param type The type of the MonitorEvent. Must not be null!
	 */
	public MonitorEvent( final String name, final String id ) {
		this( new Access( MethodTypes.GETCB ), new TypeValue( DataTypes.STRING ), name, id );
	}
	
	/**
	 * This constructor constructs a new MonitorEvent with given data type (value), process variable,
	 * name, id and MonitorEvent type.
	 * 
	 * @param dataType This paremeter is specifiying the data type of this MonitorEvent. Must not be null.
	 * @param access This paramter is specifying the access of the MonitorEvent. Must not be null and the the method type of the access must be Monitor.
	 * @param name A String objects containing the name of the MonitorEvent. Must not be null.
	 * @param id A String objects containing the id of the MonitorEvent. Must not be null.
	 */
	public MonitorEvent( final Access access, final TypeValue dataType, final String name, final String id ) {
		super( access, dataType );
		if( id == null ) {
			throw new IllegalArgumentException( "The parameter 'id' must not be null!" );
		}
		this.name = name;
		this.id = id;
		
	}
	
	/**
	 * Give back the name of the MonitorEvent.
	 * 
	 * @return A String object containing the name of the MonitorEvent. Never returns null.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gives bach the id of the MonitorEvent.
	 * @return
	 */
	public String getID() {
		return this.id;
	}
	
	/**
	 * Sets the id of this MonitorEvent. The id is a unique identifier of this MonitorEvent inside
	 * a measuring station.
	 * 
	 * @param id A String object containing the name of the MonitorEvent. Must not be null!
	 */
	public void setId( final String id) {
		if( id == null ) {
			throw new IllegalArgumentException( "The parameter 'id' must not be null!" );
		}
		this.id = id;
	}

	/**
	 * Sets the name of this MonitorEvent.
	 * 
	 * @param name A String object containing the name of the MonitorEvent. Must not be null!
	 */
	public void setName( final String name) {
		if( name == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
		}
		this.name = name;
	}

	/**
	 * This method sets the function that is used to communicate with the event trought the control system,
	 * 
	 * @param value The function that is used to communicate with the event trough the control system.
	 */
	//public void setValue( final Function value ) {
	//	this.value = value;
	//}

	/**
	 * This method gives back the function that is used to communicate with the monitor event,
	 * 
	 * @return The Function that is used to communicate with the MonitorEvent.
	 */
	//public Function getValue() {
	//	return this.value;
//	}


	/**
	 * Sets the Access of this MonitorEvent.
	 * 
	 * @param access A Access object. Must not be null.
	 */
	@Override
	public void setAccess( final Access access ) {
		super.setAccess( access );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
