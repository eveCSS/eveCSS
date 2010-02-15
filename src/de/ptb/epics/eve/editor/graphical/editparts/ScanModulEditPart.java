package de.ptb.epics.eve.editor.graphical.editparts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FocusEvent;
import org.eclipse.draw2d.FocusListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.editor.graphical.editparts.figures.ScanModuleFigure;

public class ScanModulEditPart extends AbstractGraphicalEditPart implements NodeEditPart, FocusListener {

	public ScanModulEditPart( final ScanModul scanModul ) {
		this.setModel( scanModul );
	}
	
	
	@Override
	protected IFigure createFigure() {
		final ScanModul scanModul = (ScanModul)this.getModel();
		return new ScanModuleFigure( scanModul.getName(), scanModul.getX(), scanModul.getY() );
	}

	@Override
	protected void createEditPolicies() {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected List< ? > getModelSourceConnections() {
		final List< Object > sourceList = new ArrayList< Object >();
		final ScanModul scanModul = (ScanModul)this.getModel();
		
		if( scanModul.getAppended() != null ) {
			sourceList.add( scanModul.getAppended() );
		}
		if( scanModul.getNested() != null ) {
			sourceList.add( scanModul.getNested() );
		}
		return sourceList;
	}
	
	@Override
	protected List< ? > getModelTargetConnections() {
		final List< Object > sourceList = new ArrayList< Object >();
		final ScanModul scanModul = (ScanModul)this.getModel();
		if( scanModul.getParent() != null ) {
			sourceList.add( scanModul.getParent() );
		}
		return sourceList;
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor( final ConnectionEditPart connection ) {
		final ScanModul scanModul = (ScanModul)this.getModel();
		if( connection.getModel() == scanModul.getAppended() ) {
			return ((ScanModuleFigure)this.figure).getAppendedAnchor();
		} else {
			return ((ScanModuleFigure)this.figure).getNestedAnchor();
		}
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor( final Request request ) {
		return new ChopboxAnchor( this.getFigure() );
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor( final ConnectionEditPart connection ) {
		return ((ScanModuleFigure)this.figure).getTargetAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor( final Request request ) {
		return new ChopboxAnchor( this.getFigure() );
	}

	public void removeYourSelf() {
		final ScanModul scanModul = (ScanModul)this.getModel();
		if( scanModul.getAppended() != null ) {
			scanModul.getAppended().getChildScanModul().setParent( null );
			scanModul.getAppended().setChildScanModul( null );
			this.getViewer().getEditPartRegistry().remove( scanModul.getAppended() );
			scanModul.setAppended( null );
		}
		if( scanModul.getNested() != null ) {
			scanModul.getNested().getChildScanModul().setParent( null );
			scanModul.getNested().setChildScanModul( null );
			getViewer().getEditPartRegistry().remove( scanModul.getNested() );
			scanModul.setNested( null );
		}
		if( scanModul.getParent() != null ) {
			if( scanModul.getParent().getParentScanModul() != null ) {
				if( scanModul.getParent().getParentScanModul().getAppended().getChildScanModul() == scanModul ) {
					scanModul.getParent().getParentScanModul().setAppended( null );
				} else {
					scanModul.getParent().getParentScanModul().setNested( null );
				}
			} else if( scanModul.getParent().getParentEvent() != null ) {
				if( scanModul.getParent().getParentEvent().getConnector() != null) {
					scanModul.getParent().getParentEvent().setConnector( null );
				}
				scanModul.getParent().setParentEvent( null );
			}
			scanModul.getParent().setParentScanModul( null );
			getViewer().getEditPartRegistry().remove( scanModul.getParent() );
			scanModul.setParent( null );
		}
		this.getParent().refresh();
		this.getFigure().getParent().remove( this.getFigure() );
		List connections = this.getSourceConnections();
		Iterator it = connections.iterator();
		while( it.hasNext() ) {
			ConnectionEditPart connectionEditPart = (ConnectionEditPart)it.next();
			connectionEditPart.setSource( null );
			connectionEditPart.setTarget( null );
		}
		connections = this.getTargetConnections();
		it = connections.iterator();
		while( it.hasNext() ) {
			ConnectionEditPart connectionEditPart = (ConnectionEditPart)it.next();
			connectionEditPart.setSource( null );
			connectionEditPart.setTarget( null );
		}
		//this.getViewer().getEditPartRegistry().remove( "" );
		
		this.deactivate();
		this.removeNotify();
		this.setParent( null );
		this.unregister();
		
	}
	public void refresh() {
		final ScanModul scanModul = (ScanModul)this.getModel();
		((ScanModuleFigure)this.figure).setText( scanModul.getName() );
		scanModul.setX( this.figure.getBounds().x );
		scanModul.setY( this.figure.getBounds().y );
		super.refresh();
	}


	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setFocus( final boolean focus ) {
		super.setFocus( true );
		((ScanModuleFigure)this.figure).setActive( focus );	
	}
}
