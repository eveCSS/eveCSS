package de.ptb.epics.eve.viewer.views.deviceoptionsview;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;

/**
 * <code>ValueColumnEditingSupprt</code>.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class ValueColumnEditingSupport extends EditingSupport {

	private static Logger logger = 
			Logger.getLogger(ValueColumnEditingSupport.class.getName());
	
	// 
	private final TableViewer viewer;
	
	/**
	 * 
	 * @param viewer
	 */
	public ValueColumnEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		OptionPV opv = (OptionPV)element;
		
		if(opv.isDiscrete()) {
			return new ComboBoxCellEditor(this.viewer.getTable(), 
									opv.getDiscreteValues(), SWT.READ_ONLY) {
				@Override protected void focusLost() {
					if(isActivated()) {
						fireCancelEditor();
					}
					deactivate();
					viewer.refresh();
				}
			};
		} else {
			return new TextCellEditor(viewer.getTable()) {
				@Override protected void focusLost() {
					if (isActivated()) {
						fireCancelEditor();
					}
					deactivate();
					viewer.refresh();
				}
			};
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		return !(((OptionPV)element).isReadOnly());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {
		OptionPV opv = (OptionPV)element;
		
		if(opv.isDiscrete()) {
			int count = 0;
			String val = opv.getValue();
			for(String s : opv.getDiscreteValues()) {
				if(s.equals(val)) return count;
				count++;
			}
			return 0;
		} else {
			return opv.getValue();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		((OptionPV)element).setValue(value);
		logger.debug("'" + ((OptionPV)element).getName() + "' set to " + value);
	}
}