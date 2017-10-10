/**
 * Channel modes are different behaviors of detector channels.
 * They are implemented via the state pattern. The context is 
 * the {@link de.ptb.epics.eve.data.scandescription.Channel}, the state is {@link de.ptb.epics.eve.data.scandescription.channelmode.ChannelMode} 
 * and concrete states are its sub classes (e.g. {@link de.ptb.epics.eve.data.scandescription.channelmode.StandardMode}, {@link de.ptb.epics.eve.data.scandescription.channelmode.IntervalMode}).
 */
package de.ptb.epics.eve.data.scandescription.channelmode;