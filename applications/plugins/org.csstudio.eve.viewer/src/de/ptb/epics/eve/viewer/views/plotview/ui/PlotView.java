package de.ptb.epics.eve.viewer.views.plotview.ui;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.viewer.views.plotview.XyPlot;

/**
 * <code>PlotView</code> contains an xy plot and tables with statistics 
 * for up to two detector channels.
 * 
 * @author Marcus Michalsky
 */
public class PlotView extends ViewPart {
	
	/** the unique identifier of this view */
	public static final String ID = "PlotView";

	private static Logger LOGGER = Logger.getLogger(PlotView.class);
	
	// the composite for the statistics tables
	private PlotViewDetectorComposite plotDetectorComposite;

	private ScrolledComposite sc = null;
	private Composite top = null;
	
	private XyPlot xyPlot = null;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		this.sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		this.top = new Composite(sc, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.top.setLayout(gridLayout);
		sc.setContent(this.top);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);

		Canvas canvas = new Canvas(top, SWT.NONE);
		// use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(canvas);
		// set it as the content of LightwightSystem
		xyPlot = new XyPlot();
		lws.setContents(xyPlot);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.minimumWidth = 400;
		gridData.minimumHeight = 300;
		canvas.setLayoutData(gridData);

		plotDetectorComposite = new PlotViewDetectorComposite(top, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		this.plotDetectorComposite.setLayoutData(gridData);
		
		this.setPartName("Plot: " + this.getViewSite().getSecondaryId());
	}

	/**
	 * 
	 * @param plotWindow
	 * @since 1.13
	 */
	public void setPlotWindow(PlotWindow plotWindow) {
		// delegate to XyPlot
		this.xyPlot.setPlotWindow(plotWindow);
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
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.top.setFocus();
	}
}