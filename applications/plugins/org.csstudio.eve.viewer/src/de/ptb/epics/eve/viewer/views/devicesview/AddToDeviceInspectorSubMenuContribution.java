package de.ptb.epics.eve.viewer.views.devicesview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView;

/**
 * <code>AddToDeviceInspectorSubmenuContribution</code> is a dynamic context 
 * menu provider for the 
 * {@link de.ptb.epics.eve.viewer.views.devicesview.DevicesView}.
 * <p>
 * The context menu shows all available 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.DeviceInspectorView}s 
 * a device could be added to. This set must be determined dynamically.
 * 
 * @author Marcus Michalsky
 * @since 0.4.2
 */
public class AddToDeviceInspectorSubMenuContribution extends
		CompoundContributionItem {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		IViewReference[] ref = PlatformUI.getWorkbench().
			getActiveWorkbenchWindow().getActivePage().getViewReferences();
		
		ArrayList<IContributionItem> result = new ArrayList<IContributionItem>();
		
		for(IViewReference view : ref) {
			if(view.getId().equals(DeviceInspectorView.ID)) {
				Map<String,String> params = new HashMap<String,String>();
				params.put("DeviceInspectorSecondaryId", view.getSecondaryId());
				
				CommandContributionItemParameter p = new CommandContributionItemParameter(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
					"", 
					"de.ptb.epics.eve.viewer.AddDeviceToDeviceInspector", 
					SWT.PUSH);
				p.label = view.getPartName();
				p.parameters = params;
				
				CommandContributionItem item = new CommandContributionItem(p);
				item.setVisible(true);
				
				result.add(item);
			}
		}
		return result.toArray(new IContributionItem[0]);
	}
}