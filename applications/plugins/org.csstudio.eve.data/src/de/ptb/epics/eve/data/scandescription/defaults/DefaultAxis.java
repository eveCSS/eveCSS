package de.ptb.epics.eve.data.scandescription.defaults;

import javax.xml.bind.annotation.XmlElement;

import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultAxis {
	private String id;
	private Stepfunctions stepfunction;
	private PositionMode positionmode;
	
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
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[Axis:" + this.id + "]";
	}
}