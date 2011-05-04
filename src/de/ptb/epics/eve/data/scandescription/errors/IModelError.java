package de.ptb.epics.eve.data.scandescription.errors;

/**
 * Provides the basis for errors in the scan description model.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public interface IModelError {

	/**
	 * This method returns a printable name of the error.
	 * 
	 * @return A printable name of the error. Should not return 'null'.
	 */
	public String getErrorName();
	
	/**
	 * This method returns a printable message of the error.
	 * 
	 * @return A printable message of the error. Should not return 'null'
	 */
	public String getErrorMessage();
}