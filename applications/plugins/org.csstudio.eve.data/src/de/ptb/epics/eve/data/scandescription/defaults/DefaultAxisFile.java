package de.ptb.epics.eve.data.scandescription.defaults;

import java.io.File;

/**
 * @author Marcus Michalsky
 * @since 1.8
 */
public class DefaultAxisFile extends DefaultAxisMode {
	private File file;
	
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
		this.file = file;
	}
}