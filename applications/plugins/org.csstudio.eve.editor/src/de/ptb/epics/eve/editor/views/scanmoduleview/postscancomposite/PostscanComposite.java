package de.ptb.epics.eve.editor.views.scanmoduleview.postscancomposite;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.measuringstation.filter.ExcludeDevicesOfScanModuleFilterManualUpdate;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * <code>PostscanComposite</code> is part of the 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class PostscanComposite extends Composite {

	private TableViewer tableViewer;
	private ScanModule scanModule;
	private ScanModuleView parentView;
	private ExcludeDevicesOfScanModuleFilterManualUpdate measuringStation;
	
	/**
	 * Constructs a <code>PostscanComposite</code>.
	 * 
	 * @param parent the parent
	 * @param style the style
	 * @param measuringStation the measuring station the menu options should be 
	 * 		  taken from
	 */
	public PostscanComposite(final ScanModuleView parentView, 
							final Composite parent, final int style) {
		super(parent, style);
		this.parentView = parentView;
		this.measuringStation = new ExcludeDevicesOfScanModuleFilterManualUpdate(
				false, false, false, true, false);
		this.measuringStation.setSource(Activator.getDefault().
				getMeasuringStation());

		this.setLayout(new GridLayout());
		
		createViewer();
		
		
		// TODO convert to EditingSupport
	    final CellEditor[] editors = new CellEditor[3];
	    
	    final String[] yesNo = {"yes","no"};

	    editors[0] = new TextCellEditor(this.tableViewer.getTable());
	    editors[1] = new TextCellEditor(this.tableViewer.getTable());
	    editors[2] = new ComboBoxCellEditor(
	    		this.tableViewer.getTable(), yesNo, SWT.READ_ONLY);
	    
	    this.tableViewer.setCellModifier(
	    		new CellModifyer(this.tableViewer));
	    this.tableViewer.setCellEditors(editors);
	    
	    final String[] props = {"device", "value", "reset"};
	    
	    this.tableViewer.setColumnProperties(props);
	    
	    // end of to do
	
	    
	    final MenuManager menuManager = new MenuManager("#PopupMenu");
		menuManager.setRemoveAllWhenShown(true);
		menuManager.addMenuListener(new MenuManagerMenuListener());
		
		final Menu contextMenu = 
			menuManager.createContextMenu(this.tableViewer.getControl());
		this.tableViewer.getControl().setMenu(contextMenu);
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
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setLabelProvider(new LabelProvider());
		
		// create context menu // TODO
		/*MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));*/
		// register menu
		/*parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.scanmoduleview.postscancomposite.popup", 
			menuManager, this.tableViewer);*/
	}

	/*
	 * 
	 */
	private void createColumns() {
		TableViewerColumn deviceColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		deviceColumn.getColumn().setText("Device");
		deviceColumn.getColumn().setWidth(300);
		
		TableViewerColumn valueColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		valueColumn.getColumn().setText("Value");
		valueColumn.getColumn().setWidth(100);
		
		TableViewerColumn resetColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		resetColumn.getColumn().setText("Reset Original");
		resetColumn.getColumn().setWidth(100);
		
		TableViewerColumn emptyColumn = new TableViewerColumn(
				this.tableViewer, SWT.LEFT);
		emptyColumn.getColumn().setText("");
		emptyColumn.getColumn().setWidth(10);
	}
	
	/**
	 * Sets the {@link de.ptb.epics.eve.data.scandescription.ScanModule} this 
	 * composite is based on.
	 * 
	 * @param scanModule the 
	 * {@link de.ptb.epics.eve.data.scandescription.ScanModule} this composite 
	 * 		is based on
	 */
	public void setScanModule(final ScanModule scanModule) {
		
		this.scanModule = scanModule;
		this.tableViewer.setInput(scanModule);
		this.measuringStation.setScanModule(scanModule);
		
		// if there are postcans present... 
		if(tableViewer.getTable().getItems().length > 0) {
			// ... and none is selected ...
			if(tableViewer.getTable().getSelectionCount() == 0) {
				// ... select the first one
				tableViewer.getTable().select(0);
			}
		}
	}
	
	// ***********************************************************************
	// ****************************** Listener *******************************
	// ***********************************************************************
	
	/**
	 * <code>MenuManagerMenuListener</code>.
	 */
	private class MenuManagerMenuListener implements IMenuListener {
		
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
		public void menuAboutToShow(IMenuManager manager) {
			
			((ExcludeDevicesOfScanModuleFilterManualUpdate)measuringStation).update();
			
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
									if ((!option.getName().equals("Severity")) &&
										(!option.getName().equals("Status"))) {

										SetOptionAction setOptionAction = 
											new SetOptionAction(option, option.getName());
										setOptionAction.setImageDescriptor(optionImage);
										currentMotorAxisMenu.add(setOptionAction);
									}
								}
								// add the motor axis menu to the motor menu entry
								currentMotorMenu.add(currentMotorAxisMenu);
							}
						}
						for(final Option option : motor.getOptions()) {
							// option Severity and Status not inserted in the menu
							if ((!option.getName().equals("Severity")) &&
								(!option.getName().equals("Status"))) {
							
								SetOptionAction setOptionAction = 
										new SetOptionAction(option, option.getName());
								setOptionAction.setImageDescriptor(optionImage);
								currentMotorMenu.add(setOptionAction);
							}
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
							if ((!option.getName().equals("Severity")) &&
								(!option.getName().equals("Status"))) {
						
								SetOptionAction setOptionAction = 
										new SetOptionAction(option, option.getName());
								setOptionAction.setImageDescriptor(optionImage);
								currentMotorAxisMenu.add(setOptionAction);
							}
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
									if ((!option.getName().equals("Severity")) &&
										(!option.getName().equals("Status"))) {

										SetOptionAction setOptionAction = 
											new SetOptionAction(option, option.getName());
										setOptionAction.setImageDescriptor(optionImage);
										currentDetectorChannelMenu.add(setOptionAction);
									}
								}
								currentDetectorMenu.add(currentDetectorChannelMenu);
							}
						}
						for(final Option option : detector.getOptions()) {
							// option Severity and Status not inserted in the menu
							if ((!option.getName().equals("Severity")) &&
								(!option.getName().equals("Status"))) {
							
								SetOptionAction setOptionAction = 
										new SetOptionAction(option, option.getName());
								setOptionAction.setImageDescriptor(optionImage);
								currentDetectorMenu.add(setOptionAction);
							}
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
							if ((!option.getName().equals("Severity")) &&
								(!option.getName().equals("Status"))) {
							
								SetOptionAction setOptionAction = 
										new SetOptionAction(option, option.getName());
								setOptionAction.setImageDescriptor(optionImage);
								currentDetectorChannelMenu.add(setOptionAction);
							}
						}
						currentClassMenu.add(currentDetectorChannelMenu);
						
						// *********************************
						// ****** Detector Channel End *****
						// *********************************
						
						// *********************************
						// ********** Device Start *********
						// *********************************
						
					} else if(device instanceof Device) {
						
						SetDeviceAction setDeviceAction = 
								new SetDeviceAction(
										(Device)device, device.getName());
						setDeviceAction.setImageDescriptor(deviceImage);
						currentClassMenu.add(setDeviceAction);
						
						// *********************************
						// ************ Device End *********
						// *********************************
					}
				}
				manager.add(currentClassMenu);
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
								
								SetOptionAction setOptionAction = 
									new SetOptionAction(option, option.getName());
								setOptionAction.setImageDescriptor(optionImage);
								currentMotorAxisMenu.add(setOptionAction);
							}
							currentMotorMenu.add(currentMotorAxisMenu);
						}
					}
					for(final Option option : motor.getOptions()) {
						
						SetOptionAction setOptionAction = 
								new SetOptionAction(option, option.getName());
						setOptionAction.setImageDescriptor(optionImage);
						currentMotorMenu.add(setOptionAction);
					}
				manager.add(currentMotorMenu);
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
								
								SetOptionAction setOptionAction = 
									new SetOptionAction(option, option.getName());
								setOptionAction.setImageDescriptor(optionImage);
								currentDetectorChannelMenu.add(setOptionAction);
							}
							currentDetectorMenu.add(currentDetectorChannelMenu);
						}
					}
					for(final Option option : detector.getOptions()) {
						
						SetOptionAction setOptionAction = 
							new SetOptionAction(option, option.getName());
						setOptionAction.setImageDescriptor(optionImage);
						currentDetectorMenu.add(setOptionAction);
					}
					manager.add(currentDetectorMenu);
				}
			}
			
			for(final Device device : measuringStation.getDevices()) {
				
				// add only entries for devices without class names
				if(device.getClassName() == null || 
				   device.getClassName().isEmpty()) {
					
					SetDeviceAction setDeviceAction = 
						new SetDeviceAction(device, device.getName());
					setDeviceAction.setImageDescriptor(deviceImage);
					manager.add(setDeviceAction);
				}
			}
			
			// ********************************************************
			// * end of: Menu Entries for Devices without Class Names *
			// ********************************************************
			
			if (scanModule.getPostscans().length > 0) {
				Action deleteAction = new Action(){
					
					@Override public void run() {
			    		scanModule.remove((Postscan)((IStructuredSelection)
			    				tableViewer.getSelection()).getFirstElement());
			    		
			    		tableViewer.refresh();
			    	}
			    };
			    
			    deleteAction.setEnabled(true);
			    deleteAction.setText("Delete Postscan");
			    deleteAction.setToolTipText("Deletes Postscan");
			    deleteAction.setImageDescriptor(PlatformUI.getWorkbench().
			    							getSharedImages().getImageDescriptor(
			    							ISharedImages.IMG_TOOL_DELETE));
			   
			    manager.add(deleteAction);
			}
		}
	}
	
	// ***********************************************************************
	// ****************************** Actions ********************************
	// ***********************************************************************
	
	/**
	 * <code>SetOptionAction</code>.
	 */
	private class SetOptionAction extends Action {
		
		private Option option;
		
		/**
		 * Constructs a <code>SetOptionAction</code>.
		 * 
		 * @param option the 
		 * 		  {@link de.ptb.epics.eve.data.measuringstation.Option} that 
		 * 		  should be set
		 * @param text the text that appears in the menu entry
		 */
		public SetOptionAction(Option option, String text)
		{
			this.option = option;
			this.setText(text);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			super.run();
			for(final Postscan p : scanModule.getPostscans()) {
				if(p.getAbstractDevice() == option) {
					return;
				}
			}
			final Postscan p = new Postscan();
			p.setAbstractPrePostscanDevice(option);
			scanModule.add(p);
			
			tableViewer.refresh();
		}
	}
	
	/**
	 * <code>SetDeviceAction</code>.
	 */
	private class SetDeviceAction extends Action {
		
		private Device device;
		
		/**
		 * Constructs a <code>SetDeviceAction</code>.
		 * 
		 * @param device the 
		 * 		{@link de.ptb.epics.eve.data.measuringstation.Device} that 
		 * 		should be set
		 * @param text the text that appears in the menu entry
		 */
		public SetDeviceAction(Device device, String text)
		{
			this.device = device;
			this.setText(text);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			super.run();
			for(final Postscan p : scanModule.getPostscans()) {
				if(p.getAbstractDevice() == device) {
					return;
				}
			}
			final Postscan p = new Postscan();
			p.setAbstractPrePostscanDevice(device);
			scanModule.add(p);
			
			tableViewer.refresh();
		}
	}
}