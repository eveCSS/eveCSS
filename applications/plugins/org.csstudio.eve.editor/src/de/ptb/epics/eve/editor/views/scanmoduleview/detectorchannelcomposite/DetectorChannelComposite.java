package de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * <code>DetectorChannelComposite</code>. is part of the
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class DetectorChannelComposite extends Composite {

	// logging
	private static Logger logger = Logger.getLogger(
			DetectorChannelComposite.class);

	private TableViewer tableViewer;
	private ScanModule scanModule;
	private MenuManager menuManager;
	ExcludeDevicesOfScanModuleFilterManualUpdate measuringStation;
	private ScanModuleView parentView;
	
	/**
	 * Constructs a <code>DetectorChannelComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param measuringStation the measuring station (containing available 
	 * 		  detector channels)
	 */
	public DetectorChannelComposite(final ScanModuleView parentView, 
									final Composite parent, final int style) {
		super(parent, style);
		this.parentView = parentView;
		this.measuringStation = new ExcludeDevicesOfScanModuleFilterManualUpdate(
				false, true, false, false, false);
		this.measuringStation.setSource(Activator.getDefault().
				getMeasuringStation());

		this.setLayout(new GridLayout());
		
		createViewer();
		
		this.tableViewer.getTable().addFocusListener(
				new TableViewerFocusListener());
	}

	/*
	 * 
	 */
	private void createViewer() {
		this.tableViewer = new TableViewer(this, SWT.NONE);
		GridData gridData = new GridData();
		gridData.minimumHeight = 120;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.tableViewer.getTable().setLayoutData(gridData);
		createColumns();
		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);
		this.tableViewer.setContentProvider(new DetectorChannelContentProvider());
		this.tableViewer.setLabelProvider(new DetectorChannelLabelProvider());
		
		
		this.menuManager = new MenuManager("#PopupMenu");
		this.menuManager.setRemoveAllWhenShown(true);
		this.menuManager.addMenuListener(new MenuManagerMenuListener());
		final Menu contextMenu = 
			menuManager.createContextMenu(this.tableViewer.getTable());
		this.tableViewer.getControl().setMenu(contextMenu);
		
		
		/*
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));
		// register menu
		parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.scanmoduleview.detectorchannelcomposite.popup", 
			menuManager, this.tableViewer);
			*/
	}
	
	/*
	 * 
	 */
	private void createColumns() {
		TableViewerColumn channelColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		channelColumn.getColumn().setText("Detector Channel");
		channelColumn.getColumn().setWidth(250);
		
		TableViewerColumn avgColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		avgColumn.getColumn().setText("Average");
		avgColumn.getColumn().setWidth(80);
	}
	
	/**
	 * Returns the currently set 
	 * {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * 
	 * @return the currently set 
	 * 		   {@link de.ptb.epics.eve.data.scandescription.ScanModule}
	 */
	public ScanModule getScanModule() {
		return this.scanModule;
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
	 * 
	 * @param scanModule the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.ScanModule} that 
	 * 		  should be set
	 */
	public void setScanModule(final ScanModule scanModule) {
		
		logger.debug("setScanModule");
		
		this.scanModule = scanModule;
		this.tableViewer.setInput(scanModule);
		this.measuringStation.setScanModule(scanModule);
		
		if(scanModule == null) {
			return;
		}
		
		// if there are detector channels present... 
		if(tableViewer.getTable().getItems().length > 0) {
			// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0) {
				// ... select the first one and set the detector channel view
				tableViewer.getTable().select(0);
			}
		}
		this.parentView.setSelectionProvider(this.tableViewer);
	}

	// ************************************************************************
	// **************************** Listeners *********************************
	// ************************************************************************

	/**
	 * 
	 */
	private class TableViewerFocusListener implements FocusListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {
			logger.debug("focus gained");
			parentView.setSelectionProvider(tableViewer);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
		}
	}
	
	/**
	 * 
	 */
	private class MenuManagerMenuListener implements IMenuListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void menuAboutToShow(IMenuManager manager) {
			
			final ImageDescriptor classImage = ImageDescriptor.createFromImage(
					Activator.getDefault().
					getImageRegistry().get("CLASS"));
			final ImageDescriptor detectorImage = ImageDescriptor.createFromImage(
					Activator.getDefault().
					getImageRegistry().get("DETECTOR"));
			final ImageDescriptor channelImage = ImageDescriptor.createFromImage(
					Activator.getDefault().
					getImageRegistry().get("CHANNEL"));

			((ExcludeDevicesOfScanModuleFilterManualUpdate)measuringStation).update();

			for(final String className : measuringStation.getClassNameList()) {
				
				final MenuManager currentClassMenu = new MenuManager(
						className, classImage, className);
				
				for(final AbstractDevice device : 
					measuringStation.getDeviceList(className)) {
				
					if(device instanceof Detector) {
						final Detector detector = (Detector)device;
						final MenuManager currentDetectorMenu = 
							new MenuManager(detector.getName(), detectorImage, detector.getName());
						currentClassMenu.add(currentDetectorMenu);

						// iterate for each channel of the detector
						for(final DetectorChannel channel : detector.getChannels()) {
							if (channel.getClassName().isEmpty()) {
								// add only channels which have no className
								final Action setChannelAction = new SetChannelAction(channel);
								setChannelAction.setImageDescriptor(channelImage);
								currentDetectorMenu.add(setChannelAction);
							}
						}
						// if only one channel in DetectorMenu, switch channel from DetectorMenu into ClassMenu
						if (currentDetectorMenu.getSize() == 1) {
							currentDetectorMenu.removeAll();
							// Eintrag muß zur Class hinzugefügt werden.
							for(final DetectorChannel channel : detector.getChannels()) {
								if (channel.getClassName().isEmpty()) {
									// add only channels which have no className
									final Action setChannelAction = new SetChannelAction(channel);
									setChannelAction.setImageDescriptor(channelImage);
									currentClassMenu.add(setChannelAction);
								}
							}
						}					
					} else if(device instanceof DetectorChannel) {
						final Action setChannelAction = new SetChannelAction((DetectorChannel)device);
						setChannelAction.setImageDescriptor(channelImage);
						currentClassMenu.add(setChannelAction);
					}
					manager.add(currentClassMenu);
				}
			}
				
			for(final Detector detector : measuringStation.getDetectors()) {
				if("".equals(detector.getClassName()) || detector.getClassName() == null) {
					final MenuManager currentDetectorMenu = 
							new MenuManager(detector.getName(), detectorImage, detector.getName());
					for(final DetectorChannel channel : detector.getChannels()) {
						if("".equals(channel.getClassName()) || channel.getClassName() == null) {
							final Action setChannelAction = 
								new SetChannelAction(channel);
							setChannelAction.setImageDescriptor(channelImage);
							currentDetectorMenu.add( setChannelAction );
						}
					}
					// if only one channel in DetectorMenu, switch channel from DetectorMenu into ClassMenu
					if (currentDetectorMenu.getSize() == 1) {
						currentDetectorMenu.removeAll();
						// Eintrag muß zur Class hinzugefügt werden.
						for(final DetectorChannel channel : detector.getChannels()) {
							if (channel.getClassName().isEmpty()) {
								// add only channels which have no className
								final Action setChannelAction = new SetChannelAction(channel);
								setChannelAction.setImageDescriptor(channelImage);
								manager.add(setChannelAction);
							}
						}
					}					
					manager.add(currentDetectorMenu);
				}
			}

			if (scanModule.getChannels().length > 0) {
				Action deleteAction = new DeleteAction();
				deleteAction.setEnabled(true);
				deleteAction.setText("Delete Channel");
				deleteAction.setToolTipText("Deletes Channel");
				deleteAction.setImageDescriptor(PlatformUI.getWorkbench().
				   								getSharedImages().
				   								getImageDescriptor(
				   								ISharedImages.IMG_TOOL_DELETE));
				manager.add(deleteAction);	
			}
			
		}
	}
	
	// ****************************** Actions *********************************
	
	/**
	 * 
	 */
	private class SetChannelAction extends Action {
		
		final DetectorChannel ch;
		
		SetChannelAction(DetectorChannel ch)
		{
			this.ch = ch;
			this.setText("".equals(ch.getName())
						 ? ch.getID()
						 : ch.getName());
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			super.run();
			for(final Channel c : scanModule.getChannels()) {
				if(c.getAbstractDevice() == ch) {
					return;
				}
			}
			Channel c = new Channel(scanModule);
			c.setDetectorChannel(ch);
			scanModule.add(c);
			
			// the new channel (the last itemCount) will be selected in the table and 
			// displayed in the detectorChannelView
			tableViewer.getTable().select(tableViewer.getTable().getItemCount()-1);
			tableViewer.getControl().setFocus();

			tableViewer.refresh();
		}
	}
	
	/**
	 * 
	 */
	private class DeleteAction extends Action {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
    		
    		scanModule.remove((Channel)((IStructuredSelection)
    				tableViewer.getSelection()).getFirstElement());
    		
			// if another channel is available, select the first channel
			if(tableViewer.getTable().getItems().length != 0) {
				tableViewer.getTable().select(0);
			} 
			tableViewer.getControl().setFocus();

			tableViewer.refresh();
		}
	}
}