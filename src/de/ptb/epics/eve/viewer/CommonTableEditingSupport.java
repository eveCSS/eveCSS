package de.ptb.epics.eve.viewer;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

public class CommonTableEditingSupport extends EditingSupport {
	
	private TableViewer viewer;
	private String column;
	private TextCellEditor textEditor;
	
	public CommonTableEditingSupport(TableViewer viewer, String column) {
		super(viewer);
		this.viewer = viewer;
		this.column = column;
		// TODO never used
		textEditor = new TextCellEditor(viewer.getTable());
	}

	@Override
	protected boolean canEdit(Object element) {
		System.err.println("CommonTableEditingSupport: canEdit");
		if (column.equals("remove")) {
			((CommonTableContentProvider)viewer.getInput()).removeElement(element);
		}
		if (column.equals("trigger")) {
			((CommonTableElement) element).trigger();
		}
		else {
			if (!((CommonTableElement) element).isReadonly(column))return true;
		}
		return false;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		System.err.println("CommonTableEditingSupport: getCellEditor");

		CommonTableElement ctb = (CommonTableElement) element;
		
		if (ctb.getCellEditor(column) == null){
			if (ctb.isDiscrete(column)){
				 ctb.setCellEditor (new ComboBoxCellEditor(viewer.getTable(), ctb.getSelectStrings(column)), column);
			}
			else {
				 ctb.setCellEditor ( new TextCellEditor(viewer.getTable()), column);
			}
		}
		return ctb.getCellEditor(column);
	}

	@Override
	protected Object getValue(Object element) {

		System.err.println("CommonTableEditingSupport: getValue");
		CommonTableElement ctb = (CommonTableElement) element;
		CellEditor ceditor = ctb.getCellEditor(column);
		if (ceditor instanceof ComboBoxCellEditor){
			int count = 0;
			String currentVal = ctb.getValue(column);
			for (String selection : ctb.getSelectStrings(column)) {
				if (selection.startsWith(currentVal)) return count;
				++count;
			}
			return 0;
		}
		else
			return ctb.getValue(column);
	}

	@Override
	protected void setValue(Object element, Object value) {
		System.err.println("CommonTableEditingSupport: setValue");
		// TODO Auto-generated method stub
		CommonTableElement ctb = (CommonTableElement) element;
		ctb.setValue(value, column);
	}

}
