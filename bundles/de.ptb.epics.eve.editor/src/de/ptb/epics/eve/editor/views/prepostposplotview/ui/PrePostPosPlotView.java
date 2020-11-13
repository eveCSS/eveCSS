package de.ptb.epics.eve.editor.views.prepostposplotview.ui;

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
import de.ptb.epics.eve.editor.views.EditorViewPerspectiveListener;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.ui.ClassicComposite;
import de.ptb.epics.eve.util.ui.jface.SelectionProviderWrapper;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class PrePostPosPlotView extends AbstractScanModuleView {
	public static final String ID = 
			"de.ptb.epics.eve.editor.view.PrePostscanView";

	private ScanModule currentScanModule;
	
	private Composite contentPanel;
	private StackLayout stackLayout;
	
	private Composite emptyComposite;
	private Composite snapshotComposite;
	private ClassicComposite classicComposite;
	
	protected SelectionProviderWrapper selectionProviderWrapper;
	
	private IMemento memento;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site);
		this.memento = memento;
	}
	
	/**
	 * {@inheritDoc}
	 */
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
		
		this.contentPanel = new Composite(parent, SWT.NONE);
		this.contentPanel.setLayoutData(new GridData(GridData.FILL_BOTH));
		stackLayout = new StackLayout();
		stackLayout.marginHeight = 0;
		stackLayout.marginWidth = 0;
		contentPanel.setLayout(stackLayout);
		
		this.emptyComposite = new Composite(contentPanel, SWT.NONE);
		this.snapshotComposite = new SnapshotComposite(contentPanel, SWT.NONE);
		this.classicComposite = new ClassicComposite(this, contentPanel, SWT.NONE);
		this.stackLayout.topControl = this.emptyComposite;
		
		this.restoreState();
		
		getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);
		
		this.selectionProviderWrapper = new SelectionProviderWrapper();
		getSite().setSelectionProvider(selectionProviderWrapper);
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().
			addPerspectiveListener(new EditorViewPerspectiveListener(this));
		
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
	 * {@inheritDoc}
	 */
	@Override
	protected void setScanModule(ScanModule scanModule) {
		this.currentScanModule = scanModule;
		if (this.currentScanModule != null) {
			this.setPartName("SM Prescan / Postscan / Positioning / Plot: " + 
					this.currentScanModule.getName() + 
					" (Id: " + this.currentScanModule.getId() + ")");
			
			switch (this.currentScanModule.getType()) {
			case CLASSIC:
				this.stackLayout.topControl = this.classicComposite;
				break;
			case DYNAMIC_AXIS_POSITIONS:
			case DYNAMIC_CHANNEL_VALUES:
			case SAVE_AXIS_POSITIONS:
			case SAVE_CHANNEL_VALUES:
				this.stackLayout.topControl = this.snapshotComposite;
				break;
			default:
				this.stackLayout.topControl = this.emptyComposite;
				break;
			}
		} else {
			// no scan module selected -> reset contents
			this.setPartName("SM Prescan / Postscan / Positioning / Plot: " + 
					"No Scan Module selected");
			this.stackLayout.topControl = this.emptyComposite;
		}
		contentPanel.layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScanModule getScanModule() {
		return this.currentScanModule;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		this.setScanModule(null);
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
	}
	
	private void restoreState() {
		if (memento == null) {
			return;
		}
		this.classicComposite.restoreState(this.memento);
	}
}
