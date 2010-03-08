/*******************************************************************************
 * Copyright (c) 2001, 2007 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.List;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;

import de.ptb.epics.eve.data.scandescription.Axis;

public class MotorAxisStartStopStepwidthComposite extends Composite {

	private Combo startCombo;
	private Combo stopCombo;
	private Text stepwidthText;
	private Text stepamountText;
	private Axis axis;
	private Button mainAxisCheckBox;
	
	private Button autoFillStartRadioButton;
	private Button autoFillStopRadioButton;
	private Button autoFillStepwidthRadioButton;
	private Button autoFillStepamountRadioButton;
	
//	private boolean recursion;
	
	public MotorAxisStartStopStepwidthComposite( final Composite parent, final int style ) {
		super( parent, style );
//		recursion = false;
		initialize();
	}

	private void initialize() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.setLayout( gridLayout );
		
		this.autoFillStartRadioButton = new Button( this, SWT.RADIO );
		this.autoFillStartRadioButton.setText( "Start:" );
		this.autoFillStartRadioButton.setToolTipText( "Mark to enable auto-fill for start value." );
		this.startCombo = new Combo( this, SWT.NONE );
//		this.startCombo.addModifyListener( new ModifyListener() {
//
//			public void modifyText( final ModifyEvent e ) {
//				if( axis != null ) {
//					if (!recursion){
//						recursion = true;
//						startCombo.setText(axis.formatValueDefault(startCombo.getText()));
//						axis.setStart( startCombo.getText() );
//						autoFill();
//						recursion = false;
//					}
//				}
//			}
//			
//		});	
		this.startCombo.addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) {
				
			}
			public void focusLost(FocusEvent e) {
				if( axis != null ) {
					// TODO set application-wide warning background color
					String formattedText = axis.formatValue(startCombo.getText());
					if (formattedText == null){
						formattedText = axis.getDefaultValue();
						startCombo.setBackground(new Color(startCombo.getBackground().getDevice(),255, 255, 0));
					}
					else {
						startCombo.setBackground(new Color(startCombo.getBackground().getDevice(),255, 255, 255));
					}
					startCombo.setText(formattedText);
					axis.setStart(formattedText);
					autoFill();

				}

			}
		});

		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.startCombo.setLayoutData( gridData );
		
		this.autoFillStopRadioButton = new Button( this, SWT.RADIO );
		this.autoFillStopRadioButton.setText( "Stop:" );
		this.autoFillStopRadioButton.setToolTipText( "Mark to enable auto-fill for stop value." );
		this.stopCombo = new Combo( this, SWT.NONE );		
//		this.stopCombo.addModifyListener( new ModifyListener() {
//
//			public void modifyText( final ModifyEvent e ) {
//				if( axis != null ) {
//					axis.setStop( stopCombo.getText() );
//					autoFill();
//				}
//			}
//			
//		});
		this.stopCombo.addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) {
				
			}
			public void focusLost(FocusEvent e) {
				if( axis != null ) {
					// TODO set application-wide warning background color
					String formattedText = axis.formatValue(stopCombo.getText());
					if (formattedText == null){
						formattedText = axis.getDefaultValue();
						stopCombo.setBackground(new Color(stopCombo.getBackground().getDevice(),255, 255, 0));
					}
					else {
						stopCombo.setBackground(new Color(stopCombo.getBackground().getDevice(),255, 255, 255));
					}
					stopCombo.setText(formattedText);
					axis.setStop(formattedText);
					autoFill();
				}

			}
		});
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.stopCombo.setLayoutData( gridData );

		this.autoFillStepwidthRadioButton = new Button( this, SWT.RADIO );
		this.autoFillStepwidthRadioButton.setText( "Stepwidth:" );
		this.autoFillStepwidthRadioButton.setToolTipText( "Mark to enable auto-fill for stepwidth value." );
		this.stepwidthText = new Text( this, SWT.BORDER );
//		this.stepwidthText.addModifyListener( new ModifyListener() {
// 
//			public void modifyText( final ModifyEvent e ) {
//				if( axis != null ) {
//					axis.setStepwidth( stepwidthText.getText() );
//					autoFill();
//				}
//			}
//			
//		});
		this.stepwidthText.addFocusListener( new FocusListener() {
			public void focusGained(FocusEvent e) {
				
			}
			public void focusLost(FocusEvent e) {
				if( axis != null ) {
					stepwidthText.setText(axis.formatValueDefault(stepwidthText.getText()));
					axis.setStepwidth( stepwidthText.getText() );
					autoFill();

				}

			}
		});
		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.stepwidthText.setLayoutData( gridData );
		
		this.autoFillStepamountRadioButton = new Button( this, SWT.RADIO );
		this.autoFillStepamountRadioButton.setText( "Stepcount:" );
		this.autoFillStepamountRadioButton.setToolTipText( "Mark to enable auto-fill for step count." );
		
		this.stepamountText = new Text( this, SWT.BORDER );
		this.stepamountText.addModifyListener( new ModifyListener() {
 
			public void modifyText( final ModifyEvent e ) {
				try {
					axis.setStepCount( Integer.parseInt( stepamountText.getText() ) );
					autoFill();
				} catch( final NumberFormatException ex ) {
					axis.setStepCount( -1 );
				}
				
			}
			
		});
		
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		this.stepamountText.setLayoutData( gridData );
		
		this.mainAxisCheckBox = new Button( this, SWT.CHECK );
		this.mainAxisCheckBox.setText( "main axis" );
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		this.mainAxisCheckBox.setLayoutData( gridData );
		this.mainAxisCheckBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				if( axis != null ) {
					axis.setMainAxis( mainAxisCheckBox.getSelection() );
				}
			}
			
		});
		
		
		this.startCombo.setEnabled( false );
		this.stopCombo.setEnabled( false );
		this.stepwidthText.setEnabled( false );
		this.stepamountText.setEnabled( false );
		this.mainAxisCheckBox.setEnabled( false );
		
	}
	
	public void setAxis( final Axis axis, final int stepamount ) {
		this.axis = axis;
		if( this.axis != null ) {
			if( this.axis.getMotorAxis().getGoto().isDiscrete() ) {
				this.startCombo.setItems( this.axis.getMotorAxis().getGoto().getDiscreteValues().toArray( new String[0] ) );
				this.stopCombo.setItems( this.axis.getMotorAxis().getGoto().getDiscreteValues().toArray( new String[0] ) );
			}
			this.startCombo.setText( this.axis.getStart()!=null?this.axis.getStart():"" );
			this.stopCombo.setText( this.axis.getStop()!=null?this.axis.getStop():"" );
			this.stepwidthText.setText( this.axis.getStepwidth()!=null?this.axis.getStepwidth():"" );
			if( stepamount != -1 && !axis.isMainAxis() ) {
				this.stepamountText.setText( "" + stepamount );
			} else {
				this.stepamountText.setText( "" + this.axis.getStepCount() );
			}
			this.mainAxisCheckBox.setSelection( this.axis.isMainAxis() );
			
			this.startCombo.setEnabled( true );
			this.stopCombo.setEnabled( true );
			this.stepwidthText.setEnabled( true );
			if( stepamount != -1 && !axis.isMainAxis() ) {
				this.stepamountText.setEnabled( false );
				this.autoFillStepamountRadioButton.setEnabled( false );
			} else {
				this.stepamountText.setEnabled( true );
				this.autoFillStepamountRadioButton.setEnabled( true );
			}
			if( this.mainAxisCheckBox.getSelection() || stepamount == -1 ) {
				this.mainAxisCheckBox.setEnabled( true );
			} else {
				
			}
		} else {
			this.startCombo.setEnabled( false );
			this.stopCombo.setEnabled( false );
			this.stepwidthText.setEnabled( false );
			this.stepamountText.setEnabled( false );
			this.mainAxisCheckBox.setEnabled( false );
		}
	}
	
	private void autoFill() {
		if( this.axis != null ) {
			if( this.axis.getStepfunctionString().equals( "Add" ) ) {
				if( this.autoFillStartRadioButton.getSelection() ) {
					if( !this.axis.getMotorAxis().getGoto().isDiscrete() ) {
						if( !this.stopCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) && !this.stepamountText.getText().equals( "" ) ) {
							try {
								final double stop = Double.parseDouble( this.stopCombo.getText() );
								final double stepwidth = Double.parseDouble( this.stepwidthText.getText() );
								final int stepamount = Integer.parseInt( this.stepamountText.getText() );
								
								this.startCombo.setText( "" + (stop - (stepwidth * stepamount) ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					} else {
						if( !this.stopCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) && !this.stepamountText.getText().equals( "" ) ) {
							try {
								List< String > values = this.axis.getMotorAxis().getGoto().getDiscreteValues();
								
								final int stop = values.indexOf( this.stopCombo.getText() );
								final int stepwidth = Integer.parseInt( this.stepwidthText.getText() );
								final int stepamount = Integer.parseInt( this.stepamountText.getText() );
								
								int index = ( stop - (stepwidth * stepamount) );
								if( index < 0 ) {
									index = 0;
								} else if( index >= values.size() ) {
									index = values.size() - 1;
								}
								
								this.startCombo.setText( values.get( index ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					}
				} else if( this.autoFillStopRadioButton.getSelection() ) {
					if( !this.axis.getMotorAxis().getGoto().isDiscrete() ) {
						if( !this.startCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) && !this.stepamountText.getText().equals( "" ) ) {
							try {
								final double start = Double.parseDouble( this.startCombo.getText() );
								final double stepwidth = Double.parseDouble( this.stepwidthText.getText() );
								final int stepamount = Integer.parseInt( this.stepamountText.getText() );
								
								this.stopCombo.setText( "" + (start + (stepwidth * stepamount) ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					} else {
						if( !this.startCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) && !this.stepamountText.getText().equals( "" ) ) {
							try {
								List< String > values = this.axis.getMotorAxis().getGoto().getDiscreteValues();
								
								final int start = values.indexOf( this.startCombo.getText() );
								final int stepwidth = Integer.parseInt( this.stepwidthText.getText() );
								final int stepamount = Integer.parseInt( this.stepamountText.getText() );
								
								int index = ( start + (stepwidth * stepamount) );
								if( index < 0 ) {
									index = 0;
								} else if( index >= values.size() ) {
									index = values.size() - 1;
								}
								
								this.stopCombo.setText( values.get( index ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					}
				} else if( this.autoFillStepwidthRadioButton.getSelection() ) {
					if( !this.axis.getMotorAxis().getGoto().isDiscrete() ) {
						if( !this.startCombo.getText().equals( "" ) && !this.stopCombo.getText().equals( "" ) && !this.stepamountText.getText().equals( "" ) ) {
							try {
								final double start = Double.parseDouble( this.startCombo.getText() );
								final double stop = Double.parseDouble( this.stopCombo.getText() );
								final int stepamount = Integer.parseInt( this.stepamountText.getText() );
								
								this.stepwidthText.setText( "" + ( (stop - start ) / stepamount ) );
							} catch( final NumberFormatException e ) {
								
							}
						} 
					} else {
						if( !this.startCombo.getText().equals( "" ) && !this.stopCombo.getText().equals( "" ) && !this.stepamountText.getText().equals( "" ) ) {
							try {
								List< String > values = this.axis.getMotorAxis().getGoto().getDiscreteValues();
								
								final int start = values.indexOf( this.startCombo.getText() );
								final int stop = values.indexOf( this.stopCombo.getText() );
								final int stepamount = Integer.parseInt( this.stepamountText.getText() );
								
								this.stepwidthText.setText( "" + ((stop - start) / stepamount ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					}
				} else if( this.autoFillStepamountRadioButton.getSelection() ) {
					if( !this.axis.getMotorAxis().getGoto().isDiscrete() ) {
						if( !this.startCombo.getText().equals( "" ) && !this.stopCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) ) {
							try {
								final double start = Double.parseDouble( this.startCombo.getText() );
								final double stop = Double.parseDouble( this.stopCombo.getText() );
								final double stepwidth = Double.parseDouble( this.stepwidthText.getText() );
								
								this.stepamountText.setText( "" + ( (stop - start) / stepwidth ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					} else {
						if( !this.startCombo.getText().equals( "" ) && !this.stopCombo.getText().equals( "" ) && !this.stepwidthText.getText().equals( "" ) ) {
							try {
								List< String > values = this.axis.getMotorAxis().getGoto().getDiscreteValues();
								
								final int start = values.indexOf( this.startCombo.getText() );
								final int stop = values.indexOf( this.stopCombo.getText() );
								final int stepwidth = Integer.parseInt( this.stepwidthText.getText() );
								
								this.stepamountText.setText( "" + (( stop - start) / stepwidth ) );
							} catch( final NumberFormatException e ) {
								
							}
						}
					} 
				}
			} else if( this.axis.getStepfunctionString().equals( "Multiply" ) ) {
				
			}
		}
	}

}
