package de.ptb.epics.eve.editor.gef.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.gef.figures.ScanModuleFigure;

/**
 * @author Marcus Michalsky
 * @since 1.6
 */
public class ScanModuleEditPart extends AbstractGraphicalEditPart implements
		NodeEditPart, PropertyChangeListener {

	private static Logger logger = Logger.getLogger(ScanModuleEditPart.class
			.getName());

	/**
	 * Constructor.
	 * 
	 * @param scanModule the model element
	 */
	public ScanModuleEditPart(ScanModule scanModule) {
		this.setModel(scanModule);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate() {
		this.getModel().addPropertyChangeListener("x", this);
		this.getModel().addPropertyChangeListener("y", this);
		super.activate();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate() {
		this.getModel().removePropertyChangeListener("x", this);
		this.getModel().removePropertyChangeListener("y", this);
		super.deactivate();
	}
	
	/**
	 * Returns the model element.
	 */
	public ScanModule getModel() {
		return (ScanModule)super.getModel();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IFigure createFigure() {
		return new ScanModuleFigure(this.getModel().getName(), this.getModel()
				.getX(), this.getModel().getY(), this.getModel().getWidth(),
				this.getModel().getHeight());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createEditPolicies() {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void fireSelectionChanged() {
		((ScanModuleFigure) this.getFigure())
				.setSelected(this.getSelected() == EditPart.SELECTED_PRIMARY);
		super.fireSelectionChanged();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refreshVisuals() {
		ScanModule sm = this.getModel();
		((ScanModuleFigure)this.getFigure()).setX(sm.getX());
		((ScanModuleFigure)this.getFigure()).setY(sm.getY());
		Rectangle bounds = new Rectangle(sm.getX(), sm.getY(), sm.getWidth(),
				sm.getHeight());
		((GraphicalEditPart) this.getParent()).setLayoutConstraint(this,
				this.getFigure(), bounds);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<?> getModelSourceConnections() {
		final List<Object> sourceList = new ArrayList<Object>();
		final ScanModule scanModule = (ScanModule)this.getModel();
		
		if(scanModule.getAppended() != null) {
			sourceList.add(scanModule.getAppended());
		}
		if(scanModule.getNested() != null) {
			sourceList.add(scanModule.getNested());
		}
		return sourceList;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<?> getModelTargetConnections() {
		final List<Object> sourceList = new ArrayList<Object>();
		final ScanModule scanModule = (ScanModule)this.getModel();
		if(scanModule.getParent() != null) {
			sourceList.add(scanModule.getParent());
		}
		return sourceList;
	}

	// ************************************************************************
	// ****************** Methods inherited from NodeEditPart *****************
	// ************************************************************************	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
										final ConnectionEditPart connection) {
		final ScanModule scanModule = (ScanModule)this.getModel();
		if(connection.getModel() == scanModule.getAppended()) {
			return ((ScanModuleFigure)this.figure).getAppendedAnchor();
		} else {
			return ((ScanModuleFigure)this.figure).getNestedAnchor();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(final Request request) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
										final ConnectionEditPart connection) {
		return ((ScanModuleFigure)this.getFigure()).getTargetAnchor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ConnectionAnchor getTargetConnectionAnchor(final Request request) {
		return ((ScanModuleFigure)this.getFigure()).getTargetAnchor();
	}

	/**
	 * {@inheritDoc}}
	 */
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals("x")) {
			((ScanModuleFigure)this.getFigure()).setX((Integer)e.getNewValue());
		} else if (e.getPropertyName().equals("y")) {
			((ScanModuleFigure)this.getFigure()).setY((Integer)e.getNewValue());
		}
		this.refreshVisuals();
	}
}