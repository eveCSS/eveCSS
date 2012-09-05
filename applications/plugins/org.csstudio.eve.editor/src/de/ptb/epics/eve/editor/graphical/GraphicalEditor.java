package de.ptb.epics.eve.editor.graphical;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.progress.UIJob;
import org.xml.sax.SAXException;

import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.Connector;
import de.ptb.epics.eve.data.scandescription.ScanDescription;
import de.ptb.epics.eve.data.scandescription.ScanModule;
import de.ptb.epics.eve.data.scandescription.StartEvent;
import de.ptb.epics.eve.data.scandescription.processors.ScanDescriptionLoader;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.dialogs.lostdevices.LostDevicesDialog;
import de.ptb.epics.eve.editor.graphical.editparts.ScanModuleEditPart;
import de.ptb.epics.eve.editor.jobs.file.Save;
import de.ptb.epics.eve.editor.jobs.filloptions.RemoveAllDevices;
import de.ptb.epics.eve.editor.jobs.filloptions.SaveAllChannelValues;
import de.ptb.epics.eve.editor.jobs.filloptions.SaveAllAxisPositions;

/**
 * <code>GraphicalEditor</code> is the central element of the EveEditor Plug In.
 * It allows creating and editing of scan descriptions.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class GraphicalEditor extends EditorPart implements IModelUpdateListener {

	// logging
	private static Logger logger = 
			Logger.getLogger(GraphicalEditor.class.getName());

	// a graphical view of the model (hosts the figures) (Draw2d FigureCanvas)
	private ScrollingGraphicalViewer viewer;
	
	// the currently loaded scan description
	private ScanDescription scanDescription;
	
	/*
	 * reminder of the currently selected scan module
	 * decides what is shown in the scan module view
	 */
	private ScanModule selectedScanModule = null;
	
	/*
	 * reminder of the currently selected edit part
	 * if it is a scan module, it is selected (colored)
	 */
	private EditPart selectedEditPart;
	
	/*
	 * reminder of the recently right-clicked edit part
	 * used by the actions of the context menu
	 */
	private EditPart rightClickEditPart;
	
	private EditDomain editDomain = new EditDomain();
	
	// the context menu of the editor (right-click)
	private Menu menu;
	
	// context menu actions to add appended/nested, delete or rename modules
	private MenuItem addAppendedScanModulMenuItem;
	private MenuItem addNestedScanModulMenuItem;
	private MenuItem deleteScanModulMenuItem;
	private MenuItem renameScanModulMenuItem;
	private MenuItem separator;
	private MenuItem fillMenuItem;
	private Menu fillMenu;
	private MenuItem saveAllAxisPositionsMenuItem;
	private MenuItem saveAllDetectorValuesMenuItem;
	private MenuItem removeAllMenuItem;
	
	// indicates whether the editor has unsaved changes
	private boolean dirty;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		logger.debug("create part control");
		
		this.viewer = new ScrollingGraphicalViewer();
		this.viewer.createControl(parent);
		this.editDomain.addViewer(this.viewer);

		this.viewer.getControl().addMouseListener(new ViewerMouseListener());

		// configure GraphicalViewer
		this.viewer.getControl().setBackground(ColorConstants.listBackground);
		
		((ScalableRootEditPart)this.viewer.getRootEditPart()).
				getLayer(ScalableRootEditPart.PRIMARY_LAYER).
				setLayoutManager(new XYLayout());
		
		// this.viewer.setEditPartFactory(new GraphicalEditorEditPartFactory());
		
		this.viewer.setContents(this.scanDescription);
		
		menu = createContextMenu();
		
		getSite().setSelectionProvider(viewer);
		
		updateViews();
	}

	/*
	 * 
	 */
	private Menu createContextMenu() {
		menu = new Menu(this.viewer.getControl());
		this.addAppendedScanModulMenuItem = new MenuItem(menu, SWT.NONE);
		this.addAppendedScanModulMenuItem.setText("Add appended Scan Module");
		this.addAppendedScanModulMenuItem.addSelectionListener(
				new AddAppendedScanModuleMenuItemSelectionListener());
		this.addNestedScanModulMenuItem = new MenuItem(menu, SWT.NONE);
		this.addNestedScanModulMenuItem.setText("Add nested Scan Module");
		this.addNestedScanModulMenuItem.addSelectionListener(
				new AddNestedScanModuleMenuItemSelectionListener());
		this.deleteScanModulMenuItem = new MenuItem(menu, SWT.NONE);
		this.deleteScanModulMenuItem.setText("Delete Scan Module");
		this.deleteScanModulMenuItem.addSelectionListener(
				new DeleteScanModuleMenuItemSelectionListener());
		this.renameScanModulMenuItem = new MenuItem(menu, SWT.NONE);
		this.renameScanModulMenuItem.setText("Rename Scan Module");
		this.renameScanModulMenuItem.addSelectionListener(
				new RenameScanModuleMenuItemSelectionListener());
		
		this.separator = new MenuItem(menu, SWT.SEPARATOR);
		this.separator.setText("");
		this.fillMenu = new Menu(this.menu);
		this.fillMenuItem = new MenuItem(menu, SWT.CASCADE);
		this.fillMenuItem.setText("Fill Options");
		this.fillMenuItem.setMenu(fillMenu);
		
		this.saveAllAxisPositionsMenuItem = new MenuItem(fillMenu, SWT.NONE);
		this.saveAllAxisPositionsMenuItem.setText("Save all Axis Positions");
		this.saveAllAxisPositionsMenuItem.addSelectionListener(
				new SaveAllAxisPositionsSelectionListener());
		this.saveAllDetectorValuesMenuItem = new MenuItem(fillMenu, SWT.NONE);
		this.saveAllDetectorValuesMenuItem.setText("Save all Channel Values");
		this.saveAllDetectorValuesMenuItem.addSelectionListener(
				new SaveAllChannelValuesSelectionListener());
		this.removeAllMenuItem = new MenuItem(fillMenu, SWT.NONE);
		this.removeAllMenuItem.setText("Clear All");
		this.removeAllMenuItem.addSelectionListener(
				new RemoveAllSelectionListener());
		
		this.viewer.getControl().setMenu(menu);
		
		return menu;
	}
	
	/*
	 * 
	 */
	private void updateScanModuleView() {
		refreshAllEditParts(viewer.getRootEditPart());
		logger.debug("selectedScanModule: " + selectedScanModule);
	}
	
	/*
	 * wrapper to update all views
	 */
	private void updateViews() {
		updateScanModuleView();
	}
	
	/*
	 * used to select a scan module (and deselect the old one) by 
	 * updating all necessary references
	 * 
	 * @param part the corresponding edit part of the scan module that should
	 * 		  be selected
	 */
	private void selectScanModule(ScanModuleEditPart part) {
		// if a scan module was previously selected -> deselect it
		if(selectedEditPart instanceof ScanModuleEditPart) {
			((ScanModuleEditPart)selectedEditPart).setFocus(false);
		}
		
		if(part != null) {
			// remember the selected scan module
			selectedEditPart = part;
			
			// update the model to the currently selected module 
			selectedScanModule = (ScanModule)selectedEditPart.getModel();
			
			// set the focus (to select/color it)
			((ScanModuleEditPart)selectedEditPart).setFocus(true);
		} else {
			// reset selection
			selectedEditPart = null;
			// reset model
			selectedScanModule = null;
		}
		
		// tell the views about the changes
		updateViews();
	}
	
	/**
	 * 
	 * @return
	 */
	public ScanDescription getContent() {
		return this.scanDescription;
	}
	
	// ***********************************************************************
	// ************* methods inherited from IModelUpdateListener *************
	// ***********************************************************************
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEvent(final ModelUpdateEvent modelUpdateEvent) {
		logger.debug("update event");
		this.dirty = true;
		this.firePropertyChange(PROP_DIRTY);
		
		refreshAllEditParts(viewer.getRootEditPart());
	}

	@SuppressWarnings("unchecked")
	private void refreshAllEditParts(EditPart part) {
		scanDescription.removeModelUpdateListener(this);
		
		part.refresh();
		List<EditPart> children = part.getChildren();
		for (EditPart child : children) {
			refreshAllEditParts(child);
		}
		
		scanDescription.addModelUpdateListener(this);
	}
	
	// ***********************************************************************
	// ************ methods inherited from IWorkbenchPart ********************
	// ***********************************************************************
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFocus() {
		logger.debug("Focus gained");
		
		refreshAllEditParts(viewer.getRootEditPart());
		
		updateViews();
	}
	
	// ***********************************************************************
	// ************ methods inherited from EditorPart ************************
	// ***********************************************************************
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final IEditorSite site, final IEditorInput input) 
													throws PartInitException {
		logger.debug("Init");
		
		this.selectedScanModule = null;
		
		this.setSite(site);
		this.setInput(input);
		this.setPartName(input.getName());
		
		final FileStoreEditorInput fileStoreEditorInput = 
										(FileStoreEditorInput)input;
		final File scanDescriptionFile = new File(fileStoreEditorInput.getURI());
		
		if (scanDescriptionFile.isFile() == true) {
			if (scanDescriptionFile.length() == 0) {
				// file exists but is empty -> do not read
				return;
			}
		} else {
			// file does not exist -> do not read
			return;
		}
		
		final ScanDescriptionLoader scanDescriptionLoader = 
				new ScanDescriptionLoader(Activator.getDefault().
													getMeasuringStation(), 
										  Activator.getDefault().
										  			getSchemaFile());
		this.dirty = false;
		try {
			scanDescriptionLoader.load(scanDescriptionFile);
			this.scanDescription = scanDescriptionLoader.getScanDescription();

			if (scanDescriptionLoader.getLostDevices() != null) {
				Shell shell = getSite().getShell();
				LostDevicesDialog dialog = 
						new LostDevicesDialog(shell, scanDescriptionLoader);
				dialog.open();
				this.dirty = true;
			}
			
			this.scanDescription.addModelUpdateListener(this);
		} catch(final ParserConfigurationException e) {
			logger.error(e.getMessage(), e);
		} catch(final SAXException e) {
			logger.error(e.getMessage(), e);
		} catch(final IOException e) {
			logger.error(e.getMessage(), e);
		}
		this.firePropertyChange(PROP_DIRTY);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSave(final IProgressMonitor monitor) {
		if(!isSaveAllowed()) {
			return;
		}
		try {
			monitor.beginTask("Saving Scan Description", 3);
			monitor.subTask("determine file name");
			String saveFileName = ((FileStoreEditorInput)this.getEditorInput()).
				getURI().getPath().toString();
			monitor.worked(1);
			monitor.subTask("creating save routine");
			Job saveJob = new Save("Save", saveFileName, this.scanDescription, this);
			monitor.worked(1);
			monitor.subTask("running save routine");
			saveJob.setUser(true);
			saveJob.schedule();
			monitor.worked(1);
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSaveAs() {
		if(!isSaveAllowed()) {
			return;
		}
		// show dialog preconfigured with path of "last" file name
		final FileDialog fileDialog = 
				new FileDialog(this.getEditorSite().getShell(), SWT.SAVE);
		fileDialog.setFilterPath(new File(((FileStoreEditorInput)
				this.getEditorInput()).getURI()).getParent());
		final String dialogName = fileDialog.open();
		
		// file path where the file will be saved
		String saveFileName;
		
		// check dialog result
		if(dialogName != null) {
			// adjust filename to ".scml"
			final int lastPoint = dialogName.lastIndexOf(".");
			final int lastSep = dialogName.lastIndexOf("/");
			if ((lastPoint > 0) && (lastPoint > lastSep)) {
				saveFileName = dialogName.substring(0, lastPoint) + ".scml";
			} else {
				saveFileName = dialogName + ".scml";
			}
		} else {
			// user pressed "cancel" -> do nothing
			return;
		}
		
		// create the save job
		Job saveJob = new Save(
				"Save As", saveFileName, this.scanDescription, this);
		
		// run the save job with a progress dialog
		IProgressService service = (IProgressService) getSite().getService(
				IProgressService.class);
		service.showInDialog(getSite().getShell(), saveJob);
		saveJob.setUser(true);
		saveJob.schedule();
	}
	
	/**
	 * Called by the save as job to update the input.
	 * 
	 * @param filename the new file name
	 */
	public void resetEditorState(String filename) {
		logger.debug("reset editor state");
		
		final IFileStore fileStore = 
			EFS.getLocalFileSystem().getStore(new Path(filename));
		final FileStoreEditorInput fileStoreEditorInput = 
			new FileStoreEditorInput(fileStore);
		this.setInput(fileStoreEditorInput);
		this.firePropertyChange(PROP_INPUT);
		
		this.setPartName(fileStoreEditorInput.getName());
		this.firePropertyChange(PROP_TITLE);
		
		this.dirty = false;
		this.firePropertyChange(PROP_DIRTY);
	}

	/*
	 * helper for save and save as to determine whether saving is allowed.
	 * returns true if saving is allowed, false otherwise
	 */
	private boolean isSaveAllowed() {
		// check for errors in the model
		// if present -> inform the user
		if(scanDescription.getModelErrors().size() > 0)	{
			MessageDialog.openError(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
				"Save Error", 
				"Scandescription could not be saved! " +
				"Consult the Error View for more Information.");
			return false;
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDirty() {
		return this.dirty;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSaveOnCloseNeeded() {
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		getSite().setSelectionProvider(null);
		super.dispose();
	}
	
	// ***********************************************************************
	// ************************* Listener ************************************
	// ***********************************************************************
	
	/**
	 * <code>MouseListener</code> of viewer.
	 */
	private class ViewerMouseListener implements MouseListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}

		/**
		 * {@inheritDoc}<br><br>
		 * Updates available context menu entries depending on where the user 
		 * (right-)clicked.
		 */
		@Override
		public void mouseDown(MouseEvent e) {

			if(e.button == 1) return;
			
			// get the object (part) the user clicked on
			EditPart part = viewer.findObjectAt(new Point(e.x, e.y));
			
			//part.refresh();
			
			// check on what object the user clicked at
			
			if(part instanceof ScanModuleEditPart) {
				
				// user clicked on a scan module
				final ScanModule scanModule = (ScanModule)part.getModel();
				// enable/disable context menu entries depending on the 
				// selected scan module
				if(scanModule.getAppended() == null) {
					// no appended module present -> add appended allowed
					addAppendedScanModulMenuItem.setEnabled(true);
				} else {
					// appended already present -> add appended not allowed
					addAppendedScanModulMenuItem.setEnabled(false);
				}
				if(scanModule.getNested() == null) {
					// no nested scan module present -> add nested allowed
					addNestedScanModulMenuItem.setEnabled(true);
				} else {
					// nested already present -> add nested not allowed
					addNestedScanModulMenuItem.setEnabled(false);
				}
				// delete and rename is always allowed
				deleteScanModulMenuItem.setEnabled(true);
				renameScanModulMenuItem.setEnabled(true);
				
				fillMenuItem.setEnabled(true);
			} else if(part instanceof EventEditPart) {
				
				// user clicked on an event
				EventEditPart eventEditPart = (EventEditPart)part;
				if(((StartEvent)eventEditPart.getModel()).getConnector() == null) {
					// no appended module present -> add appended allowed
					addAppendedScanModulMenuItem.setEnabled(true);
				} else {
					// appended already present -> add appended not allowed
					addAppendedScanModulMenuItem.setEnabled(false);
				}
				// add nested and delete module never allowed for events
				addNestedScanModulMenuItem.setEnabled(false);
				deleteScanModulMenuItem.setEnabled(false);
			} else {
				
				// user clicked anywhere else -> disable all actions
				addAppendedScanModulMenuItem.setEnabled(false);
				addNestedScanModulMenuItem.setEnabled(false);
				deleteScanModulMenuItem.setEnabled(false);
				renameScanModulMenuItem.setEnabled(false);
				fillMenuItem.setEnabled(false);
			}
			
			// save the edit part the user recently (right-)clicked on
			rightClickEditPart = part;
		}

		/**
		 * {@inheritDoc}<br><br>
		 * Updates the coloring depending on the selected scan module and 
		 * tells the scan module view about it.
		 */
		@Override
		public void mouseUp(MouseEvent e) {
			
			logger.debug("Mouse " + e.button);
			
			if(e.button == 2) return;
			
			// find the object the user clicked on
			EditPart part = viewer.findObjectAt(new Point(e.x, e.y));

			if(part instanceof ScanModuleEditPart) {
				// user clicked on a scan module
				
				selectScanModule((ScanModuleEditPart)part);
			} else {
				// user clicked anywhere else -> deselect scan module
				selectScanModule(null);
			}
		}
	}
	
	// ********************* MenuItem Listener...
	
	/**
	 * <code>SelectionListener</code> of 
	 * <code>addAppendedScanModulMenuItem</code>.
	 */
	private class AddAppendedScanModuleMenuItemSelectionListener 
												implements SelectionListener {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		@Override
		public void widgetSelected(SelectionEvent e) {
			try {
				if(rightClickEditPart instanceof ScanModuleEditPart) {
					// get the edit part the user right-clicked on
					// (before choosing add nested scan module)
					ScanModuleEditPart scanModuleEditPart = 
							(ScanModuleEditPart)rightClickEditPart;
					// get the model of the edit part the user clicked on
					ScanModule scanModule = 
							(ScanModule)rightClickEditPart.getModel();
					
					// find the next free id which can be used for the new module
					
					// create a new array of all available chains
					Chain[] chains = scanModule.getChain().getScanDescription().
											getChains().toArray(new Chain[0]);
					
					int newId = 1;
					
					do {
						boolean repeat = false;
						for(int i = 0; i < chains.length; ++i) {
							ScanModule[] scanModules = chains[i].getScanModules().
													  toArray(new ScanModule[0]);
							for(int j = 0; j < scanModules.length; ++j) {
								if(scanModules[j].getId() == newId) {
									newId++;
									repeat = true;
								}
							}	
						}
						if(!repeat)
							break;
					} while(true);
					// end of: find new id
					
					ScanModule newScanModule = new ScanModule(newId);
					newScanModule.setName("SM " + newId + " append");
					newScanModule.setX(scanModule.getX() + 130);
					newScanModule.setY(scanModule.getY());
					// Voreinstellungen für das neue Scan Modul
					newScanModule.setTriggerdelay(0);
					newScanModule.setSettletime(0);
					// TODO Constructor should suffice !!!
					// no additional setXYZ calls should be required
					
					Connector connector = new Connector();
					connector.setParentScanModul(scanModule);
					connector.setChildScanModul(newScanModule);
					scanModule.setAppended(connector);
					newScanModule.setParent(connector);

					scanModule.getChain().add(newScanModule);
					
					scanModuleEditPart.refresh();
					scanModuleEditPart.getParent().refresh();
					
					// select the newly created module
					EditPart part = viewer.findObjectAt(
							new Point(newScanModule.getX()+2, 
									  newScanModule.getY()+2));
					if(part instanceof ScanModuleEditPart)
						selectScanModule((ScanModuleEditPart)part);
					
				} else if(rightClickEditPart instanceof EventEditPart) {
					EventEditPart eventEditPart = 
							(EventEditPart)rightClickEditPart;
					StartEvent startEvent = 
							(StartEvent)rightClickEditPart.getModel();
					Chain[] chains = ((ScanDescription)eventEditPart.getParent().
							getModel()).getChains().toArray(new Chain[0]);
					int newId = 1;
					
					do {
						boolean repeat = false;
						for(int i = 0; i < chains.length; ++i) {
							ScanModule[] scanModules = chains[i].getScanModules().
													  toArray(new ScanModule[0]);
							for(int j = 0; j < scanModules.length; ++j) {
								if(scanModules[j].getId() == newId) {
									newId++;
									repeat = true;
								}
							}
						}
						if(!repeat)
							break;
					} while(true);
					
					ScanModule newScanModule = new ScanModule(newId);
					newScanModule.setName("SM " + newId);
					newScanModule.setX(100);
					newScanModule.setY(20);
					// Voreinstellungen für das neue Scan Modul
					newScanModule.setTriggerdelay(0);
					newScanModule.setSettletime(0);

					Connector connector = new Connector();
					connector.setParentEvent(startEvent);
					connector.setChildScanModul(newScanModule);
					startEvent.setConnector(connector);
					newScanModule.setParent(connector);
					Iterator<Chain> it = scanDescription.getChains().iterator();
					while(it.hasNext()) {
						Chain currentChain = it.next();
						if(currentChain.getStartEvent() == startEvent) {
						   currentChain.add(newScanModule);
							break;
						}
					}
					eventEditPart.refresh();
					eventEditPart.getParent().refresh();
					Iterator<EditPart> it2 = eventEditPart.getParent().getChildren().iterator();
					while(it2.hasNext()) {
						EditPart editPart = (EditPart)it2.next();
						if(editPart instanceof ChainEditPart) {
							ChainEditPart chainEditPart = (ChainEditPart)editPart;
							final Chain chain = (Chain)editPart.getModel();
							if(chain.getStartEvent() == startEvent) {
							   chainEditPart.refresh();
							}
						}
					}
					
					// select the newly created module
					EditPart part = viewer.findObjectAt(
							new Point(newScanModule.getX()+2, 
									  newScanModule.getY()+2));
					if(part instanceof ScanModuleEditPart)
						selectScanModule((ScanModuleEditPart)part);
				}
			} catch(Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
	}
	
	/**
	 * <code>SelectionListener</code> of <code>addNestedScanModulMenuItem</code>.
	 */
	private class AddNestedScanModuleMenuItemSelectionListener implements SelectionListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			ScanModuleEditPart scanModuleEditPart = 
					(ScanModuleEditPart)rightClickEditPart;
			ScanModule scanModule = (ScanModule)rightClickEditPart.getModel();
			Chain[] chains = scanModule.getChain().getScanDescription().
					getChains().toArray(new Chain[0]);
			
			// get the next available id
			int newId = 1;
			do {
				boolean repeat = false;
				
				for(int i = 0; i < chains.length; ++i) {
					ScanModule[] scanModules = 
						chains[i].getScanModules().toArray(new ScanModule[0]);
					for(int j=0; j<scanModules.length; ++j) {
						if(scanModules[j].getId() == newId) {
							newId++;
							repeat = true;
						}
					}
				}
				if(!repeat)
					break;
			} while(true);
			// end of: get free id
			
			ScanModule newScanModule = new ScanModule(newId);
			newScanModule.setName("SM " + newId + " nested");
			newScanModule.setX(scanModule.getX() + 130);
			newScanModule.setY(scanModule.getY() + 100);
			// Voreinstellungen für das neue Scan Modul
			newScanModule.setTriggerdelay(0);
			newScanModule.setSettletime(0);

			Connector connector = new Connector();
			connector.setParentScanModul(scanModule);
			connector.setChildScanModul(newScanModule);
			scanModule.setNested(connector);
			newScanModule.setParent(connector);
			
			scanModule.getChain().add(newScanModule);
			
			scanModuleEditPart.refresh();
			scanModuleEditPart.getParent().refresh();
			
			// select the newly created module
			EditPart part = viewer.findObjectAt(
					new Point(newScanModule.getX()+2, newScanModule.getY()+2));
			if(part instanceof ScanModuleEditPart)
				selectScanModule((ScanModuleEditPart)part);
		}
	}

	/**
	 * <code>SelectionListener</code> of <code>deleteScanModulMenuItem</code>.
	 */
	private class DeleteScanModuleMenuItemSelectionListener implements SelectionListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			// get the scan module the user right-clicked
			ScanModuleEditPart scanModuleEditPart = 
					(ScanModuleEditPart)rightClickEditPart;
			
			// deselect currently selected scan module (if existing)
			if(selectedEditPart instanceof ScanModuleEditPart) {
				((ScanModuleEditPart)selectedEditPart).setFocus(false);
			}
			
			// try to find parent scan module
			EditPart newPart = null;
			ScanModule scanModule = (ScanModule)scanModuleEditPart.getModel();
			ScanModule parentModule = scanModule.getParent().getParentScanModule();
			if (parentModule != null) {
				int x = parentModule.getX();
				int y = parentModule.getY();
				newPart = viewer.findObjectAt(new Point(x, y));
			}
			// scanModule mit angehängten Modulen wird entfernt
			scanModuleEditPart.removeYourSelf();
			
			if (newPart != null){ // && newPart instanceof ScanModuleEditPart) {
				// parent scan module exists -> select it
				selectScanModule((ScanModuleEditPart)newPart);
			} else {
				// no parent scan module -> select nothing
				selectedEditPart = null;
				selectedScanModule = null;
			}
			
			// tell the other views about the change
			updateViews();
		}
	}
	
	/**
	 * <code>SelectionListener</code> of <code>renameScanModuleMenuItem</code>.
	 */
	private class RenameScanModuleMenuItemSelectionListener implements SelectionListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}<br><br>
		 * Shows a dialog to enter a new name for the scan module. 
		 * If OK is pressed, the name is changed. 
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			
			// get the scan module the user right-clicked
			ScanModuleEditPart scanModuleEditPart = 
					(ScanModuleEditPart)rightClickEditPart;
			ScanModule scanModule = (ScanModule)rightClickEditPart.getModel();
			
			// show dialog to input new name
			Shell shell = getSite().getShell();
			InputDialog dialog = new InputDialog(shell, 
					"Renaming ScanModule:" + scanModule.getId(), 
					"Please enter a new name for the Scan Module:", 
					scanModule.getName(), null);
			// if user acknowledges (OK Button) -> change name, 
			// do nothing if not (Cancel Button)
			if(InputDialog.OK == dialog.open()) {
				scanModule.setName(dialog.getValue());
				scanModuleEditPart.refresh();
				scanModuleEditPart.getFigure().repaint();
			}	
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class SaveAllAxisPositionsSelectionListener implements SelectionListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			ScanModule scanModule = (ScanModule)rightClickEditPart.getModel();
			Job saveAllAxisPositions = new SaveAllAxisPositions(
					"Save all Axis Positions", scanModule);
			saveAllAxisPositions.setUser(true);
			saveAllAxisPositions.schedule();
		}
	}
	
	/**
	 * @author Marcus Michalsky
	 * @since 1.1
	 */
	private class SaveAllChannelValuesSelectionListener implements SelectionListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetSelected(SelectionEvent e) {
			ScanModule scanModule = (ScanModule)rightClickEditPart.getModel();
			Job saveAllChannelValues = new SaveAllChannelValues(
					"Save all Channel Values", scanModule);
			saveAllChannelValues.setUser(true);
			saveAllChannelValues.schedule();
		}
	}
	
	/**
	 * 
	 * @author Marcus Michalsky
	 * @since 1.2
	 */
	private class RemoveAllSelectionListener implements SelectionListener {
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
		}
		
		/**
		 * {@inheritDoc}
		 */
		public void widgetSelected(SelectionEvent e) {
			ScanModule scanModule = (ScanModule)rightClickEditPart.getModel();
			UIJob removeAllDevices = new RemoveAllDevices(
					"Remove present Devices", scanModule);
			removeAllDevices.setUser(true);
			removeAllDevices.schedule();
		};
	}
}