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
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.YAxisModifier;
import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.client.interfaces.IEngineStatusListener;
import de.ptb.epics.eve.ecp1.commands.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.types.ChainStatus;
import de.ptb.epics.eve.ecp1.types.EngineStatus;
import de.ptb.epics.eve.util.pdf.PlotStats;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.plotview.plot.TraceDataCollector;
import de.ptb.epics.eve.viewer.views.plotview.table.Data;

/**
 * <code>PlotView</code> contains an xy plot and tables with statistics 
 * for up to two detector channels.
 * 
 * @author Marcus Michalsky
 */
public class PlotView extends ViewPart implements IChainStatusListener,
		IEngineStatusListener {
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
		
		Activator.getDefault().getEcp1Client().addEngineStatusListener(this);
		Activator.getDefault().getEcp1Client().addChainStatusListener(this);
		
		this.restoreState();
		this.refreshStatisticsToggleButton();
		this.refreshInverseToggleButtons();
		
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
		this.refreshInverseToggleButtons();
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
	
	@SuppressWarnings("unchecked")
	public List<PlotStats> getPlotStatistics() {
		// TODO delegate !
		List<PlotStats> plotStatList = new ArrayList<PlotStats>();
		plotStatList.add(this.getStats(tableComposite.getDet1Elements(), 0));
		plotStatList.add(this.getStats(tableComposite.getDet2Elements(), 1));
		return plotStatList;
	}
	
	private PlotStats getStats(List<Data> elements, int yAxis) {
		PlotStats stats = new PlotStats();
		for (Data data : elements) {
			stats.setMotorName(data.getPlotWindow().getXAxis().getName());
			stats.setDetectorName(data.getPlotWindow().getYAxes().get(yAxis)
					.getNormalizedName());
			String x = data.getMotorPosition();
			String y = data.getDetectorValue();
			switch (data.getDataModifier()) {
			case CENTER:
				stats.getCenter().setL(x);
				stats.getCenter().setR(y);
				break;
			case EDGE:
				stats.getEdge().setL(x);
				stats.getEdge().setR(y);
				break;
			case FWHM:
				stats.getFullWidthHalfMinimum().setL(x);
				stats.getFullWidthHalfMinimum().setR(y);
				break;
			case MAX:
				stats.getMaximum().setL(x);
				stats.getMaximum().setR(y);
				break;
			case MEAN_VALUE:
				stats.getAverage().setL(x);
				stats.getAverage().setR(y);
				break;
			case MIN:
				stats.getMinimum().setL(x);
				stats.getMinimum().setR(y);
				break;
			case STANDARD_DEVIATION:
				stats.getDeviation().setL(x);
				stats.getDeviation().setR(y);
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
	private void refreshStatisticsToggleButton() {
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
	
	private void refreshInverseToggleButtons() {
		ICommandService commandService = (ICommandService) PlatformUI
				.getWorkbench().getService(ICommandService.class);
		Command inverseCommand1 = commandService.getCommand(
				"de.ptb.epics.eve.viewer.views.plotview.modifier.inverty1");
		Command inverseCommand2 = commandService.getCommand(
				"de.ptb.epics.eve.viewer.views.plotview.modifier.inverty2");
		State inverseState1 = inverseCommand1.getState(RegistryToggleState.STATE_ID);
		State inverseState2 = inverseCommand2.getState(RegistryToggleState.STATE_ID);
		
		List<TraceDataCollector> traces = this.xyPlot.getCollectors();
		if (!traces.isEmpty()) {
			boolean inverse1 = traces.get(0).getYAxisModifier().equals(YAxisModifier.INVERSE);
			LOGGER.debug("YAxis 1 Inverse Modifier: " + inverse1);
			inverseState1.setValue(inverse1);
		} else {
			inverseState1.setValue(Boolean.FALSE);
			inverseState2.setValue(Boolean.FALSE);
		}
		if(traces.size() > 1) {
			boolean inverse2 = traces.get(1).getYAxisModifier().equals(YAxisModifier.INVERSE);
			LOGGER.debug("YAxis 2 Inverse Modifier: " + inverse2);
			inverseState2.setValue(inverse2);
		} else {
			inverseState2.setValue(Boolean.FALSE);
		}
		
		commandService.refreshElements(
				"de.ptb.epics.eve.viewer.views.plotview.modifier.inverty1", null);
		commandService.refreshElements(
				"de.ptb.epics.eve.viewer.views.plotview.modifier.inverty2", null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.sashForm.setFocus();
		this.refreshStatisticsToggleButton();
		this.refreshInverseToggleButtons();
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
		
		Activator.getDefault().getEcp1Client().removeEngineStatusListener(this);
		Activator.getDefault().getEcp1Client().removeChainStatusListener(this);
		this.xyPlot.clear();
		this.tableComposite.clear();
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void chainStatusChanged(ChainStatusCommand chainStatusCommand) {
		if (chainStatusCommand.getChainStatus().equals(ChainStatus.EXECUTING)) {
			this.loadedScmlFile = this.bufferedScmlFile;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void engineStatusChanged(EngineStatus engineStatus, String xmlName, 
			int repeatCount) {
		if (!EngineStatus.LOADING_XML.equals(engineStatus)) {
			this.bufferedScmlFile = xmlName;
		}
	}
}