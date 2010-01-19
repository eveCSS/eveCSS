package de.ptb.epics.eve.viewer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.ecp1.client.ECP1Client;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.ptb.epics.eve.viewer";

	// The shared instance
	private static Activator plugin;
	private final MessagesContainer messagesContainer;
	private final XMLFileDispatcher xmlFileDispatcher;
	private final MeasurementDataDispatcher measurementDataDispatcher;
	private final EngineErrorReader engineErrorReader;
	private final ChainStatusAnalyzer chainStatusAnalyzer;
	
	private ScanDescription currentScanDescription;
	
	private ECP1Client ecp1Client;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
		this.ecp1Client = new ECP1Client();
		this.messagesContainer = new MessagesContainer();
		this.xmlFileDispatcher = new XMLFileDispatcher();
		this.engineErrorReader = new EngineErrorReader();
		this.chainStatusAnalyzer = new ChainStatusAnalyzer();
		this.measurementDataDispatcher = new MeasurementDataDispatcher();
		this.ecp1Client.getPlayListController().addNewXMLFileListener( this.xmlFileDispatcher );
		this.ecp1Client.addMeasurementDataListener( measurementDataDispatcher );
		this.ecp1Client.addErrorListener( this.engineErrorReader );
		this.ecp1Client.addChainStatusListener( this.chainStatusAnalyzer );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start( final BundleContext context ) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop( final BundleContext context ) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor( final String path ) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public ECP1Client getEcp1Client() {
		return this.ecp1Client;	
	}
	
	public MessagesContainer getMessagesContainer() {
		return this.messagesContainer;
	}
	
	public ScanDescription getCurrentScanDescription() {
		return this.currentScanDescription;
	}
	
	public void setCurrentScanDescription( final ScanDescription currentScanDescription ) {
		this.currentScanDescription = currentScanDescription;
	}
	
	public ChainStatusAnalyzer getChainStatusAnalyzer() {
		return this.chainStatusAnalyzer;
	}
	
}
