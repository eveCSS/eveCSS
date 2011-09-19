package de.ptb.epics.eve.viewer;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.xml.sax.SAXException;


import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoader;
import de.ptb.epics.eve.ecp1.client.interfaces.INewXMLFileListener;
import de.ptb.epics.eve.viewer.views.devicesview.DevicesView;

/**
 * <code>XMLFileDispatcher</code>.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class XMLFileDispatcher implements INewXMLFileListener {
	
	private static Logger logger = 
			Logger.getLogger(XMLFileDispatcher.class.getName());
	
	private PlotViewDispatcher plotViewDispatcher;

	/**
	 * Constructs a <code>XMLFileDispatcher</code>.
	 */
	public XMLFileDispatcher() {
		this.plotViewDispatcher = new PlotViewDispatcher();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void newXMLFileReceived(final byte[] xmlData) {
		try {
			final File schemaFile = new File(Activator.getDefault().
					getRootDirectory() + "eve/schema.xsd");
			final MeasuringStationLoader measuringStationLoader = 
					new MeasuringStationLoader(schemaFile);
			final IMeasuringStation measuringStation = 
					measuringStationLoader.loadFromByteArray(xmlData);
			
			Activator.getDefault().getWorkbench().getDisplay().syncExec( 
				new Runnable() {
					@Override public void run() {
						IViewReference[] ref = PlatformUI.getWorkbench().
							getActiveWorkbenchWindow().getPartService().
							getActivePart().getSite().getPage().getViewReferences();
						DevicesView mview = null;
						for(int i = 0; i < ref.length; ++i) {
							if(ref[i].getId().equals(DevicesView.ID)) {
								mview = (DevicesView)ref[i].getPart(false);
							}
						}
						if (mview != null) {
							mview.setMeasuringStation(measuringStation);
						}
					}
				} 
			);
			
			final ScanDescriptionLoader scanDescriptionLoader = 
					new ScanDescriptionLoader(measuringStation, schemaFile);
			scanDescriptionLoader.loadFromByteArray(xmlData);
			final ScanDescription scanDescription = 
					scanDescriptionLoader.getScanDescription();
			Activator.getDefault().setCurrentScanDescription(scanDescription);
			Activator.getDefault().getWorkbench().getDisplay().syncExec(
				new Runnable() {
					@Override public void run() {
						plotViewDispatcher.setScanDescription(scanDescription);
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
}