package de.ptb.epics.eve.editor.gef.editparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanDescription;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ScanDescriptionEditPart extends AbstractGraphicalEditPart {

	/**
	 * Constructor.
	 * 
	 * @param scanDescription 
	 */
	public ScanDescriptionEditPart(ScanDescription scanDescription) {
		this.setModel(scanDescription);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The model element represents the entire graph -> figure is a layer in 
	 * which all other figures can be displayed.
	 */
	@Override
	protected IFigure createFigure() {
		Figure figure = new FreeformLayer();
		figure.setBorder(new MarginBorder(0));
		figure.setLayoutManager(new FreeformLayout());
		return figure;
	}

	/**
	 * Returns the model of this edit part.
	 */
	public ScanDescription getModel() {
		return (ScanDescription) super.getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Object> getModelChildren() {
		List<Object> children = new ArrayList<Object>();
		// add start events
		for(Chain ch : this.getModel().getChains()) {
			children.add(ch.getStartEvent());
		}
		children.addAll(this.getModel().getChains());
		return children;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
	}
}