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
	
	/**
	 * 
	 * @return
	 */
	public static TypeValue createNewTypeValue() {
		TypeValue typeValue = new TypeValue(DataTypes.INT);
		
		return typeValue;
	}
}