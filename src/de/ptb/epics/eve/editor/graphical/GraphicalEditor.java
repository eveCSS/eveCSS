package de.ptb.epics.eve.editor.graphical;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.EditorPart;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.SaveAxisPositionsTypes;
import de.ptb.epics.eve.data.measuringstation.IMeasuringStation;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoader;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoaderHandler;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionSaverToXMLusingXerces;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.dialogs.LostDevicesDialog;
import de.ptb.epics.eve.editor.dialogs.PluginControllerDialog;
import de.ptb.epics.eve.editor.graphical.editparts.ChainEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.EventEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.ScanDescriptionEditPart;
import de.ptb.epics.eve.editor.graphical.editparts.ScanModulEditPart;
import de.ptb.epics.eve.editor.views.ErrorView;
import de.ptb.epics.eve.editor.views.ScanModulView;


public class GraphicalEditor extends EditorPart implements IModelUpdateListener {

	private ScrollingGraphicalViewer viewer;
	private ScanDescription scanDescription;
	
	private EditPart selectedEditPart;
	private EditPart rightClickEditPart;
	
	private EditDomain editDomain = new EditDomain();
	
	private MenuItem addAppendedScanModulMenuItem;
	private MenuItem addNestedScanModulMenuItem;
	private MenuItem deleteScanModulMenuItem;
	private MenuItem renameScanModulMenuItem;
	
	private SelectionListener selectionListener;
	
	private ScanModul currentScanModul = null;
	
	private boolean dirty;
	
	@Override
	public void doSave( final IProgressMonitor monitor ) {
		final FileStoreEditorInput fileStoreEditorInput = (FileStoreEditorInput)this.getEditorInput();
		final File scanDescriptionFile = new File( fileStoreEditorInput.getURI() );
		
		try {
			final FileOutputStream os = new FileOutputStream( scanDescriptionFile );	
			final IMeasuringStation measuringStation = Activator.getDefault().getMeasuringStation();
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
		// als filePath wird das Verzeichnis des aktuellen Scans gesetzt
		final FileStoreEditorInput fileStoreEditorInput2 = (FileStoreEditorInput)this.getEditorInput();
		
		int lastSeperatorIndex = fileStoreEditorInput2.getURI().getRawPath().lastIndexOf( "/" );
		final String filePath = fileStoreEditorInput2.getURI().getRawPath().substring( 0, lastSeperatorIndex + 1 );
		
		final FileDialog dialog = new FileDialog( this.getEditorSite().getShell(), SWT.SAVE );
		dialog.setFilterPath(filePath);
		final String fileName = dialog.open();

		String fileNameLang = fileName;
		
		if( fileName != null ) {
			// eventuel vorhandener Datentyp wird weggenommen
			final int lastPoint = fileName.lastIndexOf( "." );
			final int lastSep = fileName.lastIndexOf("/");
			
			if ((lastPoint > 0) && (lastPoint > lastSep))
				fileNameLang = fileName.substring(0, lastPoint) + ".scml";
			else
				fileNameLang = fileName + ".scml";
		}
		
		final File scanDescriptionFile = new File( fileNameLang );
		
		try {
			final FileOutputStream os = new FileOutputStream( scanDescriptionFile );	
			final IMeasuringStation measuringStation = Activator.getDefault().getMeasuringStation();
			final ScanDescriptionSaverToXMLusingXerces scanDescriptionSaver = new ScanDescriptionSaverToXMLusingXerces( os, measuringStation, this.scanDescription );
			scanDescriptionSaver.save();
			
			final IFileStore fileStore = EFS.getLocalFileSystem().getStore( new Path( fileNameLang ) );
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
		if (scanDescriptionFile.isFile() == true) {
			if (scanDescriptionFile.length() == 0) {
				// File ist vorhanden aber leer, es wird nicht eingelesen
				return;
			}
		}
		else {
			// File ist nicht vorhanden, es wird nicht eingelesen
			return;
		}
		
		final ScanDescriptionLoader scanDescriptionLoader = new ScanDescriptionLoader( Activator.getDefault().getMeasuringStation(), Activator.getDefault().getSchemaFile() );
		this.dirty = false;
		try {
			scanDescriptionLoader.load( scanDescriptionFile );
			this.scanDescription = scanDescriptionLoader.getScanDescription();

			if (scanDescriptionLoader.getLostDevices() != null) {
				Shell shell = getSite().getShell();
				LostDevicesDialog dialog = new LostDevicesDialog( shell, scanDescriptionLoader );
				dialog.open();
				this.dirty = true;
			}
			
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
		this.firePropertyChange( PROP_DIRTY );
		System.out.println("Hier muß jetzt das erste ScanModul aktiviert werden");
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

				// nur wenn ein Scan Modul selektiert ist, wird die Ansicht geändert
				if( part instanceof ScanModulEditPart ) {
					// Wenn schon ein SM angezeigt wird, wird der Focus weggenommen
					if( selectedEditPart instanceof ScanModulEditPart ) {
						((ScanModulEditPart)selectedEditPart).setFocus( false );
					}
					
					selectedEditPart = part;
					((ScanModulEditPart)selectedEditPart).setFocus( true );
					ScanModul scanModul = (ScanModul)selectedEditPart.getModel();
					view.setCurrentScanModul( scanModul );
					currentScanModul = scanModul;

				}
				else if( part instanceof ScanDescriptionEditPart ) {
					// Wenn schon ein SM angezeigt wird, wird der Focus weggenommen
					if( selectedEditPart instanceof ScanModulEditPart ) {
						((ScanModulEditPart)selectedEditPart).setFocus( false );
					}

					// als EditPart wird der zuletzt mit der rechten Maustaste angeklickte 
					// Part gesetzt
					selectedEditPart = rightClickEditPart;
					((ScanModulEditPart)selectedEditPart).setFocus( true );
					ScanModul scanModul = (ScanModul)selectedEditPart.getModel();
					view.setCurrentScanModul( scanModul );
					currentScanModul = scanModul;

				}
				else
					return;
			}

			public void mouseDown(MouseEvent e) {

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

				rightClickEditPart = part;
				
			}

			public void mouseUp(MouseEvent e) {
				mouseDoubleClick(e);
					
				}
		});
		
		this.configureGraphicalViewer();

	System.out.println("Initialisierung abgeschlossen, ist die Oberfläche jetzt schon da?");

	}

	protected void configureGraphicalViewer() {
		System.out.println("GraphicalEditor: configureGraphicalViewer aufgerufen");
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
		
		ScanModulView scanModulView = null;
		
		for( int i = 0; i < ref.length; ++i ) {
			if( ref[i].getId().equals( ScanModulView.ID ) ) {
				scanModulView = (ScanModulView)ref[i].getPart( false );
			}
		}
		if( scanModulView != null ) {
			scanModulView.setCurrentScanModul( currentScanModul );
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
					if( rightClickEditPart instanceof ScanModulEditPart ) {
						ScanModulEditPart scanModulEditPart = (ScanModulEditPart)rightClickEditPart;
						ScanModul scanModul = (ScanModul)rightClickEditPart.getModel();
						
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
						newScanModul.setName( "SM " + newId + " append" );
						newScanModul.setX( scanModul.getX() + 130 );
						newScanModul.setY( scanModul.getY() );
						// Voreinstellungen für das neue Scan Modul
						newScanModul.setTriggerdelay(0);
						newScanModul.setSettletime(0);
						newScanModul.setSaveAxisPositions(SaveAxisPositionsTypes.NEVER);
						
						Connector connector = new Connector();
						connector.setParentScanModul( scanModul );
						connector.setChildScanModul( newScanModul );
						scanModul.setAppended( connector );
						newScanModul.setParent( connector );

						scanModul.getChain().add( newScanModul );
						scanModulEditPart.refresh();
						scanModulEditPart.getParent().refresh();
						
					} else if( rightClickEditPart instanceof EventEditPart ) {
						EventEditPart eventEditPart = (EventEditPart)rightClickEditPart;
						StartEvent startEvent = (StartEvent)rightClickEditPart.getModel();
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
						newScanModul.setName( "SM " + newId );
						newScanModul.setX( 100 );
						newScanModul.setY( 20 );
						// Voreinstellungen für das neue Scan Modul
						newScanModul.setTriggerdelay(0);
						newScanModul.setSettletime(0);
						newScanModul.setSaveAxisPositions(SaveAxisPositionsTypes.NEVER);
						
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
						// Per Maus soll das erste Scan-Modul aktiviert werden!

						// Jetzt kommt alles das, was im mouseDoubleClick passiert!

						// gerade erzeugte ScanModul wird selektiert
						IViewReference[] ref = getSite().getPage().getViewReferences();
						ScanModulView view = null;
						for( int i = 0; i < ref.length; ++i ) {
							if( ref[i].getId().equals( ScanModulView.ID ) ) {
								view = (ScanModulView)ref[i].getPart( false );
							}
						}

						EditPart part = viewer.findObjectAt( new Point( 110, 30 ) );
						if( selectedEditPart instanceof ScanModulEditPart ) {
							((ScanModulEditPart)selectedEditPart).setFocus( false );
						}
						selectedEditPart = part;

						if( selectedEditPart instanceof ScanModulEditPart ) {
							((ScanModulEditPart)selectedEditPart).setFocus( true );
							ScanModul scanModul = (ScanModul)selectedEditPart.getModel();
							view.setCurrentScanModul( scanModul );
							currentScanModul = scanModul;
						} else {
							view.setCurrentScanModul( null );
							currentScanModul = null;
						}
					}
					
					} catch( Exception ex ) {
						ex.printStackTrace();
					}
				} else if( e.widget == addNestedScanModulMenuItem ) {
					ScanModulEditPart scanModulEditPart = (ScanModulEditPart)rightClickEditPart;
					ScanModul scanModul = (ScanModul)rightClickEditPart.getModel();
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
					newScanModul.setName( "SM " + newId + " nested" );
					newScanModul.setX( scanModul.getX() + 130 );
					newScanModul.setY( scanModul.getY() + 100 );
					// Voreinstellungen für das neue Scan Modul
					newScanModul.setTriggerdelay(0);
					newScanModul.setSettletime(0);
					newScanModul.setSaveAxisPositions(SaveAxisPositionsTypes.NEVER);
					
					Connector connector = new Connector();
					connector.setParentScanModul( scanModul );
					connector.setChildScanModul( newScanModul );
					scanModul.setNested( connector );
					newScanModul.setParent( connector );
					scanModul.getChain().add( newScanModul );
					scanModulEditPart.refresh();
					scanModulEditPart.getParent().refresh();
					
				} else if( e.widget == deleteScanModulMenuItem ) {
					ScanModulEditPart scanModulEditPart = (ScanModulEditPart)rightClickEditPart;

					// Wenn schon ein SM angezeigt wird, wird der Focus weggenommen
					if( selectedEditPart instanceof ScanModulEditPart ) {
						((ScanModulEditPart)selectedEditPart).setFocus( false );
					}

					// Parent wird als neues Scan Module angezeigt
					IViewReference[] ref = getSite().getPage().getViewReferences();
					ScanModulView view = null;
					for( int i = 0; i < ref.length; ++i ) {
						if( ref[i].getId().equals( ScanModulView.ID ) ) {
							view = (ScanModulView)ref[i].getPart( false );
						}
					}
					
					// scanModul bestimmen das den Focus bekommen soll
					EditPart newPart = null;
					ScanModul scanModul = (ScanModul)scanModulEditPart.getModel();
					ScanModul parentModul = scanModul.getParent().getParentScanModul();
					if (parentModul != null) {
						int x = parentModul.getX();
						int y = parentModul.getY();
						newPart = viewer.findObjectAt( new Point( x, y ) );
					}
					// scanModul mit angehängten Modulen wird entfernt
					scanModulEditPart.removeYourSelf();
					
					if (newPart != null) {
						// parent ScanModul bekommt den Focus
						selectedEditPart = newPart;
						((ScanModulEditPart)selectedEditPart).setFocus( true );
						view.setCurrentScanModul( parentModul );
						currentScanModul = parentModul;
					}
				} else if( e.widget == renameScanModulMenuItem ) {
					ScanModulEditPart scanModulEditPart = (ScanModulEditPart)rightClickEditPart;
					ScanModul scanModul = (ScanModul)rightClickEditPart.getModel();
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
