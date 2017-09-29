package de.ptb.epics.eve.viewer.views.plotview.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.views.plotview.table.Average;
import de.ptb.epics.eve.viewer.views.plotview.table.Center;
import de.ptb.epics.eve.viewer.views.plotview.table.Data;
import de.ptb.epics.eve.viewer.views.plotview.table.Deviation;
import de.ptb.epics.eve.viewer.views.plotview.table.Edge;
import de.ptb.epics.eve.viewer.views.plotview.table.FWHM;
import de.ptb.epics.eve.viewer.views.plotview.table.Maximum;
import de.ptb.epics.eve.viewer.views.plotview.table.Minimum;
import de.ptb.epics.eve.viewer.views.plotview.table.Normalized;
import de.ptb.epics.eve.viewer.views.plotview.table.Unmodified;

/**
 * Hide-able part of the Plot View containing aggregate tables.
 * 
 * @author Marcus Michalsky
 * @since 1.22
 */
public class TableComposite extends Composite implements PlotViewComponent,
		PropertyChangeListener {
	private TableViewer det1TableViewer;
	private TableViewer det2TableViewer;
	TabItem det1TabItem;
	TabItem det2TabItem;
	
	private IObservableList det1Elements;
	private IObservableList det2Elements;
	
	private Image gotoIcon;
	
	/**
	 * Constructor.
	 * 
	 * @param parent the parent
	 * @param style the style
	 */
	public TableComposite(Composite parent, int style) {
		super(parent, style);
		
		this.gotoIcon = Activator.getDefault().getImageRegistry()
				.get("GREENGO12");
		
		this.det1Elements = new WritableList();
		this.det2Elements = new WritableList();
		
		this.setLayout(new FillLayout());
		TabFolder tabFolder = new TabFolder(this, SWT.BORDER);
		
		det1TabItem = new TabItem(tabFolder, SWT.NONE);
		det1TabItem.setText("-");
		Composite det1Composite = new Composite(tabFolder, SWT.NONE);
		det1Composite.setLayout(new FillLayout());
		det1TabItem.setControl(det1Composite);
		det1TableViewer = this.createTable(det1Composite);
		det1TableViewer.setContentProvider(new ObservableListContentProvider());
		det1TableViewer.setInput(det1Elements);
		
		det2TabItem = new TabItem(tabFolder, SWT.NONE);
		det2TabItem.setText("-");
		Composite det2Composite = new Composite(tabFolder, SWT.NONE);
		det2Composite.setLayout(new FillLayout());
		det2TabItem.setControl(det2Composite);
		det2TableViewer = this.createTable(det2Composite);
		det2TableViewer.setContentProvider(new ObservableListContentProvider());
		det2TableViewer.setInput(det2Elements);
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
				return ((Data)element).getDataModifier().toString();
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
				return ((Data)element).getDetectorValue();
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
				return ((Data) element).getMotorPosition();
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
				Data data = (Data)element;
				if (data.isGoToEnabled()) {
					data.gotoPos();
				}
				return false;
			}
		});
		gotoColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public Image getImage(Object element) {
				if (((Data)element).isGoToEnabled()) {
					return gotoIcon;
				}
				return null;
			}
			@Override
			public String getText(Object element) {
				return null;
			}
		});
		return tableViewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPlotWindow(PlotWindow plotWindow) {
		this.removeElements(det1Elements);
		this.removeElements(det2Elements);
		this.clearTables();
		
		List<YAxis> yAxes = plotWindow.getYAxes();
		if (!yAxes.isEmpty()) {
			this.addElements(det1Elements, plotWindow, 0);
			this.det1TableViewer.getTable().getColumn(2).setText(
					plotWindow.getXAxis().getName());
			this.det1TableViewer.getTable().getColumn(1).setText(
					yAxes.get(0).getNormalizedName());
			this.det1TabItem.setText(yAxes.get(0).getNormalizedName());
		}
		if (yAxes.size() > 1) {
			this.addElements(det2Elements, plotWindow, 1);
			this.det2TableViewer.getTable().getColumn(2).setText(
					plotWindow.getXAxis().getName());
			this.det2TableViewer.getTable().getColumn(1).setText(
					yAxes.get(1).getNormalizedName());
			this.det2TabItem.setText(yAxes.get(1).getNormalizedName());
		}
	}
	
	private void addElements(IObservableList list, PlotWindow plotWindow,
			int yAxis) {
		if (plotWindow.getYAxes().get(yAxis).getNormalizeChannel() != null) {
			this.addElement(list, new Normalized(plotWindow, yAxis));
		} else {
			this.addElement(list, new Unmodified(plotWindow, yAxis));
		}
		this.addElement(list, new Minimum(plotWindow, yAxis));
		this.addElement(list, new Maximum(plotWindow, yAxis));
		this.addElement(list, new Center(plotWindow, yAxis));
		this.addElement(list, new Edge(plotWindow, yAxis));
		this.addElement(list, new Average(plotWindow, yAxis));
		this.addElement(list, new Deviation(plotWindow, yAxis));
		this.addElement(list, new FWHM(plotWindow, yAxis));
	}
	
	private void addElement(IObservableList list, Data element) {
		element.addPropertyChangeListener(Data.MOTOR_PROP, this);
		element.addPropertyChangeListener(Data.DETECTOR_PROP, this);
		list.add(element);
	}
	
	private void removeElements(IObservableList list) {
		for (Object o: list) {
			((Data)o).removePropertyChangeListener(Data.MOTOR_PROP, this);
			((Data)o).removePropertyChangeListener(Data.DETECTOR_PROP, this);
		}
		list.clear();
	}
	
	private void clearTables() {
		this.det1TableViewer.getTable().getColumn(1).setText("Channel");
		this.det1TableViewer.getTable().getColumn(2).setText("Axis");
		this.det1TabItem.setText(Data.NO_VALUE);
		
		this.det2TableViewer.getTable().getColumn(1).setText("Channel");
		this.det2TableViewer.getTable().getColumn(2).setText("Axis");
		this.det2TabItem.setText(Data.NO_VALUE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(Data.MOTOR_PROP) || 
				event.getPropertyName().equals(Data.DETECTOR_PROP)) {
			if (event.getSource() instanceof Data) {
				final Data data = (Data)event.getSource();
				if (data.getyAxis() == 0) {
					det1TableViewer.getControl().getDisplay()
							.asyncExec(new Runnable() {
						@Override
						public void run() {
							det1TableViewer.update(data, null);
						}
					});
				} else if (data.getyAxis() == 1) {
					det2TableViewer.getControl().getDisplay()
							.asyncExec(new Runnable() {
						@Override
						public void run() {
							det2TableViewer.update(data, null);
						}
					});
				}
			}
		}
	}

	/**
	 * @return the det1Elements
	 */
	public IObservableList getDet1Elements() {
		return det1Elements;
	}

	/**
	 * @return the det2Elements
	 */
	public IObservableList getDet2Elements() {
		return det2Elements;
	}
}