package de.ptb.epics.eve.data.scandescription.axismode;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.AxisErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * @author Marcus Michalsky
 * @param <T> data type of the axis this mode belongs to
 * @since 1.7
 */
public abstract class AddMultiplyMode<T extends Object> extends AxisMode {
	
	private static final Logger LOGGER = Logger.getLogger(AddMultiplyMode.class
			.getName());
	
	/** */
	public static final String ADJUST_PARAMETER_PROP = "adjustParameter";
	/** */
	public static final String START_PROP = "start";
	/** */
	public static final String STOP_PROP = "stop";
	/** */
	public static final String STEPWIDTH_PROP = "stepwidth";
	/** */
	public static final String STEPCOUNT_PROP = "stepcount";
	/** */
	public static final String MAIN_AXIS_PROP = "mainAxis";
	
	protected T start;
	protected T stop;
	protected T stepwidth;
	protected Double stepcount;
	
	private boolean autoAdjust;
	private AdjustParameter adjustParameter;
	
	protected boolean mainAxis;
	protected Axis referenceAxis;
	
	protected AddMultiplyMode(Axis axis) {
		super(axis);
		this.autoAdjust = true;
		this.adjustParameter = AdjustParameter.STEPCOUNT;
		this.mainAxis = false;
		this.referenceAxis = null;
		this.stepcount = new Double(Double.NaN);
		if (axis.getScanModule() != null && 
				axis.getScanModule().getMainAxis() != null) {
			this.referenceAxis = axis.getScanModule().getMainAxis();
			this.stepcount = this.referenceAxis.getStepcount();
		}
	}
	
	/**
	 * Adjusts this axis to match its step count with the given one's
	 * 
	 * @param mainAxis the axis set as main axis
	 */
	public void matchMainAxis(Axis mainAxis) {
		if (LOGGER.isDebugEnabled()) {
			if (mainAxis == null) {
				LOGGER.debug("Notified '" + this.axis.getMotorAxis().getName() + 
						"' that main axis has been reset.");
			} else {
				LOGGER.debug("matching " + this.axis.getMotorAxis().getName()
						+ " to " + mainAxis.getMotorAxis().getName());
			}
		}
		if (this.isMainAxis()) {
			// this axis is the main axis
			return;
		}
		this.referenceAxis = mainAxis;
		if (mainAxis == null) {
			this.adjustParameter = AdjustParameter.STEPCOUNT;
			// main axis has been reset
			return;
		}
		this.adjustParameter = AdjustParameter.STEPWIDTH;
		this.stepcount = ((AddMultiplyMode<?>)mainAxis.getMode()).getStepcount();
		this.adjust();
	}

	/*
	 * adjusts the value set in adjustParameter to satisfy the other three
	 */
	protected abstract void adjust();
	
	/**
	 * @return the start
	 */
	public T getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(T start) {
		LOGGER.debug("set start: " + start.toString());
		this.propertyChangeSupport.firePropertyChange(
				AddMultiplyMode.START_PROP, this.start, this.start = start);
		if (this.isAutoAdjust()) {
			this.adjust();
		}
	}

	/**
	 * @return the stop
	 */
	public T getStop() {
		return stop;
	}

	/**
	 * @param stop the stop to set
	 */
	public void setStop(T stop) {
		LOGGER.debug("set stop: " + stop.toString());
		this.propertyChangeSupport.firePropertyChange(
				AddMultiplyMode.STOP_PROP, this.stop, this.stop = stop);
		if (this.isAutoAdjust()) {
			this.adjust();
		}
	}

	/**
	 * @return the stepwidth
	 */
	public T getStepwidth() {
		return stepwidth;
	}

	/**
	 * @param stepwidth the stepwidth to set
	 */
	public void setStepwidth(T stepwidth) {
		LOGGER.debug("set stepwidth: " + stepwidth.toString());
		this.propertyChangeSupport.firePropertyChange(
				AddMultiplyMode.STEPWIDTH_PROP, this.stepwidth,
				this.stepwidth = stepwidth);
		if (this.isAutoAdjust()) {
			this.adjust();
		}
	}

	/**
	 * @return the step count
	 */
	public Double getStepcount() {
		return stepcount;
	}

	/**
	 * @param stepcount the stepcount to set
	 */
	public void setStepcount(Double stepcount) {
		LOGGER.debug("set stepcount: " + stepcount.toString());
		this.propertyChangeSupport.firePropertyChange(
				AddMultiplyMode.STEPCOUNT_PROP, this.stepcount,
				this.stepcount = stepcount);
		if (this.isAutoAdjust()) {
			this.adjust();
		}
	}

	/**
	 * @return the autoAdjust
	 */
	public boolean isAutoAdjust() {
		return autoAdjust;
	}

	/**
	 * @param autoAdjust the autoAdjust to set
	 */
	public void setAutoAdjust(boolean autoAdjust) {
		this.autoAdjust = autoAdjust;
	}

	/**
	 * @return the adjustParameter
	 */
	public AdjustParameter getAdjustParameter() {
		return adjustParameter;
	}

	/**
	 * @param adjustParameter the adjustParameter to set
	 */
	public void setAdjustParameter(AdjustParameter adjustParameter) {
		this.propertyChangeSupport.firePropertyChange(
				AddMultiplyMode.ADJUST_PARAMETER_PROP, this.adjustParameter,
				this.adjustParameter = adjustParameter);
		LOGGER.debug("set adjust parameter " + adjustParameter.toString());
	}

	/**
	 * @return the mainAxis
	 */
	public boolean isMainAxis() {
		return mainAxis;
	}

	/**
	 * @param mainAxis the mainAxis to set
	 */
	public void setMainAxis(boolean mainAxis) {
		this.propertyChangeSupport.firePropertyChange(
				AddMultiplyMode.MAIN_AXIS_PROP, this.mainAxis,
				this.mainAxis = mainAxis);
		if (LOGGER.isDebugEnabled()) {
			if (mainAxis) {
				LOGGER.debug("Axis " + this.axis.getMotorAxis().getName()
						+ " has been set as main axis.");
			} else {
				LOGGER.debug("Main axis has been reset.");
			}
		}
	}
	
	/**
	 * @return the referenceAxis
	 */
	public Axis getReferenceAxis() {
		return referenceAxis;
	}

	/**
	 * @return the data type
	 */
	public DataTypes getType() {
		return this.axis.getType();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		// there should be always some default value, all succeeding values 
		// are only written to the model if they are valid, so there should 
		// be no errors at all for Axis in AddMultiplyMode
		List<IModelError> errors = new ArrayList<IModelError>();
		if (this.getStart() == null) {
			errors.add(new AxisError(this.axis, AxisErrorTypes.START_NOT_SET));
		}
		if (this.getStop() == null) {
			errors.add(new AxisError(this.axis, AxisErrorTypes.STOP_NOT_SET));
		}
		if (this.getStepwidth() == null) {
			errors.add(new AxisError(this.axis,
					AxisErrorTypes.STEPWIDTH_NOT_SET));
		}
		if (this.getStepcount() == null) {
			errors.add(new AxisError(this.axis,
					AxisErrorTypes.STEPCOUNT_NOT_SET));
		}
		return errors;
	}
}