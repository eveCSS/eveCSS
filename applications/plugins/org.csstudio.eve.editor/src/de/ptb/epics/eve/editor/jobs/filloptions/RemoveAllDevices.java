package de.ptb.epics.eve.editor.jobs.filloptions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;

/**
 * {@link org.eclipse.ui.progress.UIJob} removing all devices 
 * from the given 
 * {@link de.ptb.epics.eve.data.scandescription.ScanModule}.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class RemoveAllDevices extends UIJob {
	
	private ScanModule scanModule;
	
	/**
	 * Constructor.
	 * 
	 * @param name the job name
	 * @param scanModule the scan module the devices should be deleted from
	 */
	public RemoveAllDevices(String name, ScanModule scanModule) {
		super(name);
		this.scanModule = scanModule;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		try {
			monitor.beginTask("Removing Devices", 1);
			this.scanModule.removeAll();
			this.scanModule.setType(ScanModuleTypes.CLASSIC);
			this.scanModule.setName("Empty");
			monitor.worked(1);
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
}