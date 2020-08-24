package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.positioning.ui;

import java.util.ArrayList;
import java.util.Collections;
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
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PositioningMenuContribution extends CompoundContributionItem {
	private static final Logger LOGGER = Logger.getLogger(
			PositioningMenuContribution.class.getName());
	
	private final ImageDescriptor axisImage = ImageDescriptor.createFromImage(
			Activator.getDefault().getImageRegistry().get("AXIS"));
	
	
	@Override
	protected IContributionItem[] getContributionItems() {
		// all axes currently in current scan module should be available
		// except those already present as positioning
		List<IContributionItem> result = new ArrayList<>();
		
		IWorkbenchPart activePart = Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getPartService().getActivePart();
		
		ScanModule scanModule;
		try {
			scanModule = ((AbstractScanModuleView)activePart).getScanModule();
		} catch (ClassCastException e) {
			LOGGER.error(e.getMessage(), e);
			return result.toArray(new IContributionItem[0]);
		}
		
		List<MotorAxis> motorAxes = new ArrayList<>();
		for (Axis axis : scanModule.getAxes()) {
			motorAxes.add(axis.getMotorAxis());
		}
		List<MotorAxis> positionings = new ArrayList<>();
		for (Positioning p : scanModule.getPositionings()) {
			positionings.add(p.getMotorAxis());
		}
		
		Collections.sort(motorAxes);
		
		for (MotorAxis ma : motorAxes) {
			if (!positionings.contains(ma)) {
				Map<String, String> params = new HashMap<>();
				params.put(
					"de.ptb.epics.eve.editor.command.scanmodule.addpositioning.motoraxisid", 
					ma.getID());
				
				CommandContributionItemParameter p = 
						new CommandContributionItemParameter(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
							"",
							"de.ptb.epics.eve.editor.command.scanmodule.addpositioning", 
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
