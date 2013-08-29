package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.AbstractPostscanBehavior;

/**
 * This class describes a model error of a postscan.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class PostscanError implements IModelError {
	private final AbstractPostscanBehavior postscanBahavior;
	private final PostscanErrorTypes errorType;
	
	/**
	 * This constructor creates a new error for a postscan.
	 * 
	 * @param postscanBahavior The postscan. Must not be 'null'
	 * @param errorType The error type. Must not be 'null'!
	 */
	public PostscanError(final AbstractPostscanBehavior postscanBahavior,
			final PostscanErrorTypes errorType) {
		if (postscanBahavior == null) {
			throw new IllegalArgumentException(
					"The parameter 'postscanBahavior' must not be null!");
		}
		if (errorType == null) {
			throw new IllegalArgumentException(
					"The parameter 'errorType' must not be null!");
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

	/**
	 * {@inheritDoc}
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

	/**
	 * {@inheritDoc}
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
		PostscanError other = (PostscanError) obj;
		if (errorType == null) {
			if (other.errorType != null) {
				return false;
			}
		} else if (!errorType.equals(other.errorType)) {
			return false;
		}
		if (postscanBahavior == null) {
			if (other.postscanBahavior != null) {
				return false;
			}
		} else if (!postscanBahavior.equals(other.postscanBahavior)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "PostscanError [errorType=" + errorType + ", postscanBahavior=" + 
				postscanBahavior + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorMessage() {
		return "Error in postscan " + this.postscanBahavior + " because " + 
				this.errorType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorName() {
		return "Postscan Error";
	}
}