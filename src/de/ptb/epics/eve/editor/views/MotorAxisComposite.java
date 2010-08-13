/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.Iterator;

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
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PlotWindowError;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

public class MotorAxisComposite extends Composite implements IModelUpdateListener {

	private TableViewer tableViewer;
	private Combo motorAxisCombo;
	private Button addButton;
	private ScanModul scanModul;
	
	public MotorAxisComposite( final Composite parent, final int style) {
		super( parent, style );
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
	    column.setText( "Motor Axis" );
	    column.setWidth( 250 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 1 );
	    column.setText( "Stepfunction" );
	    column.setWidth( 80 );

	    this.tableViewer.getTable().setHeaderVisible( true );
	    this.tableViewer.getTable().setLinesVisible( true );
	    
	    // hier wird eine Liste der vorhandenen DetectorChannels des Scan Moduls erstellt
	    this.tableViewer.setContentProvider( new MotorAxisInputWrapper() );
	    this.tableViewer.setLabelProvider( new MotorAxisLabelProvider() );
	    
	    final CellEditor[] editors = new CellEditor[2];
	    
	    editors[0] = new TextCellEditor( this.tableViewer.getTable() );
	    editors[1] = new TextCellEditor( this.tableViewer.getTable() );
	    
	    this.tableViewer.setCellEditors( editors );
	    
	    final String[] props = { "device", "value"};
	    
	    this.tableViewer.setColumnProperties( props );

		this.tableViewer.getTable().addSelectionListener( new SelectionListener() {
	    	
			public void widgetDefaultSelected( final SelectionEvent e ) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected( final SelectionEvent e ) {

				final String axisName = tableViewer.getTable().getSelection()[0].getText( 0 );
				Axis[] axis = scanModul.getAxis();
				for( int i = 0; i < axis.length; ++i ) {
					if( axis[i].getMotorAxis().getFullIdentifyer().equals( axisName ) ) {
						setMotorAxisView(axis[i]);
					}
				}
			}
		});
	    
	    this.motorAxisCombo = new Combo(this, SWT.READ_ONLY);
		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		this.motorAxisCombo.setLayoutData( gridData );
		
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
				if( !motorAxisCombo.getText().equals( "" ) ) {
					MotorAxis motorAxis = (MotorAxis) Activator.getDefault().getMeasuringStation()
						.getAbstractDeviceByFullIdentifyer(motorAxisCombo.getText());
					
					Axis axis = new Axis( scanModul );
					axis.setMotorAxis(motorAxis);
					scanModul.add(axis);
					setMotorAxisView(axis);
					// Table Eintrag wird aus der Combo-Box entfernt
					motorAxisCombo.remove(motorAxisCombo.getText());
					tableViewer.refresh();
				}
			}
		});
		
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
							currentClassMenu.add( currentMotorMenu );
							for( final MotorAxis axis : motor.getAxis() ) {
								final Action setAxisAction = new Action() {
									final MotorAxis ma = axis;
									public void run() {
										super.run();
										for( final Axis a : scanModul.getAxis() ) {
											if( a.getAbstractDevice() == ma ) {
												return;
											}
										}
										Axis a = new Axis( scanModul );
										a.setMotorAxis( ma );
										scanModul.add( a );
										setMotorAxisView(a);
										// Table Eintrag wird aus der Combo-Box entfernt
										motorAxisCombo.remove(ma.getFullIdentifyer());
										tableViewer.refresh();
									}
								};
								setAxisAction.setText( "".equals( axis.getName())?axis.getID():axis.getName() );
								currentMotorMenu.add( setAxisAction );
							}
						} else if( device instanceof MotorAxis ) {
							final Action setAxisAction = new Action() {
								final MotorAxis ma = (MotorAxis)device;
								public void run() {
									super.run();
									for( final Axis a : scanModul.getAxis() ) {
										if( a.getAbstractDevice() == ma ) {
											return;
										}
									}
									Axis a = new Axis( scanModul );
									a.setMotorAxis( ma );
									scanModul.add( a );
									setMotorAxisView(a);
									// Table Eintrag wird aus der Combo-Box entfernt
									motorAxisCombo.remove(ma.getFullIdentifyer());
									tableViewer.refresh();
								}
							};
							setAxisAction.setText( "".equals( device.getName())?device.getID():device.getName() );
							currentClassMenu.add( setAxisAction );
						}
						manager.add( currentClassMenu );
					}
				}
				
				for( final Motor motor : Activator.getDefault().getMeasuringStation().getMotors() ) {
					if( "".equals( motor.getClassName() ) || motor.getClassName() == null ) {
						final MenuManager currentMotorMenu = new MenuManager( "".equals( motor.getName())?motor.getID():motor.getName() );
						for( final MotorAxis axis : motor.getAxis() ) {
							if( "".equals( axis.getClassName()  ) || axis.getClassName() == null ) {
								final Action setAxisAction = new Action() {
									final MotorAxis ma = axis;
									public void run() {
										
										for( final Axis a : scanModul.getAxis() ) {
											if( a.getAbstractDevice() == ma ) {
												return;
											}
										}
										
										super.run();
										Axis a = new Axis( scanModul );
										a.setMotorAxis( ma );
										scanModul.add( a );
										setMotorAxisView(a);
										// Table Eintrag wird aus der Combo-Box entfernt
										motorAxisCombo.remove(ma.getFullIdentifyer());
										tableViewer.refresh();
									}
								};
								setAxisAction.setText( "".equals( axis.getName())?axis.getID():axis.getName() );
								currentMotorMenu.add( setAxisAction );
							}
						}
						manager.add( currentMotorMenu );
					}
				}
			}
		});
		final Menu contextMenu = menuManager.createContextMenu( this.motorAxisCombo );
		this.motorAxisCombo.setMenu( contextMenu );
		
		Action deleteAction = new Action(){
		    	public void run() {
	    		
					Axis removeAxis = (Axis)((IStructuredSelection)tableViewer.getSelection()).getFirstElement();
					
					// MotorAxis wird aus scanModul ausgetragen
					scanModul.remove( removeAxis );

		    		// ComboBox muß aktualisiert werden
		    		// alle MotorAxis' werden in die ComboBox eingetragen und die
		    		// gesetzten MotorAxis' wieder ausgetragen
					motorAxisCombo.setItems( Activator.getDefault().getMeasuringStation().getAxisFullIdentifyer().toArray( new String[0] ) );
					Axis[] axis = scanModul.getAxis();
					for (int i = 0; i < axis.length; ++i) {
						// Axis Eintrag wird aus der Combo-Box entfernt
						motorAxisCombo.remove(axis[i].getMotorAxis().getFullIdentifyer());
					}
		    		tableViewer.refresh();

		    		// PlotWindowView wird aktualisiert
		    		IViewReference[] ref = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart().getSite().getPage().getViewReferences();
					PlotWindowView plotWindowView = null;
					for (int i = 0; i < ref.length; ++i) {
						if (ref[i].getId().equals(PlotWindowView.ID)) {
							plotWindowView = (PlotWindowView) ref[i]
									.getPart(false);
						}
					}
					if( plotWindowView != null ) {
						PlotWindow aktPlotWindow = plotWindowView.getPlotWindow();
						// PlotWindowView wird neu gesetzt.
						plotWindowView.setPlotWindow(aktPlotWindow);
						// TODO: Wie kann man erreichen, daß das PlotWindow automatisch
						// aktualisiert wird, sobald sich die Auswahl der Achsen oder
						// Channels ändert? Kann man da irgendwo einen Listener setzen?
						// Gleiche Fragestellung gilt auch für DetectorChannel und
						// NormalizeChannel.
					}
		    	}
		    };
		    
		    deleteAction.setEnabled( true );
		    deleteAction.setText( "Delete Axis" );
		    deleteAction.setToolTipText( "Deletes Axis" );
		    deleteAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ) );
		    
		    MenuManager manager = new MenuManager();
		    Menu menu = manager.createContextMenu( this.tableViewer.getControl() );
		    this.tableViewer.getControl().setMenu( menu );
		    manager.add( deleteAction );
	}

	public void setMotorAxisView( Axis ansicht) {
		// MotorAxisView wird automatisch auf neue Achse gesetzt
		IViewReference[] ref = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart().getSite().getPage().getViewReferences();
		MotorAxisView motorAxisView = null;
		for (int i = 0; i < ref.length; ++i) {
			if (ref[i].getId().equals(MotorAxisView.ID)) {
				motorAxisView = (MotorAxisView) ref[i].getPart(false);
			}
		}
		if( motorAxisView != null ) {
			Axis[] axis = scanModul.getAxis();
			double stepamount = -1.0;
			for( int j = 0; j < axis.length; ++j ) {
				if( axis[j].isMainAxis() ) {
					stepamount = axis[j].getStepCount();
					break;
				}
			}
			motorAxisView.setAxis( ansicht, stepamount, scanModul );
		}
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

			this.motorAxisCombo.setItems( Activator.getDefault().getMeasuringStation().getAxisFullIdentifyer().toArray( new String[0] ) );
			Axis[] axis = scanModul.getAxis();
			for (int i = 0; i < axis.length; ++i) {
				// Motor Axis Eintrag wird aus der Combo-Box entfernt
				this.motorAxisCombo.remove(axis[i].getAbstractDevice().getFullIdentifyer());
			}
		}
		this.scanModul = scanModul;
		this.tableViewer.setInput( scanModul );
	}
	
}
