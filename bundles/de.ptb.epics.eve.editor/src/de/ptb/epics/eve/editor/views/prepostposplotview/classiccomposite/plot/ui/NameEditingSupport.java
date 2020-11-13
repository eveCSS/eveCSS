package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import de.ptb.epics.eve.data.scandescription.PlotWindow;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class NameEditingSupport extends EditingSupport {
	private TableViewer viewer;
	
	public NameEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor(viewer.getTable()) {
			@Override
			protected void focusLost() {
				if (isActivated()) {
					fireCancelEditor();
				}
				deactivate();
				viewer.refresh();
			}
		};
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
		return ((PlotWindow)element).getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		PlotWindow plotWindow = (PlotWindow)element;
		plotWindow.setName(value.toString());
	}
}
