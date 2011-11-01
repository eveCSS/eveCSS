package de.ptb.epics.eve.editor.views;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.custom.CCombo;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.SWT;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventMessage;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

/**
 * <code>EventComposite</code>.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class EventComposite extends Composite implements IModelUpdateListener {

	private static Logger logger = 
		Logger.getLogger(EventComposite.class.getName());
	
	private String[] eventIDs;
	
	private ControlEventManager controlEventManager;
	
	private TableViewer tableViewer;
	
	private Combo eventsCombo;
	
	private EventsComboFocusListener eventsComboFocusListener;
	
	private Button addButton;
	
	private AddButtonSelectionListener addbuttonSelectionListener;
	
	/**
	 * Constructs an <code>EventComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 */
	public EventComposite(final Composite parent, final int style, final ControlEventTypes eventType) {
		super(parent, style);

		// the composite gets a 2 column grid
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		this.setLayout(gridLayout);
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		
		// initialize the table viewer
		this.tableViewer = new TableViewer(this, SWT.NONE);
		this.tableViewer.getControl().setLayoutData(gridData);

/**** Änderungen für Montag:
 * 
 * hier wird der CellEditor genauso erzeugt wie im Viewer und zwar nur an
 * einer Stelle. Es wird dann unterschieden für welche Spalte es gesetzt ist.
 * Damit vereinfacht sich dann die Anzahl der Aufrufe weil es nur noch an
 * einer Stelle geschieht.
 * 
		tableViewer.getTable().setE
********/
		
		// column 1: Source
		TableColumn column = new TableColumn(
				this.tableViewer.getTable(), SWT.LEFT, 0);
	    column.setText("Source");
	    column.setWidth(280);

	    // column 2: Operator
	    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 1);
	    column.setText("Operator");
	    column.setWidth(80);

	    switch (eventType) {
	    case CONTROL_EVENT:
		    // column 3: Limit
		    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 2);
		    column.setText("Limit");
		    column.setWidth(100);
	    	break;
	    case PAUSE_EVENT:
		    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 2);
		    column.setText("Limit");
		    column.setWidth(60);
	    
		    column = new TableColumn(this.tableViewer.getTable(), SWT.LEFT, 3);
		    column.setText("CIF");
		    column.setWidth(40);
	    	break;
	    }

	    this.tableViewer.getTable().setHeaderVisible(true);
	    this.tableViewer.getTable().setLinesVisible(true);
	    
	    // set the content & label provider
	    this.tableViewer.setContentProvider(new ControlEventInputWrapper());
	    this.tableViewer.setLabelProvider(new ControlEventLabelProvider());
	    
	    // cell modifier
	    final CellEditor[] editors;
	    final String[] operators = {"eq", "ne", "gt", "lt"};
	    final String[] props = {"source", "operator", "limit", "cif"};
	    
	    switch (eventType) {
	    case CONTROL_EVENT:
		    editors = new CellEditor[3];
		    editors[0] = new TextCellEditor(this.tableViewer.getTable());
		    editors[1] = new ComboBoxCellEditor(this.tableViewer.getTable(), 
		    									operators, SWT.READ_ONLY);
		    editors[2] = new TextCellEditor(this.tableViewer.getTable());
		    this.tableViewer.setCellEditors(editors);
	    	break;

	    case PAUSE_EVENT:
		    editors = new CellEditor[4];
		    editors[0] = new TextCellEditor(this.tableViewer.getTable());

		    final ComboBoxCellEditor comboEditor = new ComboBoxCellEditor(this.tableViewer.getTable(), 
					operators, SWT.READ_ONLY | SWT.SELECTED){

				@Override protected void focusLost() {
					fireCancelEditor();
					if (isDirty() == true) {
						System.out.println("Operator Wert wurde geändert, muß aktualisiert werden");
					}

					if(isActivated()) {
						System.out.println("Operator Cell ist aktiviert, CancelEditor");
						fireCancelEditor();
					}
					deactivate();
				}};

				((CCombo)comboEditor.getControl()).addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						System.out.println("widgetSelected");
						// nachsehen ob neuer Wert = vorhandener Wert
						// JA: nichts tun
						// NEIN: neuen Wert setzten.
						// TODO Auto-generated method stub

// Test ausprobieren hier an dieser Stelle ob das so geht.
						// Wenn ja, übernehmen in die allgemeine Funktion!
						System.out.println("   Wert auslesen");
						System.out.println("   comboEditor: " + comboEditor.toString());
						System.out.println("   Wert: " + ((CCombo)comboEditor.getControl()).getText());

						// new selected value
						String newValue = ((CCombo)comboEditor.getControl()).getText();
						// index of new selected value
						int newPoint = ((CCombo)comboEditor.getControl()).getSelectionIndex();
						
						// PauseEvent Object of selected row
						Object o = ((IStructuredSelection)tableViewer.getSelection()).toList().get(0);
						if (o instanceof PauseEvent) {
							PauseEvent pause = (PauseEvent)o;

							System.out.println(" Comparison: " + pause.getLimit().getComparison().toString());
							System.out.println(" equals: " + pause.getLimit().getComparison().equals(ComparisonTypes.stringToType(newValue)));

							// if actual value of pauseEvent equals new value, value not changed
							// -> deactivate and close menu
							if (pause.getLimit().getComparison().equals(ComparisonTypes.stringToType(newValue))) {
								comboEditor.deactivate();
								return;
							}
						}
						
						// value changed, call modify event
						tableViewer.getCellModifier().modify(o, "operator", newPoint);

						// deactivate ComboBox
						comboEditor.deactivate();
						
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
						System.out.println("widgetDefaultSelected");
						// TODO Auto-generated method stub
						
					}
				});
				
				editors[1] = comboEditor;
				editors[2] = new TextCellEditor(this.tableViewer.getTable()) {
					@Override protected void focusLost() {
						if (isDirty() == true) {
							logger.info("...f");
						}
						// if value not changed cancel focusLost callback
						if(isActivated() && !isDirty()) {
							fireCancelEditor();
						}
						deactivate();
					}};
				editors[3] = new CheckboxCellEditor(this.tableViewer.getTable());
				this.tableViewer.setCellEditors(editors);
	    	break;
	    }
	    
	    this.tableViewer.setCellModifier(
	    		new ControlEventCellModifyer(this.tableViewer));
	    this.tableViewer.setColumnProperties(props);
	    
	    // delete action as context menu
	    Action deleteAction = new DeleteAction();
	    deleteAction.setEnabled(true);
	    deleteAction.setText("Delete Control Event");
	    deleteAction.setToolTipText("Deletes the Control Event");
	    deleteAction.setImageDescriptor(PlatformUI.getWorkbench().
	    								getSharedImages().getImageDescriptor( 
	    								ISharedImages.IMG_TOOL_DELETE));
	    
	    MenuManager manager = new MenuManager();
	    Menu menu = manager.createContextMenu(this.tableViewer.getControl());
	    this.tableViewer.getControl().setMenu(menu);
	    manager.add(deleteAction);
	    
	    // combo box and add button to add new events
		this.eventsCombo = new Combo(this, SWT.READ_ONLY);		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		this.eventsCombo.setLayoutData(gridData);
		
		this.eventsComboFocusListener = new EventsComboFocusListener();
		this.eventsCombo.addFocusListener(eventsComboFocusListener);
		
		this.addButton = new Button(this, SWT.NONE);
		this.addButton.setText("Add");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.END;
		gridData.verticalAlignment = GridData.CENTER;
		this.addButton.setLayoutData(gridData);
		
		this.addbuttonSelectionListener = new AddButtonSelectionListener();
		this.addButton.addSelectionListener(addbuttonSelectionListener);
	}

	/**
	 * TODO
	 */
	public void setEventChoice() {
		Event[] measuringStationEvents = Activator.getDefault().
											getMeasuringStation().getEvents().
											toArray(new Event[0]);
		Event[] scanDescriptionEvents = null;

		if(controlEventManager.getParentChain() != null) {
			scanDescriptionEvents = controlEventManager.getParentChain().
										getScanDescription().getEvents().
										toArray(new Event[0]);
		} else if(controlEventManager.getParentScanModul() != null) {
			scanDescriptionEvents = controlEventManager.getParentScanModul().
										getChain().getScanDescription().
										getEvents().toArray(new Event[0]);
		} else if(controlEventManager.getParentChannel() != null) {
			scanDescriptionEvents = controlEventManager.getParentChannel().
										getParentScanModul().getChain().
										getScanDescription().getEvents().
										toArray(new Event[0]);
		}

		List<? extends ControlEvent> vorhListe = 
				controlEventManager.getControlEventsList();

		eventIDs = new String[
		        measuringStationEvents.length + scanDescriptionEvents.length];
		for(int i = 0; i < measuringStationEvents.length; ++i) {
			eventIDs[i] = measuringStationEvents[i].getName();
		}

		for(int i = measuringStationEvents.length; i < eventIDs.length; ++i) {
			eventIDs[i] = 
				scanDescriptionEvents[i-measuringStationEvents.length].getName();
		}
		eventsCombo.setItems(eventIDs);

		// durchlaufen durch die vorhandenen Events,
		// alle Namen die schon gesetzt sind mit remove wieder entfernen.
		for(int i = measuringStationEvents.length; i < eventIDs.length; ++i) {
			for(ControlEvent cevent : vorhListe) {
				if(cevent.getId() != null) {
					if(cevent.getId().equals(scanDescriptionEvents[
					               i-measuringStationEvents.length].getID())) {
						eventsCombo.remove(scanDescriptionEvents[
						       i-measuringStationEvents.length].getNameID());
					}
				}
			}
		}
	}

	/**
	 * Sets the control event manager.
	 * 
	 * @param controlEventManager the control event manager that should be set.
	 */
	public void setControlEventManager(
			final ControlEventManager controlEventManager) {

		if(controlEventManager != null)
			logger.debug("set control event manager (" +
						 controlEventManager.hashCode() + ")");
		else
			logger.debug("set control event manager (null)");
		
		if(this.controlEventManager != null) {
			this.controlEventManager.removeModelUpdateListener(this);
		}
		this.controlEventManager = controlEventManager;
		this.tableViewer.getTable().setEnabled(true);
		
		if(this.controlEventManager != null) {
			this.controlEventManager.addModelUpdateListener(this);

		} else { // controlEventManager == null
			this.tableViewer.getTable().clearAll();
			this.tableViewer.getTable().setEnabled(false);
		}
		this.tableViewer.setInput(controlEventManager);
	}
	
	/**
	 * Returns the control event manager.
	 * 
	 * @return the control event manager
	 */
	public ControlEventManager getControlEventManager() {
		return this.controlEventManager;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		// TODO was soll die Methode ?
		removeListeners();
		
		if(modelUpdateEvent.getMessage() instanceof ControlEventMessage) {
			
		}
		
		addListeners();
	}
	
	/*
	 * 
	 */
	private void addListeners()
	{
		this.eventsCombo.addFocusListener(eventsComboFocusListener);
		this.addButton.addSelectionListener(addbuttonSelectionListener);
	}
	
	/*
	 * 
	 */
	private void removeListeners()
	{
		this.eventsCombo.removeFocusListener(eventsComboFocusListener);
		this.addButton.removeSelectionListener(addbuttonSelectionListener);
	}
	
	// ************************************************************************
	// ****************************** Listeners *******************************
	// ************************************************************************
	
	/**
	 * <code>FocusListener</code> of <code>eventsCombo</code>.
	 */
	class EventsComboFocusListener implements FocusListener {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {
			System.out.println("\nFocus Gained von EventComposite aufgerufen");
			final String currentTextBuffer = eventsCombo.getText();
			setEventChoice();
			eventsCombo.setText(currentTextBuffer);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost (FocusEvent e) {
			System.out.println("\nFocus Lost von EventComposite aufgerufen");
		}
	}
	
	/**
	 * <code>SelectionListener</code> of <code>addButton</code>.
	 */
	class AddButtonSelectionListener implements SelectionListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(!eventsCombo.getText().equals("")) {
				// Wenn Klammern vorhanden, wird nur der Text innerhalb der 
				// Klammern verwendet. Die ID der Events steht innerhalb.
				
				int stelleAnfang = eventsCombo.getText().indexOf("(");
				int stelleEnde = eventsCombo.getText().indexOf(")");
				String addEvent = eventsCombo.getText().substring(
						stelleAnfang + 2, stelleEnde - 1);
					
				Event event = Activator.getDefault().getMeasuringStation().
										getEventById(addEvent);
				
				if(event == null) {
					if(controlEventManager.getParentChain() != null) {
						event = controlEventManager.getParentChain().
									getScanDescription().getEventById(addEvent);
					} else if(controlEventManager.getParentScanModul() != null) {
						event = controlEventManager.getParentScanModul().
											getChain().getScanDescription().
											getEventById(addEvent);
					} else if(controlEventManager.getParentChannel() != null) {
						event = controlEventManager.getParentChannel().
											getParentScanModul().getChain().
											getScanDescription().
											getEventById(addEvent);
					}
				}
				ControlEvent newEvent;
				if(controlEventManager.getControlEventType() == 
				   ControlEventTypes.CONTROL_EVENT) {
					newEvent = new ControlEvent(event.getType());
				} else {
					newEvent = new PauseEvent(event.getType());
				}
				newEvent.setEvent(event);
				// Id des neuen Events wird gesetzt
				newEvent.setId(addEvent);
				controlEventManager.addControlEvent(newEvent);
				
				// Auswahl der Events wird aktualisiert
				setEventChoice();
			}
		}
	}
	
	// ****************************** Actions *********************************
	
	/**
	 * 
	 */
	class DeleteAction extends Action {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			controlEventManager.removeControlEvent((ControlEvent)
					((IStructuredSelection)
							tableViewer.getSelection()).getFirstElement());
			setEventChoice();
		}
	}
}