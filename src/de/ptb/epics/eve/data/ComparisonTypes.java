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
 * This enum represents all available comparison types, like equals, not equals, greater than and less than. It is user by the Limit class of the scan description.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @see de.ptb.epics.eve.data.scandescription.Limit
 * @version 1.3
 *
 */
public enum ComparisonTypes {
	
	/**
	 * Equals 
	 */
	EQ, 
	/**
	 * Not Equals
	 */
	NE,
	/**
	 * Greater than
	 */
	GT,
	/**
	 * Less than
	 */
	LT;

	/**
	 * This static method is translating a comparisonType, that is written in small letter
	 * into it's correspondenting enum field, like it's used in scan and/or measuring station
	 * description. If the given name is not a valid comparisonType the method return null.
	 * 
	 * @param name The comparisonType in small letters. Must not be null.
	 * @return Returns the corespondenting ComparisonType or null if the name 
	 */
	public static ComparisonTypes stringToType( final String name ) {
		if( name == null ) {
			throw new IllegalArgumentException( "The parameter 'name' must not be null!" );
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
	 * This static method is translating a comparison type into a correspndenting string,
	 * written in small letters, like it's used in scan and/or measuring station description.
	 * 
	 * @param type The ComparisonType of which you want to have the string. Must not be null.
	 * @return The correpondenting string, null if the type was invalid.
	 */
	public static String typeToString( final ComparisonTypes type ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		
		switch( type ) {
			case EQ:
				return "eq";
			case NE:
				return "ne";
			case GT:
				return "gt";
			case LT:
				return "lt";
		}
		return null;
	}
	
	/**
	 * This static method is translating an Array of ComparsionTypes into an array of the
	 * correpondenting Strings, like it's used in scan and/or measuring station description.
	 * Notice, that no field in the array must be null!
	 * 
	 * @param type An Array of the ComparisonTypes, which have to be translated. Must not be null.
	 * @return A String-Array in the same order as the given Comparison-Type Array.
	 */
	public static String[] typeToString( final ComparisonTypes[] type ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		final String[] names = new String[ type.length ];
		for( int i = 0; i < type.length; ++i ) {
			names[i] = ComparisonTypes.typeToString( type[i] );
		}
		return names;
	}
	
	/**
	 * This static method is translating an List of ComparsionTypes into an List of the
	 * correpondenting Strings, like it's used in scan and/or measuring station description.
	 * Notice, that they must not any null element in the list.
	 * 
	 * @param type A List of the ComparisonTypes, which have to be translated. Must not be null.
	 * @return A List in the same order as the given Comparison-Type List.
	 */
	public static List<String> typeToString( final List<ComparisonTypes> type ) {
		if( type == null ) {
			throw new IllegalArgumentException( "The parameter 'type' must not be null!" );
		}
		final List<String> names = new ArrayList<String>( type.size() );
		final Iterator<ComparisonTypes> it = type.iterator();
		while( it.hasNext() ) {
			names.add( ComparisonTypes.typeToString( it.next() ) );
		}
		return names;
		
	}

	
}
