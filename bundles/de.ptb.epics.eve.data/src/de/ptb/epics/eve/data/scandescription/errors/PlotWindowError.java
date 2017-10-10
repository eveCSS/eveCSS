package de.ptb.epics.eve.data.scandescription.errors;

import de.ptb.epics.eve.data.scandescription.PlotWindow;

/**
 * This class describes a model error of a plotWindow.
 * 
 * @author Hartmut Scherr <hartmut.scherr (-at-) ptb.de>
 *
 */
public class PlotWindowError implements IModelError {
	private final PlotWindow plotWindow;
	private final PlotWindowErrorTypes errorType;
	
	/**
	 * This constructor creates a new error for a plotWindow.
	 * 
	 * @param plotWindowBahavior The plotWindow. Must not be 'null'
	 * @param errorType The error type. Must not be 'null'!
	 */
	public PlotWindowError(final PlotWindow plotWindow,
			final PlotWindowErrorTypes errorType) {
		if (plotWindow == null) {
			throw new IllegalArgumentException(
					"The parameter 'plotWindow' must not be null!");
		}
		if (errorType == null) {
			throw new IllegalArgumentException(
					"The parameter 'errorType' must not be null!");
		}
		this.plotWindow = plotWindow;
		this.errorType = errorType;
	}

	/**
	 * This method returns the plotWindow where the error occurred.
	 * 
	 * @return The plotWindow where the error occurred. Never returns 'null'.
	 */
	public PlotWindow getPlotWindow() {
		return this.plotWindow;
	}

	/**
	 * This method return the type of the error.
	 * 
	 * @return The type of the error. Never returns 'null'.
	 */
	public PlotWindowErrorTypes getErrorType() {
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
				+ ((plotWindow == null) ? 0 : plotWindow.hashCode());
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
		PlotWindowError other = (PlotWindowError) obj;
		if (errorType == null) {
			if (other.errorType != null) {
				return false;
			}
		} else if (!errorType.equals(other.errorType)) {
			return false;
		}
		if (plotWindow == null) {
			if (other.plotWindow != null) {
				return false;
			}
		} else if (!plotWindow.equals(other.plotWindow)) {
			return false;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "PlotWindowError [errorType=" + errorType + ", plotWindow=" + 
				plotWindow + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorMessage() {
		return "Error in plotWindow " + this.plotWindow + " because " + 
				this.errorType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorName() {
		return "PlotWindow Error";
	}
}
