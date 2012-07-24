package de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;
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
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.motoraxiscomposite.MotorAxisComposite}
 * 's context menu.
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public class MenuContribution extends CompoundContributionItem {

	private static Logger logger = Logger.getLogger(MenuContribution.class
			.getName());

	final ImageDescriptor classImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("CLASS"));
	final ImageDescriptor motorImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("MOTOR"));
	final ImageDescriptor axisImage = ImageDescriptor.createFromImage(Activator
			.getDefault().getImageRegistry().get("AXIS"));
	final ImageDescriptor motorsAxesImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("MOTORSAXES"));

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		// create the list that will be returned.
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

		// create filter, that excludes devices used already in scan module
		ExcludeDevicesOfScanModuleFilterManualUpdate measuringStation = 
				new ExcludeDevicesOfScanModuleFilterManualUpdate(
						true, false, false, false, false);
		measuringStation.setSource(sm.getChain().getScanDescription()
				.getMeasuringStation());
		measuringStation.setScanModule(sm);
		measuringStation.update();

		// create menu from all remaining devices:

		// create a string buffer (for the insert all menu entry
		StringBuilder classDeviceList = new StringBuilder();
		StringBuilder motorDeviceList = new StringBuilder();
		
		// iterate over all classes
		List<String> classNames = new ArrayList<String>();
		classNames.addAll(measuringStation.getClassNameList());
		Collections.sort(classNames);

		for (final String className : classNames) {
			// each class gets a sub menu entry ...
			final MenuManager currentClassMenu = new MenuManager(className,
					classImage, className);
			
			// (re-)init insert all list (class level)
			classDeviceList = new StringBuilder();
			
			for (final AbstractDevice device : measuringStation
					.getDeviceList(className)) {
				
				// each motor of that class gets a sub menu (of that class)
				if (device instanceof Motor) {
					// (re-)init insert all list (motor level)
					motorDeviceList = new StringBuilder();
					
					final Motor motor = (Motor) device;
					final MenuManager currentMotorMenu = new MenuManager(
							motor.getName(), motorImage, motor.getName());
					currentClassMenu.add(currentMotorMenu);

					// iterate for each axis of the motor
					for (final MotorAxis axis : motor.getAxes()) {
						if (axis.getClassName().isEmpty()) {
							// add only axis which have no className
							currentMotorMenu.add(this.getItem(
									"de.ptb.epics.eve.editor.command.addaxis",
									"de.ptb.epics.eve.editor.command.addaxis.motoraxisid", 
									axis.getID(),
									axis.getName(), 
									axisImage));
							// add the id to device list (insert all menu entry)
							motorDeviceList.append(axis.getID() + "!");
						}
					}
					// if motor has no axes the (invisible) menu entry has to 
					// be removed
					if (motor.getAxes().size() == 0) {
						currentClassMenu.remove(currentMotorMenu);
					}
					// if only one axis in MotorMenu, switch axis from
					// MotorMenu into ClassMenu
					if (currentMotorMenu.getSize() == 1) {
						currentClassMenu.add(currentMotorMenu.getItems()[0]);
						currentMotorMenu.removeAll();
						currentClassMenu.remove(currentMotorMenu);
						// adjust insert all lists
						classDeviceList.append(motorDeviceList.toString());
					} else if (currentMotorMenu.getSize() > 1) {
						// insert all menu entry
						currentMotorMenu.add(new Separator());
						currentMotorMenu.add(this.getItem(
							"de.ptb.epics.eve.editor.command.addabstractdevices",
							"de.ptb.epics.eve.editor.command.addabstractdevices.devicelist",
							motorDeviceList.toString(), "Add All", null));
					}
				} else if (device instanceof MotorAxis) {
					MotorAxis axis = (MotorAxis) device;
					currentClassMenu.add(this.getItem(
							"de.ptb.epics.eve.editor.command.addaxis",
							"de.ptb.epics.eve.editor.command.addaxis.motoraxisid", 
							axis.getID(),
							axis.getName(), 
							axisImage));
					
					classDeviceList.append(axis.getID() + "!");
				}
			}
			
			if(currentClassMenu.getSize() > 1) {
				// insert all menu entry
				currentClassMenu.add(new Separator());
				currentClassMenu.add(this.getItem(
						"de.ptb.epics.eve.editor.command.addabstractdevices", 
						"de.ptb.epics.eve.editor.command.addabstractdevices.devicelist",
						classDeviceList.toString(), 
						"Add All", 
						null));
			}
			
			result.add(currentClassMenu);
		}

		// motors and axes WITHOUT class
		final MenuManager motorsAndAxesMenu = new MenuManager("Motors && Axes",
				motorsAxesImage, "motorsAndAxes");

		StringBuilder noClassMotorDeviceList = new StringBuilder();
		StringBuilder noClassAxesDeviceList = new StringBuilder();
		
		// all motors and axes without a class
		for (final Motor motor : measuringStation.getMotors()) {
			noClassMotorDeviceList = new StringBuilder();
			if (motor.getClassName().isEmpty() || motor.getClassName() == null) {
				final MenuManager currentMotorMenu = new MenuManager(
						motor.getName(), motorImage, motor.getName());
				motorsAndAxesMenu.add(currentMotorMenu);
				for (final MotorAxis axis : motor.getAxes()) {
					if (axis.getClassName().isEmpty()
							|| axis.getClassName() == null) {
						currentMotorMenu.add(this.getItem(
								"de.ptb.epics.eve.editor.command.addaxis", 
								"de.ptb.epics.eve.editor.command.addaxis.motoraxisid", 
								axis.getID(),
								axis.getName(),
								axisImage));
						// add the id to device list (insert all menu entry)
						noClassMotorDeviceList.append(axis.getID() + "!");
					}
				}
				// if a motor has no axes the (invisible) entry has to be 
				// removed
				if (motor.getAxes().size() == 0) {
					motorsAndAxesMenu.remove(currentMotorMenu);
				}
				// if only one axis in MotorMenu, switch axis from
				// MotorMenu into ClassMenu
				if (currentMotorMenu.getSize() == 1) {
					motorsAndAxesMenu.add(currentMotorMenu.getItems()[0]);
					currentMotorMenu.removeAll();
					motorsAndAxesMenu.remove(currentMotorMenu);
					// adjust insert all list
					noClassAxesDeviceList.append(noClassMotorDeviceList
							.toString());
				} else if (currentMotorMenu.getSize() > 1) {
					// insert all menu entry
					currentMotorMenu.add(new Separator());
					currentMotorMenu.add(this.getItem(
							"de.ptb.epics.eve.editor.command.addabstractdevices",
							"de.ptb.epics.eve.editor.command.addabstractdevices.devicelist",
							noClassMotorDeviceList.toString(), "Add All", null));
				}
			}
		}
		
		if (motorsAndAxesMenu.getSize() > 1) {
			motorsAndAxesMenu.add(new Separator());
			motorsAndAxesMenu.add(this.getItem(
					"de.ptb.epics.eve.editor.command.addabstractdevices",
					"de.ptb.epics.eve.editor.command.addabstractdevices.devicelist",
					noClassAxesDeviceList.toString(), "Add All", null));
		}
		
		result.add(motorsAndAxesMenu);
		return result.toArray(new IContributionItem[0]);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}
	
	/*
	 * 
	 */
	private CommandContributionItem getItem(String commandId, String paramId,
			String paramValue, String label, ImageDescriptor icon) {
		
		Map<String, String> params = new HashMap<String, String>();
		params.put(paramId, paramValue);
		CommandContributionItemParameter p = new CommandContributionItemParameter(
				PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow(), "",
				commandId,
				SWT.PUSH);
		p.label = label;
		p.icon = icon;
		p.parameters = params;

		CommandContributionItem item = new CommandContributionItem(
				p);
		item.setVisible(true);
		return item;
	}
}