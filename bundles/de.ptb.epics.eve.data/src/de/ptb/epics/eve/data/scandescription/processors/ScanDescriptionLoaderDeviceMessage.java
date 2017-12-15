package de.ptb.epics.eve.data.scandescription.processors;

/**
 * A Message containing a type and a description of the action taken.
 * <p>
 * Created by the 
 * {@link de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoaderHandler}
 * and used by the {@link de.ptb.epics.eve.editor.dialogs.LostDeviesDialog}.
 * 
 * @author Marcus Michalsy
 * @since 1.5
 */
public class ScanDescriptionLoaderDeviceMessage {

	private ScanDescriptionLoaderMessageType type;
	private String action;
	
	/**
	 * Constructor.
	 * 
	 * @param type the type as in 
	 * 		{@link de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoaderDeviceMessage}
	 * @param action the action taken
	 */
	public ScanDescriptionLoaderDeviceMessage(
			ScanDescriptionLoaderMessageType type, String action) {
		this.type = type;
		this.action = action;
	}

	/**
	 * @return the type
	 */
	public ScanDescriptionLoaderMessageType getType() {
		return type;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
}