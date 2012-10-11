package de.ptb.epics.eve.data.scandescription.axismode;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Axis;
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
		case MULTIPLY: switch(type) {
			case DATETIME: return new AddMultiplyModeDate(axis);
			case DOUBLE: return new AddMultiplyModeDouble(axis);
			case INT: return new AddMultiplyModeInt(axis);
			default: throw new IllegalArgumentException("unknown axis type");
			}
		case FILE: return new FileMode(axis);
		case PLUGIN: return new PluginMode(axis);
		case POSITIONLIST: return new PositionlistMode(axis);
		default: throw new IllegalArgumentException(
				"Incorrect Stepfunction code");
		}
	}
	
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