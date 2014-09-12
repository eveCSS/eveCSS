package de.ptb.epics.eve.data.measuringstation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.ptb.epics.eve.data.PluginTypes;

/**
 *  This class represents a plug in at a measuring station.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 */
public class PlugIn {
	
	/**
	 * The name of the plug in.
	 */
	private String name;
	
	/**
	 * The location of the plug in.
	 */
	private String location;
	
	/**
	 * The type of the plug in.
	 */
	private PluginTypes type;
	
	/**
	 * This List contains all parameters of the plug in.
	 */
	private List<PluginParameter> parameters;

	/**
	 * the measuring station object
	 */
	private MeasuringStation measuringStation;
	
	/**
	 * Constructs a <code>PlugIn</code> with a given type.
	 * 
	 * @param type the type of the plug in. Must not be null.
	 */
	public PlugIn(final PluginTypes type, final MeasuringStation measuringStation) {
		this("", "", type, measuringStation);
	}
	
	/**
	 * Constructs a <code>PlugIn</code> with the given values.
	 * 
	 * @param name the name of the plug in.
	 * @param location the location of the plug in in the file system.
	 * @param type the type of the plug in. Must not be null.
	 * @param parameters a <code>List</code> holding the parameters of the 
	 * 		   plug in. Pass <code>null</code> if you don't want parameters.
	 * @throws IllegalArgumentException if name or location is 
	 * 			<code>null</code>
	 */
	public PlugIn(final String name, final String location, 
					final PluginTypes type, 
					final MeasuringStation measuringStation) {
		if(name == null) {
			throw new IllegalArgumentException(
					"The parameter 'name' must not be null!");
		}
		if(location == null) {
			throw new IllegalArgumentException(
					"The parameter 'location' must not be null!");
		}
		
		this.name = name;
		this.location = location;
		this.type = type;
		this.measuringStation = measuringStation;
		
		this.parameters = new ArrayList<PluginParameter>();
	}
	
	/**
	 * Returns the name of the <code>PlugIn</code>.
	 * 
	 * @return a <code>String</code> containing the name of the plug in.
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the location of the <code>PlugIn</code>.
	 * 
	 * @return a <code>String</code> containing the location of the plug in.
	 */
	public String getLocation() {
		return this.location;
	}
	
	/**
	 * Returns the type of the <code>PlugIn</code>.
	 * 
	 * @return the type of the plug in.
	 */
	public PluginTypes getType() {
		return this.type;
	}

	/**
	 * Returns the measuring Station.
	 * 
	 * @return the measuringStation
	 */
	public MeasuringStation getMeasuringstation() {
		return this.measuringStation;
	}
	
	/**
	 * Returns a copy of the internal list, that is holding all parameters.
	 * 
	 * @return A <code>List</code> holding the parameters.
	 */
	public List<PluginParameter> getParameters() {
		return new ArrayList<PluginParameter>(this.parameters);
	}
	
	/**
	 * Returns the amount of parameters.
	 * 
	 * @return The amount of parameters.
	 */
	public int parameters() {
		return this.parameters.size();
	}
	
	/**
	 * Returns an <code>Iterator</code> over the internal list of parameters.
	 * 
	 * @return an <code>Iterator</code> over the internal List of parameters.
	 */
	public Iterator<PluginParameter> parameterIterator() {
		return this.parameters.iterator();
	}
	
	/**
	 * Sets the location of the plug in.
	 * 
	 * @param location a <code>String</code> containing the location.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setLocation(final String location) {
		if(location == null) {
			throw new IllegalArgumentException(
					"The parameter 'location' must not be null!");
		}
		this.location = location;
	}

	/**
	 * Sets the name of the plug in.
	 * 
	 * @param name a <code>String</code> containing the name of the location.
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
	 * Sets the Type of the <code>PlugIn</code>.
	 * 
	 * @param type one of the values of <code>EventTypes</code>.
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public void setType(final PluginTypes type) {
		if(type == null) {
			throw new IllegalArgumentException(
					"The parameters 'type must not be null!");
		}
		this.type = type;
	}

	/**
	 * Adds a parameter to the plug in.
	 * 
	 * @param parameter an object containing the parameter.
	 * @return <code>true</code> if the parameter has been added, 
	 * 			<code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public boolean add(final PluginParameter parameter) {
		if(parameter == null) {
			throw new IllegalArgumentException(
					"The parameter 'parameter' must not be null!");
		}
		return parameters.add(parameter);
	}

	/**
	 * This method removes all parameters from the <code>PlugIn</code>.
	 */
	public void clear() {
		parameters.clear();
	}

	/**
	 * Removes a parameter from the plug in.
	 * 
	 * @param parameter the parameter that should be removed.
	 * @return <code>true</code> if the parameter has been removed, 
	 * 			<code>false</code> otherwise
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public boolean remove(final PluginParameter parameter) {
		if(parameter == null) {
			throw new IllegalArgumentException(
					"The parameter 'parameter' must not be null!");
		}
		return parameters.remove(parameter);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parameters == null) ? 0 : parameters.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/**
	 * Checks whether the argument and calling object are equal.
	 * 
	 * @param obj the <code>Object</code> that should be checked.
	 * @return <code>true</code> if objects are equal, 
	 * 			<code>false</code> otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PlugIn other = (PlugIn) obj;
		if (location == null) {
			if (other.location != null) {
				return false;
			}
		} else if (!location.equals(other.location)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (parameters == null) {
			if (other.parameters != null) {
				return false;
			}
		} else if (!parameters.equals(other.parameters)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}	
}