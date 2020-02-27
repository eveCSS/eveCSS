package de.ptb.epics.eve.editor.handler.axeschannelsview;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.editor.views.axeschannelsview.AbstractScanModuleView;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class RemoveChannelsDefaultHandler extends AbstractHandler {
	public static final String ID = 
			"de.ptb.epics.eve.editor.command.scanmodule.removechannels";
	
	private static final Logger LOGGER = Logger.getLogger(
			RemoveChannelsDefaultHandler.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof AbstractScanModuleView) {
			ScanModule sm = ((AbstractScanModuleView)activePart).getScanModule();
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			for (Object o : ((IStructuredSelection)selection).toList()) {
				if ((o instanceof Channel)) {
					List<Channel> normalizationChannels = this.getNormalizationChannels((Channel)o);
					List<Channel> stoppedByChannels = this.getStoppedByChannels((Channel)o);
					sm.remove((Channel)o);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Channel " + ((Channel)o).getDetectorChannel().
								getName() + " removed");
					}
					if (!normalizationChannels.isEmpty()) {
						this.createNormalizationInfoDialog((Channel)o, normalizationChannels);
					}
					if (!stoppedByChannels.isEmpty() ) {
						this.createStoppedByInfoDialog((Channel)o, stoppedByChannels);
					}
				}
			}
		} else {
			LOGGER.error("Channel(s) could not be removed!");
			throw new ExecutionException("Active Part is not a Scan Module View.");
		}
		return null;
	}
	
	private List<Channel> getNormalizationChannels(Channel ch) {
		List<Channel> normalizationChannels = new ArrayList<>();
		for (Channel smChannel : ch.getScanModule().getChannelList()) {
			if (smChannel.equals(ch)) {
				continue;
			}
			if (smChannel.getNormalizeChannel() != null && 
					smChannel.getNormalizeChannel().equals(ch.getDetectorChannel())) {
				normalizationChannels.add(smChannel);
			}
		}
		return normalizationChannels;
	}
	
	private List<Channel> getStoppedByChannels(Channel ch) {
		List<Channel> stoppedByChannels = new ArrayList<>();
		for (Channel smChannel : ch.getScanModule().getChannelList()) {
			if (smChannel.equals(ch)) {
				continue;
			}
			if (ChannelModes.INTERVAL.equals(smChannel.getChannelMode()) && 
					smChannel.getStoppedBy() != null &&
					smChannel.getStoppedBy().equals(ch.getDetectorChannel())) {
				stoppedByChannels.add(smChannel);
			}
		}
		return stoppedByChannels;
	}
	
	private void createNormalizationInfoDialog(Channel ch, List<Channel> normalizationChannels) {
		final String message;
		if (normalizationChannels.size() > 1) {
			message = "Channel " + ch.getDetectorChannel().getName() +
					"was also removed as Normalization Channel from Channels " +
					StringUtil.buildCommaSeparatedString(normalizationChannels) + 
					".";
		} else {
			message = "Channel " + ch.getDetectorChannel().getName() + 
					"was also removed as Normalization Channel from Channel " +
					StringUtil.buildCommaSeparatedString(normalizationChannels) + 
					".";
		}
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(
						Display.getDefault().getActiveShell(), 
						"Normalization Removed", message);
			}
		});
	}
	
	private void createStoppedByInfoDialog(Channel ch, List<Channel> stoppedByChannels) {
		final String message;
		if (stoppedByChannels.size() > 1) {
			message = "Channel " + ch.getDetectorChannel().getName()
					+ " was also removed as Stopped By Channel from Channels "
					+ StringUtil.buildCommaSeparatedString(stoppedByChannels) + 
					".";
		} else {
			message = "Channel " + ch.getDetectorChannel().getName()
					+ " was also removed as Stopped By Channel from Channel "
					+ StringUtil.buildCommaSeparatedString(stoppedByChannels) + 
					".";
		}
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(Display
						.getDefault().getActiveShell(),
						"Stopped By Removed",
						message);
			}
		});
	}
}
