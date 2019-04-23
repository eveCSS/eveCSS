package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.apache.log4j.Logger;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.StackLayout;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.gef.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.gef.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.gef.editparts.tree.ScanModuleTreeEditPart;
import de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener;
import de.ptb.epics.eve.editor.views.IEditorView;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.ClassicComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.dynamicaxispositionscomposite.DynamicAxisPositionsComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.dynamicchannelvaluescomposite.DynamicChannelValuesComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.saveaxispositionscomposite.SaveAxisPositionsComposite;
import de.ptb.epics.eve.editor.views.scanmoduleview.savechannelvaluescomposite.SaveChannelValuesComposite;
import de.ptb.epics.eve.util.ui.jface.SelectionProviderWrapper;

/**
 * <code>ScanModulView</code> shows the currently selected scan module.
 * 
 * @author Marcus Michalsky
 */
public class ScanModuleView extends ViewPart implements IEditorView,
		ISelectionListener, IModelUpdateListener {
	public static final String ID = "de.ptb.epics.eve.editor.views.ScanModulView";
	private static Logger logger = Logger.getLogger(ScanModuleView.class);

	// the scan module currently represented by this view
	private ScanModule currentScanModule;

	private Composite emptyComposite;
	private ScanModuleViewComposite classicComposite;
	private ScanModuleViewComposite saveAxisPositionsComposite;
	private ScanModuleViewComposite saveChannelValuesComposite;
	private ScanModuleViewComposite dynamicAxisPositionsComposite;
	private ScanModuleViewComposite dynamicChannelValuesComposite;

	// the selection service only accepts one selection provider per view,
	// since we have multiple tabs with tables capable of providing selections,
	// a wrapper handles them and registers the active one with the global
	// selection service
	protected SelectionProviderWrapper selectionProviderWrapper;

	private EditorViewPerspectiveListener editorViewPerspectiveListener;

	private IMemento memento;

	Composite contentPanel;
	StackLayout stackLayout;
	
	Button button1;
	Button button2;
	Button button3;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
		this.memento = memento;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		logger.debug("createPartControl");

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.verticalAlignment = SWT.FILL;
		parent.setLayoutData(gridData);
		parent.setLayout(new GridLayout());
		
		if (Activator.getDefault().getMeasuringStation() == null) {
			final Label errorLabel = new Label(parent, SWT.NONE);
			errorLabel.setText("No Measuring Station has been loaded. "
					+ "Please check Preferences!");
			return;
		}
		
		contentPanel = new Composite(parent, SWT.NONE);
		contentPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		stackLayout = new StackLayout();
		stackLayout.marginHeight = 0;
		stackLayout.marginWidth = 0;
		contentPanel.setLayout(stackLayout);
		this.emptyComposite = new Composite(contentPanel, SWT.NONE);
		this.classicComposite = new ClassicComposite(this, contentPanel, SWT.NONE);
		this.saveAxisPositionsComposite = new SaveAxisPositionsComposite(this, contentPanel, SWT.NONE);
		this.saveChannelValuesComposite = new SaveChannelValuesComposite(this, contentPanel, SWT.NONE);
		this.dynamicAxisPositionsComposite = new DynamicAxisPositionsComposite(this, contentPanel, SWT.NONE);
		this.dynamicChannelValuesComposite = new DynamicChannelValuesComposite(this, contentPanel, SWT.NONE);
		stackLayout.topControl = emptyComposite;

		this.restoreState();

		// listen to selection changes (if a scan module is selected, its
		// attributes are made available for editing)
		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this);

		selectionProviderWrapper = new SelectionProviderWrapper();
		getSite().setSelectionProvider(selectionProviderWrapper);

		// reset view if last editor was closed
		this.editorViewPerspectiveListener = new EditorViewPerspectiveListener(
				this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.addPerspectiveListener(this.editorViewPerspectiveListener);
		
		this.setCurrentScanModule(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		logger.debug("focus gained -> forward to top composite");
		this.contentPanel.setFocus();
	}

	/**
	 * Introduced to "fix" code smell of EventMenuContributionHelper (which 
	 * accessed the public attribute directly before).
	 * @return the selection index of the events tab folder in the classic composite or -1
	 * @since 1.31
	 */
	public int getEventsTabFolderSelectionIndex() {
		if (!this.currentScanModule.getType().equals(ScanModuleTypes.CLASSIC)) {
			return -1;
		}
		return ((ClassicComposite) this.classicComposite).
				getEventsTabFolderSelectionIndex();
	}
	
	/**
	 * Returns the currently active scan module.
	 * 
	 * @return the scan module
	 */
	public ScanModule getCurrentScanModule() {
		return currentScanModule;
	}

	/*
	 * Sets the currently active scan module. Called by selectionChanged if the
	 * current selection is of interest.
	 */
	private void setCurrentScanModule(ScanModule currentScanModule) {
		logger.debug("setCurrentScanModule");

		// if there was a scan module shown before, stop listening to changes
		if (this.currentScanModule != null) {
			this.currentScanModule.removeModelUpdateListener(this);
		}

		// set the new scan module as the current one
		this.currentScanModule = currentScanModule;

		
		if (this.currentScanModule != null) {
			// new scan module
			this.currentScanModule.addModelUpdateListener(this);

			this.setPartName("Scan Module: " + this.currentScanModule.getName() 
					+ " (Id: " + this.currentScanModule.getId() + ")");

			switch (this.currentScanModule.getType()) {
			case CLASSIC:
				stackLayout.topControl = this.classicComposite;
				break;
			case DYNAMIC_AXIS_POSITIONS:
				stackLayout.topControl = this.dynamicAxisPositionsComposite;
				break;
			case DYNAMIC_CHANNEL_VALUES:
				stackLayout.topControl = this.dynamicChannelValuesComposite;
				break;
			case SAVE_AXIS_POSITIONS:
				stackLayout.topControl = this.saveAxisPositionsComposite;
				break;
			case SAVE_CHANNEL_VALUES:
				stackLayout.topControl = this.saveChannelValuesComposite;
				break;
			default:
				stackLayout.topControl = this.emptyComposite;
				break;
			}
			contentPanel.layout();
			
		} else {
			// no scan module selected -> reset contents
			selectionProviderWrapper.setSelectionProvider(null);
			this.setPartName("No Scan Module selected");
			stackLayout.topControl = this.emptyComposite;
			contentPanel.layout();
		}
		updateEvent(null);
	}

	/**
	 * Sets the selection provider.
	 * 
	 * @param selectionProvider
	 *            the selection provider that should be set
	 */
	public void setSelectionProvider(ISelectionProvider selectionProvider) {
		this.selectionProviderWrapper.setSelectionProvider(selectionProvider);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		logger.debug("selection changed");
		if (!(selection instanceof IStructuredSelection)
				|| ((IStructuredSelection) selection).size() == 0) {
			return;
		}
		// at any given time this view can only display options of
		// one device we take the last (see org.eclipse.gef.SelectionMananger,
		// primary selection)
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object o = structuredSelection.toList().get(
				structuredSelection.size() - 1);
		if (o instanceof ScanModuleEditPart || o instanceof ScanModuleTreeEditPart) {
			// set new ScanModule
			if (logger.isDebugEnabled()) {
				logger.debug("ScanModule: "
						+ ((ScanModule)((EditPart)o).getModel()).getId()
						+ " selected.");
			}
			setCurrentScanModule((ScanModule)((EditPart)o).getModel());
		} else if (o instanceof ChainEditPart) {
			// clicking empty space in the editor
			logger.debug("selection is ChainEditPart: " + o);
			setCurrentScanModule(null);
		} else if (o instanceof ScanDescriptionEditPart) {
			logger.debug("selection is ScanDescriptionEditPart: " + o);
			setCurrentScanModule(null);
		} else {
			logger.debug("selection other than ScanModule -> ignore: " + o);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState(IMemento memento) {
		this.classicComposite.saveState(memento);
		this.saveAxisPositionsComposite.saveState(memento);
		this.saveChannelValuesComposite.saveState(memento);
		this.dynamicAxisPositionsComposite.saveState(memento);
		this.dynamicChannelValuesComposite.saveState(memento);
	}

	/*
	 * 
	 */
	private void restoreState() {
		if (memento == null) {
			return;
		}
		this.classicComposite.restoreState(memento);
		this.saveAxisPositionsComposite.restoreState(memento);
		this.saveChannelValuesComposite.restoreState(memento);
		this.dynamicAxisPositionsComposite.restoreState(memento);
		this.dynamicChannelValuesComposite.restoreState(memento);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		// TODO call setSM(null) on SMViewComposites ?
		this.setCurrentScanModule(null);
		// TODO call setSM(null) on SMViewComposites ?
	}
}