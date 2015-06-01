package de.ptb.epics.eve.editor.gef.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.commands.Command;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.ScanModule;

/**
 * Copies channels of a scan module to another.
 * 
 * @author Marcus Michalsky
 * @since 1.19
 */
public class CopyScanModuleChannels extends Command {
	private ScanModule from;
	private ScanModule to;
	
	/**
	 * Constructor.
	 * 
	 * @param from scan module channels should be copied from
	 * @param to scan module channels should be copied to
	 */
	public CopyScanModuleChannels(ScanModule from, ScanModule to) {
		this.from = from;
		this.to = to;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		List<Channel> clones = new ArrayList<Channel>();
		Map<Channel, DetectorChannel> channelMap = new HashMap<>();
		for (Channel channel : from.getChannels()) {
			Channel clone = Channel.newInstance(channel, to);
			clones.add(clone);
			if (channel.getNormalizeChannel() != null) {
				channelMap.put(clone, channel.getNormalizeChannel());
			}
		}
		to.addAll(clones, channelMap);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void undo() {
		for (Channel channel : from.getChannels()) {
			to.remove(channel);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void redo() {
		this.execute();
	}
}