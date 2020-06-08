package de.ptb.epics.eve.editor.views.axeschannelsview.ui;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;
import de.ptb.epics.eve.editor.views.AbstractScanModuleViewComposite;
import de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.ClassicComposite;
import de.ptb.epics.eve.util.ui.jface.SelectionProviderWrapper;

/**
 * Listens to Scan Module selections showing different content depending on its 
 * type. For snapshot types tables containing the snapshot devices are shown. For 
 * non-dynamic snapshots these tables are editable. If a scan module of type 
 * classic is selected an axes and channels table are shown. Devices can be added
 * and removed and attributes of them can be edited.
 * 
 * @author Marcus Michalsky
 * @since 1.34
 */
public class AxesChannelsView extends AbstractScanModuleView {
	public static final String ID = 
			"de.ptb.epics.eve.editor.views.AxesChannelsView";
	
	private ScanModule currentScanModule;

	private Composite emptyComposite;
	private AbstractScanModuleViewComposite classicComposite;
	private AbstractScanModuleViewComposite saveAxisPositionsComposite;
	private AbstractScanModuleViewComposite saveChannelValuesComposite;
	private AbstractScanModuleViewComposite dynamicAxisPositionsComposite;
	private AbstractScanModuleViewComposite dynamicChannelValuesComposite;
	
	// the selection service only accepts one selection provider per view,
	// since we have multiple tabs with tables capable of providing selections,
	// a wrapper handles them and registers the active one with the global
	// selection service
	protected SelectionProviderWrapper selectionProviderWrapper;

	private EditorViewPerspectiveListener editorViewPerspectiveListener;

	private IMemento memento;

	Composite contentPanel;
	StackLayout stackLayout;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
		this.memento = memento;
	}
	
	@Override
	public void createPartControl(Composite parent) {
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
		this.classicComposite = new ClassicComposite(
				this, contentPanel, SWT.NONE);
		this.saveAxisPositionsComposite = new SaveAxisPositionsComposite(
				this, contentPanel, SWT.NONE);
		this.saveChannelValuesComposite = new SaveChannelValuesComposite(
				this, contentPanel, SWT.NONE);
		this.dynamicAxisPositionsComposite = new DynamicAxisPositionsComposite(
				this, contentPanel, SWT.NONE);
		this.dynamicChannelValuesComposite = new DynamicChannelValuesComposite(
				this, contentPanel, SWT.NONE);
		stackLayout.topControl = emptyComposite;
		
		this.restoreState();
		
		getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);
		
		selectionProviderWrapper = new SelectionProviderWrapper();
		getSite().setSelectionProvider(selectionProviderWrapper);

		// reset view if last editor was closed
		this.editorViewPerspectiveListener = new EditorViewPerspectiveListener(
				this);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.addPerspectiveListener(this.editorViewPerspectiveListener);
		
		this.setScanModule(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.contentPanel.setFocus();
	}

	/**
	 * Returns the currently active scan module.
	 * 
	 * @return the scan module
	 */
	public ScanModule getCurrentScanModule() {
		return currentScanModule;
	}
	
	protected void setScanModule(ScanModule scanModule) {
		this.currentScanModule = scanModule;
		if (this.currentScanModule != null) {

			this.setPartName("SM Axes / Channels: " + 
					this.currentScanModule.getName() + 
					" (Id: " + this.currentScanModule.getId() + ")");
			
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
		} else {
			// no scan module selected -> reset contents
			this.setPartName("SM Axes / Channels: No Scan Module selected");
			stackLayout.topControl = this.emptyComposite;
		}
		contentPanel.layout();
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
	public void saveState(IMemento memento) {
		this.classicComposite.saveState(memento);
		this.saveAxisPositionsComposite.saveState(memento);
		this.saveChannelValuesComposite.saveState(memento);
		this.dynamicAxisPositionsComposite.saveState(memento);
		this.dynamicChannelValuesComposite.saveState(memento);
	}
	
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
		this.setScanModule(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScanModule getScanModule() {
		return this.currentScanModule;
	}
}
