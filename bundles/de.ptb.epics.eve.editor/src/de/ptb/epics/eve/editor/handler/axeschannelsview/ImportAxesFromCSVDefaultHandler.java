package de.ptb.epics.eve.editor.handler.axeschannelsview;

import java.io.File;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
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
import de.ptb.epics.eve.editor.views.AbstractScanModuleView;

/**
 * @author Marcus Michalsky
 * @since 1.34
 */
public class ImportAxesFromCSVDefaultHandler extends AbstractHandler {
	private static final Logger LOGGER = Logger.getLogger(
			ImportAxesFromCSVDefaultHandler.class.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileDialog fileDialog = new FileDialog(
				Display.getDefault().getActiveShell(), SWT.OPEN);
		fileDialog.setFilterExtensions(new String[] {"*.csv;*.txt"});
		fileDialog.setFilterNames(new String[] {"CSV file (.csv;.txt)"});
		fileDialog.setFilterPath(
				de.ptb.epics.eve.resources.Activator.getDefault().
				getDefaultsManager().getWorkingDirectory().getAbsolutePath());
		String result = fileDialog.open();
		
		if (result == null) {
			LOGGER.info("File Dialog canceled. Nothing imported.");
			return null;
		}
		
		File csvFile = new File(fileDialog.getFilterPath() + "/" + 
				fileDialog.getFileName());
		
		if (!csvFile.isFile()) {
			String message = "File not found!";
			LOGGER.error(message);
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
					message, message);
			throw new ExecutionException(message);
		} else {
			LOGGER.info("File " + csvFile.getAbsolutePath() + " selected.");
			de.ptb.epics.eve.resources.Activator.getDefault().
				getDefaultsManager().
				setWorkingDirectory(csvFile.getParentFile());
		}
		
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart instanceof AbstractScanModuleView) {
			ScanModule scanModule = ((AbstractScanModuleView)activePart).
					getScanModule();
			
			Job csvImport = new ImportCSV(csvFile, Activator.getDefault().
					getPreferenceStore().getString(
					PreferenceConstants.P_CSV_DELIMITER).charAt(0), scanModule);
			csvImport.setUser(true);
			csvImport.schedule();
		}
		return null;
	}
}
