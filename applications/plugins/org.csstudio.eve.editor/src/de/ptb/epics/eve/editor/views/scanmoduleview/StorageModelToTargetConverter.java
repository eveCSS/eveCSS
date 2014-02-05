package de.ptb.epics.eve.editor.views.scanmoduleview;

import org.eclipse.core.databinding.conversion.IConverter;

import de.ptb.epics.eve.data.scandescription.Storage;

/**
 * @author Marcus Michalsky
 * @since 1.18
 */
public class StorageModelToTargetConverter implements IConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getFromType() {
		return Storage.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getToType() {
		return String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
		return ((Storage)fromObject).toString();
	}
}