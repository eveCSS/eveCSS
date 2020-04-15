package de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.axes.file.FilenameValidator;
import de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.ValuesDialogCellEditor;
import de.ptb.epics.eve.util.io.FileUtil;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class FileDialogCellEditor extends ValuesDialogCellEditor {
	private static final Logger LOGGER = Logger.getLogger(
			FileDialogCellEditor.class.getName());
	
	public FileDialogCellEditor(Composite parent, Axis axis) {
		super(parent, axis);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		PositionFileDialog dialog = new PositionFileDialog(
				cellEditorWindow.getShell(), cellEditorWindow, getAxis());
		dialog.open();
		if (getAxis().getFile() != null) {
			return getAxis().getFile().getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void decorate(boolean oldValidState, boolean newValidState) {
		ControlDecoration decoration = getDecoration();
		if (!newValidState) {
			decoration.setDescriptionText(getErrorMessage());
			String filename = getValuesText().getText();
			File file = new File(filename);
			if (filename.isEmpty()) {
				decoration.setImage(getErrorImage());
			} else if (!file.exists()) {
				decoration.setImage(getWarnImage());
			} else {
				try {
					List<Double> values = StringUtil.getDoubleList(
							FileUtil.readLines(file));
					if (values == null) {
						decoration.setImage(getWarnImage());
					}
				} catch (IOException e) {
					decoration.setImage(getErrorImage());
				}
			}
			decoration.show();
		} else {
			decoration.setDescriptionText("");
			decoration.setImage(null);
			decoration.hide();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String validate(String filename) {
		FilenameValidator validator = new FilenameValidator();
		IStatus status = validator.validate(filename);
		if (status.isOK()) {
			LOGGER.debug("validation ok");
			return null;
		} else {
			LOGGER.debug("validation failed: " + status.getMessage());
			return status.getMessage();
		}
	}
}
