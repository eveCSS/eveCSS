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
 * Contains all available comparison types 
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
	 * Not Equal
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
	 * Converts a <code>String</code> into its corresponding 
	 * <code>ComparisonType</code>.
	 *
	 * @param name the <code>String</code> that should be translated<br>
	 * 				<b>Precondition:</b> name is element of {"eq","ne","gt","lt"}
	 * @return corresponding <code>ComparisonType</code>
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public static ComparisonTypes stringToType(final String name) {
		if(name == null) {
			throw new IllegalArgumentException( 
					"The parameter 'name' must not be null!");
		}
		if(name.equals("eq")) {
			return ComparisonTypes.EQ;
		} else if(name.equals("ne")) {
			return ComparisonTypes.NE;
		} else if(name.equals("gt")) {
			return ComparisonTypes.GT;
		} else if(name.equals("lt")) {
			return ComparisonTypes.LT;
		} 
		return null;		
	}
	
	/**
	 * Converts a <code>ComparisonType</code> into its corresponding 
	 * <code>String</code>. 
	 * 
	 * @param type <code>ComparisonType</code> to be converted to a 
	 * 				<code>String</code><br>
	 * @return corresponding <code>String</code>
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public static String typeToString(final ComparisonTypes type) {	
		if(type == null) {
			throw new IllegalArgumentException( 
					"The parameter 'type' must not be null!");
		}
		
		switch(type) {
			case EQ: return "eq";
			case NE: return "ne";
			case GT: return "gt";
			case LT: return "lt";
		}
		
		return null;
	}
	
	/**
	 * Converts an array of <code>ComparsionType</code>s into an array of 
	 * corresponding <code>String</code>s.
	 * 
	 * @param type array of <code>ComparisonType</code>s to be translated<br>
	 * 		   <b>Precondition:</b> contains no <code>null</code> elements
	 * @return array of <code>String</code>s (same order as given 
	 * 			<code>ComparisonType</code> Array).
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public static String[] typeToString(final ComparisonTypes[] type) {
		if(type == null) {
			throw new IllegalArgumentException( 
					"The parameter 'type' must not be null!");
		}	
		
		final String[] names = new String[type.length];
	
		for(int i=0; i<type.length; ++i) {
			names[i] = ComparisonTypes.typeToString(type[i]);
		}
		
		return names;
	}
	
	/**
	 * Converts a <code>List</code> of <code>ComparisonType</code>s into a 
	 * <code>List</code> of corresponding <code>String</code>s.
	 * 
	 * @param type the <code>List</code> of <code>ComparisonTypes</code> 
	 * 		   to be translated<br>
	 * 		   <b>Precondition:</b> contains no <code>null</code> elements
	 * @return A <code>List</code> (same order)
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	public static List<String> typeToString(final List<ComparisonTypes> type) 
	{		
		if(type == null) {
			throw new IllegalArgumentException( 
					"The parameter 'type' must not be null!");
		}
		
		final List<String> names = new ArrayList<String>(type.size());
		final Iterator<ComparisonTypes> it = type.iterator();
		
		while(it.hasNext()) {
			names.add(ComparisonTypes.typeToString(it.next()));
		}		
		return names;
	}	
}