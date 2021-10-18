package de.ptb.epics.eve.editor.views.chainview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.handler.pauseconditions.AddPauseConditionDefaultHandler;

/**
 * @author Marcus Michalsky
 * @since 1.36
 */
public class PauseConditionsMenuContribution extends CompoundContributionItem {
	private final ImageDescriptor classImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("CLASS"));
	private final ImageDescriptor motorImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("MOTOR"));
	private final ImageDescriptor axisImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("AXIS"));
	private final ImageDescriptor detectorImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("DETECTOR"));
	private final ImageDescriptor channelImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("CHANNEL"));
	private final ImageDescriptor deviceImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("DEVICE"));
	private final ImageDescriptor optionImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("OPTION"));
	private final ImageDescriptor motorsAxesImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("MOTORSAXES"));
	private final ImageDescriptor detectorsAndChannelsImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("DETECTORSCHANNELS"));
	private final ImageDescriptor devicesImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("DEVICES"));
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> result = new ArrayList<>();
		
		// use Filter although nothing is filtered, because "normal" implementation
		// Measuringstation#getDeviceList(className) seems to be buggy
		// motor axis with class definition in motor without is missing in result
		// see #5742-47
		ExcludeDevicesOfScanModuleFilterManualUpdate measuringStation = 
				new ExcludeDevicesOfScanModuleFilterManualUpdate(
						false, false, false, false, false);
		measuringStation.setSource(Activator.getDefault().getMeasuringStation());
		measuringStation.update();
		
		List<String> classNames = new ArrayList<>();
		classNames.addAll(measuringStation.getClassNameList());
		Collections.sort(classNames);
		
		// devices with classes
		for (String className : classNames) {
			// create a menu entry for each class name
			MenuManager currentClassMenu = new MenuManager(
					className, classImage, className);
			
			// for "each device of that class" do ...
			for (AbstractDevice device : measuringStation.getDeviceList(className)) {
				if (device instanceof Motor) {
					this.addDeviceMenu((Motor)device, currentClassMenu);
				} else if (device instanceof MotorAxis) {
					this.addDeviceMenu((MotorAxis)device, currentClassMenu);
				} else if (device instanceof Detector) {
					this.addDeviceMenu((Detector)device, currentClassMenu);
				} else if (device instanceof DetectorChannel) {
					this.addDeviceMenu((DetectorChannel)device, currentClassMenu);
				} else if (device instanceof Device) {
					this.addDeviceMenu((Device)device, currentClassMenu);
				}
			}
			
			result.add(currentClassMenu);
		}
		
		// devices without classes
		MenuManager motorsAndAxesMenu = new MenuManager("Motors && Axes", 
				motorsAxesImage, "motorsAndAxes");
		for (Motor motor : measuringStation.getMotors()) {
			if (motor.getClassName() == null || motor.getClassName().isEmpty()) {
				this.addDeviceMenu(motor, motorsAndAxesMenu);
			}
		}
		result.add(motorsAndAxesMenu);
		
		MenuManager detectorsAndChannelsMenu = new MenuManager(
				"Detectors && Channels", detectorsAndChannelsImage, 
				"detectorsAndChannels");
		for (Detector detector : measuringStation.getDetectors()) {
			if (detector.getClassName() == null || detector.getClassName().isEmpty()) {
				this.addDeviceMenu(detector, detectorsAndChannelsMenu);
			}
		}
		result.add(detectorsAndChannelsMenu);
		
		MenuManager devicesMenu = new MenuManager("Devices", devicesImage, "devices");
		for (Device device : measuringStation.getDevices()) {
			if (device.getClassName() == null || device.getClassName().isEmpty()) {
				this.addDeviceMenu(device, devicesMenu);
			}
		}
		result.add(devicesMenu);

		return result.toArray(new IContributionItem[0]);
	}
	
	private void addDeviceMenu(Motor motor, MenuManager menu) {
		MenuManager motorMenu = new MenuManager(motor.getName(), motorImage, 
				motor.getName());
		menu.add(motorMenu);
		this.addMotorAxisEntries(motor, motorMenu);
		this.addOptionEntries(motor, motorMenu);
	}
	
	private void addDeviceMenu(MotorAxis axis, MenuManager menu) {
		this.addMotorAxisEntry(axis, menu);
	}
	
	private void addDeviceMenu(Detector detector, MenuManager menu) {
		MenuManager detectorMenu = new MenuManager(detector.getName(), 
				detectorImage, detector.getName());
		menu.add(detectorMenu);
		this.addDetectorChannelEntries(detector, detectorMenu);
		this.addOptionEntries(detector, detectorMenu);
	}
	
	private void addDeviceMenu(DetectorChannel channel, MenuManager menu) {
		this.addDetectorChannelEntry(channel, menu);
	}
	
	private void addDeviceMenu(Device device, MenuManager menu) {
		MenuManager deviceMenu = new MenuManager(device.getName(), deviceImage, 
				device.getName());
		menu.add(deviceMenu);
		// add device value
		Map<String, String> params = new HashMap<>();
		params.put(AddPauseConditionDefaultHandler.PARAM_ID, device.getID());
		CommandContributionItemParameter p = 
				new CommandContributionItemParameter(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
					"",
					AddPauseConditionDefaultHandler.ID,
					SWT.PUSH);
		p.label = "Value";
		p.icon = deviceImage;
		p.parameters = params;
		CommandContributionItem item = new CommandContributionItem(p);
		item.setVisible(true);
		deviceMenu.add(item);
		this.addOptionEntries(device, deviceMenu);
	}
	
	private void addMotorAxisEntries(Motor motor, MenuManager menu) {
		for (MotorAxis motorAxis : motor.getAxes()) {
			if (motorAxis.getClassName().isEmpty()) {
				this.addMotorAxisEntry(motorAxis, menu);
			}
		}
	}
	
	private void addMotorAxisEntry(MotorAxis motorAxis, MenuManager menu) {
		MenuManager motorAxisMenu = new MenuManager(motorAxis.getName(), 
				axisImage, motorAxis.getName());
		menu.add(motorAxisMenu);
		// add motor axis read back
		Map<String, String> params = new HashMap<>();
		params.put(AddPauseConditionDefaultHandler.PARAM_ID, motorAxis.getID());
		CommandContributionItemParameter p = 
				new CommandContributionItemParameter(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
					"", 
					AddPauseConditionDefaultHandler.ID, 
					SWT.PUSH);
		p.label = "Position";
		p.icon = axisImage;
		p.parameters = params;
		CommandContributionItem item = new CommandContributionItem(p);
		item.setVisible(true);
		motorAxisMenu.add(item);
		this.addOptionEntries(motorAxis, motorAxisMenu);
	}
	
	private void addDetectorChannelEntries(Detector detector, MenuManager menu) {
		for (DetectorChannel channel : detector.getChannels()) {
			if (channel.getClassName().isEmpty()) {
				this.addDetectorChannelEntry(channel, menu);
			}
		}
	}
	
	private void addDetectorChannelEntry(DetectorChannel channel, MenuManager menu) {
		MenuManager channelMenu = new MenuManager(channel.getName(), 
				channelImage, channel.getName());
		menu.add(channelMenu);
		// add channel value
		Map<String, String> params = new HashMap<>();
		params.put(AddPauseConditionDefaultHandler.PARAM_ID, channel.getID());
		CommandContributionItemParameter p = 
				new CommandContributionItemParameter(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
					"",
					AddPauseConditionDefaultHandler.ID,
					SWT.PUSH);
		p.label = "Value";
		p.icon = channelImage;
		p.parameters = params;
		CommandContributionItem item = new CommandContributionItem(p);
		item.setVisible(true);
		channelMenu.add(item);
		this.addOptionEntries(channel, channelMenu);
	}
	
	private void addOptionEntries(AbstractDevice device, MenuManager menu) {
		for (Option option : device.getOptions()) {
			// TODO exclude severity and status options ?
			Map<String, String> params = new HashMap<>();
			params.put(AddPauseConditionDefaultHandler.PARAM_ID, option.getID());
			CommandContributionItemParameter p = 
					new CommandContributionItemParameter(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
						"", 
						AddPauseConditionDefaultHandler.ID, 
						SWT.PUSH);
			p.label = option.getName();
			p.icon = optionImage;
			p.parameters = params;
			
			CommandContributionItem item = new CommandContributionItem(p);
			item.setVisible(true);
			menu.add(item);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDynamic() {
		return true;
	}
}
