package de.ptb.epics.eve.editor.views.scanmoduleview.positioningcomposite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * 
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class MenuContribution extends CompoundContributionItem {

	private static Logger logger = Logger.getLogger(MenuContribution.class
			.getName());
	
	final ImageDescriptor axisImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("AXIS"));
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> result = new ArrayList<IContributionItem>();
		
		// get active part
		IWorkbenchPart activePart = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getPartService().getActivePart();

		// get current scan module
		ScanModule sm;
		try {
			sm = ((ScanModuleView) activePart).getCurrentScanModule();
		} catch (ClassCastException e) {
			logger.warn(e.getMessage());
			return result.toArray(new IContributionItem[0]);
		}
		
		// fetch all used axes
		List<MotorAxis> motorAxes = new ArrayList<MotorAxis>();
		for(Axis a : sm.getAxes()) {
			motorAxes.add(a.getMotorAxis());
		}
		// fetch all positionings
		List<MotorAxis> positionings = new ArrayList<MotorAxis>();
		for(Positioning p : sm.getPositionings()) {
			positionings.add(p.getMotorAxis());
		}
		
		// create menu entry for all axes which have no positionings
		for(MotorAxis ma : motorAxes) {
			if(!positionings.contains(ma)) {
				Map<String,String> params = new HashMap<String,String>();
				params.put(
					"de.ptb.epics.eve.editor.command.addpositioning.motoraxisid", 
					ma.getID());
				
				CommandContributionItemParameter p = 
					new CommandContributionItemParameter(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
						"", 
						"de.ptb.epics.eve.editor.command.addpositioning", 
						SWT.PUSH);
				p.label = ma.getName();
				p.icon = this.axisImage;
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