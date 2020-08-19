package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.ui;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.views.AbstractScanModuleViewComposite;
import de.ptb.epics.eve.editor.views.prepostposplotview.ui.PrePostPosPlotView;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ClassicComposite extends AbstractScanModuleViewComposite {
	private static final int TABLE_MIN_HEIGHT = 120;
	
	private static final String MEMENTO_PREPOST_SASH_WEIGHT = 
			"prePostSashWeight";
	private static final String MEMENTO_POSITIONING_SASH_WEIGHT = 
			"positioningSashWeight";
	private static final String MEMENTO_PLOT_SASH_WEIGHT = "plotSashWeight";
	
	private IViewPart parentView;
	
	private ScanModule scanModule;

	private SashForm sashForm;
	
	private TableViewer prePostscanTable;
	private TableViewer positioningTable;
	private TableViewer plotTable;
	
	public ClassicComposite(IViewPart parentView, Composite parent, int style) {
		super(parentView, parent, style);
		this.parentView = parentView;
		
		this.setLayout(new FillLayout());
		ScrolledComposite sc = new ScrolledComposite(this, SWT.H_SCROLL | SWT.V_SCROLL);
		
		Composite topComposite = new Composite(sc, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		topComposite.setLayout(gridLayout);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setContent(topComposite);
		
		this.sashForm = new SashForm(topComposite, SWT.VERTICAL);
		this.sashForm.SASH_WIDTH = 4;
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		sashForm.setLayoutData(gridData);
		
		Composite prePostScanComposite = new Composite(sashForm, SWT.BORDER);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		prePostScanComposite.setLayout(gridLayout);
		
		Label prePostScanLabel = new Label(prePostScanComposite, SWT.NONE);
		prePostScanLabel.setText("Prescans / Postscans:");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		prePostScanLabel.setLayoutData(gridData);
		this.createPrePostScanTable(prePostScanComposite);
		
		Composite positioningComposite = new Composite(sashForm, SWT.BORDER);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		positioningComposite.setLayout(gridLayout);
		
		Label positiongLabel = new Label(positioningComposite, SWT.NONE);
		positiongLabel.setText("Positionings:");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		positiongLabel.setLayoutData(gridData);
		this.createPositioningTable(positioningComposite);
		
		Composite plotComposite = new Composite(sashForm, SWT.BORDER);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		plotComposite.setLayout(gridLayout);
		
		Label plotLabel = new Label(plotComposite, SWT.NONE);
		plotLabel.setText("Plots:");
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.LEFT;
		plotLabel.setLayoutData(gridData);
		this.createPlotTable(plotComposite);
		
		sc.setMinSize(sashForm.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		this.parentView.getSite().getWorkbenchWindow().getSelectionService().
			addSelectionListener(this);
	}
	
	private void createPrePostScanTable(Composite parent) {
		this.prePostscanTable = new TableViewer(parent, SWT.BORDER);
		this.prePostscanTable.getTable().setHeaderVisible(true);
		this.prePostscanTable.getTable().setLinesVisible(true);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.minimumHeight = TABLE_MIN_HEIGHT;
		this.prePostscanTable.getTable().setLayoutData(gridData);
		this.createPrePostScanTableColumns(prePostscanTable);
		
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.prePostscanTable.getTable().setMenu(
			menuManager.createContextMenu(this.prePostscanTable.getTable()));
		this.parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.popup", 
			menuManager, this.prePostscanTable);
	}
	
	private void createPrePostScanTableColumns(TableViewer viewer) {
		TableViewerColumn deleteColumn = new TableViewerColumn(viewer, SWT.NONE);
		deleteColumn.getColumn().setWidth(22);
		
		TableViewerColumn prescanColumn = new TableViewerColumn(viewer, SWT.NONE);
		prescanColumn.getColumn().setWidth(80);
		prescanColumn.getColumn().setText("Prescan");
		
		TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.NONE);
		nameColumn.getColumn().setWidth(400);
		nameColumn.getColumn().setText("Name");
		
		TableViewerColumn postscanColumn = new TableViewerColumn(viewer, SWT.NONE);
		postscanColumn.getColumn().setWidth(80);
		postscanColumn.getColumn().setText("Postscan");
		
		TableViewerColumn resetColumn = new TableViewerColumn(viewer, SWT.NONE);
		resetColumn.getColumn().setWidth(100);
		resetColumn.getColumn().setText("Reset Original");
	}
	
	private void createPositioningTable(Composite parent) {
		this.positioningTable = new TableViewer(parent, SWT.BORDER);
		this.positioningTable.getTable().setHeaderVisible(true);
		this.positioningTable.getTable().setLinesVisible(true);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.minimumHeight = TABLE_MIN_HEIGHT;
		this.positioningTable.getTable().setLayoutData(gridData);
		
		this.createPositioningTableColumns(positioningTable);
	}
	
	private void createPositioningTableColumns(TableViewer viewer) {
		
	}
	
	private void createPlotTable(Composite parent) {
		this.plotTable = new TableViewer(parent, SWT.BORDER);
		this.plotTable.getTable().setHeaderVisible(true);
		this.plotTable.getTable().setLinesVisible(true);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.minimumHeight = TABLE_MIN_HEIGHT;
		this.plotTable.getTable().setLayoutData(gridData);
		
		this.createPlotTableColumns(plotTable);
	}
	
	private void createPlotTableColumns(TableViewer viewer) {
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrePostPosPlotView getParentView() {
		return this.getParentView();
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
		if (this.scanModule != null) {
			this.scanModule.removeModelUpdateListener(this);
		}
		this.scanModule = scanModule;
		// TODO set input to table ? model ?
		if (this.scanModule != null) {
			this.scanModule.addModelUpdateListener(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		this.prePostscanTable.refresh();
		this.positioningTable.refresh();
		this.plotTable.refresh();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState(IMemento memento) {
		// sash weights
		int[] sashWeights = sashForm.getWeights();
		memento.putInteger(MEMENTO_PREPOST_SASH_WEIGHT, sashWeights[0]);
		memento.putInteger(MEMENTO_POSITIONING_SASH_WEIGHT, sashWeights[1]);
		memento.putInteger(MEMENTO_PLOT_SASH_WEIGHT, sashWeights[2]);
		// TODO table sort states
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void restoreState(IMemento memento) {
		// TODO restore sash weights
		int[] sashWeights = new int[] {1,1,1};
		if (memento.getInteger(MEMENTO_PREPOST_SASH_WEIGHT) != null) {
			sashWeights[0] = memento.getInteger(MEMENTO_PREPOST_SASH_WEIGHT);
		}
		if (memento.getInteger(MEMENTO_POSITIONING_SASH_WEIGHT) != null) {
			sashWeights[1] = memento.getInteger(MEMENTO_POSITIONING_SASH_WEIGHT);
		}
		if (memento.getInteger(MEMENTO_PLOT_SASH_WEIGHT) != null) {
			sashWeights[2] = memento.getInteger(MEMENTO_PLOT_SASH_WEIGHT);
		}
		this.sashForm.setWeights(sashWeights);
		// TODO table sort states
		
	}
}
