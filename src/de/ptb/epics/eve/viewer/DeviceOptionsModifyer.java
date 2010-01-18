package de.ptb.epics.eve.viewer;

import org.csstudio.platform.simpledal.ConnectionState;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.TableItem;

import de.ptb.epics.eve.data.measuringstation.Option;

public class DeviceOptionsModifyer implements ICellModifier {

	private final TableViewer tableViewer;
	
	public DeviceOptionsModifyer( final TableViewer tableViewer ) {
		this.tableViewer = tableViewer;
	}

	public boolean canModify( final Object element, String property ) {
		if( property.equals( "value" ) && ((OptionConnector)element).getConnectionState() == ConnectionState.CONNECTED ) {
			final OptionConnector optionConnector = (OptionConnector)element;
			final CellEditor[] cellEditors = tableViewer.getCellEditors();
			if( optionConnector.getOption().isDiscrete() ) {
				final String[] items = optionConnector.getOption().getValue().getDiscreteValues().toArray( new String[0] );
				if( !(cellEditors[1] instanceof ComboBoxCellEditor) ) {
					cellEditors[1].dispose();
					cellEditors[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), items );
				} else {
					((ComboBoxCellEditor)cellEditors[1]).setItems(items);
					optionConnector.getOption().getValue().getDiscreteValues().toArray( new String[0] );
				}
			} else {
				if( !(cellEditors[1] instanceof TextCellEditor) ) {
					cellEditors[1].dispose();
					cellEditors[1] = new TextCellEditor( this.tableViewer.getTable() );
				}
			}
			return true;
		}
		return false;
	}

	public Object getValue( final Object element, String property ) {
		final CellEditor[] cellEditors = tableViewer.getCellEditors();
		if( cellEditors[1] instanceof ComboBoxCellEditor ) {
			final String currentValue = ((OptionConnector)element).getValue();
			final String[] items = ((ComboBoxCellEditor)cellEditors[1]).getItems();
			for( int i = 0; i < items.length; ++i ) {
				if( items[i].equals( currentValue ) ) {
					return i;
				}
			}
		}
		return ((OptionConnector)element).getValue();
	}

	public void modify( final Object element, final String property, final Object value ) {
		final TableItem tableItem = (TableItem)element;
		((OptionConnector)tableItem.getData()).setValue( (String)value );

	}

}
