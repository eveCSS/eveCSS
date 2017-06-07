/**
 * Provides classes necessary to model a scan description. 
 * <p>
 * A scan description is an XML-file which describes how involved devices 
 * behave during a scan.<br>
 * It is represented by 
 * {@link de.ptb.epics.eve.data.scandescription.ScanDescription}. Within a scan 
 * description multiple scans are possible. A scan is a composition of 
 * {@link de.ptb.epics.eve.data.scandescription.Chain}s. Each chain is composed 
 * of {@link de.ptb.epics.eve.data.scandescription.ScanModule}s. A scan module 
 * could be attached to another or being nested. <br>
 * Scan modules are partitioned into three stages, a PreScan Phase, a Main Phase 
 * and a PostScan Phase:
 * <ul>
 *   <li>Prescan-Phase (Prior to a scan): values and options for Devices will be 
 *       set (e.g. open beam shutter).</li>
 *   <li>Main-Phase: actually measures something by controlling devices 
 *   	 (e.g. moving motor axis) and reading detector channels.</li>
 *   <li>Postscan-Phase (after measuring): options and devices are eventually 
 *   	 set again, or reset (e.g. close beam shutter).</li>
 * </ul>
 * Scans begin with a {@link de.ptb.epics.eve.data.scandescription.StartEvent}. 
 * 
 * @since 0.4.1
 * @author Marcus Michalsky
 */
package de.ptb.epics.eve.data.scandescription;