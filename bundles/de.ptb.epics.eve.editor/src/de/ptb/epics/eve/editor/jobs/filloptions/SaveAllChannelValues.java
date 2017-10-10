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
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.ScanModuleTypes;

/**
 * <code>SaveAllDetectorValues</code> saves all detector channel values by 
 * inserting the channels in the given scan module.
 * 
 * @author Marcus Michalsky
 * @since 1.1
 */
public class SaveAllChannelValues extends Job {

	private static final Logger logger = 
			Logger.getLogger(SaveAllChannelValues.class.getName());
	
	private String family = "filloptions";
	
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
	public SaveAllChannelValues(String name, ScanModule scanModule) {
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
				if (ch.isSaveValue()) {
					detectorChannels.add(ch);
				}
			}
		}
		
		monitor.beginTask(this.getName(), scanModule.getAxes().length + 
				scanModule.getDeviceCount() + detectorChannels.size()*2);
		
		// delete present devices
		monitor.subTask("removing present devices");
		UIJob removeAllDevices = new RemoveAllDevices("Remove present Devices", 
				this.scanModule);
		removeAllDevices.setUser(true);
		removeAllDevices.schedule();
		
		// wait for removal Thread
		try {
			removeAllDevices.join();
		} catch (InterruptedException e1) {
			logger.error(e1);
			return Status.CANCEL_STATUS;
		}
		
		monitor.worked(scanModule.getDeviceCount());
		
		// creating channels
		final List<Channel> channels = new ArrayList<Channel>();
		for(DetectorChannel ch : detectorChannels) {
			monitor.subTask("creating channel " + ch.getName());
			Channel channel = new Channel(scanModule);
			channel.setDetectorChannel(ch);
			channel.setAverageCount(1);
			channel.setDeferred(false);
			channels.add(channel);
			if(logger.isDebugEnabled()) {
				// progress view in "Slow Motion" when debugging
				try { Thread.sleep(200); } catch (Exception e) {}
			}
			monitor.worked(1);
		}
		
		// adding counter and channels
		monitor.subTask("adding channels");
		UIJob addAllChannels = new AddAllChannels(channels);
		addAllChannels.setUser(true);
		addAllChannels.schedule();
		monitor.worked(detectorChannels.size());
		
		logger.debug("job finished");
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldRun() {
		if (logger.isDebugEnabled()) {
			if (Job.getJobManager().find(this.family).length > 1) {
				logger.debug("found another running job of family 'filloptions', aborting");
			}
		}
		return Job.getJobManager().find(this.family).length == 1;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldSchedule() {
		if (logger.isDebugEnabled()) {
			if (Job.getJobManager().find(this.family).length > 0) {
				logger.debug("found another scheduled job of family 'filloptions', aborting");
			}
		}
		return Job.getJobManager().find(this.family).length == 0;
	}
	
	/* ********************************************************************* */
	
	/**
	 * adds all channels (with average 1) and a counter (Start 0, Stop 0)
	 * 
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class AddAllChannels extends UIJob {
		private final List<Channel> channels;
		
		/**
		 * Constructor.
		 * 
		 * @param channels the channels that should be added
		 * @param counter the counter (axis) that should be added
		 */
		public AddAllChannels(List<Channel> channels) {
			super("adding channels");
			this.channels = channels;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			monitor.beginTask("adding channels", channels.size());
			for(Channel ch : channels) {
				monitor.subTask("adding channel " + 
						ch.getDetectorChannel().getName());
				scanModule.add(ch);
				monitor.worked(1);
			}
			scanModule.setName("S CVAL");
			scanModule.setType(ScanModuleTypes.SAVE_CHANNEL_VALUES);
			monitor.done();
			return Status.OK_STATUS;
		}
	}
}