package de.ptb.epics.eve.editor.views.motoraxisview.filecomposite;

import java.util.Formatter;
import java.util.Locale;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.util.math.statistics.DescriptiveStats;

/**
 * @author Marcus Michalsky
 * @since 1.10
 */
public class FileNameTableLabelProvider implements ITableLabelProvider {
	
	private static final String NOT_AVAILABLE = "N/A";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		DescriptiveStats stats = (DescriptiveStats)element;
		Formatter formatter = new Formatter(new Locale(
				Locale.ENGLISH.getCountry()));
		switch(columnIndex) {
		case 0: // # points
			return Long.toString(stats.getSampleSize());
		case 1: // min
			if (stats.getMinimum() != null) {
			return formatter.format("%12.4g", stats.getMinimum()).toString();
			} 
			return FileNameTableLabelProvider.NOT_AVAILABLE;
		case 2: // max
			if (stats.getMaximum() != null) {
			return formatter.format("%12.4g", stats.getMaximum()).toString();
			}
			return FileNameTableLabelProvider.NOT_AVAILABLE;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
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
	public void addListener(ILabelProviderListener listener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
	}
}