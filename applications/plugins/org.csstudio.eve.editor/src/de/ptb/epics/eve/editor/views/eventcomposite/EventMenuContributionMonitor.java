package de.ptb.epics.eve.editor.views.eventcomposite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.editor.Activator;
import static de.ptb.epics.eve.editor.views.eventcomposite.EventMenuContributionHelper.*;

/**
 * <code>EventMenuContributionMonitor</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class EventMenuContributionMonitor extends CompoundContributionItem {
	private static Logger logger = Logger
			.getLogger(EventMenuContributionMonitor.class.getName());

	private MenuManager mmAbc;
	private MenuManager mmDef;
	private MenuManager mmGhi;
	private MenuManager mmJkl;
	private MenuManager mmMno;
	private MenuManager mmPqr;
	private MenuManager mmStu;
	private MenuManager mmVwx;
	private MenuManager mmYz;
	private MenuManager mm09;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		this.mmAbc = new MenuManager("A-C", "abcMenu");
		this.mmDef = new MenuManager("D-F", "defMenu");
		this.mmGhi = new MenuManager("G-I", "ghiMenu");
		this.mmJkl = new MenuManager("J-L", "jklMenu");
		this.mmMno = new MenuManager("M-O", "MnoMenu");
		this.mmPqr = new MenuManager("P-R", "PqrMenu");
		this.mmStu = new MenuManager("S-U", "StuMenu");
		this.mmVwx = new MenuManager("V-X", "VwxMenu");
		this.mmYz = new MenuManager("Y-Z", "YzMenu");
		this.mm09 = new MenuManager("0-9", "09Menu");

		ArrayList<IContributionItem> result = new ArrayList<IContributionItem>();
		List<Event> events = Activator.getDefault().getMeasuringStation()
				.getEvents();
		Collections.sort(events);
		for (Event e : events) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("de.ptb.epics.eve.editor.command.AddEvent.EventId",
					e.getId());
			params.put("de.ptb.epics.eve.editor.command.AddEvent.EventType",
					EventTypes.MONITOR.toString());
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
			p.label = e.getName();
			p.parameters = params;
			CommandContributionItem item = new CommandContributionItem(p);
			item.setVisible(true);
			String firstChar = e.getName().substring(0, 1).toLowerCase();
			this.getMenu(firstChar).add(item);
		}

		result.add(mmAbc);
		result.add(mmDef);
		result.add(mmGhi);
		result.add(mmJkl);
		result.add(mmMno);
		result.add(mmPqr);
		result.add(mmStu);
		result.add(mmVwx);
		result.add(mmYz);
		result.add(mm09);

		return result.toArray(new IContributionItem[0]);
	}

	/*
	 * hash like function that returns the menu manager responsible for the
	 * given key.
	 */
	private MenuManager getMenu(String key) {
		if ("abc".contains(key)) {
			return mmAbc;
		} else if ("def".contains(key)) {
			return mmDef;
		} else if ("ghi".contains(key)) {
			return mmGhi;
		} else if ("jkl".contains(key)) {
			return mmJkl;
		} else if ("mno".contains(key)) {
			return mmMno;
		} else if ("pqr".contains(key)) {
			return mmPqr;
		} else if ("stu".contains(key)) {
			return mmStu;
		} else if ("vwx".contains(key)) {
			return mmVwx;
		} else if ("yz".contains(key)) {
			return mmYz;
		}
		return mm09;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}
}