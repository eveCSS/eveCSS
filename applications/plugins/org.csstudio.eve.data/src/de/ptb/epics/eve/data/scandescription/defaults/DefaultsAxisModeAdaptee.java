package de.ptb.epics.eve.data.scandescription.defaults;

import javax.xml.bind.annotation.XmlElement;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultsAxisModeAdaptee {
	private Object start;
	private Object stop;
	private Object stepwidth;

	/**
	 * @return the start
	 */
	public Object getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	@XmlElement(name = "start")
	public void setStart(Object start) {
		this.start = start;
	}

	/**
	 * @return the stop
	 */
	public Object getStop() {
		return stop;
	}

	/**
	 * @param stop the stop to set
	 */
	@XmlElement(name = "stop")
	public void setStop(Object stop) {
		this.stop = stop;
	}

	/**
	 * @return the stepwidth
	 */
	public Object getStepwidth() {
		return stepwidth;
	}

	/**
	 * @param stepwidth the stepwidth to set
	 */
	@XmlElement(name = "stepwidth")
	public void setStepwidth(Object stepwidth) {
		this.stepwidth = stepwidth;
	}
}