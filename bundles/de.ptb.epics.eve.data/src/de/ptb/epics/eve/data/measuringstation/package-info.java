/**
 * Provides classes necessary to model a measuring station.
 * <p>
 * Generally a measuring station is an alignment of devices to run experiments. 
 * The measuring station itself is represented by 
 * {@link de.ptb.epics.eve.data.measuringstation.MeasuringStation} 
 * (implementing the interface 
 * {@link de.ptb.epics.eve.data.measuringstation.IMeasuringStation}). Beginning 
 * with an abstract formulation of a device 
 * ({@link de.ptb.epics.eve.data.measuringstation.AbstractDevice}) there are two 
 * distinct devices available which inherit from it.<br>
 * The model distinguishes between 
 * {@link de.ptb.epics.eve.data.measuringstation.AbstractMainPhaseDevice} and   
 * {@link de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice}.<br> 
 * {@link de.ptb.epics.eve.data.measuringstation.Motor} and 
 * {@link de.ptb.epics.eve.data.measuringstation.MotorAxis} as well as 
 * {@link de.ptb.epics.eve.data.measuringstation.Detector} and  
 * {@link de.ptb.epics.eve.data.measuringstation.DetectorChannel} are Subclasses 
 * of  {@link de.ptb.epics.eve.data.measuringstation.AbstractMainPhaseDevice}.<br>
 * Motors are devices where you can read from or write to (e.g. a 'real' motor 
 * or a radiator). They contain at least one axis. Axes can be controlled in 
 * different ways:
 * <ul>
 *   <li>Start-Value, Stop-Value, Step-Width: like 
 *   	 <code>for(int i=start; i<=stop; i+stepwidth)</code></li>
 *   <li>Position File: external file with position data</li>
 *   <li>PlugIn: like a function calculating the position</li>
 *   <li>set of states: e.g. {red, green, blue}</li>
 * </ul>
 * Additionally there are two 'non-physical' motors available:
 * <ul>
 *   <li>Counter: e.g. like the physical motor with start, stop and step-width</li>
 *   <li>Timer: e.g. measure 10 times once each second</li>
 * </ul>
 * Detectors are devices to measure something. They have at least one channel 
 * ({@link de.ptb.epics.eve.data.measuringstation.DetectorChannel}). During the 
 * main phase they can only be read. Additionally options can be set during pre 
 * and post scan phases.<br>Furthermore 
 * {@link de.ptb.epics.eve.data.measuringstation.AbstractPrePostscanDevice} 
 * distinguishes between {@link de.ptb.epics.eve.data.measuringstation.Device} 
 * and {@link de.ptb.epics.eve.data.measuringstation.Option}.<br>
 * Values can be assigned to these devices only during pre and post scan phases 
 * (e.g. a beam shutter - open/close, fan - on/off).
 * 
 * @since 0.4.1
 * @author Marcus Michalsky
 */
package de.ptb.epics.eve.data.measuringstation;