package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.ScanDescription;

/**
 * @author Marcus Michalsky
 * @since 1.33
 */
public class ScanDescriptionError implements IModelError {
	private final ScanDescription scanDescription;
	private final ScanDescriptionErrorTypes scanDescriptionErrorTypes;

	// TODO no class hierarchy for Error Classes !
	
	/**
	 * @param scanDescription the scanDescription the error occurred in
	 * @param scanDescriptionErrorType the type of error
	 */
	public ScanDescriptionError(ScanDescription scanDescription, 
			ScanDescriptionErrorTypes scanDescriptionErrorType) {
		if (scanDescription == null) {
			throw new IllegalArgumentException(
					"scanDescription must not be null");
		}
		if (scanDescriptionErrorType == null) {
			throw new IllegalArgumentException(
					"scanDescriptionErrorType must not be null");
		}
		this.scanDescription = scanDescription;
		this.scanDescriptionErrorTypes = scanDescriptionErrorType;
	}
	
	/**
	 * Returns the ScanDescription the error occurred in
	 * @return the ScanDescription where the error occurred
	 */
	public ScanDescription getScanDescription() {
		return this.scanDescription;
	}
	
	/**
	 * Return the scan description error type
	 * @return the scan description error type
	 */
	public ScanDescriptionErrorTypes getErrorType() {
		return this.scanDescriptionErrorTypes;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorName() {
		return "Scan Description Error";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorMessage() {
		return this.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ScanDescriptionError [Scan: " + 
				this.scanDescription.getFileName() + 
				", error: " + 
				this.getErrorType().toString();
	}
}
