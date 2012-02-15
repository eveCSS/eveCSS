package de.ptb.epics.eve.editor.views.eventcomposite;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;

/**
 * 
 * @author ?
 */
public class CellModifyer implements ICellModifier {

	private TableViewer tableViewer;
	
	/**
	 * Constructs a <code>ControlEventcellModifyer</code>.
	 * 
	 * @param tableViewer the table viewer the cell modifier is based on
	 * @throws IllegalArgumentException if the argument is <code>null</code>.
	 */
	public CellModifyer(final TableViewer tableViewer) {
		if(tableViewer == null) {
			throw new IllegalArgumentException(
					"The parameter 'tableViewer' must not be null!");
		}
		this.tableViewer = tableViewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canModify(final Object element, final String property) {
		
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue( final Object element, final String property ) {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modify( final Object element, final String property, final Object value ) {
		final ControlEvent controlEvent = (element instanceof Item)
				?(ControlEvent)((Item)element).getData()
				:(ControlEvent)element;
		 if( property.equals( "limit" ) ) {
			if( this.tableViewer.getCellEditors()[2] instanceof ComboBoxCellEditor ) {
				controlEvent.getLimit().setValue( 
						((ComboBoxCellEditor)this.tableViewer.getCellEditors()[2]).getItems()[ (Integer)value ] );
			} else {
				controlEvent.getLimit().setValue( 
						(String)((TextCellEditor)this.tableViewer.getCellEditors()[2]).getValue() );
			}
		}
	}	
}