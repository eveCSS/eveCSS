package de.ptb.epics.eve.editor.views;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Item;

import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.ControlEvent;
import de.ptb.epics.eve.data.scandescription.PauseEvent;

/**
 * 
 * @author ?
 */
public class ControlEventCellModifyer implements ICellModifier {

	private TableViewer tableViewer;
	
	/**
	 * Constructs a <code>ControlEventcellModifyer</code>.
	 * 
	 * @param tableViewer the table viewer the cell modifier is based on
	 * @throws IllegalArgumentException if the argument is <code>null</code>.
	 */
	public ControlEventCellModifyer(final TableViewer tableViewer) {
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
		
		if( property.equals( "source" ) || ((ControlEvent)element).getEvent().getType() == EventTypes.SCHEDULE 
										|| ((ControlEvent)element).getEvent().getType() == EventTypes.DETECTOR ) {
			return false;
		}
		if( property.equals( "operator" ) ) {
			((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).setItems( ComparisonTypes.typeToString( DataTypes.getPossibleComparisonTypes( ((ControlEvent)element).getEvent().getMonitor().getDataType().getType() ) ) );
		} else if( property.equals( "limit" ) ) {
			if( this.tableViewer.getCellEditors()[2] instanceof TextCellEditor && ((ControlEvent)element).getEvent().getMonitor().getDataType().isDiscrete() ) {
				
				this.tableViewer.getCellEditors()[2].dispose();
				this.tableViewer.getCellEditors()[2] = new ComboBoxCellEditor( this.tableViewer.getTable(), ((ControlEvent)element).getEvent().getMonitor().getDataType().getDiscreteValues().toArray( new String[0] ), SWT.READ_ONLY );
				
			} else if( this.tableViewer.getCellEditors()[2] instanceof ComboBoxCellEditor && ((ControlEvent)element).getEvent().getMonitor().getDataType().isDiscrete() ) {
				
				((ComboBoxCellEditor)this.tableViewer.getCellEditors()[2]).setItems( ((ControlEvent)element).getEvent().getMonitor().getDataType().getDiscreteValues().toArray( new String[0] ) );
				
			} else if( this.tableViewer.getCellEditors()[2] instanceof ComboBoxCellEditor && !((ControlEvent)element).getEvent().getMonitor().getDataType().isDiscrete() ) {
				
				this.tableViewer.getCellEditors()[2].dispose();
				this.tableViewer.getCellEditors()[2] = new TextCellEditor( this.tableViewer.getTable() );
				
			}
		} else if( property.equals( "cif" ) ) {
			return true;
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue( final Object element, final String property ) {
		if( property.equals( "operator" ) ) {
			final String[] operators = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).getItems();
			for( int i = 0; i < operators.length; ++i ) {
				if( operators[i].equals( ComparisonTypes.typeToString( ((ControlEvent)element).getLimit().getComparison() ) ) ) {
					return i;
				}
			}
			return -1;
		} else if( property.equals( "limit" ) ) {
			if( this.tableViewer.getCellEditors()[2] instanceof ComboBoxCellEditor ) {
				final String[] values = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[2]).getItems();
				for( int i = 0; i < values.length; ++i ) {
					if( values[i].equals( ((ControlEvent)element).getLimit().getValue() ) ) {
						return i;
					}
				}
				return -1;
			}
			return ((ControlEvent)element).getLimit().getValue();
		} else if( property.equals( "cif" ) ) {
			return new Boolean( ((PauseEvent)element).isContinueIfFalse() );
		}
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modify( final Object element, final String property, final Object value ) {
		System.out.println("\nModify von ControlEventCellModifyer aufgerufen, property: " + property);
		final ControlEvent controlEvent = (element instanceof Item)?(ControlEvent)((Item)element).getData():(ControlEvent)element;
		if( property.equals( "operator" ) ) {
			controlEvent.getLimit().setComparison( ComparisonTypes.stringToType( ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).getItems()[ (Integer)value ] ) );
		} else if( property.equals( "limit" ) ) {
			if( this.tableViewer.getCellEditors()[2] instanceof ComboBoxCellEditor ) {
				controlEvent.getLimit().setValue( ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[2]).getItems()[ (Integer)value ] );
			} else {
				controlEvent.getLimit().setValue( (String)((TextCellEditor)this.tableViewer.getCellEditors()[2]).getValue() );
			}
		} else if( property.equals( "cif" ) ) {
			final PauseEvent pauseEvent = (PauseEvent)controlEvent;
			pauseEvent.setContinueIfFalse( (Boolean)value );
		}
	}	
}