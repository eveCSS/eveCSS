package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.ui;

import java.util.ArrayList;
import java.util.Arrays;
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
		List<Prescan> prescans = Arrays.asList(scanModule.getPrescans());
		List<Postscan> postscans = Arrays.asList(scanModule.getPostscans());
		// add all Prescans that are not also Postscans
		for (Prescan prescan : prescans) {
			boolean alsoInPostscan = false;
			for (Postscan postscan : postscans) {
				if (postscan.getAbstractPrePostscanDevice().equals(
						prescan.getAbstractPrePostscanDevice())) {
					alsoInPostscan = true;
				}
			}
			if (!alsoInPostscan) {
				entries.add(new PrePostscanEntry(
						prescan.getAbstractPrePostscanDevice(), prescan, null));
			}
		}
		// Add all Prescans that are also Postscans
		for (Prescan prescan : prescans) {
			for (Postscan postscan : postscans) {
				if (postscan.getAbstractPrePostscanDevice().equals(
						prescan.getAbstractPrePostscanDevice())) {
					entries.add(new PrePostscanEntry(prescan.
							getAbstractPrePostscanDevice(), prescan, postscan));
				}
			}
		}
		// Add all Postscans that are not also Prescans
		for (Postscan postscan : postscans) {
			boolean alsoInPrescan = false;
			for (Prescan prescan : prescans) {
				if (prescan.getAbstractPrePostscanDevice().equals(
						postscan.getAbstractPrePostscanDevice())) {
					alsoInPrescan = true;
				}
			}
			if (!alsoInPrescan) {
				entries.add(new PrePostscanEntry(postscan.
						getAbstractPrePostscanDevice(), null, postscan));
			}
		}
		return entries;
	}
}
