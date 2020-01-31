package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PositionMode;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class PositionModeEditingSupport extends EditingSupport {
	private TableViewer viewer;
	
	public PositionModeEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new MyComboBoxCellEditor(this.viewer.getTable(), 
				new String[] {"relative", "absolute"});
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		PositionMode posMode = ((Axis)element).getPositionMode();
		if (posMode.equals(PositionMode.ABSOLUTE)) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	protected void setValue(Object element, Object value) {
		int index = (Integer)value;
		PositionMode[] positionModes = PositionMode.values();
		((Axis)element).setPositionMode(positionModes[index]);
	}
}
