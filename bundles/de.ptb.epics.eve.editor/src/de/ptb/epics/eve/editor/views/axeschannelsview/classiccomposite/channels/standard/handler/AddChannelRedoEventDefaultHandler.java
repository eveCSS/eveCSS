package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.handler;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.ScanDescriptionEditor;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard.ChannelRedoEventMenuContributionMonitor;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AddChannelRedoEventDefaultHandler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			AddChannelRedoEventDefaultHandler.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		/* we want to add a monitor event (found in the device definition) to 
		 * a channel as a redo event.
		 */
		
		// get command parameters
		int chainId = Integer.parseInt(event.getParameter(
				ChannelRedoEventMenuContributionMonitor.PARAM_CHAIN_ID));
		int smId = Integer.parseInt(event.getParameter(
				ChannelRedoEventMenuContributionMonitor.PARAM_SCANMODULE_ID));
		String channelId = event.getParameter(
				ChannelRedoEventMenuContributionMonitor.PARAM_CHANNEL_ID);
		String eventId = event.getParameter(
				ChannelRedoEventMenuContributionMonitor.PARAM_EVENT_ID);
		
		// determine channel where the event should be added to
		ScanDescription scanDescription = ((ScanDescriptionEditor) Activator.
				getDefault().getWorkbench().getActiveWorkbenchWindow().
				getActivePage().getActiveEditor()).getContent();
		Channel channelToAddEventTo = null;
		for (Channel channel : scanDescription.getChain(chainId).
				getScanModuleById(smId).getChannelList()) {
			if (channelId.equals(channel.getDetectorChannel().getID())) {
				channelToAddEventTo = channel;
				break;
			}
		}
		if (channelToAddEventTo == null) {
			String errorMessage = "Channel not found!";
			LOGGER.error(errorMessage);
			throw new ExecutionException(errorMessage);
		}
		
		// get event
		Event monitorEvent = Activator.getDefault().getMeasuringStation().
				getEventById(eventId);
		if (monitorEvent == null) {
			String errorMessage = "Event not found!";
			LOGGER.error(errorMessage);
			throw new ExecutionException(errorMessage);
		}
		
		channelToAddEventTo.addRedoEvent(new ControlEvent(EventTypes.MONITOR, 
				monitorEvent, monitorEvent.getId()));
		
		return null;
	}
}
