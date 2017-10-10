package de.ptb.epics.eve.viewer;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoader;
import de.ptb.epics.eve.ecp1.client.interfaces.INewXMLFileListener;

/**
 * Catches the event that a new xml file was loaded (a new scan) and tells 
 * interested parties 
 * (registered by {@link #addPropertyChangeListener(String, PropertyChangeListener)}) 
 * about it. Either {@link #DEVICE_DEFINITION_PROP} or 
 * {@link #SCAN_DESCRIPTION_PROP} (or both) are available.
 * 
 * @author Marcus Michalsky
 * @since 1.13
 */
public class XMLDispatcher implements INewXMLFileListener {
	
	/** device definition property */
	public static final String DEVICE_DEFINITION_PROP = "measuringStation";
	
	/** scan description property */
	public static final String SCAN_DESCRIPTION_PROP = "scanDescription";
	
	private static Logger logger = 
			Logger.getLogger(XMLDispatcher.class.getName());

	private IMeasuringStation measuringStation;
	private ScanDescription scanDescription;
	
	private PropertyChangeSupport propertyChangeSupport;

	/**
	 * Initializes property change support.
	 */
	public XMLDispatcher() {
		this.measuringStation = null;
		this.scanDescription = null;
		this.propertyChangeSupport = new PropertyChangeSupport(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void newXMLFileReceived(final byte[] xmlData) {
		/*
		 * receives the new xml file in raw form and loads it into the object
		 * structure. Listeners of both properties are informed.
		 */
		logger.debug("new xml file received");
		try {
			final File schemaFile = Activator.getDefault().getSchemaFile();
			final MeasuringStationLoader measuringStationLoader = 
					new MeasuringStationLoader(schemaFile);
			final IMeasuringStation ims = 
					measuringStationLoader.loadFromByteArray(xmlData);
			Activator.getDefault().getWorkbench().getDisplay().syncExec(
				new Runnable() {
					@Override public void run() {
							propertyChangeSupport.firePropertyChange(
									XMLDispatcher.DEVICE_DEFINITION_PROP,
									measuringStation, measuringStation = ims);
					}
				}
			);
			
			final ScanDescriptionLoader scanDescriptionLoader = 
					new ScanDescriptionLoader(measuringStation, schemaFile);
			scanDescriptionLoader.loadFromByteArray(xmlData);
			final ScanDescription sd = 
					scanDescriptionLoader.getScanDescription();
			Activator.getDefault().setCurrentScanDescription(sd); // should be removed...
			// ... interested parties should use the listener mechanism
			// using Activator.getDefault().getCurrentScanDescription maybe 
			// is not thread safe !
			Activator.getDefault().getWorkbench().getDisplay().syncExec(
				new Runnable() {
					@Override public void run() {
							propertyChangeSupport.firePropertyChange(
									XMLDispatcher.SCAN_DESCRIPTION_PROP,
									scanDescription, scanDescription = sd);
					}
				}
			);
		} catch(final ParserConfigurationException e) {
			logger.error(e.getMessage(), e);
		} catch(final SAXException e) {
			logger.error(e.getMessage(), e);
		} catch(final IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * 
	 * @param property
	 * @param listener
	 */
	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport
				.addPropertyChangeListener(property, listener);
	}
	
	/**
	 * 
	 * @param property
	 * @param listener
	 */
	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(property,
				listener);
	}
}