package de.ptb.epics.eve.data.scandescription.defaults;

import java.io.File;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.Duration;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;

/**
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
@XmlJavaTypeAdapter(DefaultsAxisAdapter.class)
public class DefaultsAxis implements Comparable<DefaultsAxis> {
	private String id;
	private Stepfunctions stepfunction;
	private PositionMode positionmode;
	
	private DefaultsAxisMode mode;
	private DataTypes type;
	
	private boolean mainAxis;
	
	/**
	 * 
	 */
	public DefaultsAxis() {
		this.id = null;
		this.stepfunction = null;
		this.positionmode = null;
		this.mode = null;
		this.type = null;
		this.mainAxis = false;
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
	 * @return the type
	 */
	public DataTypes getType() {
		return type;
	}

	/**
	 * @param type the data type
	 */
	public void setStartStopStepType(DataTypes type) {
		this.type = type;
		switch(type) {
		case DATETIME:
			if (this.getPositionmode().equals(PositionMode.ABSOLUTE)) {
				this.mode = new DefaultsAxisAddMultiply<Date>();
			} else {
				this.mode = new DefaultsAxisAddMultiply<Duration>();
			}
			break;
		case DOUBLE:
			this.mode = new DefaultsAxisAddMultiply<Double>();
			break;
		case INT:
			this.mode = new DefaultsAxisAddMultiply<Integer>();
			break;
		default:
			throw new IllegalArgumentException("wrong type");
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
	 * @return 
	 */
	public Object getStart() {
		return ((DefaultsAxisAddMultiply<?>)this.mode).getStart();
	}
	
	/**
	 * 
	 * @param start
	 */
	@SuppressWarnings("unchecked")
	public void setStart(int start) {
		if (!this.type.equals(DataTypes.INT)) {
			return;
		}
		if (this.stepfunction.equals(Stepfunctions.ADD) ||
				this.stepfunction.equals(Stepfunctions.MULTIPLY)) {
			((DefaultsAxisAddMultiply<Integer>)this.mode).setStart(start);
		}
	}
	
	/**
	 * 
	 * @param start
	 */
	@SuppressWarnings("unchecked")
	public void setStart(double start) {
		if (!this.type.equals(DataTypes.DOUBLE)) {
			return;
		}
		if (this.stepfunction.equals(Stepfunctions.ADD) ||
				this.stepfunction.equals(Stepfunctions.MULTIPLY)) {
			((DefaultsAxisAddMultiply<Double>)this.mode).setStart(start);
		}
	}
	
	/**
	 * 
	 * @param start
	 */
	@SuppressWarnings("unchecked")
	public void setStart(Date start) {
		if (!this.type.equals(DataTypes.DATETIME)) {
			return;
		}
		if (this.stepfunction.equals(Stepfunctions.ADD) ||
				this.stepfunction.equals(Stepfunctions.MULTIPLY)) {
			((DefaultsAxisAddMultiply<Date>)this.mode).setStart(start);
		}
	}
	
	/**
	 * 
	 * @param start
	 */
	@SuppressWarnings("unchecked")
	public void setStart(Duration start) {
		if (!this.type.equals(DataTypes.DATETIME)) {
			return;
		}
		if (this.stepfunction.equals(Stepfunctions.ADD) ||
				this.stepfunction.equals(Stepfunctions.MULTIPLY)) {
			((DefaultsAxisAddMultiply<Duration>)this.mode).setStart(start);
		}
	}
	
	/**
	 * @return
	 */
	public Object getStop() {
		return ((DefaultsAxisAddMultiply<?>)this.mode).getStop();
	}
	
	/**
	 * 
	 * @param stop
	 */
	@SuppressWarnings("unchecked")
	public void setStop(int stop) {
		if (!this.type.equals(DataTypes.INT)) {
			return;
		}
		if (this.stepfunction.equals(Stepfunctions.ADD) ||
				this.stepfunction.equals(Stepfunctions.MULTIPLY)) {
			((DefaultsAxisAddMultiply<Integer>)this.mode).setStop(stop);
		}
	}
	
	/**
	 * 
	 * @param stop
	 */
	@SuppressWarnings("unchecked")
	public void setStop(double stop) {
		if (!this.type.equals(DataTypes.DOUBLE)) {
			return;
		}
		if (this.stepfunction.equals(Stepfunctions.ADD) ||
				this.stepfunction.equals(Stepfunctions.MULTIPLY)) {
			((DefaultsAxisAddMultiply<Double>)this.mode).setStop(stop);
		}
	}
	
	/**
	 * 
	 * @param stop
	 */
	@SuppressWarnings("unchecked")
	public void setStop(Date stop) {
		if (!this.type.equals(DataTypes.DATETIME)) {
			return;
		}
		if (this.stepfunction.equals(Stepfunctions.ADD) ||
				this.stepfunction.equals(Stepfunctions.MULTIPLY)) {
			((DefaultsAxisAddMultiply<Date>)this.mode).setStop(stop);
		}
	}
	
	/**
	 * 
	 * @param stop
	 */
	@SuppressWarnings("unchecked")
	public void setStop(Duration stop) {
		if (!this.type.equals(DataTypes.DATETIME)) {
			return;
		}
		if (this.stepfunction.equals(Stepfunctions.ADD) ||
				this.stepfunction.equals(Stepfunctions.MULTIPLY)) {
			((DefaultsAxisAddMultiply<Duration>)this.mode).setStop(stop);
		}
	}
	
	/**
	 * @return
	 */
	public Object getStepwidth() {
		return ((DefaultsAxisAddMultiply<?>)this.mode).getStepwidth();
	}
	
	/**
	 * 
	 * @param stepwidth
	 */
	@SuppressWarnings("unchecked")
	public void setStepwidth(int stepwidth) {
		if (!this.type.equals(DataTypes.INT)) {
			return;
		}
		if (this.stepfunction.equals(Stepfunctions.ADD) ||
				this.stepfunction.equals(Stepfunctions.MULTIPLY)) {
			((DefaultsAxisAddMultiply<Integer>)this.mode).setStepwidth(stepwidth);
		}
	}
	
	/**
	 * 
	 * @param stepwidth
	 */
	@SuppressWarnings("unchecked")
	public void setStepwidth(double stepwidth) {
		if (!this.type.equals(DataTypes.DOUBLE)) {
			return;
		}
		if (this.stepfunction.equals(Stepfunctions.ADD) ||
				this.stepfunction.equals(Stepfunctions.MULTIPLY)) {
			((DefaultsAxisAddMultiply<Double>)this.mode).setStepwidth(stepwidth);
		}
	}
	
	/**
	 * 
	 * @param stepwidth
	 */
	@SuppressWarnings("unchecked")
	public void setStepwidth(Date stepwidth) {
		if (!this.type.equals(DataTypes.DATETIME)) {
			return;
		}
		if (this.stepfunction.equals(Stepfunctions.ADD) ||
				this.stepfunction.equals(Stepfunctions.MULTIPLY)) {
			((DefaultsAxisAddMultiply<Date>)this.mode).setStepwidth(stepwidth);
		}
	}
	
	/**
	 * @param stepwidth the stepwidth to set
	 */
	@SuppressWarnings("unchecked")
	public void setStepwidth(Duration stepwidth) {
		if (!this.type.equals(DataTypes.DATETIME)) {
			return;
		}
		if (this.stepfunction.equals(Stepfunctions.ADD) ||
				this.stepfunction.equals(Stepfunctions.MULTIPLY)) {
			((DefaultsAxisAddMultiply<Duration>)this.mode).setStepwidth(stepwidth);
		}
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "[Axis:" + this.id + "]";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(DefaultsAxis other) {
		return this.getId().compareTo(other.getId());
	}
}