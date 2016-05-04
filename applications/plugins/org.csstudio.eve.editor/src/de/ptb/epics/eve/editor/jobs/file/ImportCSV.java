package de.ptb.epics.eve.editor.jobs.file;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;
import de.ptb.epics.eve.editor.dialogs.csvimport.CSVImportDialog;
import de.ptb.epics.eve.util.csv.CSVUtil;
import de.ptb.epics.eve.util.io.StringUtil;
import javafx.util.Pair;

/**
 * @author Marcus Michalsky
 * @since 1.25.2
 */
public class ImportCSV extends Job {
	private static final Logger LOGGER = Logger.getLogger(
			ImportCSV.class.getName());
	
	private final String family = "file";
	
	private File csvFile;
	private char delimiter;
	private ScanModule scanModule;
	
	public ImportCSV(File csvFile, char delimiter, ScanModule scanModule) {
		super("Import axes from CSV");
		this.csvFile = csvFile;
		this.delimiter = delimiter;
		this.scanModule = scanModule;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		monitor.beginTask(this.getName(), 3);
		
		monitor.subTask("reading CSV file");
		LOGGER.debug("reading CSV file");
		List<Pair<String, List<String>>> axesList = CSVUtil.getColumns(
				csvFile, delimiter);
		monitor.worked(1);
		
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		
		monitor.subTask("checking for invalid/already present devices");
		LOGGER.debug("checking for invalid/already present devices");
		List<Pair<String, List<String>>> present = new ArrayList<>();
		List<Pair<String, List<String>>> absent = new ArrayList<>();
		List<Pair<String, List<String>>> invalid = new ArrayList<>();
		List<Axis> toRemove = new ArrayList<>();
		
		for (Pair<String, List<String>> axis : axesList) {
			MotorAxis motorAxis = scanModule.getChain().getScanDescription().
					getMeasuringStation().getMotorAxisByName(axis.getKey());
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
		monitor.worked(1);
		
		if (monitor.isCanceled()) {
			return Status.CANCEL_STATUS;
		}
		
		monitor.subTask("presenting import result");
		LOGGER.debug("presenting import result");
		UIJob importCSVUI = new ImportCSVUI(present, absent, invalid, toRemove, 
				scanModule);
		importCSVUI.setUser(true);
		importCSVUI.schedule();
		
		try {
			importCSVUI.join();
		} catch(InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
			return Status.CANCEL_STATUS;
		}
		monitor.worked(1);
		monitor.done();
		return Status.OK_STATUS;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean belongsTo(Object family) {
		return family.equals(this.family);
	}
	
	private class ImportCSVUI extends UIJob {
		private List<Pair<String, List<String>>> present;
		private List<Pair<String, List<String>>> absent;
		private List<Pair<String, List<String>>> invalid;
		private List<Axis> toRemove;
		private ScanModule scanModule;
		
		public ImportCSVUI(List<Pair<String, List<String>>> present, 
				List<Pair<String, List<String>>> absent, 
				List<Pair<String, List<String>>> invalid,
				List<Axis> toRemove, 
				ScanModule scanModule) {
			super("");
			this.present = present;
			this.absent = absent;
			this.invalid = invalid;
			this.toRemove = toRemove;
			this.scanModule = scanModule;
		}
		
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			monitor.beginTask(this.getName(), 
					scanModule.getPlotWindows().length + 
					toRemove.size() + present.size() + absent.size() + 
					scanModule.getPlotWindows().length);
			
			if (!present.isEmpty() || !invalid.isEmpty()) {
				monitor.subTask("showing import conflicts");
				LOGGER.debug("showing import conflicts");
				CSVImportDialog csvDialog = new CSVImportDialog(Display
						.getDefault().getActiveShell(), present, absent,
						invalid);
				csvDialog.open();
				if (csvDialog.getReturnCode() == Window.CANCEL) {
					return Status.CANCEL_STATUS;
				}
			}
			
			monitor.subTask("Gathering plot window relationships of related axes");
			LOGGER.debug("Gathering plot window relationships of related axes");
			Map<PlotWindow, MotorAxis> plotWindowRelationships = new HashMap<>();
			for(PlotWindow plotWindow : scanModule.getPlotWindows()) {
				plotWindowRelationships.put(plotWindow, plotWindow.getXAxis());
				monitor.worked(1);
			}
			
			monitor.subTask("removing axes which were already present");
			LOGGER.debug("removing axes which were already present");
			for (Axis smAxis : toRemove) {
				scanModule.remove(smAxis);
				monitor.worked(1);
			}
			monitor.subTask("adding axes from CSV file");
			LOGGER.debug("adding axes from CSV file");
			for (Pair<String, List<String>> axis : present) {
				addAxis(scanModule, axis);
				monitor.worked(1);
			}
			for (Pair<String, List<String>> axis : absent) {
				addAxis(scanModule, axis);
				monitor.worked(1);
			}
			
			monitor.subTask("restoring (eventually lost) plot window relations");
			LOGGER.debug("restoring (eventually lost) plot window relations");
			for(PlotWindow plotWindow : plotWindowRelationships.keySet()) {
				plotWindow.setXAxis(plotWindowRelationships.get(plotWindow));
				monitor.worked(1);
			}
			
			monitor.done();
			LOGGER.debug("finished CSV Import");
			return Status.OK_STATUS;
		}
		
		private void addAxis(ScanModule scanModule, Pair<String, List<String>> axis) {
			Axis smAxis = new Axis(scanModule, scanModule.getChain().
					getScanDescription().getMeasuringStation().
					getMotorAxisByName(axis.getKey()), false);
			smAxis.setStepfunction(Stepfunctions.POSITIONLIST);
			smAxis.setPositionlist(StringUtil.buildCommaSeparatedString(
					axis.getValue()));
			scanModule.add(smAxis);
		}
	}
}