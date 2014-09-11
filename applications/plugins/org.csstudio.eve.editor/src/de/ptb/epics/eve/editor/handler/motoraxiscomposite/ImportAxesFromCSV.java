package de.ptb.epics.eve.editor.handler.motoraxiscomposite;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.dialogs.csvimport.CSVImportDialog;
import de.ptb.epics.eve.editor.preferences.PreferenceConstants;
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.util.csv.CSVUtil;
import de.ptb.epics.eve.util.io.StringUtil;

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
		fileDialog.setFilterPath(Activator.getDefault().getRootDirectory());
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
		}
		
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart.getSite().getId()
				.equals("de.ptb.epics.eve.editor.views.ScanModulView")) {
			ScanModule scanModule = ((ScanModuleView) activePart)
					.getCurrentScanModule();
			List<Pair<String, List<String>>> axesList = CSVUtil.getColumns(
					csvFile, Activator.getDefault().getPreferenceStore()
							.getString(PreferenceConstants.P_CSV_DELIMITER)
							.charAt(0));
			
			List<Pair<String, List<String>>> present = new ArrayList<>();
			List<Pair<String, List<String>>> absent = new ArrayList<>();
			List<Pair<String, List<String>>> invalid = new ArrayList<>();
			List<Axis> toRemove = new ArrayList<>();
			
			for (Pair<String, List<String>> axis : axesList) {
				MotorAxis motorAxis = this.getAxisByName(scanModule
						.getChain().getScanDescription()
						.getMeasuringStation(), axis.getKey());
				if (motorAxis == null) {
					invalid.add(axis);
				} else {
					boolean exists = false;
					for (Axis smAxis : scanModule.getAxes()) {
						if (smAxis.getAbstractDevice().getName()
								.equals(axis.getKey())) {
							exists = true;
							toRemove.add(smAxis);
						}
					}
					if (exists) {
						present.add(axis);
					} else {
						absent.add(axis);
					}
				}
			}
			if (!present.isEmpty() || !invalid.isEmpty()) {
				CSVImportDialog csvDialog = new CSVImportDialog(Display
						.getDefault().getActiveShell(), present, absent,
						invalid);
				csvDialog.open();
				if (csvDialog.getReturnCode() == Window.CANCEL) {
					LOGGER.info("CSV import canceled.");
					return null;
				}
			}
			
			for (Axis smAxis : toRemove) {
				scanModule.remove(smAxis);
			}
			for (Pair<String, List<String>> axis : present) {
				addAxis(scanModule, axis);
			}
			for (Pair<String, List<String>> axis : absent) {
				addAxis(scanModule, axis);
			}
		}
		return null;
	}
	
	private void addAxis(ScanModule scanModule, Pair<String, List<String>> axis) {
		Axis smAxis = new Axis(scanModule, this.getAxisByName(scanModule
				.getChain().getScanDescription().getMeasuringStation(),
				axis.getKey()));
		smAxis.setStepfunction(Stepfunctions.POSITIONLIST);
		smAxis.setPositionlist(StringUtil.buildCommaSeparatedString(
				axis.getValue()));
		scanModule.add(smAxis);
	}
	
	private MotorAxis getAxisByName(IMeasuringStation measuringStation, 
			String name) {
		for (MotorAxis axis : measuringStation.getMotorAxes().values()) {
			if (axis.getName().equals(name)) {
				return axis;
			}
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