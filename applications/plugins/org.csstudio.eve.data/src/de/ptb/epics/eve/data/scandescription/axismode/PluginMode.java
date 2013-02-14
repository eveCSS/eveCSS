package de.ptb.epics.eve.data.scandescription.axismode;

import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.AxisErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class PluginMode extends AxisMode implements IModelUpdateListener {
	private PluginController pluginController;

	/**
	 * @param axis the axis this mode belongs to
	 */
	public PluginMode(Axis axis) {
		super(axis);
	}
	
	/**
	 * @return the pluginController
	 */
	public PluginController getPluginController() {
		return pluginController;
	}

	/**
	 * @param pluginController the pluginController to set
	 */
	public void setPluginController(PluginController pluginController) {
		if (this.pluginController != null) {
			this.pluginController.removeModelUpdateListener(this);
		}
		this.propertyChangeSupport
				.firePropertyChange("plugin", this.pluginController,
						this.pluginController = pluginController);
		if (this.pluginController != null) {
			this.pluginController.addModelUpdateListener(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		List<IModelError> errors = new ArrayList<IModelError>();
		if (this.pluginController == null) {
			errors.add(new AxisError(this.axis, AxisErrorTypes.PLUGIN_NOT_SET));
		} else {
			errors.addAll(this.pluginController.getModelErrors());
			if (this.pluginController.getModelErrors().size() > 0) {
				errors.add(new AxisError(this.axis, AxisErrorTypes.PLUGIN_ERROR));
			}
		}
		return errors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer getPositionCount() {
		// TODO Not supported yet
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
	}
}