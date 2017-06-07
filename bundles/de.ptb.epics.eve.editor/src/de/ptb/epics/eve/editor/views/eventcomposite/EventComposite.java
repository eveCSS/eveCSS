package de.ptb.epics.eve.editor.views.eventcomposite;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

import de.ptb.epics.eve.data.EventImpacts;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.channelmode.StandardMode;
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
public class EventComposite extends Composite implements PropertyChangeListener {
	private static Logger logger = 
		Logger.getLogger(EventComposite.class.getName());

	private IViewPart parentView;

	private ControlEventTypes eventType;

	private TableViewer tableViewer;
	
	private TableViewerFocusListener tableViewerFocusListener;

	private Chain chain;
	private ScanModule scanModule;
	private Channel channel;
	
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
		
		this.chain = null;
		this.scanModule = null;
		this.chain = null;
		
		createViewer();
	} // end of: Constructor

	/*
	 * 
	 */
	private void createViewer() {
		this.tableViewer = new TableViewer(this, SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData();
		//gridData.minimumHeight = 50;
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
	 * 
	 * @param chain
	 * @param type
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public void setEvents(Chain chain, EventImpacts type) {
		if (chain == null) {
			if (this.chain == null) {
				return;
			}
			this.chain.removePropertyChangeListener(Chain.BREAK_EVENT_PROP, this);
			this.chain.removePropertyChangeListener(Chain.PAUSE_EVENT_PROP, this);
			this.chain.removePropertyChangeListener(Chain.REDO_EVENT_PROP, this);
			this.chain.removePropertyChangeListener(Chain.STOP_EVENT_PROP, this);
			this.tableViewer.setInput(null);
			this.chain = null;
			return;
		}
		this.chain = chain;
		switch(type) {
		case BREAK:
			this.tableViewer.setInput(chain.getBreakEvents());
			chain.addPropertyChangeListener(Chain.BREAK_EVENT_PROP, this);
			break;
		case PAUSE:
			this.tableViewer.setInput(chain.getPauseEvents());
			chain.addPropertyChangeListener(Chain.PAUSE_EVENT_PROP, this);
			break;
		case REDO:
			this.tableViewer.setInput(chain.getRedoEvents());
			chain.addPropertyChangeListener(Chain.REDO_EVENT_PROP, this);
			break;
		case STOP:
			this.tableViewer.setInput(chain.getStopEvents());
			chain.addPropertyChangeListener(Chain.STOP_EVENT_PROP, this);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Sets events of certain type for the scan module.
	 * 
	 * @param scanModule the scan module
	 * @param type the event type (pass <code>null</code> to reset)
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public void setEvents(ScanModule scanModule, EventImpacts type) {
		if (scanModule == null) {
			if (this.scanModule == null) {
				return;
			}
			this.scanModule.removePropertyChangeListener(
					ScanModule.BREAK_EVENT_PROP, this);
			this.scanModule.removePropertyChangeListener(
					ScanModule.PAUSE_EVENT_PROP, this);
			this.scanModule.removePropertyChangeListener(
					ScanModule.REDO_EVENT_PROP, this);
			this.scanModule.removePropertyChangeListener(
					ScanModule.TRIGGER_EVENT_PROP, this);
			this.tableViewer.setInput(null);
			this.scanModule = null;
			return;
		}
		this.scanModule = scanModule;
		switch(type) {
		case BREAK:
			this.tableViewer.setInput(scanModule.getBreakEvents());
			scanModule.addPropertyChangeListener(ScanModule.BREAK_EVENT_PROP,
					this);
			break;
		case PAUSE:
			this.tableViewer.setInput(scanModule.getPauseEvents());
			scanModule.addPropertyChangeListener(ScanModule.PAUSE_EVENT_PROP,
					this);
			break;
		case REDO:
			this.tableViewer.setInput(scanModule.getRedoEvents());
			scanModule.addPropertyChangeListener(ScanModule.REDO_EVENT_PROP,
					this);
			break;
		case TRIGGER:
			this.tableViewer.setInput(scanModule.getTriggerEvents());
			scanModule.addPropertyChangeListener(ScanModule.TRIGGER_EVENT_PROP,
					this);
			break;
		default:
			break;
		}
		/*
		if (this.isVisible()) {
			((ScanModuleView)parentView).setSelectionProvider(tableViewer);
		}*/
	}
	/**
	 * Sets events of certain type for the channel.
	 * 
	 * @param channel the channel
	 * @param type the event type (pass <code>null</code> to reset)
	 * @author Marcus Michalsky
	 * @since 1.19
	 */
	public void setEvents(Channel channel, EventImpacts type) {
		if (channel == null) {
			if (this.channel == null) {
				return;
			}
			this.channel.removePropertyChangeListener(StandardMode.REDO_EVENT_PROP, this);
			this.tableViewer.setInput(null);
			this.channel = null;
			return;
		}
		this.channel = channel;
		switch(type) {
		case REDO:
			this.tableViewer.setInput(channel.getRedoEvents());
			channel.addPropertyChangeListener(StandardMode.REDO_EVENT_PROP, this);
			break;
		default:
			break;
		}
	}
	
	/**
	 * Returns the table viewer.
	 * 
	 * @return the table viewer
	 */
	public TableViewer getTableViewer() {
		return this.tableViewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		this.tableViewer.refresh();
	}

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