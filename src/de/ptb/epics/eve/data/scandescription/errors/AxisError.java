package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.Axis;

/**
 * This class represents an error of a axis of a scan module.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class AxisError implements IModelError {

	/**
	 * The axis where the error occurred.
	 */
	private final Axis axis;
	
	/**
	 * The type of the error
	 */
	private final AxisErrorTypes errorType;
	
	/**
	 * This constructor creates a new axis error.
	 * 
	 * @param axis The axis where the error occurred. Must not be null!
	 * @param errorType The type of the error. Must not be null!
	 */
	public AxisError( final Axis axis, final AxisErrorTypes errorType ) {
		if( axis == null ) {
			throw new IllegalArgumentException( "The parameter 'axis' must not be null!" );
		}
		if( errorType == null ) {
			throw new IllegalArgumentException( "The parameter 'errorType' must not be null!" );
		}
		this.axis = axis;
		this.errorType = errorType;
	}
	
	/**
	 * This method returns the axis where the error occurred
	 * 
	 * @return The axis where the error occurred. Never returns null!
	 */
	public Axis getAxis() {
		return this.axis;
	}
	
	/**
	 * This method return the type of the error.
	 * 
	 * @return The type of the error. Never returns null!
	 */
	public AxisErrorTypes getErrorType() {
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
		result = prime * result + ( (axis == null) ? 0 : axis.hashCode() );
		result = prime * result + ( (errorType == null) ? 0 : errorType.hashCode() );
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
		if( obj == null ) {
			return false;
		}
		if( getClass() != obj.getClass() ) {
			return false;
		}
		final AxisError other = (AxisError)obj;
		if( axis == null ) {
			if( other.axis != null ) {
				return false;
			}
		} else if( !axis.equals( other.axis ) ) {
			return false;
		}
		if( errorType == null ) {
			if (other.errorType != null)  {
				return false;
			}
		} else if( !errorType.equals( other.errorType ) ) {
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
		return "AxisError [axis=" + axis + ", errorType=" + errorType + "]";
	}

	@Override
	public String getErrorMessage() {
		return "Error in axis " + axis + " because " + errorType;
	}

	@Override
	public String getErrorName() {
		return "Axis Error";
	}
	
	
}
