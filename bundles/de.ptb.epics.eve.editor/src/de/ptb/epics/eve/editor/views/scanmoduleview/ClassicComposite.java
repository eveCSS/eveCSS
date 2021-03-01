package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.EventImpacts;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.views.AbstractScanModuleViewComposite;
import de.ptb.epics.eve.editor.views.eventcomposite.EventComposite;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public class ClassicComposite extends AbstractScanModuleViewComposite {
	private static final Logger LOGGER = Logger.getLogger(
			ClassicComposite.class.getName());

	private static final String MEMENTO_EVENTS_TAB_FOLDER_SELECTION_INDEX = 
			"eventsTabFolderSelectionIndex";
	
	private ScanModuleView parentView;
	private ScanModule currentScanModule;
	
	ScrolledComposite sc;
	Composite top;
	
	public CTabFolder eventsTabFolder;
	private CTabItem redoEventsTabItem;
	private CTabItem breakEventsTabItem;
	private CTabItem triggerEventsTabItem;
	private EventComposite redoEventComposite;
	private EventComposite breakEventComposite;
	private EventComposite triggerEventComposite;
	
	public ClassicComposite(ScanModuleView parentView, Composite parent, int style) {
		super(parentView, parent, style);
		
		this.parentView = parentView;
		
		this.setLayout(new FillLayout());
		this.sc = new ScrolledComposite(this, SWT.V_SCROLL);
		
		this.top = new Composite(sc, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		this.top.setLayout(gridLayout);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setContent(top);

		this.eventsTabFolder = new CTabFolder(top, SWT.NONE);
		this.eventsTabFolder.setSimple(true);
		this.eventsTabFolder.setBorderVisible(true);
		eventsTabFolder.addSelectionListener(
				new EventsTabFolderSelectionListener());
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 2;
		this.eventsTabFolder.setLayoutData(gridData);
		
		redoEventComposite = new EventComposite(eventsTabFolder, SWT.NONE,
				ControlEventTypes.CONTROL_EVENT, this.parentView);
		breakEventComposite = new EventComposite(eventsTabFolder, SWT.NONE,
				ControlEventTypes.CONTROL_EVENT, this.parentView);
		triggerEventComposite = new EventComposite(eventsTabFolder, SWT.NONE,
				ControlEventTypes.CONTROL_EVENT, this.parentView);

		this.redoEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.redoEventsTabItem.setText("Redo Events");
		this.redoEventsTabItem
				.setToolTipText("Repeat the last acquisition, if redo event occurs");
		this.redoEventsTabItem.setControl(redoEventComposite);
		this.breakEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.breakEventsTabItem.setText("Skip Events");
		this.breakEventsTabItem
				.setToolTipText("Finish this scan module and continue with next");
		this.breakEventsTabItem.setControl(breakEventComposite);
		this.triggerEventsTabItem = new CTabItem(eventsTabFolder, SWT.NONE);
		this.triggerEventsTabItem.setText("Trigger Events");
		this.triggerEventsTabItem
				.setToolTipText("Wait for trigger event before moving to next position");
		this.triggerEventsTabItem.setControl(triggerEventComposite);
		
		this.parentView.getSite().getWorkbenchWindow().getSelectionService().
			addSelectionListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScanModuleTypes getType() {
		return ScanModuleTypes.CLASSIC;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setScanModule(ScanModule scanModule) {
		LOGGER.debug("ClassicComposite#setScanModule: " + scanModule);
		// if there was a scan module shown before, stop listening to changes
		if (this.currentScanModule != null) {
			this.currentScanModule.removeModelUpdateListener(this);
		}
		
		// set the new scan module as the current one
		this.currentScanModule = scanModule;

		if (this.currentScanModule != null) {
			// new scan module
			this.currentScanModule.addModelUpdateListener(this);
			
			if (this.eventsTabFolder.getSelection() == null) {
				this.eventsTabFolder.setSelection(0);
			}
		} else {
			// no scan module selected -> reset contents
			this.parentView.setSelectionProvider(null);
		}
		updateEvent(null);
		this.layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		if (this.currentScanModule != null) {
			this.triggerEventComposite.setEvents(this.currentScanModule,
					EventImpacts.TRIGGER);
			this.breakEventComposite.setEvents(this.currentScanModule,
					EventImpacts.BREAK);
			this.redoEventComposite.setEvents(this.currentScanModule,
					EventImpacts.REDO);
			
			checkForErrors();
		} else {
			triggerEventComposite.setEvents(this.currentScanModule, null);
			breakEventComposite.setEvents(this.currentScanModule, null);
			redoEventComposite.setEvents(this.currentScanModule, null);
		}
	}
	
	private void checkForErrors() {
		this.redoEventsTabItem.setImage(null);
		this.breakEventsTabItem.setImage(null);
		this.triggerEventsTabItem.setImage(null);

		for (ControlEvent event : this.currentScanModule.getRedoEvents()) {
			if (!event.getModelErrors().isEmpty()) {
				this.redoEventsTabItem.setImage(PlatformUI.getWorkbench()
						.getSharedImages()
						.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
			}
		}

		for (ControlEvent event : this.currentScanModule.getBreakEvents()) {
			if (!event.getModelErrors().isEmpty()) {
				this.breakEventsTabItem.setImage(PlatformUI.getWorkbench()
						.getSharedImages()
						.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
			}
		}

		for (ControlEvent event : this.currentScanModule.getTriggerEvents()) {
			if (!event.getModelErrors().isEmpty()) {
				this.triggerEventsTabItem.setImage(PlatformUI.getWorkbench()
						.getSharedImages()
						.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState(IMemento memento) {
		memento.putInteger(MEMENTO_EVENTS_TAB_FOLDER_SELECTION_INDEX, 
				this.eventsTabFolder.getSelectionIndex());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void restoreState(IMemento memento) {
		if (memento.getInteger(MEMENTO_EVENTS_TAB_FOLDER_SELECTION_INDEX) != null) {
			this.eventsTabFolder.setSelection(memento.getInteger(
					MEMENTO_EVENTS_TAB_FOLDER_SELECTION_INDEX));
		}
	}
	
	/**
	 * For Legacy Compatibility of Code Smells from EventMenuContributionHelper
	 * which accessed this (public) attribute directly before (when the classic 
	 * parts were contained directly in the view).
	 * @return the selection index of the events tab folder or -1
	 * @since 1.31
	 */
	public int getEventsTabFolderSelectionIndex() {
		return this.eventsTabFolder.getSelectionIndex();
	}
	
	private class EventsTabFolderSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			switch (eventsTabFolder.getSelectionIndex()) {
			case 0:
				parentView.setSelectionProvider(
						redoEventComposite.getTableViewer());
				break;
			case 1:
				parentView.setSelectionProvider(
						breakEventComposite.getTableViewer());
				break;
			case 2:
				parentView.setSelectionProvider(
						triggerEventComposite.getTableViewer());
				break;
			default:
				break;
			}
		}
	}
}
