package de.ptb.epics.eve.data.scandescription;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PositioningError;
import de.ptb.epics.eve.data.scandescription.errors.PositioningErrorTypes;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;

/**
 * <code>Positioning</code> represents a positioning behavior.
 * 
 * A position occurs after a scan. A motor axis is moved to a position, 
 * calculated by detector data.
 * 
 * @author Stephan Rehfeld <stephan.rehfeld (-at-) ptb.de>
 * @author Marcus Michalsky
 */
public class Positioning extends AbstractBehavior {

	/*
	 * The detector channel that delivers data to calculate the position of 
	 * the motor axis.
	 */
	private DetectorChannel detectorChannel;
	
	/*
	 * The detector channel that is used to normalize the detector data.
	 */
	private DetectorChannel normalization;
	
	/*
	 * The plug in controller for the plug in that calculates the motor 
	 * position with the detector data.
	 */
	private PluginController pluginController;
	
	/**
	 * Constructs a(n) (empty) <code>Positioning</code>.
	 */
	public Positioning() {
		this( null );
	}

	/**
	 * Constructs a <code>Positioning</code> with a given motor axis.
	 * 
	 * @param motorAxis the motor axis whose position is modified
	 */
	public Positioning(final MotorAxis motorAxis) {
		this(motorAxis, null);
	}
	
	/**
	 * Constructs a <code>Positioning</code> with a given motor axis and 
	 * detector channel.
	 * 
	 * @param motorAxis the motor axis whose position is modified 
	 * @param detectorChannel the detector channel used to calculate the new 
	 * 		  position.
	 */
	public Positioning(final MotorAxis motorAxis, 
					   final DetectorChannel detectorChannel) {
		this(motorAxis, detectorChannel, null);
	}
	
	/**
	 * Constructs a <code>Positioning</code> with a given motor axis, detector 
	 * channel, and detector channel for normalization.
	 * 
	 * @param motorAxis the motor axis whose position is modified 
	 * @param detectorChannel the detector channel used to calculate the new 
	 * 		  position.
	 * @param normalization the detector channel used to normalize the values 
	 * 		  of the other detector channel.
	 */
	public Positioning(final MotorAxis motorAxis, 
					   final DetectorChannel detectorChannel, 
					   final DetectorChannel normalization) {
		this(motorAxis, detectorChannel, normalization, null);
	}
	
	/**
	 * Constructs a <code>Positioning</code> with a given motor axis, detector 
	 * channel, detector channel for normalization, and plug in.
	 * 
	 * @param motorAxis the motor axis whose position is modified 
	 * @param detectorChannel the detector channel used to calculate the new 
	 * 		  position.
	 * @param normalization the detector channel used to normalize the values 
	 * 		  of the other detector channel.
	 * @param plugin the plug in that calculates the new position for the motor
	 * 		  axis.
	 */
	public Positioning(final MotorAxis motorAxis, 
					   final DetectorChannel detectorChannel, 
					   final DetectorChannel normalization, 
					   final PlugIn plugin) {
		this.abstractDevice = motorAxis;
		this.detectorChannel = detectorChannel;
		this.normalization = normalization;
		this.pluginController = new PluginController();
		if(plugin != null) {
			this.pluginController.setPlugin(plugin);
		}
		this.pluginController.addModelUpdateListener(this);
	}

	/**
	 * Returns the detector channel of the positioning.
	 * 
	 * @return the detector channel of the positioning.
	 */
	public DetectorChannel getDetectorChannel() {
		return this.detectorChannel;
	}

	/**
	 * Sets the detector channel of the positioning.
	 * 
	 * @param detectorChannel the detector channel of this positioning.
	 */
	public void setDetectorChannel(final DetectorChannel detectorChannel) {
		this.detectorChannel = detectorChannel;
		updateListeners();
	}

	/**
	 * Returns the detector channel used to normalize the values of the other 
	 * detector channel.
	 * 
	 * @return The detector channel used to normalize the values of the other 
	 * 		   detector channel.
	 */
	public DetectorChannel getNormalization() {
		return this.normalization;
	}

	/**
	 * Sets the detector channel used to normalize the values of the other 
	 * detector channel.
	 * 
	 * @param normalization the other detector channel used to normalize 
	 * 		  the values of the other detector channel.
	 */
	public void setNormalization(final DetectorChannel normalization) {
		this.normalization = normalization;
		updateListeners();
	}
	
	/**
	 * Returns the plug in controller that is used to control the plug in that 
	 * calculates the motor axis position out of the detect channel data.
	 * 
	 * @return the plug in controller that controls the plug in that calculates 
	 * 		   the new motor axis position.
	 */
	public PluginController getPluginController() {
		return this.pluginController;
	}
	
	/**
	 * Returns the controlled motor axis.
	 * 
	 * @return the controlled motor axis.
	 */
	public MotorAxis getMotorAxis() {
		return (MotorAxis)this.abstractDevice;
	}
	
	/**
	 * Sets the controlled motor axis.
	 * 
	 * @param motorAxis the controlled motor axis.
	 */
	public void setMotorAxis(final MotorAxis motorAxis) {
		this.abstractDevice = motorAxis;
		updateListeners();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<IModelError> getModelErrors() {
		final List<IModelError> modelErrors = new ArrayList<IModelError>();
		if(this.detectorChannel == null) {
			modelErrors.add(new PositioningError(this, 
					PositioningErrorTypes.NO_DETECTOR_CHANNEL_SET));
		}
		modelErrors.addAll(this.pluginController.getModelErrors());
		return modelErrors;
	}
	
	/*
	 * 
	 */
	private void updateListeners()
	{
		final CopyOnWriteArrayList<IModelUpdateListener> list = 
			new CopyOnWriteArrayList<IModelUpdateListener>(this.modelUpdateListener);
		
		Iterator<IModelUpdateListener> it = list.iterator();
		
		while(it.hasNext()) {
			it.next().updateEvent(new ModelUpdateEvent(this, null));
		}
	}
}