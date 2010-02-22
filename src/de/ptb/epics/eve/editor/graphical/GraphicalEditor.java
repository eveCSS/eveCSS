package de.ptb.epics.eve.editor.graphical;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.EditorPart;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.measuringstation.MeasuringStation;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoader;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionSaverToXMLusingXerces;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.graphical.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.EventEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.ScanModulEditPart;
import de.ptb.epics.eve.editor.views.ErrorView;
import de.ptb.epics.eve.editor.views.ScanModulView;



public class GraphicalEditor extends EditorPart implements IModelUpdateListener {

	private ScrollingGraphicalViewer viewer;
	private ScanDescription scanDescription;
	
	private EditPart selectedEditPart;
	private EditPart leftClickEditPart;
	
	private EditDomain editDomain = new EditDomain();
	
	private MenuItem addAppendedScanModulMenuItem;
	private MenuItem addNestedScanModulMenuItem;
	private MenuItem deleteScanModulMenuItem;
	private MenuItem renameScanModulMenuItem;
	
	private SelectionListener selectionListener;
	
	private boolean dirty;
	
	@Override
	public void doSave( final IProgressMonitor monitor ) {
		final FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput)this.getEditorInput();
		final File scanDescriptionFile = new File( fileStoreEditorInput.getURI() );
		
		
		try {
			final FileOutputStream os = new FileOutputStream( scanDescriptionFile );	
			final MeasuringStation measuringStation = Activator.getDefault().getMeasuringStation();
			final ScanDescriptionSaverToXMLusingXerces scanDescriptionSaver = new ScanDescriptionSaverToXMLusingXerces( os, measuringStation, this.scanDescription );
			scanDescriptionSaver.save();
			
			this.dirty = false;
			this.firePropertyChange( PROP_DIRTY );
			
		} catch( final FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void doSaveAs() {
		
		final FileDialog dialog = new FileDialog( this.getEditorSite().getShell(), SWT.SAVE );
		final String fileName = dialog.open();
		
		final File scanDescriptionFile = new File( fileName );
		
		try {
			final FileOutputStream os = new FileOutputStream( scanDescriptionFile );	
			final MeasuringStation measuringStation = Activator.getDefault().getMeasuringStation();
			final ScanDescriptionSaverToXMLusingXerces scanDescriptionSaver = new ScanDescriptionSaverToXMLusingXerces( os, measuringStation, this.scanDescription );
			scanDescriptionSaver.save();
			
			final IFileStore fileStore = EFS.getLocalFileSystem().getStore( new Path( fileName ) );
			final FileStoreEditorInput fileStoreEditorInput = new FileStoreEditorInput( fileStore );
			this.setInput( fileStoreEditorInput );
			
			this.dirty = false;
			this.firePropertyChange( PROP_DIRTY );
			
			this.setPartName( fileStoreEditorInput.getName() );
			
		} catch( final FileNotFoundException e ) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			this.scanDescription.addModelUpdateListener( this );
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
		this.dirty = false;
		this.firePropertyChange( PROP_DIRTY );
	}

	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	@Override
	public void createPartControl( final Composite parent ) {
	
		this.viewer = new ScrollingGraphicalViewer();
		this.viewer.createControl( parent );
		this.editDomain.addViewer( this.viewer );
		
		this.viewer.getControl().addMouseListener( new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				
				IViewReference[] ref = getSite().getPage().getViewReferences();
				ScanModulView view = null;
				for( int i = 0; i < ref.length; ++i ) {
					if( ref[i].getId().equals( ScanModulView.ID ) ) {
						view = (ScanModulView)ref[i].getPart( false );
					}
				}
				
				EditPart part = viewer.findObjectAt( new Point( e.x, e.y ) );
				if( selectedEditPart instanceof ScanModulEditPart ) {
					((ScanModulEditPart)selectedEditPart).setFocus( false );
				}
				selectedEditPart = part;
				if( selectedEditPart instanceof ScanModulEditPart ) {
					((ScanModulEditPart)selectedEditPart).setFocus( true );
					ScanModul scanModul = (ScanModul)selectedEditPart.getModel();
					view.setCurrentScanModul( scanModul );
				} else {
					view.setCurrentScanModul( null );
				}
			}

			public void mouseDown(MouseEvent e) {
				
				
				
			}

			public void mouseUp(MouseEvent e) {
			
				
					EditPart part = viewer.findObjectAt( new Point( e.x, e.y ) );
					part.refresh();
					if( part instanceof ScanModulEditPart ) {
						final ScanModul scanModul = (ScanModul)part.getModel();
						if( scanModul.getAppended() == null ) {
							addAppendedScanModulMenuItem.setEnabled( true );
						} else {
							addAppendedScanModulMenuItem.setEnabled( false );
						}
						if( scanModul.getNested() == null ) {
							addNestedScanModulMenuItem.setEnabled( true );
						} else {
							addNestedScanModulMenuItem.setEnabled( false );
						}
						deleteScanModulMenuItem.setEnabled( true );
						renameScanModulMenuItem.setEnabled( true );
					} else if( part instanceof EventEditPart ) {
						EventEditPart eventEditPart = (EventEditPart)part;
						if( ((StartEvent)eventEditPart.getModel()).getConnector() == null ) {
							addAppendedScanModulMenuItem.setEnabled( true );
						} else {
							addAppendedScanModulMenuItem.setEnabled( false );
						}
						addNestedScanModulMenuItem.setEnabled( false );
						deleteScanModulMenuItem.setEnabled( false );
					} else {
						addAppendedScanModulMenuItem.setEnabled( false );
						addNestedScanModulMenuItem.setEnabled( false );
						deleteScanModulMenuItem.setEnabled( false );
						renameScanModulMenuItem.setEnabled( false );
					}
					leftClickEditPart = part;
				}
			
		});
		
		this.configureGraphicalViewer();
	}

	protected void configureGraphicalViewer() {
		this.viewer.getControl().setBackground( ColorConstants.listBackground );
		((ScalableRootEditPart)this.viewer.getRootEditPart()).getLayer( ScalableRootEditPart.PRIMARY_LAYER ).setLayoutManager( new XYLayout() );
		this.viewer.setEditPartFactory( new GraphicalEditorEditPartFactory() );
		this.viewer.setContents( this.scanDescription );
		
		Menu menu = new Menu( this.viewer.getControl() );
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
		
		this.viewer.getControl().setMenu(menu);
		
	}
	
	@Override
	public void setFocus() {
		IViewReference[] ref = getSite().getPage().getViewReferences();
		ErrorView view = null;
		for( int i = 0; i < ref.length; ++i ) {
			if( ref[i].getId().equals( ErrorView.ID ) ) {
				view = (ErrorView)ref[i].getPart( false );
				if( view != null ) {
					view.setScanDescription( this.scanDescription );
				}
			}
		}
		
	}
	
	private void createSelectionListener() {
		this.selectionListener = new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				
				if( e.widget == addAppendedScanModulMenuItem ) {
					try {
					if( leftClickEditPart instanceof ScanModulEditPart ) {
						ScanModulEditPart scanModulEditPart = (ScanModulEditPart)leftClickEditPart;
						ScanModul scanModul = (ScanModul)leftClickEditPart.getModel();
						
						Chain[] chains = scanModul.getChain().getScanDescription().getChains().toArray( new Chain[0] );
						int newId = 1;
						do {
							boolean repeat = false;
							for( int i = 0; i < chains.length; ++i ) {
								ScanModul[] scanModules = chains[i].getScanModuls().toArray( new ScanModul[0] );
								for( int j = 0; j < scanModules.length; ++j ) {
									if( scanModules[j].getId() == newId ) {
										newId++;
										repeat = true;
									}
								}
								
							}
							if( !repeat )
								break;
						} while( true );
						ScanModul newScanModul = new ScanModul( newId );
						newScanModul.setName( "New Scan Modul" );
						newScanModul.setX( scanModul.getX() + 130 );
						newScanModul.setY( scanModul.getY() );
						Connector connector = new Connector();
						connector.setParentScanModul( scanModul );
						connector.setChildScanModul( newScanModul );
						scanModul.setAppended( connector );
						newScanModul.setParent( connector );
						scanModul.getChain().add( newScanModul );
						scanModulEditPart.refresh();
						scanModulEditPart.getParent().refresh();
					} else if( leftClickEditPart instanceof EventEditPart ) {
						EventEditPart eventEditPart = (EventEditPart)leftClickEditPart;
						StartEvent startEvent = (StartEvent)leftClickEditPart.getModel();
						Chain[] chains = ((ScanDescription)eventEditPart.getParent().getModel()).getChains().toArray( new Chain[0] );
						int newId = 1;
						do {
							boolean repeat = false;
							for( int i = 0; i < chains.length; ++i ) {
								ScanModul[] scanModules = chains[i].getScanModuls().toArray( new ScanModul[0] );
								for( int j = 0; j < scanModules.length; ++j ) {
									if( scanModules[j].getId() == newId ) {
										newId++;
										repeat = true;
									}
								}
								
							}
							if( !repeat )
								break;
						} while( true );
						ScanModul newScanModul = new ScanModul( newId );
						newScanModul.setName( "New Scan Modul" );
						Connector connector = new Connector();
						connector.setParentEvent( startEvent );
						connector.setChildScanModul( newScanModul );
						startEvent.setConnector( connector );
						newScanModul.setParent( connector );
						Iterator<Chain> it = scanDescription.getChains().iterator();
						while( it.hasNext() ) {
							Chain currentChain = it.next();
							if( currentChain.getStartEvent() == startEvent ) {
								currentChain.add( newScanModul );
								break;
							}
						}
						eventEditPart.refresh();
						eventEditPart.getParent().refresh();
						Iterator it2 = eventEditPart.getParent().getChildren().iterator();
						while( it2.hasNext() ) {
							EditPart editPart = (EditPart)it2.next();
							if( editPart instanceof ChainEditPart ) {
								ChainEditPart chainEditPart = (ChainEditPart)editPart;
								final Chain chain = (Chain)editPart.getModel();
								if( chain.getStartEvent() == startEvent ) {
									chainEditPart.refresh();
								}
							}
							
						}
					}
					
					} catch( Exception ex ) {
						ex.printStackTrace();
					}
				} else if( e.widget == addNestedScanModulMenuItem ) {
					ScanModulEditPart scanModulEditPart = (ScanModulEditPart)leftClickEditPart;
					ScanModul scanModul = (ScanModul)leftClickEditPart.getModel();
					Chain[] chains = scanModul.getChain().getScanDescription().getChains().toArray( new Chain[0] );
					int newId = 1;
					do {
						boolean repeat = false;
						for( int i = 0; i < chains.length; ++i ) {
							ScanModul[] scanModules = chains[i].getScanModuls().toArray( new ScanModul[0] );
							for( int j = 0; j < scanModules.length; ++j ) {
								if( scanModules[j].getId() == newId ) {
									newId++;
									repeat = true;
								}
							}
							
						}
						if( !repeat )
							break;
					} while( true );
					ScanModul newScanModul = new ScanModul( newId );
					newScanModul.setName( "New Scan Modul" );
					newScanModul.setX( scanModul.getX() + 130 );
					newScanModul.setY( scanModul.getY() + 100 );
					Connector connector = new Connector();
					connector.setParentScanModul( scanModul );
					connector.setChildScanModul( newScanModul );
					scanModul.setNested( connector );
					newScanModul.setParent( connector );
					scanModul.getChain().add( newScanModul );
					scanModulEditPart.refresh();
					scanModulEditPart.getParent().refresh();
					
				} else if( e.widget == deleteScanModulMenuItem ) {
					
					ScanModulEditPart scanModulEditPart = (ScanModulEditPart)leftClickEditPart;
					scanModulEditPart.removeYourSelf();
					Iterator< Chain > it = scanDescription.getChains().iterator();
					while( it.hasNext() ) {
						it.next().remove( (ScanModul)scanModulEditPart.getModel() );
					}
					
				} else if( e.widget == renameScanModulMenuItem ) {
					ScanModulEditPart scanModulEditPart = (ScanModulEditPart)leftClickEditPart;
					ScanModul scanModul = (ScanModul)leftClickEditPart.getModel();
					Shell shell = getSite().getShell();
					InputDialog dialog = new InputDialog( shell, "Renaming ScanModul:" + scanModul.getId(), "Please enter the new name for the Scan Modul.", scanModul.getName(), null );
					if( InputDialog.OK == dialog.open() ) {
						scanModul.setName( dialog.getValue() );
						scanModulEditPart.refresh();
						scanModulEditPart.getFigure().repaint();
					}
					
				}
				
			}
			
		};
	}

	private void appendSelectionListener() {
		
		this.addAppendedScanModulMenuItem.addSelectionListener( this.selectionListener );
		this.addNestedScanModulMenuItem.addSelectionListener( this.selectionListener );
		this.deleteScanModulMenuItem.addSelectionListener( this.selectionListener );
		this.renameScanModulMenuItem.addSelectionListener( this.selectionListener );
	}

	@Override
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		this.dirty = true;
		this.firePropertyChange( PROP_DIRTY );
		
	}

}
