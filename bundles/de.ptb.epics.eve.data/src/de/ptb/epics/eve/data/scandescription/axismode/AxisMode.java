package de.ptb.epics.eve.data.scandescription.axismode;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public abstract class AxisMode implements IModelErrorProvider {
	protected PropertyChangeSupport propertyChangeSupport;
	protected Axis axis;
	
	protected AxisMode(Axis axis) {
		this.axis = axis;
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * Factory Method.
	 * 
	 * @param stepfunction the step function
	 * @param axis the axis
	 * @return an axis delegate for handling specified step function
	 */
	public static AxisMode newMode(Stepfunctions stepfunction, Axis axis) {
		DataTypes type = axis.getType();
		switch (stepfunction) {
		case ADD: 
		case MULTIPLY:
			switch (type) {
			case DATETIME:
				switch (axis.getPositionMode()) {
				case ABSOLUTE:
					return new AddMultiplyModeDate(axis);
				case RELATIVE:
					return new AddMultiplyModeDuration(axis);
				}
				break;
			case DOUBLE:
				return new AddMultiplyModeDouble(axis);
			case INT:
				return new AddMultiplyModeInt(axis);
			default:
				throw new IllegalArgumentException("unknown axis type");
			}
			throw new IllegalArgumentException("Incorrect Stepfunction code");
		case FILE: 
			return new FileMode(axis);
		case PLUGIN: 
			return new PluginMode(axis);
		case POSITIONLIST: 
			return new PositionlistMode(axis);
		default: 
			throw new IllegalArgumentException("Incorrect Stepfunction code");
		}
	}
	
	/**
	 * Copy Constructor.
	 * 
	 * @param axisMode the axis mode to be copied
	 * @param axis the axis the axis mode will belong to
	 * @return a copy of the given axis mode
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public static AxisMode newInstance(AxisMode axisMode, Axis axis) {
		AxisMode newMode = AxisMode.newMode(axis.getStepfunction(), axis);
		
		if (axisMode instanceof AddMultiplyModeInt) {
			AddMultiplyModeInt axisModeInt = (AddMultiplyModeInt) axisMode;
			AddMultiplyModeInt newModeInt = (AddMultiplyModeInt) newMode;
			newModeInt.setAdjustParameter(axisModeInt.getAdjustParameter());
			newModeInt.setAutoAdjust(axisModeInt.isAutoAdjust());
			newModeInt.setMainAxis(axisModeInt.isMainAxis());
			newModeInt.setStart(axisModeInt.getStart());
			newModeInt.setStop(axisModeInt.getStop());
			newModeInt.setStepwidth(axisModeInt.getStepwidth());
			newModeInt.setStepcount(axisModeInt.getStepcount());
		} else if (axisMode instanceof AddMultiplyModeDouble) {
			AddMultiplyModeDouble axisModeDouble = 
					(AddMultiplyModeDouble) axisMode;
			AddMultiplyModeDouble newModeDouble = 
					(AddMultiplyModeDouble) newMode;
			newModeDouble.setAdjustParameter(axisModeDouble.getAdjustParameter());
			newModeDouble.setAutoAdjust(axisModeDouble.isAutoAdjust());
			newModeDouble.setMainAxis(axisModeDouble.isMainAxis());
			newModeDouble.setStart(axisModeDouble.getStart());
			newModeDouble.setStop(axisModeDouble.getStop());
			newModeDouble.setStepwidth(axisModeDouble.getStepwidth());
			newModeDouble.setStepcount(axisModeDouble.getStepcount());
		} else if (axisMode instanceof AddMultiplyModeDate) {
			AddMultiplyModeDate axisModeDate = (AddMultiplyModeDate) axisMode;
			AddMultiplyModeDate newModeDate = (AddMultiplyModeDate) newMode;
			newModeDate.setAdjustParameter(axisModeDate.getAdjustParameter());
			newModeDate.setAutoAdjust(axisModeDate.isAutoAdjust());
			newModeDate.setMainAxis(axisModeDate.isMainAxis());
			newModeDate.setStart(axisModeDate.getStart());
			newModeDate.setStop(axisModeDate.getStop());
			newModeDate.setStepwidth(axisModeDate.getStepwidth());
			newModeDate.setStepcount(axisModeDate.getStepcount());
		} else if (axisMode instanceof AddMultiplyModeDuration) {
			AddMultiplyModeDuration axisModeDuration = 
					(AddMultiplyModeDuration) axisMode;
			AddMultiplyModeDuration newModeDuration = 
					(AddMultiplyModeDuration) newMode;
			newModeDuration.setAdjustParameter(
					axisModeDuration.getAdjustParameter());
			newModeDuration.setAutoAdjust(axisModeDuration.isAutoAdjust());
			newModeDuration.setMainAxis(axisModeDuration.isMainAxis());
			newModeDuration.setStart(axisModeDuration.getStart());
			newModeDuration.setStop(axisModeDuration.getStop());
			newModeDuration.setStepwidth(axisModeDuration.getStepwidth());
			newModeDuration.setStepcount(axisModeDuration.getStepcount());
		} else if (axisMode instanceof FileMode) {
			FileMode axisFileMode = (FileMode) axisMode;
			FileMode newFileMode = (FileMode) newMode;
			newFileMode.setFile(new File(axisFileMode.getFile().toURI()));
		} else if (axisMode instanceof PluginMode) {
			PluginMode axisPluginMode = (PluginMode) axisMode;
			PluginMode newPluginMode = (PluginMode) newMode;
			newPluginMode.setPluginController(PluginController.newInstance(
				axisPluginMode.getPluginController(),axis.getScanModule()));
		} else if (axisMode instanceof PositionlistMode) {
			PositionlistMode axisPositionlistMode = (PositionlistMode) axisMode;
			PositionlistMode newPositionlistMode = (PositionlistMode) newMode;
			newPositionlistMode.setPositionList(axisPositionlistMode
					.getPositionList());
		}
		
		return newMode;
	}
	
	/**
	 * @return the axis
	 */
	public Axis getAxis() {
		return axis;
	}

	/**
	 * Returns the number of motor positions of the corresponding axis.
	 * 
	 * @return the number of motor positions of the corresponding axis or 
	 * 		<code>null</code> if calculation is not possible
	 * @author Marcus Michalsky
	 * @since 1.10
	 */
	public abstract Integer getPositionCount();
	
	/**
	 * @param listener the listener to add
	 * @see {@link java.beans.PropertyChangeSupport#addPropertyChangeListener(PropertyChangeListener)}
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	/**
	 * @param listener the listener that should be removed
	 * @see {@link java.beans.PropertyChangeSupport#removePropertyChangeListener(PropertyChangeListener)}
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	/**
	 * Adds a listener for the given property.
	 * 
	 * @param property the property to listen to
	 * @param listener the listener that should be added
	 * @see {@link java.beans.PropertyChangeSupport#addPropertyChangeListener(String, PropertyChangeListener)}
	 */
	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport
				.addPropertyChangeListener(property, listener);
	}
	
	/**
	 * Removes a listener for the given property.
	 * 
	 * @param property the property to stop listening to
	 * @param listener the listener that should be removed
	 * @see {@link java.beans.PropertyChangeSupport#removePropertyChangeListener(String, PropertyChangeListener)}
	 */
	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(property,
				listener);
	}
}