package de.ptb.epics.eve.editor.views.scanmoduleview.plotcomposite;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.scanmoduleview.CompositeContentProvider;

/**
 * @author Marcus Michalsky
 * @since 1.2
 * @see de.ptb.epics.eve.editor.views.scanmoduleview.CompositeContentProvider
 */
public class ContentProvider extends CompositeContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return ((ScanModule)inputElement).getPlotWindows();
	}
}