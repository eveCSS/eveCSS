package de.ptb.epics.eve.editor.handler.axeschannelsview;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager;
import de.ptb.epics.eve.data.scandescription.defaults.channel.DefaultsChannel;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.axeschannelsview.AbstractScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddChannelDefaultHandler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			AddChannelDefaultHandler.class.getName());

	public static final String COMMAND_ID = 
			"de.ptb.epics.eve.editor.command.scanmodule.addchannel";
	
	public static final String PARAM_CHANNEL_ID = 
			"de.ptb.epics.eve.editor.command.scanmodule.addchannel.detectorchannelid";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String channelId = event.getParameter(PARAM_CHANNEL_ID);
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof AbstractScanModuleView) {
			ScanModule scanModule = ((AbstractScanModuleView)activePart).
					getScanModule();
			IMeasuringStation measuringStation = Activator.getDefault().
					getMeasuringStation();
			DetectorChannel detectorChannel = measuringStation.
					getDetectorChannelById(channelId);
			Channel channel = new Channel(scanModule, detectorChannel);
			if (!ScanModuleTypes.SAVE_CHANNEL_VALUES.equals(scanModule.getType())) {
				DefaultsChannel defaultsChannel = Activator.getDefault().
						getDefaults().getChannel(detectorChannel.getID());
				if (defaultsChannel != null) {
					DefaultsManager.transferDefaults(defaultsChannel, channel);
				}
			}
			scanModule.add(channel);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Detector Channel " + detectorChannel.getName() + " added.");
			}
		} else {
			LOGGER.error("Detector Channel could not be added.");
			throw new ExecutionException("Active part is not a Scan Module View.");
		}
		return null;
	}
}
