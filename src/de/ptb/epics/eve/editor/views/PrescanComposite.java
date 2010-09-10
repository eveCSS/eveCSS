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
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

public class PrescanComposite extends Composite implements IModelUpdateListener {

	private TableViewer tableViewer;
	private ScanModul scanModul;
	private IMeasuringStation measuringStation;
	
	public PrescanComposite( final Composite parent, final int style, final IMeasuringStation measuringStation ) {
		super( parent, style );
		this.measuringStation = measuringStation;
		this.measuringStation.addModelUpdateListener( this );
		initialize();
	}
	
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
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
	    column.setWidth( 80 );

	    this.tableViewer.getTable().setHeaderVisible( true );
	    this.tableViewer.getTable().setLinesVisible( true );
	    
	    // hier wird eine Liste der vorhandenen Prescans des Scan Moduls erstellt
	    this.tableViewer.setContentProvider( new PrescanInputWrapper() );
	    this.tableViewer.setLabelProvider( new PrescanLabelProvider() );
	    
	    final CellEditor[] editors = new CellEditor[2];
	    
	    editors[0] = new TextCellEditor( this.tableViewer.getTable() );
	    editors[1] = new TextCellEditor( this.tableViewer.getTable() );
	    
	    this.tableViewer.setCellModifier( new PrescanCellModifyer( this.tableViewer ) );
	    this.tableViewer.setCellEditors( editors );
	    
	    final String[] props = { "device", "value"};
	    
	    this.tableViewer.setColumnProperties( props );
	    
		
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
											for( final Prescan p : scanModul.getPrescans() ) {
												if( p.getAbstractDevice() == o ) {
													return;
												}
											}
											final Prescan p = new Prescan();
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
										for( final Prescan p : scanModul.getPrescans() ) {
											if( p.getAbstractDevice() == o ) {
												return;
											}
										}
										final Prescan p = new Prescan();
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
										for( final Prescan p : scanModul.getPrescans() ) {
											if( p.getAbstractDevice() == o ) {
												return;
											}
										}
										final Prescan p = new Prescan();
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
											for( final Prescan p : scanModul.getPrescans() ) {
												if( p.getAbstractDevice() == o ) {
													return;
												}
											}
											final Prescan p = new Prescan();
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
										for( final Prescan p : scanModul.getPrescans() ) {
											if( p.getAbstractDevice() == o ) {
												return;
											}
										}
										final Prescan p = new Prescan();
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
										for( final Prescan p : scanModul.getPrescans() ) {
											if( p.getAbstractDevice() == o ) {
												return;
											}
										}
										final Prescan p = new Prescan();
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
									for( final Prescan p : scanModul.getPrescans() ) {
										if( p.getAbstractDevice() == dv ) {
											return;
										}
									}
									final Prescan p = new Prescan();
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
											for( final Prescan p : scanModul.getPrescans() ) {
												if( p.getAbstractDevice() == o ) {
													return;
												}
											}
											final Prescan p = new Prescan();
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
									for( final Prescan p : scanModul.getPrescans() ) {
										if( p.getAbstractDevice() == o ) {
											return;
										}
									}
									final Prescan p = new Prescan();
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
											for( final Prescan p : scanModul.getPrescans() ) {
												if( p.getAbstractDevice() == o ) {
													return;
												}
											}
											final Prescan p = new Prescan();
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
									for( final Prescan p : scanModul.getPrescans() ) {
										if( p.getAbstractDevice() == o ) {
											return;
										}
									}
									final Prescan p = new Prescan();
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
								for( final Prescan p : scanModul.getPrescans() ) {
									if( p.getAbstractDevice() == dv ) {
										return;
									}
								}
								final Prescan p = new Prescan();
								p.setAbstractPrePostscanDevice( dv );
								scanModul.add( p );
								
								tableViewer.refresh();
							}
						};
						manager.add( setDeviceAction );
						setDeviceAction.setText( "".equals( device.getName())?device.getID():device.getName() );
					}
				}
				Action deleteAction = new Action(){
			    	public void run() {
		    		
						// Prescan wird aus scanModul ausgetragen
			    		scanModul.remove( (Prescan)((IStructuredSelection)tableViewer.getSelection()).getFirstElement() );
			    		
			    		tableViewer.refresh();
			    	}
			    };
			    
			    deleteAction.setEnabled( true );
			    deleteAction.setText( "Delete Prescan" );
			    deleteAction.setToolTipText( "Deletes Prescan" );
			    deleteAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ) );

			    manager.add( deleteAction );
			}
		} );
		
		final Menu contextMenu = menuManager.createContextMenu( this.tableViewer.getControl() );
		this.tableViewer.getControl().setMenu( contextMenu );
	
		
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
