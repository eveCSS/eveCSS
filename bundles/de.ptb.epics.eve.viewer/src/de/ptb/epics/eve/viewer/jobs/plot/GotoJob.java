package de.ptb.epics.eve.viewer.jobs.plot;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.ptb.epics.eve.util.pv.PVWrapper;

/**
 * {@link org.eclipse.core.runtime.jobs.Job} which sends a goto to the given 
 * motor (and a trigger if necessary), see 
 * {@link #GotoJob(String, String, String, Object)} for more details.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class GotoJob extends Job {

	private static Logger logger = Logger.getLogger(GotoJob.class.getName());
	
	private String motorPv;
	private String triggerPv;
	private Object rawValue;
	
	/**
	 * Constructor.
	 * 
	 * @param name the name of the job
	 * @param motorPv the process variable of the motor
	 * @param triggerPv the process variable of the motor trigger
	 * @param rawValue the value the motor should be set to
	 */
	public GotoJob(String name, String motorPv, String triggerPv, Object rawValue) {
		super(name);
		this.motorPv = motorPv;
		this.triggerPv = triggerPv;
		this.rawValue = rawValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			// 4 steps: create object, connect pv, set value, disconnect pv
			monitor.beginTask(this.getName(), 4);
			// Step 1: create object
			monitor.subTask("Creating PV");
			PVWrapper pv = new PVWrapper(motorPv, triggerPv);
			monitor.worked(1);
			// Step 2: (wait for) connect pv (10 tries)
			monitor.subTask("Connecting PV");
			int i = 0;
			while(!pv.isConnected() && i++ < 10) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
			monitor.worked(1);
			if (monitor.isCanceled() || i >= 10) {
				return Status.CANCEL_STATUS;
			}
			// Step 3: set value
			monitor.subTask("Set value of " + motorPv + " to " + rawValue);
			pv.setValue(rawValue);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.debug(e.getMessage(), e);
			}
			monitor.worked(1);
			logger.debug("send goto " + this.rawValue + " to " + this.motorPv);
			// Step 4: disconnect pv
			monitor.subTask("Disconnecting PV");
			pv.disconnect();
			monitor.worked(1);
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean belongsTo(Object family) {
		if(family.equals("PV")) {
			return true;
		}
		return false;
	}
}