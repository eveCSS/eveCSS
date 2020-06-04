package de.ptb.epics.eve.editor.views.prepostposplotview.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.ui.ClassicComposite;

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
		
		getSite().getWorkbenchWindow().getSelectionService().
				addSelectionListener(this);
		// TODO Auto-generated method stub
		this.setScanModule(null);
	}
	
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	
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
			this.setPartName("SM Prescan / Postscan / Positioning / Plot: No Scan Module selected");
		}
		contentPanel.layout();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScanModule getScanModule() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		this.setScanModule(null);
	}
}
