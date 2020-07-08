/**
 * Contains all elements related to the newly introduced SM Axes / Channels View.
 * 
 * {@link de.ptb.epics.eve.editor.views.axeschannelsview.ui.AxesChannelsView} is 
 * an {@link de.ptb.epics.eve.editor.views.AbstractScanModuleView}, i.e. depending
 * on the current selection different content is shown. After determining the 
 * selected scan module type the corresponding 
 * {@link de.ptb.epics.eve.editor.views.axeschannelsview.ui.AxesChannelsViewComposite}
 * is notified.
 * Besides the snapshot type composites, for classic scan modules 
 * {@link de.ptb.epics.eve.editor.views.axeschannelsview.classiccomposite.ui.ClassicComposite}
 * is used. It contains an axes and channels table which was formerly contained 
 * in the Scan Module View.
 * 
 * @author Marcus Michalsky
 * @since 1.34
 */
package de.ptb.epics.eve.editor.views.axeschannelsview;