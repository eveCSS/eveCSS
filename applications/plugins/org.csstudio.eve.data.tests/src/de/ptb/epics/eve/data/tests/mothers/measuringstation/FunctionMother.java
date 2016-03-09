package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import java.beans.PropertyChangeEvent;

import de.ptb.epics.eve.data.measuringstation.Function;

/**
 * Fabricates Function test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class FunctionMother {
	
	/**
	 * 
	 * @return
	 */
	public static Function createNewFunction() {
		return new Function(AccessMother.createNewAccess(),
				TypeValueMother.createNewTypeValue()) {
			public void readDiscreteValues() {};
			public synchronized void propertyChange(PropertyChangeEvent evt) {};
		};
	}
}