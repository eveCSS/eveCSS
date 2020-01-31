package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class MainAxisEditingSupport extends EditingSupport {

	public MainAxisEditingSupport(TableViewer viewer) {
		super(viewer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		Axis axis = (Axis)element;
		if (Stepfunctions.ADD.equals(axis.getStepfunction()) || 
				Stepfunctions.MULTIPLY.equals(axis.getStepfunction())) {
			if (axis.isMainAxis()) {
				axis.setMainAxis(false);
			} else {
				axis.setMainAxis(true);
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		// fake editing via canEdit...
	}
}
