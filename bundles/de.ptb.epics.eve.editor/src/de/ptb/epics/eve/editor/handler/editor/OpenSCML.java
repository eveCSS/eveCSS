package de.ptb.epics.eve.editor.handler.editor;

import java.io.File;

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

import de.ptb.epics.eve.resources.Activator;
import de.ptb.epics.eve.util.hdf5.HDF5Util;
import de.ptb.epics.eve.util.hdf5.SCMLNotFoundException;
import de.ptb.epics.eve.util.hdf5.SCMLExtractionException;

/**
 * @author Marcus Michalsky
 * @since 1.23
 */
public class OpenSCML extends AbstractHandler {
	private static final Logger LOGGER = Logger
			.getLogger(OpenSCML.class.getName());

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
		String filterPath = Activator.getDefault().getFilterPath();

		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
		fileDialog.setFilterPath(filterPath);
		fileDialog.setFilterExtensions(new String[] { "*.scml;*.h5" });
		fileDialog.setFilterNames(
				new String[] { "Scan Description (*.scml,*.h5)" });
		String scmlPath = fileDialog.open();

		if (scmlPath == null) {
			LOGGER.info("Open SCML: Dialog canceled.");
			return null;
		}

		File tempFile = null;
		if (scmlPath.endsWith(".h5")) {
			try {
				tempFile = HDF5Util.getSCMLTempFile(new File(scmlPath));
			} catch (SCMLNotFoundException e) {
				String message = "H5 file does not contain a scan description!";
				LOGGER.error(message);
				MessageDialog.openError(
						HandlerUtil.getActiveWorkbenchWindow(event).getShell(),
						"No SCML found!",
						message);
				return null;
			} catch (SCMLExtractionException e) {
				String message = "SCML could not be extracted. Reason:\n"
						+ e.getMessage();
				LOGGER.error(message);
				MessageDialog.openError(
						HandlerUtil.getActiveWorkbenchWindow(event)
								.getShell(),
						"Error extracting SCML",
						message);
				return null;
			}
		}
		
		String filePath = null;
		if (tempFile == null) {
			// chosen file was .scml
			filePath = scmlPath;
		} else {
			// chosen file was .h5
			filePath = tempFile.getAbsolutePath();
		}
		LOGGER.debug("File " + filePath + " will be opened.");

		IFileStore fileStore = EFS.getLocalFileSystem()
				.getStore((new File(filePath)).toURI());
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		
		try {
			IDE.openEditorOnFileStore(page, fileStore);
			// use the path of the SCML as default
			File chosenPath = new File(filePath.substring(0,
					filePath.lastIndexOf(File.separator)));
			if (tempFile != null) {
				// if SCML was extracted from H5 use H5 directory (and not 
				// the temp dir
				chosenPath = new File(scmlPath.substring(0, 
					scmlPath.lastIndexOf(File.separator)));
			}
			// set defaults working dir (if changed)
			if (!chosenPath.getAbsolutePath().equals(filterPath)) {
				de.ptb.epics.eve.editor.Activator.getDefault().getDefaults()
						.setWorkingDirectory(chosenPath);
			}
		} catch (PartInitException e) {
			LOGGER.error(e.getMessage(), e);
		}

		return null;
	}
}