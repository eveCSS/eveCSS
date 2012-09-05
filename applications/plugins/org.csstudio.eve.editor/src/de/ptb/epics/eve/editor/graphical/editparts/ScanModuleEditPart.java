package de.ptb.epics.eve.editor.graphical.editparts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.CoordinateListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.editor.graphical.editparts.figures.ScanModuleFigure;

/**
 * <code>ScanModulEditPart</code> is the controller of the graphical editor 
 * linking its model ({@link ScanModule}) and view ({@link ScanModuleFigure}).
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class ScanModuleEditPart extends AbstractGraphicalEditPart {

	private ScanModuleFigureCoordinateListener scanModuleFigureCoordinateListener;

	/**
	 * TODO remove: Redmine #168
	 */
	public void removeYourSelf() {
		this.getFigure().removeCoordinateListener(scanModuleFigureCoordinateListener);
		
		final ScanModule scanModule = (ScanModule)this.getModel();
		if( scanModule.getAppended() != null ) {
			// append Scan Modul vorhanden, muß auch entfernt werden
			removeAppendedScanModul(scanModule);
		}
		if( scanModule.getNested() != null ) {
			// nested Scan Modul vorhandn, muß auch entfernt werden
			removeNestedScanModul(scanModule);
		}
		if( scanModule.getParent() != null ) {
			if( scanModule.getParent().getParentScanModule() != null ) {
				if( scanModule.getParent().getParentScanModule().getAppended() != null ) {
					if( scanModule.getParent().getParentScanModule().getAppended().getChildScanModule() == scanModule ) {
						scanModule.getParent().getParentScanModule().setAppended( null );
					}
				} 
				if( scanModule.getParent().getParentScanModule().getNested() != null ) {
					if( scanModule.getParent().getParentScanModule().getNested().getChildScanModule() == scanModule ) {
						scanModule.getParent().getParentScanModule().setNested( null );
					}
				} 
			} else if( scanModule.getParent().getParentEvent() != null ) {
				if( scanModule.getParent().getParentEvent().getConnector() != null) {
					scanModule.getParent().getParentEvent().setConnector( null );
				}
				scanModule.getParent().setParentEvent( null );
			}
			// Hier wird im Connector das parentScanModul auf 0 gesetzt.
			scanModule.getParent().setParentScanModul(null);
			getViewer().getEditPartRegistry().remove( scanModule.getParent() );
			// Connector wird gelöscht
			scanModule.setParent( null );
		}

		List<?> connections = this.getSourceConnections();
		Iterator<?> it = connections.iterator();
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
		
		scanModule.getChain().remove(scanModule);

		//this.getParent().refresh();

		// TODO: this.getFigure..., this.removeNotify und this.unregister
		// auskokmmentiert am 11.1.11. Funktionen scheinen nicht
		// gebraucht zu werden. Wofür sollten sie sein?

		// TODO: steht in der GEF Doku... =clean up
		// da aber GEF im Grunde nicht benutzt wird...
		// Redmine Bug #168
		
//		this.getFigure().getParent().remove( this.getFigure() );
		this.deactivate();
//		this.removeNotify();
//		this.unregister();
		this.setParent( null );
		
	}
	
	/*
	 * called by removeYourSelf()
	 */
	private void removeAppendedScanModul(ScanModule appendModul) {
		if (appendModul.getAppended().getChildScanModule().getAppended() != null) {
			removeAppendedScanModul(appendModul.getAppended().getChildScanModule());
		}
		if (appendModul.getAppended().getChildScanModule().getNested() != null) {
			removeNestedScanModul(appendModul.getAppended().getChildScanModule());
		}
		appendModul.getAppended().getChildScanModule().setParent(null);
		appendModul.getChain().remove(appendModul.getAppended().getChildScanModule());
		appendModul.getAppended().setChildScanModul(null);
		this.getViewer().getEditPartRegistry().remove(appendModul.getAppended());
		appendModul.setAppended(null);
	}

	/*
	 * called by removeYourSelf()
	 */
	private void removeNestedScanModul(ScanModule nestedModule) {
		// recursively remove any nested scan modules present
		if (nestedModule.getNested().getChildScanModule().getNested() != null) {
			removeNestedScanModul(nestedModule.getNested().getChildScanModule());
		}
		// recursively remove any appended scan modules present
		if (nestedModule.getNested().getChildScanModule().getAppended() != null) {
			removeAppendedScanModul(nestedModule.getNested().getChildScanModule());
		}
		
		nestedModule.getNested().getChildScanModule().setParent(null);
		nestedModule.getChain().remove(nestedModule.getNested().getChildScanModule());
		nestedModule.getNested().setChildScanModul(null);
		this.getViewer().getEditPartRegistry().remove(nestedModule.getNested());
		nestedModule.setNested(null);
	}
	
	// ************************************************************************
	// ********* Methods inherited from AbstractGraphicalEditPart *************
	// ************************************************************************
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		
		// get the scan module corresponding to this edit part
		final ScanModule scanModule = (ScanModule)this.getModel();

		// set the graphical text to the name of the scan module
		((ScanModuleFigure)this.figure).setText(scanModule.getName());

		// check if the scan module is still in a "safe" area, and if not move 
		// it into it
		if( (scanModule.getX() != this.figure.getBounds().x) || 
			 scanModule.getY() != this.figure.getBounds().y) {
				scanModule.setX(this.figure.getBounds().x);
				scanModule.setY(this.figure.getBounds().y);
		}
		
		((ScanModuleFigure)this.figure).
				setError(scanModule.getModelErrors().size() > 0);
		
		((ScanModuleFigure)this.figure).repaint();
		
		super.refresh();
	}
	
	// ************************************************************************
	// ***************************** Listeners ********************************
	// ************************************************************************
	class ScanModuleFigureCoordinateListener implements CoordinateListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void coordinateSystemChanged(IFigure figure) {
			logger.debug("ScanModuleFigure moved -> save (x,y) to model");
			((ScanModule)getModel()).setX(getFigure().getBounds().x);
			((ScanModule)getModel()).setY(getFigure().getBounds().y);
		}
	}
}