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
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.measuringstation.AbstractDevice;
import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

public class DetectorChannelComposite extends Composite implements IModelUpdateListener {

	private TableViewer tableViewer;
	private ScanModul scanModul;
	private MenuManager menuManager;
	private final IMeasuringStation measuringStation;
	
	
	public DetectorChannelComposite( final Composite parent, final int style, final IMeasuringStation measuringStation ) {
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
	    column.setText( "Detector Channel" );
	    column.setWidth( 250 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 1 );
	    column.setText( "Average" );
	    column.setWidth( 80 );

	    this.tableViewer.getTable().setHeaderVisible( true );
	    this.tableViewer.getTable().setLinesVisible( true );
	    
	    // hier wird eine Liste der vorhandenen DetectorChannels des Scan Moduls erstellt
	    this.tableViewer.setContentProvider( new DetectorChannelInputWrapper() );
	    this.tableViewer.setLabelProvider( new DetectorChannelLabelProvider() );
	    
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

				final String channelName = tableViewer.getTable().getSelection()[0].getText( 0 );
				Channel[] channels = scanModul.getChannels();
				for( int i = 0; i < channels.length; ++i ) {
					if( channels[i].getDetectorChannel().getFullIdentifyer().equals( channelName ) ) {
						setDetectorChannelView(channels[i]);
					}
				}
			}
		});
	    
	    
		menuManager = new MenuManager( "#PopupMenu" );
		
		menuManager.setRemoveAllWhenShown( true );
		menuManager.addMenuListener( new IMenuListener() {

			@Override
			public void menuAboutToShow( final IMenuManager manager ) {
				
				for( final String className : measuringStation.getClassNameList() ) {
					boolean containsAtLeastOne = false;
					final MenuManager currentClassMenu = new MenuManager( className );
					for( final AbstractDevice device : measuringStation.getDeviceList( className ) ) {
						if( device instanceof Detector ) {
							containsAtLeastOne = true;
							final Detector detector = (Detector)device;
							final MenuManager currentDetectorMenu = new MenuManager( "".equals( detector.getName())?detector.getID():detector.getName() );
							currentClassMenu.add( currentDetectorMenu );
							for( final DetectorChannel channel : detector.getChannels() ) {
								final Action setChannelAction = new Action() {
									final DetectorChannel dc = channel;
									public void run() {
										super.run();
										for( final Channel c : scanModul.getChannels() ) {
											if( c.getAbstractDevice() == dc ) {
												return;
											}
										}
										Channel c = new Channel( scanModul );
										c.setDetectorChannel( dc );
										scanModul.add( c );
										setDetectorChannelView(c);
										tableViewer.refresh();
									}
								};
								setChannelAction.setText( "".equals( channel.getName())?channel.getID():channel.getName() );
								containsAtLeastOne = true;
								currentDetectorMenu.add( setChannelAction );
							}
						} else if( device instanceof DetectorChannel ) {
							containsAtLeastOne = true;	
							final Action setChannelAction = new Action() {
								final DetectorChannel dc = (DetectorChannel)device;
								public void run() {
									super.run();
									for( final Channel c : scanModul.getChannels() ) {
										if( c.getAbstractDevice() == dc ) {
											return;
										}
									}
									Channel c = new Channel( scanModul );
									c.setDetectorChannel( dc );
									scanModul.add( c );
									setDetectorChannelView(c);
									tableViewer.refresh();
								}
							};
							currentClassMenu.add( setChannelAction );
							setChannelAction.setText( "".equals( device.getName())?device.getID():device.getName() );
							containsAtLeastOne = true;
						}
						if( containsAtLeastOne ) {
							manager.add( currentClassMenu );
						} 
					}
				}
				
				for( final Detector detector : measuringStation.getDetectors() ) {
					if( "".equals( detector.getClassName() ) || detector.getClassName() == null ) {
						boolean containsAtLeastOne = false;
						final MenuManager currentDetectorMenu = new MenuManager( "".equals( detector.getName())?detector.getID():detector.getName() );
						for( final DetectorChannel channel : detector.getChannels() ) {
							if( "".equals( channel.getClassName()  ) || channel.getClassName() == null ) {
								final Action setChannelAction = new Action() {
									final DetectorChannel dc = channel;
									public void run() {
										
										for( final Channel c : scanModul.getChannels()) {
											if( c.getAbstractDevice() == dc ) {
												return;
											}
										}
										
										super.run();
										Channel c = new Channel( scanModul );
										c.setDetectorChannel( dc );
										scanModul.add( c );
										setDetectorChannelView(c);
										tableViewer.refresh();
									}
								};
								setChannelAction.setText( "".equals( channel.getName())?channel.getID():channel.getName() );
								containsAtLeastOne = true;
								currentDetectorMenu.add( setChannelAction );
							}
						}
						if( containsAtLeastOne ) {
							manager.add( currentDetectorMenu );
						} 
					}
				}
				
				Action deleteAction = new Action(){
			    	public void run() {
		    		
						// DetectorChannel wird aus scanModul ausgetragen
			    		scanModul.remove( (Channel)((IStructuredSelection)tableViewer.getSelection()).getFirstElement() );

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
						}
			    	}
			    };
			    
			    deleteAction.setEnabled( true );
			    deleteAction.setText( "Delete Channel" );
			    deleteAction.setToolTipText( "Deletes Channel" );
			    deleteAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ) );
			    
			    manager.add( deleteAction );
			}
			
		});
				    
		final Menu contextMenu = menuManager.createContextMenu( this.tableViewer.getTable() );
		this.tableViewer.getControl().setMenu( contextMenu );
	}

	public void setDetectorChannelView( Channel ansicht) {
		// DetectorChannelView wird automatisch auf neuen Channel gesetzt

		IViewReference[] ref = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getPartService().getActivePart().getSite().getPage().getViewReferences();
		DetectorChannelView detectorChannelView = null;
		for (int i = 0; i < ref.length; ++i) {
			if (ref[i].getId().equals(DetectorChannelView.ID)) {
				detectorChannelView = (DetectorChannelView) ref[i].getPart(false);
			}
		}
		if( detectorChannelView != null ) {
			detectorChannelView.setChannel( ansicht );
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
		}
		this.scanModul = scanModul;
		this.tableViewer.setInput( scanModul );
	}
	
}
