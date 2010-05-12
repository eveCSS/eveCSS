/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.TypeValue;

/**
 * The AbstractNothingNullTypeValueAccessContainer is an extension of the
 * AbstractTypeValueAccessContainer. Is implements the not null checkings
 * for the data type, that must be not null at a Event and a Trigger.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.1
 * 
 * 
 * @see de.ptb.epics.eve.data.measuringstation.Event
 * @see de.ptb.epics.eve.data.measuringstation.Trigger
 * 
 */
public abstract class AbstractNothingNullTypeValueAccessContainer extends
		AbstractTypeValueAccessContainer {
	
	/**
	 * This constructor construct a new AbstractNothingNullTypeValuePVContainer with
	 * a given access and data type.
	 * 
	 * @param access A Access object. Must not be null.
	 * @param dataType A TypeValue object. Must not be null.
	 */
	public AbstractNothingNullTypeValueAccessContainer( final Access access, final TypeValue dataType ) {
		super( access, dataType );
		if( dataType == null ) {
			throw new IllegalArgumentException( "The parameter 'dataType' must not be null!" );
		}
	}
	
	/**
	 * Sets the data type of this AbstractNothingNullTypeValuePVContainer.
	 * 
	 * @param dataType A TypeValu object. Must not be null.
	 */
	@Override
	public void setDataType( final TypeValue dataType ) {
		if( dataType == null ) {
			throw new IllegalArgumentException( "The parameter 'dataType' must not be null" );
		}
		super.setDataType( dataType );
	}

}
