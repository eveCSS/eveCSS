package de.ptb.epics.eve.editor.handler.editor;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

import de.ptb.epics.eve.resources.init.Startup;

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
		
		IFileStore fileStore = EFS.getLocalFileSystem().getStore(
				(new File(scmlPath)).toURI());
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {
			IDE.openEditorOnFileStore(page, fileStore);
		File chosenPath = new File(scmlPath.substring(0,
				scmlPath.lastIndexOf(File.separator)));
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
}