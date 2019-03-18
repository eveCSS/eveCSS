package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.postscancomposite;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.CompositeContentProvider;

/**
 * @author Marcus Michalsky
 * @since 1.2
 * @see de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.CompositeContentProvider
 */
public class ContentProvider extends CompositeContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return ((ScanModule)inputElement).getPostscans();
	}
}