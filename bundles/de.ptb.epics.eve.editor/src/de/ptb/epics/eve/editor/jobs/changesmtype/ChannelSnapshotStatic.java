package de.ptb.epics.eve.editor.jobs.changesmtype;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;

/**
 * @author Marcus Michalsky
 * @since 1.31
 */
public class ChannelSnapshotStatic extends ChangeToJob {
	private static final Logger LOGGER = 
			Logger.getLogger(ChannelSnapshotStatic.class.getName());
	private final ScanModule scanModule;
	private final IMeasuringStation measuringStation;

	/**
	 * @param scanModule the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ScanModule} the axes 
	 * 		positions should be saved in
	 * @param the device definition snapshot axis are taken from
	 */
	public ChannelSnapshotStatic(ScanModule scanModule, IMeasuringStation measuringStation) {
		super("Change SM type to Channel Snapshot (static)");
		this.scanModule = scanModule;
		this.measuringStation = measuringStation;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		LOGGER.debug("executing");
		monitor.beginTask(this.getName(), 3);
		// delete present devices
		scanModule.removeAll();
		monitor.worked(1);	
		LOGGER.debug("content of ScanModule removed");
		// set name and type
		this.scanModule.setType(ScanModuleTypes.SAVE_CHANNEL_VALUES);
		this.scanModule.setName(ScanModule.DEFAULT_NAME_SAVE_CHANNEL_VALUES);
		monitor.worked(1);
		LOGGER.debug("type and name updated");
		// add snapshot channels
		this.scanModule.saveAllChannelValues(this.measuringStation);
		monitor.worked(1);
		LOGGER.debug("snapshot channels added");
		monitor.done();
		LOGGER.debug("done");
		return Status.OK_STATUS;
	}
}
