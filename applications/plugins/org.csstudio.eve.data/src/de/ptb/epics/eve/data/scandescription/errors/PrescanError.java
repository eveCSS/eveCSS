package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.AbstractPrescanBehavior;

/**
 * This class describes a model error of a prescan.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class PrescanError implements IModelError {

	/**
	 * The prescan where the error occurred.
	 */
	private final AbstractPrescanBehavior prescanBahavior;
	
	/**
	 * The type of the error.
	 */
	private final PrescanErrorTypes errorType;
	
	/**
	 * This constructor creates a new error for a prescan.
	 * 
	 * @param prescanBahavior The prescan. Must not be 'null'
	 * @param errorType The error type. Must not be 'null'!
	 */
	public PrescanError( final AbstractPrescanBehavior prescanBahavior, final PrescanErrorTypes errorType ) {
		if( prescanBahavior == null ) {
			throw new IllegalArgumentException( "The parameter 'prescanBahavior' must not be null!" );
		}
		if( errorType == null ) {
			throw new IllegalArgumentException( "The parameter 'errorType' must not be null!" );
		}
		this.prescanBahavior = prescanBahavior;
		this.errorType = errorType;
	}

	/**
	 * This method returns the prescan where the error occurred.
	 * 
	 * @return The prescan where the error occurred. Never returns 'null'.
	 */
	public AbstractPrescanBehavior getPrescanBahavior() {
		return this.prescanBahavior;
	}

	/**
	 * This method return the type of the error.
	 * 
	 * @return The type of the error. Never returns 'null'.
	 */
	public PrescanErrorTypes getErrorType() {
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
		result = prime * result
				+ ((errorType == null) ? 0 : errorType.hashCode());
		result = prime
				* result
				+ ((prescanBahavior == null) ? 0 : prescanBahavior
						.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrescanError other = (PrescanError) obj;
		if (errorType == null) {
			if (other.errorType != null)
				return false;
		} else if (!errorType.equals(other.errorType))
			return false;
		if (prescanBahavior == null) {
			if (other.prescanBahavior != null)
				return false;
		} else if (!prescanBahavior.equals(other.prescanBahavior))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PrescanError [errorType=" + errorType + ", prescanBahavior=" + prescanBahavior + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelError#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return "Error in prescan " + this.prescanBahavior + " because " + this.errorType;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelError#getErrorName()
	 */
	@Override
	public String getErrorName() {
		return "Prescan Error";
	}
}
