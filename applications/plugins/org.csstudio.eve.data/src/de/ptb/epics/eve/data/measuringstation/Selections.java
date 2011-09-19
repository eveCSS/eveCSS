package de.ptb.epics.eve.data.measuringstation;

import java.util.Arrays;

/**
 * This class is holding all selections of the measuring station. The selections
 * are String values, that are describing styles, colors, functions etc.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld( -at -) ptb.de>
 * @version 1.2
 *
 */
public class Selections {
	
	/**
	 * This array holds all available step functions.
	 */
	private String[] stepfunctions;
	
	/**
	 * This array holds all available scan module types.
	 */
	private String[] smtypes;

	/**
	 * Returns an array, that is holding all scan module types.
	 * 
	 * @return An Array that is holding all scan module types.
	 */
	public String[] getSmtypes() {
		return this.smtypes;
	}

	/**
	 * Returns an array, that is holding all step functions.
	 * 
	 * @return An Array, that is holding all step functions.
	 */
	public String[] getStepfunctions() {
		return this.stepfunctions;
	}

	/**
	 * Sets the array, that is holding all scan module types.
	 * 
	 * @param smtypes A Array, that is holding all scan module types.
	 */
	public void setSmtypes(final String[] smtypes) {
		this.smtypes = smtypes;
	}

	/**
	 * Sets the array, that is holding all step functions.
	 * 
	 * @param stepfunctions A Array, that is holding all step functions.
	 */
	public void setStepfunctions(final String[] stepfunctions) {
		this.stepfunctions = stepfunctions;
	}

	/**
	 * @return a hash
	 */
	@Override
	public int hashCode() {
		// TODO Explain !!!!!!!!!!!!!!!!!!!
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(smtypes);
		result = prime * result + Arrays.hashCode(stepfunctions);
		return result;
	}

	/**
	 * Checks whether the argument and calling object are equal.
	 * 
	 * @param obj the <code>Object</code> that should be checked
	 * @return <code>true</code> if objects are equal, 
	 * 			<code>false</code> otherwise
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		Selections other = (Selections) obj;

		if (!Arrays.equals(smtypes, other.smtypes)) {
			return false;
		}
		if (!Arrays.equals(stepfunctions, other.stepfunctions)) {
			return false;
		}
		return true;
	}	
}