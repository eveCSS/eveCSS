package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;

import de.ptb.epics.eve.util.io.FileUtil;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class FilenameValidator implements IValidator {
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(Object value) {
		String filename = String.valueOf(value);
		File file = new File(filename);
		if (filename.isEmpty()) {
			return ValidationStatus.error("Providing a file name is mandatory!");
		} else if (!file.exists()) {
			return ValidationStatus.warning("File does not exist! " + 
				"(legit but the file must be present when the scan is started.)");
		}
		
		try {
			List<Double> values = StringUtil.getDoubleList(
					FileUtil.readLines(file));
			if (values == null) {
				return ValidationStatus.warning("Cannot read values from file.");
			}
		} catch (IOException e) {
			return ValidationStatus.error(e.getMessage());
		}
		
		return ValidationStatus.ok();
	}
}
