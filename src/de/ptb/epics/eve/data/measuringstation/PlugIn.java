/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
	 * The location of the plugin.
	 */
	private String location;
	
	/**
	 * The type of the plugin.
	 */
	private PluginTypes type;
	
	/**
	 * This List contains all parameters of the plugin.
	 */
	private List< PluginParameter > parameters;
	
	/**
	 * This constructor construct a new PlugIn with a given type.
	 * 
	 * @param type The type of the plugin. Must not be null.
	 */
	public PlugIn( final PluginTypes type ) {
		this( "", "", type );
	}
	
	/**
	 * This constructor constructs a new PlugIn with the given values.
	 * 
	 * @param name The name of the plugin. Must not be null!
	 * @param location The location of the plugin in the file system. Must not be null!
	 * @param type The type of the plugin. Must not be null.
	 * @param parameters A List, that is holding the parameters of the plugin. Pass 'null' if you don't want to add parameters while contruction.
	 */
	public PlugIn( final String name, final String location, final PluginTypes type ) {
		if( name == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
		}
		if( location == null ) {
			throw new IllegalArgumentException( "The parameter 'location' must not be null!" );
		}
		
		this.name = name;
		this.location = location;
		this.type = type;
		
		this.parameters = new ArrayList< PluginParameter >();
		
	}
	
	/**
	 * Gives back the name of the PlugIn.
	 * 
	 * @return A String object, containing the name of the plugin. Never returns null!
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Gives back the location of the PlugIn.
	 * 
	 * @return A String object, containing the location of the plugin. Never returns null!
	 */
	public String getLocation() {
		return this.location;
	}
	
	/**
	 * Gives back the type of the Plugin.
	 * 
	 * @return The type of the PlugIn. never returns null!
	 */
	public PluginTypes getType() {
		return this.type;
	}
	
	/**
	 * Gives back a copy of the internal list, that is holding all parameters.
	 * 
	 * @return A List object, that is holding the parameters. Never returns null!
	 */
	public List< PluginParameter > getParameters() {
		return new ArrayList< PluginParameter >( this.parameters );
	}
	
	/**
	 * Gives back the amount of parameters.
	 * 
	 * @return The amount of parameters.
	 */
	public int parameters() {
		return this.parameters.size();
	}
	
	/**
	 * Gives back a Iterator over the internal List of parameters.
	 * 
	 * @return A Iterator object over the internal List of parameters. Never returns null.
	 */
	public Iterator< PluginParameter > parameterIterator() {
		return this.parameters.iterator();
	}
	
	/**
	 * Sets the location of the plugin.
	 * 
	 * @param location A String object, containg the location. Must not be null!
	 */
	public void setLocation( final String location ) {
		if( location == null ) {
			throw new IllegalArgumentException( "The parameter 'location' must not be null!" );
		}
		this.location = location;
	}

	/**
	 * Sets the name of the plugin.
	 * 
	 * @param name A String object, containing the name of the location. Must not be null!
	 */
	public void setName( final String name ) {
		if( name == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
		}
		this.name = name;
	}

	/**
	 * Sets the Type of the PlugIn.
	 * 
	 * @see de.trustedcode.scanmoduleditor.data.EventTypes
	 * @param type One of the values of EventTypes. Must not be null.
	 */
	public void setType( final PluginTypes type ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameters 'type must not be null!" );
		}
		this.type = type;
	}

	/**
	 * Adds a parameter to the plugin.
	 * 
	 * @param parameter A object containing the parameter. Must not be null!
	 * @return Gives back 'true' if the parameter has been added.
	 */
	public boolean add( final PluginParameter parameter ) {
		if( parameter == null ) {
			throw new IllegalArgumentException( "The parameter 'parameter' must not be null!" );
		}
		return parameters.add( parameter );
	}

	/**
	 * This method removes all parameters from the PlugIn.
	 *
	 */
	public void clear() {
		parameters.clear();
	}

	/**
	 * Removes a parameter from the plug in.
	 * 
	 * @param parameter The parameter that should be removed. The parameter must not be null.
	 * @return Gives back 'true' if the parameter has been remove.
	 */
	public boolean remove( final PluginParameter parameter ) {
		if( parameter == null ) {
			throw new IllegalArgumentException( "The parameter 'parameter' must not be null!" );
		}
		return parameters.remove( parameter );
	}
	
}
