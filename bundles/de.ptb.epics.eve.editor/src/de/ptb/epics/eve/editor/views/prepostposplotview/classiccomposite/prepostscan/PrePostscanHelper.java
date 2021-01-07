package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PrePostscanHelper {
	private PrePostscanHelper() {
	}
	
	/**
	 * 
	 * @param scanModule
	 * @return
	 */
	public static List<PrePostscanEntry> createEntries(ScanModule scanModule) {
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
