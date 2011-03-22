package de.ptb.epics.eve.viewer.views;

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
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.plot.MathFunction;
import de.ptb.epics.eve.viewer.plot.MathTableContentProvider;
import de.ptb.epics.eve.viewer.plot.MathTableElement;

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
		
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		setLayoutData( gridData );
		
		// detectorLabel1 = new Label(this, SWT.None);
		tableViewerDet1 = makeTable();
		// detectorLabel2 = new Label(this, SWT.None);
		tableViewerDet2 = makeTable();
	}
	
	/*
	 * called by the constructor to construct the table
	 */
	private TableViewer makeTable(){
		
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
										new ColumnPixelData(75));

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
										new ColumnPixelData(100));

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
	 * 
	 * 
	 * @param chid
	 * @param smid
	 * @param motorId
	 * @param motorName
	 * @param motorPv
	 * @param detector1Id
	 * @param detector1Name
	 * @param detector2Id
	 * @param detector2Name
	 */
	public void refresh(int chid, int smid, String motorId, String motorName, 
						 String motorPv, String detector1Id, 
						 String detector1Name, String detector2Id, 
						 String detector2Name) {
		
		if (detector1Id != null)
		{
			createContent(chid, smid, tableViewerDet1, motorPv, motorId, 
						  detector1Id);
			
			tableViewerDet1.getTable().getColumn(1).setText(detector1Name);
			tableViewerDet1.getTable().getColumn(2).setText(motorName);
			tableViewerDet1.refresh();
			tableViewerDet1.getTable().setVisible(true);
		}
		
		if (detector2Id != null)
		{
			createContent(chid, smid, tableViewerDet2, motorPv, motorId, 
						  detector2Id);
			
			tableViewerDet2.getTable().getColumn(1).setText(detector2Name);
			tableViewerDet2.getTable().getColumn(2).setText(motorName);
			tableViewerDet2.refresh();
			tableViewerDet2.getTable().setVisible(true);
		}
		else
		{
			// detector2Id is null => hide second table
			tableViewerDet2.getTable().setVisible(false);
		}

		// TODO next statement should be somewhere else.
		// just added to get rid of the editor area temporarily
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().
					setEditorAreaVisible(false);
		
		this.layout();
		this.redraw();
		this.getParent().layout();
		this.getParent().redraw();
	}
	
	/*
	 * called by the refresh method...
	 * creates the content of the two statistics tables
	 */
	private void createContent(int chid, int smid, TableViewer tableViewer, 
						String motorPv, String motorId, String detectorId) {

		// create a content provider for the table...
		MathTableContentProvider contentProvider = 
				(MathTableContentProvider) tableViewer.getContentProvider();
		// ...and clear it
		contentProvider.clear();
		
		// create the elements of interest and add them to the content provider
		
		MathTableElement element = new MathTableElement(chid, smid, tableViewer, 
				MathFunction.UNMODIFIED, motorPv, motorId, detectorId);
		contentProvider.addElement(element);
		
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