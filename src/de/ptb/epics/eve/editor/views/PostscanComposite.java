/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.Option;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

public class PostscanComposite extends Composite implements IModelUpdateListener {

	private TableViewer tableViewer;
	private Combo postscanCombo;
	private Button addButton;
	private ScanModul scanModul;
	private final IMeasuringStation measuringStation;
	
	public PostscanComposite( final Composite parent, final int style, final IMeasuringStation measuringStation ) {
		super( parent, style );
		initialize();
		this.measuringStation = measuringStation;
		this.measuringStation.addModelUpdateListener( this );
	}
	
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		// All Options and Devices are typically a long list on a real measuring station (about 1000 entries)
		// If you call setItems with such a big list, this functions needs an extremly long time.
		//this.postscanCombo.setItems( this.measuringStation.getPrePostScanDevicesFullIdentifyer().toArray( new String[0] ) );
		this.measuringStation.getPrePostScanDevicesFullIdentifyer().toArray( new String[0] );
		
	}

	private void initialize() {
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		this.setLayout( gridLayout );
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		
		this.tableViewer = new TableViewer( this, SWT.NONE );
		this.tableViewer.getControl().setLayoutData( gridData );
		
		TableColumn column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 0 );
	    column.setText( "Device" );
	    column.setWidth( 300 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 1 );
	    column.setText( "Value" );
	    column.setWidth( 100 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 2 );
	    column.setText( "Reset Original" );
	    column.setWidth( 100 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 3 );
	    column.setText( " " );
	    column.setWidth( 10 );

	    this.tableViewer.getTable().setHeaderVisible( true );
	    this.tableViewer.getTable().setLinesVisible( true );
	    
	    // hier wird eine Liste der vorhandenen Postscans des Scan Moduls erstellt
	    this.tableViewer.setContentProvider( new PostscanInputWrapper() );
	    this.tableViewer.setLabelProvider( new PostscanLabelProvider() );
	    
	    final CellEditor[] editors = new CellEditor[3];
	    
	    final String[] yesNo = {"yes","no"};

	    editors[0] = new TextCellEditor( this.tableViewer.getTable() );
	    editors[1] = new TextCellEditor( this.tableViewer.getTable() );
	    editors[2] = new ComboBoxCellEditor( this.tableViewer.getTable(), yesNo, SWT.READ_ONLY);
	    
	    this.tableViewer.setCellModifier( new PostscanCellModifyer( this.tableViewer ) );
	    this.tableViewer.setCellEditors( editors );
	    
	    final String[] props = { "device", "value", "reset"};
	    
	    this.tableViewer.setColumnProperties( props );
	    
	    this.postscanCombo = new Combo(this, SWT.READ_ONLY);
	    this.postscanCombo.add( "" );
	    
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		this.postscanCombo.setLayoutData( gridData );
		
		final MenuManager menuManager = new MenuManager( "#PopupMenu" );
		menuManager.setRemoveAllWhenShown( true );
		menuManager.addMenuListener( new IMenuListener() {

			@Override
			public void menuAboutToShow( final IMenuManager manager ) {
				
				for( final String className : Activator.getDefault().getMeasuringStation().getClassNameList() ) {
					final MenuManager currentClassMenu = new MenuManager( className );
					
					for( final AbstractDevice device : Activator.getDefault().getMeasuringStation().getDeviceList( className ) ) {
						if( device instanceof Motor ) {
							final Motor motor = (Motor)device;
							final MenuManager currentMotorMenu = new MenuManager( "".equals( motor.getName())?motor.getID():motor.getName() );
							for( final MotorAxis motorAxis : motor.getAxis() ) {
								final MenuManager currentMotorAxisMenu = new MenuManager( "".equals( motorAxis.getName())?motorAxis.getID():motorAxis.getName() );
								for( final Option option : motorAxis.getOptions() ) {
									final Action setOptionAction = new Action() {
										final Option o = option;
										public void run() {
											super.run();
											for( final Postscan p : scanModul.getPostscans() ) {
												if( p.getAbstractDevice() == o ) {
													return;
												}
											}
											final Postscan p = new Postscan();
											p.setAbstractPrePostscanDevice( o );
											scanModul.add( p );
											
											tableViewer.refresh();
										}
									};
									currentMotorAxisMenu.add( setOptionAction );
									setOptionAction.setText( "".equals( option.getName())?option.getID():option.getName() );
								}
								currentMotorMenu.add( currentMotorAxisMenu );
							}
							for( final Option option : motor.getOptions() ) {
								final Action setOptionAction = new Action() {
									final Option o = option;
									public void run() {
										super.run();
										for( final Postscan p : scanModul.getPostscans() ) {
											if( p.getAbstractDevice() == o ) {
												return;
											}
										}
										final Postscan p = new Postscan();
										p.setAbstractPrePostscanDevice( o );
										scanModul.add( p );
										
										tableViewer.refresh();
									}
								};
								currentMotorMenu.add( setOptionAction );
								setOptionAction.setText( "".equals( option.getName())?option.getID():option.getName() );
							}
							currentClassMenu.add( currentMotorMenu );
						} else if( device instanceof MotorAxis ) {
							final MotorAxis motorAxis = (MotorAxis)device;
							final MenuManager currentMotorAxisMenu = new MenuManager( "".equals( motorAxis.getName())?motorAxis.getID():motorAxis.getName() );
							for( final Option option : motorAxis.getOptions() ) {
								final Action setOptionAction = new Action() {
									final Option o = option;
									public void run() {
										super.run();
										for( final Postscan p : scanModul.getPostscans() ) {
											if( p.getAbstractDevice() == o ) {
												return;
											}
										}
										final Postscan p = new Postscan();
										p.setAbstractPrePostscanDevice( o );
										scanModul.add( p );
										
										tableViewer.refresh();
									}
								};
								currentMotorAxisMenu.add( setOptionAction );
								setOptionAction.setText( "".equals( option.getName())?option.getID():option.getName() );
							}
							currentClassMenu.add( currentMotorAxisMenu );
						} else if( device instanceof Detector ) {
							final Detector detector = (Detector)device;
							final MenuManager currentDetectorMenu = new MenuManager( "".equals( detector.getName())?detector.getID():detector.getName() );
							for( final DetectorChannel detectorChannel : detector.getChannels() ) {
								final MenuManager currentDetectorChannelMenu = new MenuManager( "".equals( detectorChannel.getName())?detectorChannel.getID():detectorChannel.getName() );
								for( final Option option : detectorChannel.getOptions() ) {
									final Action setOptionAction = new Action() {
										final Option o = option;
										public void run() {
											super.run();
											for( final Postscan p : scanModul.getPostscans() ) {
												if( p.getAbstractDevice() == o ) {
													return;
												}
											}
											final Postscan p = new Postscan();
											p.setAbstractPrePostscanDevice( o );
											scanModul.add( p );
											
											tableViewer.refresh();
										}
									};
									currentDetectorChannelMenu.add( setOptionAction );
									setOptionAction.setText( "".equals( option.getName())?option.getID():option.getName() );
								}
								currentDetectorMenu.add( currentDetectorChannelMenu );
							}
							for( final Option option : detector.getOptions() ) {
								final Action setOptionAction = new Action() {
									final Option o = option;
									public void run() {
										super.run();
										for( final Postscan p : scanModul.getPostscans() ) {
											if( p.getAbstractDevice() == o ) {
												return;
											}
										}
										final Postscan p = new Postscan();
										p.setAbstractPrePostscanDevice( o );
										scanModul.add( p );
										
										tableViewer.refresh();
									}
								};
								currentDetectorMenu.add( setOptionAction );
								setOptionAction.setText( "".equals( option.getName())?option.getID():option.getName() );
							}
							currentClassMenu.add( currentDetectorMenu );
						} else if( device instanceof DetectorChannel ) {
							final DetectorChannel detectorChannel = (DetectorChannel)device;
							final MenuManager currentDetectorChannelMenu = new MenuManager( "".equals( detectorChannel.getName())?detectorChannel.getID():detectorChannel.getName() );
							for( final Option option : detectorChannel.getOptions() ) {
								final Action setOptionAction = new Action() {
									final Option o = option;
									public void run() {
										super.run();
										for( final Postscan p : scanModul.getPostscans() ) {
											if( p.getAbstractDevice() == o ) {
												return;
											}
										}
										final Postscan p = new Postscan();
										p.setAbstractPrePostscanDevice( o );
										scanModul.add( p );
										
										tableViewer.refresh();
									}
								};
								currentDetectorChannelMenu.add( setOptionAction );
								setOptionAction.setText( "".equals( option.getName())?option.getID():option.getName() );
							}
							currentClassMenu.add( currentDetectorChannelMenu );
						} else if( device instanceof Device ) {
							final Action setDeviceAction = new Action() {
								final Device dv = (Device)device;
								public void run() {
									super.run();
									for( final Postscan p : scanModul.getPostscans() ) {
										if( p.getAbstractDevice() == dv ) {
											return;
										}
									}
									final Postscan p = new Postscan();
									p.setAbstractPrePostscanDevice( dv );
									scanModul.add( p );
									
									tableViewer.refresh();
								}
							};
							currentClassMenu.add( setDeviceAction );
							setDeviceAction.setText( "".equals( device.getName())?device.getID():device.getName() );
						}
					}
					manager.add( currentClassMenu );

				}
				for( final Motor motor : Activator.getDefault().getMeasuringStation().getMotors() ) {
					if( "".equals( motor.getClassName() ) || motor.getClassName() == null ) {
						final MenuManager currentMotorMenu = new MenuManager( "".equals( motor.getName())?motor.getID():motor.getName() );
						for( final MotorAxis motorAxis : motor.getAxis() ) {
							if( "".equals( motorAxis.getClassName() ) || motorAxis.getClassName() == null ) {
								final MenuManager currentMotorAxisMenu = new MenuManager( "".equals( motorAxis.getName())?motorAxis.getID():motorAxis.getName() );
								for( final Option option : motorAxis.getOptions() ) {
									final Action setOptionAction = new Action() {
										final Option o = option;
										public void run() {
											super.run();
											for( final Postscan p : scanModul.getPostscans() ) {
												if( p.getAbstractDevice() == o ) {
													return;
												}
											}
											final Postscan p = new Postscan();
											p.setAbstractPrePostscanDevice( o );
											scanModul.add( p );
											
											tableViewer.refresh();
										}
									};
									currentMotorAxisMenu.add( setOptionAction );
									setOptionAction.setText( "".equals( option.getName())?option.getID():option.getName() );
								}
								currentMotorMenu.add( currentMotorAxisMenu );
							}
						}
						for( final Option option : motor.getOptions() ) {
							final Action setOptionAction = new Action() {
								final Option o = option;
								public void run() {
									super.run();
									for( final Postscan p : scanModul.getPostscans() ) {
										if( p.getAbstractDevice() == o ) {
											return;
										}
									}
									final Postscan p = new Postscan();
									p.setAbstractPrePostscanDevice( o );
									scanModul.add( p );
									
									tableViewer.refresh();
								}
							};
							currentMotorMenu.add( setOptionAction );
							setOptionAction.setText( "".equals( option.getName())?option.getID():option.getName() );
						}
					manager.add( currentMotorMenu );
					}
				}
				for( final Detector detector : Activator.getDefault().getMeasuringStation().getDetectors() ) {
					if( "".equals( detector.getClassName() ) || detector.getClassName() == null ) {
						final MenuManager currentDetectorMenu = new MenuManager( "".equals( detector.getName())?detector.getID():detector.getName() );
						for( final DetectorChannel detectorChannel : detector.getChannels() ) {
							if( "".equals( detectorChannel.getClassName() ) || detectorChannel.getClassName() == null ) {
								final MenuManager currentDetectorChannelMenu = new MenuManager( "".equals( detectorChannel.getName())?detectorChannel.getID():detectorChannel.getName() );
								for( final Option option : detectorChannel.getOptions() ) {
									final Action setOptionAction = new Action() {
										final Option o = option;
										public void run() {
											super.run();
											for( final Postscan p : scanModul.getPostscans() ) {
												if( p.getAbstractDevice() == o ) {
													return;
												}
											}
											final Postscan p = new Postscan();
											p.setAbstractPrePostscanDevice( o );
											scanModul.add( p );
											
											tableViewer.refresh();
										}
									};
									currentDetectorChannelMenu.add( setOptionAction );
									setOptionAction.setText( "".equals( option.getName())?option.getID():option.getName() );
								}
								currentDetectorMenu.add( currentDetectorChannelMenu );
							}
							
						}
						for( final Option option : detector.getOptions() ) {
							final Action setOptionAction = new Action() {
								final Option o = option;
								public void run() {
									super.run();
									for( final Postscan p : scanModul.getPostscans() ) {
										if( p.getAbstractDevice() == o ) {
											return;
										}
									}
									final Postscan p = new Postscan();
									p.setAbstractPrePostscanDevice( o );
									scanModul.add( p );
									
									tableViewer.refresh();
								}
							};
							currentDetectorMenu.add( setOptionAction );
							setOptionAction.setText( "".equals( option.getName())?option.getID():option.getName() );
						}
						manager.add( currentDetectorMenu );
					}
				}
				for( final Device device : Activator.getDefault().getMeasuringStation().getDevices() ) {
					if( "".equals( device.getClassName() ) || device.getClassName() == null ) {
						final Action setDeviceAction = new Action() {
							final Device dv = (Device)device;
							public void run() {
								super.run();
								for( final Postscan p : scanModul.getPostscans() ) {
									if( p.getAbstractDevice() == dv ) {
										return;
									}
								}
								final Postscan p = new Postscan();
								p.setAbstractPrePostscanDevice( dv );
								scanModul.add( p );
								
								tableViewer.refresh();
							}
						};
						manager.add( setDeviceAction );
						setDeviceAction.setText( "".equals( device.getName())?device.getID():device.getName() );
					}
				}
			}
		} );
		
		final Menu contextMenu = menuManager.createContextMenu( this.postscanCombo );
		this.postscanCombo.setMenu( contextMenu );
		
		
		this.addButton = new Button( this, SWT.NONE );
		this.addButton.setText( "Add" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.END;
		gridData.verticalAlignment = GridData.CENTER;
		this.addButton.setLayoutData( gridData );
		this.addButton.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				
				if( !postscanCombo.getText().equals( "" ) ) {

					AbstractPrePostscanDevice device = (AbstractPrePostscanDevice) Activator
							.getDefault().getMeasuringStation()
							.getAbstractDeviceByFullIdentifyer(
										postscanCombo.getText());

					Postscan postscan = new Postscan ();
					postscan.setAbstractPrePostscanDevice(device);
					
					scanModul.add(postscan);
					
					tableViewer.refresh();
				}
			}
		});
		
		   Action deleteAction = new Action(){
		    	public void run() {
		    		
		    		scanModul.remove( (Postscan)((IStructuredSelection)tableViewer.getSelection()).getFirstElement() );
		    		
		    		tableViewer.refresh();
		    	}
		    };
		    
		    deleteAction.setEnabled( true );
		    deleteAction.setText( "Delete Postscan" );
		    deleteAction.setToolTipText( "Deletes Postscan" );
		    deleteAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ) );
		    
		    MenuManager manager = new MenuManager();
		    Menu menu = manager.createContextMenu( this.tableViewer.getControl() );
		    this.tableViewer.getControl().setMenu( menu );
		    manager.add( deleteAction );
		
	}
	
	public ScanModul getScanModul() {
		return this.scanModul;
	}
	
	public void setScanModul( final ScanModul scanModul ) {
		if( this.scanModul != null ) {
			this.scanModul.removeModelUpdateListener( this );
		}
		if( scanModul != null ) {
			scanModul.addModelUpdateListener( this );
		}
		this.scanModul = scanModul;
		this.tableViewer.setInput( scanModul );
	}
	
}
