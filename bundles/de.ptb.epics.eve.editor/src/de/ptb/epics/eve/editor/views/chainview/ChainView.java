package de.ptb.epics.eve.editor.views.chainview;

import org.apache.log4j.Logger;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.EventImpacts;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.updatenotification.ControlEventTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.gef.editparts.StartEventEditPart;
import de.ptb.epics.eve.editor.gef.editparts.tree.ScanModuleTreeEditPart;
import de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener;
import de.ptb.epics.eve.editor.views.IEditorView;
import de.ptb.epics.eve.editor.views.eventcomposite.EventComposite;
import de.ptb.epics.eve.util.ui.jface.SelectionProviderWrapper;

/**
 * @author Marcus Michalsky
 * @since 1.12
 */
public class ChainView extends ViewPart implements IEditorView,
		ISelectionListener, IModelUpdateListener {
	/** the unique identifier of the view */
	public static final String ID = "de.ptb.epics.eve.editor.views.ChainView";

	private static Logger logger = Logger.getLogger(ChainView.class.getName());
	
	public ChainView() {
	}
	
	private Chain currentChain;

	private Composite top;

	public CTabFolder eventsTabFolder;
	private CTabItem pauseTabItem;
	private EventComposite pauseEventComposite;
	private CTabItem redoTabItem;
	private EventComposite redoEventComposite;
	private CTabItem breakTabItem;
	private EventComposite breakEventComposite;
	private CTabItem stopTabItem;
	private EventComposite stopEventComposite;

	private Image eventErrorImage;

	// Delegates
	private EditorViewPerspectiveListener perspectiveListener;
	private SelectionProviderWrapper selectionProviderWrapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(Composite parent) {
		logger.debug("createPartControl");

		parent.setLayout(new FillLayout());

		// if no measuring station is loaded -> show error and do nothing
		if (Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. "
					+ "Please check Preferences!");
			return;
		}

		this.eventErrorImage = PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);

		// top composite
		this.top = new Composite(parent, SWT.NONE);
		this.top.setLayout(new GridLayout());

		// expand bar for Save Options, Comment & Events
		ExpandBar bar = new ExpandBar(this.top, SWT.V_SCROLL);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		bar.setLayoutData(gridData);

		Composite eventsComposite = new Composite(bar, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		eventsComposite.setLayout(gridLayout);

		this.eventsTabFolder = new CTabFolder(eventsComposite, SWT.FLAT);
		this.eventsTabFolder.setSimple(true);
		this.eventsTabFolder.setBorderVisible(true);
		this.eventsTabFolder
				.addSelectionListener(new EventsTabFolderSelectionListener());

		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.minimumHeight = 150;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.eventsTabFolder.setLayoutData(gridData);
		
		pauseEventComposite = new EventComposite(eventsTabFolder, SWT.NONE,
				ControlEventTypes.PAUSE_EVENT, this);
		redoEventComposite = new EventComposite(eventsTabFolder, SWT.NONE,
				ControlEventTypes.CONTROL_EVENT, this);
		breakEventComposite = new EventComposite(eventsTabFolder, SWT.NONE,
				ControlEventTypes.CONTROL_EVENT, this);
		stopEventComposite = new EventComposite(eventsTabFolder, SWT.NONE,
				ControlEventTypes.CONTROL_EVENT, this);

		this.pauseTabItem = new CTabItem(eventsTabFolder, SWT.FLAT);
		this.pauseTabItem.setText(" Pause ");
		this.pauseTabItem.setControl(pauseEventComposite);
		this.pauseTabItem.setToolTipText("Event to pause an resume this scan");
		this.redoTabItem = new CTabItem(eventsTabFolder, SWT.FLAT);
		this.redoTabItem.setText(" Redo ");
		this.redoTabItem.setControl(redoEventComposite);
		this.redoTabItem
				.setToolTipText("Repeat the current scan point, if redo event occurs");
		this.breakTabItem = new CTabItem(eventsTabFolder, SWT.FLAT);
		this.breakTabItem.setText(" Skip ");
		this.breakTabItem.setControl(breakEventComposite);
		this.breakTabItem
				.setToolTipText("Finish the current scan module and continue with next");
		this.stopTabItem = new CTabItem(eventsTabFolder, SWT.FLAT);
		this.stopTabItem.setText(" Stop ");
		this.stopTabItem.setControl(stopEventComposite);
		this.stopTabItem.setToolTipText("Stop this scan");

		ExpandItem eventsExpandItem = new ExpandItem(bar, SWT.NONE, 0);
		eventsExpandItem.setText("Events");
		eventsExpandItem.setControl(eventsComposite);
		eventsExpandItem.setExpanded(true);
		eventsExpandItem.setHeight(eventsComposite.computeSize(
				SWT.DEFAULT, SWT.DEFAULT).y);

		this.eventsTabFolder.showItem(this.pauseTabItem);
		
		top.setVisible(false);

		// the selection service only accepts one selection provider per view,
		// since we have four tables capable of providing selections a wrapper
		// handles them and registers the active one with the global selection
		// service
		selectionProviderWrapper = new SelectionProviderWrapper();
		getSite().setSelectionProvider(selectionProviderWrapper);

		// listen to selection changes (if a chain (or one of its scan modules)
		// is selected, its attributes are made available for editing)
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this);

		// listen to "last editor closed" to reset the view.
		perspectiveListener = new EditorViewPerspectiveListener(this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.addPerspectiveListener(perspectiveListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		logger.debug("Focus gained -> forward to top composite");
		this.top.setFocus();
	}

	/**
	 * Sets the current {@link de.ptb.epics.eve.data.scandescription.Chain} (the
	 * underlying model whose contents is presented by this view).
	 * 
	 * @param currentChain
	 *            the {@link de.ptb.epics.eve.data.scandescription.Chain} that
	 *            should be set current. Use <code>null</code> to present an
	 *            empty view.
	 */
	private void setCurrentChain(final Chain currentChain) {
		logger.debug("setCurrentChain");
		if (this.currentChain != null) {
			this.currentChain.removeModelUpdateListener(this);
		}
		// set the new chain as current chain
		this.currentChain = currentChain;
		if (this.currentChain != null) {
			this.currentChain.addModelUpdateListener(this);
		}
		updateEvent(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		this.setCurrentChain(null);
	}

	/**
	 * 
	 * @return
	 */
	public Chain getCurrentChain() {
		return this.currentChain;
	}

	/*
	 * called by setCurrentChain() to check for errors in user input and show
	 * error decorators.
	 */
	private void checkForErrors() {
		// reset all
		this.pauseTabItem.setImage(null);
		this.breakTabItem.setImage(null);
		this.redoTabItem.setImage(null);
		this.stopTabItem.setImage(null);

		for (ControlEvent event : this.currentChain.getPauseEvents()) {
			if (!event.getModelErrors().isEmpty()) {
				this.pauseTabItem.setImage(eventErrorImage);
			}
		}
		
		for (ControlEvent event : this.currentChain.getBreakEvents()) {
			if (!event.getModelErrors().isEmpty()) {
				this.breakTabItem.setImage(eventErrorImage);
			}
		}
		
		for (ControlEvent event : this.currentChain.getRedoEvents()) {
			if (!event.getModelErrors().isEmpty()) {
				this.redoTabItem.setImage(eventErrorImage);
			}
		}
		
		for (ControlEvent event : this.currentChain.getStopEvents()) {
			if (!event.getModelErrors().isEmpty()) {
				this.stopTabItem.setImage(eventErrorImage);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		logger.debug("selection changed");

		if (selection instanceof IStructuredSelection) {
			if (((IStructuredSelection) selection).size() == 0) {
				return;
			}
		}
		// since at any given time this view can only display options of
		// one device we take the first element of the selection
		Object o = ((IStructuredSelection) selection).toList().get(0);
		if (o instanceof ScanModuleEditPart || o instanceof ScanModuleTreeEditPart) {
			// a scan module belongs to a chain -> show chain
			if (logger.isDebugEnabled()) {
				logger.debug("ScanModule "
						+ ((ScanModule)((EditPart)o).getModel()) + " selected.");
			}
			setCurrentChain(((ScanModule)((EditPart)o).getModel()).getChain());
		} else if (o instanceof StartEventEditPart) {
			// a start event belongs to a chain -> show chain
			if (logger.isDebugEnabled()) {
				logger.debug("Chain "
						+ (((StartEventEditPart) o).getModel()).getChain()
						+ " selected.");
			}
			setCurrentChain((((StartEventEditPart) o).getModel()).getChain());
		} else if (o instanceof ChainEditPart) {
			logger.debug("selection is ScanDescriptionEditPart: " + o);
			setCurrentChain(null);
		} else if (o instanceof ScanDescriptionEditPart) {
			logger.debug("selection is ScanDescriptionEditPart: " + o);
			setCurrentChain(null);
		} else {
			logger.debug("selection other than Chain -> ignore: " + o);
		}
	}

	/**
	 * 
	 * @param selectionProvider
	 */
	public void setSelectionProvider(ISelectionProvider selectionProvider) {
		this.selectionProviderWrapper.setSelectionProvider(selectionProvider);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		if (this.currentChain != null) {
			this.top.setVisible(true);
			if (this.eventsTabFolder.getSelection() == null) {
				this.eventsTabFolder.setSelection(this.pauseTabItem);
			}
			this.setPartName("Chain: " + this.currentChain.getId());
			
			this.pauseEventComposite.setEvents(this.currentChain,
					EventImpacts.PAUSE);
			this.redoEventComposite.setEvents(this.currentChain,
					EventImpacts.REDO);
			this.breakEventComposite.setEvents(this.currentChain,
					EventImpacts.BREAK);
			this.stopEventComposite.setEvents(this.currentChain,
					EventImpacts.STOP);
			
			checkForErrors();
		} else { // currentChain == null
			this.pauseEventComposite.setEvents(this.currentChain, null);
			this.redoEventComposite.setEvents(this.currentChain, null);
			this.breakEventComposite.setEvents(this.currentChain, null);
			this.stopEventComposite.setEvents(this.currentChain, null);

			this.setPartName("No Chain selected");
			this.top.setVisible(false);
		}
	}

	/**
	 * {@link org.eclipse.swt.events.SelectionListener} of
	 * <code>eventsTabFolder</code>.
	 * 
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class EventsTabFolderSelectionListener extends SelectionAdapter {

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			selectionProviderWrapper
					.setSelectionProvider(((EventComposite) eventsTabFolder
							.getSelection().getControl()).getTableViewer());
		}
	}
}