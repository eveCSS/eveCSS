package de.ptb.epics.eve.editor.jobs.changesmtype;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public class AxisSnapshotDynamic extends ChangeToJob {
	private static final Logger LOGGER = 
			Logger.getLogger(AxisSnapshotDynamic.class.getName());
	private ScanModule scanModule;

	public AxisSnapshotDynamic(ScanModule scanModule) {
		super("Change SM type to Axis Snapshot (dynamic)");
		this.scanModule = scanModule;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		LOGGER.debug("executing");
		monitor.beginTask(this.getName(), 2);
		scanModule.removeAll();
		monitor.worked(1);
		LOGGER.debug("content of ScanModule removed");
		this.scanModule.setType(ScanModuleTypes.DYNAMIC_AXIS_POSITIONS);
		this.scanModule.setName(ScanModule.DEFAULT_NAME_DYNAMIC_AXIS_POSITIONS);
		monitor.worked(1);
		LOGGER.debug("type and name updated");
		monitor.done();
		LOGGER.debug("done");
		return Status.OK_STATUS;
	}

}
