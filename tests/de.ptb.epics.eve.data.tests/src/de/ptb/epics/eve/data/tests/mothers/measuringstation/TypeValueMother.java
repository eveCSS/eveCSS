package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.TypeValue;

/**
 * Fabricates TypeValue test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class TypeValueMother {
	
	public static TypeValue createNewTypeValue() {
		return TypeValueMother.createNewIntTypeValue();
	}
	
	/**
	 * @since 1.34
	 */
	public static TypeValue createNewIntTypeValue() {
		return new TypeValue(DataTypes.INT);
	}
	
	/**
	 * @since 1.34
	 */
	public static TypeValue createNewDoubleTypeValue() {
		return new TypeValue(DataTypes.DOUBLE);
	}
	
	private TypeValueMother() {
		// private
	}
}