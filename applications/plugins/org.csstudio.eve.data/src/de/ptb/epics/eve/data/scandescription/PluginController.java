package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.measuringstation.PluginParameter;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.IModelErrorProvider;
import de.ptb.epics.eve.data.scandescription.errors.PluginError;
import de.ptb.epics.eve.data.scandescription.errors.PluginErrorTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateProvider;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * This class represents a plug in controller.
 * 
 * A <code>PluginController</code> takes a
 * {@link de.ptb.epics.eve.data.measuringstation.PlugIn} and saves the
 * corresponding parameter values.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 * @author Marcus Michalsky
 */
public class PluginController implements IModelErrorProvider,
		IModelUpdateProvider {

	/* The plug in to control. */
	private PlugIn plugin;

	/*
	 * The values of the parameter of the plug in.
	 */
	private Map<String, String> values;

	/*
	 * The list of model update listener.
	 */
	private List<IModelUpdateListener> modelUpdateListener;

	/*
	 * This flag indicates if the values has been filled with default values.
	 */
	private boolean defaultFlag;

	/*
	 * The parent scan module.
	 */
	private ScanModule scanModule;

	/**
	 * Constructs a(n) (empty) <code>PluginController</code>.
	 */
	public PluginController() {
		this(null);
	}

	/**
	 * Constructs a <code>PluginController</code> with a given plug in to
	 * control.
	 * 
	 * @param plugin
	 *            the plug in to control.
	 */
	public PluginController(final PlugIn plugin) {
		this.plugin = plugin;
		this.values = new HashMap<String, String>();
		this.modelUpdateListener = new ArrayList<IModelUpdateListener>();

		if (this.plugin != null) {
			this.fillWithDefaults();
		}
	}

	/**
	 * Copy Constructor.
	 * 
	 * @param pluginController the plugin controller to be copied
	 * @param scanModule the scan module the plug in will be added to
	 * @return a copy of the given plugin controller
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public static PluginController newInstance(
			PluginController pluginController, ScanModule scanModule) {
		PluginController newPluginController = 
				new PluginController(pluginController.getPlugin());
		newPluginController.setScanModule(scanModule);
		newPluginController.setValues(new HashMap<String, String>(
				pluginController.getValues()));
		if (pluginController.isFilledWithDefault()) {
			newPluginController.fillWithDefaults();
		}
		return newPluginController;
	}
	
	/**
	 * Returns the current plug in to control.
	 * 
	 * @return the current plug in that is controlled by this controller
	 */
	public PlugIn getPlugin() {
		return this.plugin;
	}

	/**
	 * Sets the controlled plug in.
	 * 
	 * @param plugin
	 *            the plug in to control.
	 */
	public void setPlugin(final PlugIn plugin) {
		this.plugin = plugin;
		this.values.clear();
		if (this.plugin != null) {
// auskommentiert am 12.9.14 von Hartmut
			this.fillWithDefaults();
		}
		updateListeners();
	}

	/**
	 * Sets a value for a parameter of the plug in.
	 * 
	 * @param name
	 *            the name of the parameter.
	 * @param value
	 *            the value for the parameter.
	 */
	public void set(final String name, final String value) {
		this.values.put(name, value);
		this.defaultFlag = false;
		updateListeners();
	}

	/**
	 * Sets back a value.
	 * 
	 * @param name
	 *            the name of the value that should be set back.
	 */
	public void unset(final String name) {
		this.values.remove(name);
		updateListeners();
	}

	/**
	 * Returns a value of a parameter.
	 * 
	 * @param name
	 *            the name of the parameter.
	 * @return the value of the parameter.
	 */
	public String get(final String name) {
		return this.values.get(name);
	}

	/**
	 * Returns the parameter value map.
	 * 
	 * @return parameter values
	 */
	public Map<String, String> getValues() {
		return this.values;
	}
	
	/**
	 * Set parameter value map.
	 * 
	 * @param values parameter values
	 */
	public void setValues(Map<String, String> values) {
		this.values = values;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean addModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.modelUpdateListener.add(modelUpdateListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean removeModelUpdateListener(
			final IModelUpdateListener modelUpdateListener) {
		return this.modelUpdateListener.remove(modelUpdateListener);
	}

	/**
	 * Checks whether the values are filled with default values.
	 * 
	 * @return <code>true</code> if the values are filled, <code>false</code>
	 *         otherwise
	 */
	public boolean isFilledWithDefault() {
		return this.defaultFlag;
	}

	/**
	 * Fills the values with default values specified by the plug in.
	 */
	private void fillWithDefaults() {
		Iterator<PluginParameter> it = this.plugin.getParameters().iterator();
		while (it.hasNext()) {
			final PluginParameter currentPluginParameter = it.next();
			// TODO um hier vernünftige Werte vorzuschlagen muß irgendwie
			// erkannt werden, welche Parameter diskrete Werte beinhalten
			// z.B. referenceaxis aus position Plugin.
			// kann man da einen Zusatz in das Messplatz.xml File schreiben
			// wie z.B. type=discrete?
			// es müßte über den datatype axisid erkannt werden!!!
			if (currentPluginParameter.getType().toString().equals("AXISID")) {
				// aus dem scanModul wird der erste Wert des Plugins erzeugt!
				if (scanModule != null) {
					Axis[] currentAxis = scanModule.getAxes();
					String[] currentField = new String[currentAxis.length];
					for (int i = 0; i < currentAxis.length; ++i) {
						currentField[i] = currentAxis[i].getMotorAxis()
								.getName();
					}
					this.values.put(currentPluginParameter.getName(),
							currentField[0]);
				} else {
					this.values.put(currentPluginParameter.getName(),
							currentPluginParameter.getDefaultValue());
				}
			} else {
				this.values.put(currentPluginParameter.getName(),
						currentPluginParameter.getDefaultValue());
			}
		}
		this.defaultFlag = true;
	}

	/**
	 * Returns an array of all elements of the plug in controller.
	 * 
	 * @return an array with all key/value pairs.
	 */
	@SuppressWarnings("unchecked")
	public Entry<String, String>[] getElements() {
		return this.values.entrySet().toArray(new Entry[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuffer stringBuffer = new StringBuffer();
		if (this.plugin != null) {
			Iterator<PluginParameter> it = this.plugin.getParameters()
					.iterator();
			while (it.hasNext()) {
				final PluginParameter pp = it.next();
				stringBuffer.append(pp.getName());
				stringBuffer.append('=');
				stringBuffer.append(this.values.get(pp.getName()));
				stringBuffer.append("; ");
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * Returns the scan module.
	 * 
	 * @return the scan module.
	 */
	public ScanModule getScanModule() {
		return this.scanModule;
	}

	/**
	 * Sets the scan module.
	 * 
	 * @param scanModule
	 *            The current scan module.
	 */
	public void setScanModule(final ScanModule scanModule) {
		this.scanModule = scanModule;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		final List<IModelError> errorList = new ArrayList<IModelError>();
		if (this.plugin != null) {
			final Iterator<PluginParameter> it = this.plugin.getParameters()
					.iterator();
			while (it.hasNext()) {
				final PluginParameter parameter = it.next();
				if (parameter.isMandatory()
						&& !this.values.containsKey(parameter.getName())) {
					errorList.add(new PluginError(this,
							PluginErrorTypes.MISSING_MANDATORY_PARAMETER,
							parameter.getName()));
				}
				if (this.values.containsKey(parameter.getName())
						&& !parameter.isValuePossible(this.values.get(parameter
								.getName()))) {
					errorList.add(new PluginError(this,
							PluginErrorTypes.WRONG_VALUE, parameter.getName()));
				}
			}
		} else {
			errorList.add(new PluginError(this,
					PluginErrorTypes.PLUING_NOT_SET, ""));
		}
		return errorList;
	}

	/*
	 * 
	 */
	private void updateListeners() {
		final CopyOnWriteArrayList<IModelUpdateListener> list = new CopyOnWriteArrayList<IModelUpdateListener>(
				this.modelUpdateListener);

		Iterator<IModelUpdateListener> it = list.iterator();

		while (it.hasNext()) {
			it.next().updateEvent(new ModelUpdateEvent(this, null));
		}
	}
}