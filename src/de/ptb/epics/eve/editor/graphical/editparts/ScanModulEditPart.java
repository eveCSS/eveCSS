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

	private void removeAppendScanModul(ScanModul appendModul) {
		// append Scan Modul wird entfernt
		if (appendModul.getAppended().getChildScanModul().getAppended() != null) {
			removeAppendScanModul(appendModul.getAppended().getChildScanModul());
		}
		if (appendModul.getAppended().getChildScanModul().getNested() != null) {
			removeNestedScanModul(appendModul.getAppended().getChildScanModul());
		}
		appendModul.getAppended().getChildScanModul().setParent( null );
		appendModul.getChain().remove(appendModul.getAppended().getChildScanModul());
		appendModul.getAppended().setChildScanModul( null );
		this.getViewer().getEditPartRegistry().remove( appendModul.getAppended() );
		appendModul.setAppended( null );
	}

	private void removeNestedScanModul(ScanModul nestedModul) {
		// nested Scan Modul wird entfernt
		if (nestedModul.getNested().getChildScanModul().getNested() != null) {
			removeNestedScanModul(nestedModul.getNested().getChildScanModul());
		}
		if (nestedModul.getNested().getChildScanModul().getAppended() != null) {
			removeAppendScanModul(nestedModul.getNested().getChildScanModul());
		}
		nestedModul.getNested().getChildScanModul().setParent( null );
		nestedModul.getChain().remove(nestedModul.getNested().getChildScanModul());
		nestedModul.getNested().setChildScanModul( null );
		this.getViewer().getEditPartRegistry().remove( nestedModul.getNested() );
		nestedModul.setNested( null );
	}
	
	public void removeYourSelf() {
		final ScanModul scanModul = (ScanModul)this.getModel();
		if( scanModul.getAppended() != null ) {
			// append Scan Modul vorhanden, muß auch entfernt werden
			removeAppendScanModul(scanModul);
		}
		if( scanModul.getNested() != null ) {
			// nested Scan Modul vorhandn, muß auch entfernt werden
			removeNestedScanModul(scanModul);
		}
		if( scanModul.getParent() != null ) {
			if( scanModul.getParent().getParentScanModul() != null ) {
				if( scanModul.getParent().getParentScanModul().getAppended() != null ) {
					if( scanModul.getParent().getParentScanModul().getAppended().getChildScanModul() == scanModul ) {
						scanModul.getParent().getParentScanModul().setAppended( null );
					}
				} 
				if( scanModul.getParent().getParentScanModul().getNested() != null ) {
					if( scanModul.getParent().getParentScanModul().getNested().getChildScanModul() == scanModul ) {
						scanModul.getParent().getParentScanModul().setNested( null );
					}
				} 
			} else if( scanModul.getParent().getParentEvent() != null ) {
				if( scanModul.getParent().getParentEvent().getConnector() != null) {
					scanModul.getParent().getParentEvent().setConnector( null );
				}
				scanModul.getParent().setParentEvent( null );
			}
			// Hier wird im Connector das parentScanModul auf 0 gesetzt.
			scanModul.getParent().setParentScanModul( null );
			getViewer().getEditPartRegistry().remove( scanModul.getParent() );
			// Connector wird gelöscht
			scanModul.setParent( null );
		}

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

		scanModul.getChain().remove(scanModul);

		this.getParent().refresh();
		this.getFigure().getParent().remove( this.getFigure() );
		
		this.deactivate();
		this.removeNotify();
		this.unregister();
		this.setParent( null );
		
	}
	public void refresh() {
		final ScanModul scanModul = (ScanModul)this.getModel();
		((ScanModuleFigure)this.figure).setText( scanModul.getName() );
		if( (scanModul.getX() != this.figure.getBounds().x) || scanModul.getY() != this.figure.getBounds().y ) {
		    scanModul.setX( this.figure.getBounds().x );
			scanModul.setY( this.figure.getBounds().y );
		}
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
