package de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * Menu entries of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite.MotorAxisComposite}'s
 * context menu.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public class MenuContribution extends CompoundContributionItem {

	final ImageDescriptor classImage = ImageDescriptor.createFromImage(
			Activator.getDefault().getImageRegistry().get("CLASS"));
	final ImageDescriptor motorImage = ImageDescriptor.createFromImage(
			Activator.getDefault().getImageRegistry().get("MOTOR"));
	final ImageDescriptor axisImage = ImageDescriptor.createFromImage(
			Activator.getDefault().getImageRegistry().get("AXIS"));

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		// create the list that will be returned.
		ArrayList<IContributionItem> result = new ArrayList<IContributionItem>();
		
		// get current scan module
		ScanModule sm = ((ScanModuleView)Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getPartService().getActivePart()).
				getCurrentScanModule();
		
		// create filter, that excludes devices used already in scan module
		ExcludeDevicesOfScanModuleFilterManualUpdate measuringStation = 
				new ExcludeDevicesOfScanModuleFilterManualUpdate(
						true, false, false, false, false);
		measuringStation.setSource(sm.getChain().getScanDescription().
				getMeasuringStation());
		measuringStation.setScanModule(sm);
		measuringStation.update();
		
		// create menu from all remaining devices:
		
		// iterate over all classes
		for(final String className : measuringStation.getClassNameList()) {
			// each class gets a sub menu entry ...
			final MenuManager currentClassMenu = new MenuManager(
					className, classImage, className);
			for(final AbstractDevice device : 
				measuringStation.getDeviceList(className)) {
				
				// each motor of that class gets a sub menu (of that class)
				if(device instanceof Motor) {
					final Motor motor = (Motor)device;
					final MenuManager currentMotorMenu = 
						new MenuManager(motor.getName(), motorImage, 
								motor.getName());
					currentClassMenu.add(currentMotorMenu);
					
					// iterate for each axis of the motor
					for(final MotorAxis axis : motor.getAxes()) {
						if (axis.getClassName().isEmpty()) {
							// add only axis which have no className
							Map<String,String> params = new HashMap<String,String>();
							params.put("de.ptb.epics.eve.editor.command.addaxis.motoraxisid", 
									axis.getID());
							CommandContributionItemParameter p = 
								new CommandContributionItemParameter(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
									"", 
									"de.ptb.epics.eve.editor.command.addaxis", 
									SWT.PUSH);
							p.label = axis.getName();
							p.icon = axisImage;
							p.parameters = params;
							
							CommandContributionItem item = 
								new CommandContributionItem(p);
							item.setVisible(true);
							currentMotorMenu.add(item);
						}
					}
					// if only one axis in MotorMenu, switch axis from 
					// MotorMenu into ClassMenu
					if (currentMotorMenu.getSize() == 1) {
						currentClassMenu.add(currentMotorMenu.getItems()[0]);
						currentMotorMenu.removeAll();
					}
				} else if(device instanceof MotorAxis) {
					MotorAxis axis = (MotorAxis)device;
					Map<String,String> params = new HashMap<String,String>();
					params.put("de.ptb.epics.eve.editor.command.addaxis.motoraxisid", 
							axis.getID());
					CommandContributionItemParameter p = 
						new CommandContributionItemParameter(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
							"", 
							"de.ptb.epics.eve.editor.command.addaxis", 
							SWT.PUSH);
					p.label = axis.getName();
					p.icon = axisImage;
					p.parameters = params;
					
					CommandContributionItem item = 
						new CommandContributionItem(p);
					item.setVisible(true);
					currentClassMenu.add(item);
				}
			}
			result.add(currentClassMenu);
		}
		
		// all motors and axes without a class
		for(final Motor motor : measuringStation.getMotors()) {
			if(motor.getClassName().isEmpty() || motor.getClassName() == null) {
				final MenuManager currentMotorMenu = 
						new MenuManager(motor.getName(), motorImage, 
								motor.getName());
				for(final MotorAxis axis : motor.getAxes()) {
					if(axis.getClassName().isEmpty() || axis.getClassName() == null) {
						Map<String,String> params = new HashMap<String,String>();
						params.put("de.ptb.epics.eve.editor.command.addaxis.motoraxisid", 
								axis.getID());
						CommandContributionItemParameter p = 
							new CommandContributionItemParameter(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
								"", 
								"de.ptb.epics.eve.editor.command.addaxis", 
								SWT.PUSH);
						p.label = axis.getName();
						p.icon = axisImage;
						p.parameters = params;
						
						CommandContributionItem item = 
							new CommandContributionItem(p);
						item.setVisible(true);
						currentMotorMenu.add(item);
					}
				}
				// if only one axis in MotorMenu, switch axis from 
				// MotorMenu into ClassMenu
				if (currentMotorMenu.getSize() == 1) {
					result.add(currentMotorMenu.getItems()[0]);
					currentMotorMenu.removeAll();
				}
				result.add(currentMotorMenu);
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