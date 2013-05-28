package de.ptb.epics.eve.editor.views.eventcomposite;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;

import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventManager;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.editor.views.DelColumnEditingSupport;
import de.ptb.epics.eve.editor.views.chainview.ChainView;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

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

	private ControlEventTypes eventType;
	private ControlEventManager controlEventManager;

	private TableViewer tableViewer;
	
	private TableViewerFocusListener tableViewerFocusListener;

	/**
	 * Constructs an <code>EventComposite</code>.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param eventType control or pause event
	 * @param parentView the parent view
	 */
	public EventComposite(final Composite parent, final int style, 
			final ControlEventTypes eventType, final IViewPart parentView) {
		super(parent, style);
		
		this.setLayout(new GridLayout());
		
		this.eventType = eventType;	
		this.parentView = parentView;
		
		createViewer();
	} // end of: Constructor

	/*
	 * 
	 */
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
		
		this.tableViewerFocusListener = new TableViewerFocusListener();
		this.tableViewer.getTable().addFocusListener(tableViewerFocusListener);
		
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

	/*
	 * 
	 */
	private void createColumns(TableViewer viewer) {
		TableViewerColumn delColumn = new TableViewerColumn(viewer, SWT.CENTER);
		delColumn.getColumn().setText("");
		delColumn.getColumn().setWidth(22);
		delColumn.setEditingSupport(new DelColumnEditingSupport(viewer,
				"de.ptb.epics.eve.editor.command.removeevent"));
		
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
			cifCol.getColumn().setText("Action");
			cifCol.getColumn().setWidth(40);
			cifCol.setEditingSupport(new PauseEditingSupport(this.tableViewer));
			break;
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
		
		if (this.isVisible() && this.parentView instanceof ScanModuleView) {
			((ScanModuleView)parentView).setSelectionProvider(tableViewer);
		}
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
	 * Returns the table viewer.
	 * 
	 * @return the table viewer
	 */
	public TableViewer getTableViewer() {
		return this.tableViewer;
	}
	
	/* ********************************************************************* */
	
	/**
	 * 
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class TableViewerFocusListener implements FocusListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusGained(FocusEvent e) {
			if (parentView instanceof ScanModuleView) {
				((ScanModuleView)parentView).setSelectionProvider(tableViewer);
			} else if (parentView instanceof ChainView) {
				((ChainView)parentView).setSelectionProvider(tableViewer);
			}
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void focusLost(FocusEvent e) {
		}
	}
}