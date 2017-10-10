package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import de.ptb.epics.eve.data.measuringstation.Unit;

/**
 * Fabricates Unit test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class UnitMother {
	
	public static Unit createNewUnit() {
		return new Unit(AccessMother.createNewAccess());
	}
}