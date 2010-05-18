/**
 * 
 */
package de.ptb.epics.eve.viewer;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ValueType;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;

import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.intern.ChainStatus;
import de.ptb.epics.eve.ecp1.intern.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.intern.DataModifier;

/**
 * @author eden
 *
 */
public class PlotDetectorComposite extends Composite {


	private int chid;
	private int smid;
	private TableViewer tableViewerDet1;
	private TableViewer tableViewerDet2;
	private Image gotoIcon;
	private Label detectorLabel1;
	private Label detectorLabel2;

	public PlotDetectorComposite(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub

		gotoIcon = Activator.getDefault().getImageRegistry().get("GREENGO12");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		setLayout( gridLayout );
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		setLayoutData( gridData );
		
		detectorLabel1 = new Label(this, SWT.None);
		tableViewerDet1 = makeTable();
		detectorLabel2 = new Label(this, SWT.None);
		tableViewerDet2 = makeTable();

	}
	
	private TableViewer makeTable(){
		
		Composite tableComposite = new Composite(this, SWT.FULL_SELECTION);
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);

		TableViewer tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);

		tableViewer.getTable().setHeaderVisible(false);
		tableViewer.getTable().setLinesVisible(true);

		MathTableContentProvider contentProvider = new MathTableContentProvider(tableViewer);
		tableViewer.setContentProvider(contentProvider);

		TableViewerColumn nameColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		nameColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((MathTableElement) element).getName();
			}
		});
		tableColumnLayout.setColumnData(nameColumn.getColumn(), new ColumnPixelData(75));

		TableViewerColumn valueColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		//valueColumn.getColumn().setText("Value");
		valueColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((MathTableElement) element).getValue();
			}
		});
		tableColumnLayout.setColumnData(valueColumn.getColumn(), new ColumnPixelData(75));

		TableViewerColumn motorColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		//motorColumn.getColumn().setText("Value");
		motorColumn.setLabelProvider(new ColumnLabelProvider() {
			public String getText(Object element) {
				return ((MathTableElement) element).getPosition();
			}
		});
		tableColumnLayout.setColumnData(motorColumn.getColumn(), new ColumnPixelData(75));

		TableViewerColumn gotoColumn = new TableViewerColumn(tableViewer, SWT.NONE);
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
		tableColumnLayout.setColumnData(gotoColumn.getColumn(), new ColumnPixelData(22));

		tableViewer.setInput(contentProvider);
		tableViewer.getTable().setVisible(false);
		return tableViewer;
	}
	
	public void refresh(int chid, int smid, String motorId, String motorName, String motorPv, String detector1Id, String detector1Name, String detector2Id, String detector2Name) {
		
		if (detector1Id != null){
			createContent(chid, smid, tableViewerDet1, motorPv, motorId, detector1Id);
			tableViewerDet1.getTable().setVisible(true);
			detectorLabel1.setText(detector1Name);
		}
		
		if (detector2Id != null){
			createContent(chid, smid, tableViewerDet2, motorPv, motorId, detector2Id);
			tableViewerDet2.getTable().setVisible(true);
			detectorLabel2.setText(detector2Name);
		}
		
		this.layout();
		this.redraw();
		this.getParent().layout();
		this.getParent().redraw();

	}
	
	private void createContent(int chid, int smid, TableViewer tableViewer, String motorPv, String motorId, String detectorId){

		MathTableContentProvider contentProvider = (MathTableContentProvider)tableViewer.getContentProvider();
		contentProvider.clear();
		MathTableElement element = new MathTableElement(chid, smid, tableViewer, MathFunction.UNMODIFIED, motorPv, motorId, detectorId);
		contentProvider.addElement(element);
		element = new MathTableElement(chid, smid, tableViewer, MathFunction.MINIMUM, motorPv, motorId, detectorId);
		contentProvider.addElement(element);
		element = new MathTableElement(chid, smid, tableViewer, MathFunction.MAXIMUM, motorPv, motorId, detectorId);
		contentProvider.addElement(element);
		element = new MathTableElement(chid, smid, tableViewer, MathFunction.CENTER, motorPv, motorId, detectorId);
		contentProvider.addElement(element);
		element = new MathTableElement(chid, smid, tableViewer, MathFunction.EDGE, motorPv, motorId, detectorId);
		contentProvider.addElement(element);
		element = new MathTableElement(chid, smid, tableViewer, MathFunction.AVERAGE, motorPv, motorId, detectorId);
		contentProvider.addElement(element);
		element = new MathTableElement(chid, smid, tableViewer, MathFunction.DEVIATION, motorPv, motorId, detectorId);
		contentProvider.addElement(element);
		element = new MathTableElement(chid, smid, tableViewer, MathFunction.FWHM, motorPv, motorId, detectorId);
		contentProvider.addElement(element);	
	}
}
