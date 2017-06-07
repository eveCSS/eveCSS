package de.ptb.epics.eve.editor.dialogs.updater;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import de.ptb.epics.eve.data.scandescription.updater.AbstractModification;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class FromColumnLabelProvider extends ColumnLabelProvider {
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(Object element) {
		return ((AbstractModification) element).belongsTo().getSourceVersion()
				.toString();
	}
}