package de.ptb.epics.eve.editor.views;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.custom.CCombo;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
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

	private ControlEventTypes eventType;
	
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
	public EventComposite(final Composite parent, final int style, 
							final ControlEventTypes eventType) {
		super(parent, style);
		
		this.setLayout(new GridLayout());
		
		this.eventType = eventType;
		
		createViewer(parent);
		
		
		/*
		
	    // cell modifier
	    final CellEditor[] editors;
	    final String[] operators = {"eq", "ne", "gt", "lt"};
	    final String[] props = {"source", "operator", "limit", "cif"};
	    
	    editors = new CellEditor[4];
	    editors[0] = new TextCellEditor(this.tableViewer.getTable());

	    final ComboBoxCellEditor comboEditor = new ComboBoxCellEditor(this.tableViewer.getTable(), 
				operators, SWT.READ_ONLY | SWT.SELECTED) {

			@Override protected void focusLost() {
				fireCancelEditor();
				deactivate();
			}};

			((CCombo)comboEditor.getControl()).addSelectionListener(new SelectionListener() {
				@Override public void widgetSelected(SelectionEvent e) {
					// new selected value
					String newValue = ((CCombo)comboEditor.getControl()).getText();
					// index of new selected value
					int newPoint = ((CCombo)comboEditor.getControl()).getSelectionIndex();
					
					// ControlEvent Object of selected row
					Object o = ((IStructuredSelection)tableViewer.getSelection()).toList().get(0);
					if (o instanceof ControlEvent) {
						ControlEvent control = (ControlEvent)o;
						// if actual value of pauseEvent equals new value, value not changed
						// -> deactivate and close menu
						if (control.getLimit().getComparison().equals(ComparisonTypes.stringToType(newValue))) {
							comboEditor.deactivate();
							return;
						}
					}
					
					// value changed, call modify event
					removeListeners();
					tableViewer.getCellModifier().modify(o, "operator", newPoint);
					addListeners();

					// deactivate ComboBox
					comboEditor.deactivate();
				}
				
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			editors[1] = comboEditor;
			editors[2] = new TextCellEditor(this.tableViewer.getTable()) {
				@Override protected void focusLost() {
					// if value not changed, cancel focusLost callback
					if(isActivated() && !isDirty()) {
						fireCancelEditor();
					}
					deactivate();
				}};

	    switch (eventType) {
	    case PAUSE_EVENT:
				editors[3] = new CheckboxCellEditor(this.tableViewer.getTable());
	    	break;
	    }

	    this.tableViewer.setCellEditors(editors);
	    
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
		GridData gridData = new GridData();
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
		
		*/
	} // end of: Constructor

	private void createViewer(Composite parent) {
		this.tableViewer = new TableViewer(this, SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData();
		gridData.minimumHeight = 120;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.tableViewer.getTable().setLayoutData(gridData);
		createColumns(this.tableViewer);
		this.tableViewer.getTable().setHeaderVisible(true);
		this.tableViewer.getTable().setLinesVisible(true);
		this.tableViewer.setContentProvider(new ControlEventInputWrapper());
		this.tableViewer.setLabelProvider(new ControlEventLabelProvider());
	}

	private void createColumns(TableViewer viewer) {
		// column 1: Source
		TableViewerColumn sourceCol = new TableViewerColumn(viewer, SWT.LEFT);
		sourceCol.getColumn().setText("Source");
		sourceCol.getColumn().setWidth(280);
		
		// column 2: Operator
		TableViewerColumn operatorCol = new TableViewerColumn(viewer, SWT.LEFT);
		operatorCol.getColumn().setText("Operator");
		operatorCol.getColumn().setWidth(80);
		
		// column 3: Limit
		TableViewerColumn limitCol = new TableViewerColumn(viewer, SWT.LEFT);
		limitCol.getColumn().setText("Limit");
		switch (this.eventType) {
		case CONTROL_EVENT:
			limitCol.getColumn().setWidth(100);
			break;
		case PAUSE_EVENT:
			limitCol.getColumn().setWidth(60);
			// column 4: CIF (Continue if false), only for Pause Events
			TableViewerColumn cifCol = new TableViewerColumn(viewer, SWT.LEFT);
			cifCol.getColumn().setText("CIF");
			cifCol.getColumn().setWidth(40);
			break;
		}
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
										getScanModule().getChain().
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
		//eventsCombo.setItems(eventIDs);

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
		
		if(this.controlEventManager != null) {
			this.controlEventManager.addModelUpdateListener(this);

		} else { // controlEventManager == null
			this.tableViewer.getTable().clearAll();
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
			final String currentTextBuffer = eventsCombo.getText();
			setEventChoice();
			eventsCombo.setText(currentTextBuffer);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost (FocusEvent e) {
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
											getScanModule().getChain().
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