/**
 * {@link de.ptb.epics.eve.data.scandescription.axismode.AxisMode} is a strategy 
 * of an {@link de.ptb.epics.eve.data.scandescription.Axis}. 
 * During an Axis' lifecycle its mode could be changed. Several modes are 
 * available, e.g. 
 * the generic {@link de.ptb.epics.eve.data.scandescription.axismode.AddMultiplyMode}, 
 * {@link de.ptb.epics.eve.data.scandescription.axismode.FileMode}, 
 * {@link de.ptb.epics.eve.data.scandescription.axismode.PluginMode} and
 * {@link de.ptb.epics.eve.data.scandescription.axismode.PositionlistMode}.
 * For the polymorphic AddMultiplyMode implementations exist for 
 * {@link java.lang.Integer}, {@link java.lang.Double}, {@link java.util.Date} 
 * and {@link javax.xml.datatype.Duration} types, each auto adjusting its start, 
 * stop, stepwidth triple.
 * 
 * @author Marcus Michalsky
 * @since 1.7
 */
package de.ptb.epics.eve.data.scandescription.axismode;