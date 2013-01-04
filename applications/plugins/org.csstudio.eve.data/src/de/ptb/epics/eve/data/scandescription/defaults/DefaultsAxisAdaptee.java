package de.ptb.epics.eve.data.scandescription.defaults;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;

/**
 * @author Marcus Michalsky
 * @since 1.9
 */
public class DefaultsAxisAdaptee {
	private String id;
	private Stepfunctions stepfunction;
	private PositionMode positionmode;
	private Object mode;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	@XmlElement(name="axisid")
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the stepfunction
	 */
	public Stepfunctions getStepfunction() {
		return stepfunction;
	}

	/**
	 * @param stepfunction the stepfunction to set
	 */
	@XmlElement(name="stepfunction")
	public void setStepfunction(Stepfunctions stepfunction) {
		this.stepfunction = stepfunction;
	}

	/**
	 * @return the positionmode
	 */
	public PositionMode getPositionmode() {
		return positionmode;
	}

	/**
	 * @param positionmode the positionmode to set
	 */
	@XmlElement(name="positionmode")
	public void setPositionmode(PositionMode positionmode) {
		this.positionmode = positionmode;
	}

	/**
	 * @return the mode
	 */
	public Object getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	@XmlElements(value = {
			@XmlElement(name = "startstopstep", type = DefaultsAxisModeAdaptee.class),
			@XmlElement(name = "stepfilename", type = String.class),
			//@XmlElement(name = "plugin", type = String.class), // TODO
			@XmlElement(name = "positionlist", type = String.class) 
	})
	public void setMode(Object mode) {
		this.mode = mode;
	}
}