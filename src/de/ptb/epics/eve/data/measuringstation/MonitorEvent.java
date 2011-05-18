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
 * @author Marcus Michalsky
 */
public class MonitorEvent extends AbstractNothingNullTypeValueAccessContainer {

	// the name
	private String name;
	
	// the id
	private String id;

	/**
	 * Constructs a <code>MonitorEvent</code> with a {@link java.lang.String} 
	 * as data type, an empty monitor process variable and empty name.
	 */
	public MonitorEvent() {
		this(new Access(MethodTypes.GETCB), 
			  new TypeValue(DataTypes.STRING), "", "");
	}
	
	/**
	 * Constructs a <code>MonitorEvent</code> with a given 
	 * {@link de.ptb.epics.eve.data.TypeValue} and a given 
	 * {@link de.ptb.epics.eve.data.measuringstation.Access}. 
	 * 
	 * @param access the {@link de.ptb.epics.eve.data.measuringstation.Access} 
	 * 			that should be set. Must not be null and the method 
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
	 * @param dataType the {@link de.ptb.epics.eve.data.TypeValue}
	 * @param access the {@link de.ptb.epics.eve.data.measuringstation.Access}
	 * @param name the name
	 * @param id the id
	 * @throws IllegalArgumentException if at least one of 
	 * 			<code>access, dataType</code> or <code>id</code> is 
	 * 			<code>null</code> 
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
	 * Returns the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the id.
	 * 
	 * @return the id
	 */
	public String getID() {
		return this.id;
	}
	
	/**
	 * Sets the id. The id is a unique identifier within 
	 * the measuring station. 
	 * 
	 * @param id the id that should be set
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
	 * Sets the name.
	 * 
	 * @param name a the name that should be set
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setName(final String name) {
		if(name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
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