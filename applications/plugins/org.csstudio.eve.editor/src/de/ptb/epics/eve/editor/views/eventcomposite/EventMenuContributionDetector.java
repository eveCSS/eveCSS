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
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
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
		ScanDescription sd = ((ScanDescriptionEditor) Activator.getDefault()
				.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getActiveEditor()).getContent();
		for (Event e : sd.getEvents()) {
			if (e.getType().equals(EventTypes.DETECTOR)) {
				Map<String, String> params = new HashMap<String, String>();
				params.put("de.ptb.epics.eve.editor.command.AddEvent.EventId",
						e.getID());
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
				p.label = e.getName();
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