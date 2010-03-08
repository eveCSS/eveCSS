/*******************************************************************************
 * Copyright (c) 2001, 2008 Physikalisch Technische Bundesanstalt.
 * All rights reserved.
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.ptb.epics.eve.editor.views;

import java.util.Iterator;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;


import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.measuringstation.MotorAxis;
import de.ptb.epics.eve.data.scandescription.Axis;
import de.ptb.epics.eve.data.scandescription.Channel;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.ScanModul;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.data.PlotModes;
import de.ptb.epics.eve.editor.Activator;


public class PlotWindowView extends ViewPart {

	public static final String ID = "de.ptb.epics.eve.editor.views.PlotWindowView";

	private Composite top = null;
	
	private ExpandBar bar = null;
	
	private Composite xAxisComposite = null;
	
	private Composite yAxis1Composite = null;
	
	private Composite yAxis2Composite = null;
	
	private Label plotWindowIDLabel = null;
	
	private Spinner plotWindowIDSpinner = null;
	
	private Label plotWindowIDErrorLabel = null;
	
	private Label motorAxisLabel = null;
	
	private Combo motorAxisComboBox = null;
	
	private Label motorAxisErrorLabel = null;
	
	private Button preInitWindowCheckBox = null;
	
	private Label scaleTypeLabel = null;
	
	private Combo scaleTypeComboBox = null;
	
	private Label scaleTypeErrorLabel = null;
	
	private Label yAxis1DetectorChannelLabel = null;
	
	private Combo yAxis1DetectorChannelComboBox = null;
	
	private Label yAxis1DetectorChannelErrorLabel = null;
	
	private Label yAxis1NormalizeChannelLabel = null;
	private Combo yAxis1NormalizeChannelComboBox = null;
	private Label yAxis1NormalizeChannelErrorLabel = null;

	private Label yAxis1ColorLabel = null;
	
	private Combo yAxis1ColorComboBox = null;
	
	private Label yAxis1ColorErrorLabel = null;
	
	private Label yAxis1LinestyleLabel = null;
	
	private Combo yAxis1LinestyleComboBox = null;
	
	private Label yAxis1LinestyleErrorLabel = null;
	
	private Label yAxis1MarkstyleLabel = null;
	
	private Combo yAxis1MarkstyleComboBox = null;
	
	private Label yAxis1MarkstyleErrorLabel = null;
	
	private Label yAxis1ScaletypeLabel = null;
	
	private Combo yAxis1ScaletypeComboBox = null;
	
	private Label yAxis1ScaletypeErrorLabel = null;
	
	private Label yAxis2DetectorChannelLabel = null;
	
	private Combo yAxis2DetectorChannelComboBox = null;
	
	private Label yAxis2DetectorChannelErrorLabel = null;
	
	private Label yAxis2NormalizeChannelLabel = null;
	private Combo yAxis2NormalizeChannelComboBox = null;
	private Label yAxis2NormalizeChannelErrorLabel = null;

	private Label yAxis2ColorLabel = null;
	
	private Combo yAxis2ColorComboBox = null;
	
	private Label yAxis2ColorErrorLabel = null;
	
	private Label yAxis2LinestyleLabel = null;
	
	private Combo yAxis2LinestyleComboBox = null;
	
	private Label yAxis2LinestyleErrorLabel = null;
	
	private Label yAxis2MarkstyleLabel = null;
	
	private Combo yAxis2MarkstyleComboBox = null;
	
	private Label yAxis2MarkstyleErrorLabel = null;
	
	private Label yAxis2ScaletypeLabel = null;
	
	private Combo yAxis2ScaletypeComboBox = null;
	
	private Label yAxis2ScaletypeErrorLabel = null;
	
	private ExpandItem item0;

	private ExpandItem item1;

	private ExpandItem item2;

	private PlotWindow plotWindow;
	
	private YAxis[] yAxis;

	private ScanModul scanModul;

	@Override
	public void createPartControl( final Composite parent ) {
		
		parent.setLayout(new FillLayout());
		
		if( Activator.getDefault().getMeasuringStation() == null ) {
			final Label errorLabel = new Label( parent, SWT.NONE );
			errorLabel.setText( "No Measuring Station has been loaded. Please check Preferences!" );
			return;
		}
		top = new Composite( parent, SWT.NONE );
		top.setLayout( new GridLayout() );
	
		this.yAxis = new YAxis[ 2 ];
		
		this.bar = new ExpandBar( this.top, SWT.V_SCROLL);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		this.bar.setLayoutData( gridData );
		
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		
		this.xAxisComposite = new Composite( this.bar, SWT.NONE );
		this.xAxisComposite.setLayout( gridLayout );
		
		this.plotWindowIDLabel = new Label( this.xAxisComposite, SWT.NONE );
		this.plotWindowIDLabel.setText( "Plot Window ID:" );
		
		this.plotWindowIDSpinner = new Spinner( this.xAxisComposite, SWT.BORDER );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.plotWindowIDSpinner.setLayoutData( gridData );
		this.plotWindowIDSpinner.addModifyListener( new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				if( plotWindow != null ) {
					plotWindow.setId( plotWindowIDSpinner.getSelection() );
				}
				
			}
			
		});
		
		this.plotWindowIDErrorLabel = new Label( this.xAxisComposite, SWT.NONE );
		
		this.motorAxisLabel = new Label( this.xAxisComposite, SWT.NONE );
		this.motorAxisLabel.setText( "Motor Axis:" );
		
		this.motorAxisComboBox = new Combo( this.xAxisComposite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.motorAxisComboBox.setLayoutData( gridData );
		this.motorAxisComboBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				// TODO Auto-generated method stub
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				if( motorAxisComboBox.getText().equals( "" )) {
					// keine erlaubte Auswahl
					// TODO Wie wird hier eine Fehlerbehandlung durchgeführt? Was muß dabei alles registriert werden?
					throw new IllegalArgumentException( "The parameter 'Motor Axis' must not be null!" );
				}
				else {
					plotWindow.setXAxis( (MotorAxis)Activator.getDefault().getMeasuringStation().getAbstractDeviceByFullIdentifyer( motorAxisComboBox.getText() ) );
				}
			}
		});
		
		this.motorAxisErrorLabel = new Label( this.xAxisComposite, SWT.NONE );
		
		this.preInitWindowCheckBox = new Button( this.xAxisComposite, SWT.CHECK );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		this.preInitWindowCheckBox.setLayoutData( gridData );
		this.preInitWindowCheckBox.setText( "Preinit Window" );
		this.preInitWindowCheckBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				plotWindow.setInit( preInitWindowCheckBox.getSelection() );
			}
			
		});
		
		this.scaleTypeLabel = new Label( this.xAxisComposite, SWT.NONE );
		this.scaleTypeLabel.setText( "Scale Type:" );

		this.scaleTypeComboBox = new Combo( this.xAxisComposite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.scaleTypeComboBox.setLayoutData( gridData );
		this.scaleTypeComboBox.setItems( PlotModes.valuesAsString() );
		this.scaleTypeComboBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				plotWindow.setMode( PlotModes.stringToMode( scaleTypeComboBox.getText()  ) );
				
			}
			
		});

		this.scaleTypeErrorLabel = new Label( this.xAxisComposite, SWT.NONE );
		
		this.yAxis1Composite = new Composite( this.bar, SWT.NONE );
		this.yAxis1Composite.setLayout( gridLayout );
		
		this.yAxis1DetectorChannelLabel = new Label( this.yAxis1Composite, SWT.NONE );
		this.yAxis1DetectorChannelLabel.setText( "Detector Channel:" );
		
		this.yAxis1DetectorChannelComboBox = new Combo( this.yAxis1Composite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis1DetectorChannelComboBox.setLayoutData( gridData );
		this.yAxis1DetectorChannelComboBox.addModifyListener( new ModifyListener() {

			public void modifyText( final ModifyEvent e ) {
				if( yAxis1DetectorChannelComboBox.getText().equals( "" ) || 
					yAxis1DetectorChannelComboBox.getText().equals( "none" ) ) {
					if( yAxis[0] != null ) {
						plotWindow.removeYAxis( yAxis[0] );
						yAxis[0] = null;
					}
					yAxis1NormalizeChannelComboBox.setEnabled( false );
					yAxis1ColorComboBox.setEnabled( false );
					yAxis1LinestyleComboBox.setEnabled( false );
					yAxis1MarkstyleComboBox.setEnabled( false );
					yAxis1ScaletypeComboBox.setEnabled( false );
					
					yAxis1NormalizeChannelComboBox.setText( "none" );
					yAxis1ColorComboBox.setText( "" );
					yAxis1LinestyleComboBox.setText( "" );
					yAxis1MarkstyleComboBox.setText( "" );
					yAxis1ScaletypeComboBox.setText( "" );
				} else {
					if( yAxis[0] == null ) {
						yAxis[0] = new YAxis();
						plotWindow.addYAxis( yAxis[0] );
						// Voreinstellungen für Color, Linestyle und Markstyle wird gesetzt
						yAxis[0].setColor(yAxis1ColorComboBox.getItem(0));
						yAxis[0].setLinestyle(yAxis1LinestyleComboBox.getItem(0));
						yAxis[0].setMarkstyle(yAxis1MarkstyleComboBox.getItem(0));
					}
					yAxis1NormalizeChannelComboBox.setEnabled( true );
					yAxis1ColorComboBox.setEnabled( true );
					yAxis1LinestyleComboBox.setEnabled( true );
					yAxis1MarkstyleComboBox.setEnabled( true );
					yAxis1ScaletypeComboBox.setEnabled( true );
					
					yAxis1ColorComboBox.setText( yAxis[0].getColor() );
					yAxis1LinestyleComboBox.setText( yAxis[0].getLinestyle() );
					yAxis1MarkstyleComboBox.setText( yAxis[0].getMarkstyle() );
					yAxis1ScaletypeComboBox.setText( PlotModes.modeToString( yAxis[0].getMode() ) );
					
					yAxis[0].setDetectorChannel( (DetectorChannel)Activator.getDefault().getMeasuringStation().getAbstractDeviceByFullIdentifyer( yAxis1DetectorChannelComboBox.getText() ) );
				}
			}
			
		});

		this.yAxis1DetectorChannelErrorLabel = new Label( this.yAxis1Composite, SWT.NONE );
		
		this.yAxis1NormalizeChannelLabel = new Label( this.yAxis1Composite, SWT.NONE );
		this.yAxis1NormalizeChannelLabel.setText( "Normalize Channel:" );
		
		this.yAxis1NormalizeChannelComboBox = new Combo( this.yAxis1Composite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis1NormalizeChannelComboBox.setLayoutData( gridData );
		this.yAxis1NormalizeChannelComboBox.addModifyListener( new ModifyListener() {

			public void modifyText( final ModifyEvent e ) {
				if( yAxis1NormalizeChannelComboBox.getText().equals( "" ) ) {
				} else if( yAxis1NormalizeChannelComboBox.getText().equals( "none" ) ) {
					if (yAxis[0] != null)
						yAxis[0].clearNormalizeChannel();
				} else {
					yAxis[0].setNormalizeChannel( (DetectorChannel)Activator.getDefault().getMeasuringStation().getAbstractDeviceByFullIdentifyer( yAxis1NormalizeChannelComboBox.getText() ) );
				}
			}
			
		});
		
		this.yAxis1NormalizeChannelErrorLabel = new Label( this.yAxis1Composite, SWT.NONE );
		
		this.yAxis1ColorLabel = new Label( this.yAxis1Composite, SWT.NONE );
		this.yAxis1ColorLabel.setText( "Color:" );
		
		this.yAxis1ColorComboBox = new Combo( this.yAxis1Composite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis1ColorComboBox.setLayoutData( gridData );
		this.yAxis1ColorComboBox.setItems( Activator.getDefault().getMeasuringStation().getSelections().getColors() );
		this.yAxis1ColorComboBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				if( yAxis[ 0 ] != null ) {
					yAxis[ 0 ].setColor( yAxis1ColorComboBox.getText() );
				}
			}
			
		});
		
		this.yAxis1ColorErrorLabel = new Label( this.yAxis1Composite, SWT.NONE );
		
		this.yAxis1LinestyleLabel = new Label( this.yAxis1Composite, SWT.NONE );
		this.yAxis1LinestyleLabel.setText( "Linestyle:" );
		
		this.yAxis1LinestyleComboBox = new Combo( this.yAxis1Composite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis1LinestyleComboBox.setLayoutData( gridData );
		this.yAxis1LinestyleComboBox.setItems( Activator.getDefault().getMeasuringStation().getSelections().getLinestyles() );
		this.yAxis1LinestyleComboBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				if( yAxis[ 0 ] != null ) {
					yAxis[ 0 ].setLinestyle( yAxis1LinestyleComboBox.getText() );
				}
				
			}
			
		});
		
		this.yAxis1LinestyleErrorLabel = new Label( this.yAxis1Composite, SWT.NONE );
		
		this.yAxis1MarkstyleLabel = new Label( this.yAxis1Composite, SWT.NONE );
		this.yAxis1MarkstyleLabel.setText( "Markstyle:" );
		
		this.yAxis1MarkstyleComboBox = new Combo( this.yAxis1Composite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis1MarkstyleComboBox.setLayoutData( gridData );
		this.yAxis1MarkstyleComboBox.setItems( Activator.getDefault().getMeasuringStation().getSelections().getMarkstyles() );
		this.yAxis1MarkstyleComboBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				if( yAxis[ 0 ] != null ) {
					yAxis[ 0 ].setMarkstyle( yAxis1MarkstyleComboBox.getText() );
				}
				
			}
			
		});
		
		this.yAxis1MarkstyleErrorLabel = new Label( this.yAxis1Composite, SWT.NONE );
		
		this.yAxis1ScaletypeLabel = new Label( this.yAxis1Composite, SWT.NONE );
		this.yAxis1ScaletypeLabel.setText( "Scaletype:" );
		
		this.yAxis1ScaletypeComboBox = new Combo( this.yAxis1Composite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis1ScaletypeComboBox.setLayoutData( gridData );
		this.yAxis1ScaletypeComboBox.setItems( PlotModes.valuesAsString() );
		this.yAxis1ScaletypeComboBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				if( yAxis[ 0 ] != null ) {
					yAxis[ 0 ].setMode( PlotModes.stringToMode( yAxis1ScaletypeComboBox.getText() ) );
				}
				
			}
			
		});
		
		this.yAxis1ScaletypeErrorLabel = new Label( this.yAxis1Composite, SWT.NONE );
		
		
		this.yAxis2Composite = new Composite( this.bar, SWT.NONE );
		this.yAxis2Composite.setLayout( gridLayout );
		
		this.yAxis2DetectorChannelLabel = new Label( this.yAxis2Composite, SWT.NONE );
		this.yAxis2DetectorChannelLabel.setText( "Detector Channel:" );
		
		this.yAxis2DetectorChannelComboBox = new Combo( this.yAxis2Composite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis2DetectorChannelComboBox.setLayoutData( gridData );
		this.yAxis2DetectorChannelComboBox.addModifyListener( new ModifyListener() {

			public void modifyText( final ModifyEvent e ) {
				if( yAxis2DetectorChannelComboBox.getText().equals( "" ) ||
					yAxis2DetectorChannelComboBox.getText().equals( "none" ) ) {
					if( yAxis[1] != null ) {
						plotWindow.removeYAxis( yAxis[1] );
						yAxis[1] = null;
					}
					yAxis2NormalizeChannelComboBox.setEnabled( false );
					yAxis2ColorComboBox.setEnabled( false );
					yAxis2LinestyleComboBox.setEnabled( false );
					yAxis2MarkstyleComboBox.setEnabled( false );
					yAxis2ScaletypeComboBox.setEnabled( false );
					
					yAxis2NormalizeChannelComboBox.setText( "none" );
					yAxis2ColorComboBox.setText( "" );
					yAxis2LinestyleComboBox.setText( "" );
					yAxis2MarkstyleComboBox.setText( "" );
					yAxis2ScaletypeComboBox.setText( "" );
				} else {
					if( yAxis[1] == null ) {
						yAxis[1] = new YAxis();
						plotWindow.addYAxis( yAxis[1] );
						// Voreinstellungen für Color, Linestyle und Markstyle wird gesetzt
						if (yAxis2ColorComboBox.getItemCount() > 1)
							yAxis[1].setColor(yAxis2ColorComboBox.getItem(1));
						else
							yAxis[1].setColor(yAxis2ColorComboBox.getItem(0));
						if (yAxis2LinestyleComboBox.getItemCount() > 1)
							yAxis[1].setLinestyle(yAxis2LinestyleComboBox.getItem(1));
						else
							yAxis[1].setLinestyle(yAxis2LinestyleComboBox.getItem(0));
						if (yAxis2MarkstyleComboBox.getItemCount() > 1)
							yAxis[1].setMarkstyle(yAxis2MarkstyleComboBox.getItem(1));
						else
							yAxis[1].setMarkstyle(yAxis2MarkstyleComboBox.getItem(0));
					}
					yAxis2NormalizeChannelComboBox.setEnabled( true );
					yAxis2ColorComboBox.setEnabled( true );
					yAxis2LinestyleComboBox.setEnabled( true );
					yAxis2MarkstyleComboBox.setEnabled( true );
					yAxis2ScaletypeComboBox.setEnabled( true );
					
					yAxis2ColorComboBox.setText( yAxis[1].getColor() );
					yAxis2LinestyleComboBox.setText( yAxis[1].getLinestyle() );
					yAxis2MarkstyleComboBox.setText( yAxis[1].getMarkstyle() );
					yAxis2ScaletypeComboBox.setText( PlotModes.modeToString( yAxis[1].getMode() ) );
					
					yAxis[1].setDetectorChannel( (DetectorChannel)Activator.getDefault().getMeasuringStation().getAbstractDeviceByFullIdentifyer( yAxis2DetectorChannelComboBox.getText() ) );
				}
			}
		});
		
		this.yAxis2DetectorChannelErrorLabel = new Label( this.yAxis2Composite, SWT.NONE );
		
		this.yAxis2NormalizeChannelLabel = new Label( this.yAxis2Composite, SWT.NONE );
		this.yAxis2NormalizeChannelLabel.setText( "Normalize Channel:" );
		
		this.yAxis2NormalizeChannelComboBox = new Combo( this.yAxis2Composite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis2NormalizeChannelComboBox.setLayoutData( gridData );
		this.yAxis2NormalizeChannelComboBox.addModifyListener( new ModifyListener() {

			public void modifyText( final ModifyEvent e ) {
				if( yAxis2NormalizeChannelComboBox.getText().equals( "" ) ) {
				} else if( yAxis2NormalizeChannelComboBox.getText().equals( "none" ) ) {
					if (yAxis[1] != null)
						yAxis[1].clearNormalizeChannel();
				} else {
					yAxis[1].setNormalizeChannel( (DetectorChannel)Activator.getDefault().getMeasuringStation().getAbstractDeviceByFullIdentifyer( yAxis2NormalizeChannelComboBox.getText() ) );
				}
			}
			
		});

		
		this.yAxis2NormalizeChannelErrorLabel = new Label( this.yAxis2Composite, SWT.NONE );
		
		this.yAxis2ColorLabel = new Label( this.yAxis2Composite, SWT.NONE );
		this.yAxis2ColorLabel.setText( "Color:" );
		
		this.yAxis2ColorComboBox = new Combo( this.yAxis2Composite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis2ColorComboBox.setLayoutData( gridData );
		this.yAxis2ColorComboBox.setItems( Activator.getDefault().getMeasuringStation().getSelections().getColors() );
		this.yAxis2ColorComboBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				if( yAxis[ 1 ] != null ) {
					yAxis[ 1 ].setColor( yAxis2ColorComboBox.getText() );
				}
			}
			
		});
		
		this.yAxis2ColorErrorLabel = new Label( this.yAxis2Composite, SWT.NONE );
		
		this.yAxis2LinestyleLabel = new Label( this.yAxis2Composite, SWT.NONE );
		this.yAxis2LinestyleLabel.setText( "Linestyle:" );
		
		this.yAxis2LinestyleComboBox = new Combo( this.yAxis2Composite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis2LinestyleComboBox.setLayoutData( gridData );
		this.yAxis2LinestyleComboBox.setItems( Activator.getDefault().getMeasuringStation().getSelections().getLinestyles() );
		this.yAxis2LinestyleComboBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				if( yAxis[ 1 ] != null ) {
					yAxis[ 1 ].setLinestyle( yAxis2LinestyleComboBox.getText() );
				}
				
			}
			
		});
		
		
		this.yAxis2LinestyleErrorLabel = new Label( this.yAxis2Composite, SWT.NONE );
		
		this.yAxis2MarkstyleLabel = new Label( this.yAxis2Composite, SWT.NONE );
		this.yAxis2MarkstyleLabel.setText( "Markstyle:" );
		
		this.yAxis2MarkstyleComboBox = new Combo( this.yAxis2Composite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis2MarkstyleComboBox.setLayoutData( gridData );
		this.yAxis2MarkstyleComboBox.setItems( Activator.getDefault().getMeasuringStation().getSelections().getMarkstyles() );
		this.yAxis2MarkstyleComboBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				if( yAxis[ 1 ] != null ) {
					yAxis[ 1 ].setMarkstyle( yAxis2MarkstyleComboBox.getText() );
				}
				
			}
			
		});
		
		
		this.yAxis2MarkstyleErrorLabel = new Label( this.yAxis2Composite, SWT.NONE );
		
		this.yAxis2ScaletypeLabel = new Label( this.yAxis2Composite, SWT.NONE );
		this.yAxis2ScaletypeLabel.setText( "Scaletype:" );
	
		this.yAxis2ScaletypeComboBox = new Combo( this.yAxis2Composite, SWT.READ_ONLY );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		this.yAxis2ScaletypeComboBox.setLayoutData( gridData );
		this.yAxis2ScaletypeComboBox.setItems( PlotModes.valuesAsString() );
		this.yAxis2ScaletypeComboBox.addSelectionListener( new SelectionListener() {

			public void widgetDefaultSelected( final SelectionEvent e ) {
				
				
			}

			public void widgetSelected( final SelectionEvent e ) {
				if( yAxis[ 1 ] != null ) {
					yAxis[ 1 ].setMode( PlotModes.stringToMode( yAxis2ScaletypeComboBox.getText() ) );
				}
				
			}
			
		});
		
		this.yAxis2ScaletypeErrorLabel = new Label( this.yAxis2Composite, SWT.NONE );
		
		this.item0 = new ExpandItem ( this.bar, SWT.NONE, 0);
		item0.setText("General");
		item0.setHeight( this.xAxisComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item0.setControl( this.xAxisComposite );
		
		
		this.item1 = new ExpandItem ( this.bar, SWT.NONE, 0);
		item1.setText("Y-Axis 1");
		item1.setHeight( this.yAxis1Composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item1.setControl( this.yAxis1Composite );
		
		this.item2 = new ExpandItem ( this.bar, SWT.NONE, 0);
		item2.setText("Y-Axis 2");
		item2.setHeight( this.yAxis2Composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item2.setControl( this.yAxis2Composite );
		
		
		this.setEnabledForAll(false);
	}

	private void setEnabledForAll( final boolean enabled ) {
		this.plotWindowIDSpinner.setEnabled( enabled );
		this.motorAxisComboBox.setEnabled( enabled );
		this.preInitWindowCheckBox.setEnabled( enabled );
		this.scaleTypeComboBox.setEnabled( enabled );
		
		this.yAxis1DetectorChannelComboBox.setEnabled( enabled );
		/*this.yAxis1NormalizeChannelCheckBox.setEnabled( enabled );
		this.yAxis1ColorComboBox.setEnabled( enabled );
		this.yAxis1LinestyleComboBox.setEnabled( enabled );
		this.yAxis1MarkstyleComboBox.setEnabled( enabled );
		this.yAxis1ScaletypeComboBox.setEnabled( enabled );*/
		
		this.yAxis2DetectorChannelComboBox.setEnabled( enabled );
		/*this.yAxis2NormalizeChannelCheckBox.setEnabled( enabled );
		this.yAxis2ColorComboBox.setEnabled( enabled );
		this.yAxis2LinestyleComboBox.setEnabled( enabled );
		this.yAxis2MarkstyleComboBox.setEnabled( enabled );
		this.yAxis2ScaletypeComboBox.setEnabled( enabled );*/
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public void setPlotWindow( final PlotWindow plotWindow ) {
		if( this.plotWindow != null && plotWindow != null ) {
			this.plotWindow.clearYAxis();
			if( this.yAxis[ 0 ] != null ) {
				this.plotWindow.addYAxis( this.yAxis[ 0 ] );
				this.yAxis[ 0 ] = null;
			}
			if( this.yAxis[ 1 ] != null ) {
				this.plotWindow.addYAxis( this.yAxis[ 1 ] );
				this.yAxis[ 1 ] = null;
			}
			
		}
		this.plotWindow = plotWindow;
		if( this.plotWindow != null ) {
			int i = 0;
			for( Iterator< YAxis > it = this.plotWindow.getYAxisIterator(); it.hasNext(); ++i ) {
				this.yAxis[ i ] = it.next();
			}
			
			this.plotWindowIDSpinner.setSelection( this.plotWindow.getId() );
			this.motorAxisComboBox.setText( this.plotWindow.getXAxis()!=null?plotWindow.getXAxis().getFullIdentifyer():"" );
			this.preInitWindowCheckBox.setSelection( this.plotWindow.isInit() );
			this.scaleTypeComboBox.setText( PlotModes.modeToString( this.plotWindow.getMode() ) );
			if( this.yAxis[ 0 ] != null ) {
				this.yAxis1DetectorChannelComboBox.setText( yAxis[ 0 ].getDetectorChannel().getFullIdentifyer() );
				if (yAxis[0].getNormalizeChannel() != null)
					this.yAxis1NormalizeChannelComboBox.setText( yAxis[ 0 ].getNormalizeChannel().getFullIdentifyer() );
				else
					this.yAxis1NormalizeChannelComboBox.setText("none");
			} else {
				this.yAxis1DetectorChannelComboBox.setText( "none" );
			}
						
			if( this.yAxis[ 1 ] != null ) {
				this.yAxis2DetectorChannelComboBox.setText( yAxis[ 1 ].getDetectorChannel().getFullIdentifyer() );
				if (yAxis[1].getNormalizeChannel() != null)
					this.yAxis2NormalizeChannelComboBox.setText( yAxis[ 1 ].getNormalizeChannel().getFullIdentifyer() );
				else
					this.yAxis2NormalizeChannelComboBox.setText("none");
			}  else {
				this.yAxis2DetectorChannelComboBox.setText( "none" );
			}
			this.setEnabledForAll( true );
		} else {
			//TODO Frage an Stephan: warum wird hier kein removeYAxis() oder clearYAxis() aufgerufen werden?
			this.yAxis[ 0 ] = null;
			this.yAxis[ 1 ] = null;
			this.plotWindowIDSpinner.setSelection( 0 );
			
			this.preInitWindowCheckBox.setSelection( false );
			this.scaleTypeComboBox.setText( "" );

			/* Durch den Aufruf von setScanModul werden die Inhalte der 3 ComboBoxen neu gesetzt und brauchen hier nicht
			 * eingestellt zu werden.
			 */
//			this.motorAxisComboBox.setText( "" );
//			this.yAxis1DetectorChannelComboBox.setText( "none" );
//			this.yAxis2DetectorChannelComboBox.setText( "none" );

			this.setEnabledForAll( false );
		}
	}

	public void setScanModul(ScanModul scanModul) {
		// TODO Auto-generated method stub
		if( scanModul != null ) {
			// Es werden nur die Achsen erlaubt die in diesem ScanModul verwendet werden.
			Axis[] cur_axis = scanModul.getAxis();
			String[] cur_axis_feld = new String[cur_axis.length];
			for (int i=0; i<cur_axis.length; ++i) {
				cur_axis_feld[i] = cur_axis[i].getMotorAxis().getFullIdentifyer();
			}
			this.motorAxisComboBox.setItems(cur_axis_feld);
			this.motorAxisComboBox.add("", 0);	
		
			// Es werden nur die Channels erlaubt die in diesem ScanModul verwendet werden.
			Channel[] cur_channel = scanModul.getChannels();
			String[] cur_ch_feld = new String[cur_channel.length];
			for (int i=0; i<cur_channel.length; ++i) {
				cur_ch_feld[i] = cur_channel[i].getDetectorChannel().getFullIdentifyer();
			}
			this.yAxis1DetectorChannelComboBox.setItems(cur_ch_feld);
			this.yAxis1DetectorChannelComboBox.add("none", 0);	
			this.yAxis1NormalizeChannelComboBox.setItems(cur_ch_feld);
			this.yAxis1NormalizeChannelComboBox.add("none", 0);	
			this.yAxis2DetectorChannelComboBox.setItems(cur_ch_feld);
			this.yAxis2DetectorChannelComboBox.add("none", 0);	
			this.yAxis2NormalizeChannelComboBox.setItems(cur_ch_feld);
			this.yAxis2NormalizeChannelComboBox.add("none", 0);	

		}
		this.scanModul = scanModul;
	}

}
