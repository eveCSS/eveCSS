/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
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
	 * This array holds all available stepfunctions.
	 */
	private String[] stepfunctions;
	
	/**
	 * This array holds all available linestyles.
	 */
	private String[] linestyles;
	
	/**
	 * This array holds all available colors.
	 */
	private String[] colors;
	
	/**
	 * This array holds all available markstyles.
	 */
	private String[] markstyles;
	
	/**
	 * This array holds all available scan modul types.
	 */
	private String[] smtypes;

	/**
	 * Gives back the array, that is holding all color;
	 * 
	 * @return An Array that is holding all colors.
	 */
	public String[] getColors() {
		return this.colors;
	}

	/**
	 * Gives back the array, that is holding all linestyles.
	 * 
	 * @return An Array that is holding all linestyles.
	 */
	public String[] getLinestyles() {
		return this.linestyles;
	}

	/**
	 * Gives back the array, that is holding all markstyles.
	 * 
	 * @return An Array that is holding all markstyles.
	 */
	public String[] getMarkstyles() {
		return this.markstyles;
	}

	/**
	 * Gives back an array, that is holding all scan modul types.
	 * 
	 * @return An Array that is holding all scan modul types.
	 */
	public String[] getSmtypes() {
		return this.smtypes;
	}

	/**
	 * Gives back an array, that is holding all stepfunctions.
	 * 
	 * @return An Array, that is holding all stepfunctions.
	 */
	public String[] getStepfunctions() {
		return this.stepfunctions;
	}

	/**
	 * Sets the array, that is holding all colors.
	 * 
	 * @param colors A Array that is holding all colors.
	 */
	public void setColors( final String[] colors ) {
		this.colors = colors;
	}

	/**
	 * Sets the array, that is holding all linestyles.
	 * 
	 * @param linestyles A Array that is holding all linestyles.
	 */
	public void setLinestyles( final String[] linestyles) {
		this.linestyles = linestyles;
	}

	/**
	 * Sets the array, that is holding all markstyles.
	 * 
	 * @param markstyles A Array that is holding all markstyles.
	 */
	public void setMarkstyles( final String[] markstyles) {
		this.markstyles = markstyles;
	}

	/**
	 * Sets the array, that is holding all scan module types.
	 * 
	 * @param smtypes A Array, that is holding all scan module types.
	 */
	public void setSmtypes( final String[] smtypes) {
		this.smtypes = smtypes;
	}

	/**
	 * Sets the array, that is holding all stepfunctions.
	 * 
	 * @param stepfunctions A Array, that is holding all step functions.
	 */
	public void setStepfunctions( final String[] stepfunctions) {
		this.stepfunctions = stepfunctions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(colors);
		result = prime * result + Arrays.hashCode(linestyles);
		result = prime * result + Arrays.hashCode(markstyles);
		result = prime * result + Arrays.hashCode(smtypes);
		result = prime * result + Arrays.hashCode(stepfunctions);
		return result;
	}

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
		if (!Arrays.equals(colors, other.colors)) {
			return false;
		}
		if (!Arrays.equals(linestyles, other.linestyles)) {
			return false;
		}
		if (!Arrays.equals(markstyles, other.markstyles)) {
			return false;
		}
		if (!Arrays.equals(smtypes, other.smtypes)) {
			return false;
		}
		if (!Arrays.equals(stepfunctions, other.stepfunctions)) {
			return false;
		}
		return true;
	}
	
	
}
