package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.Chain;

/**
 * Represents an error which occurred in a 
 * {@link de.ptb.epics.eve.data.scandescription.Chain}.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class ChainError implements IModelError {

	// the chain where the error occurred
	private final Chain chain;
	
	// the error type
	private final ChainErrorTypes errorType;
	
	/**
	 * Constructs a <code>ChainError</code>.
	 * 
	 * @param chain the chain where the error occurred
	 * @param errorType the error type
	 * @throws IllegalArgumentException 
	 * 		   <ul>
	 * 			<li>if <code>chain</code> is <code>null</code></li>
	 * 			<li>if <code>errorType</code> is <code>null</code></li>
	 * 		   </ul>
	 */
	public ChainError(final Chain chain, final ChainErrorTypes errorType) {
		if(chain == null) {
			throw new IllegalArgumentException(
					"The parameter 'chain' must not be null!");
		}
		if(errorType == null) {
			throw new IllegalArgumentException(
					"The parameter 'errorType' must not be null!");
		}
		this.chain = chain;
		this.errorType = errorType;
	}
	
	/**
	 * Returns the chain where the error occurred.
	 * 
	 * @return the chain where the error occurred
	 */
	public Chain getChain() {
		return this.chain;
	}
	
	/**
	 * Returns the error type as defined in 
	 * {@link de.ptb.epics.eve.data.scandescription.errors.ChainErrorTypes}.
	 * 
	 * @return the type of the error
	 */
	public ChainErrorTypes getErrorType() {
		return this.errorType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result +  this.chain.hashCode();
		result = prime * result +  this.errorType.hashCode();
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
		final ChainError other = (ChainError)obj;
		if(!this.chain.equals(other.chain)) {
			return false;
		}
		if(!this.errorType.equals( other.errorType)) {
			return false;
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "ChainError [chain=" + this.chain.getId() + 
				", errorType=" + this.errorType + "]";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorMessage() {
		return "Error in chain " + this.chain.getId() + " because " + errorType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorName() {
		return "Chain Error";
	}
}