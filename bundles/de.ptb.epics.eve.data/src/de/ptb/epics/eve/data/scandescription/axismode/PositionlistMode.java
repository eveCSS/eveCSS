package de.ptb.epics.eve.data.scandescription.axismode;

import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.AxisErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class PositionlistMode extends AxisMode {
	
	/** */
	public static final String POSITIONLIST_PROP = 
			"positionList";
	
	private String positionList;

	/**
	 * @param axis the axis this mode belongs to
	 */
	public PositionlistMode(Axis axis) {
		super(axis);
	}
	
	/**
	 * @return the positionList
	 */
	public String getPositionList() {
		return positionList;
	}

	/**
	 * @param positionList the positionList to set
	 */
	public void setPositionList(String positionList) {
		this.propertyChangeSupport.firePropertyChange(
				PositionlistMode.POSITIONLIST_PROP,
				this.positionList, this.positionList = positionList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getPositionCount() {
		if (positionList == null) {
			return null;
		}
		if (positionList.endsWith(",")) {
			return null;
		}
		return this.positionList.split(",").length;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		List<IModelError> errors = new ArrayList<IModelError>();
		if (this.positionList == null || this.positionList.equals("")) {
			errors.add(new AxisError(this.axis, 
					AxisErrorTypes.POSITIONLIST_NOT_SET));
		}
		return errors;
	}
}