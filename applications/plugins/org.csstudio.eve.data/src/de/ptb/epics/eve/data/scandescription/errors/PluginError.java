package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.PluginController;

/**
 * This class describes an error of a plug in.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 *
 */
public class PluginError implements IModelError {

	/**
	 * The plug in controller where the error occurred.
	 */
	private final PluginController pluginController;
	
	/**
	 * The type of the error.
	 */
	private final PluginErrorTypes pluginErrorType;
	
	/**
	 * The name of the parameter. 
	 */
	private final String parameterName;
	
	/**
	 * This constructor creates a new plug in error.
	 * 
	 * @param pluginController The plug in controller where the error occurred. Must not be null!
	 * @param pluginErrorType The error type. Must not be null!
	 * @param parameterName The name of the parameter where the error occured. Must not be null!
	 */
	public PluginError( final PluginController pluginController, final PluginErrorTypes pluginErrorType, final String parameterName ) {
		if( pluginController == null ) {
			throw new IllegalArgumentException( "The parameter 'pluginController' must not be null!" );
		}
		if( pluginErrorType == null ) {
			throw new IllegalArgumentException( "The parameter 'pluginErrorType' must not be null!" );
		}
		if( parameterName == null ) {
			throw new IllegalArgumentException( "The parameter 'parameterName' must not be null!" );
		}
		this.pluginController = pluginController;
		this.pluginErrorType = pluginErrorType;
		this.parameterName = parameterName;
	}

	/**
	 * This method returns the plug in controller where error occurred.
	 * 
	 * @return The plug in controller where the error occurred. Never returns null.
	 */
	public PluginController getPluginController() {
		return pluginController;
	}

	/**
	 * This method returns the type of the error.
	 * 
	 * @return The type of the error. Never returns null.
	 */
	public PluginErrorTypes getPluginErrorType() {
		return pluginErrorType;
	}

	/**
	 * This method return the name of the parameter where the error occurred.
	 * 
	 * @return The name of the parameter where the error occurred. Never returns null.
	 */
	public String getParameterName() {
		return parameterName;
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
				+ ((parameterName == null) ? 0 : parameterName.hashCode());
		result = prime
				* result
				+ ((pluginController == null) ? 0 : pluginController.hashCode());
		result = prime * result
				+ ((pluginErrorType == null) ? 0 : pluginErrorType.hashCode());
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
		final PluginError other = (PluginError)obj;
		if( parameterName == null ) {
			if( other.parameterName != null ) {
				return false;
			}
		} else if( !parameterName.equals( other.parameterName ) ) {
			return false;
		}
		if( pluginController == null ) {
			if( other.pluginController != null ) {
				return false;
			}
		} else if( !pluginController.equals( other.pluginController ) ) {
			return false;
		}
		if( pluginErrorType == null ) {
			if( other.pluginErrorType != null ) {
				return false;
			}
		} else if( !pluginErrorType.equals( other.pluginErrorType ) ) {
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
		return "PluginError [parameterName=" + parameterName
				+ ", pluginController=" + pluginController
				+ ", pluginErrorType=" + pluginErrorType + "]";
	}
	
	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelError#getErrorMessage()
	 */
	@Override
	public String getErrorMessage() {
		return "Error in plugin " + this.pluginController + " because " + this.pluginErrorType + " in parameter " + this.parameterName;
	}

	/*
	 * (non-Javadoc)
	 * @see de.ptb.epics.eve.data.scandescription.errors.IModelError#getErrorName()
	 */
	@Override
	public String getErrorName() {
		return "Plugin Error";
	}
	
}
