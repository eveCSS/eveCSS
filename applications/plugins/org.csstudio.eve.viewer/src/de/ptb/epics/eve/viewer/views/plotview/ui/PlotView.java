package de.ptb.epics.eve.viewer.views.plotview.ui;

import org.apache.log4j.Logger;
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
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.plotview.MathFunction;
import de.ptb.epics.eve.viewer.views.plotview.MathTableElement;
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
	
	private SashForm sashForm;
	
	private XyPlot xyPlot;
	
	TableViewer table1Viewer;
	TableViewer table2Viewer;
	TabItem itemAxis1;
	TabItem itemAxis2;
	
	private Image gotoIcon;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		gotoIcon = Activator.getDefault().getImageRegistry().get("GREENGO12");
		
		parent.setLayout(new FillLayout());
		sashForm = new SashForm(parent, SWT.HORIZONTAL);
		sashForm.SASH_WIDTH = 2;
		
		Canvas canvas = new Canvas(sashForm, SWT.BORDER);
		// use LightweightSystem to create the bridge between SWT and draw2D
		final LightweightSystem lws = new LightweightSystem(canvas);
		// set it as the content of LightwightSystem
		xyPlot = new XyPlot();
		lws.setContents(xyPlot);
		
		TabFolder tabFolder = new TabFolder(sashForm, SWT.BORDER);
		itemAxis1 = new TabItem(tabFolder, SWT.NONE);
		itemAxis1.setText("Tab1");
		Composite table1Composite = new Composite(tabFolder, SWT.NONE);
		table1Composite.setLayout(new FillLayout());
		itemAxis1.setControl(table1Composite);
		table1Viewer = this.createTable(table1Composite);
		
		itemAxis2 = new TabItem(tabFolder, SWT.NONE);
		itemAxis2.setText("Tab2");
		Composite table2Composite = new Composite(tabFolder, SWT.NONE);
		table2Composite.setLayout(new FillLayout());
		itemAxis2.setControl(table2Composite);
		table2Viewer = this.createTable(table2Composite);

		sashForm.setWeights(new int[] {2,1});
		
		this.setPartName("Plot: " + this.getViewSite().getSecondaryId());
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
			public Image getImage(Object element) {
				if (((MathTableElement)element).drawIcon()) 
					return gotoIcon;
				else
					return null;
			}
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
			itemAxis1.setText(yAxis1.getNormalizeChannel().getName() + "/"
					+ yAxis1.getDetectorChannel().getName());
			fillTable(table1Viewer, plotWindow, yAxis1.getNormalizeChannel()
					.getID() + "/" + yAxis1.getDetectorChannel().getID(), true);
		} else {
			itemAxis1.setText(yAxis1.getDetectorChannel().getName());
			fillTable(table1Viewer, plotWindow, yAxis1.getDetectorChannel()
					.getID(), false);
		}
		if (plotWindow.getYAxisAmount() > 1) {
			YAxis yAxis2 = plotWindow.getYAxes().get(1);
			if (yAxis2.getNormalizeChannel() != null) {
				itemAxis2.setText(yAxis2.getNormalizeChannel().getName()
						+ "/" + yAxis2.getDetectorChannel().getName());
				fillTable(table2Viewer, plotWindow, yAxis2
						.getNormalizeChannel().getID()
						+ "/"
						+ yAxis2.getDetectorChannel().getID(), true);
			} else {
				itemAxis2.setText(yAxis2.getDetectorChannel().getName());
				fillTable(table2Viewer, plotWindow, yAxis2.getDetectorChannel()
						.getID(), false);
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
			String detectorId, boolean normalized) {
		// create a content provider for the table...
		MathTableContentProvider contentProvider = 
				(MathTableContentProvider) tableViewer.getContentProvider();

		final int chid = plotWindow.getScanModule().getChain().getId();
		final int smid = plotWindow.getScanModule().getId();
		final String motorId = plotWindow.getXAxis().getID();
		final String motorPv = plotWindow.getXAxis().getGoto().getAccess()
				.getVariableID();
		
		MathTableElement element;
		if (normalized) {
			element = new MathTableElement(chid, smid, tableViewer, 
					MathFunction.NORMALIZED, motorPv, motorId, detectorId);
				contentProvider.addElement(element);
		} else {
			element = new MathTableElement(chid, smid, tableViewer,
					MathFunction.UNMODIFIED, motorPv, motorId, detectorId);
			contentProvider.addElement(element);
		}

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.MINIMUM, motorPv, motorId, detectorId);
		contentProvider.addElement(element);

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.MAXIMUM, motorPv, motorId, detectorId);
		contentProvider.addElement(element);

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.CENTER, motorPv, motorId, detectorId);
		contentProvider.addElement(element);

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.EDGE, motorPv, motorId, detectorId);
		contentProvider.addElement(element);

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.AVERAGE, motorPv, motorId, detectorId);
		contentProvider.addElement(element);

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.DEVIATION, motorPv, motorId, detectorId);
		contentProvider.addElement(element);

		element = new MathTableElement(chid, smid, tableViewer,
				MathFunction.FWHM, motorPv, motorId, detectorId);
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
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		this.sashForm.setFocus();
	}
}