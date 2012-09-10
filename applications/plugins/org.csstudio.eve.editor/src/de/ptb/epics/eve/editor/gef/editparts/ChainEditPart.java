package de.ptb.epics.eve.editor.gef.editparts;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ShortestPathConnectionRouter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.gef.editpolicies.ChainComponentEditPolicy;
import de.ptb.epics.eve.editor.gef.editpolicies.ChainLayoutEditPolicy;
import de.ptb.epics.eve.editor.gef.figures.ChainFigure;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ChainEditPart extends AbstractGraphicalEditPart {

	private static Logger logger = Logger.getLogger(ChainEditPart.class
			.getName());
	
	/**
	 * Constructor.
	 * 
	 * @param chain the model element
	 */
	public ChainEditPart(Chain chain) {
		this.setModel(chain);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		ChainFigure figure = new ChainFigure();
		ConnectionLayer connLayer = (ConnectionLayer) getLayer(
				LayerConstants.CONNECTION_LAYER);
		connLayer.setConnectionRouter(new ShortestPathConnectionRouter(figure));
		return figure;
	}

	/**
	 * Returns the model element.
	 */
	public Chain getModel() {
		return (Chain)super.getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<ScanModule> getModelChildren() {
		return this.getModel().getScanModules();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
		logger.debug("createEditPolicies");
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ChainLayoutEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new ChainComponentEditPolicy());
	}
}