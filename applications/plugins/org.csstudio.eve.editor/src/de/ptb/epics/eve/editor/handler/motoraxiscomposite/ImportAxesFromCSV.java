package de.ptb.epics.eve.editor.handler.motoraxiscomposite;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.jobs.file.ImportCSV;
import de.ptb.epics.eve.editor.preferences.PreferenceConstants;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.20
 */
public class ImportAxesFromCSV implements IHandler {
	private static final Logger LOGGER = Logger.getLogger(ImportAxesFromCSV.class
			.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileDialog fileDialog = new FileDialog(Display.getDefault()
				.getActiveShell(), SWT.OPEN);
		fileDialog.setFilterExtensions(new String[] {"*.csv;*.txt"});
		fileDialog.setFilterNames(new String[] {"CSV file (.csv;.txt)"});
		fileDialog.setFilterPath(de.ptb.epics.eve.resources.Activator
				.getDefault().getDefaultsManager().getWorkingDirectory()
				.getAbsolutePath());
		String result = fileDialog.open();
		
		if (result == null) {
			LOGGER.info("File Dialog canceled. Nothing imported.");
			return null;
		}
		
		File csvFile = new File(fileDialog.getFilterPath() + "/" + 
				fileDialog.getFileName());
		
		if (!csvFile.isFile()) {
			LOGGER.warn("File not found.");
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"File not found", "File not found.");
			throw new ExecutionException("File not found!");
		} else {
			LOGGER.info("File " + csvFile.getAbsolutePath() + " selected.");
			de.ptb.epics.eve.resources.Activator.getDefault()
					.getDefaultsManager()
					.setWorkingDirectory(csvFile.getParentFile());
		}
		
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart.getSite().getId()
				.equals("de.ptb.epics.eve.editor.views.ScanModulView")) {
			ScanModule scanModule = ((ScanModuleView) activePart)
					.getCurrentScanModule();
			
			Job csvImport = new ImportCSV(csvFile, Activator.getDefault().
				getPreferenceStore().getString(
					PreferenceConstants.P_CSV_DELIMITER).charAt(0), scanModule);
			csvImport.setUser(true);
			csvImport.schedule();
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEnabled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHandled() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
	}
}