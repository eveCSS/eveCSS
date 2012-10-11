package de.ptb.epics.eve.data.scandescription.axismode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.errors.AxisError;
import de.ptb.epics.eve.data.scandescription.errors.AxisErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;

/**
 * @author Marcus Michalsky
 * @since 1.7
 */
public class FileMode extends AxisMode {
	
	/** */
	public static final String FILE_PROP = "file";
	
	private File file;

	/**
	 * @param axis the axis this mode belongs to
	 */
	public FileMode(Axis axis) {
		super(axis);
	}
	
	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @param file the file to set
	 */
	public void setFile(File file) {
		this.propertyChangeSupport.firePropertyChange(FileMode.FILE_PROP, 
				this.file, this.file = file);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		List<IModelError> errors = new ArrayList<IModelError>();
		if (this.file == null) {
			errors.add(new AxisError(this.axis,
						AxisErrorTypes.FILENAME_NOT_SET));
		}
		return errors;
	}
}