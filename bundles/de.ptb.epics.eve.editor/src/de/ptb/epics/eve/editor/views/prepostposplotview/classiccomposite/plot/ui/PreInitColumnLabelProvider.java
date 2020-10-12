package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.editor.StringLabels;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PreInitColumnLabelProvider extends ColumnLabelProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		PlotWindow plotWindow = (PlotWindow)element;
		
		if (plotWindow.isInit()) {
			return StringLabels.HEAVY_CHECK_MARK;
		}
		return "";
	}
}
