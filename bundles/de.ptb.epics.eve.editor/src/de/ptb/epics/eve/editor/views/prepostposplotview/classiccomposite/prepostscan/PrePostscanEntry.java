package de.ptb.epics.eve.editor.views.prepostposplotview.classiccomposite.prepostscan;

import de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.Prescan;

/**
 * View Model.
 * 
 * @author Marcus Michalsky
 * @since 1.35
 */
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

	public AbstractPrePostscanDevice getDevice() {
		return device;
	}

	public Prescan getPrescan() {
		return prescan;
	}
	
	public void setPrescan(Prescan prescan) {
		this.prescan = prescan;
	}

	public Postscan getPostscan() {
		return postscan;
	}
	
	public void setPostscan(Postscan postscan) {
		this.postscan = postscan;
	}
}
