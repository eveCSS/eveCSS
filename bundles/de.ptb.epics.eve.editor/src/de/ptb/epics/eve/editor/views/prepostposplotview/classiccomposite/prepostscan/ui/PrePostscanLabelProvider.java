package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.ui;

import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.measuringstation.Device;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.CommonLabelProvider;
import de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan.PrePostscanEntry;

/**
 * @author Marcus Michalsky
 * @since 1.35
 */
public class PrePostscanLabelProvider extends CommonLabelProvider {
	private static final String EM_DASH = "\u2014";
	private static final String OPTION_DELIMITER = "\u00BB";
	private static final String ASTERISK = "\u2217";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		switch (columnIndex) {
		case 0: // delete column
			return DELETE_IMG;
		case 1: // prescan column
			break;
		case 2: // name column
			// TODO return info image if neither pre nor post is set ? -> ghost item
			break;
		case 3: // postscan column
			break;
		case 4: // reset column
			break;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		PrePostscanEntry entry = (PrePostscanEntry)element;
		switch (columnIndex) {
		case 1: // prescan column
			if (entry.getPrescan() == null) {
				return EM_DASH;
			}
			return entry.getPrescan().getValue();
		case 2: // name column
			if (entry.getDevice() instanceof Device) {
				return entry.getDevice().getName();
			}
			return entry.getDevice().getParent().getName() + 
					" " + OPTION_DELIMITER + " " + 
					entry.getDevice().getName();
		case 3: // postscan column
			if (entry.getPostscan() == null) {
				return EM_DASH;
			}
			if (entry.getPostscan().isReset()) {
				return ASTERISK;
			}
			return entry.getPostscan().getValue();
		case 4: // reset column
			if (entry.getPostscan() == null) {
				return EM_DASH;
			}
			return Boolean.toString(entry.getPostscan().isReset());
		default:
			return null;
		}
	}
}
