package de.ptb.epics.eve.editor.handler.motoraxiscomposite;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javafx.util.Pair;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jface.dialogs.MessageDialog;
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
import de.ptb.epics.eve.editor.views.scanmoduleview.ScanModuleView;
import de.ptb.epics.eve.util.csv.CSVUtil;
import de.ptb.epics.eve.util.io.StringUtil;

/**
 * @author Marcus Michalsky
 * @since 1.20
 */
public class ImportCSVAxes implements IHandler {
	private static final Logger LOGGER = Logger.getLogger(ImportCSVAxes.class
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
		LOGGER.debug("File " + csvFile.getAbsolutePath() + " selected.");
		if (!csvFile.isFile()) {
			MessageDialog.openError(Display.getDefault().getActiveShell(),
					"File not found", "File not found.");
		}
		
		IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
		if (activePart.getSite().getId()
				.equals("de.ptb.epics.eve.editor.views.ScanModulView")) {
			ScanModule scanModule = ((ScanModuleView) activePart)
					.getCurrentScanModule();
			List<Pair<String, List<String>>> axes = CSVUtil.getColumns(csvFile);
			for (Pair<String, List<String>> axis : axes) {
				if (!Arrays.asList(scanModule.getAxes()).contains(axis)) {
					Axis smAxis = new Axis(scanModule, this.getAxisByName(
							scanModule.getChain().getScanDescription()
							.getMeasuringStation(), axis.getKey()));
					smAxis.setStepfunction(Stepfunctions.POSITIONLIST);
					smAxis.setPositionlist(StringUtil
							.buildCommaSeparatedString(axis.getValue()));
					scanModule.add(smAxis);
				}
			}
		}
		
		// TODO Auto-generated method stub
		return null;
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