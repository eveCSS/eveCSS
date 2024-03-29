package de.ptb.epics.eve.data.tests.mothers.measuringstation;

import de.ptb.epics.eve.data.measuringstation.Function;

/**
 * Fabricates Function test fixtures and tailors them.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
public class FunctionMother {
	
	public static Function createNewFunction() {
		return FunctionMother.createNewIntTypeFunction();
	}
	
	/**
	 * @since 1.34
	 */
	public static Function createNewIntTypeFunction() {
		return new Function(AccessMother.createNewIntTypeAccess(),
				TypeValueMother.createNewIntTypeValue());
	}
	
	/**
	 * @since 1.34
	 */
	public static Function createNewDoubleTypeFunction() {
		return new Function(AccessMother.createNewDoubleTypeAccess(),
				TypeValueMother.createNewDoubleTypeValue());
	}
	
	/**
	 * @since 1.35
	 */
	public static Function createNewDateTypeFunction() {
		return new Function(AccessMother.createNewDateTypeAccess(),
				TypeValueMother.createNewDateTypeValue());
	}
	
	/**
	 * @since 1.36
	 */
	public static Function createNewStringTypeFunction() {
		return new Function(AccessMother.createNewStringTypeAccess(),
				TypeValueMother.createNewStringTypeValue());
	}
	
	/**
	 * @since 1.36
	 */
	public static Function createNewDiscreteStringTypeFunction() {
		return new Function(AccessMother.createNewStringTypeAccess(),
				TypeValueMother.createNewDiscreteStringTypeValue());
	}
	
	private FunctionMother() {
		// private
	}
}