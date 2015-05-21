package de.ptb.epics.eve.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlEnumValue;

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
	 */
	@XmlEnumValue(value = "eq")
	EQ("eq") {
		@Override
		public String toString() {
			return "eq";
		}
	},
	
	/**
	 * Not Equal
	 */
	@XmlEnumValue(value = "ne")
	NE("ne") {
		@Override
		public String toString() {
			return "ne";
		}
	},
	
	/**
	 * Greater Than
	 */
	@XmlEnumValue(value = "gt")
	GT("gt") {
		@Override
		public String toString() {
			return "gt";
		}
	},
	
	/**
	 * Less Than
	 */
	@XmlEnumValue(value = "lt")
	LT("lt") {
		@Override
		public String toString() {
			return "lt";
		}
	};

	private ComparisonTypes(String s) {
	}
	
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