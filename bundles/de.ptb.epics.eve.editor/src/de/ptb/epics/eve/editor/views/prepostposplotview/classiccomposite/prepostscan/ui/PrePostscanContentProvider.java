package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.PrePostscanEntry;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PrePostscanContentProvider implements IStructuredContentProvider {
	//private ScanModule scanModule;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		/*
		 * Ensure that changes are observed and represented.
		 * see also #5363
		 */
		// TODO unregister listeners from oldInput
		//this.scanModule = (ScanModule)newInput;
		// TODO register listeners for newInput
		// TODO Auto-generated method stub
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
		return this.createEntries(scanModule).toArray();
	}
	
	private List<PrePostscanEntry> createEntries(ScanModule scanModule) {
		List<PrePostscanEntry> entries = new ArrayList<>();
		// TODO
		Prescan[] prescans = scanModule.getPrescans();
		Postscan[] postscans = scanModule.getPostscans();
		// for now just plain dumb add all (duplicates!)
		for (Prescan prescan : prescans) {
			entries.add(new PrePostscanEntry(
				prescan.getAbstractPrePostscanDevice(), prescan, null));
		}
		for (Postscan postscan : postscans) {
			entries.add(new PrePostscanEntry(
				postscan.getAbstractPrePostscanDevice(), null, postscan));
		}
		return entries;
	}
}
