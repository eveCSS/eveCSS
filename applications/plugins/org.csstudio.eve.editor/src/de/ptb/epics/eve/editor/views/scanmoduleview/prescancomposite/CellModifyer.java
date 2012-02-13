package de.ptb.epics.eve.editor.views.scanmoduleview.prescancomposite;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Prescan;

/**
 * <code>PrescanCellModifyer</code> is the cell modifier of the table viewer 
 * defined in 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.prescancomposite.PrescanComposite}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class CellModifyer implements ICellModifier {

	private final TableViewer tableViewer;
	
	/**
	 * Constructs a <code>PrescanCellModifyer</code>.
	 * 
	 * @param tableViewer the table viewer the cell modifier is appended to
	 */
	public CellModifyer(final TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canModify(final Object element, final String property) {
		
		if(property.equals("value")) {

			// mögliche Auswahl muß erstellt werden
			final Prescan prescan = (Prescan)element;

			if (prescan.getAbstractPrePostscanDevice().isDiscrete()){
				// Prescan erlaubt nur disrecte Werte => ComboBoxCellEditor

				if (this.tableViewer.getCellEditors()[1] instanceof TextCellEditor) {
					// aus dem TextCellEditor eine ComobBox machen
					this.tableViewer.getCellEditors()[1].dispose();

			    	// Wenn der PrescanDatatype on/off oder open/close ist, 
					// dieses zur Auswahl stellen und nicht die
			    	// vorhandenen Zahlen.
			    	if(prescan.getAbstractPrePostscanDevice().getValue().
			    			getType().equals(DataTypes.ONOFF)) {
			    		this.tableViewer.getCellEditors()[1] = 
			    			new ComboBoxCellEditor(this.tableViewer.getTable(), 
			    					new String[] {"On", "Off"}, SWT.READ_ONLY);
			    	}
			    	else if(prescan.getAbstractPrePostscanDevice().getValue().
			    			getType().equals(DataTypes.OPENCLOSE)) {
			    		this.tableViewer.getCellEditors()[1] = 
			    			new ComboBoxCellEditor(this.tableViewer.getTable(), 
			    				new String[] {"Open", "Close"}, SWT.READ_ONLY);
			    	}
			    	else
			    		this.tableViewer.getCellEditors()[1] = 
			    			new ComboBoxCellEditor(this.tableViewer.getTable(), 
			    				prescan.getAbstractPrePostscanDevice().
			    				getValue().getDiscreteValues().
			    				toArray(new String[0]), SWT.READ_ONLY);
			    }
			    else if(this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor) {
			    	// nur die möglichen Werte hinzufügen, ComboBox ist schon vorhanden

			    	// Wenn der PrescanDatatype on/off oder open/close ist, 
			    	// dieses zur Auswahl stellen und nicht die
			    	// vorhandenen Zahlen.
			    	if (prescan.getAbstractPrePostscanDevice().getValue().
			    			getType().equals(DataTypes.ONOFF)) {
			    		((ComboBoxCellEditor)this.tableViewer.
			    			getCellEditors()[1]).setItems(
			    				new String[] {"On", "Off"});
			    	}
			    	else if(prescan.getAbstractPrePostscanDevice().getValue().
			    			getType().equals(DataTypes.OPENCLOSE)) {
			    		((ComboBoxCellEditor)this.tableViewer.
			    			getCellEditors()[1]).setItems(
			    				new String[] {"Open", "Close"});
			    	}
			    	else
			    		((ComboBoxCellEditor)this.tableViewer.
			    			getCellEditors()[1]).setItems(
			    				prescan.getAbstractPrePostscanDevice().
			    				getValue().getDiscreteValues().
			    				toArray(new String[0]));
			    }
			}
			else {
				// Prescan erlaubt freie Werteingabe => TextCellEditor 
			    if (this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor) {
					// aus der ComboBox einen TextCellEditor machen
					this.tableViewer.getCellEditors()[1].dispose();
					this.tableViewer.getCellEditors()[1] = 
						new TextCellEditor( this.tableViewer.getTable());
			    }
			}
		}
		return property.equals("value");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue(final Object element, final String property) {
		final Prescan prescan = (Prescan)element;
		if(property.equals("value")) {
		    if(this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor) {
		    	final String[] operators = ((ComboBoxCellEditor)
		    		this.tableViewer.getCellEditors()[1]).getItems();
		    	for(int i = 0; i < operators.length; ++i) {
		    		if(operators[i].equals(prescan.getValue())) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modify(final Object element, final String property, 
						final Object value) {
		
		final Prescan prescan = (Prescan)((TableItem)element).getData();
		
		if(property.equals("value")) {
			if(this.tableViewer.getCellEditors()[1] instanceof ComboBoxCellEditor) {
				// Wenn Datentyp OnOff oder OpenClose ist, wird nicht OnOff 
				// oder OpenClose gesetzt sondern der 
				// echte Wert des PrePostscanDevice
				if (prescan.getAbstractPrePostscanDevice().getValue().
						getType().equals(DataTypes.ONOFF)) {
					final String[] auswahl = 
						(prescan.getAbstractPrePostscanDevice().getValue().
								getDiscreteValues().toArray(new String[0]));
					prescan.setValue(auswahl[(Integer)value]);
				}
				else if(prescan.getAbstractPrePostscanDevice().getValue().
						getType().equals(DataTypes.OPENCLOSE)) {
					final String[] auswahl = 
						(prescan.getAbstractPrePostscanDevice().getValue().
						getDiscreteValues().toArray(new String[0]));
					prescan.setValue(auswahl[(Integer)value]);
				}
				else {
					final String[] operators = ((ComboBoxCellEditor)
						this.tableViewer.getCellEditors()[1]).getItems();
					prescan.setValue(operators[(Integer)value]);
				}
			}
			else {
				// Feld ist ein TextCellEditor
				prescan.setValue(value.toString());
			}
		}
		this.tableViewer.refresh();
	}
}