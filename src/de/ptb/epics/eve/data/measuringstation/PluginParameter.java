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

import de.ptb.epics.eve.data.PluginDataType;

/**
 * This class represents the parameter of a plugin. 
 * 
 * @author Stephan Rehfeld <stephan.rehfel( -at )ptb.de>
 *
 */
public class PluginParameter {

	/**
	 * The name of parameter.
	 */
	private String name;
	
	/**
	 * The default value of the parameter.
	 */
	private String defaultValue;
	
	/**
	 * A flag if this parameter is mandatory
	 */
	private boolean mandatory;
	
	/**
	 * The type of the plugin paramter.
	 */
	private PluginDataType type;
	
	/**
	 * The string that holds the limitations of the plugin Parameter.
	 */
	private String values;
	
	/**
	 * This constructor creates a new plugin parameter.
	 * 
	 * @param name The name of the plugin parameter. Must not be null.
	 * @param type The type of the plugin parameter. Must not be null.
	 * @param defaultValue The default value for the plugin parameter. Maybe null.
	 * @param mandatory A flag if the parameter ist mandatory.
	 */
	public PluginParameter( final String name, final PluginDataType type, final String defaultValue, final boolean mandatory ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		if( name == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
		}
		this.type = type;
		this.name = name;
		this.defaultValue = defaultValue;
		this.mandatory = mandatory;
	}

	/**
	 * This method gives back the default value of the parameter or null if it has no default parameter.
	 *
	 * @return The default value or null, if no default value is set.
	 */
	public String getDefaultValue() {
		// Wenn kein defaultValue gesetzt ist, einen erzeugen!
		if (defaultValue != null) {
			return defaultValue;
		}
		else {
			if (this.isDiscrete()) {
				// Value ist diskrete, ersten Eintrag als default setzen
				String[] values = this.getDiscreteValues().toArray(new String[0]);
				return values[0];
			}
			else {
				// kein diskreter Wert vorhanden, default Wert über Typ ermitteln
				return PluginDataType.getDefaultValue(type);
			}
		}
	}

	/**
	 * This methods sets the default parameter.
	 * 
	 * @param defaultValue The new default value of 'null', if a default value is not required.
	 */
	public void setDefaultValue( final String defaultValue ) {
		this.defaultValue = defaultValue;
	}

	/**
	 * This method gives back if this this parameter is mandatory for the plugin.
	 * 
	 * @return Returns 'true' if the parameter is mandatory.
	 */
	public boolean isMandatory() {
		return this.mandatory;
	}

	/**
	 * This methods sets if the parameter is mandatory. 
	 * 
	 * @param mandatory Pass 'true' if the parameter is mandatory and 'false' if not.
	 */
	public void setMandatory( final boolean mandatory ) {
		this.mandatory = mandatory;
	}

	/**
	 * This method gives back the name of parameter.
	 * 
	 * @return The name of the parameter.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * This method sets the name of the parameter.
	 * 
	 * @param name The name of the parameter. Must not be null.
	 */
	public void setName( final String name ) {
		if( name == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
		}
		this.name = name;
	}

	/**
	 * This method gives back the type of the parameter.
	 * 
	 * @return The type of the parameter.
	 */
	public PluginDataType getType() {
		return this.type;
	}

	/**
	 * This method sets the type of the parameter.
	 * 
	 * @param type The type of the parameter. Must not be 'null'
	 */
	public void setType( final PluginDataType type ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		this.type = type;
	}

	/**
	 * This method gives back the String that describes value limits for the parameter.
	 * 
	 * @return The String that describes value limits for the parameter.
	 */
	public String getValues() {
		return values;
	}

	/**
	 * This method sets the values string of the
	 * 
	 * @param values
	 */
	public void setValues( final String values ) {
		this.values = values;
	}
	
	/**
	 * This method gives back if this parameter can only have discrete values.
	 * 
	 * @return Gives back 'true' if the parameter only can have discrete values.
	 */
	public boolean isDiscrete() {
		if( values == null ) {
			return false;
		}
		StringBuffer buffer = new StringBuffer( this.values );
		boolean escape = false;
		escape = false;
		for( int i = 0; i < buffer.length(); ++i ) {
			if( buffer.charAt( i ) == '"' ) {
				escape = !escape;
			} else if( !escape && buffer.charAt( i ) == ',' ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * If the parameter only can take discrete values, this methods returns a List contains all possible values.
	 * 
	 * @return Gives back a list of all possible values.
	 */
	public List<String> getDiscreteValues() {
		if( this.isDiscrete() ) {
			StringBuffer buffer = new StringBuffer( this.values );
			boolean escape = false;
			
			for( int i = 0; i < buffer.length(); ++i ) {
				if( buffer.charAt( i ) == '"' ) {
					escape = !escape;
				} else if( !escape && buffer.charAt( i ) == ' ' ) {
					buffer.deleteCharAt( i );
					--i;
				}
			}
			
			escape = false;
			
			int lastIndex = 0;
			List<String> elements = new ArrayList<String>();
			for( int i = 0; i < buffer.length(); ++i ) {
				if( buffer.charAt( i ) == '"' ) {
					escape = !escape;
				} else if( !escape && buffer.charAt( i ) == ',' ) {
					StringBuffer buffer2 = new StringBuffer( buffer.substring( lastIndex, i ) );
					if( buffer2.charAt( 0 ) == '"' ) {
						buffer2.deleteCharAt( 0 );
					}
					if( buffer2.charAt( buffer2.length() - 1 ) == '"' ) {
						buffer2.deleteCharAt( buffer2.length() - 1 );
					}
					elements.add( buffer2.toString() );
					lastIndex = i + 1;
				}
			}
			StringBuffer buffer2 = new StringBuffer( buffer.substring( lastIndex, buffer.length() ) );
			if( buffer2.charAt( 0 ) == '"' ) {
				buffer2.deleteCharAt( 0 );
			}
			if( buffer2.charAt( buffer2.length() - 1 ) == '"' ) {
				buffer2.deleteCharAt( buffer2.length() - 1 );
			}
			elements.add( buffer2.toString() );
			
			return elements;
		} else {
			return null;
		}
	}
	
	/**
	 * Finds out if a value is possible under the constraints of this TypeValue object.
	 * 
	 * @param value The value that should be checked
	 * @return Returns true if the value fits the constrains, false if not.
	 */
	public boolean isValuePossible( final String value ) {
		if( value == null ) {
			throw new IllegalArgumentException( "The parameter value must not be null" );
		}
		
		if( !PluginDataType.isValuePossible( this.type, value ) ) {
			return false;
		}
		
		if( values == null ) {
			return true;
		}
		
		
		StringBuffer buffer = new StringBuffer( this.values );
		
		boolean escape = false;
		for( int i = 0; i < buffer.length(); ++i ) {
			if( buffer.charAt( i ) == '"' ) {
				escape = !escape;
			} else if( !escape && buffer.charAt( i ) == ' ' ) {
				buffer.deleteCharAt( i );
				--i;
			}
		}
		
		escape = false;
		
		if( this.isDiscrete() ) {
			escape = false;
			int lastIndex = 0;
			List<String> elements = new ArrayList<String>();
			for( int i = 0; i < buffer.length(); ++i ) {
				if( buffer.charAt( i ) == '"' ) {
					escape = !escape;
				} else if( !escape && buffer.charAt( i ) == ',' ) {
					StringBuffer buffer2 = new StringBuffer( buffer.substring( lastIndex, i ) );
					if( buffer2.charAt( 0 ) == '"' ) {
						buffer2.deleteCharAt( 0 );
					}
					if( buffer2.charAt( buffer2.length() - 1 ) == '"' ) {
						buffer2.deleteCharAt( buffer2.length() - 1 );
					}
					elements.add( buffer2.toString() );
					lastIndex = i + 1;
				}
			}
			StringBuffer buffer2 = new StringBuffer( buffer.substring( lastIndex, buffer.length() ) );
			if( buffer2.charAt( 0 ) == '"' ) {
				buffer2.deleteCharAt( 0 );
			}
			if( buffer2.charAt( buffer2.length() - 1 ) == '"' ) {
				buffer2.deleteCharAt( buffer2.length() - 1 );
			}
			elements.add( buffer2.toString() );
			
			
			Iterator<String> it = elements.iterator();
			if( this.type == PluginDataType.INT ) {
				int val = Integer.parseInt( value );
				while( it.hasNext() ) {
					if( Integer.parseInt( it.next() ) == val )
						return true;
				}
			} else if( this.type == PluginDataType.DOUBLE ) {
				double val = Double.parseDouble( value );
				while( it.hasNext() ) {
					if( Double.parseDouble( it.next() ) == val )
						return true;
						
				}
			} else {
				while( it.hasNext() ) {
					if( it.next().equals( value ) )
						return true;
				}
			}
			
		} else {
			String[] values = this.values.split( "to" );
			if( values.length != 2 ) {
				this.values = null;
				return true;
			}
			System.err.println( this.values );
			System.err.println( value );
			if( this.type == PluginDataType.INT ) {
				if( Integer.parseInt( value ) > Integer.parseInt( values[0] ) && Integer.parseInt( value ) < Integer.parseInt( values[1] ) ) {
					return true;
				}
			} else if( this.type == PluginDataType.DOUBLE ) {
				if( Double.parseDouble( value ) >= Double.parseDouble( values[0] ) && Double.parseDouble( value ) <= Double.parseDouble( values[1] ) ) {
					return true;
				}
			}
		}
		
		return false;
	}
	
}
