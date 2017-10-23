package de.ptb.epics.eve.editor.handler.editor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

import de.ptb.epics.eve.resources.init.Startup;
import de.ptb.epics.eve.util.hdf5.HDF5Util;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public class OpenSCML extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(OpenSCML.class
			.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		if (shell == null) {
			throw new ExecutionException(
					"Could not get Shell of Workbench Window!");
		}
		String setFilterPath = "";
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		File currentWorkingDirectory = de.ptb.epics.eve.editor.Activator.getDefault()
				.getDefaults().getWorkingDirectory();
		if (currentWorkingDirectory.isDirectory()) {
			fileDialog.setFilterPath(currentWorkingDirectory.getAbsolutePath());
			setFilterPath = currentWorkingDirectory.getAbsolutePath();
		} else {
			File file = new File(Startup.readStartupParameters()
					.getRootDir() + "eve/");
			File scmlDir = new File(file.getAbsolutePath() + "/scml/");
			if (scmlDir.isDirectory()) {
				fileDialog.setFilterPath(scmlDir.getAbsolutePath());
				setFilterPath = scmlDir.getAbsolutePath();
			} else {
				fileDialog.setFilterPath(file.getAbsolutePath());
				setFilterPath = file.getAbsolutePath();
			}
		}
		fileDialog.setFilterExtensions(new String[] {"*.scml;*.h5"});
		fileDialog.setFilterNames(new String[] {"Scan Description (*.scml,*.h5)"});
		String scmlPath = fileDialog.open();
		
		if (scmlPath == null) {
			LOGGER.info("Open SCML: Dialog canceled.");
			return null;
		}
		
		String h5Path = "";
		boolean scmlFromH5 = false;
		if (scmlPath.endsWith(".h5")) {
			if (!HDF5Util.isEveSCML(new File(scmlPath))) {
				MessageDialog.openError(
						HandlerUtil.getActiveWorkbenchWindow(event).getShell(), 
						"No SCML found!", 
						"H5 file does not contain a scan description!");
				return null;
			} else {
				try {
					h5Path = scmlPath;
					scmlPath = this.extractSCML(scmlPath);
					scmlFromH5 = true;
				} catch (SCMLExtractionException e) {
					MessageDialog.openError(
						HandlerUtil.getActiveWorkbenchWindow(event).getShell(), 
						"Error extracting SCML", 
						"SCML could not be extracted. Reason:\n" + 
							e.getMessage());
					return null;
				}
			}
		}
		
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(
				(new File(scmlPath)).toURI());
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {
			IDE.openEditorOnFileStore(page, fileStore);
			File chosenPath = new File(scmlPath.substring(0,
					scmlPath.lastIndexOf(File.separator)));
			if (scmlFromH5) {
				chosenPath = new File(h5Path.substring(0, 
						h5Path.lastIndexOf(File.separator)));
			}
			if (!chosenPath.getAbsolutePath().equals(setFilterPath)) {
				de.ptb.epics.eve.editor.Activator
						.getDefault()
						.getDefaults()
						.setWorkingDirectory(chosenPath);
			}
		} catch (PartInitException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		return null;
	}

	private String extractSCML(String scmlPath) throws SCMLExtractionException {
		File h5File = new File(scmlPath);
		File scmlFile = null;
		FileOutputStream fos = null;
		try {
			scmlFile = File.createTempFile(
				h5File.getName() + "-", 
					".scml", 
				new File(System.getProperty("java.io.tmpdir") 
					+ "/eve-" + System.getProperty("user.name")));
			fos = new FileOutputStream(scmlFile);
			fos.write(HDF5Util.getSCML(h5File));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new SCMLExtractionException(e.getMessage());
		} catch (IllegalArgumentException e) {
			LOGGER.error(e.getMessage(), e);
			throw new SCMLExtractionException(e.getMessage());
		} catch (DataFormatException e) {
			LOGGER.error(e.getMessage(), e);
			throw new SCMLExtractionException(e.getMessage());
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
		}
		return scmlFile.getAbsolutePath();
	}
}