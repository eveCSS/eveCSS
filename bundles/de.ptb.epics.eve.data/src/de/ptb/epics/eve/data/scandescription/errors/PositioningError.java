package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.Positioning;

/**
 * This class represents an error that occurred in a positioning.
 * @author   Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 */
public class PositioningError implements IModelError {
	private final Positioning positioning;
	private final PositioningErrorTypes errorType;
	
	/**
	 * This constructor creates a new error of a positioning.
	 * 
	 * @param positioning The positioning where the error occurred. Must not be null!
	 * @param errorType The type of the error. Must not be null!
	 */
	public PositioningError(final Positioning positioning,
			final PositioningErrorTypes errorType) {
		if (positioning == null) {
			throw new IllegalArgumentException(
					"The parameter 'positioning' must not be null!");
		}
		if (errorType == null) {
			throw new IllegalArgumentException(
					"The parameter 'errorType' must not be null!");
		}
		this.positioning = positioning;
		this.errorType = errorType;
	}

	/**
	 * This method returns the positioning where the error occurred.
	 * 
	 * @return The positioning where the error occurred. Never returns null.
	 */
	public Positioning getPositioning() {
		return this.positioning;
	}

	/**
	 * This method returns the type of the error.
	 * 
	 * @return The type of the error. Never returns null.
	 */
	public PositioningErrorTypes getErrorType() {
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
		result = prime * result
				+ ((positioning == null) ? 0 : positioning.hashCode());
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
		PositioningError other = (PositioningError) obj;
		if (errorType == null) {
			if (other.errorType != null) {
				return false;
			}
		} else if (!errorType.equals(other.errorType)) {
			return false;
		}
		if (positioning == null) {
			if (other.positioning != null) {
				return false;
			}
		} else if (!positioning.equals(other.positioning)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "PositioningError [errorType=" + errorType + ", positioning="
				+ positioning + "]";
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorMessage() {
		return "Error in positioning " + this.positioning + " because " + 
				this.errorType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorName() {
		return "Positioning Error";
	}
}