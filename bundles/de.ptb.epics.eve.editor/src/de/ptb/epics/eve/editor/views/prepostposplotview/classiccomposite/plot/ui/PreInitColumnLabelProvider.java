package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.plot.ui;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import de.ptb.epics.eve.data.scandescription.PlotWindow;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PreInitColumnLabelProvider extends ColumnLabelProvider {
	private static final String HEAVY_CHECK_MARK = Character.toString('\u2714');
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		PlotWindow plotWindow = (PlotWindow)element;
		
		if (plotWindow.isInit()) {
			return HEAVY_CHECK_MARK;
		}
		return "";
	}
}
