package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.channels.standard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import de.ptb.epics.eve.data.measuringstation.event.Event;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ChannelRedoEventMenuContributionMonitor {
	public static final String PARAM_CHAIN_ID = 
			"de.ptb.epics.eve.editor.command.AddChannelRedoEvent.ChainId";
	public static final String PARAM_SCANMODULE_ID = 
			"de.ptb.epics.eve.editor.command.AddChannelRedoEvent.ScanModuleId";
	public static final String PARAM_CHANNEL_ID = 
			"de.ptb.epics.eve.editor.command.AddChannelRedoEvent.ChannelId";
	public static final String PARAM_EVENT_ID = 
			"de.ptb.epics.eve.editor.command.AddChannelRedoEvent.EventId";
	
	private Channel channel;
	
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
	
	public ChannelRedoEventMenuContributionMonitor(Channel channel) {
		super();
		this.channel = channel;
		
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
	}
	
	public IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> result = new ArrayList<>();
		List<Event> events = Activator.getDefault().getMeasuringStation()
				.getEvents();
		Collections.sort(events);
		for (Event e : events) {
			Map<String, String> params = new HashMap<>();
			params.put(PARAM_CHAIN_ID, 
				Integer.toString(channel.getScanModule().getChain().getId()));
			params.put(PARAM_SCANMODULE_ID, 
				Integer.toString(channel.getScanModule().getId()));
			params.put(PARAM_CHANNEL_ID, channel.getDetectorChannel().getID());
			params.put(PARAM_EVENT_ID, e.getId());
			CommandContributionItemParameter p = new CommandContributionItemParameter(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(), "",
					"de.ptb.epics.eve.editor.command.AddChannelRedoEvent", SWT.PUSH);
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
		
		// TODO further divide sub menus if they contain too much items
		
		return result.toArray(new IContributionItem[0]);
	}
	
	/*
	 * returns the menu manager responsible for the given key.
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
}
