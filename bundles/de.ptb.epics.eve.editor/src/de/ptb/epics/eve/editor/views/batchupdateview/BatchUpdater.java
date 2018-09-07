package de.ptb.epics.eve.editor.views.batchupdateview;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import de.ptb.epics.eve.editor.jobs.batchupdate.FileInfoJob;
import de.ptb.epics.eve.editor.jobs.batchupdate.FileUpdateJob;
import de.ptb.epics.eve.util.data.Version;

/**
 * @author Marcus Michalsky
 * @since 1.30
 */
public class BatchUpdater {
	public static final String SOURCE_PROP = "source";
	public static final String TARGET_PROP = "target";
	public static final String FILES_PROP = "files";
	public static final String UPDATE_STATUS_PROP = "updateStatus";
	
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	
	private List<FileEntry> files;
	private File source;
	private File target;
	private Version currentVersion;
	private UpdateStatus updateStatus;
	
	private int initFileCount;
	private int updateFileCount;
	
	public BatchUpdater() {
		this.files = new ArrayList<>();
		this.source = null;
		this.target = null;
		this.currentVersion = de.ptb.epics.eve.resources.Activator
				.getSchemaVersion();
		this.updateStatus = UpdateStatus.IDLE;
	}
	
	/**
	 * @return the source
	 */
	public synchronized File getSource() {
		return source;
	}
	
	/**
	 * @param source the source to set
	 */
	public synchronized void setSource(File source) {
		if (!source.isDirectory()) {
			throw new IllegalArgumentException("not a directory");
		}
		File oldValue = this.source;
		this.source = source;
		this.pcs.firePropertyChange(BatchUpdater.SOURCE_PROP, oldValue,
				this.source);
		this.setFiles();
	}
	
	private void setFiles() {
		this.files.clear();
		UpdateStatus oldStatus = this.updateStatus;
		this.updateStatus = UpdateStatus.INITIALIZING;
		this.pcs.firePropertyChange(BatchUpdater.UPDATE_STATUS_PROP, oldStatus,
				this.updateStatus);
		/* when switching to Java 8 use try with resources and streaming API 
		 * try (Stream<Path> paths = Files.walk(Paths.get("/home/you/Desktop"))) {
		 *   paths
		 *   .filter(Files::isRegularFile)
		 *   .forEach(System.out::println);
		 * } 
		 */
		FilenameFilter filenameFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".h5") || name.endsWith(".scml");
			}
		};
		for (final File file : this.source.listFiles(filenameFilter)) {
			this.files.add(new FileEntry(file));
		}
		this.pcs.firePropertyChange(BatchUpdater.FILES_PROP, null, this.files);
		
		initFileCount = this.files.size();
		for (final FileEntry file : this.files) {
			FileInfoJob fileInfoJob = new FileInfoJob(file.getName(), this,
					file);
			fileInfoJob.addJobChangeListener(new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					synchronized (files) {
					initFileCount = initFileCount - 1;
					if (initFileCount == 0) {
						UpdateStatus oldStatus = updateStatus;
						if (target != null) {
							updateStatus = UpdateStatus.READY_FOR_UPDATE;
						} else {
							updateStatus = UpdateStatus.INITIALIZED;
						}
						pcs.firePropertyChange(BatchUpdater.UPDATE_STATUS_PROP,
								oldStatus, updateStatus);
					}
					}
					super.done(event);
				}
			});
			fileInfoJob.schedule();
		}
	}
	
	/**
	 * @return the target
	 */
	public synchronized File getTarget() {
		return target;
	}
	
	/**
	 * @param target the target to set
	 */
	public synchronized void setTarget(File target) {
		File oldValue = this.target;
		this.target = target;
		this.pcs.firePropertyChange(BatchUpdater.TARGET_PROP, oldValue,
				this.target);
		if (this.updateStatus.equals(UpdateStatus.INITIALIZED)) {
			this.updateStatus = UpdateStatus.READY_FOR_UPDATE;
			this.pcs.firePropertyChange(BatchUpdater.UPDATE_STATUS_PROP,
					UpdateStatus.INITIALIZED, this.updateStatus);
		} else if (this.updateStatus.equals(UpdateStatus.UPDATE_DONE)) {
			this.setFiles();
		}
	}
	
	/**
	 * @return the files
	 */
	public List<FileEntry> getFiles() {
		return files;
	}
	
	/**
	 * @return the currentVersion
	 */
	public Version getCurrentVersion() {
		return currentVersion;
	}

	public synchronized void update() {
		updateFileCount = this.getOutdatedCount();
		for (FileEntry file : this.files) {
			if (file.getStatus().equals(FileStatus.OUTDATED)) {
				FileUpdateJob fileUpdateJob = new FileUpdateJob(file,
						target.getAbsolutePath(), this.currentVersion);
				fileUpdateJob.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						synchronized(files) {
							updateFileCount = updateFileCount - 1;
							if (updateFileCount == 0) {
								UpdateStatus oldStatus = updateStatus;
								updateStatus = UpdateStatus.UPDATE_DONE;
								pcs.firePropertyChange(
										BatchUpdater.UPDATE_STATUS_PROP,
										oldStatus, updateStatus);
							}
						}
						super.done(event);
					}
				});
				fileUpdateJob.schedule();
			}
		}
	}

	/**
	 * resets the batch updater to its initial state:
	 * <ul>
	 * 	<li>source directory is reset</li>
	 *  <li>target directory is reset</li>
	 *  <li>file table is cleared</li>
	 * </ul>
	 */
	public synchronized void reset() {
		File oldSource = this.source;
		this.source = null;
		this.pcs.firePropertyChange(BatchUpdater.SOURCE_PROP, oldSource, null);
		File oldTarget = this.target;
		this.target = null;
		this.pcs.firePropertyChange(BatchUpdater.TARGET_PROP, oldTarget, null);
		this.files.clear();
		this.pcs.firePropertyChange(BatchUpdater.FILES_PROP, this.files, null);
		this.pcs.firePropertyChange(BatchUpdater.UPDATE_STATUS_PROP, null,
				UpdateStatus.IDLE);
	}
	
	/**
	 * Returns a list of names of files that would be overwritten during the
	 * update.
	 * @return a list of names of files that would be overwritten during the
	 * 	update
	 */
	public List<String> checkForExistingFilesInTarget() {
		List<String> existingFiles = new ArrayList<>();
		if (this.target == null) {
			return existingFiles;
		}
		List<String> targetFiles = Arrays.asList(this.target.list());
		for (FileEntry file : this.files) {
			if (file.getStatus().equals(FileStatus.OUTDATED) && 
					targetFiles.contains(file.getName())) {
				existingFiles.add(file.getName());
			}
		}
		return existingFiles;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getOutdatedCount() {
		int outdated = 0;
		for (FileEntry file : this.files) {
			if (file.getStatus().equals(FileStatus.OUTDATED)) {
				outdated++;
			}
		}
		return outdated;
	}

	/**
	 * Delegate to
	 * {@link java.beans.PropertyEditorSupport#addPropertyChangeListener(PropertyChangeListener)}
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}
	
	/**
	 * Delegate to
	 * {@link java.beans.PropertyEditorSupport#removePropertyChangeListener(PropertyChangeListener)}
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}
	
	/**
	 * Delegate to
	 * {@link java.beans.PropertyEditorSupport#addPropertyChangeListener(String, PropertyChangeListener)}
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Delegate to
	 * {@link java.beans.PropertyEditorSupport#removePropertyChangeListener(String, PropertyChangeListener)}
	 */
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(propertyName, listener);
	}
}