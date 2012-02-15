/**
 * 
 */
package de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.ui.actions.CompoundContributionItem;

import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.editor.Activator;

/**
 * @author mmichals
 *
 */
public class MenuContribution extends CompoundContributionItem {

	private ExcludeDevicesOfScanModuleFilterManualUpdate measuringStation;
	
	/**
	 * 
	 */
	public MenuContribution() {
		measuringStation = new ExcludeDevicesOfScanModuleFilterManualUpdate(
				false, true, false, false, false);
		measuringStation.setSource(Activator.getDefault().getMeasuringStation());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.CompoundContributionItem#getContributionItems()
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		
		List<IContributionItem> items = new ArrayList<IContributionItem>();
		
		for(String s : measuringStation.getClassNameList()) {
			CompoundContributionItem item = new CompoundContributionItem() {
				
				@Override
				protected IContributionItem[] getContributionItems() {

					// TODO Auto-generated method stub
					return null;
				}
			};
			
		}
		// TODO Auto-generated method stub
		return null;
	}

}
