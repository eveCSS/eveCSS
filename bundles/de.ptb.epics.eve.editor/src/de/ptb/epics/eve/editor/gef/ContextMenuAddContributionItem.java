package de.ptb.epics.eve.editor.gef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import de.ptb.epics.eve.editor.Activator;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ContextMenuAddContributionItem extends CompoundContributionItem {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> result = new ArrayList<>();
		
		Map<String, String> params = new HashMap<>();
		CommandContributionItemParameter p = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"", "de.ptb.epics.eve.editor.command.addappended",
				SWT.PUSH);
		p.label = "Appended Scan Module";
		p.parameters = params;
		p.icon = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, 
				"icons/devices/scanmodule.gif");
		CommandContributionItem item = new CommandContributionItem(p);
		item.setVisible(true);
		result.add(item);
		
		params = new HashMap<>();
		p = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"", "de.ptb.epics.eve.editor.command.addnested",
				SWT.PUSH);
		p.label = "Nested Scan Module";
		p.parameters = params;
		p.icon = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, 
				"icons/devices/scanmodule.gif");
		item = new CommandContributionItem(p);
		item.setVisible(true);
		result.add(item);
		
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