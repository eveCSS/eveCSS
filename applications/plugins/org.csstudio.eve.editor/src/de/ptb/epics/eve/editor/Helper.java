/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor;

public class Helper {

	public static boolean contains( final String[] array, final String string ) {
		for( int i = 0; i < array.length; ++i ) {
			if( array[i].equals( string ) ) {
				return true;
			}
		}
		return false;
	}
	
}
