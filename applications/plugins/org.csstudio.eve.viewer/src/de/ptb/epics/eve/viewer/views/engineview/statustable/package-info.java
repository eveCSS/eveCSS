/**
 * Contains the non-GUI elements related to the table in the StatusTableComposite.
 * 
 * Entries of the StatusTable are represented by StatusTableElement. 
 * 
 * The StatusTracker listens to the engine client and keeps track of the scan 
 * information in a ScanInfo object. 
 * 
 * A ScanInfo could contain multiple ChainInfos which again could contain 
 * multiple ScanModuleInfos.
 * 
 * @author Marcus Michalsky
 * @since 1.26
 */
package de.ptb.epics.eve.viewer.views.engineview.statustable;