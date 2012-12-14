package de.ptb.epics.eve.data.scandescription.defaults;

import java.io.File;

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
	
	private DefaultAxisMode mode;
	
	/**
	 * 
	 */
	public DefaultAxis() {
		this.id = null;
		this.stepfunction = null;
		this.positionmode = null;
		this.mode = null;
	}
	
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
		switch(this.stepfunction) {
		case ADD:
		case MULTIPLY:
			// TODO
			break;
		case FILE:
			this.mode = new DefaultAxisFile();
			break;
		case PLUGIN:
			// TODO
			break;
		case POSITIONLIST:
			this.mode = new DefaultAxisList();
			break;
		default: 
			this.mode = null;
			break;
		}
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
	 * @return the file
	 */
	public String getFile() {
		if (!(this.mode instanceof DefaultAxisFile)) {
			throw new IllegalStateException("Axis step function is not "
					+ Stepfunctions.FILE);
		}
		return ((DefaultAxisFile)this.mode).getFile().getPath();
	}
	
	/**
	 * @param file the file to set
	 */
	@XmlElement(name="stepfilename")
	public void setFile(String file) {
		if (!(this.mode instanceof DefaultAxisFile)) {
			throw new IllegalStateException("Axis step function is not "
					+ Stepfunctions.FILE);
		}
		((DefaultAxisFile)this.mode).setFile(new File(file));
	}
	
	/**
	 * @return the list
	 */
	public String getPositionList() {
		if (!(this.mode instanceof DefaultAxisList)) {
			throw new IllegalStateException("Axis step function is not "
					+ Stepfunctions.POSITIONLIST);
		}
		return ((DefaultAxisList)this.mode).getPositionList();
	}
	
	/**
	 * @param list the list to set
	 */
	@XmlElement(name="positionlist")
	public void setPositionList(String list) {
		if (!(this.mode instanceof DefaultAxisList)) {
			throw new IllegalStateException("Axis step function is not "
					+ Stepfunctions.POSITIONLIST);
		}
		((DefaultAxisList)this.mode).setPositionList(list);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[Axis:" + this.id + "]";
	}
}