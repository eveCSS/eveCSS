package de.ptb.epics.eve.editor.views.motoraxisview.filecomposite;

import java.io.File;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class FileNameTargetToModelConverter implements IConverter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getFromType() {
		return String.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getToType() {
		return File.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object convert(Object fromObject) {
		if (String.valueOf(fromObject).isEmpty()) {
			return null;
		}
		return new File(String.valueOf(fromObject));
	}
}