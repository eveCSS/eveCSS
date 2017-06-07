/**
 * Contains elements for saving default values of motor axes and detector 
 * channels.
 * 
 * A {@link de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager}
 * is used to load defaults from a given XML file. During its lifecycle its 
 * default values can be updated via 
 * {@link de.ptb.epics.eve.data.scandescription.defaults.DefaultsManager#update(de.ptb.epics.eve.data.scandescription.ScanDescription)}.
 * The state (current defaults) can be saved in a file.
 * 
 * @author Marcus Michalsky
 * @since 1.8
 */
package de.ptb.epics.eve.data.scandescription.defaults;