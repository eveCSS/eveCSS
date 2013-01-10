package de.ptb.epics.eve.data.scandescription.defaults;

import java.io.File;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
@XmlJavaTypeAdapter(DefaultsAxisAdapter.class)
public class DefaultsAxis {
	private String id;
	private Stepfunctions stepfunction;
	private PositionMode positionmode;
	
	private DefaultsAxisMode mode;
	
	/**
	 * 
	 */
	public DefaultsAxis() {
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
	public void setStepfunction(Stepfunctions stepfunction) {
		this.stepfunction = stepfunction;
		switch(this.stepfunction) {
		case ADD:
		case MULTIPLY:
			// TODO
			break;
		case FILE:
			this.mode = new DefaultsAxisFile();
			break;
		case PLUGIN:
			this.mode = new DefaultsAxisPlugin();
			// TODO
			break;
		case POSITIONLIST:
			this.mode = new DefaultsAxisList();
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
	public void setPositionmode(PositionMode positionmode) {
		this.positionmode = positionmode;
	}
	
	/**
	 * @return the file
	 */
	public String getFile() {
		if (!(this.mode instanceof DefaultsAxisFile)) {
			throw new IllegalStateException("Axis step function is not "
					+ Stepfunctions.FILE);
		}
		return ((DefaultsAxisFile)this.mode).getFile().getPath();
	}
	
	/**
	 * @param file the file to set
	 */
	public void setFile(String file) {
		if (!(this.mode instanceof DefaultsAxisFile)) {
			throw new IllegalStateException("Axis step function is not "
					+ Stepfunctions.FILE);
		}
		((DefaultsAxisFile)this.mode).setFile(new File(file));
	}
	
	/**
	 * @return the list
	 */
	public String getPositionList() {
		if (!(this.mode instanceof DefaultsAxisList)) {
			throw new IllegalStateException("Axis step function is not "
					+ Stepfunctions.POSITIONLIST);
		}
		return ((DefaultsAxisList)this.mode).getPositionList();
	}
	
	/**
	 * @param list the list to set
	 */
	public void setPositionList(String list) {
		if (!(this.mode instanceof DefaultsAxisList)) {
			throw new IllegalStateException("Axis step function is not "
					+ Stepfunctions.POSITIONLIST);
		}
		((DefaultsAxisList)this.mode).setPositionList(list);
	}
	
	/**
	 * @return the plugin
	 */
	public DefaultsAxisPlugin getPlugin() {
		if (!(this.mode instanceof DefaultsAxisPlugin)) {
			throw new IllegalStateException("Axis step function is not "
					+ Stepfunctions.PLUGIN);
		}
		return ((DefaultsAxisPlugin)this.mode);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[Axis:" + this.id + "]";
	}
}