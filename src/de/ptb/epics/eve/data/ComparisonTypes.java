/* 
 * Copyright (c) 2001, 2008 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * represents the available comparison types
 * (equals, not equals, greater than, less than). 
 *
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 * @see de.ptb.epics.eve.data.scandescription.Limit
 */
public enum ComparisonTypes {
	
	/**
	 * Equals
	 *
	 * @uml.property  name="eQ"
	 * @uml.associationEnd  
	 */
	EQ, 
	/**
	 * Not Equals
	 *
	 * @uml.property  name="nE"
	 * @uml.associationEnd  
	 */
	NE,
	/**
	 * Greater Than
	 *
	 * @uml.property  name="gT"
	 * @uml.associationEnd  
	 */
	GT,
	/**
	 * Less Than
	 *
	 * @uml.property  name="lT"
	 * @uml.associationEnd  
	 */
	LT;

	/**
	 * translates a ComparisonType, written in small letters, into it's 
	 * corresponding enum field. 
	 *
	 * @param name ComparisonType in small letters. Must not be null.
	 * @return corresponding ComparisonType or null if name is not valid 
	 */
	public static ComparisonTypes stringToType( final String name ) {
		if( name == null ) {
			throw new IllegalArgumentException( 
					"The parameter 'name' must not be null!" );
		}
		if( name.equals( "eq" ) ) {
			return ComparisonTypes.EQ;
		} else if( name.equals( "ne" ) ) {
			return ComparisonTypes.NE;
		} else if( name.equals( "gt" ) ) {
			return ComparisonTypes.GT;
		} else if( name.equals( "lt" ) ) {
			return ComparisonTypes.LT;
		} 
		return null;		
	}
	
	/**
	 * translates a ComparisonType into it's corresponding string,written in 
	 * small letters. 
	 * 
	 * @param type ComparisonType to be converted to a string. Must not be null.
	 * @return corresponding string, null if type was invalid.
	 */
	public static String typeToString( final ComparisonTypes type ) {
		if( type == null ) {
			throw new IllegalArgumentException( 
					"The parameter 'type' must not be null!" );
		}
		
		switch( type ) {
			case EQ: return "eq";
			case NE: return "ne";
			case GT: return "gt";
			case LT: return "lt";
		}
		return null;
	}
	
	/**
	 * translates an array of ComparsionTypes into an array of corresponding
	 * Strings.
	 * 
	 * @param type array of ComparisonTypes to be translated. Must not be null.
	 * @return array of strings (same order as given Comparison-Type Array).
	 */
	public static String[] typeToString( final ComparisonTypes[] type ) {
		if( type == null ) {
			throw new IllegalArgumentException( 
					"The parameter 'type' must not be null!" );
		}
		final String[] names = new String[ type.length ];
		for( int i = 0; i < type.length; ++i ) {
			names[i] = ComparisonTypes.typeToString( type[i] );
		}
		return names;
	}
	
	/**
	 * translates a List of ComparsionTypes into a List of corresponding
	 * Strings.
	 * Notice, that they must not any null element in the list.
	 * 
	 * @param type A List of ComparisonTypes to be translated. Must not be null.
	 * @return A List in the same order as the given Comparison-Type List.
	 */
	public static List<String> typeToString(final List<ComparisonTypes> type) {
		if( type == null ) {
			throw new IllegalArgumentException( 
					"The parameter 'type' must not be null!" );
		}
		final List<String> names = new ArrayList<String>( type.size() );
		final Iterator<ComparisonTypes> it = type.iterator();
		while( it.hasNext() ) {
			names.add( ComparisonTypes.typeToString( it.next() ) );
		}
		return names;
	}	
}
