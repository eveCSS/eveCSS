package de.ptb.epics.eve.editor.adapters;

import org.apache.log4j.Logger;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class ScanModulePropertySource implements IPropertySource {

	private static final Logger LOGGER = Logger
			.getLogger(ScanModulePropertySource.class);
	
	private static final String PROPERTY_NAME = "scanmodule.name";
	private static final String PROPERTY_MEASUREMENTS = "scanmodule.measurements";
	
	private ScanModule scanModule;
	
	/**
	 * 
	 * @param scanModule
	 */
	public ScanModulePropertySource(ScanModule scanModule) {
		this.scanModule = scanModule;
		LOGGER.debug("construct");
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getEditableValue() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor namedescriptor = 
				new TextPropertyDescriptor(PROPERTY_NAME, "Name");
		namedescriptor.setCategory("General");
		
		PropertyDescriptor numberOfMeasurementsDescriptor = 
				new TextPropertyDescriptor(PROPERTY_MEASUREMENTS, 
						"#Measurements");
		numberOfMeasurementsDescriptor.setCategory("General");
		
		return new IPropertyDescriptor[] {namedescriptor, 
				numberOfMeasurementsDescriptor};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(PROPERTY_NAME)) {
			return this.scanModule.getName();
		} else if (id.equals(PROPERTY_MEASUREMENTS)) {
			return Integer.toString(this.scanModule.getValueCount());
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPropertySet(Object id) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetPropertyValue(Object id) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(PROPERTY_NAME)) {
			this.scanModule.setName(value.toString());
		} else if (id.equals(PROPERTY_MEASUREMENTS)) {
			
		}
	}
}