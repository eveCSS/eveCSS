package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan;

import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;

public class PrePostscanEntry {
	private AbstractPrePostscanDevice device;
	private Prescan prescan;
	private Postscan postscan;
	
	public PrePostscanEntry(AbstractPrePostscanDevice device) {
		this(device, null, null);
	}
	
	public PrePostscanEntry(AbstractPrePostscanDevice device, Prescan prescan, 
			Postscan postscan) {
		this.device = device;
		this.prescan = prescan;
		this.postscan = postscan;
	}

	/**
	 * @return the device
	 */
	public AbstractPrePostscanDevice getDevice() {
		return device;
	}

	/**
	 * @return the prescan
	 */
	public Prescan getPrescan() {
		return prescan;
	}

	/**
	 * @return the postscan
	 */
	public Postscan getPostscan() {
		return postscan;
	}
}
