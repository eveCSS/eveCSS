package de.ptb.epics.eve.editor.views.axeschannelsview;

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
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.handler.axeschannelsview.AddAbstractDevicesDefaultHandler;
import de.ptb.epics.eve.editor.handler.axeschannelsview.AddChannelDefaultHandler;
import de.ptb.epics.eve.editor.views.axeschannelsview.ui.AxesChannelsView;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ChannelsMenuContribution extends CompoundContributionItem {
	private static Logger logger = Logger.getLogger(
			ChannelsMenuContribution.class.getName());
	
	private final ImageDescriptor classImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("CLASS"));
	private final ImageDescriptor detectorImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("DETECTOR"));
	private final ImageDescriptor channelImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("CHANNEL"));
	private final ImageDescriptor detectorsAndChannelsImage = ImageDescriptor
			.createFromImage(Activator.getDefault().getImageRegistry()
					.get("DETECTORSCHANNELS"));
	
	/**
	 * {@inheritDoc}
	 * * 	<p>
	 * The List is created as follows:
	 * <ul>
	 *  <li>root entry for each class name</li>
	 *  <li>"Detectors & Channels" root entry for motors and axes without class 
	 *  	names</li>
	 * </ul>
	 * Class root entries contain channels of the same class as well as sub 
	 * menus of detectors with the same class name.<br>
	 * The "Detectors & Channels" root menu contains channels without class 
	 * names as well as sub menus of detectors without a class name.<br>
	 * In both cases detector sub menus list channels without class names.<br>
	 * If a detector has only one channel the channel is moved to the level 
	 * of the detector and the detector becomes invisible.<br>
	 * Additionally if there exists more than one (non-menu) entry on a level 
	 * a separator and an "Add All" entry is added.
	 */
	@Override
	protected IContributionItem[] getContributionItems() {
		// create the list that will be returned.
		ArrayList<IContributionItem> result = new ArrayList<>();

		// get active part
		IWorkbenchPart activePart = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getPartService().getActivePart();

		// get current scan module
		ScanModule sm;
		try {
			sm = ((AxesChannelsView) activePart).getCurrentScanModule();
		} catch (ClassCastException e) {
			logger.warn(e.getMessage());
			return result.toArray(new IContributionItem[0]);
		}

		// create filter, that excludes devices used already in scan module
		ExcludeDevicesOfScanModuleFilterManualUpdate measuringStation = 
				new ExcludeDevicesOfScanModuleFilterManualUpdate(
						false, true, false, false, false);
		measuringStation.setSource(Activator.getDefault().getDeviceDefinition());
		measuringStation.setScanModule(sm);
		measuringStation.update();

		// create menu from all remaining devices:

		StringBuilder classDeviceList = new StringBuilder();
		StringBuilder detectorDeviceList = new StringBuilder();
				
		// iterate over all classes
		List<String> classNames = new ArrayList<>();
		classNames.addAll(measuringStation.getClassNameList());
		Collections.sort(classNames);
				
		for (final String className : classNames) {
			final MenuManager currentClassMenu = new MenuManager(className,
					classImage, className);
					
			// (re-)init insert all list (class level)
			classDeviceList = new StringBuilder();
			
			for (final AbstractDevice device : measuringStation
					.getDeviceList(className)) {
				
				if (device instanceof Detector) {
					// (re-)init insert all list (motor level)
					detectorDeviceList = new StringBuilder();
					final Detector detector = (Detector) device;
					final MenuManager currentDetectorMenu = new MenuManager(
							detector.getName(), detectorImage,
							detector.getName());
					currentClassMenu.add(currentDetectorMenu);
					
					// iterate for each channel of the detector
					for (final DetectorChannel channel : detector.getChannels()) {
						if (channel.getClassName().isEmpty()) {
							// add only channels which have no className
							currentDetectorMenu.add(this.getItem(
								AddChannelDefaultHandler.COMMAND_ID, 
								AddChannelDefaultHandler.PARAM_CHANNEL_ID, 
								channel.getID(), 
								channel.getName(), 
								channelImage));
							// add the id to device list (insert all menu entry)
							detectorDeviceList.append(channel.getID() + "!");
						}
					}
					// if detector has no channels the (invisible) menu entry 
					// has to be removed
					if (detector.getChannels().isEmpty()) {
						currentClassMenu.remove(currentDetectorMenu);
					}
					// if only one channel in DetectorMenu, switch channel from
					// DetectorMenu into ClassMenu
					if (currentDetectorMenu.getSize() == 1) {
						currentClassMenu.add(currentDetectorMenu.getItems()[0]);
						currentDetectorMenu.removeAll();
						currentClassMenu.remove(currentDetectorMenu);
						// adjust insert all list
						classDeviceList.append(detectorDeviceList.toString());
					} else if (currentDetectorMenu.getSize() > 1) {
						// insert all menu entry
						currentDetectorMenu.add(new Separator());
						currentDetectorMenu.add(this.getItem(
								AddAbstractDevicesDefaultHandler.COMMAND_ID,
								AddAbstractDevicesDefaultHandler.PARAM_DEVICE_LIST,
							detectorDeviceList.toString(), "Add All", null));
					}
				} else if (device instanceof DetectorChannel) {
					DetectorChannel channel = (DetectorChannel) device;
					currentClassMenu.add(this.getItem(
							AddChannelDefaultHandler.COMMAND_ID, 
							AddChannelDefaultHandler.PARAM_CHANNEL_ID, 
						channel.getID(),
						channel.getName(),
						channelImage));
					classDeviceList.append(channel.getID() + "!");
				}
			}
			// count number of channel elements in current class menu
			int count = 0;
			for (IContributionItem item : currentClassMenu.getItems()) {
				if (item instanceof CommandContributionItem) {
					count++;
				}
			}
			// only add "Add All" button if more than one channel is present
			if (count > 1) {
				// insert all menu entry
				currentClassMenu.add(new Separator());
				currentClassMenu.add(this.getItem(
						AddAbstractDevicesDefaultHandler.COMMAND_ID,
						AddAbstractDevicesDefaultHandler.PARAM_DEVICE_LIST,
					classDeviceList.toString(), "Add All", null));
			}
			
			result.add(currentClassMenu);
		}

		// detectors and channels WITHOUT class
		final MenuManager detectorsAndChannelsMenu = new MenuManager(
				"Detectors && Channels", detectorsAndChannelsImage,
				"detectorsAndChannels");

		StringBuilder noClassDetectorDeviceList = new StringBuilder();
		StringBuilder noClassChannelDeviceList = new StringBuilder();
		
		for (final Detector detector : measuringStation.getDetectors()) {
			noClassDetectorDeviceList = new StringBuilder();
			if (detector.getClassName().isEmpty()
					|| detector.getClassName() == null) {
				final MenuManager currentDetectorMenu = new MenuManager(
						detector.getName(), detectorImage, detector.getName());
				detectorsAndChannelsMenu.add(currentDetectorMenu);
				for (final DetectorChannel channel : detector.getChannels()) {
					if (channel.getClassName().isEmpty()
							|| channel.getClassName() == null) {
						currentDetectorMenu.add(this.getItem(
							AddChannelDefaultHandler.COMMAND_ID, 
							AddChannelDefaultHandler.PARAM_CHANNEL_ID, 
							channel.getID(),
							channel.getName(), 
							channelImage));
						// add the id to device list (insert all menu entry)
						noClassDetectorDeviceList.append(channel.getID() + "!");
					}
				}
				// if a detector has no channels the (invisible) entry has to 
				// be removed
				boolean isEmpty = true;
				for (DetectorChannel ch : detector.getChannels()) {
					if (ch.getClassName() == null || ch.getClassName().isEmpty()) {
						isEmpty = false;
					}
				}
				if (isEmpty) {
					detectorsAndChannelsMenu.remove(currentDetectorMenu);
				}
				// if only one channel in DetectorMenu, switch channel from
				// DetectorMenu into ClassMenu
				if (currentDetectorMenu.getSize() == 1) {
					detectorsAndChannelsMenu.add(currentDetectorMenu.getItems()[0]);
					currentDetectorMenu.removeAll();
					detectorsAndChannelsMenu.remove(currentDetectorMenu);
					// adjust insert all list
					noClassChannelDeviceList.append(noClassDetectorDeviceList
							.toString());
				} else if (currentDetectorMenu.getSize() > 1) {
					// insert all menu entry
					currentDetectorMenu.add(new Separator());
					currentDetectorMenu.add(this.getItem(
							AddAbstractDevicesDefaultHandler.COMMAND_ID,
							AddAbstractDevicesDefaultHandler.PARAM_DEVICE_LIST,
							noClassDetectorDeviceList.toString(), "Add All", null));
				}
			}
		}
				
		// count number of channel items in Detectors & Channels menu
		int count = 0;
		for (IContributionItem item : detectorsAndChannelsMenu.getItems()) {
			if (item instanceof CommandContributionItem) {
				count++;
			}
		}
		// only add "Add All" button if more than one channel is present
		if (count > 1) {
			detectorsAndChannelsMenu.add(new Separator());
			detectorsAndChannelsMenu.add(this.getItem(
					AddAbstractDevicesDefaultHandler.COMMAND_ID,
					AddAbstractDevicesDefaultHandler.PARAM_DEVICE_LIST,
					noClassChannelDeviceList.toString(), "Add All", null));
		}
		
		result.add(detectorsAndChannelsMenu);
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
	
	/*
	 * 
	 */
	private CommandContributionItem getItem(String commandId, String paramId,
			String paramValue, String label, ImageDescriptor icon) {
		
		Map<String, String> params = new HashMap<>();
		params.put(paramId, paramValue);
		CommandContributionItemParameter p = new CommandContributionItemParameter(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(), "",
				commandId,
				SWT.PUSH);
		p.label = label;
		p.icon = icon;
		p.parameters = params;

		CommandContributionItem item = new CommandContributionItem(p);
		item.setVisible(true);
		return item;
	}
}
