package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.AbstractPostscanBehavior;

/**
 * This class describes a model error of a postscan.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class PostscanError implements IModelError {

	/**
	 * The postscan where the error occurred.
	 */
	private final AbstractPostscanBehavior postscanBahavior;
	
	/**
	 * The type of the error.
	 */
	private final PostscanErrorTypes errorType;
	
	/**
	 * This constructor creates a new error for a postscan.
	 * 
	 * @param postscanBahavior The postscan. Must not be 'null'
	 * @param errorType The error type. Must not be 'null'!
	 */
	public PostscanError( final AbstractPostscanBehavior postscanBahavior, final PostscanErrorTypes errorType ) {
		if( postscanBahavior == null ) {
			throw new IllegalArgumentException( "The parameter 'postscanBahavior' must not be null!" );
		}
		if( errorType == null ) {
			throw new IllegalArgumentException( "The parameter 'errorType' must not be null!" );
		}
		this.postscanBahavior = postscanBahavior;
		this.errorType = errorType;
	}

	/**
	 * This method returns the postscan where the error occurred.
	 * 
	 * @return The postscan where the error occurred. Never returns 'null'.
	 */
	public AbstractPostscanBehavior getPostscanBahavior() {
		return this.postscanBahavior;
	}

	/**
	 * This method return the type of the error.
	 * 
	 * @return The type of the error. Never returns 'null'.
	 */
	public PostscanErrorTypes getErrorType() {
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
				+ ((postscanBahavior == null) ? 0 : postscanBahavior
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
		PostscanError other = (PostscanError) obj;
		if (errorType == null) {
			if (other.errorType != null)
				return false;
		} else if (!errorType.equals(other.errorType))
			return false;
		if (postscanBahavior == null) {
			if (other.postscanBahavior != null)
				return false;
		} else if (!postscanBahavior.equals(other.postscanBahavior))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PostscanError [errorType=" + errorType + ", postscanBahavior=" + postscanBahavior + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelError#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return "Error in postscan " + this.postscanBahavior + " because " + this.errorType;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelError#getErrorName()
	 */
	@Override
	public String getErrorName() {
		return "Postscan Error";
	}
}
