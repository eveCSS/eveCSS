package de.ptb.epics.eve.editor.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import de.ptb.epics.eve.editor.gef.editpolicies.ChainLayoutEditPolicy;
import de.ptb.epics.eve.editor.gef.figures.ChainFigure;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ChainEditPart extends AbstractGraphicalEditPart implements
		PropertyChangeListener {

	private static Logger logger = Logger.getLogger(ChainEditPart.class
			.getName());
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate() {
		this.getModel().addPropertyChangeListener(Chain.SCANMODULE_ADDED_PROP,
				this);
		this.getModel().addPropertyChangeListener(
				Chain.SCANMODULE_REMOVED_PROP, this);
		super.activate();
	}
	
	@Override
	public void deactivate() {
		this.getModel().removePropertyChangeListener(
				Chain.SCANMODULE_ADDED_PROP, this);
		this.getModel().removePropertyChangeListener(
				Chain.SCANMODULE_REMOVED_PROP, this);
		super.deactivate();
	}
	
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
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ChainLayoutEditPolicy());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(Chain.SCANMODULE_ADDED_PROP) ||
				e.getPropertyName().equals(Chain.SCANMODULE_REMOVED_PROP)) {
			this.refreshChildren();
			if (logger.isDebugEnabled()) {
				logger.debug("Property changed: " + e.getPropertyName());
			}
		}
	}
}