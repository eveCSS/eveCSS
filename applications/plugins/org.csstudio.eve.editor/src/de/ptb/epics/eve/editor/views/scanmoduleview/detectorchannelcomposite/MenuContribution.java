package de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite;

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
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * Menu entries of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite.DetectorChannelComposite}'s 
 * context menu
 * 
 * @author Marcus Michalsky
 * @since 1.2
 */
public class MenuContribution extends CompoundContributionItem {

	final ImageDescriptor classImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("CLASS"));
	final ImageDescriptor detectorImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("DETECTOR"));
	final ImageDescriptor channelImage = ImageDescriptor.createFromImage(
			Activator.getDefault().
			getImageRegistry().get("CHANNEL"));

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
						false, true, false, false, false);
		measuringStation.setSource(Activator.getDefault().getMeasuringStation());
		measuringStation.setSource(sm.getChain().getScanDescription().
				getMeasuringStation());
		measuringStation.setScanModule(sm);
		measuringStation.update();
		
		// create menu from all remaining devices:
		
		// iterate over all classes
		List<String> classNames = new ArrayList<String>();
		classNames.addAll(measuringStation.getClassNameList());
		Collections.sort(classNames);
		
		for(final String className : classNames) {
			final MenuManager currentClassMenu = new MenuManager(
					className, classImage, className);
			
			for(final AbstractDevice device : 
				measuringStation.getDeviceList(className)) {
			
				if(device instanceof Detector) {
					final Detector detector = (Detector)device;
					final MenuManager currentDetectorMenu = 
						new MenuManager(detector.getName(), detectorImage, 
								detector.getName());
					currentClassMenu.add(currentDetectorMenu);
					
					// iterate for each channel of the detector
					for(final DetectorChannel channel : detector.getChannels()) {
						if (channel.getClassName().isEmpty()) {
							// add only channels which have no className
							Map<String,String> params = new HashMap<String,String>();
							params.put("de.ptb.epics.eve.editor.command.addchannel.detectorchannelid", 
									channel.getID());
							CommandContributionItemParameter p = 
								new CommandContributionItemParameter(
									PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
									"", 
									"de.ptb.epics.eve.editor.command.addchannel", 
									SWT.PUSH);
							p.label = channel.getName();
							p.icon = channelImage;
							p.parameters = params;
							
							CommandContributionItem item = 
								new CommandContributionItem(p);
							item.setVisible(true);
							currentDetectorMenu.add(item);
						}
					}
					// if only one channel in DetectorMenu, switch channel from 
					// DetectorMenu into ClassMenu
					if (currentDetectorMenu.getSize() == 1) {
						currentClassMenu.add(currentDetectorMenu.getItems()[0]);
						currentDetectorMenu.removeAll();
					}
				} else if(device instanceof DetectorChannel) {
					DetectorChannel channel = (DetectorChannel)device;
					Map<String,String> params = new HashMap<String,String>();
					params.put("de.ptb.epics.eve.editor.command.addchannel.detectorchannelid", 
							channel.getID());
					CommandContributionItemParameter p = 
						new CommandContributionItemParameter(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
							"", 
							"de.ptb.epics.eve.editor.command.addchannel", 
							SWT.PUSH);
					p.label = channel.getName();
					p.icon = channelImage;
					p.parameters = params;
					
					CommandContributionItem item = 
						new CommandContributionItem(p);
					item.setVisible(true);
					currentClassMenu.add(item);
				}
				result.add(currentClassMenu);
			}
		}
			
		for(final Detector detector : measuringStation.getDetectors()) {
			if(detector.getClassName().isEmpty() || detector.getClassName() == null) {
				final MenuManager currentDetectorMenu = new MenuManager(
						detector.getName(), detectorImage, detector.getName());
				for(final DetectorChannel channel : detector.getChannels()) {
					if(channel.getClassName().isEmpty() || channel.getClassName() == null) {
						Map<String,String> params = new HashMap<String,String>();
						params.put("de.ptb.epics.eve.editor.command.addchannel.detectorchannelid", 
								channel.getID());
						CommandContributionItemParameter p = 
							new CommandContributionItemParameter(
								PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
								"", 
								"de.ptb.epics.eve.editor.command.addchannel", 
								SWT.PUSH);
						p.label = channel.getName();
						p.icon = channelImage;
						p.parameters = params;
						
						CommandContributionItem item = 
							new CommandContributionItem(p);
						item.setVisible(true);
						currentDetectorMenu.add(item);
					}
				}
				// if only one channel in DetectorMenu, switch channel from 
				// DetectorMenu into ClassMenu
				if (currentDetectorMenu.getSize() == 1) {
					result.add(currentDetectorMenu.getItems()[0]);
					currentDetectorMenu.removeAll();
				}
				result.add(currentDetectorMenu);
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