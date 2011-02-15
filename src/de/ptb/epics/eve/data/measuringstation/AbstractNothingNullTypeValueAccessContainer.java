/*
 * Copyright (c) 2001, 2007 Physikalisch-Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 */
package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.TypeValue;

/**
 * An extension of <code>AbstractTypeValueAccessContainer</code>. It implements 
 * the 'not null' validations for the data type, that must not be null at an 
 * Event and a Trigger.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.1 
 * @see de.ptb.epics.eve.data.measuringstation.Event
 */
public abstract class AbstractNothingNullTypeValueAccessContainer extends
		AbstractTypeValueAccessContainer {
	
	/**
	 * Constructs a new <code>AbstractNothingNullTypeValueAccessContainer</code> 
	 * with a given access and data type.
	 * 
	 * @param access an <code>Access</code> Must not be null.
	 * @param dataType a <code>TypeValue</code>
	 * @throws IllegalArgumentException if access or dataType is 
	 * 			<code>null</code>
	 */
	public AbstractNothingNullTypeValueAccessContainer(final Access access, 
												final TypeValue dataType) {
		// TODO are the following 2 operations in the wrong order ?
		// They should be vice versa ?!? checking == null after passing it as
		// argument is not necessary ? But super must be the first operation ???
		super(access, dataType);
		if(dataType == null) {
			throw new IllegalArgumentException(
					"The parameter 'dataType' must not be null!");
		}
	}
	
	/**
	 * Sets the data type of this 
	 * <code>AbstractNothingNullTypeValueAccessContainer</code>
	 * 
	 * @param dataType a <code>TypeValue</code>
	 * @throws IllegalArgumentException if the argument is <code>null</code>
	 */
	@Override
	public void setDataType(final TypeValue dataType) {
		if(dataType == null) {
			throw new IllegalArgumentException(
					"The parameter 'dataType' must not be null");
		}
		super.setDataType(dataType);
	}
}