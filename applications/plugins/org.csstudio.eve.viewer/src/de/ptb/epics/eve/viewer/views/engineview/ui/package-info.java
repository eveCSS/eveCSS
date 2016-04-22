/**
 * Contains all gui elements of the
 * {@link de.ptb.epics.eve.viewer.views.engineview.ui.EngineView}.
 * 
 * EngineView is the ViewPart of the Engine Perspective.
 * 
 * ProgressBarComposite and StatusTableComposite are parts of the EngineView.
 * ProgressBarComposite contains the GUI elements of the scan progress bar.
 * 
 * StatusTableComposite displays detailed information of chains and scan modules 
 * of the currently running scan. It uses a custom ContentProvider and Sorter 
 * (TableViewerSorter).
 * 
 * The TableViewerSorter sorts the items first by the natural order of their 
 * chain id and then in execution order of the scan modules. The execution order 
 * is determined by calculating the preorder notation of the binary tree the 
 * chain represents (left child = nested module, right child = appended module).
 * 
 * @author Marcus Michalsky
 * @since 1.25
 */
package de.ptb.epics.eve.viewer.views.engineview.ui;