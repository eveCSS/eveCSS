package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.PlotWindow;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class XAxisEditingSupport extends EditingSupport {
	private TableViewer viewer;
	private ColumnLabelProvider labelProvider;
	
	public XAxisEditingSupport(TableViewer viewer, ColumnLabelProvider labelProvider) {
		super(viewer);
		this.viewer = viewer;
		this.labelProvider = labelProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		PlotWindow plotWindow = (PlotWindow)element;
		return new XAxisDialogCellEditor(this.viewer.getTable(), plotWindow);
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
		return this.labelProvider.getText(element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		// Data is bound in the dialog to the model directly
	}
}
