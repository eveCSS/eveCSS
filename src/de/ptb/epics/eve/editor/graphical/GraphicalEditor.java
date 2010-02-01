package de.ptb.epics.eve.editor.graphical;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.EditorPart;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoader;
import de.ptb.epics.eve.editor.Activator;



public class GraphicalEditor extends EditorPart {

	private ScrollingGraphicalViewer viewer;
	private ScanDescription scanDescription;
	
	@Override
	public void doSave( final IProgressMonitor monitor ) {
		
		
	}

	@Override
	public void doSaveAs() {
		
		
	}

	@Override
	public void init( final IEditorSite site, final IEditorInput input) throws PartInitException {
		this.setSite( site );
		this.setInput( input );
		this.setPartName( input.getName() );
		final FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput)input;
		final File scanDescriptionFile = new File( fileStoreEditorInput.getURI() );
		final ScanDescriptionLoader scanDescriptionLoader = new ScanDescriptionLoader();
		scanDescriptionLoader.setMeasuringStation( Activator.getDefault().getMeasuringStation() );
		try {
			scanDescriptionLoader.load( scanDescriptionFile );
			this.scanDescription = scanDescriptionLoader.getScanDescription();
		} catch( final ParserConfigurationException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch( final SAXException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch( final IOException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl( final Composite parent ) {
	
		this.viewer = new ScrollingGraphicalViewer();
		this.viewer.createControl( parent );
		//this.editDomain.addViewer( this.viewer );
		
		this.configureGraphicalViewer();
	}

	protected void configureGraphicalViewer() {
		this.viewer.getControl().setBackground( ColorConstants.listBackground );
		((ScalableRootEditPart)this.viewer.getRootEditPart()).getLayer( ScalableRootEditPart.PRIMARY_LAYER ).setLayoutManager( new XYLayout() );
		this.viewer.setEditPartFactory( new GraphicalEditorEditPartFactory() );
		this.viewer.setContents( this.scanDescription );
		
		/*Menu menu = new Menu( this.viewer.getControl() );
		this.addAppendedScanModulMenuItem = new MenuItem( menu, SWT.NONE );
		this.addAppendedScanModulMenuItem.setText( "Add appended Scan Modul" );
		this.addNestedScanModulMenuItem = new MenuItem( menu, SWT.NONE );
		this.addNestedScanModulMenuItem.setText( "Add nested Scan Modul" );
		this.deleteScanModulMenuItem = new MenuItem( menu, SWT.None );
		this.deleteScanModulMenuItem.setText( "Delete" );
		this.renameScanModulMenuItem = new MenuItem( menu, SWT.None );
		this.renameScanModulMenuItem.setText( "Rename" );
		this.createSelectionListener();
		this.appendSelectionListener();
		
		this.viewer.getControl().setMenu(menu);*/
	}
	
	@Override
	public void setFocus() {
		
		
	}

}
