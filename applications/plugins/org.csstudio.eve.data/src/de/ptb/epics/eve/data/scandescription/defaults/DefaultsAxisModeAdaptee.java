package de.ptb.epics.eve.data.scandescription.defaults;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
@XmlType(propOrder = {"start", "stop", "stepwidth", "mainAxis"})
public class DefaultsAxisModeAdaptee {
	private DefaultsAxisTypeValue start;
	private DefaultsAxisTypeValue stop;
	private DefaultsAxisTypeValue stepwidth;
	private Boolean mainAxis;
	
	/**
	 * @return the start
	 */
	public DefaultsAxisTypeValue getStart() {
		return start;
	}
	
	/**
	 * @param start the start to set
	 */
	@XmlElement(name="start")
	public void setStart(DefaultsAxisTypeValue start) {
		this.start = start;
	}
	
	/**
	 * @return the stop
	 */
	public DefaultsAxisTypeValue getStop() {
		return stop;
	}
	
	/**
	 * @param stop the stop to set
	 */
	@XmlElement(name="stop")
	public void setStop(DefaultsAxisTypeValue stop) {
		this.stop = stop;
	}
	
	/**
	 * @return the stepwidth
	 */
	public DefaultsAxisTypeValue getStepwidth() {
		return stepwidth;
	}
	
	/**
	 * @param stepwidth the stepwidth to set
	 */
	@XmlElement(name="stepwidth")
	public void setStepwidth(DefaultsAxisTypeValue stepwidth) {
		this.stepwidth = stepwidth;
	}
	
	/**
	 * @return the mainAxis
	 */
	public Boolean getMainAxis() {
		return mainAxis;
	}
	
	/**
	 * @param mainAxis the mainAxis to set
	 */
	@XmlElement(name="ismainaxis")
	public void setMainAxis(Boolean mainAxis) {
		this.mainAxis = mainAxis;
	}
}