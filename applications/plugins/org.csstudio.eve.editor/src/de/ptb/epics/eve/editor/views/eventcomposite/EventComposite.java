package de.ptb.epics.eve.editor.views.eventcomposite;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;

import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.editor.Activator;

/**
 * <code>EventComposite</code>.
 * 
 * @author ?
 * @author Marcus Michalsky
 * @author Hartmut Scherr
 */
public class EventComposite extends Composite {

	private static Logger logger = 
		Logger.getLogger(EventComposite.class.getName());

	private IViewPart parentView;
	
	private String[] eventIDs;

	private ControlEventTypes eventType;
	private ControlEventManager controlEventManager;

	private TableViewer tableViewer;

	/**
	 * Constructs an <code>EventComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 */
	public EventComposite(final Composite parent, final int style, 
			final ControlEventTypes eventType, final IViewPart parentView) {
		super(parent, style);
		
		this.setLayout(new GridLayout());
		
		this.eventType = eventType;	
		this.parentView = parentView;
		
		createViewer();
		
		/*
			editors[2] = new TextCellEditor(this.tableViewer.getTable()) {
				@Override protected void focusLost() {
					// if value not changed, cancel focusLost callback
					if(isActivated() && !isDirty()) {
						fireCancelEditor();
					}
					deactivate();
				}};

		*/
	} // end of: Constructor

	private void createViewer() {
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
		this.tableViewer.setContentProvider(new ContentProvider());
		this.tableViewer.setLabelProvider(new LabelProvider());
		this.tableViewer.setInput(null);
		
		// create context menu
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.tableViewer.getTable().setMenu(
				menuManager.createContextMenu(this.tableViewer.getTable()));
		// register menu
		parentView.getSite().registerContextMenu(
				"de.ptb.epics.eve.editor.views.eventcomposite.tablepopup", 
				menuManager, this.tableViewer);
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
		operatorCol.setEditingSupport(
				new OperatorEditingSupport(this.tableViewer));
		
		// column 3: Limit
		TableViewerColumn limitCol = new TableViewerColumn(viewer, SWT.LEFT);
		limitCol.getColumn().setText("Limit");
		limitCol.setEditingSupport(new LimitEditingSupport(this.tableViewer));
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
			cifCol.setEditingSupport(new CifEditingSupport(this.tableViewer));
			break;
		}
	}

	/**
	 * TODO
	 */
	private void setEventChoice() {
		Event[] measuringStationEvents = Activator.getDefault().
											getMeasuringStation().getEvents().
											toArray(new Event[0]);
		Event[] scanDescriptionEvents = null;

		if(controlEventManager.getParentChain() != null) {
			scanDescriptionEvents = controlEventManager.getParentChain().
										getScanDescription().getEvents().
										toArray(new Event[0]);
		} else if(controlEventManager.getParentScanModule() != null) {
			scanDescriptionEvents = controlEventManager.getParentScanModule().
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
						//eventsCombo.remove(scanDescriptionEvents[
					//	       i-measuringStationEvents.length].getNameID());
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
		
		this.controlEventManager = controlEventManager;
		
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
	 * 
	 * @return
	 */
	public TableViewer getTableViewer() {
		return this.tableViewer;
	}
}