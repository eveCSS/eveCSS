package de.ptb.epics.eve.viewer.views.plotview.ui;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.types.ChainStatus;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.util.pdf.PlotStats;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.IUpdateListener;

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
	private TableComposite tableComposite;
	
	private boolean showStats;
	
	private String loadedScmlFile;
	private String bufferedScmlFile;
	
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
		parent.setLayout(new FillLayout());
		sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.SASH_WIDTH = 2;
		
		canvas = new Canvas(sashForm, SWT.BORDER);
		// use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(canvas);
		// set it as the content of LightwightSystem
		xyPlot = new XyPlot(this);
		lws.setContents(xyPlot);
		
		tableComposite = new TableComposite(sashForm, SWT.NONE);
		
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
	
	/**
	 * 
	 * @param plotWindow
	 * @since 1.13
	 */
	public void setPlotWindow(PlotWindow plotWindow) {
		// delegate to XyPlot
		this.xyPlot.setPlotWindow(plotWindow);
		this.tableComposite.setPlotWindow(plotWindow);
		LOGGER.debug("initializing plot window with id " + plotWindow.getId());
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
		// TODO delegate !
		List<PlotStats> plotStatList = new ArrayList<PlotStats>();
		/*
		plotStatList.add(this.getStats(((MathTableContentProvider) table1Viewer
				.getContentProvider()).getElements()));
		plotStatList.add(this.getStats(((MathTableContentProvider) table2Viewer
				.getContentProvider()).getElements()));
				*/
		return plotStatList;
	}
	
	// TODO
	/*
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
	}*/
	
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