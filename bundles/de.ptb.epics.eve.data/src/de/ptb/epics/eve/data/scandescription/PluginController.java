package de.ptb.epics.eve.data.scandescription;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
 * @author Hartmut Scherr
 */
public class PluginController implements IModelErrorProvider,
		IModelUpdateProvider {

	public static final String VALUES_PROP = "values";
	
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
	 * The parent scan module.
	 */
	private ScanModule scanModule;

	private PropertyChangeSupport propertyChangeSupport;
	
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

		this.propertyChangeSupport = new PropertyChangeSupport(this);
	
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

		String oldValue = this.values.get(name);
		
		this.values.put(name, value);

		propertyChangeSupport.firePropertyChange(PluginController.VALUES_PROP, oldValue, value);
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
	 * Fills the values with default values specified by the plug in.
	 */
	private void fillWithDefaults() {
		for (PluginParameter pluginParameter: this.plugin.getParameters()) {
			
			switch (pluginParameter.getType()){
			case AXISID:
				if (scanModule != null) {
					this.values.put(pluginParameter.getName(), scanModule.getAxes()[0].getMotorAxis().getID());
				}
				else 
					this.values.put(pluginParameter.getName(),
							pluginParameter.getDefaultValue());
				break;
			case CHANNELID:
			case DEVICEID:
			case DOUBLE:
			case INT:
			case ONOFF:
			case OPENCLOSE:
			case STRING:
				this.values.put(pluginParameter.getName(),
						pluginParameter.getDefaultValue());
				break;
			}
		}
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

				if (this.values.containsKey(parameter.getName())) {
					/* Wenn Parameter nicht Mandatory ist, ist ein leerer Value OK */
					if (!parameter.isMandatory() 
						&& this.values.get(parameter.getName()) == null) {
					}
					else if (!parameter.isValuePossible(this.values.get(parameter.getName()))) {
					errorList.add(new PluginError(this,
							PluginErrorTypes.WRONG_VALUE, parameter.getName()));
					}
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

	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(property, listener);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(property, listener);
	}

}