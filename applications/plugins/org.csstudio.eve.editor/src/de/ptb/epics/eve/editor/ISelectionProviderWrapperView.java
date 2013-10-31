package de.ptb.epics.eve.editor;

import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * Views containing multiple choices of selection providers (i.e. multiple 
 * jface viewers) should implement this interface.
 * The currently active {@link org.eclipse.jface.viewers.ISelectionProvider} 
 * should be set via {@link #setSelectionProvider(ISelectionProvider)} by 
 * delegating it to a contained 
 * {@link de.ptb.epics.eve.util.jface.SelectionProviderWrapper} instance.
 * See <a href="http://www.eclipsezone.com/eclipse/forums/t74510.html">
 * 		Multiple Selection Providers (EclipseZone)</a> for more information.
 * @author Marcus Michalsky
 * @since 0.4.2
 */
public interface ISelectionProviderWrapperView {

	/**
	 * Sets the currently active 
	 * {@link org.eclipse.jface.viewers.ISelectionProvider} of the implementing 
	 * view.
	 * Should be done by delegation to a contained 
	 * {@link de.ptb.epics.eve.util.jface.SelectionProviderWrapper}.
	 * 
	 * @param selectionProvider the selection provider that should be the 
	 * 		active one
	 */
	void setSelectionProvider(ISelectionProvider selectionProvider);
}