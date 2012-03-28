package de.ptb.epics.eve.editor.views.scanmoduleview.postscancomposite;

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
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
/**
 * @author Marcus Michalsky
 * @since 1.2
 */
public class MenuContribution extends CompoundContributionItem {

	final ImageDescriptor classImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("CLASS"));
	final ImageDescriptor motorImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("MOTOR"));
	final ImageDescriptor axisImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("AXIS"));
	final ImageDescriptor detectorImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("DETECTOR"));
	final ImageDescriptor channelImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("CHANNEL"));
	final ImageDescriptor deviceImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("DEVICE"));
	final ImageDescriptor optionImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("OPTION"));

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		ArrayList<IContributionItem> result = new ArrayList<IContributionItem>();
		
		ScanModule sm = ((ScanModuleView)Activator.getDefault().getWorkbench().
				getActiveWorkbenchWindow().getPartService().getActivePart()).
				getCurrentScanModule();
		
		// create filter, that excludes devices used already in scan module
		ExcludeDevicesOfScanModuleFilterManualUpdate measuringStation = 
				new ExcludeDevicesOfScanModuleFilterManualUpdate(
						false, false, false, true, false);
		measuringStation.setSource(Activator.getDefault().getMeasuringStation());
		measuringStation.setSource(sm.getChain().getScanDescription().
				getMeasuringStation());
		measuringStation.setScanModule(sm);
		measuringStation.update();
		
		// *************************************************
		// **** Menu Entries for Devices with Class Names **
		// *************************************************
		
		// iterate over all classes of devices
		for(final String className : measuringStation.getClassNameList()) {
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
								if (option.getName().equals("Severity") ||
									option.getName().equals("Status")) {
										continue;
								}
								Map<String,String> params = new HashMap<String,String>();
								params.put("de.ptb.epics.eve.editor.command.addpostscan.postscanid", 
										option.getID());
								CommandContributionItemParameter p = 
									new CommandContributionItemParameter(
										PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
										"", 
										"de.ptb.epics.eve.editor.command.addpostscan", 
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
						if (option.getName().equals("Severity") ||
							option.getName().equals("Status")) {
								continue;
						}
						Map<String,String> params = new HashMap<String,String>();
						params.put("de.ptb.epics.eve.editor.command.addpostscan.postscanid", 
								option.getID());
						CommandContributionItemParameter p = 
							new CommandContributionItemParameter(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
								"", 
								"de.ptb.epics.eve.editor.command.addpostscan", 
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
						if (option.getName().equals("Severity") ||
							option.getName().equals("Status")) {
								continue;
						}
						Map<String,String> params = new HashMap<String,String>();
						params.put("de.ptb.epics.eve.editor.command.addpostscan.postscanid", 
								option.getID());
						CommandContributionItemParameter p = 
							new CommandContributionItemParameter(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
								"", 
								"de.ptb.epics.eve.editor.command.addpostscan", 
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
								if (option.getName().equals("Severity") ||
									option.getName().equals("Status")) {
										continue;
								}
								Map<String,String> params = new HashMap<String,String>();
								params.put("de.ptb.epics.eve.editor.command.addpostscan.postscanid", 
										option.getID());
								CommandContributionItemParameter p = 
									new CommandContributionItemParameter(
										PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
										"", 
										"de.ptb.epics.eve.editor.command.addpostscan", 
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
						if (option.getName().equals("Severity") ||
							option.getName().equals("Status")) {
								continue;
						}
						Map<String,String> params = new HashMap<String,String>();
						params.put("de.ptb.epics.eve.editor.command.addpostscan.postscanid", 
								option.getID());
						CommandContributionItemParameter p = 
							new CommandContributionItemParameter(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
								"", 
								"de.ptb.epics.eve.editor.command.addpostscan", 
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
						if (option.getName().equals("Severity") ||
							option.getName().equals("Status")) {
								continue;
						}
						Map<String,String> params = new HashMap<String,String>();
						params.put("de.ptb.epics.eve.editor.command.addpostscan.postscanid", 
								option.getID());
						CommandContributionItemParameter p = 
							new CommandContributionItemParameter(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
								"", 
								"de.ptb.epics.eve.editor.command.addpostscan", 
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
					
					Map<String,String> params = new HashMap<String,String>();
					params.put("de.ptb.epics.eve.editor.command.addpostscan.postscanid", 
							device.getID());
					CommandContributionItemParameter p = 
						new CommandContributionItemParameter(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
							"", 
							"de.ptb.epics.eve.editor.command.addpostscan", 
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
							Map<String,String> params = new HashMap<String,String>();
							params.put("de.ptb.epics.eve.editor.command.addpostscan.postscanid", 
									option.getID());
							CommandContributionItemParameter p = 
								new CommandContributionItemParameter(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
									"", 
									"de.ptb.epics.eve.editor.command.addpostscan", 
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
					Map<String,String> params = new HashMap<String,String>();
					params.put("de.ptb.epics.eve.editor.command.addpostscan.postscanid", 
							option.getID());
					CommandContributionItemParameter p = 
						new CommandContributionItemParameter(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
							"", 
							"de.ptb.epics.eve.editor.command.addpostscan", 
							SWT.PUSH);
					p.label = option.getName();
					p.icon = optionImage;
					p.parameters = params;
					
					CommandContributionItem item = 
						new CommandContributionItem(p);
					item.setVisible(true);
					currentMotorMenu.add(item);
				}
				result.add(currentMotorMenu);
			}
		}
		
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
							Map<String,String> params = new HashMap<String,String>();
							params.put("de.ptb.epics.eve.editor.command.addpostscan.postscanid", 
									option.getID());
							CommandContributionItemParameter p = 
								new CommandContributionItemParameter(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
									"", 
									"de.ptb.epics.eve.editor.command.addpostscan", 
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
					Map<String,String> params = new HashMap<String,String>();
					params.put("de.ptb.epics.eve.editor.command.addpostscan.postscanid", 
							option.getID());
					CommandContributionItemParameter p = 
						new CommandContributionItemParameter(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
							"", 
							"de.ptb.epics.eve.editor.command.addpostscan", 
							SWT.PUSH);
					p.label = option.getName();
					p.icon = optionImage;
					p.parameters = params;
					
					CommandContributionItem item = 
						new CommandContributionItem(p);
					item.setVisible(true);
					currentDetectorMenu.add(item);
				}
				result.add(currentDetectorMenu);
			}
		}
		
		for(final Device device : measuringStation.getDevices()) {
			// add only entries for devices without class names
			if(device.getClassName() == null || 
					device.getClassName().isEmpty()) {
				Map<String,String> params = new HashMap<String,String>();
				params.put("de.ptb.epics.eve.editor.command.addpostscan.postscanid", 
						device.getID());
				CommandContributionItemParameter p = 
					new CommandContributionItemParameter(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
						"", 
						"de.ptb.epics.eve.editor.command.addpostscan", 
						SWT.PUSH);
				p.label = device.getName();
				p.icon = deviceImage;
				p.parameters = params;
				
				CommandContributionItem item = 
					new CommandContributionItem(p);
				item.setVisible(true);
				result.add(item);
			}
		}
		
		// ********************************************************
		// * end of: Menu Entries for Devices without Class Names *
		// ********************************************************
		
		return result.toArray(new IContributionItem[0]);
	}
}