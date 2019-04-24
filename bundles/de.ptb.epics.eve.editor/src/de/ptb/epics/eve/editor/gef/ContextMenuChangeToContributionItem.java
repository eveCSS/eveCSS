package de.ptb.epics.eve.editor.gef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public class ContextMenuChangeToContributionItem extends CompoundContributionItem {
	private static final String CMD_ID_EMPTY_SCANMODULE = "de.ptb.epics.eve.editor.command.changeto.emptyscanmodule";
	private static final String CMD_ID_STATIC_AXES_SNAPSHOT = "de.ptb.epics.eve.editor.command.changeto.axissnapshotstatic";
	private static final String CMD_ID_STATIC_CHANNEL_SNAPSHOT = "de.ptb.epics.eve.editor.command.changeto.channelsnapshotstatic";
	private static final String CMD_ID_DYNAMIC_AXES_SNAPSHOT = "de.ptb.epics.eve.editor.command.changeto.axissnapshotdynamic";
	private static final String CMD_ID_DYNAMIC_CHANNEL_SNAPSHOT = "de.ptb.epics.eve.editor.command.changeto.channelsnapshotdynamic";
	private static final String TEXT_EMPTY_SCANMODULE = "Empty Scan Module";
	private static final String TEXT_STATIC_AXES_SNAPSHOT = "Axis Snapshot (static)";
	private static final String TEXT_STATIC_CHANNEL_SNAPSHOT = "Channel Snapshot (static)";
	private static final String TEXT_DYNAMIC_AXES_SNAPSHOT = "Axis Snapshot (dynamic)";
	private static final String TEXT_DYNAMIC_CHANNEL_SNAPSHOT = "Channel Snapshot (dynamic)";
	private static final String ICON_EMPTY_SCANMODULE = "icons/devices/scanmodule32.gif";
	private static final String ICON_STATIC_AXES_SNAPSHOT = "icons/devices/scanmoduleaxes32.gif";
	private static final String ICON_STATIC_CHANNEL_SNAPSHOT = "icons/devices/scanmodulechannels32.gif";
	private static final String ICON_DYNAMIC_AXES_SNAPSHOT = "icons/devices/scanmoduleaxesdynamic32.gif";
	private static final String ICON_DYNAMIC_CHANNEL_SNAPSHOT = "icons/devices/scanmodulechannelsdynamic32.gif";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> result = new ArrayList<>();

		ScanModule sm = this.getSelection();
		if (sm == null) {
			return result.toArray(new IContributionItem[0]);
		}
		switch (sm.getType()) {
		case CLASSIC:
		case SAVE_AXIS_POSITIONS:
		case SAVE_CHANNEL_VALUES:
			result.add(this.getCommand(
					CMD_ID_EMPTY_SCANMODULE, 
					TEXT_EMPTY_SCANMODULE, 
					ICON_EMPTY_SCANMODULE));
			result.add(this.getCommand(
					CMD_ID_STATIC_AXES_SNAPSHOT, 
					TEXT_STATIC_AXES_SNAPSHOT, 
					ICON_STATIC_AXES_SNAPSHOT));
			result.add(this.getCommand(
					CMD_ID_DYNAMIC_AXES_SNAPSHOT, 
					TEXT_DYNAMIC_AXES_SNAPSHOT,
					ICON_DYNAMIC_AXES_SNAPSHOT));
			result.add(this.getCommand(
					CMD_ID_STATIC_CHANNEL_SNAPSHOT, 
					TEXT_STATIC_CHANNEL_SNAPSHOT,
					ICON_STATIC_CHANNEL_SNAPSHOT));
			result.add(this.getCommand(
					CMD_ID_DYNAMIC_CHANNEL_SNAPSHOT, 
					TEXT_DYNAMIC_CHANNEL_SNAPSHOT,
					ICON_DYNAMIC_CHANNEL_SNAPSHOT));
			break;
		case DYNAMIC_AXIS_POSITIONS:
			// switching from dynamic axis to dynamic axis does nothing
			result.add(this.getCommand(
					CMD_ID_EMPTY_SCANMODULE, 
					TEXT_EMPTY_SCANMODULE, 
					ICON_EMPTY_SCANMODULE));
			result.add(this.getCommand(
					CMD_ID_STATIC_AXES_SNAPSHOT, 
					TEXT_STATIC_AXES_SNAPSHOT, 
					ICON_STATIC_AXES_SNAPSHOT));
			result.add(this.getCommand(
					CMD_ID_STATIC_CHANNEL_SNAPSHOT, 
					TEXT_STATIC_CHANNEL_SNAPSHOT,
					ICON_STATIC_CHANNEL_SNAPSHOT));
			result.add(this.getCommand(
					CMD_ID_DYNAMIC_CHANNEL_SNAPSHOT, 
					TEXT_DYNAMIC_CHANNEL_SNAPSHOT,
					ICON_DYNAMIC_CHANNEL_SNAPSHOT));
			break;
		case DYNAMIC_CHANNEL_VALUES:
			// switching from dynamic channel to dynamic channel does nothing
			result.add(this.getCommand(
					CMD_ID_EMPTY_SCANMODULE, 
					TEXT_EMPTY_SCANMODULE, 
					ICON_EMPTY_SCANMODULE));
			result.add(this.getCommand(
					CMD_ID_STATIC_AXES_SNAPSHOT, 
					TEXT_STATIC_AXES_SNAPSHOT, 
					ICON_STATIC_AXES_SNAPSHOT));
			result.add(this.getCommand(
					CMD_ID_DYNAMIC_AXES_SNAPSHOT, 
					TEXT_DYNAMIC_AXES_SNAPSHOT,
					ICON_DYNAMIC_AXES_SNAPSHOT));
			result.add(this.getCommand(
					CMD_ID_STATIC_CHANNEL_SNAPSHOT, 
					TEXT_STATIC_CHANNEL_SNAPSHOT,
					ICON_STATIC_CHANNEL_SNAPSHOT));
			break;
		default:
			break;
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return this.getSelection() != null;
	}
	
	private ScanModule getSelection() {
		ISelection selection = Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getSelectionService().getSelection();
		
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = 
					(IStructuredSelection)selection;
			if (structuredSelection.size() == 1 && structuredSelection.
					getFirstElement() instanceof ScanModuleEditPart) {
				return ((ScanModuleEditPart)structuredSelection.getFirstElement()).getModel();
			}
		}
		return null;
	}
	
	private CommandContributionItem getCommand(String id, String label, String iconPath) {
		Map<String, String> params = new HashMap<>();
		CommandContributionItemParameter p = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"",id,
				SWT.PUSH);
		p.label = label;
		p.parameters = params;
		p.icon = Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, 
				iconPath);
		CommandContributionItem item = new CommandContributionItem(p);
		item.setVisible(true);
		return item;
	}
}
