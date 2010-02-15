/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;

import de.ptb.epics.eve.data.ComparisonTypes;
import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.EventTypes;
import de.ptb.epics.eve.data.measuringstation.Event;
import de.ptb.epics.eve.data.scandescription.PauseEvent;
import de.ptb.epics.eve.editor.Activator;
import de.ptb.epics.eve.editor.Helper;

public class PauseEventComposite extends Composite {

	private PauseEvent event;  //  @jve:decl-index=0:
	private Combo pauseEventCombo = null;
	private Combo pauseEventOperatorCombo = null;
	private Combo pauseEventLimitCombo = null;
	private Button continueCheckBox = null;

	private String[] eventIDs;
	private boolean filling = false;
	private ModifyListener modifyListener;
	private SelectionListener selectionListener;
	public PauseEventComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		
		Event[] events = Activator.getDefault().getMeasuringStation().getEvents().toArray( new Event[0] );
		this.eventIDs = new String[events.length];
		for( int i = 0; i < this.eventIDs.length; ++i ) {
			this.eventIDs[i] = events[i].getID();
		}
		
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		createPauseEventCombo();
		this.setLayout(gridLayout);
		createPauseEventOperatorCombo();
		setSize(new Point(300, 200));
		pauseEventLimitCombo = new Combo(this, SWT.BORDER);
		continueCheckBox = new Button(this, SWT.CHECK);
		continueCheckBox.setText("Continue if false");
		continueCheckBox.setLayoutData(gridData);
		this.createListener();
		this.appendListener();
	}

	/**
	 * This method initializes pauseEventCombo	
	 *
	 */
	private void createPauseEventCombo() {
		pauseEventCombo = new Combo(this, SWT.NONE);
		pauseEventCombo.setItems( this.eventIDs );
	}

	/**
	 * This method initializes pauseEventOperatorCombo	
	 *
	 */
	private void createPauseEventOperatorCombo() {
		pauseEventOperatorCombo = new Combo(this, SWT.NONE);
	}

	protected PauseEvent getEvent() {
		return event;
	}

	protected void setEvent(PauseEvent event) {
		this.event = event;
		this.fillFields();
		
	}

	protected void setEnabledForAll( final boolean enabled ) {
		this.setEnabled(enabled);
		this.pauseEventCombo.setEnabled( enabled );
		this.pauseEventOperatorCombo.setEnabled(enabled);
		this.pauseEventLimitCombo.setEnabled(enabled);
		this.continueCheckBox.setEnabled(enabled);
	}
	
	private void createListener() {
		this.modifyListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if( !filling ) {
					if( e.widget == pauseEventCombo ) {
						
						if( Helper.contains( eventIDs, pauseEventCombo.getText() ) ) {
							pauseEventOperatorCombo.setEnabled( true );
							pauseEventLimitCombo.setEnabled( true );
							continueCheckBox.setEnabled( true );
							Color oldColor = pauseEventCombo.getBackground();
							pauseEventCombo.setBackground( new Color( oldColor.getDevice(), 255, 255, 255 ) );
							oldColor.dispose();
							if( event.getEvent() == Activator.getDefault().getMeasuringStation().getEventById( pauseEventCombo.getText()  ) )
									return;
							event.setEvent( Activator.getDefault().getMeasuringStation().getEventById( pauseEventCombo.getText() ) );
							if( event.getEvent().getType() == EventTypes.SCHEDULE ) {
								pauseEventOperatorCombo.setEnabled( false );
								pauseEventLimitCombo.setEnabled( false );
								continueCheckBox.setEnabled( false );
								
								oldColor = pauseEventOperatorCombo.getBackground();
								pauseEventOperatorCombo.setBackground( new Color( oldColor.getDevice(), 255, 255, 255 ) );
								oldColor.dispose();
								
								oldColor = pauseEventLimitCombo.getBackground();
								pauseEventLimitCombo.setBackground( new Color( oldColor.getDevice(), 255, 255, 255 ) );
								oldColor.dispose();
								
							
							} else {
								pauseEventOperatorCombo.setEnabled( true );
								pauseEventLimitCombo.setEnabled( true );
								event.getLimit().setType( event.getEvent().getMonitor().getDataType().getType() );
								pauseEventOperatorCombo.setItems( ComparisonTypes.typeToString( DataTypes.getPossibleComparisonTypes( event.getLimit().getType() ) ) );
								pauseEventLimitCombo.setItems( (event.getEvent().getMonitor().getDataType().getValues()!=null)?event.getEvent().getMonitor().getDataType().getValues().split(","):new String[0] );
							}
						} else if( pauseEventCombo.getText().equals( "" ) ) {
							pauseEventOperatorCombo.setEnabled( false );
							pauseEventLimitCombo.setEnabled( false );
							continueCheckBox.setEnabled( false );
							Color oldColor = pauseEventCombo.getBackground();
							pauseEventCombo.setBackground( new Color( oldColor.getDevice(), 255, 255, 255 ) );
							oldColor.dispose();
							event.setEvent( null );
							pauseEventOperatorCombo.setItems( new String[0] );
						
						} else {
							pauseEventOperatorCombo.setEnabled( false );
							pauseEventLimitCombo.setEnabled( false );
							continueCheckBox.setEnabled( false );
							Color oldColor = pauseEventCombo.getBackground();
							pauseEventCombo.setBackground( new Color( oldColor.getDevice(), 255, 0, 0 ) );
							oldColor.dispose();
							event.setEvent( null );
							pauseEventOperatorCombo.setItems( new String[0] );
						}
					} else if( e.widget == pauseEventOperatorCombo ) {
					
						if( Helper.contains( pauseEventOperatorCombo.getItems(), pauseEventOperatorCombo.getText() ) ) {
							Color oldColor = pauseEventOperatorCombo.getBackground();
							pauseEventOperatorCombo.setBackground( new Color( oldColor.getDevice(), 255, 255, 255 ) );
							oldColor.dispose();
							event.getLimit().setComparison( ComparisonTypes.stringToType( pauseEventOperatorCombo.getText() ) );
						} else {
							Color oldColor = pauseEventOperatorCombo.getBackground();
							pauseEventOperatorCombo.setBackground( new Color( oldColor.getDevice(), 255, 0, 0 ) );
							oldColor.dispose();
							event.getLimit().setComparison( null );
						}
					
					} else if( e.widget == pauseEventLimitCombo ) {
						if( pauseEventLimitCombo.getItems().length > 0 ) {
							if( !Helper.contains( pauseEventLimitCombo.getItems(), pauseEventLimitCombo.getText() ) ) {
								Color oldColor = pauseEventLimitCombo.getBackground();
								pauseEventLimitCombo.setBackground( new Color( oldColor.getDevice(), 255, 0, 0 ) );
								oldColor.dispose();
								event.getLimit().setValue( "" );
								return;
							}
						}
						Color oldColor = pauseEventLimitCombo.getBackground();
						pauseEventLimitCombo.setBackground( new Color( oldColor.getDevice(), 255, 255, 255 ) );
						oldColor.dispose();
						event.getLimit().setValue( pauseEventLimitCombo.getText() );
					}
				
				}
			}
			
			
		};
		
		this.selectionListener = new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected(SelectionEvent e) {
				
				if( e.widget == continueCheckBox ) {
					event.setContinueIfFalse( continueCheckBox.getSelection() );
				}
				
			}
			
		};
	}
	private void appendListener() {
		this.pauseEventCombo.addModifyListener( this.modifyListener );
		this.pauseEventOperatorCombo.addModifyListener( this.modifyListener );
		this.pauseEventLimitCombo.addModifyListener( this.modifyListener );
		this.continueCheckBox.addSelectionListener( this.selectionListener );
	}
	
	private void fillFields() {
		this.filling = true;
		if( this.event != null ) {
			this.pauseEventCombo.setText( (this.event.getEvent()!=null)?this.event.getEvent().getID():"" );
			if( this.event.getEvent()==null || this.event.getEvent().getType() == EventTypes.SCHEDULE ) {
				this.pauseEventOperatorCombo.setEnabled( false );
				this.pauseEventLimitCombo.setEnabled( false );
				this.continueCheckBox.setEnabled( false );
			} else {
				this.pauseEventOperatorCombo.setEnabled( true );
				this.pauseEventLimitCombo.setEnabled( true );
				this.continueCheckBox.setEnabled( true );
				pauseEventOperatorCombo.setItems( ComparisonTypes.typeToString( DataTypes.getPossibleComparisonTypes( event.getLimit().getType() ) ) );
				pauseEventLimitCombo.setItems( (event.getEvent().getMonitor().getDataType().getValues()!=null)?event.getEvent().getMonitor().getDataType().getValues().split(","):new String[0] );
				this.pauseEventOperatorCombo.setText( (this.event.getLimit().getComparison()!=null)?ComparisonTypes.typeToString( this.event.getLimit().getComparison() ):"" );
				this.pauseEventLimitCombo.setText( (this.event.getLimit().getValue()!=null)?this.event.getLimit().getValue():"" );
				this.continueCheckBox.setSelection( this.event.isContinueIfFalse() );
				
			}
		} else {
			this.pauseEventCombo.setText( "" );
			this.pauseEventOperatorCombo.setText( "" );
			this.pauseEventLimitCombo.setText( "" );
			this.continueCheckBox.setSelection( false );
		}
		this.filling = false;
	}
	
	
}
