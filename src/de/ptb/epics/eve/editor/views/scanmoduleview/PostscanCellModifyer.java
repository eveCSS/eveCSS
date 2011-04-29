/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Postscan;

public class PostscanCellModifyer implements ICellModifier {

	private final TableViewer tableViewer;
	
	public PostscanCellModifyer( final TableViewer tableViewer ) {
		this.tableViewer = tableViewer;
	}
	
	public boolean canModify( final Object element, final String property ) {
		if (property.equals("value")) {
			// mögliche Auswahl muß erstellt werden
			final Postscan postscan = (Postscan)element;
			if (postscan.isReset())
				return false;
			
			if (postscan.getAbstractPrePostscanDevice().isDiscrete()){
				// Postscan erlaubt nur disrecte Werte => ComboBoxCellEditor
			    if (this.tableViewer.getCellEditors()[1] instanceof TextCellEditor) {
					// aus dem TextCellEditor eine ComobBox machen
					this.tableViewer.getCellEditors()[1].dispose();

			    	// Wenn der PostcanDatatype on/off oder open/close ist, dieses zur Auswahl stellen und nicht die
			    	// vorhandenen Zahlen.
			    	if (postscan.getAbstractPrePostscanDevice().getValue().getType().equals(DataTypes.ONOFF)) {
			    		this.tableViewer.getCellEditors()[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), new String[] { "On", "Off"}, SWT.READ_ONLY);
			    	}
			    	else if (postscan.getAbstractPrePostscanDevice().getValue().getType().equals(DataTypes.OPENCLOSE)) {
			    		this.tableViewer.getCellEditors()[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), new String[] { "Open", "Close"}, SWT.READ_ONLY);
			    	}
			    	else
			    		this.tableViewer.getCellEditors()[1] = new ComboBoxCellEditor( this.tableViewer.getTable(), postscan.getAbstractPrePostscanDevice().getValue().getDiscreteValues().toArray(new String[0]), SWT.READ_ONLY);
				}
			    else if( this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor) {
			    	// nur die möglichen Werte hinzufügen, ComboBox ist schon vorhanden

			    	// Wenn der PostscanDatatype on/off oder open/close ist, dieses zur Auswahl stellen und nicht die
			    	// vorhandenen Zahlen.
			    	if (postscan.getAbstractPrePostscanDevice().getValue().getType().equals(DataTypes.ONOFF)) {
			    		((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).setItems(new String[] { "On", "Off" });
			    	}
			    	else if (postscan.getAbstractPrePostscanDevice().getValue().getType().equals(DataTypes.OPENCLOSE)) {
			    		((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).setItems(new String[] { "Open", "Close" });
			    	}
			    	else
			    		((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).setItems(postscan.getAbstractPrePostscanDevice().getValue().getDiscreteValues().toArray(new String[0]));
			    }
			}
			else {
				// Postscan erlaubt freie Werteingabe => TextCellEditor 
			    if (this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor) {
					// aus der ComboBox einen TextCellEditor machen
					this.tableViewer.getCellEditors()[1].dispose();
					this.tableViewer.getCellEditors()[1] = new TextCellEditor( this.tableViewer.getTable());
			    }
			}
			return property.equals( "value" );
		}
		else if (property.equals("reset")) {
			return property.equals( "reset" );
		}
		return false;
	}

	public Object getValue( final Object element, final String property ) {
		final Postscan postscan = (Postscan)element;
		if( property.equals( "value" ) ) {
		    if (this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor) {
		    	// Feld ist ein ComboBoxCellEditor
		    	final String[] operators = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).getItems();
		    	for( int i = 0; i < operators.length; ++i ) {
		    		if( operators[i].equals(postscan.getValue())) {
		    			return i;
		    		}
		    	}
		    	// mit return 0 wird der erste Wert voreingestellt
		    	return 0;
		    }		    
		    else {
		    	// Feld ist ein TextCellEditor
				return ((Postscan)element).getValue();
		    }
		}
		if( property.equals( "reset" ) ) {
			// Feld ist ein ComboBoxCellEditor
		    if (postscan.isReset())
		    	return 0;	// reset = yes
		    else
		    	return 1;	// reset = no
		}
		return -1;
	}

	public void modify( final Object element, final String property, final Object value ) {
		final Postscan postscan = (Postscan)((TableItem)element).getData();
		if( property.equals( "value" ) ) {
		    if (this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor) {
		    	// Feld ist ein ComboBoxCellEditor
		    	
		    	// Wenn Datentyp OnOff oder OpenClose ist, wird nicht OnOff oder OpenClose gesetzt sondern der 
		    	// echte Wert des PrePostscanDevice
		    	if (postscan.getAbstractPrePostscanDevice().getValue().getType().equals(DataTypes.ONOFF)) {
			    	final String[] auswahl = (postscan.getAbstractPrePostscanDevice().getValue().getDiscreteValues().toArray(new String[0]));
			    	postscan.setValue(auswahl[(Integer)value]);
		    	}
		    	else if (postscan.getAbstractPrePostscanDevice().getValue().getType().equals(DataTypes.OPENCLOSE)) {
			    	final String[] auswahl = (postscan.getAbstractPrePostscanDevice().getValue().getDiscreteValues().toArray(new String[0]));
			    	postscan.setValue(auswahl[(Integer)value]);
		    	}
		    	else {
		    		final String[] operators = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[1]).getItems();
		    		postscan.setValue(operators[(Integer)value]);
		    	}
		    }
		    else {
		    	// Feld ist ein TextCellEditor
			    postscan.setValue(value.toString());
		    }
		}
		if( property.equals( "reset" ) ) {
	    	final String[] operators = ((ComboBoxCellEditor)this.tableViewer.getCellEditors()[2]).getItems();
	    	if (operators[(Integer)value].equals("yes"))
	    		postscan.setReset(true);
	    	else
	    		postscan.setReset(false);
		}
		this.tableViewer.refresh();
	}

}
