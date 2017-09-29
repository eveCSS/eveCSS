package de.ptb.epics.eve.editor.views.motoraxisview.plugincomposite;

import de.ptb.epics.eve.data.measuringstation.PluginParameter;
import de.ptb.epics.eve.data.scandescription.PluginController;

public class PluginParameterValue {

	private PluginParameter pluginParameter;
	private PluginController pluginController;

	private String value;

	public PluginParameterValue(PluginParameter pluginParameter,
			PluginController pluginController, String value) {

		this.pluginParameter = pluginParameter;
		this.pluginController = pluginController;
		this.value = value;

	}

	public PluginParameter getPluginParameter() {
		return pluginParameter;
	}

	public PluginController getPluginController() {
		return pluginController;
	}

	public void setPluginParameter(PluginParameter pluginParameter) {
		this.pluginParameter = pluginParameter;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
