package de.ptb.epics.eve.editor.gef.figures;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ScanDescriptionFigure extends FreeformLayer {
	
	/**
	 * Constructor.
	 */
	public ScanDescriptionFigure() {
		super();
		this.setLayoutManager(new FreeformLayout());
	}
}