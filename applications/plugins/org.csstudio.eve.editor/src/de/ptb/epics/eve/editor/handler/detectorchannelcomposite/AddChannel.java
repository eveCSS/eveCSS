package de.ptb.epics.eve.editor.handler.detectorchannelcomposite;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsChannel;
import de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * Default handler of the add channel command.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public class AddChannel implements IHandler {

	private static Logger logger = Logger.getLogger(AddChannel.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String channelId = event
				.getParameter("de.ptb.epics.eve.editor.command.addchannel.detectorchannelid");
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart.getSite().getId()
				.equals("de.ptb.epics.eve.editor.views.ScanModulView")) {
			ScanModule scanModule = ((ScanModuleView) activePart)
					.getCurrentScanModule();
			DetectorChannel detChannel = scanModule.getChain()
					.getScanDescription().getMeasuringStation()
					.getDetectorChannelById(channelId);
			Channel channel = new Channel(scanModule, detChannel);
			if (!(scanModule.getType() == ScanModuleTypes.SAVE_CHANNEL_VALUES)) {
				DefaultsChannel defCh = Activator.getDefault().getDefaults()
						.getChannel(detChannel.getID());
				if (defCh != null) {
					DefaultsManager.transferDefaults(defCh, channel);
				}
			}
			scanModule.add(channel);
			if (logger.isDebugEnabled()) {
				logger.debug("Detector Channel " + detChannel.getName()
						+ " added.");
			}
		} else {
			logger.warn("Detector Channel was not added!");
			throw new ExecutionException(
					"ScanModulView is not the active part!");
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHandled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}
}