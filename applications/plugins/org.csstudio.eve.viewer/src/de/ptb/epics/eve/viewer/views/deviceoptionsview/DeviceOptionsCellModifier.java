package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

/**
 * <code>DeviceOptionsCellModifier</code> is the cell modifier of the table 
 * viewer defined in 
 * {@link de.ptb.epics.eve.viewer.views.deviceoptionsview.DeviceOptionsView}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class DeviceOptionsCellModifier implements ICellModifier {

	private final TableViewer tableViewer;
	
	/**
	 * Constructs a <code>DeviceOptionsCellModifier</code>.
	 * 
	 * @param tableViewer the table viewer the modifier is assigned to
	 */
	public DeviceOptionsCellModifier(final TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canModify(final Object element, String property) {
		if(property.equals("value") && ((OptionConnector)element).isConnected()) {
			final OptionConnector optionConnector = (OptionConnector)element;
			if (optionConnector.isReadOnly()) return false;
			final CellEditor[] cellEditors = tableViewer.getCellEditors();
			if(optionConnector.isDiscrete()) {
				final String[] items = optionConnector.getDiscreteValues();
				if (!(cellEditors[1] instanceof ComboBoxCellEditor)) {
					cellEditors[1].dispose();
					cellEditors[1] = new ComboBoxCellEditor(
							this.tableViewer.getTable(), items, SWT.READ_ONLY);
				} else {
					((ComboBoxCellEditor)cellEditors[1]).setItems(items);
				}
			} else {
				if (!(cellEditors[1] instanceof TextCellEditor)) {
					cellEditors[1].dispose();
					cellEditors[1] = new TextCellEditor(
							this.tableViewer.getTable());
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue(final Object element, String property) {
		final CellEditor[] cellEditors = tableViewer.getCellEditors();
		if (cellEditors[1] instanceof ComboBoxCellEditor) {
			final String currentValue = ((OptionConnector)element).getValue();
			final String[] items = ((ComboBoxCellEditor)cellEditors[1]).getItems();
			for(int i = 0; i < items.length; ++i) {
				if(items[i].equals( currentValue)) {
					return i;
				}
			}
		}
		return ((OptionConnector)element).getValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modify(final Object element, final String property, final Object value) {
		final TableItem tableItem = (TableItem)element;
		final OptionConnector optionConnector = (OptionConnector)tableItem.getData();
		if (!optionConnector.isReadOnly()) optionConnector.setValue( value.toString());
	}
}