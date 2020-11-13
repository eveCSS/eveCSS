package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;
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
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PostscanMenuContribution extends CompoundContributionItem {
	private static final String OPTION_NAME_STATUS = "Status";
	private static final String OPTION_NAME_SEVERITY = "Severity";
	private static final String COMMAND_ADDPOSTSCAN = 
			"de.ptb.epics.eve.editor.command.scanmodule.addpostscan";
	private static final String COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID = 
			"de.ptb.epics.eve.editor.command.scanmodule.addpostscan.postscanid";

	private static final Logger LOGGER = Logger.getLogger(
			PostscanMenuContribution.class.getName());
	
	private final ImageDescriptor classImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("CLASS"));
	private final ImageDescriptor motorImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("MOTOR"));
	private final ImageDescriptor axisImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("AXIS"));
	private final ImageDescriptor detectorImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("DETECTOR"));
	private final ImageDescriptor channelImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("CHANNEL"));
	private final ImageDescriptor deviceImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("DEVICE"));
	private final ImageDescriptor optionImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("OPTION"));
	private final ImageDescriptor motorsAxesImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("MOTORSAXES"));
	private final ImageDescriptor detectorsAndChannelsImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("DETECTORSCHANNELS"));
	private final ImageDescriptor devicesImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("DEVICES"));

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> result = new ArrayList<>();
		
		IWorkbenchPart activePart = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getPartService().getActivePart();

		if (!(activePart instanceof AbstractScanModuleView)) {
			LOGGER.error("active part is not a scan module view");
			return result.toArray(new IContributionItem[0]);
		}
		
		ScanModule sm = ((AbstractScanModuleView)activePart).getScanModule();
		
		// create filter, that excludes devices used already in scan module
		ExcludeDevicesOfScanModuleFilterManualUpdate measuringStation = 
				new ExcludeDevicesOfScanModuleFilterManualUpdate(
						false, false, true, true, false);
		measuringStation.setSource(Activator.getDefault().getMeasuringStation());
		measuringStation.setSource(sm.getChain().getScanDescription().
				getMeasuringStation());
		measuringStation.setScanModule(sm);
		measuringStation.update();
		
		// *************************************************
		// **** Menu Entries for Devices with Class Names **
		// *************************************************
		
		// iterate over all classes of devices
		List<String> classNames = new ArrayList<>();
		classNames.addAll(measuringStation.getClassNameList());
		Collections.sort(classNames);
		
		for(final String className : classNames) {
			// create a menu entry for each class name
			final MenuManager currentClassMenu = new MenuManager(
					className, classImage, className);
			
			// iterate over each device in that class
			for(final AbstractDevice device : 
				measuringStation.getDeviceList(className)) {
				
				// *********************************
				// *********** Motor Start *********
				// *********************************
				
				if(device instanceof Motor) {
					// device is a motor
					final Motor motor = (Motor)device;
					// create a menu entry for that motor and label it
					final MenuManager currentMotorMenu = 
							new MenuManager(motor.getName(), 
									motorImage, 
									motor.getName());
					currentClassMenu.add(currentMotorMenu);
					
					// iterate over the axes of that motor
					for(final MotorAxis motorAxis : motor.getAxes()) {
						if (motorAxis.getClassName().isEmpty()) {
							// add only axis which have no className
							// create a menu entry for the motor axis
							final MenuManager currentMotorAxisMenu = 
									new MenuManager(motorAxis.getName(), 
											axisImage, 
											motorAxis.getName());
							
							// iterate over the options of that axis
							for(final Option option : motorAxis.getOptions()) {
								// option Severity and Status not inserted in the menu
								if (option.getName().equals(OPTION_NAME_SEVERITY) ||
									option.getName().equals(OPTION_NAME_STATUS)) {
										continue;
								}
								Map<String,String> params = new HashMap<>();
								params.put(COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID, 
										option.getID());
								CommandContributionItemParameter p = 
									new CommandContributionItemParameter(
										PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
										"", 
										COMMAND_ADDPOSTSCAN, 
										SWT.PUSH);
								p.label = option.getName();
								p.icon = optionImage;
								p.parameters = params;
								
								CommandContributionItem item = 
									new CommandContributionItem(p);
								item.setVisible(true);
								currentMotorAxisMenu.add(item);
							}
							// add the motor axis menu to the motor menu entry
							currentMotorMenu.add(currentMotorAxisMenu);
						}
					}
					for(final Option option : motor.getOptions()) {
						// option Severity and Status not inserted in the menu
						if (option.getName().equals(OPTION_NAME_SEVERITY) ||
							option.getName().equals(OPTION_NAME_STATUS)) {
								continue;
						}
						Map<String,String> params = new HashMap<>();
						params.put(COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID, 
								option.getID());
						CommandContributionItemParameter p = 
							new CommandContributionItemParameter(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
								"", 
								COMMAND_ADDPOSTSCAN, 
								SWT.PUSH);
						p.label = option.getName();
						p.icon = optionImage;
						p.parameters = params;
						
						CommandContributionItem item = 
							new CommandContributionItem(p);
						item.setVisible(true);
						currentMotorMenu.add(item);
					}
					// *********************************
					// *********** Motor End ***********
					// *********************************
					
					// *********************************
					// ******** Motor Axis Start *******
					// *********************************
					
				} else if(device instanceof MotorAxis) {
					final MotorAxis motorAxis = (MotorAxis)device;
					
					// create menu entry for the motor axis
					final MenuManager currentMotorAxisMenu = 
							new MenuManager(motorAxis.getName(), 
									axisImage, 
									motorAxis.getName());
					
					// iterate over options of the axis
					for(final Option option : motorAxis.getOptions()) {
						// option Severity and Status not inserted in the menu
						if (option.getName().equals(OPTION_NAME_SEVERITY) ||
							option.getName().equals(OPTION_NAME_STATUS)) {
								continue;
						}
						Map<String,String> params = new HashMap<>();
						params.put(COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID, 
								option.getID());
						CommandContributionItemParameter p = 
							new CommandContributionItemParameter(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
								"", 
								COMMAND_ADDPOSTSCAN, 
								SWT.PUSH);
						p.label = option.getName();
						p.icon = optionImage;
						p.parameters = params;
						
						CommandContributionItem item = 
							new CommandContributionItem(p);
						item.setVisible(true);
						currentMotorAxisMenu.add(item);
					}
					// add motor axis menu to the motor menu entry
					currentClassMenu.add(currentMotorAxisMenu);
					
					// *********************************
					// ********* Motor Axis End ********
					// *********************************
					
					// *********************************
					// ********* Detector Start ********
					// *********************************
					
				} else if(device instanceof Detector) {
					
					final Detector detector = (Detector)device;
					
					final MenuManager currentDetectorMenu = 
							new MenuManager(detector.getName(), 
									detectorImage, 
									detector.getName());
					for(final DetectorChannel detectorChannel : detector.getChannels()) {
						if (detectorChannel.getClassName().isEmpty()) {
							// add only channels which have no className
							final MenuManager currentDetectorChannelMenu = 
									new MenuManager(detectorChannel.getName(), 
											channelImage, 
											detectorChannel.getName());
							for(final Option option : detectorChannel.getOptions()) {
								// option Severity and Status not inserted in the menu
								if (option.getName().equals(OPTION_NAME_SEVERITY) ||
									option.getName().equals(OPTION_NAME_STATUS)) {
										continue;
								}
								Map<String,String> params = new HashMap<>();
								params.put(COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID, 
										option.getID());
								CommandContributionItemParameter p = 
									new CommandContributionItemParameter(
										PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
										"", 
										COMMAND_ADDPOSTSCAN, 
										SWT.PUSH);
								p.label = option.getName();
								p.icon = optionImage;
								p.parameters = params;
								
								CommandContributionItem item = 
									new CommandContributionItem(p);
								item.setVisible(true);
								currentDetectorChannelMenu.add(item);
							}
							currentDetectorMenu.add(currentDetectorChannelMenu);
						}
					}
					for(final Option option : detector.getOptions()) {
						// option Severity and Status not inserted in the menu
						if (option.getName().equals(OPTION_NAME_SEVERITY) ||
							option.getName().equals(OPTION_NAME_STATUS)) {
								continue;
						}
						Map<String,String> params = new HashMap<>();
						params.put(COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID, 
								option.getID());
						CommandContributionItemParameter p = 
							new CommandContributionItemParameter(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
								"", 
								COMMAND_ADDPOSTSCAN, 
								SWT.PUSH);
						p.label = option.getName();
						p.icon = optionImage;
						p.parameters = params;
						
						CommandContributionItem item = 
							new CommandContributionItem(p);
						item.setVisible(true);
						currentDetectorMenu.add(item);
					}
					currentClassMenu.add(currentDetectorMenu);
					
					// *********************************
					// ********** Detector End *********
					// *********************************
					
					// *********************************
					// ***** Detector Channel Start ****
					// *********************************
					
				} else if(device instanceof DetectorChannel) {
					final DetectorChannel detectorChannel = 
							(DetectorChannel)device;
					final MenuManager currentDetectorChannelMenu = 
							new MenuManager(detectorChannel.getName(), 
									channelImage, 
									detectorChannel.getName());
					
					for(final Option option : detectorChannel.getOptions()) {
						// option Severity and Status not inserted in the menu
						if (option.getName().equals(OPTION_NAME_SEVERITY) ||
							option.getName().equals(OPTION_NAME_STATUS)) {
								continue;
						}
						Map<String,String> params = new HashMap<>();
						params.put(COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID, 
								option.getID());
						CommandContributionItemParameter p = 
							new CommandContributionItemParameter(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
								"", 
								COMMAND_ADDPOSTSCAN, 
								SWT.PUSH);
						p.label = option.getName();
						p.icon = optionImage;
						p.parameters = params;
						
						CommandContributionItem item = 
							new CommandContributionItem(p);
						item.setVisible(true);
						currentDetectorChannelMenu.add(item);
					}
					currentClassMenu.add(currentDetectorChannelMenu);
					
					// *********************************
					// ****** Detector Channel End *****
					// *********************************
					
					// *********************************
					// ********** Device Start *********
					// *********************************
					
				} else if(device instanceof Device) {
					
					Map<String,String> params = new HashMap<>();
					params.put(COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID, 
							device.getID());
					CommandContributionItemParameter p = 
						new CommandContributionItemParameter(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
							"", 
							COMMAND_ADDPOSTSCAN, 
							SWT.PUSH);
					p.label = device.getName();
					p.icon = deviceImage;
					p.parameters = params;
					
					CommandContributionItem item = 
						new CommandContributionItem(p);
					item.setVisible(true);
					currentClassMenu.add(item);
					
					// *********************************
					// ************ Device End *********
					// *********************************
				}
			}
			result.add(currentClassMenu);
		} // end of: iterate over all classes of devices
		
		// *****************************************************
		// * end of: Menu Entries for Devices with Class Names *
		// *****************************************************
		
		// *************************************************
		// ** Menu Entries for Devices without Class Names *
		// *************************************************
		
		final MenuManager motorsAndAxesMenu = new MenuManager("Motors && Axes",
				motorsAxesImage, "motorsAndAxes");
		
		for(final Motor motor : measuringStation.getMotors()) {
			// add only entries for motors without class names
			if(motor.getClassName() == null || 
					motor.getClassName().isEmpty()) {
				
				final MenuManager currentMotorMenu = 
						new MenuManager(motor.getName(), 
								motorImage, 
								motor.getName());
				
				for(final MotorAxis motorAxis : motor.getAxes()) {
					if(motorAxis.getClassName() == null ||
							motorAxis.getClassName().isEmpty()) {
						
						final MenuManager currentMotorAxisMenu = 
								new MenuManager(motorAxis.getName(), 
										axisImage, 
										motorAxis.getName());
						
						for(final Option option : motorAxis.getOptions()) {
							Map<String,String> params = new HashMap<>();
							params.put(COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID, 
									option.getID());
							CommandContributionItemParameter p = 
								new CommandContributionItemParameter(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
									"", 
									COMMAND_ADDPOSTSCAN, 
									SWT.PUSH);
							p.label = option.getName();
							p.icon = optionImage;
							p.parameters = params;
							
							CommandContributionItem item = 
								new CommandContributionItem(p);
							item.setVisible(true);
							currentMotorAxisMenu.add(item);
						}
						currentMotorMenu.add(currentMotorAxisMenu);
					}
				}
				for(final Option option : motor.getOptions()) {
					Map<String,String> params = new HashMap<>();
					params.put(COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID, 
							option.getID());
					CommandContributionItemParameter p = 
						new CommandContributionItemParameter(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
							"", 
							COMMAND_ADDPOSTSCAN, 
							SWT.PUSH);
					p.label = option.getName();
					p.icon = optionImage;
					p.parameters = params;
					
					CommandContributionItem item = 
						new CommandContributionItem(p);
					item.setVisible(true);
					currentMotorMenu.add(item);
				}
				motorsAndAxesMenu.add(currentMotorMenu);
			}
		}
		result.add(motorsAndAxesMenu);
		
		final MenuManager detectorsAndChannelsMenu = new MenuManager(
				"Detectors && Channels", detectorsAndChannelsImage,
				"detectorsAndChannels");
		
		for(final Detector detector : measuringStation.getDetectors()) {
			// add only menu entries for detectors without class names
			if(detector.getClassName() == null || 
					detector.getClassName().isEmpty()) {
				final MenuManager currentDetectorMenu = 
						new MenuManager(detector.getName(), 
								detectorImage, 
								detector.getName());
				for(final DetectorChannel detectorChannel : 
					detector.getChannels()) {
					if(detectorChannel.getClassName() == null ||
							detectorChannel.getClassName().isEmpty()) {
						final MenuManager currentDetectorChannelMenu = 
								new MenuManager(detectorChannel.getName(), 
										channelImage, 
										detector.getName());
						for(final Option option : detectorChannel.getOptions()) {
							Map<String,String> params = new HashMap<>();
							params.put(COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID, 
									option.getID());
							CommandContributionItemParameter p = 
								new CommandContributionItemParameter(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
									"", 
									COMMAND_ADDPOSTSCAN, 
									SWT.PUSH);
							p.label = option.getName();
							p.icon = optionImage;
							p.parameters = params;
							
							CommandContributionItem item = 
								new CommandContributionItem(p);
							item.setVisible(true);
							currentDetectorChannelMenu.add(item);
						}
						currentDetectorMenu.add(currentDetectorChannelMenu);
					}
				}
				for(final Option option : detector.getOptions()) {
					Map<String,String> params = new HashMap<>();
					params.put(COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID, 
							option.getID());
					CommandContributionItemParameter p = 
						new CommandContributionItemParameter(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
							"", 
							COMMAND_ADDPOSTSCAN, 
							SWT.PUSH);
					p.label = option.getName();
					p.icon = optionImage;
					p.parameters = params;
					
					CommandContributionItem item = 
						new CommandContributionItem(p);
					item.setVisible(true);
					currentDetectorMenu.add(item);
				}
				detectorsAndChannelsMenu.add(currentDetectorMenu);
			}
		}
		result.add(detectorsAndChannelsMenu);
		
		final MenuManager devicesMenu = new MenuManager(
				"Devices", devicesImage, "devices");
		
		for(final Device device : measuringStation.getDevices()) {
			// add only entries for devices without class names
			if(device.getClassName() == null || 
					device.getClassName().isEmpty()) {
				Map<String,String> params = new HashMap<>();
				params.put(COMMAND_ADDPOSTSCAN_PARAM_POSTSCANID, 
						device.getID());
				CommandContributionItemParameter p = 
					new CommandContributionItemParameter(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
						"", 
						COMMAND_ADDPOSTSCAN, 
						SWT.PUSH);
				p.label = device.getName();
				p.icon = deviceImage;
				p.parameters = params;
				
				CommandContributionItem item = 
					new CommandContributionItem(p);
				item.setVisible(true);
				devicesMenu.add(item);
			}
		}
		result.add(devicesMenu);
		
		// ********************************************************
		// * end of: Menu Entries for Devices without Class Names *
		// ********************************************************
		
		measuringStation.setScanModule(null);
		measuringStation.setSource(null);
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
