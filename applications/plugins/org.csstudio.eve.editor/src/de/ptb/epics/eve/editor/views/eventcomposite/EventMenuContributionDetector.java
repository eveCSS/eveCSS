package de.ptb.epics.eve.editor.views.eventcomposite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import de.ptb.epics.eve.data.EventImpacts;
import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.event.DetectorEvent;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.ScanDescriptionEditor;
import static de.ptb.epics.eve.editor.views.eventcomposite.EventMenuContributionHelper.*;

/**
 * <code>EventMenuContributionDetector</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class EventMenuContributionDetector extends CompoundContributionItem {
	private static Logger logger = Logger
			.getLogger(EventMenuContributionDetector.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> result = new ArrayList<IContributionItem>();
		
		// create a (sorted) map, where each entry contains:
		// - the channel as key (comparable)
		// - a list of events (one entry for each occurrence of the detector)
		// only modules of type classic will be considered
		Map<Channel, List<Event>> detectorEventsMap = 
				new TreeMap<Channel, List<Event>>();
		ScanDescription sd = ((ScanDescriptionEditor) Activator.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor()).getContent();
		for (Chain chain : sd.getChains()) {
			for (ScanModule sm : chain.getScanModules()) {
				if (sm.getType() != ScanModuleTypes.CLASSIC) {
					continue;
				}
				for (Channel channel : sm.getChannels()) {
					List<Event> channelEvents = detectorEventsMap.get(
							channel);
					if (channelEvents == null) {
						channelEvents = new LinkedList<Event>();
						detectorEventsMap.put(channel, channelEvents);
					}
					channelEvents.add(new DetectorEvent(channel));
				}
			}
		}
		
		// create the menu entries (one menu item for each detector with 
		// sub menu containing the occurrences
		for (Map.Entry<Channel, List<Event>> entry : detectorEventsMap.entrySet()) {
			MenuManager detectorMenu = new MenuManager(
					entry.getKey().getAbstractDevice().getName(), 
					entry.getKey().getAbstractDevice().getID());
			for (Event event : entry.getValue()) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("de.ptb.epics.eve.editor.command.AddEvent.EventId",
						event.getId());
				params.put(
						"de.ptb.epics.eve.editor.command.AddEvent.EventType",
						EventTypes.DETECTOR.toString());
				EventImpacts eventImpact = determineEventImpact();
				if (eventImpact == null) {
					logger.warn("EventImpact not found.");
					return result.toArray(new IContributionItem[0]);
				}
				params.put(
						"de.ptb.epics.eve.editor.command.AddEvent.EventImpact",
						eventImpact.toString());
				IViewPart activePart = getActiveViewPart();
				if (activePart == null) {
					logger.warn("active part is not a view.");
					return result.toArray(new IContributionItem[0]);
				}
				params.put(
						"de.ptb.epics.eve.editor.command.AddEvent.ActivePart",
						activePart.getViewSite().getId());
				CommandContributionItemParameter p = new CommandContributionItemParameter(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
						"", "de.ptb.epics.eve.editor.command.addevent",
						SWT.PUSH);
				p.label = event.getName();
				p.parameters = params;
				CommandContributionItem item = new CommandContributionItem(p);
				item.setVisible(true);
				detectorMenu.add(item);
			}
			result.add(detectorMenu);
		}
		return result.toArray(new IContributionItem[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}
}