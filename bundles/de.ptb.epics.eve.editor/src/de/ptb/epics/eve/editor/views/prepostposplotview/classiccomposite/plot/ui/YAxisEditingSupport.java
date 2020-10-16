package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.YAxis;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class YAxisEditingSupport extends EditingSupport {
	private TableViewer viewer;
	private int axisIndex;
	private YAxisLabelProvider labelProvider;
	
	public YAxisEditingSupport(TableViewer viewer, 
			YAxisLabelProvider labelProvider, int axisIndex) {
		super(viewer);
		this.viewer = viewer;
		this.labelProvider = labelProvider;
		this.axisIndex = axisIndex;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		PlotWindow plotWindow = (PlotWindow)element;
		return new YAxisDialogCellEditor(this.viewer.getTable(), plotWindow);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		PlotWindow plotWindow = (PlotWindow)element;
		if (plotWindow.getYAxes().isEmpty()) {
			return null;
		}
		YAxis yAxis = plotWindow.getYAxes().get(this.axisIndex);
		return this.labelProvider.getText(yAxis);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		// values are set directly in the dialog, nothing to do here
	}
}
