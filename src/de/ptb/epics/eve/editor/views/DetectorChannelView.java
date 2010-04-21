/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;


import java.util.Iterator;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;

import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.errors.ChannelError;
import de.ptb.epics.eve.data.scandescription.errors.ChannelErrorTypes;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.editor.Activator;

public class DetectorChannelView extends ViewPart {

	public static final String ID = "de.ptb.epics.eve.editor.views.DetectorChannelView"; // TODO Needs to be whatever is mentioned in plugin.xml

	private Composite top = null;

	private Label averageLabel;
	private Text averageText;
	private Label averageErrorLabel;
	
	private Label maxDeviationLabel;
	private Text maxDeviationText;
	private Label maxDeviationErrorLabel;
	
	private Label minimumLabel;
	private Text minimumText;
	private Label minimumErrorLabel;
	
	private Label maxAttemptsLabel;
	private Text maxAttemptsText;
	private Label maxAttemptsErrorLabel;
	
	private Button confirmTriggerManualCheckBox;

	private ExpandBar bar = null;
	private ExpandItem item0;
	private CTabFolder eventsTabFolder = null;
	private EventComposite redoEventComposite = null;
	private Composite eventComposite = null;

	private Button detectorReadyEventCheckBox;

	private Channel currentChannel;

	@Override
	public void createPartControl( final Composite parent ) {
		parent.setLayout(new FillLayout());
		
		if( Activator.getDefault().getMeasuringStation() == null ) {
			final Label errorLabel = new Label( parent, SWT.NONE );
			errorLabel.setText( "No Measuring Station has been loaded. Please check Preferences!" );
			return;
		}
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		GridData gridData;
		
		this.top = new Composite( parent, SWT.NONE );
		this.top.setLayout( gridLayout );
		
		this.averageLabel = new Label( this.top, SWT.NONE );
		this.averageLabel.setText( "Average:" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.averageLabel.setLayoutData( gridData );
		
		this.averageText = new Text( this.top, SWT.BORDER );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.averageText.setLayoutData( gridData );
		this.averageText.addModifyListener( new ModifyListener() {

			public void modifyText( final ModifyEvent e ) {
				if( currentChannel != null ) {
					try {
						currentChannel.setAverageCount( Integer.parseInt( averageText.getText() ) );
					} catch( final NumberFormatException ex ) {
					
					}
				}
			}
			
		});

		this.averageErrorLabel = new Label( this.top, SWT.NONE );
//		gridData = new GridData();
//		gridData.horizontalAlignment = GridData.FILL;
//		this.averageErrorLabel.setLayoutData( gridData );
		this.averageErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK ) );
		this.averageErrorLabel.setToolTipText( "Fehlerbehandlung fehlt" );
		
		this.maxDeviationLabel = new Label( this.top, SWT.NONE );
		this.maxDeviationLabel.setText( "Max. Deviation (%):" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.maxDeviationLabel.setLayoutData( gridData );
		
		this.maxDeviationText = new Text( this.top, SWT.BORDER );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.maxDeviationText.setLayoutData( gridData );
		this.maxDeviationText.addModifyListener( new ModifyListener() {

			public void modifyText( final ModifyEvent e ) {
				if( currentChannel != null ) {
					if( maxDeviationText.getText().equals( "" ) ) {
						currentChannel.setMaxDeviation( Double.NEGATIVE_INFINITY );
					} else {
						try {
							currentChannel.setMaxDeviation( Double.parseDouble( maxDeviationText.getText() ) );
						} catch( final NumberFormatException ex ) {
					
						}
					}
				}
			}
		});
		
		this.maxDeviationErrorLabel = new Label( this.top, SWT.NONE );
//		gridData = new GridData();
//		gridData.horizontalAlignment = GridData.FILL;
//		this.maxDeviationErrorLabel.setData( gridData );
		this.maxDeviationErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK ) );
		this.maxDeviationErrorLabel.setToolTipText( "Fehlerbehandlung fehlt" );
		// TODO: maxDeviation darf nur ein Zahlenwert sein
		
		this.minimumLabel = new Label( this.top, SWT.NONE );
		this.minimumLabel.setText( "Minumum:" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.minimumLabel.setLayoutData( gridData );
		
		this.minimumText = new Text( this.top, SWT.BORDER );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.minimumText.setLayoutData( gridData );
		this.minimumText.addModifyListener( new ModifyListener() {

			public void modifyText( final ModifyEvent e ) {
				if( currentChannel != null ) {
					if( minimumText.getText().equals( "" ) ) {
						currentChannel.setMinumum( Double.NEGATIVE_INFINITY );
					} else {
						try {
							currentChannel.setMinumum( Double.parseDouble( minimumText.getText() ) );
						} catch( final NumberFormatException ex ) {
					
						}
					}
				}
			}
			
		});
		
		this.minimumErrorLabel = new Label( this.top, SWT.NONE );
//		gridData = new GridData();
//		gridData.horizontalAlignment = GridData.FILL;
//		this.minimumErrorLabel.setData( gridData );
		this.minimumErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK ) );
		this.minimumErrorLabel.setToolTipText( "Fehlerbehandlung fehlt" );
		// TODO: minimum darf nur ein Zahlenwert sein
		
		this.maxAttemptsLabel = new Label( this.top, SWT.NONE );
		this.maxAttemptsLabel.setText( "Max. Attempts:" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.maxAttemptsLabel.setLayoutData( gridData );
		
		this.maxAttemptsText = new Text( this.top, SWT.BORDER );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.maxAttemptsText.setLayoutData( gridData );
		this.maxAttemptsText.addModifyListener( new ModifyListener() {

			public void modifyText( final ModifyEvent e ) {
				if( currentChannel != null ) {
					try {
						currentChannel.setMaxAttempts( Integer.parseInt( maxAttemptsText.getText() ) );
					} catch( final NumberFormatException ex ) {
					
					}
				}
				
			}
			
		});
		
		this.maxAttemptsErrorLabel = new Label( this.top, SWT.NONE );
//		gridData = new GridData();
//		gridData.horizontalAlignment = GridData.FILL;
//		this.maxAttemptsErrorLabel.setData( gridData );
		this.maxAttemptsErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK ) );
		this.maxAttemptsErrorLabel.setToolTipText( "Fehlerbehandlung fehlt" );
		
		this.confirmTriggerManualCheckBox = new Button( this.top, SWT.CHECK );
		this.confirmTriggerManualCheckBox.setText( "Confirm Trigger manual" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		this.confirmTriggerManualCheckBox.setLayoutData( gridData );
		this.confirmTriggerManualCheckBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				System.err.println( "Confirm Trigger" );
				if( currentChannel != null ) {
					currentChannel.setConfirmTrigger( confirmTriggerManualCheckBox.getSelection() );
				}
			}
		});

		this.bar = new ExpandBar( this.top, SWT.V_SCROLL);
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		this.bar.setLayoutData( gridData );

		// Event Section
		gridLayout = new GridLayout();
		this.eventComposite = new Composite( this.bar, SWT.NONE );
		this.eventComposite.setLayout( gridLayout );

		// first expand item (Events)
		this.item0 = new ExpandItem ( this.bar, SWT.NONE, 0);
		this.item0.setText("Event options");
		this.item0.setHeight( this.eventComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		this.item0.setControl( this.eventComposite );
	
		this.bar.addControlListener( new ControlListener() {

			public void controlMoved( final ControlEvent e ) {
			}

			public void controlResized( final ControlEvent e ) {
				int height = bar.getSize().y - item0.getHeaderHeight() - 20;
				item0.setHeight( height<200?200:height );
			}
		});

		this.detectorReadyEventCheckBox = new Button( this.eventComposite, SWT.CHECK );
		this.detectorReadyEventCheckBox.setText( "Send Detector Ready Event" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		this.detectorReadyEventCheckBox.setLayoutData( gridData );
		this.detectorReadyEventCheckBox.addSelectionListener( new SelectionListener() {
			
			public void widgetDefaultSelected( final SelectionEvent e ) {
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				
				// we create an event and add it to the list if selected 
				// or remove the event with same id from the list if deselected
				Event detReadyEvent = new Event(currentChannel.getAbstractDevice().getID(), currentChannel.getAbstractDevice().getParent().getName(), currentChannel.getAbstractDevice().getName(), currentChannel.getParentScanModul().getChain().getId(), currentChannel.getParentScanModul().getId());

				if( detectorReadyEventCheckBox.getSelection() ) {
					currentChannel.getParentScanModul().getChain().getScanDescription().add( detReadyEvent );
					currentChannel.setDetectorReadyEvent(detReadyEvent);
				} else {
					currentChannel.getParentScanModul().getChain().getScanDescription().removeEventById( detReadyEvent.getID() );
					currentChannel.setDetectorReadyEvent(null);
				}
			}
		});
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;

		eventsTabFolder = new CTabFolder(this.eventComposite, SWT.FLAT );
		eventsTabFolder.setLayoutData( gridData );
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
		
		redoEventComposite = new EventComposite( eventsTabFolder, SWT.NONE);
		CTabItem tabItem1 = new CTabItem(eventsTabFolder, SWT.FLAT);
		tabItem1.setText( "Redo" );
		tabItem1.setControl(redoEventComposite);
		
		this.averageText.setEnabled( false );
		this.maxDeviationText.setEnabled( false );
		this.minimumText.setEnabled( false );
		this.maxAttemptsText.setEnabled( false );
		this.confirmTriggerManualCheckBox.setEnabled( false );
		this.detectorReadyEventCheckBox.setEnabled( false );
		this.eventsTabFolder.setEnabled(false);
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void setChannel( final Channel channel ) {
		this.currentChannel = channel;
		if( this.currentChannel != null ) {
			this.averageText.setText( "" + this.currentChannel.getAverageCount() );
			if( this.currentChannel.getMaxDeviation() != Double.NEGATIVE_INFINITY ) {
				this.maxDeviationText.setText( "" + this.currentChannel.getMaxDeviation() );
			} else {
				this.maxDeviationText.setText( "" );
			}
			if( this.currentChannel.getMinumum() != Double.NEGATIVE_INFINITY ) {
				this.minimumText.setText( "" + this.currentChannel.getMinumum() );
			} else {
				this.minimumText.setText( "" );
			}
			this.maxAttemptsText.setText( "" + this.currentChannel.getMaxAttempts() );
			this.confirmTriggerManualCheckBox.setSelection( this.currentChannel.isConfirmTrigger() );
			this.detectorReadyEventCheckBox.setSelection( this.currentChannel.getDetectorReadyEvent() != null );
			this.setPartName( channel.getAbstractDevice().getFullIdentifyer() );

			this.averageText.setEnabled( true );
			this.maxDeviationText.setEnabled( true );
			this.minimumText.setEnabled( true );
			this.maxAttemptsText.setEnabled( true );
			this.confirmTriggerManualCheckBox.setEnabled( true );
			this.detectorReadyEventCheckBox.setEnabled( true );
			this.eventsTabFolder.setEnabled(true);
			
			this.redoEventComposite.setControlEventManager( this.currentChannel.getRedoControlEventManager() );
			
			// TODO: Warum funktioniert hier die Fehlerbehandlung noch nicht
			// richtig? Wenn in MaxDeviation ein negativer Wert steht, sollte
			// das als Fehler ausgewertet werden. (Hartmut 21.4.10)
			
			this.maxDeviationErrorLabel.setImage( null );
			this.minimumErrorLabel.setImage( null );
		
			final Iterator< IModelError > it = this.currentChannel.getModelErrors().iterator();
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				if( modelError instanceof ChannelError ) {
					final ChannelError channelError = (ChannelError) modelError;
					if( channelError.getErrorType() == ChannelErrorTypes.MAX_DEVIATION_NOT_POSSIBLE ) {
						this.maxDeviationErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
					} else if( channelError.getErrorType() == ChannelErrorTypes.MINIMUM_NOT_POSSIBLE ) {
						this.minimumErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
					}
				}			}
		} else {
			this.averageText.setText( "" );
			this.maxDeviationText.setText( "" );
			this.minimumText.setText( "" );
			this.maxAttemptsText.setText( "" );
			this.confirmTriggerManualCheckBox.setSelection( false );
			this.detectorReadyEventCheckBox.setSelection( false );
			this.eventsTabFolder.setEnabled(false);
			
			this.averageText.setEnabled( false );
			this.maxDeviationText.setEnabled( false );
			this.minimumText.setEnabled( false );
			this.maxAttemptsText.setEnabled( false );
			this.confirmTriggerManualCheckBox.setEnabled( false );
			this.detectorReadyEventCheckBox.setEnabled( false );
			this.setPartName( "Detector Channel View" );
			
			this.maxDeviationErrorLabel.setImage( null );
			this.minimumErrorLabel.setImage( null );
		}
	}

}
