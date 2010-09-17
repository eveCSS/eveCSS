/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation;


/**
 *  This class represents a motor axis at a measuring station.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.3
 */
public class Option extends AbstractPrePostscanDevice {

	@Override
	public Object clone() {
		final Option option = new Option();
		
		option.setClassName( this.getClassName() );
		option.setDisplaygroup( this.getDisplaygroup() );
		option.setValue( (Function)(this.getValue()!=null?this.getValue().clone():null) );
		
		option.setName( this.getName() );
		option.setId( this.getID() );
		option.setUnit( (Unit)(this.getUnit()!=null?this.getUnit().clone():null) );
		
		return option;
	}
}
