package de.ptb.epics.eve.viewer.views.playlistview;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.ecp1.client.model.PlayListEntry;

/**
 * <code>PlayListTableLabelProvider</code>.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class PlayListTableLabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		PlayListEntry entry = (PlayListEntry)element;
		if(columnIndex == 0) {
			return entry.getName();
		} else if (columnIndex == 1) {
			return entry.getAuthor();
		} else if (columnIndex == 2) {
			Date date = entry.getTimeStamp();
			if (date == null) {
				return "";
			}
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		}
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(final ILabelProviderListener listener) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(final ILabelProviderListener listener) {
	}
}