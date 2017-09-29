package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * This class represents an error of a axis of a scan module.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 * @author Marcus Michalsky
 */
public class AxisError implements IModelError {

	// the axis where the error occurred
	private final Axis axis;
	
	// the error type
	private final AxisErrorTypes errorType;
	
	/**
	 * Constructs a new axis error.
	 * 
	 * @param axis the axis where the error occurred
	 * @param errorType The error type
	 * @throws IllegalArgumentException 
	 * 		   <ul>
	 * 			<li>if <code>axis</code> is <code>null</code></li>
	 * 			<li>if <code>errorType</code> is <code>null</code></li>
	 * 		   </ul>
	 */
	public AxisError(final Axis axis, final AxisErrorTypes errorType) {
		if(axis == null) {
			throw new IllegalArgumentException(
					"The parameter 'axis' must not be null!");
		}
		if(errorType == null) {
			throw new IllegalArgumentException(
					"The parameter 'errorType' must not be null!");
		}
		this.axis = axis;
		this.errorType = errorType;
	}
	
	/**
	 * Returns the axis where the error occurred.
	 * 
	 * @return the axis where the error occurred
	 */
	public Axis getAxis() {
		return this.axis;
	}
	
	/**
	 * Returns the type of the error as defined in 
	 * {@link de.ptb.epics.eve.data.scandescription.errors.AxisErrorTypes}.
	 * 
	 * @return The error type
	 */
	public AxisErrorTypes getErrorType() {
		return this.errorType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((axis == null) ? 0 : axis.hashCode());
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
		final AxisError other = (AxisError)obj;
		if(axis == null) {
			if(other.axis != null) {
				return false;
			}
		} else if(!axis.equals(other.axis)) {
			return false;
		}
		if(errorType == null) {
			if(other.errorType != null) {
				return false;
			}
		} else if(!errorType.equals( other.errorType)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "AxisError [axis=" + axis + ", errorType=" + errorType + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorMessage() {
		return "Error in axis " + axis + " because " + errorType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorName() {
		return "Axis Error";
	}	
}