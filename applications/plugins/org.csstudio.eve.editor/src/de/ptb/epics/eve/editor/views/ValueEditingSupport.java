package de.ptb.epics.eve.editor.views;


import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import de.ptb.epics.eve.data.measuringstation.PluginParameter;
import de.ptb.epics.eve.editor.views.motoraxisview.plugincomposite.PluginParameterValue;
import de.ptb.epics.eve.util.jface.MyComboBoxCellEditor;

/**
 * {@link org.eclipse.jface.viewers.EditingSupport} of the value column.
 * 
 * @author Hartmut Scherr
 * @since 1.20
 */
public class ValueEditingSupport extends EditingSupport {

	private TableViewer viewer;
	
	/**
	 * Constructor.
	 * 
	 * @param viewer
	 */
	public ValueEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		final PluginParameterValue pluginParameterValue = (PluginParameterValue)element;

		PluginParameter pluginParameter = pluginParameterValue.getPluginParameter();

		if (pluginParameter != null) {
			if (pluginParameter.isDiscrete()) {
				List<String> discrValuesList = pluginParameter.getDiscreteValues();
				return new MyComboBoxCellEditor(this.viewer.getTable(), 
						discrValuesList.toArray(new String[0]));
			} else {
				return new TextCellEditor(this.viewer.getTable()) {
					@Override protected void focusLost() {
						if(isActivated()) {
							fireCancelEditor();
						}
						deactivate();
					}
				};
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object getValue(Object element) {

		final PluginParameterValue pluginParameterValue = (PluginParameterValue)element;

		if (pluginParameterValue.getPluginParameter().isDiscrete()) {
			// Wert einer ComoboBox muss ermittelt werden
			// Der Index muss zurückgegeben werden
			// aktuelle Werte muss erst noch aus dem Model gelesen und dann gesetzt werden

			// Bevor die Bestimmung klappt, wird erstmal index 0 gesetzt
			return 0;
		} else{
			// Text Cell, es wird einfach der Wert zurückgegeben
			return pluginParameterValue.getValue();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {

		final PluginParameterValue pluginParameterValue = (PluginParameterValue)element;
		
		if (pluginParameterValue.getPluginParameter().isDiscrete()) {
			// Wert einer ComoboBox muss mit dem Index gesetzt werden
			pluginParameterValue.getPluginController().set(pluginParameterValue.getPluginParameter().getName(), pluginParameterValue.getPluginParameter().getDiscreteValues().get((int)value) );
		} else{
			// Text Cell, es wird einfach der Wert zurückgegeben
			pluginParameterValue.getPluginController().set(pluginParameterValue.getPluginParameter().getName(), value.toString());
		}
	}
}