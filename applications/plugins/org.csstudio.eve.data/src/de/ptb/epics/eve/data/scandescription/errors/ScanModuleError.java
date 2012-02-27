package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * <code>ScanModuleError</code> represents an error which occurred in a 
 * {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
 * 
 * @author ?
 * @author Hartmut Scherr
 */
public class ScanModuleError implements IModelError {

	// the channel where the error occurred
	private final ScanModule scanModule;
	
	// the error type
	private final ScanModuleErrorTypes errorType;
	
	/**
	 * Constructs a <code>ScanModuleError</code>.
	 * 
	 * @param channel the {@link de.ptb.epics.eve.data.scandescription.ScanModule} 
	 * 				  where the error occurred
	 * @param errorType the error type
	 * @throws IllegalArgumentException 
	 * 			<ul>
	 * 			  <li>if <code>scanModule</code> is <code>null</code></li>
	 * 			  <li>if <code>errorType</code> is <code>null</code></li»
	 * 			</ul>
	 */
	public ScanModuleError(final ScanModule scanModule, final 
			ScanModuleErrorTypes errorType) {
		if(scanModule == null) {
			throw new IllegalArgumentException(
					"The parameter 'scanModule' must not be null!");
		}
		if(errorType == null) {
			throw new IllegalArgumentException(
					"The parameter 'errorType' must not be null!");
		}
		this.scanModule = scanModule;
		this.errorType = errorType;
	}

	/**#
	 * Returns the {@link de.ptb.epics.eve.data.scandescription.ScanModule} where 
	 * the error occurred.
	 * 
	 * @return the {@link de.ptb.epics.eve.data.scandescription.ScanModule} where 
	 * 		   the error occurred
	 */
	public ScanModule getScanModule() {
		return this.scanModule;
	}

	/**
	 * Returns the error type as defined in 
	 * {@link de.ptb.epics.eve.data.scandescription.errors.ScanModuleErrorTypes}.
	 * 
	 * @return the error type
	 */
	public ScanModuleErrorTypes getErrorType() {
		return this.errorType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + 
				((scanModule == null) ? 0 : scanModule.hashCode());
		result = prime * result + 
				((errorType == null) ? 0 : errorType.hashCode());
		return result;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		final ScanModuleError other = (ScanModuleError)obj;
		if(scanModule == null) {
			if(other.scanModule != null) {
				return false;
			}
		} else if(!scanModule.equals(other.scanModule)) {
			return false;
		}
		if(errorType == null) {
			if(other.errorType != null) {
				return false;
			}
		} else if(!errorType.equals(other.errorType)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ScanModuleError [scanModule=" + scanModule + 
				", errorType=" + errorType + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorMessage() {
		return "Error in scanModule " + this.scanModule + " because " + 
				errorType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorName() {
		return "ScanModule Error";
	}	
}