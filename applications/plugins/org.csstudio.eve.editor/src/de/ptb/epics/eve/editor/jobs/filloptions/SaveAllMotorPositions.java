package de.ptb.epics.eve.editor.jobs.filloptions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.UIJob;

import de.ptb.epics.eve.data.measuringstation.Motor;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.PluginController;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.Stepfunctions;

/**
 * <code>SaveAllMotorPositions</code> saves all motor positions by inserting 
 * their axes into the given scan module and setting their plugin to 
 * "MotionDisabled".
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class SaveAllMotorPositions extends Job {

	private static final Logger logger = 
			Logger.getLogger(SaveAllMotorPositions.class.getName());
	
	// the scan module the axes are added to
	private ScanModule scanModule;
	
	/**
	 * Constructor.
	 * 
	 * @param name the name of the job
	 * @param scanModule the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ScanModule} the axes 
	 * 		positions should be saved in
	 */
	public SaveAllMotorPositions(String name, ScanModule scanModule) {
		super(name);
		this.scanModule = scanModule;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		logger.debug("job started");
		
		// get available motor axes
		List<MotorAxis> motorAxes = new ArrayList<MotorAxis>();
		for(Motor m : scanModule.getChain().getScanDescription().
						getMeasuringStation().getMotors()) {
			for(MotorAxis ma : m.getAxes()) {
				motorAxes.add(ma);
			}
		}
		
		monitor.beginTask(this.getName(), 
				scanModule.getAxes().length + motorAxes.size()*2);
		
		// delete present axes
		monitor.subTask("deleting present axes");
		UIJob deletePresentAxes = new DeletePresentAxes();
		deletePresentAxes.setUser(true);
		deletePresentAxes.schedule();
		monitor.worked(scanModule.getAxes().length);
		
		// create axes
		final List<Axis> axes = new ArrayList<Axis>();
		for(MotorAxis ma : motorAxes) {
			monitor.subTask("creating axis " + ma.getName());
			final Axis axis = new Axis(scanModule);
			axis.setMotorAxis(ma);
			axis.setStepfunction(Stepfunctions.stepfunctionToString(
					Stepfunctions.PLUGIN));
			PlugIn motionDisabled = scanModule.getChain().getScanDescription().
					getMeasuringStation().getPluginByName("MotionDisabled");
			axis.setPositionPluginController(new PluginController(motionDisabled));
			axis.getPositionPluginController().setPlugin(motionDisabled);
			axes.add(axis);
			if(logger.isDebugEnabled()) {
				// progress view in "Slow Motion" when debugging
				try { Thread.sleep(200); } catch (Exception e) {}
			}
			monitor.worked(1);
		}
		
		// add axes
		monitor.subTask("adding axes");
		UIJob addAllAxes = new AddAllAxes(axes);
		addAllAxes.setUser(true);
		addAllAxes.schedule();
		monitor.worked(motorAxes.size());
		
		logger.debug("job finished");
		monitor.done();
		return Status.OK_STATUS;
	}
	
	/* ********************************************************************* */
	
	/**
	 * deletes all axes present in the scan module
	 */
	private class DeletePresentAxes extends UIJob {
		
		/**
		 * Constructor.
		 * 
		 * @param name the name of the job
		 */
		public DeletePresentAxes() {
			super("deleting present axes");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			final Axis[] presentAxes = scanModule.getAxes();
			monitor.beginTask(this.getName(), presentAxes.length);
			for(Axis a : presentAxes) {
				monitor.subTask("removing axis " + a.getMotorAxis().getName());
				scanModule.remove(a);
				monitor.worked(1);
			}
			return Status.OK_STATUS;
		}
	}
	
	/**
	 * adds all axes (with MotionDisabled plugin)
	 * 
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class AddAllAxes extends UIJob {
		
		private final List<Axis> axes;
		
		/**
		 * Constructor.
		 * 
		 * @param axes the axes that should be added to the scan module
		 */
		public AddAllAxes(final List<Axis> axes) {
			super("adding axes");
			this.axes = axes;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			monitor.beginTask("adding axes to scan module", axes.size());
			for(Axis axis : axes) {
				monitor.subTask("adding axis " + 
						axis.getMotorAxis().getName());
				scanModule.add(axis);
				monitor.worked(1);
			}
			scanModule.setName("S MPOS");
			monitor.done();
			return Status.OK_STATUS;
		}
	}
}