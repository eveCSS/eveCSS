/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

import de.ptb.epics.eve.data.scandescription.Prescan;

public class PrescanCellModifyer implements ICellModifier {

	private final TableViewer tableViewer;
	
	public PrescanCellModifyer( final TableViewer tableViewer ) {
		this.tableViewer = tableViewer;
	}
	
	public boolean canModify( final Object element, final String property ) {
		if (property.equals("value")) {
			// mögliche Auswahl muß erstellt werden
			final Prescan prescan = (Prescan)element;

			if (prescan.getAbstractPrePostscanDevice().isDiscrete()){
				// Prescan erlaubt nur disrecte Werte => ComboBoxCellEditor

			    if (this.tableViewer.getCellEditors()[1] instanceof TextCellEditor) {
					// aus dem TextCellEditor eine ComobBox machen
					this.tableViewer.getCellEditors()[1].dispose();
					this.tableViewer.getCellEditors()[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), prescan.getAbstractPrePostscanDevice().getValue().getDiscreteValues().toArray(new String[0]), SWT.READ_ONLY);
				}
			    else if( this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor) {
			    	// nur die möglichen Werte hinzufügen, ComboBox ist schon vorhanden
			    	((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).setItems(prescan.getAbstractPrePostscanDevice().getValue().getDiscreteValues().toArray(new String[0]));
			    }
			}
			else {
				// Prescan erlaubt freie Werteingabe => TextCellEditor 
			    if (this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor) {
					// aus der ComboBox einen TextCellEditor machen
					this.tableViewer.getCellEditors()[1].dispose();
					this.tableViewer.getCellEditors()[1] = new TextCellEditor( this.tableViewer.getTable());
			    }
			}
		}
		return property.equals( "value" );
	}

	public Object getValue( final Object element, final String property ) {
		final Prescan prescan = (Prescan)element;
		if( property.equals( "value" ) ) {
		    if (this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor) {
		    	// Feld ist ein ComboBoxCellEditor
		    	final String[] operators = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).getItems();
		    	for( int i = 0; i < operators.length; ++i ) {
		    		if( operators[i].equals(prescan.getValue())) {
		    			return i;
		    		}
		    	}
		    	// mit return 0 wird der erste Wert voreingestellt
		    	return 0;
		    }		    
		    else {
		    	// Feld ist ein TextCellEditor
				return ((Prescan)element).getValue();
		    }
		}
		return -1;
	}

	public void modify( final Object element, final String property, final Object value ) {
		final Prescan prescan = (Prescan)((TableItem)element).getData();
		if( property.equals( "value" ) ) {
		    if (this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor) {
		    	// Feld ist ein ComboBoxCellEditor
		    	final String[] operators = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).getItems();
		    	prescan.setValue(operators[(Integer)value]);
		    }
		    else {
		    	// Feld ist ein TextCellEditor
			    prescan.setValue(value.toString());
		    }
		}
		this.tableViewer.refresh();
	}

}
