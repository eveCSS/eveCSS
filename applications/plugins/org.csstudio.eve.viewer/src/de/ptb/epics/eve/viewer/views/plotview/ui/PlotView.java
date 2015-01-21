package de.ptb.epics.eve.viewer.views.plotview.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.types.ChainStatus;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.util.pdf.PlotStats;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.IUpdateListener;
import de.ptb.epics.eve.viewer.views.plotview.MathFunction;
import de.ptb.epics.eve.viewer.views.plotview.MathTableElement;
import de.ptb.epics.eve.viewer.views.plotview.XyPlot;

/**
 * <code>PlotView</code> contains an xy plot and tables with statistics 
 * for up to two detector channels.
 * 
 * @author Marcus Michalsky
 */
public class PlotView extends ViewPart implements IChainStatusListener,
		IUpdateListener {
	/** the unique identifier of this view */
	public static final String ID = "PlotView";

	private static final Logger LOGGER = Logger.getLogger(PlotView.class);
	
	private SashForm sashForm;
	
	private Canvas canvas;
	private XyPlot xyPlot;
	
	private TableViewer table1Viewer;
	private TableViewer table2Viewer;
	private TabItem itemAxis1;
	private TabItem itemAxis2;
	
	private boolean showStats;
	
	private String loadedScmlFile;
	private String bufferedScmlFile;
	
	private Image gotoIcon;
	
	// saves/restores user defined settings
	private IMemento memento;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		this.memento = memento;
		super.init(site, memento);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		gotoIcon = Activator.getDefault().getImageRegistry().get("GREENGO12");
		
		parent.setLayout(new FillLayout());
		sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.SASH_WIDTH = 2;
		
		canvas = new Canvas(sashForm, SWT.BORDER);
		// use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(canvas);
		// set it as the content of LightwightSystem
		xyPlot = new XyPlot(this);
		lws.setContents(xyPlot);
		
		TabFolder tabFolder = new TabFolder(sashForm, SWT.BORDER);
		itemAxis1 = new TabItem(tabFolder, SWT.NONE);
		itemAxis1.setText("-");
		Composite table1Composite = new Composite(tabFolder, SWT.NONE);
		table1Composite.setLayout(new FillLayout());
		itemAxis1.setControl(table1Composite);
		table1Viewer = this.createTable(table1Composite);
		
		itemAxis2 = new TabItem(tabFolder, SWT.NONE);
		itemAxis2.setText("-");
		Composite table2Composite = new Composite(tabFolder, SWT.NONE);
		table2Composite.setLayout(new FillLayout());
		itemAxis2.setControl(table2Composite);
		table2Viewer = this.createTable(table2Composite);
		
		this.setPartName("Plot: " + this.getViewSite().getSecondaryId());
		
		this.loadedScmlFile = "unknown";
		this.bufferedScmlFile = "unknown";
		
		Activator.getDefault().getChainStatusAnalyzer().addUpdateListener(this);
		Activator.getDefault().getEcp1Client().addChainStatusListener(this);
		
		this.restoreState();
		this.refreshToggleButton();
		
		this.initAutoScale();
	}

	private void initAutoScale() {
		ICommandService cmdService = (ICommandService) getSite().getService(
				ICommandService.class);
		
		Command autoScaleCmdX = cmdService
				.getCommand("de.ptb.epics.eve.viewer.views.plotview.autoscalex");
		if (autoScaleCmdX.isDefined()) {
			autoScaleCmdX.getState("org.eclipse.ui.commands.toggleState")
					.addListener(this.xyPlot);
		} else {
			LOGGER.error("Command auto scale x not defined!");
		}
		
		Command autoScaleCmdY1 = cmdService
				.getCommand("de.ptb.epics.eve.viewer.views.plotview.autoscaley1");
		if (autoScaleCmdY1.isDefined()) {
			autoScaleCmdY1.getState("org.eclipse.ui.commands.toggleState")
					.addListener(this.xyPlot);
		} else {
			LOGGER.error("Command auto scale y1 not defined!");
		}
		
		Command autoScaleCmdY2 = cmdService
				.getCommand("de.ptb.epics.eve.viewer.views.plotview.autoscaley1");
		if (autoScaleCmdY2.isDefined()) {
			autoScaleCmdY2.getState("org.eclipse.ui.commands.toggleState")
					.addListener(this.xyPlot);
		} else {
			LOGGER.error("Command auto scale y1 not defined!");
		}
	}
	
	/*
	 * 
	 */
	private TableViewer createTable(Composite parent) {
		TableViewer tableViewer = new TableViewer(parent, SWT.BORDER
				| SWT.FULL_SELECTION);
		
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);
		
		// the first column is a vertical header column
		TableViewerColumn nameColumn = new TableViewerColumn(tableViewer,
				SWT.NONE);
		nameColumn.getColumn().setText("");
		nameColumn.getColumn().setWidth(85);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MathTableElement) element).getName();
			}
		});

		// the second column contains the statistics for the detector channel
		TableViewerColumn valueColumn = new TableViewerColumn(tableViewer,
				SWT.NONE);
		valueColumn.getColumn().setText("Channel");
		valueColumn.getColumn().setWidth(140);
		valueColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MathTableElement) element).getValue();
			}
		});
		
		// the third column contains the positions of the motor axis where the
		// corresponding statistical value was detected
		TableViewerColumn motorColumn = new TableViewerColumn(tableViewer,
				SWT.NONE);
		motorColumn.getColumn().setText("Axis");
		motorColumn.getColumn().setWidth(100);
		motorColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((MathTableElement) element).getPosition();
			}
		});
		
		// the fourth column contains the goto icons
		// if you click on this icon the motor moves to the position indicated
		// in the third column (same row)
		TableViewerColumn gotoColumn = new TableViewerColumn(tableViewer,
				SWT.NONE);
		gotoColumn.getColumn().setText("GoTo");
		gotoColumn.getColumn().setWidth(22);
		gotoColumn.setEditingSupport(new EditingSupport(tableViewer) {
			@Override
			protected void setValue(Object element, Object value) {
			}
			@Override
			protected Object getValue(Object element) {
				return null;
			}
			@Override
			protected CellEditor getCellEditor(Object element) {
				return null;
			}
			@Override
			protected boolean canEdit(Object element) {
				((MathTableElement)element).gotoPos();
				return false;
			}
		});
		gotoColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				if (((MathTableElement)element).drawIcon()) {
					return gotoIcon;
				} else {
					return null;
				}
			}
			@Override
			public String getText(Object element) {
				return null;
			}
		});
		
		// provide content for the table
		MathTableContentProvider contentProvider = new MathTableContentProvider(
				tableViewer);
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setInput(contentProvider);
		
		return tableViewer;
	}
	
	/**
	 * 
	 * @param plotWindow
	 * @since 1.13
	 */
	public void setPlotWindow(PlotWindow plotWindow) {
		// delegate to XyPlot
		this.xyPlot.setPlotWindow(plotWindow);
		
		((MathTableContentProvider) table1Viewer.getContentProvider()).clear();
		((MathTableContentProvider) table2Viewer.getContentProvider()).clear();
		
		YAxis yAxis1 = plotWindow.getYAxes().get(0);
		if (yAxis1.getNormalizeChannel() != null) {
			itemAxis1.setText(yAxis1.getDetectorChannel().getName() + "/"
					+ yAxis1.getNormalizeChannel().getName());
			fillTable(table1Viewer, plotWindow, yAxis1.getDetectorChannel()
					.getID() + "__" + yAxis1.getNormalizeChannel().getID(), true, 1);
		} else {
			itemAxis1.setText(yAxis1.getDetectorChannel().getName());
			fillTable(table1Viewer, plotWindow, yAxis1.getDetectorChannel()
					.getID(), false, 1);
		}
		if (plotWindow.getYAxisAmount() > 1) {
			YAxis yAxis2 = plotWindow.getYAxes().get(1);
			if (yAxis2.getNormalizeChannel() != null) {
				itemAxis2.setText(yAxis2.getDetectorChannel().getName()
						+ "/" + yAxis2.getNormalizeChannel().getName());
				fillTable(table2Viewer, plotWindow, yAxis2
						.getDetectorChannel().getID() + "__"
						+ yAxis2.getNormalizeChannel().getID(), true, 2);
			} else {
				itemAxis2.setText(yAxis2.getDetectorChannel().getName());
				fillTable(table2Viewer, plotWindow, yAxis2.getDetectorChannel()
						.getID(), false, 2);
			}
		} else {
			itemAxis2.setText("-");
		}
		
		LOGGER.debug("initializing plot window with id " + plotWindow.getId());
	}
	
	/*
	 * 
	 */
	private void fillTable(TableViewer tableViewer, PlotWindow plotWindow,
				String detectorId, boolean normalized, int axis) {
		// create a content provider for the table...
		MathTableContentProvider contentProvider = 
				(MathTableContentProvider) tableViewer.getContentProvider();

		final int chid = plotWindow.getScanModule().getChain().getId();
		final int smid = plotWindow.getScanModule().getId();
		final String motorId = plotWindow.getXAxis().getID();
		final String motorPv = plotWindow.getXAxis().getGoto().getAccess()
				.getVariableID();
		final int plotWindowId = plotWindow.getId();
		
		// renaming table columns
		tableViewer.getTable().getColumn(2)
				.setText(plotWindow.getXAxis().getName());
		tableViewer.getTable().getColumn(2)
				.setToolTipText(plotWindow.getXAxis().getName());
		String columnName = "Channel";
		if (axis == 1) {
			YAxis yAxis = plotWindow.getYAxes().get(0);
			if (normalized) {
				columnName = yAxis.getDetectorChannel().getName() + "/"
						+ yAxis.getNormalizeChannel().getName();
			} else {
				columnName = yAxis.getDetectorChannel().getName();
			}
		} else if (axis == 2) {
			YAxis yAxis = plotWindow.getYAxes().get(1);
			if (normalized) {
				columnName = yAxis.getDetectorChannel().getName() + "/"
						+ yAxis.getNormalizeChannel().getName();
			} else {
				columnName = yAxis.getDetectorChannel().getName();
			}
		}
		tableViewer.getTable().getColumn(1).setText(columnName);
		tableViewer.getTable().getColumn(1).setToolTipText(columnName);
		
		MathTableElement element;
		if (normalized) {
			element = new MathTableElement(chid, smid, tableViewer, 
					MathFunction.NORMALIZED, motorPv, motorId, detectorId, plotWindowId);
				contentProvider.addElement(element);
		} else {
			element = new MathTableElement(chid, smid, tableViewer,
					MathFunction.UNMODIFIED, motorPv, motorId, detectorId, plotWindowId);
			contentProvider.addElement(element);
		}

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.MINIMUM, motorPv, motorId, detectorId, plotWindowId);
		contentProvider.addElement(element);

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.MAXIMUM, motorPv, motorId, detectorId, plotWindowId);
		contentProvider.addElement(element);

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.CENTER, motorPv, motorId, detectorId, plotWindowId);
		contentProvider.addElement(element);

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.EDGE, motorPv, motorId, detectorId, plotWindowId);
		contentProvider.addElement(element);

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.AVERAGE, motorPv, motorId, detectorId, plotWindowId);
		contentProvider.addElement(element);

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.DEVIATION, motorPv, motorId, detectorId, plotWindowId);
		contentProvider.addElement(element);

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.FWHM, motorPv, motorId, detectorId, plotWindowId);
		contentProvider.addElement(element);
	}
	
	/**
	 * 
	 */
	public void finish() {
		this.xyPlot.finish();
		LOGGER.debug("finish sm");
	}
	
	/**
	 * 
	 * @return
	 */
	public XyPlot getPlotFigure() {
		return this.xyPlot;
	}
	
	/**
	 * Returns the currently loaded SCML file.
	 * @return the currently loaded SCML file
	 */
	public String getLoadedScmlFile() {
		return this.loadedScmlFile;
	}
	
	/**
	 * Sets whether the statistics tables are shown.
	 * <p>
	 * The state is saved in the memento.
	 */
	public void showStatistics(boolean show) {
		if (show) {
			this.showStats = true;
			this.sashForm.setMaximizedControl(null);
		} else {
			this.showStats = false;
			this.sashForm.setMaximizedControl(this.canvas);
		}
	}
	
	public List<PlotStats> getPlotStatistics() {
		List<PlotStats> plotStatList = new ArrayList<PlotStats>();
		plotStatList.add(this.getStats(((MathTableContentProvider) table1Viewer
				.getContentProvider()).getElements()));
		plotStatList.add(this.getStats(((MathTableContentProvider) table2Viewer
				.getContentProvider()).getElements()));
		return plotStatList;
	}
	
	private PlotStats getStats(List<MathTableElement> elements) {
		PlotStats stats = new PlotStats();
		for (MathTableElement element : elements) {
			stats.setMotorName(Activator.getDefault().getMeasuringStation()
					.getMotorAxisById(element.getMotorId()).getName());
			stats.setDetectorName(Activator.getDefault().getMeasuringStation()
					.getDetectorChannelById(element.getDetectorId()).getName());
			switch (element.getType()) {
			case AVERAGE:
				if (element.getPosition() != null) {
					stats.getAverage().setL(element.getPosition());
				}
				if (element.getValue() != null) {
					stats.getAverage().setR(element.getValue());
				}
				break;
			case CENTER:
				if (element.getPosition() != null) {
					stats.getCenter().setL(element.getPosition());
				}
				if (element.getValue() != null) {
					stats.getCenter().setR(element.getValue());
				}
				break;
			case DEVIATION:
				if (element.getPosition() != null) {
					stats.getDeviation().setL(element.getPosition());
				}
				if (element.getValue() != null) {
					stats.getDeviation().setR(element.getValue());
				}
				break;
			case EDGE:
				if (element.getPosition() != null) {
					stats.getEdge().setL(element.getPosition());
				}
				if (element.getValue() != null) {
					stats.getEdge().setR(element.getValue());
				}
				break;
			case FWHM:
				if (element.getPosition() != null) {
					stats.getFullWidthHalfMinimum().setL(element.getPosition());
				}
				if (element.getValue() != null) {
					stats.getFullWidthHalfMinimum().setR(element.getValue());
				}
				break;
			case MAXIMUM:
				if (element.getPosition() != null) {
					stats.getMaximum().setL(element.getPosition());
				}
				if (element.getValue() != null) {
					stats.getMaximum().setR(element.getValue());
				}
				break;
			case MINIMUM:
				if (element.getPosition() != null) {
					stats.getMinimum().setL(element.getPosition());
				}
				if (element.getValue() != null) {
					stats.getMinimum().setR(element.getValue());
				}
				break;
			case NORMALIZED:
			case PEAK:
			case SUM:
			case UNKNOWN:
			case UNMODIFIED:
				break;
			default:
				break;
			}
		}
		return stats;
	}
	
	/*
	 * 
	 */
	private void refreshToggleButton() {
		ICommandService commandService = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class);
		Command toggleCommand = commandService
				.getCommand("de.ptb.epics.eve.viewer.views.plotview.togglestats");

		State state = toggleCommand
				.getState("org.eclipse.ui.commands.toggleState");

		state.setValue(this.showStats);
		
		commandService.refreshElements(
				"de.ptb.epics.eve.viewer.views.plotview.togglestats", null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.sashForm.setFocus();
		this.refreshToggleButton();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveState(IMemento memento) {
		memento.putBoolean("showStats", this.showStats);
		// save composite heights
		memento.putInteger("plotWeight", sashForm.getWeights()[0]);
		memento.putInteger("statsWeight", sashForm.getWeights()[1]);
	}
	
	/*
	 * 
	 */
	private void restoreState() {
		if(this.memento == null) {
			// nothing saved
			return;
		}
		
		// restore sash weights
		int plotWeight = this.memento.getInteger("plotWeight") == null
				? 2
				: this.memento.getInteger("plotWeight");
		int statsWeight = this.memento.getInteger("statsWeight") == null
				? 1
				: this.memento.getInteger("statsWeight");
		this.sashForm.setWeights(new int[] {plotWeight, statsWeight});
		
		// restore show statistics property and state
		this.showStats = memento.getBoolean("showStats") == null
				? true
				: memento.getBoolean("showStats");
		this.showStatistics(this.showStats);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		ICommandService cmdService = (ICommandService) getSite().getService(
				ICommandService.class);
		
		Command autoScaleCmdX = cmdService
				.getCommand("de.ptb.epics.eve.viewer.views.plotview.autoscalex");
		if (autoScaleCmdX.isDefined()) {
			autoScaleCmdX.getState("org.eclipse.ui.commands.toggleState").
				removeListener(this.xyPlot);
		}
		
		Command autoScaleCmdY1 = cmdService
				.getCommand("de.ptb.epics.eve.viewer.views.plotview.autoscaley1");
		if (autoScaleCmdY1.isDefined()) {
			autoScaleCmdY1.getState("org.eclipse.ui.commands.toggleState").
				removeListener(this.xyPlot);
		}
		
		Command autoScaleCmdY2 = cmdService
				.getCommand("de.ptb.epics.eve.viewer.views.plotview.autoscaley2");
		if (autoScaleCmdY2.isDefined()) {
			autoScaleCmdY2.getState("org.eclipse.ui.commands.toggleState").
				removeListener(this.xyPlot);
		}
		
		Activator.getDefault().getChainStatusAnalyzer().removeUpdateListener(this);
		Activator.getDefault().getEcp1Client().removeChainStatusListener(this);
		this.xyPlot.clear();
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoadedScmlFile(String name) {
		this.bufferedScmlFile = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateOccured(int remainTime) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearStatusTable() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillStatusTable(int chainId, int scanModuleId, String smName, String status,
			int remainTime) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillEngineStatus(EngineStatus engineStatus, int repeatCount) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAutoPlayStatus(boolean autoPlayStatus) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disableSendToFile() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chainStatusChanged(ChainStatusCommand chainStatusCommand) {
		if (chainStatusCommand.getChainStatus().equals(ChainStatus.EXECUTING_SM)) {
			this.loadedScmlFile = this.bufferedScmlFile;
		}
	}
}