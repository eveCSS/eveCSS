package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.util.io.StringUtil;
import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class StepfunctionEditingSupport extends EditingSupport {
	private TableViewer viewer;
	
	public StepfunctionEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new MyComboBoxCellEditor(this.viewer.getTable(), 
				StringUtil.getStringList(((Axis)element).getStepfunctions()).
						toArray(new String[] {}));
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		Axis axis = (Axis)element;
		if (axis.getMotorAxis().getGoto().isDiscrete()) {
			switch (axis.getStepfunction()) {
			case FILE:
				return 0;
			case PLUGIN:
				return 1;
			case POSITIONLIST:
				return 2;
			default:
				return 0;
			}
		} else {
			return (((Axis)element).getStepfunction().ordinal());
		}
	}

	@Override
	protected void setValue(Object element, Object value) {
		int index = (Integer)value;
		Stepfunctions[] stepFunctions = Stepfunctions.values();
		((Axis)element).setStepfunction(stepFunctions[index]);
	}
}
