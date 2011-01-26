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
import org.eclipse.swt.widgets.Control;
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
import de.ptb.epics.eve.data.scandescription.updatenotification.IModelUpdateListener;
import de.ptb.epics.eve.data.scandescription.updatenotification.ModelUpdateEvent;
import de.ptb.epics.eve.editor.Activator;

public class DetectorChannelView extends ViewPart implements IModelUpdateListener {

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

	private CTabItem redoEventTabItem;
	
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
					if( averageText.getText().equals( "" ) ) {
						// average soll immer gefüllt sein, obwohl es im xsd-File
						// bisher kein Pflichtfeld ist (Hartmut 6.5.10)
						averageErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
						averageErrorLabel.setToolTipText( "The average must not be empty." );
					} else {
						try {
							currentChannel.setAverageCount( Integer.parseInt( averageText.getText() ) );
							averageErrorLabel.setImage( null );
							averageErrorLabel.setToolTipText( null );
						} catch( final NumberFormatException ex ) {
							averageErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
							averageErrorLabel.setToolTipText( "The average must be a integer number." );
						}
						catch( final IllegalArgumentException ex ) {
							averageErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
							averageErrorLabel.setToolTipText( ex.getMessage() );
						}
					}
				}
			}
		});

		this.averageErrorLabel = new Label( this.top, SWT.NONE );
		this.averageErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK ) );
		
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
						maxDeviationErrorLabel.setImage( null );
						maxDeviationErrorLabel.setToolTipText( null );
					} else {
						try {
							currentChannel.setMaxDeviation( Double.parseDouble( maxDeviationText.getText() ) );
							maxDeviationErrorLabel.setImage( null );
							maxDeviationErrorLabel.setToolTipText( null );
						} catch( final NumberFormatException ex ) {
							maxDeviationErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
							maxDeviationErrorLabel.setToolTipText( "The max deviation must be a floating point number." );
						}
					}
				}
			}
		});
		
		this.maxDeviationErrorLabel = new Label( this.top, SWT.NONE );
		this.maxDeviationErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK ) );
		
		this.minimumLabel = new Label( this.top, SWT.NONE );
		this.minimumLabel.setText( "Minumum:" );
		this.minimumLabel.setToolTipText("for values < minimum no deviation check");
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
						minimumErrorLabel.setImage( null );
						minimumErrorLabel.setToolTipText( null );
					} else {
						try {
							currentChannel.setMinumum( Double.parseDouble( minimumText.getText() ) );
							minimumErrorLabel.setImage( null );
							minimumErrorLabel.setToolTipText( null );
						} catch( final NumberFormatException ex ) {
							minimumErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
							minimumErrorLabel.setToolTipText( "The minimum must be a floating point number." );
						}
					}
				}
			}
		});
		
		this.minimumErrorLabel = new Label( this.top, SWT.NONE );
		this.minimumErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK ) );
		
		this.maxAttemptsLabel = new Label( this.top, SWT.NONE );
		this.maxAttemptsLabel.setText( "Max. Attempts:" );
		this.maxAttemptsLabel.setToolTipText("Maximum attemps to calculate deviation:" );
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
					if( maxAttemptsText.getText().equals( "" ) ) {
						currentChannel.setMaxAttempts( Integer.MIN_VALUE );
						maxAttemptsErrorLabel.setImage( null );
						maxAttemptsErrorLabel.setToolTipText( null );
					} else {
						try {
							currentChannel.setMaxAttempts( Integer.parseInt( maxAttemptsText.getText() ) );
							maxAttemptsErrorLabel.setImage( null );
							maxAttemptsErrorLabel.setToolTipText( null );
						} catch( final NumberFormatException ex ) {
							maxAttemptsErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
							maxAttemptsErrorLabel.setToolTipText( "The max attempts must be a non negativ integer number." );
						}
					}
				}
			}
		});
		
		this.maxAttemptsErrorLabel = new Label( this.top, SWT.NONE );
		this.maxAttemptsErrorLabel.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_WARN_TSK ) );
		
		this.confirmTriggerManualCheckBox = new Button( this.top, SWT.CHECK );
		this.confirmTriggerManualCheckBox.setText( "Confirm Trigger manual" );
		this.confirmTriggerManualCheckBox.setToolTipText("Mark to ask before this channel");
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
		this.detectorReadyEventCheckBox.setToolTipText( "Mark to send detector ready event if channel is ready" );
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

		this.eventComposite.addControlListener(new ControlListener() {

			@Override
			public void controlMoved(ControlEvent e) {
				// Erst werden die Children ausgelesen um nachzusehen, welches
				// Child ein CTabFolder ist, dieses bekommt dann den Focus!
				Control[] childArray = eventComposite.getChildren();
				for( int i = 0; i < childArray.length; ++i ) {
					if (childArray[i].toString().equals("CTabFolder {}")) {
						childArray[i].setFocus();
					}
				}
			}

			@Override
			public void controlResized(ControlEvent e) {
			}
			
		});
		
		redoEventComposite = new EventComposite( eventsTabFolder, SWT.NONE);
		 
		this.redoEventTabItem = new CTabItem(eventsTabFolder, SWT.FLAT);
		this.redoEventTabItem.setText( "Redo" );
		this.redoEventTabItem.setToolTipText("Repeat the current reading of the channel, if redo event occurs");
		this.redoEventTabItem.setControl(redoEventComposite);
		
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
		if( this.currentChannel != null ) {
			this.currentChannel.removeModelUpdateListener( this );
		}
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

			if( this.currentChannel.getMaxAttempts() != Integer.MIN_VALUE ) {
				this.maxAttemptsText.setText( "" + this.currentChannel.getMaxAttempts() );
			} else {
				this.maxAttemptsText.setText( "" );
			}
			
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
			
			// TODO von Hartmut: da maxDeviation und minimum keine 
			// ChannelErrors liefern, muß hier auch keien Abfrage erfolgen!
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
				}
			}
			if( this.currentChannel.getRedoControlEventManager().getModelErrors().size() > 0 ) {
				this.redoEventTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
			} else {
				this.redoEventTabItem.setImage( null );
			}
			this.currentChannel.addModelUpdateListener( this );
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
			
			this.averageErrorLabel.setImage( null );
			this.maxDeviationErrorLabel.setImage( null );
			this.minimumErrorLabel.setImage( null );
			this.maxAttemptsErrorLabel.setImage(null);
		}
	}

	@Override
	public void updateEvent(ModelUpdateEvent modelUpdateEvent) {
		if( this.currentChannel.getRedoControlEventManager().getModelErrors().size() > 0 ) {
			this.redoEventTabItem.setImage( PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK ) );
		} else {
			this.redoEventTabItem.setImage( null );
		}
		
	}

}
