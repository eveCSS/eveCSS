package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.editor.StringLabels;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class XAxisColumnLabelProvider extends ColumnLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		PlotWindow plotWindow = (PlotWindow)element;
		if (plotWindow.getXAxis() == null) {
			return StringLabels.EM_DASH;
		}
		StringBuilder sb = new StringBuilder();
		
		sb.append(plotWindow.getXAxis().getName());
		if (plotWindow.getMode().equals(PlotModes.LOG)) {
			sb.append(" (log)");
		}
		return sb.toString();
	}
}
