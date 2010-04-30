/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.ptb.epics.eve.data.PluginTypes;
import de.ptb.epics.eve.data.measuringstation.PlugIn;
import de.ptb.epics.eve.data.scandescription.Chain;
import de.ptb.epics.eve.data.scandescription.errors.ChainError;
import de.ptb.epics.eve.data.scandescription.errors.ChainErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.Helper;
import de.ptb.epics.eve.editor.dialogs.PluginControllerDialog;

public class ScanView extends ViewPart implements IModelUpdateListener {

	public static final String ID = "de.ptb.epics.eve.editor.views.ScanView"; // TODO Needs to be whatever is mentioned in plugin.xml

	private boolean filling;
	
	private Chain currentChain;  //  @jve:decl-index=0:
	
	private Composite top = null;
	
	private Composite savingComposite = null;
	
	private Composite commentComposite = null;
	
	private Composite pluginComposite = null;
	
	private ExpandBar bar = null;

	private Label filenameLabel = null;

	private Text filenameInput = null;
	
	private Label filenameErrorLabel = null;

	private Button searchButton = null;

	private Button manualSaveCheckBox = null;

	private Button autoNumberCheckBox = null;

	private Label savePluginLabel = null;

	private Combo savePlugingCombo = null;
	
	private Label savePluginComboErrorLabel = null;
	
	private Button savePluginOptionsButton = null;
	
	private Button saveScanDescriptionCheckBox = null;
	
	private Text commentInput = null;

	private CTabFolder eventsTabFolder = null;

	private EventComposite pauseEventComposite = null;
	private EventComposite redoEventComposite = null;
	private EventComposite breakEventComposite = null;
	private EventComposite stopEventComposite = null;
	
	private ModifyListener modifyListener;  //  @jve:decl-index=0:
	private SelectionListener selectionListener;
	
	private ExpandItem item0;
	private ExpandItem item1;
	private ExpandItem item2;
	
	private CTabItem pauseTabItem;
	private CTabItem redoTabItem;
	private CTabItem breakTabItem;
	private CTabItem stopTabItem;
	
		
	@Override
	public void createPartControl( final Composite parent ) {
	    
		parent.setLayout( new FillLayout() );
		
		if( Activator.getDefault().getMeasuringStation() == null ) {
			final Label errorLabel = new Label( parent, SWT.NONE );
			errorLabel.setText( "No Measuring Station has been loaded. Please check Preferences!" );
			return;
		}
		
		this.top = new Composite(parent, SWT.NONE);
		//this.top.setLayout( new FillLayout() );
		this.top.setLayout( new GridLayout() );
		
		this.bar = new ExpandBar( this.top, SWT.V_SCROLL);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.bar.setLayoutData( gridData );
		
		// Save Section
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		this.savingComposite = new Composite( this.bar, SWT.NONE );
		this.savingComposite.setLayout( gridLayout );
		
		// Filename Label / Button
		this.filenameLabel = new Label( this.savingComposite, SWT.NONE );
		this.filenameLabel.setText( "Filename:" );
		this.filenameInput = new Text( this.savingComposite, SWT.BORDER );
		this.filenameInput.setToolTipText( "The filename where the data should be saved." );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalAlignment = GridData.FILL;
		this.filenameInput.setLayoutData(gridData);
		
		this.filenameErrorLabel = new Label( this.savingComposite, SWT.NONE );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		this.filenameErrorLabel.setLayoutData( gridData );
		this.filenameErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		
		// File Browse Button
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.END;
		gridData11.verticalAlignment = GridData.CENTER;
		this.searchButton = new Button( this.savingComposite, SWT.NONE );
		this.searchButton.setText("Browse...");
		this.searchButton.setLayoutData(gridData11);
		this.searchButton.setToolTipText( "Browse for a file" );
		
		this.searchButton.addMouseListener( new MouseListener() {

			public void mouseDoubleClick( final MouseEvent e ) {
				// TODO Auto-generated method stub
				
			}

			public void mouseDown( final MouseEvent e ) {
				Shell shell = getSite().getShell();
				String name = new FileDialog( shell, SWT.OPEN ).open();
				
				if( name != null ) {
				      filenameInput.setText( name );
				}
				
			}

			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});

		// Save Plugin Box / Labels
		this.savePluginLabel = new Label( this.savingComposite, SWT.NONE );
		this.savePluginLabel.setText( "File format:" );
		this.savePlugingCombo = new Combo( this.savingComposite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		this.savePlugingCombo.setLayoutData( gridData );
		
		List<String> pluginNames = new ArrayList<String>();
		PlugIn[] plugins = Activator.getDefault().getMeasuringStation().getPlugins().toArray( new PlugIn[0] );
		
		for( int i = 0; i < plugins.length; ++i ) {
			if( plugins[i].getType() == PluginTypes.SAVE ) {
				pluginNames.add( plugins[i].getName() );
			}
		}
		this.savePlugingCombo.setItems( pluginNames.toArray( new String[0] ) );
		
		// Save Plugin Error Label
		this.savePluginComboErrorLabel = new Label(this.savingComposite, SWT.NONE);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 1;
		this.savePluginComboErrorLabel.setLayoutData( gridData );
		
		// Save Plugin Option Box
		this.savePluginOptionsButton = new Button( this.savingComposite, SWT.NONE );
		this.savePluginOptionsButton.setText( "Options" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;
		gridData.horizontalSpan = 1;
		this.savePluginOptionsButton.setLayoutData( gridData );

		// Save Scan Description Box / Labels
		this.saveScanDescriptionCheckBox = new Button( this.savingComposite, SWT.CHECK );
		this.saveScanDescriptionCheckBox.setText( "Save Scan-Description" );
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		this.saveScanDescriptionCheckBox.setLayoutData( gridData );
		
		@SuppressWarnings("unused")
		Label filler12 = new Label( this.savingComposite, SWT.NONE );
		
		// Confirm Save Box / Labels
		this.manualSaveCheckBox = new Button( this.savingComposite, SWT.CHECK );
		this.manualSaveCheckBox.setText( "Confirm Save" );
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		this.manualSaveCheckBox.setLayoutData( gridData );
		
		// Auto Number Box / Labels
		this.autoNumberCheckBox = new Button( this.savingComposite, SWT.CHECK );
		this.autoNumberCheckBox.setText( "Add Autoincrementing Number to Filename" );
		gridData = new GridData();
		gridData.horizontalSpan = 3;
		this.autoNumberCheckBox.setLayoutData( gridData );

		@SuppressWarnings("unused")
		Label filler1 = new Label( this.savingComposite, SWT.NONE );
		
		// first expand item (Save Options)
		this.item0 = new ExpandItem ( this.bar, SWT.NONE, 0);
		item0.setText("Save Options");
		item0.setHeight( this.savingComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item0.setControl( this.savingComposite );
		
		// Comment Section
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		this.commentComposite = new Composite( this.bar, SWT.NONE );
		this.commentComposite.setLayout( gridLayout );
		
		// Comment Input
		this.commentInput = new Text( this.commentComposite, SWT.BORDER | SWT.V_SCROLL );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.commentInput.setLayoutData( gridData );
		
		// second expand item (Comment)
		this.item1 = new ExpandItem ( this.bar, SWT.NONE, 0);
		item1.setText("Comment");
		//item1.setHeight( this.commentComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item1.setHeight( 100 );
		item1.setControl( this.commentComposite );
	
		// Event Section
		createEventsTabFolder();
		
		// third expand item (Events)
		this.item2 = new ExpandItem ( this.bar, SWT.NONE, 0);
		this.item2.setText("Events");
		this.item2.setHeight( this.pluginComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		this.item2.setControl( this.pluginComposite );
		
		this.bar.addControlListener( new ControlListener() {

			public void controlMoved( final ControlEvent e ) {
			}

			public void controlResized( final ControlEvent e ) {
				int height = bar.getSize().y - item0.getHeaderHeight() - (item0.getExpanded()?item0.getHeight():0) - item1.getHeaderHeight() - 20;
				item2.setHeight( height<150?150:height );
			}
			
		});
		
		this.savingComposite.addControlListener( new ControlListener() {

			public void controlMoved( final ControlEvent e ) {
				int height = bar.getSize().y - item0.getHeaderHeight() - (item0.getExpanded()?item0.getHeight():0) - item1.getHeaderHeight() - (item1.getExpanded()?item1.getHeight():0) - item2.getHeaderHeight() - 20;
				item2.setHeight( height<150?150:height );
			}

			public void controlResized( final ControlEvent e ) {
				int height = bar.getSize().y - item0.getHeaderHeight() - (item0.getExpanded()?item0.getHeight():0) - item1.getHeaderHeight() - (item1.getExpanded()?item1.getHeight():0) - item2.getHeaderHeight() - 20;
				item2.setHeight( height<150?150:height );
			}
			
		});
		
		this.pluginComposite.addControlListener( new ControlListener() {

			public void controlMoved( final ControlEvent e ) {
				int height = bar.getSize().y - item0.getHeaderHeight() - (item0.getExpanded()?item0.getHeight():0) - item1.getHeaderHeight() - (item1.getExpanded()?item1.getHeight():0) - item2.getHeaderHeight() - 20;
				item2.setHeight( height<150?150:height );
			}

			public void controlResized( final ControlEvent e ) {
				int height = bar.getSize().y - item0.getHeaderHeight() - (item0.getExpanded()?item0.getHeight():0) - item1.getHeaderHeight() - (item1.getExpanded()?item1.getHeight():0) - item2.getHeaderHeight() - 20;
				item2.setHeight( height<150?150:height );
			}
			
		});
		
		this.setEnabledForAll( false );

		this.createListener();
		this.appendListener();

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * This method initializes eventsTabFolder	
	 *
	 */
	private void createEventsTabFolder() {
		
		GridLayout gridLayout = new GridLayout();
		this.pluginComposite = new Composite( this.bar, SWT.NONE );
		this.pluginComposite.setLayout( gridLayout );
		//this.pluginComposite.setLayout( new FillLayout() );

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		this.pluginComposite.setLayoutData( gridData );
		
		eventsTabFolder = new CTabFolder(this.pluginComposite, SWT.FLAT );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.eventsTabFolder.setLayoutData( gridData );
		eventsTabFolder.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}

			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				// Einträge in der Auswahlliste werden aktualisiert
				CTabItem wahlItem = eventsTabFolder.getSelection();
				EventComposite wahlComposite = (EventComposite)wahlItem.getControl();
				wahlComposite.setEventChoice();
			}
		});
		
		pauseEventComposite = new EventComposite( eventsTabFolder, SWT.NONE );
		redoEventComposite = new EventComposite( eventsTabFolder, SWT.NONE);
		breakEventComposite = new EventComposite( eventsTabFolder, SWT.NONE );
		stopEventComposite = new EventComposite( eventsTabFolder, SWT.NONE );
		this.pauseTabItem = new CTabItem(eventsTabFolder, SWT.FLAT);
		this.pauseTabItem.setText( "Pause" );
		this.pauseTabItem.setControl(pauseEventComposite);
		this.redoTabItem = new CTabItem(eventsTabFolder, SWT.FLAT);
		this.redoTabItem.setText( "Redo" );
		this.redoTabItem.setControl(redoEventComposite);
		this.breakTabItem = new CTabItem(eventsTabFolder, SWT.FLAT);
		this.breakTabItem.setText( "Break" );
		this.breakTabItem.setControl(breakEventComposite);
		this.stopTabItem = new CTabItem(eventsTabFolder, SWT.FLAT);
		this.stopTabItem.setText( "Stop" );
		this.stopTabItem.setControl(stopEventComposite);
		
	}

	private void setEnabledForAll( final boolean enabled ) {
		this.filenameInput.setEnabled(enabled);
		this.searchButton.setEnabled(enabled);
		this.manualSaveCheckBox.setEnabled(enabled);
		this.autoNumberCheckBox.setEnabled(enabled);
		this.savePlugingCombo.setEnabled(enabled);
		this.eventsTabFolder.setEnabled(enabled);
		this.saveScanDescriptionCheckBox.setEnabled( enabled );
		this.commentInput.setEnabled( enabled );
		if( enabled ) {
			if( this.currentChain.getSavePluginController().getPlugin() != null && this.currentChain.getSavePluginController().getPlugin().getParameters().size() > 0 ) {
				this.savePluginOptionsButton.setEnabled( true );
			} else {
				this.savePluginOptionsButton.setEnabled( false );
			}
		} else {
			this.savePluginOptionsButton.setEnabled( enabled );
		}
	
	}

	private void fillFields() {
		this.filling = true;

		if( this.currentChain != null ) {
			try {
			this.filenameInput.setText( (this.currentChain.getSaveFilename()!=null)?this.currentChain.getSaveFilename():"" );
			this.manualSaveCheckBox.setSelection( this.currentChain.isConfirmSave() );
			this.autoNumberCheckBox.setSelection( this.currentChain.isAutoNumber() );
			
			this.saveScanDescriptionCheckBox.setSelection( this.currentChain.isSaveScanDescription() );
			
			this.savePlugingCombo.setText( (this.currentChain.getSavePluginController().getPlugin() !=null)?this.currentChain.getSavePluginController().getPlugin().getName():"" );		
			
			if( this.currentChain.getSavePluginController().getModelErrors().size() > 0 ) {
				this.savePluginComboErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
				this.savePluginComboErrorLabel.setToolTipText( "There is at least one error in the plug in configuration!" );
			} else {
				this.savePluginComboErrorLabel.setImage( null );
				this.savePluginComboErrorLabel.setToolTipText( "" );
				
			}
			
			this.commentInput.setText( this.currentChain.getComment() );
			this.commentInput.setSelection( this.currentChain.getComment().length() );
			
			if( this.pauseEventComposite.getControlEventManager() != this.currentChain.getPauseControlEventManager() ) {
				this.pauseEventComposite.setControlEventManager( this.currentChain.getPauseControlEventManager() );
			}
			if( this.redoEventComposite.getControlEventManager() != this.currentChain.getRedoControlEventManager() ) {
				this.redoEventComposite.setControlEventManager( this.currentChain.getRedoControlEventManager() );
			}
			if( this.breakEventComposite.getControlEventManager() != this.currentChain.getRedoControlEventManager() ) {
				this.breakEventComposite.setControlEventManager( this.currentChain.getBreakControlEventManager() );
			}
			if( this.stopEventComposite.getControlEventManager() != this.currentChain.getStopControlEventManager() ) {
				this.stopEventComposite.setControlEventManager( this.currentChain.getStopControlEventManager() );
			}
			
			final List< IModelError > errorList = this.currentChain.getModelErrors();
			final Iterator< IModelError > it = errorList.iterator();
			
			this.filenameErrorLabel.setImage( null );
			this.filenameErrorLabel.setToolTipText( "" );

			
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				if( modelError instanceof ChainError ) {
					final ChainError chainError = (ChainError)modelError;
					this.filenameErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
					if( chainError.getErrorType() == ChainErrorTypes.FILENAME_EMPTY ) {
						this.filenameErrorLabel.setToolTipText( "The filename must not be empty!" );
					} else if( chainError.getErrorType() == ChainErrorTypes.FILENAME_ILLEGAL_CHARACTER ) {
						this.filenameErrorLabel.setToolTipText( "The filename contains illegal character!" );
					}
				}
			}			
			
			} catch( Exception ex ) {
				ex.printStackTrace();
			}
			if( this.currentChain.getPauseControlEventManager().getModelErrors().size() > 0 ) {
				this.pauseTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
			} else {
				this.pauseTabItem.setImage( null );
			}
			if( this.currentChain.getBreakControlEventManager().getModelErrors().size() > 0 ) {
				this.breakTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
			} else {
				this.breakTabItem.setImage( null );
			}
			if( this.currentChain.getRedoControlEventManager().getModelErrors().size() > 0 ) {
				this.redoTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
			} else {
				this.redoTabItem.setImage( null );
			}
			if( this.currentChain.getStopControlEventManager().getModelErrors().size() > 0 ) {
				this.stopTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
			} else {
				this.stopTabItem.setImage( null );
			}
		} 
		else {
			this.filenameInput.setText( "" );
			this.manualSaveCheckBox.setSelection( false );
			this.autoNumberCheckBox.setSelection( false );
			this.saveScanDescriptionCheckBox.setSelection( false );
			this.savePlugingCombo.setText( "" );
			this.pauseEventComposite.setControlEventManager( null );
			this.redoEventComposite.setControlEventManager( null );
			this.breakEventComposite.setControlEventManager( null );
			this.stopEventComposite.setControlEventManager( null );
			this.commentInput.setText( "" );
			
			this.savePluginComboErrorLabel.setImage( null );
			
			
		}
		this.filling = false;
	}
	
	public Chain getCurrentChain() {
		return currentChain;
	}

	public void setCurrentChain( final Chain currentChain ) {
		if( this.currentChain != null ) {
			this.currentChain.removeModelUpdateListener( this );
		}
		this.currentChain = currentChain;
		if( this.currentChain != null ) {
			this.currentChain.addModelUpdateListener( this );
			this.setEnabledForAll( true );
			this.setPartName( "Scan: " + this.currentChain.getId() );
			this.fillFields();
		} else {
			this.setEnabledForAll( false );
			this.setPartName( "No Scan loaded" );
		}
		
	}
	
	private void appendListener() {
		this.filenameInput.addModifyListener( this.modifyListener );
		this.savePlugingCombo.addModifyListener( this.modifyListener );
		this.manualSaveCheckBox.addSelectionListener( this.selectionListener );
		this.autoNumberCheckBox.addSelectionListener( this.selectionListener );
		this.saveScanDescriptionCheckBox.addSelectionListener( this.selectionListener );
		this.commentInput.addModifyListener( this.modifyListener );
		this.savePluginOptionsButton.addSelectionListener( this.selectionListener );
	}
	
	private void createListener() {
		this.modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if( !filling ) {
					if( e.widget == filenameInput ) {
						currentChain.setSaveFilename( filenameInput.getText() );
					} else if( e.widget == savePlugingCombo ) {
						// TODO: hier kann eigentlich kein Fehler mehr auftauchen, da das Plugin
						// jetzt nur noch ausgewählt und nicht mehr verändert werden kann (Hartmut 20.4.10)
						if( savePlugingCombo.getText().equals("") || Helper.contains( savePlugingCombo.getItems(), savePlugingCombo.getText() ) ) {
							currentChain.getSavePluginController().setPlugin( Activator.getDefault().getMeasuringStation().getPluginByName( savePlugingCombo.getText() ) );
							savePluginComboErrorLabel.setImage( null );	
							savePluginComboErrorLabel.setToolTipText( "" );
						} else {
							currentChain.getSavePluginController().setPlugin( null );
							savePluginComboErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );	
							savePluginComboErrorLabel.setToolTipText( "The Plug-In cannot be found" );
						}
						savePluginOptionsButton.setEnabled( currentChain.getSavePluginController().getPlugin() != null && currentChain.getSavePluginController().getPlugin().getParameters().size() > 0 );
					} else if( e.widget == commentInput ) {
						currentChain.setComment( commentInput.getText() );
					} 
				}
			}
			
		};

		this.selectionListener = new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}

			public void widgetSelected(SelectionEvent e) {
				if( !filling ) {
					if( e.widget == manualSaveCheckBox ) {
						currentChain.setConfirmSave( manualSaveCheckBox.getSelection() );
					} else if( e.widget == saveScanDescriptionCheckBox ) {
						currentChain.setSaveScanDescription( saveScanDescriptionCheckBox.getSelection() );
					} else if( e.widget == autoNumberCheckBox ) {
						currentChain.setAutoNumber( autoNumberCheckBox.getSelection() );
					} else if( e.widget == savePluginOptionsButton ) {
						PluginControllerDialog dialog = new PluginControllerDialog( null, currentChain.getSavePluginController() );
						dialog.setBlockOnOpen( true );
						dialog.open();
					}
				}
			}
			
		};
	}

	@Override
	public void updateEvent( final ModelUpdateEvent modelUpdateEvent ) {
		this.fillFields();
		
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10,327,191"
