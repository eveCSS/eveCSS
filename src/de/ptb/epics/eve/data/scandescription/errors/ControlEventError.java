package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.ControlEvent;

/**
 * This class represents an error of a control event.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class ControlEventError implements IModelError {

	/**
	 * The control event where the error occurred.
	 */
	private final ControlEvent controlEvent;
	
	/**
	 * The type of the error. 
	 */
	private final ControlEventErrorTypes errorType;
	
	/**
	 * This constructor create a new error of a control event.
	 * 
	 * @param controlEvent The control event where the error occurred. Must not be null!
	 * @param errorType  The type of the error that occurred. Must not be null!
	 */
	public ControlEventError( final ControlEvent controlEvent, final ControlEventErrorTypes errorType ) {
		if( controlEvent == null ) {
			throw new IllegalArgumentException( "The parameter 'controlEvent' must not be null!" );
		}
		if( errorType == null ) {
			throw new IllegalArgumentException( "The parameter 'errorType' must not be null!" );
		}
		this.controlEvent = controlEvent;
		this.errorType = errorType;
	}

	/**
	 * This method returns the control event where the error occurred. 
	 * 
	 * @return The control event where the error occurred. Never returns null!
	 */
	public ControlEvent getControlEvent() {
		return this.controlEvent;
	}

	/**
	 * This method returns the type of the error.
	 * 
	 * @return The type of the error. Must not be null!
	 */
	public ControlEventErrorTypes getErrorType() {
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
		result = prime * result + ( ( controlEvent == null ) ? 0 : controlEvent.hashCode() );
		result = prime * result + ( ( errorType == null ) ? 0 : errorType.hashCode() );
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
		final ControlEventError other = (ControlEventError)obj;
		if( controlEvent == null ) {
			if( other.controlEvent != null ) {
				return false;
			}
		} else if( !controlEvent.equals(other.controlEvent ) ) {
			return false;
		}
		if( errorType == null ) {
			if( other.errorType != null ) {
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
		return "ControlEventError [controlEvent=" + controlEvent + ", errorType=" + errorType + "]";
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelError#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return "Error in control event " + controlEvent + " because " + errorType;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelError#getErrorName()
	 */
	@Override
	public String getErrorName() {
		return "Control Event Error";
	}
}
