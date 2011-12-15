package de.ptb.epics.eve.editor.jobs.filloptions;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.UIJob;

import de.ptb.epics.eve.data.measuringstation.Detector;
import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * <code>SaveAllDetectorValues</code> saves all detector channel values by 
 * inserting the channels in the given scan module.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class SaveAllDetectorValues extends Job {

	private static final Logger logger = 
			Logger.getLogger(SaveAllDetectorValues.class.getName());
	
	// the scan module the detectors are added to
	private ScanModule scanModule;
	
	/**
	 * Constructor.
	 * 
	 * @param name the name of the job
	 * @param scanModule the 
	 * 		{@link de.ptb.epics.eve.data.scandescription.ScanModule} the 
	 * 		detector values should be saved in
	 */
	public SaveAllDetectorValues(String name, ScanModule scanModule) {
		super(name);
		this.scanModule = scanModule;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		logger.debug("job started");
		
		// get available channels
		List<DetectorChannel> detectorChannels = new ArrayList<DetectorChannel>();
		for(Detector det : scanModule.getChain().getScanDescription().
							getMeasuringStation().getDetectors()) {
			for(DetectorChannel ch : det.getChannels()) {
				detectorChannels.add(ch);
			}
		}
		
		monitor.beginTask(this.getName(), scanModule.getAxes().length + 
				scanModule.getChannels().length + detectorChannels.size()*2);
		
		// delete present axes and channels
		monitor.subTask("deleting present axes and channels");
		UIJob deletePresentAxesAndChannels = new DeletePresentAxesAndChannels();
		deletePresentAxesAndChannels.setUser(true);
		deletePresentAxesAndChannels.schedule();
		monitor.worked(scanModule.getAxes().length + scanModule.getChannels().length);
		
		// creating channels
		final List<Channel> channels = new ArrayList<Channel>();
		for(DetectorChannel ch : detectorChannels) {
			monitor.subTask("creating channel " + ch.getName());
			Channel channel = new Channel(scanModule);
			channel.setDetectorChannel(ch);
			channel.setAverageCount(1);
			channels.add(channel);
			if(logger.isDebugEnabled()) {
				// progress view in "Slow Motion" when debugging
				try { Thread.sleep(200); } catch (Exception e) {}
			}
			monitor.worked(1);
		}
		
		// adding counter and channels
		monitor.subTask("adding channels");
		MotorAxis counter = scanModule.getChain().getScanDescription().
				getMeasuringStation().getMotorAxisById("Counter-mot");
		final Axis axis = new Axis(scanModule);
		axis.setMotorAxis(counter);
		axis.setStart("0");
		axis.setStop("0");
		UIJob addAllChannels = new AddAllChannels(channels, axis);
		addAllChannels.setUser(true);
		addAllChannels.schedule();
		monitor.worked(detectorChannels.size());
		
		logger.debug("job finished");
		monitor.done();
		return Status.OK_STATUS;
	}
	
	/* ********************************************************************* */
	
	/**
	 * deletes all axes and channels present in the scan module
	 * 
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class DeletePresentAxesAndChannels extends UIJob {
		
		/**
		 * Constructor.
		 * 
		 * @param name the name of the job
		 */
		public DeletePresentAxesAndChannels() {
			super("deleting present axes and channels");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			final Axis[] presentAxes = scanModule.getAxes();
			final Channel[] presentChannels = scanModule.getChannels();
			monitor.beginTask(this.getName(), 
					presentAxes.length + presentChannels.length);
			for(Axis a : presentAxes) {
				monitor.subTask("removing axis " + a.getMotorAxis().getName());
				scanModule.remove(a);
				monitor.worked(1);
			}
			for(Channel ch : presentChannels) {
				monitor.subTask("removing channel " + 
						ch.getDetectorChannel().getName());
				scanModule.remove(ch);
				monitor.worked(1);
			}
			monitor.done();
			return Status.OK_STATUS;
		}
	}
	
	/**
	 * adds all channels (with average 1) and a counter (Start 0, Stop 0)
	 * 
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class AddAllChannels extends UIJob {
		
		private final List<Channel> channels;
		private final Axis counter;
		
		/**
		 * Constructor.
		 * 
		 * @param channels the channels that should be added
		 * @param counter the counter (axis) that should be added
		 */
		public AddAllChannels(List<Channel> channels, Axis counter) {
			super("adding channels");
			this.channels = channels;
			this.counter = counter;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			monitor.beginTask("adding channels", channels.size() + 1);
			monitor.subTask("adding counter");
			scanModule.add(counter);
			monitor.worked(1);
			for(Channel ch : channels) {
				monitor.subTask("adding channel " + 
						ch.getDetectorChannel().getName());
				scanModule.add(ch);
				monitor.worked(1);
			}
			scanModule.setName("S DVAL");
			monitor.done();
			return Status.OK_STATUS;
		}
	}
}