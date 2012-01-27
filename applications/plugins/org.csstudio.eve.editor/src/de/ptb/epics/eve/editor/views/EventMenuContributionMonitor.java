package de.ptb.epics.eve.editor.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.editor.Activator;

import static de.ptb.epics.eve.editor.views.EventMenuContributionHelper.*;

/**
 * <code>EventMenuContributionMonitor</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class EventMenuContributionMonitor extends CompoundContributionItem {

	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> result = new ArrayList<IContributionItem>();
		
		for(Event e : Activator.getDefault().getMeasuringStation().getEvents()) {
			if(e.getType().equals(EventTypes.MONITOR)) {
				Map<String,String> params = new HashMap<String,String>();
				params.put("de.ptb.epics.eve.editor.command.AddEvent.EventId", 
						e.getID());
				params.put("de.ptb.epics.eve.editor.command.AddEvent.EventType", 
						EventTypes.MONITOR.toString());
				params.put("de.ptb.epics.eve.editor.command.AddEvent.EventImpact", 
					determineEventImpact().toString());
				params.put("de.ptb.epics.eve.editor.command.AddEvent.ActivePart", 
					getActiveViewPart().getViewSite().getId());
				
				CommandContributionItemParameter p = 
					new CommandContributionItemParameter(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
						"", 
						"de.ptb.epics.eve.editor.command.addevent", 
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