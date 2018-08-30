package de.ptb.epics.eve.editor.views.batchupdateview;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;

import de.ptb.epics.eve.util.data.Version;

/**
 * File entry of the table viewer in the Batch Update View containing 
 * information about the file such as its name and update status.
 * 
 * @author Marcus Michalsky
 * @since 1.30
 */
public class FileEntry {
	public static final String VERSION_PROP = "version";
	public static final String STATUS_PROP = "status";
	
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	private final File file;
	private Version version;
	private FileStatus status;
	private String errorMessage;
	
	public FileEntry(File file) {
		this.file = file;
		this.version = null;
		this.status = FileStatus.PENDING;
	}
	
	/**
	 * @return the file
	 */
	public File getFile() {
		return file;
	}

	/**
	 * @return the file name
	 */
	public String getName() {
		return file.getName();
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return file.getAbsolutePath();
	}
	
	/**
	 * @return the version
	 */
	public Version getVersion() {
		return version;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersion(Version version) {
		Version oldValue = this.version;
		this.version = version;
		this.pcs.firePropertyChange(FileEntry.VERSION_PROP, oldValue,
				this.version);
	}

	/**
	 * @return the status
	 */
	public FileStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(FileStatus status) {
		FileStatus oldValue = this.status;
		this.status = status;
		this.pcs.firePropertyChange(STATUS_PROP, oldValue, this.status);
	}
	
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}
}