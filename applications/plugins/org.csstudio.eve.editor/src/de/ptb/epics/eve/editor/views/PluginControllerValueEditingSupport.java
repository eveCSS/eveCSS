package de.ptb.epics.eve.editor.views;

import java.util.List;

import org.apache.log4j.Logger;
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
public class PluginControllerValueEditingSupport extends EditingSupport {

	private static final Logger logger = Logger
			.getLogger(PluginControllerValueEditingSupport.class.getName());

	private TableViewer viewer;

	/**
	 * Constructor.
	 * 
	 * @param viewer
	 */
	public PluginControllerValueEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CellEditor getCellEditor(Object element) {
		logger.debug("getCellEditor");
		final PluginParameterValue pluginParameterValue = (PluginParameterValue) element;

		PluginParameter pluginParameter = pluginParameterValue
				.getPluginParameter();

		if (pluginParameter != null) {
			if (pluginParameter.isDiscrete()) {
				List<String> discrValuesList = pluginParameter
						.getDiscreteValues();
				return new MyComboBoxCellEditor(this.viewer.getTable(),
						discrValuesList.toArray(new String[0]));
			} else {
				return new TextCellEditor(this.viewer.getTable()) {
					@Override
					protected void focusLost() {
						if (isActivated()) {
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
		logger.debug("getCellValue");

		final PluginParameterValue pluginParameterValue = (PluginParameterValue) element;

		if (pluginParameterValue.getPluginParameter().isDiscrete()) {
			// Wert einer ComoboBox muss ermittelt werden
			// Der Index muss zurückgegeben werden
			List<String> idList = pluginParameterValue.getPluginParameter()
					.getDiscreteIDs();
			int index = idList.indexOf(pluginParameterValue.getValue());
			return index;
		} else {
			// Text Cell, es wird einfach der Wert zurückgegeben
			// wenn er != null ist 
			if (pluginParameterValue.getValue() != null) {
				return pluginParameterValue.getValue();
			}
			return "";
			
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void setValue(Object element, Object value) {
		logger.debug("setValue");

		final PluginParameterValue pluginParameterValue = (PluginParameterValue) element;

		if (pluginParameterValue.getPluginParameter().isDiscrete()) {
			// Wert einer ComoboBox muss mit dem Index gesetzt werden
			pluginParameterValue.getPluginController().set(
					pluginParameterValue.getPluginParameter().getName(),
					pluginParameterValue.getPluginParameter().getDiscreteIDs()
							.get((int) value));
		} else {
			// Text Cell, es wird einfach der Wert zurückgegeben
			if (!"".equals(value)) {
				pluginParameterValue.getPluginController().set(
					pluginParameterValue.getPluginParameter().getName(),
					value.toString());
			} else {
				// ein leerer String wird nicht ins Model geschrieben,
				// dann wird null geschrieben
				pluginParameterValue.getPluginController().set(
						pluginParameterValue.getPluginParameter().getName(),null);
			}
		}
	}
}