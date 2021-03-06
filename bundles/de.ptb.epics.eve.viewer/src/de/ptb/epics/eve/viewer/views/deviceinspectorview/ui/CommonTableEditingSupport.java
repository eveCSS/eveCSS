package de.ptb.epics.eve.viewer.views.deviceinspectorview.ui;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;

import de.ptb.epics.eve.util.ui.jface.MyComboBoxCellEditor;
import de.ptb.epics.eve.viewer.Activator;
import de.ptb.epics.eve.viewer.preferences.PreferenceConstants;
import de.ptb.epics.eve.viewer.views.deviceinspectorview.CommonTableElement;

/**
 * <code>CommonTableEditingSupport</code> is the 
 * {@link org.eclipse.jface.viewers.EditingSupport} for the table viewers 
 * defined in 
 * {@link de.ptb.epics.eve.viewer.views.deviceinspectorview.ui.DeviceInspectorView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class CommonTableEditingSupport extends EditingSupport {
	private static final Logger LOGGER = 
			Logger.getLogger(CommonTableEditingSupport.class.getName());
	
	// the table viewer the editing support belongs to
	private TableViewer viewer;
	
	// the column name
	private String column;
	
	/**
	 * Constructs a <code>CommonTableEditingSupport</code>.
	 * 
	 * @param viewer the table viewer the editing support is based on
	 * @param column the column of the table the support is based on
	 */
	public CommonTableEditingSupport(TableViewer viewer, String column) {
		super(viewer);
		this.viewer = viewer;
		this.column = column;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		// TODO do not execute stuff in here
		// this function should only determine if a column is editable
		if (column.equals("remove")) {
			((CommonTableContentProvider)viewer.getInput()).removeElement(element);
		} else if (column.equals("trigger")) {
			((CommonTableElement) element).trigger();
		} else if (column.equals("stop")) {
			((CommonTableElement) element).stop();
		} else if (column.equals("tweakforward")) {
			((CommonTableElement) element).tweak(true);
		} else if (column.equals("tweakreverse")) {
			((CommonTableElement) element).tweak(false);
		} else {
			CommonTableElement ctb = (CommonTableElement) element;
			if (!ctb.isReadonly(column) && ctb.isConnected(column)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		CommonTableElement ctb = (CommonTableElement) element;
		
		//if (ctb.getCellEditor(column) == null) {
			if (ctb.isDiscrete(column)) {
				ctb.setCellEditor(new MyComboBoxCellEditor(viewer.getTable(), 
						ctb.getSelectStrings(column)), column);
			} else {
				TextCellEditor textCellEditor = 
						new TextCellEditor(viewer.getTable()) {
					@Override protected void focusLost() {
						if (isActivated()) {
							fireCancelEditor();
						}
						deactivate();
					}
				};
				ctb.setCellEditor(textCellEditor, column);
			}
		//}
		return ctb.getCellEditor(column);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		CommonTableElement ctb = (CommonTableElement) element;
		CellEditor ceditor = ctb.getCellEditor(column);
		if (ceditor instanceof MyComboBoxCellEditor) {
			int count = 0;
			String currentVal = ctb.getValue(column);
			for (String selection : ctb.getSelectStrings(column)) {
				if (selection.startsWith(currentVal)) {
					return count;
				}
				++count;
			}
			return 0;
		}
		return ctb.getValue(column);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		CommonTableElement ctb = (CommonTableElement) element;

		if (value.equals("")) {
			/* no value set => return without an action */
			return;
		}
		
		if (column.equals("define")) {
			boolean oldState = Activator.getDefault().getPreferenceStore().
					getBoolean(PreferenceConstants.P_SHOW_DEFINE_CONFIRM_DIALOG);
			if (oldState) {
				MessageDialogWithToggle defineDialog = 
					MessageDialogWithToggle.openOkCancelConfirm(
						viewer.getControl().getShell(), 
						"Confirm Define of Axis " + ctb.getAbstractDevice().getName(), 
						"You are about to define axis " + ctb.getAbstractDevice().getName() + ". Continue ?", 
						"Do not show this dialog again", 
						!oldState,
						Activator.getDefault().getPreferenceStore(), 
						PreferenceConstants.P_SHOW_DEFINE_CONFIRM_DIALOG);
				if (defineDialog.getReturnCode() == Window.CANCEL) {
					LOGGER.debug("define confirm dialog was canceled.");
					return;
				}
				LOGGER.debug("dialog show define confirm property: " + 
						defineDialog.getToggleState());
			}
		}
		ctb.setValue(value, column);
	}
}