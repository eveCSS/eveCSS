package de.ptb.epics.eve.data.scandescription.defaults;

/**
 * @author Marcus Michalsky
 * @param <T>  one of {Integer, Double, DateTime, Duration}
 * @since 1.8
 */
public class DefaultsAxisAddMultiply<T> extends DefaultAxis {
	private T start;
	private T stop;
	private T stepwidth;
	private boolean mainAxis;
	
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
		this.start = start;
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
		this.stop = stop;
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
		this.stepwidth = stepwidth;
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
		this.mainAxis = mainAxis;
	}
}