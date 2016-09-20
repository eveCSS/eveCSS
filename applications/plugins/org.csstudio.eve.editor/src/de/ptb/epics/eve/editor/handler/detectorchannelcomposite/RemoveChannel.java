package de.ptb.epics.eve.editor.handler.detectorchannelcomposite;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.channelmode.ChannelModes;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * Default handler of the remove channel command.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public class RemoveChannel implements IHandler {

	private static Logger logger = 
			Logger.getLogger(RemoveChannel.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart.getSite().getId().equals(
				"de.ptb.epics.eve.editor.views.ScanModulView")) {
			ScanModule sm = ((ScanModuleView)activePart).
					getCurrentScanModule();
			ISelection selection = HandlerUtil.getCurrentSelection(event);
			if(selection instanceof IStructuredSelection) {
				final Object o = ((IStructuredSelection)selection).getFirstElement();
				if(o instanceof Channel) {
					final List<Channel> normalizationChannels = new ArrayList<>();
					final List<Channel> stoppedByChannels = new ArrayList<>();
					for (Channel channel : sm.getChannels()) {
						if (channel.equals((Channel)o)) {
							continue;
						}
						if (channel.getNormalizeChannel() != null &&
								channel.getNormalizeChannel().equals(((Channel)o).getDetectorChannel())) {
							normalizationChannels.add(channel);
						}
						if (channel.getChannelMode().equals(ChannelModes.INTERVAL)
								&& channel.getStoppedBy() != null
								&& channel.getStoppedBy().equals(((Channel)o).getDetectorChannel())) {
							stoppedByChannels.add(channel);
						}
					}
					sm.remove((Channel)o);
					if(logger.isDebugEnabled()) {
						logger.debug("Channel " + ((Channel)o).getDetectorChannel().
							getName() + " removed");
					}
					if (!normalizationChannels.isEmpty()) {
						final String message;
						if (normalizationChannels.size() > 1) {
							message = "Channel " + ((Channel) o)
								.getDetectorChannel().getName()
								+ " was also removed as Normalization Channel from Channels "
								+ StringUtil.buildCommaSeparatedString(normalizationChannels) + ".";
						} else {
							message = "Channel " + ((Channel) o)
									.getDetectorChannel().getName()
									+ " was also removed as Normalization Channel from Channel "
									+ StringUtil.buildCommaSeparatedString(normalizationChannels) + ".";
						}
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openInformation(Display
										.getDefault().getActiveShell(),
										"Normalization Removed",
										message);
							}
						});
					}
					if (!stoppedByChannels.isEmpty()) {
						final String message;
						if (stoppedByChannels.size() > 1) {
							message = "Channel " + ((Channel) o)
									.getDetectorChannel().getName()
									+ " was also removed as Stopped By Channel from Channels "
									+ StringUtil.buildCommaSeparatedString(stoppedByChannels) + ".";
						} else {
								message = "Channel " + ((Channel) o)
									.getDetectorChannel().getName()
									+ " was also removed as Stopped By Channel from Channel "
									+ StringUtil.buildCommaSeparatedString(stoppedByChannels) + ".";
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
			}
		} else {
			logger.warn("Channel could not be removed!");
			throw new ExecutionException("ScanModulView is not the active Part");
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