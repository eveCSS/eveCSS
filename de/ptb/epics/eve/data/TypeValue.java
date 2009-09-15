/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * The class TypeValue is a very fundemental class in the datatype system of the Scan Modul Editor.
 * Very often an Device have more limitations in it's possible values than just the primitve datatypes.
 * In this case you have a discrete amount of values like green, red and blue. So the TypeValue class
 * gets intialized with the DataTypes.STRING and value "green red blue".
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.4
 * 
 * @see de.ptb.epics.eve.data.measuringstation.ProcessVariable
 *
 */
public class TypeValue {

	/**
	 * The basic primitive datatype on which this definition based.
	 */
	private final DataTypes type;
	
	/**
	 * The possible values seperated by a whitespace.
	 */
	private String values;
	
	/**
	 * This contructor is used if a TypeValue object should be initialzed that has no further limitations than the base type.
	 * 
	 * @param type The basic primitive datatype on which this definition based.
	 */
	public TypeValue( final DataTypes type ) {
		
		this( type, null );
		
	}
	
	/**
	 * This constructor ist used if a TypeValue object should be initialized that has a limitation of the possible values.
	 * 
	 * @param type The basic primitive datatype on which this definition based.
	 * @param value The possible values seperated by a whitespace
	 */
	public TypeValue( final DataTypes type, final String values ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		this.type = type;
		this.values = values;
		
	}
	
	/**
	 * Returns the datatype of this TypeValue object.
	 * 
	 * @return The datatype.
	 */
	public DataTypes getType() {
		return this.type;
	}
	
	/**
	 * Returns the possible values.
	 * 
	 * @return The possible values.
	 */
	public String getValues() {
		return this.values;
	}

	/**
	 * Sets the possible values of this TypeValue.
	 * 
	 * @param values A String of the possible Values seperated by a single space.
	 */
	public void setValues( final String values ) {
		this.values = values;
	}

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
		
		if( !DataTypes.isValuePossible( this.type, value ) ) {
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
			if( this.type == DataTypes.INT ) {
				int val = Integer.parseInt( value );
				while( it.hasNext() ) {
					if( Integer.parseInt( it.next() ) == val )
						return true;
				}
			} else if( this.type == DataTypes.DOUBLE ) {
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
			if( this.type == DataTypes.INT ) {
				if( Integer.parseInt( value ) > Integer.parseInt( values[0] ) && Integer.parseInt( value ) < Integer.parseInt( values[1] ) ) {
					return true;
				}
			} else if( this.type == DataTypes.DOUBLE ) {
				if( Double.parseDouble( value ) >= Double.parseDouble( values[0] ) && Double.parseDouble( value ) <= Double.parseDouble( values[1] ) ) {
					return true;
				}
			}
		}
		
		return false;
	}

}
