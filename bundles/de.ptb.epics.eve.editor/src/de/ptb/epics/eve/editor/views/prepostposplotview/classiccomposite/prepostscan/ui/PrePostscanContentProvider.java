package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.ui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.PrePostscanHelper;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PrePostscanContentProvider implements IStructuredContentProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		// nothing to do for now
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// nothing to do for now
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		/*
		 * gets a whole Scan Module as input and is responsible for transforming
		 * the separate prescan and postscan lists into a list of 
		 * PrePostScanEntry (view model).
		 */
		ScanModule scanModule = (ScanModule)inputElement;
		return PrePostscanHelper.createEntries(scanModule).toArray();
	}
}
