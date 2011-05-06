package de.ptb.epics.eve.viewer;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.processors.MeasuringStationLoader;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.ecp1.client.ECP1Client;
import de.ptb.epics.eve.preferences.PreferenceConstants;
import de.ptb.epics.eve.viewer.messages.MessagesContainer;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	private static Logger logger = Logger.getLogger(Activator.class);

	/**
	 * The unique identifier of the plug in
	 */
	public static final String PLUGIN_ID = "de.ptb.epics.eve.viewer";

	// The shared instance
	private static Activator plugin;
	
	private final MessagesContainer messagesContainer;
	private final XMLFileDispatcher xmlFileDispatcher;
	private final EngineErrorReader engineErrorReader;
	private final ChainStatusAnalyzer chainStatusAnalyzer;
	private IMeasuringStation measuringStation;
	private ColorRegistry colorreg;
	private FontRegistry fontreg;
	
	private ScanDescription currentScanDescription;
	
	private ECP1Client ecp1Client;
	
	private RequestProcessor requestProcessor;

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

		this.ecp1Client.getPlayListController().addNewXMLFileListener( this.xmlFileDispatcher );

		this.ecp1Client.addErrorListener( this.engineErrorReader );
		this.ecp1Client.addEngineStatusListener( this.chainStatusAnalyzer );
		this.ecp1Client.addChainStatusListener( this.chainStatusAnalyzer );
		this.colorreg = new ColorRegistry();
		this.fontreg = new FontRegistry();
		this.requestProcessor = new RequestProcessor( Display.getCurrent() );
		this.ecp1Client.addRequestListener( this.requestProcessor );
		
		// activate logging 
		DOMConfigurator.configure(System.getProperty("user.home") + "/logger.xml");
	}

	/**
	 * {@inheritDoc}
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		
		// register fonts, colors and images
		Font defaultFont = fontreg.defaultFont();
		FontData[] fontData = defaultFont.getFontData();
		// Use a smaller font if system font is higher 11
		for (int i = 0; i < fontData.length; i++) {
			if (fontData[i].getHeight() > 11) fontData[i].setHeight(11);
		}
		fontreg.put("VIEWERFONT", fontData);
		
		colorreg.put("COLOR_PV_INITIAL", new RGB(0, 0, 0));
		colorreg.put("COLOR_PV_CONNECTED", new RGB(0, 0, 0));
		colorreg.put("COLOR_PV_DISCONNECTED", new RGB(130, 130, 130));
		colorreg.put("COLOR_PV_ALARM", new RGB(255, 0, 0));
		colorreg.put("COLOR_PV_OK", new RGB(0, 180, 0));
		colorreg.put("COLOR_PV_MINOR", new RGB(255, 255, 50));
		colorreg.put("COLOR_PV_MAJOR", new RGB(255, 0, 0));
		colorreg.put("COLOR_PV_INVALID", new RGB(180, 180, 180));
		colorreg.put("COLOR_PV_UNKNOWN", new RGB(130, 130, 130));
		
		ImageRegistry imagereg = getImageRegistry();
		imagereg.put("GREENPLUS12", imageDescriptorFromPlugin(PLUGIN_ID, "icons/greenPlus12.12.gif").createImage());
		imagereg.put("GREENMINUS12", imageDescriptorFromPlugin(PLUGIN_ID, "icons/greenMinus12.12.gif").createImage());
		imagereg.put("GREENGO12", imageDescriptorFromPlugin(PLUGIN_ID, "icons/greenGo12.12.gif").createImage());
		imagereg.put("PLAY16", imageDescriptorFromPlugin(PLUGIN_ID, "icons/play.gif").createImage());
		imagereg.put("PAUSE16", imageDescriptorFromPlugin(PLUGIN_ID, "icons/pause.gif").createImage());
		imagereg.put("STOP16", imageDescriptorFromPlugin(PLUGIN_ID, "icons/stop.gif").createImage());
		imagereg.put("SKIP16", imageDescriptorFromPlugin(PLUGIN_ID, "icons/skip.gif").createImage());
		imagereg.put("HALT16", imageDescriptorFromPlugin(PLUGIN_ID, "icons/halt.gif").createImage());
		imagereg.put("KILL16", imageDescriptorFromPlugin(PLUGIN_ID, "icons/kill.gif").createImage());
		imagereg.put("TRIGGER16", imageDescriptorFromPlugin(PLUGIN_ID, "icons/trigger.gif").createImage());
		imagereg.put("PLAYALL16", imageDescriptorFromPlugin(PLUGIN_ID, "icons/playAll2.gif").createImage());
		
		imagereg.put("MOTOR", imageDescriptorFromPlugin(PLUGIN_ID, "icons/motor.gif").createImage());
		imagereg.put("AXIS", imageDescriptorFromPlugin(PLUGIN_ID, "icons/axis.gif").createImage());
		imagereg.put("DETECTOR", imageDescriptorFromPlugin(PLUGIN_ID, "icons/detector.gif").createImage());
		imagereg.put("CHANNEL", imageDescriptorFromPlugin(PLUGIN_ID, "icons/channel.gif").createImage());
		
		imagereg.put("MOVEUP", imageDescriptorFromPlugin(PLUGIN_ID, "icons/prev_nav.gif").createImage());
		imagereg.put("MOVEDOWN", imageDescriptorFromPlugin(PLUGIN_ID, "icons/next_nav.gif").createImage());
		
		final String measuringStationDescription = de.ptb.epics.eve.preferences.Activator.getDefault().getPreferenceStore().getString( PreferenceConstants.P_DEFAULT_MEASURING_STATION_DESCRIPTION );
		
		if( !measuringStationDescription.equals( "" ) ) { 
			final int lastSeperatorIndex = measuringStationDescription.lastIndexOf( File.separatorChar );
			final String schemaFileLocation = measuringStationDescription.substring( 0, lastSeperatorIndex + 1 ) + "scml.xsd";
			final File schemaFile = new File( schemaFileLocation );
			final File measuringStationDescriptionFile = new File( measuringStationDescription );

			if( measuringStationDescriptionFile.exists() ) {
				try {
					final MeasuringStationLoader measuringStationLoader = new MeasuringStationLoader( schemaFile );
					measuringStationLoader.load( measuringStationDescriptionFile );
					measuringStation = measuringStationLoader.getMeasuringStation();

				} catch( final Throwable th ) {
					measuringStation = null;
					logger.warn(th.getMessage(), th);
				}
			}
		}		
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop(final BundleContext context) throws Exception {
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
	 * 
	 * @return
	 */
	public IMeasuringStation getMeasuringStation() {
		return this.measuringStation;
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
	
	/**
	 * 
	 * @param colorname
	 * @return
	 */
	public Color getColor(String colorname){
		Color color = colorreg.get(colorname);
		if (color == null)
			return colorreg.get("COLOR_PV_INITIAL");
		else
			return color;
	}
	
	/**
	 * 
	 * @param fontname
	 * @return
	 */
	public Font getFont(String fontname){
		Font font = fontreg.get(fontname);
		if (font == null)
			return fontreg.defaultFont();
		else
			return font;
	}
	
	/**
	 * Returns the 
	 * 
	 * @return the 
	 */
	public ECP1Client getEcp1Client() {
		return this.ecp1Client;	
	}
	
	/**
	 * Returns the {@link de.ptb.epics.eve.viewer.messages.MessagesContainer} 
	 * used to collect messages of several types from different sources.
	 * 
	 * @return the messages container of the viewer
	 */
	public MessagesContainer getMessagesContainer() {
		return this.messagesContainer;
	}
	
	/**
	 * 
	 * @return
	 */
	public ScanDescription getCurrentScanDescription() {
		return this.currentScanDescription;
	}
	
	/**
	 * 
	 * 
	 * @param currentScanDescription
	 */
	public void setCurrentScanDescription(
			final ScanDescription currentScanDescription) {
		this.currentScanDescription = currentScanDescription;
	}
	
	/**
	 * 
	 * @return
	 */
	public ChainStatusAnalyzer getChainStatusAnalyzer() {
		return this.chainStatusAnalyzer;
	}

	/**
	 * Adds a scan description to the play list.
	 * 
	 * @param file the file containing the Scan Description (SCML)
	 */
	public void addScanDescription(final File file) {

		Activator.getDefault().connectEngine();
		// either we were connected before or have done it above, we are 
		// connected now and can add the scan description to the play list.
		if(this.ecp1Client.isRunning()) {
			try {
				this.ecp1Client.getPlayListController().addLocalFile(file);
			} catch(final IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Connect the Engine if Ecp1Client is running
	 */
	public void connectEngine() {
	    // if we are not connected to the engine -> connect to it
		if( !Activator.getDefault().getEcp1Client().isRunning()) {
			// getting the service to execute registered commands
			IHandlerService handlerService = (IHandlerService) 
					PlatformUI.getWorkbench().getService(IHandlerService.class);
			// execute the connect command
			try {
				handlerService.executeCommand(
						"de.ptb.epics.eve.viewer.connectCommand", null);
			} catch (Exception e2) {
				logger.error(e2.getMessage(), e2);
			}
		}
	};

}