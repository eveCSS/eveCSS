package de.ptb.epics.eve.data.measuringstation;

import de.ptb.epics.eve.data.TypeValue;

/**
 * An extension of <code>AbstractTypeValueAccessContainer</code>. It implements 
 * the 'not null' validations for the data type, that must not be null at an 
 * Event and a Trigger.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @author Marcus Michalsky
 */
public abstract class AbstractNothingNullTypeValueAccessContainer extends
		AbstractTypeValueAccessContainer {
	
	/**
	 * Constructs an <code>AbstractNothingNullTypeValueAccessContainer</code> 
	 * with a given access and data type.
	 * 
	 * @param access an <code>Access</code>
	 * @param dataType a <code>TypeValue</code>
	 * @throws IllegalArgumentException if <code>access</code> or 
	 * 			<code>dataType</code> is <code>null</code>
	 */
	public AbstractNothingNullTypeValueAccessContainer(final Access access, 
												final TypeValue dataType) {
		super(access, dataType);
		if(dataType == null) {
			throw new IllegalArgumentException(
					"The parameter 'dataType' must not be null!");
		}
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.TypeValue}.
	 * 
	 * @param dataType the {@link de.ptb.epics.eve.data.TypeValue} that should 
	 * 			be set
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