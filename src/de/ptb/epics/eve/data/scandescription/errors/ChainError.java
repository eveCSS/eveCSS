package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.Chain;

/**
 * This class represents an error of a chain object.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class ChainError implements IModelError {

	/**
	 * The chain object where the error occurred.
	 */
	final Chain chain;
	
	/**
	 * The type of error.
	 */
	final ChainErrorTypes errorType;
	
	/**
	 * This method creates a new description object for an error in a chain object.
	 * 
	 * @param chain The chain object where the error occurred. Must not be null!
	 * @param errorType The type of the error. Must not be null!
	 */
	public ChainError( final Chain chain, final ChainErrorTypes errorType ) {
		if( chain == null ) {
			throw new IllegalArgumentException( "The parameter 'chain' must not be null!" );
		}
		if( errorType == null ) {
			throw new IllegalArgumentException( "The parameter 'errorType' must not be null!" );
		}
		this.chain = chain;
		this.errorType = errorType;
	}
	
	/**
	 * This method returns the chain of this chain error. 
	 * 
	 * @return The chain where the error is located. Never returns null!
	 */
	public Chain getChain() {
		return this.chain;
	}
	
	/**
	 * The type of the error.
	 * 
	 * @return The type of the error. Never returns null!
	 */
	public ChainErrorTypes getErrorType() {
		return this.errorType;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result +  this.chain.hashCode();
		result = prime * result +  this.errorType.hashCode();
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals( final Object obj ) {
		if( this == obj ) {
			return true;
		}
		if( obj == null) {
			return false;
		}
		if( getClass() != obj.getClass() ) {
			return false;
		}
		final ChainError other = (ChainError)obj;
		if(! this.chain.equals( other.chain ) ) {
			return false;
		}
		if( !this.errorType.equals( other.errorType ) ) {
			return false;
		}
		return true;
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		//return "ChainError [chain=" + this.chain + ", errorType=" + this.errorType + "]";
		return "ChainError [chain=" + this.chain.getId() + ", errorType=" + this.errorType + "]";
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelError#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return "Error in chain " + this.chain.getId() + " because " + errorType;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelError#getErrorName()
	 */
	@Override
	public String getErrorName() {
		return "Chain Error";
	}
}
