package de.ptb.epics.eve.editor.views.eventcomposite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import de.ptb.epics.eve.data.EventImpacts;
import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.ScanDescriptionEditor;
import static de.ptb.epics.eve.editor.views.eventcomposite.EventMenuContributionHelper.*;

/**
 * <code>EventMenuContributionSchedule</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class EventMenuContributionSchedule extends CompoundContributionItem {
	private static Logger logger = Logger
			.getLogger(EventMenuContributionSchedule.class.getName());

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> result = new ArrayList<IContributionItem>();
		ScanDescription sd = ((ScanDescriptionEditor) Activator.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor()).getContent();
		
		{
			Event startEvent = sd.getDefaultStartEvent();

			Map<String, String> params = new HashMap<String, String>();
			params.put("de.ptb.epics.eve.editor.command.AddEvent.EventId",
					startEvent.getId());
			params.put("de.ptb.epics.eve.editor.command.AddEvent.EventType",
					EventTypes.SCHEDULE.toString());
			EventImpacts eventImpact = determineEventImpact();
			if (eventImpact == null) {
				logger.warn("EventImpact not found.");
				return result.toArray(new IContributionItem[0]);
			}
			params.put("de.ptb.epics.eve.editor.command.AddEvent.EventImpact",
					eventImpact.toString());
			IViewPart activePart = getActiveViewPart();
			if (activePart == null) {
				logger.warn("active part is not a view.");
				return result.toArray(new IContributionItem[0]);
			}
			params.put("de.ptb.epics.eve.editor.command.AddEvent.ActivePart",
					activePart.getViewSite().getId());
			CommandContributionItemParameter p = new CommandContributionItemParameter(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(), "",
					"de.ptb.epics.eve.editor.command.addevent", SWT.PUSH);
			p.label = startEvent.getName();
			p.parameters = params;
			CommandContributionItem item = new CommandContributionItem(p);
			item.setVisible(true);
			result.add(item);
		}
		
		for (Chain chain : sd.getChains()) {
			for (ScanModule sm : chain.getScanModules()) {
				String eventId = "S-" + chain.getId() + "-" + sm.getId() + "-"
						+ "E";
				Map<String, String> params = new HashMap<String, String>();
				params.put("de.ptb.epics.eve.editor.command.AddEvent.EventId",
						eventId);
				params.put(
						"de.ptb.epics.eve.editor.command.AddEvent.EventType",
						EventTypes.SCHEDULE.toString());
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
				p.label = "Schedule " + "( " + eventId + " )";
				p.parameters = params;
				CommandContributionItem item = new CommandContributionItem(p);
				item.setVisible(true);
				result.add(item);
			}
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