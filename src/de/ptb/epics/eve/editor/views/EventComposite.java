/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessage;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;


public class EventComposite extends Composite implements IModelUpdateListener {

	private String[] eventIDs;
	
	private ControlEventManager controlEventManager;
	private TableViewer tableViewer;
	private Combo eventsCombo;
	private Button addButton;
	
	public EventComposite( final Composite parent, final int style) {
		super(parent, style);
		initialize();
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
	    column.setText( "Source" );
	    column.setWidth( 200 );
	    
	    this.tableViewer.getTable().setHeaderVisible( true );
	    this.tableViewer.getTable().setLinesVisible( true );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 1 );
	    column.setText( "Operator" );
	    column.setWidth( 80 );

	    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 2 );
	    column.setText( "Limit" );
	    column.setWidth( 100 );
	    
	    this.tableViewer.setContentProvider( new ControlEventInputWrapper() );
	    this.tableViewer.setLabelProvider( new ControlEventLabelProvider() );
	    
	    final CellEditor[] editors = new CellEditor[3];
	    final String[] operators = { "eq", "ne", "gt", "lt" };
	    final String[] props = { "source", "operator", "limit" };
	    
	    editors[0] = new TextCellEditor( this.tableViewer.getTable() );
	    editors[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), operators, SWT.READ_ONLY );
	    editors[2] = new TextCellEditor( this.tableViewer.getTable() );
	    
	    this.tableViewer.setCellEditors( editors );
	    this.tableViewer.setCellModifier( new ControlEventCellModifyer( this.tableViewer ) );
	    this.tableViewer.setColumnProperties( props );
	    
	    Action deleteAction = new Action(){
	    	public void run() {
	    		
	    		controlEventManager.removeControlEvent( (ControlEvent)((IStructuredSelection)tableViewer.getSelection()).getFirstElement() );
				createEventChoice();
	    	}
	    };
	    
	    deleteAction.setEnabled( true );
	    deleteAction.setText( "Delete Control Event" );
	    deleteAction.setToolTipText( "Deletes the Control Event" );
	    deleteAction.setImageDescriptor( PlatformUI.getWorkbench().getSharedImages().getImageDescriptor( ISharedImages.IMG_TOOL_DELETE ) );
	    
	    MenuManager manager = new MenuManager();
	    Menu menu = manager.createContextMenu( this.tableViewer.getControl() );
	    this.tableViewer.getControl().setMenu( menu );
	    manager.add( deleteAction );
	    
		this.eventsCombo = new Combo(this, SWT.READ_ONLY);
		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		this.eventsCombo.setLayoutData( gridData );
		
		this.addButton = new Button( this, SWT.NONE );
		this.addButton.setText( "Add" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.END;
		gridData.verticalAlignment = GridData.CENTER;
		this.addButton.setLayoutData( gridData );
		this.createListener();

		this.eventsCombo.addFocusListener( new FocusListener() {

			public void focusGained( final FocusEvent e ) {
				// TODO: warum wird hier der TextBuffer gelesen und an die gleiche
				// Stelle wieder geschrieben. Hat das einen Sinn? (Hartmut 16.4.10)
				final String currentTextBuffer = eventsCombo.getText();
				createEventChoice();
				eventsCombo.setText( currentTextBuffer );
			}

			public void focusLost( final FocusEvent e ) {
			}
			
		}) ;
	}

	public void setEventChoice() {
		createEventChoice();
	}

	private void createEventChoice() {
		Event[] measuringStationEvents = Activator.getDefault().getMeasuringStation().getEvents().toArray( new Event[0] );
		Event[] scanDescriptionEvents = null;

		if( controlEventManager.getParentChain() != null ) {
			scanDescriptionEvents = controlEventManager.getParentChain().getScanDescription().getEvents().toArray( new Event[0] );
		} else if( controlEventManager.getParentScanModul() != null )  {
			scanDescriptionEvents = controlEventManager.getParentScanModul().getChain().getScanDescription().getEvents().toArray( new Event[0] );
		} else if( controlEventManager.getParentChannel() != null ) {
			scanDescriptionEvents = controlEventManager.getParentChannel().getParentScanModul().getChain().getScanDescription().getEvents().toArray( new Event[0] );
		}

		List<? extends ControlEvent> vorhListe = controlEventManager.getControlEventsList();

		eventIDs = new String[measuringStationEvents.length + scanDescriptionEvents.length ];
		for( int i = 0; i < measuringStationEvents.length; ++i ) {
			eventIDs[i] = measuringStationEvents[i].getName();
		}

		for( int i = measuringStationEvents.length; i < eventIDs.length; ++i ) {
			eventIDs[i] = scanDescriptionEvents[i-measuringStationEvents.length].getName();
		}
		eventsCombo.setItems( eventIDs );

		// durchlaufen durch die vorhandenen Events,
		// alle Namen die schon gesetzt sind mit remove wieder entfernen.
		for( int i = measuringStationEvents.length; i < eventIDs.length; ++i ) {
			for (ControlEvent cevent : vorhListe) {
				if (cevent.getId() != null) {
					if (cevent.getId().equals(scanDescriptionEvents[i-measuringStationEvents.length].getID())) {
						eventsCombo.remove(scanDescriptionEvents[i-measuringStationEvents.length].getNameID());
					}
				}
			}
		}
	}
	
	private void createListener() {
	
		this.addButton.addSelectionListener( new SelectionListener() {

				public void widgetDefaultSelected( final SelectionEvent e ) {
					// TODO Auto-generated method stub
				
				}

				public void widgetSelected( final SelectionEvent e ) {
					if( !eventsCombo.getText().equals( "" ) ) {
						// Wenn Klammern vorhanden, wird nur der Text innerhalb der Klammern verwendet
						// Die ID der Events steht innerhalb von Klammern
						
						int stelleAnfang = eventsCombo.getText().indexOf("(");
						int stelleEnde = eventsCombo.getText().indexOf(")");
						String addEvent = eventsCombo.getText().substring( stelleAnfang + 2, stelleEnde - 1);
							
						Event event = Activator.getDefault().getMeasuringStation().getEventById( addEvent );
						
						if( event == null ) {
							if( controlEventManager.getParentChain() != null ) {
								event = controlEventManager.getParentChain().getScanDescription().getEventById( addEvent );
							} else if( controlEventManager.getParentScanModul() != null ) {
								event = controlEventManager.getParentScanModul().getChain().getScanDescription().getEventById( addEvent );
							} else if( controlEventManager.getParentChannel() != null ) {
								event = controlEventManager.getParentChannel().getParentScanModul().getChain().getScanDescription().getEventById( addEvent );
							}
						}
						ControlEvent newEvent;
						if( controlEventManager.getControlEventType() == ControlEventTypes.CONTROL_EVENT ) {
							newEvent = new ControlEvent(event.getType());
						} else {
							newEvent = new PauseEvent(event.getType());
						}
						newEvent.setEvent( event );
						// Id des neuen Events wird gesetzt
						newEvent.setId(addEvent);
						controlEventManager.addControlEvent( newEvent );
						
						// Auswahl der Events wird aktualisiert
						createEventChoice();
					}
				}
			}
		);
	}
	
	public void setControlEventManager( final ControlEventManager controlEventManager ) {
		if( this.controlEventManager != null ) {
			this.controlEventManager.removeModelUpdateListener( this );
		}
		this.controlEventManager = controlEventManager;
		
		if( this.controlEventManager != null ) {
			this.controlEventManager.addModelUpdateListener( this );
			
			if( (controlEventManager.getControlEventType() == ControlEventTypes.CONTROL_EVENT) && this.tableViewer.getTable().getColumns().length != 3 ) {
				
				TableColumn[] columns = this.tableViewer.getTable().getColumns();
				for( int i = 0; i < columns.length; ++i ) {
					columns[i].dispose();
				}
				
				TableColumn column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 0 );
			    column.setText( "SourceA" );
			    column.setWidth( 200 );
			    
			    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 1 );
			    column.setText( "OperatorB" );
			    column.setWidth( 60 );

			    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 2 );
			    column.setText( "LimitC" );
			    column.setWidth( 50 );
			    
			    final CellEditor[] editors = new CellEditor[3];
			    final String[] operators = { "eq", "ne", "gt", "lt" };
			    editors[0] = new TextCellEditor( this.tableViewer.getTable() );
			    editors[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), operators, SWT.READ_ONLY );
			    editors[2] = new TextCellEditor( this.tableViewer.getTable() );
			    
			    this.tableViewer.setCellEditors( editors );
			    
			    final String[] props = { "source", "operator", "limit" };
			    this.tableViewer.setColumnProperties( props );
				
			} 
			else if( (controlEventManager.getControlEventType() == ControlEventTypes.PAUSE_EVENT) && this.tableViewer.getTable().getColumns().length != 4 ) {

				TableColumn[] columns = this.tableViewer.getTable().getColumns();
				for( int i = 0; i < columns.length; ++i ) {
					columns[i].dispose();
				}
				
				TableColumn column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 0 );
			    column.setText( "Source" );
			    column.setWidth( 200 );
			    
			    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 1 );
			    column.setText( "Operator" );
			    column.setWidth( 80 );

			    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 2 );
			    column.setText( "Limit" );
			    column.setWidth( 50 );
			    
			    column = new TableColumn( this.tableViewer.getTable(), SWT.LEFT, 3 );
			    column.setText( "CIF" );
			    column.setWidth( 40 );
			    
			    final CellEditor[] editors = new CellEditor[4];
			    final String[] operators = { "eq", "ne", "gt", "lt" };
			    editors[0] = new TextCellEditor( this.tableViewer.getTable() );
			    editors[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), operators, SWT.READ_ONLY );
			    editors[2] = new TextCellEditor( this.tableViewer.getTable() );
			    editors[3] = new CheckboxCellEditor( this.tableViewer.getTable() );

			    this.tableViewer.setCellEditors( editors );
			    
			    final String[] props = { "source", "operator", "limit", "cif" };
			    this.tableViewer.setColumnProperties( props );
				
			}  
			
		}
	
		this.tableViewer.setInput( controlEventManager );
	}
	

	public ControlEventManager getControlEventManager() {
		return this.controlEventManager;
	}
	
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		
		if( modelUpdateEvent.getMessage() instanceof ControlEventMessage ) {
			
		}
	}
}
