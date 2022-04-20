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
	private static final String FORMATTER_STRING = "%12.7g";
	
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
		Formatter formatter = new Formatter(new Locale(
				Locale.ENGLISH.getCountry()));
		DescriptiveStats stats = (DescriptiveStats)element;
		String result = null;
		switch(columnIndex) {
		case 0: // # points
			formatter.close();
			return Long.toString(stats.getSampleSize());
		case 1: // 1st
			if (stats.getFirstValue() != null) {
				result = formatter.format(FORMATTER_STRING, 
					stats.getFirstValue()).toString();
			} else {
				result = FileNameTableLabelProvider.NOT_AVAILABLE;
			}
			break;
		case 2: // last
			if (stats.getLastValue() != null) {
				result = formatter.format(FORMATTER_STRING, 
					stats.getLastValue()).toString();
			} else {
				result = FileNameTableLabelProvider.NOT_AVAILABLE;
			}
			break;
		case 3: // min
			if (stats.getMinimum() != null) {
				result = formatter.format(FORMATTER_STRING, 
					stats.getMinimum()).toString();
			} else {
				result = FileNameTableLabelProvider.NOT_AVAILABLE;
			}
			break;
		case 4: // max
			if (stats.getMaximum() != null) {
				result = formatter.format(FORMATTER_STRING, 
					stats.getMaximum()).toString();
			} else {
				result = FileNameTableLabelProvider.NOT_AVAILABLE;
			}
			break;
		default:
			result = FileNameTableLabelProvider.NOT_AVAILABLE;
			break;
		}
		formatter.close();
		return result;
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
		// not needed
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
		// not needed
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
		// not needed
	}
}