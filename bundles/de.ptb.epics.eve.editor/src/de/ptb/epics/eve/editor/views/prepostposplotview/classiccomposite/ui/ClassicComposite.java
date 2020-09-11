package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.ui;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.Positioning;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.handler.prepostposplotview.RemovePlotWindowsDefaultHandler;
import de.ptb.epics.eve.editor.handler.prepostposplotview.RemovePositioningsDefaultHandler;
import de.ptb.epics.eve.editor.views.AbstractScanModuleViewComposite;
import de.ptb.epics.eve.editor.views.DelColumnEditingSupport;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui.IdColumnLabelProvider;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui.IdEditingSupport;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui.NameEditingSupport;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui.PlotContentProvider;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui.PreInitColumnLabelProvider;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui.PreInitEditingSupport;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui.XAxisColumnLabelProvider;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui.YAxisLabelProvider;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.positioning.ui.ChannelEditingSupport;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.positioning.ui.NormalizeEditingSupport;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.positioning.ui.PluginEditingSupport;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.positioning.ui.PositioningContentProvider;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.positioning.ui.PositioningLabelProvider;
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
		this.prePostscanTable = new TableViewer(parent, SWT.BORDER | SWT.MULTI);
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
			"de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.popup", 
			menuManager, this.prePostscanTable);
		
		this.prePostscanTable.getTable().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				getParentView().setSelectionProvider(prePostscanTable);
			}
		});
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
		this.positioningTable = new TableViewer(parent, SWT.BORDER | SWT.MULTI);
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
		
		this.positioningTable.setContentProvider(
				new PositioningContentProvider());
		this.positioningTable.setLabelProvider(
				new PositioningLabelProvider());
		
		this.positioningTable.setSorter(new ViewerSorter() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				MotorAxis axis1 = ((Positioning)e1).getMotorAxis();
				MotorAxis axis2 = ((Positioning)e2).getMotorAxis();
				return axis1.getName().toLowerCase().compareTo(
						axis2.getName().toLowerCase());
			}
		});
		
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.positioningTable.getTable().setMenu(
			menuManager.createContextMenu(this.positioningTable.getTable()));
		this.parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.positioning.popup", 
			menuManager, this.prePostscanTable);
		
		this.positioningTable.getTable().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				getParentView().setSelectionProvider(positioningTable);
			}
		});
	}
	
	private void createPositioningTableColumns(TableViewer viewer) {
		TableViewerColumn deleteColumn = new TableViewerColumn(
				viewer, SWT.CENTER);
		deleteColumn.getColumn().setText("");
		deleteColumn.getColumn().setWidth(22);
		deleteColumn.setEditingSupport(new DelColumnEditingSupport(viewer, 
				RemovePositioningsDefaultHandler.ID));
		
		TableViewerColumn axisColumn = new TableViewerColumn(viewer, SWT.LEFT);
		axisColumn.getColumn().setText("Motor Axis");
		axisColumn.getColumn().setWidth(160);
		
		TableViewerColumn pluginColumn = new TableViewerColumn(
				viewer, SWT.LEFT);
		pluginColumn.getColumn().setText("Plugin");
		pluginColumn.getColumn().setWidth(120);
		pluginColumn.setEditingSupport(new PluginEditingSupport(viewer));
		
		TableViewerColumn channelColumn = new TableViewerColumn(
				viewer, SWT.LEFT);
		channelColumn.getColumn().setText("Detector Channel");
		channelColumn.getColumn().setWidth(160);
		channelColumn.setEditingSupport(new ChannelEditingSupport(viewer));
		
		TableViewerColumn normalizeColumn = new TableViewerColumn(
				viewer, SWT.LEFT);
		normalizeColumn.getColumn().setText("Normalize Channel");
		normalizeColumn.getColumn().setWidth(160);
		normalizeColumn.setEditingSupport(new NormalizeEditingSupport(viewer));
		
		TableViewerColumn paramColumn = new TableViewerColumn(viewer, SWT.LEFT);
		paramColumn.getColumn().setText("Parameters");
		paramColumn.getColumn().setWidth(100);
	}
	
	private void createPlotTable(Composite parent) {
		this.plotTable = new TableViewer(parent, SWT.BORDER | SWT.MULTI);
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
		
		this.plotTable.setContentProvider(new PlotContentProvider());
		
		ColumnViewerToolTipSupport.enableFor(plotTable);
		
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.setRemoveAllWhenShown(true);
		this.plotTable.getTable().setMenu(
			menuManager.createContextMenu(this.plotTable.getTable()));
		this.parentView.getSite().registerContextMenu(
			"de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.popup", 
			menuManager, this.prePostscanTable);
		
		this.plotTable.getTable().addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				getParentView().setSelectionProvider(plotTable);
			}
		});
	}
	
	private void createPlotTableColumns(TableViewer viewer) {
		TableViewerColumn deleteColumn = new TableViewerColumn(viewer, 
				SWT.CENTER);
		deleteColumn.getColumn().setText("");
		deleteColumn.getColumn().setWidth(22);
		deleteColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return null;
			}
			@Override
			public Image getImage(Object element) {
				return PlatformUI.getWorkbench().getSharedImages().getImage(
						ISharedImages.IMG_TOOL_DELETE);
			}
		});
		deleteColumn.setEditingSupport(new DelColumnEditingSupport(viewer, 
				RemovePlotWindowsDefaultHandler.ID));
		
		TableViewerColumn idColumn = new TableViewerColumn(viewer, SWT.LEFT);
		idColumn.getColumn().setText("Id");
		idColumn.getColumn().setWidth(40);
		idColumn.setLabelProvider(new IdColumnLabelProvider());
		idColumn.setEditingSupport(new IdEditingSupport(viewer));
		
		TableViewerColumn nameColumn = new TableViewerColumn(viewer, SWT.LEFT);
		nameColumn.getColumn().setText("Name");
		nameColumn.getColumn().setWidth(120);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((PlotWindow)element).getName();
			}
			@Override
			public String getToolTipText(Object element) {
				return this.getText(element);
			}
		});
		nameColumn.setEditingSupport(new NameEditingSupport(viewer));
		
		TableViewerColumn preInitColumn = new TableViewerColumn(viewer, 
				SWT.CENTER);
		preInitColumn.getColumn().setText("Init");
		preInitColumn.getColumn().setWidth(35);
		preInitColumn.setLabelProvider(new PreInitColumnLabelProvider());
		preInitColumn.setEditingSupport(new PreInitEditingSupport(viewer));
		
		TableViewerColumn xAxisColumn = new TableViewerColumn(viewer, SWT.LEFT);
		xAxisColumn.getColumn().setText("x-Axis");
		xAxisColumn.getColumn().setWidth(130);
		xAxisColumn.setLabelProvider(new XAxisColumnLabelProvider());
		
		TableViewerColumn yAxis1Column = new TableViewerColumn(viewer, SWT.LEFT);
		yAxis1Column.getColumn().setText("y-Axis 1");
		yAxis1Column.getColumn().setWidth(260);
		yAxis1Column.setLabelProvider(new YAxisLabelProvider(0));
		
		TableViewerColumn yAxis2Column = new TableViewerColumn(viewer, SWT.LEFT);
		yAxis2Column.getColumn().setText("y-Axis 2");
		yAxis2Column.getColumn().setWidth(260);
		yAxis2Column.setLabelProvider(new YAxisLabelProvider(1));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PrePostPosPlotView getParentView() {
		return (PrePostPosPlotView)this.parentView;
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
		this.positioningTable.setInput(scanModule);
		this.plotTable.setInput(scanModule);
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
