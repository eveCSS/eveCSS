package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.addmultiply.intdouble;

import de.ptb.epics.eve.data.scandescription.axismode.AdjustParameter;

/**
 * Immutable Data container to hold parameters of add/multiply axes of type int 
 * or double.
 * 
 * @author Marcus Michalsky
 * @since 1.34
 */
public final class AddIntDoubleValues<T> {
	private final T start;
	private final T stop;
	private final T stepwidth;
	private final Double stepcount;
	private final AdjustParameter adjustParameter;
	
	public AddIntDoubleValues(T start, T stop, T stepwidth, Double stepcount, 
			AdjustParameter adjustParameter) {
		this.start = start;
		this.stop = stop;
		this.stepwidth = stepwidth;
		this.stepcount = stepcount;
		this.adjustParameter = adjustParameter;
	}

	/**
	 * @return the start
	 */
	public T getStart() {
		return start;
	}

	/**
	 * @return the stop
	 */
	public T getStop() {
		return stop;
	}

	/**
	 * @return the stepwidth
	 */
	public T getStepwidth() {
		return stepwidth;
	}

	/**
	 * @return the stepcount
	 */
	public Double getStepcount() {
		return stepcount;
	}

	/**
	 * @return the adjustParameter
	 */
	public AdjustParameter getAdjustParameter() {
		return adjustParameter;
	}
}
