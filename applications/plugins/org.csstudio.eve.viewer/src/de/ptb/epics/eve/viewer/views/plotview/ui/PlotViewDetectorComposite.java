package de.ptb.epics.eve.viewer.views.plotview.ui;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.plotview.MathFunction;
import de.ptb.epics.eve.viewer.views.plotview.MathTableElement;

/**
 * <code>PlotDetectorComposite</code> is a composite containing two tables 
 * with statistics of two detector channels.
 * 
 * @author Jens Eden
 * @author Marcus Michalsky
 */
public class PlotViewDetectorComposite extends Composite {

	// the table for the first detector
	private TableViewer tableViewerDet1;
	// the table for the second detector
	private TableViewer tableViewerDet2;
	// the icon used in the goto column to set a motor axis to a specific value
	private Image gotoIcon;

	// indicates whether normalization is set for the specific axis
	private boolean normalizeAxis1;
	private boolean normalizeAxis2;
	
	/**
	 * Constructs a <code>PlotDetectorComposite</code>.
	 * 
	 * @param parent the parent it should belong to
	 * @param style the style
	 */
	public PlotViewDetectorComposite(Composite parent, int style) {
		super(parent, style);
		
		// grab the goto icon
		gotoIcon = Activator.getDefault().getImageRegistry().get("GREENGO12");
		
		// we use a one column grid layout:
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		setLayout(gridLayout);
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		setLayoutData( gridData );
		
		// table for detector 1
		tableViewerDet1 = makeTable();
		// table for detector 2
		tableViewerDet2 = makeTable();
		
		normalizeAxis1 = false;
		normalizeAxis2 = false;
	}
	
	/*
	 * called by the constructor to construct the tables
	 */
	private TableViewer makeTable() {
		
		// create the table widget
		Composite tableComposite = new Composite(this, SWT.FULL_SELECTION);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		TableViewer tableViewer = new TableViewer(tableComposite, SWT.BORDER | 
												   SWT.FULL_SELECTION);

		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLinesVisible(true);

		// provide content for the table
		MathTableContentProvider contentProvider = 
					new MathTableContentProvider(tableViewer);
		tableViewer.setContentProvider(contentProvider);

		// the first column is a vertical header column
		TableViewerColumn nameColumn = new TableViewerColumn(tableViewer, 
															  SWT.NONE);
		nameColumn.getColumn().setText("");
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((MathTableElement) element).getName();
			}
		});
		tableColumnLayout.setColumnData(nameColumn.getColumn(), 
										new ColumnPixelData(85));

		// the second column contains the statistics for the detector channel
		TableViewerColumn valueColumn = 
				new TableViewerColumn(tableViewer, SWT.NONE);
		valueColumn.getColumn().setText("Channel");
		valueColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((MathTableElement) element).getValue();
			}
		});
		tableColumnLayout.setColumnData(valueColumn.getColumn(), 
										new ColumnPixelData(140));

		// the third column contains the positions of the motor axis where the
		// corresponding statistical value was detected
		TableViewerColumn motorColumn = 
				new TableViewerColumn(tableViewer, SWT.NONE);
		motorColumn.getColumn().setText("Axis");
		motorColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((MathTableElement) element).getPosition();
			}
		});
		tableColumnLayout.setColumnData(motorColumn.getColumn(), 
										new ColumnPixelData(100));

		// the fourth column contains the goto icons
		// if you click on this icon the motor moves to the position indicated
		// in the third column (same row)
		TableViewerColumn gotoColumn = 
				new TableViewerColumn(tableViewer, SWT.NONE);
		gotoColumn.getColumn().setText("GoTo");
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
		tableColumnLayout.setColumnData(gotoColumn.getColumn(), 
										new ColumnPixelData(22));

		tableViewer.setInput(contentProvider);
		
		// initially the table is not visible
		tableViewer.getTable().setVisible(false);
		
		// the table viewer is ready for use now
		return tableViewer;
	}
	
	/**
	 * Updates the table with current data.
	 * 
	 * @param plotWindow the 
	 * 		  {@link de.ptb.epics.eve.data.scandescription.PlotWindow} 
	 * 		  containing all information corresponding to the plot
	 * @param chid the id of the chain
	 * @param smid the id of the scan module
	 * @param motorId the id of the motor
	 * @param motorName the name of the motor
	 * @param motorPv the process variable of the motor
	 * @param detector1Id the id of the first detector
	 * @param detector1Name the name of the first detector
	 * @param detector2Id the id of the second detector
	 * @param detector2Name the name of the second detector
	 */
	public void refresh(PlotWindow plotWindow, int chid, int smid, 
						String motorId, String motorName, 
						String motorPv, String detector1Id, 
						String detector1Name, String detector2Id, 
						String detector2Name) {
		
		if (detector1Id != null) {
			normalizeAxis1 = (plotWindow.getYAxes().get(0)
					.getNormalizeChannel() != null);

			if (normalizeAxis1) {
				String normalizeId = plotWindow.getYAxes().get(0)
						.getNormalizeChannel().getID();
				String normalizeName = plotWindow.getYAxes().get(0)
						.getNormalizeChannel().getName();
				createContent(1, chid, smid, tableViewerDet1, motorPv, motorId,
						detector1Id + "__" + normalizeId);
				tableViewerDet1.getTable().getColumn(1)
						.setText(detector1Name + " / " + normalizeName);
			} else {
				createContent(1, chid, smid, tableViewerDet1, motorPv, motorId,
						detector1Id);
				tableViewerDet1.getTable().getColumn(1).setText(detector1Name);
			}
			tableViewerDet1.getTable().getColumn(2).setText(motorName);
			tableViewerDet1.refresh();
			tableViewerDet1.getTable().setVisible(true);
		}

		if (detector2Id != null) {
			normalizeAxis2 = (plotWindow.getYAxes().get(1)
					.getNormalizeChannel() != null);

			if (normalizeAxis2) {
				String normalizeId = plotWindow.getYAxes().get(1)
						.getNormalizeChannel().getID();
				String normalizeName = plotWindow.getYAxes().get(1)
						.getNormalizeChannel().getName();
				createContent(2, chid, smid, tableViewerDet2, motorPv, motorId,
						detector2Id + "__" + normalizeId);
				tableViewerDet2.getTable().getColumn(1)
						.setText(detector2Name + " / " + normalizeName);
			} else {
				createContent(2, chid, smid, tableViewerDet2, motorPv, motorId,
						detector2Id);
				tableViewerDet2.getTable().getColumn(1).setText(detector2Name);
			}
			createContent(2, chid, smid, tableViewerDet2, motorPv, motorId,
					detector2Id);

			tableViewerDet2.getTable().getColumn(2).setText(motorName);
			tableViewerDet2.refresh();
			tableViewerDet2.getTable().setVisible(true);
		} else {
			// detector2Id is null => hide second table
			tableViewerDet2.getTable().setVisible(false);
		}
		
		this.layout();
		this.redraw();
		this.getParent().layout();
		this.getParent().redraw();
	}
	
	/*
	 * called by the refresh method...
	 * creates the content of the two statistics tables
	 */
	private void createContent(int axis, int chid, int smid, 
							   TableViewer tableViewer, String motorPv, 
							   String motorId, String detectorId) {

		// create a content provider for the table...
		MathTableContentProvider contentProvider = 
				(MathTableContentProvider) tableViewer.getContentProvider();
		// ...and clear it
		contentProvider.clear();
		
		MathTableElement element;
		
		// create the elements of interest and add them to the content provider
		
		if((axis == 1 && !normalizeAxis1) || (axis == 2 && !normalizeAxis2))
		{
			element = new MathTableElement(chid, smid, tableViewer, 
				MathFunction.UNMODIFIED, motorPv, motorId, detectorId);
			contentProvider.addElement(element);
		}
		if((axis == 1 && normalizeAxis1) || (axis == 2 && normalizeAxis2))
		{
			element = new MathTableElement(chid, smid, tableViewer, 
					MathFunction.NORMALIZED, motorPv, motorId, detectorId);
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
}