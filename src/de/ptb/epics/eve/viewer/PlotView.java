package de.ptb.epics.eve.viewer;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.YAxis;
import de.ptb.epics.eve.ecp1.client.interfaces.IMeasurementDataListener;
import de.ptb.epics.eve.ecp1.client.model.MeasurementData;

public class PlotView extends ViewPart {

	protected static final String ID = "PlotView";

	private int id = -1;
	
	private DetectorChannel detectorChannel1 = null;
	private DetectorChannel detectorChannel2 = null;
	
	private Label motorPositionLabel;
	private Label currentMotorPositionLabel;
	private Label detector1Label;
	private Label detector1CurrentValueLabel;
	private Label detector2Label;
	private Label detector2CurrentValueLabel;
	
	private Label normalizeLabel;
	private Combo normalizeComboBox;
	
	private Label detector1MinLabel;
	private Label detector1currentMinLabel;
	private Button detector1GotoMinButton;
	
	private Label detector2MinLabel;
	private Label detector2currentMinLabel;
	private Button detector2GotoMinButton;
	
	private Label emptyLabel1;
	private Label emptyLabel2;
	
	private Label detector1MaxLabel;
	private Label detector1currentMaxLabel;
	private Button detector1GotoMaxButton;
	
	private Label detector2MaxLabel;
	private Label detector2currentMaxLabel;
	private Button detector2GotoMaxButton;
	
	private Label emptyLabel3;
	private Label emptyLabel4;
	
	private Label detector1CenterLabel;
	private Label detector1currentCenterLabel;
	private Button detector1GotoCenterButton;
	
	private Label detector2CenterLabel;
	private Label detector2currentCenterLabel;
	private Button detector2GotoCenterButton;
	
	private Label emptyLabel5;
	private Label emptyLabel6;
	
	private Label detector1EdgeLabel;
	private Label detector1currentEdgeLabel;
	private Button detector1GotoEdgeButton;
	
	private Label detector2EdgeLabel;
	private Label detector2currentEdgeLabel;
	private Button detector2GotoEdgeButton;
	
	private Label emptyLabel7;
	private Label emptyLabel8;
	
	
	private Label detector1MeanLabel;
	private Label detector1currentMeanLabel;
	
	private Label detector2MeanLabel;
	private Label detector2currentMeanLabel;
	
	private Label emptyLabel9;
	private Label emptyLabel10;
	
	private Label detector1StandDevLabel;
	private Label detector1currentStandDevLabel;
	
	private Label detector2StandDevLabel;
	private Label detector2currentStandDevLabel;

	
	private Label emptyLabel11;
	private Label emptyLabel12;
	
	private Label detector1FWHMLabel;
	private Label detector1currentFWHMLabel;
	
	private Label detector2FWHMLabel;
	private Label detector2currentFWHMLabel;
	
	private PlotWindow plotWindow;
	
	{
		
	}
	
	@Override
	public void createPartControl( final Composite parent ) {
		
		final GridLayout gridLayout = new GridLayout();
		
		gridLayout.numColumns = 8;

		parent.setLayout( gridLayout );
		
		this.motorPositionLabel = new Label( parent, SWT.NONE );
		this.motorPositionLabel.setText( "Motor Position:" );
		
		this.currentMotorPositionLabel = new Label( parent, SWT.NONE );
		this.currentMotorPositionLabel.setText( "?" );
		GridData gridData = new GridData();
		
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.currentMotorPositionLabel.setLayoutData( gridData );
		
		this.detector1Label = new Label( parent, SWT.NONE );
		this.detector1Label.setText( "Detector 1:" );
		
		this.detector1CurrentValueLabel = new Label( parent, SWT.NONE );
		this.detector1CurrentValueLabel.setText( "?" );
		
		gridData = new GridData();
		
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		
		this.detector1CurrentValueLabel.setLayoutData( gridData );
		
		this.detector2Label = new Label( parent, SWT.NONE );
		this.detector2Label.setText( "Detector 2:" );
		
		this.detector2CurrentValueLabel = new Label( parent, SWT.NONE );
		this.detector2CurrentValueLabel.setText( "?" );
		
		gridData = new GridData();
		
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		
		this.detector2CurrentValueLabel.setLayoutData( gridData );
		
		this.normalizeLabel = new Label( parent, SWT.NONE );
		this.normalizeLabel.setText( "Normalize:" );
		
		this.normalizeComboBox = new Combo( parent, SWT.NONE );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.normalizeComboBox.setLayoutData( gridData );
		final String[] normalizeItems = { "None", "Detector1 / Detector 2", "Detector 2 / Detector 1" };
		this.normalizeComboBox.setItems( normalizeItems ); 
		
		this.detector1MinLabel = new Label( parent, SWT.NONE );
		this.detector1MinLabel.setText( "Min:" );
		
		this.detector1currentMinLabel = new Label( parent, SWT.NONE );
		this.detector1currentMinLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector1currentMinLabel.setLayoutData( gridData );
		
		this.detector1GotoMinButton = new Button( parent, SWT.NONE );
		this.detector1GotoMinButton.setText( "Goto" );
		
		this.detector2MinLabel = new Label( parent, SWT.NONE );
		this.detector2MinLabel.setText( "Min:" );
		
		this.detector2currentMinLabel = new Label( parent, SWT.NONE );
		this.detector2currentMinLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector2currentMinLabel.setLayoutData( gridData );
		
		this.detector2GotoMinButton = new Button( parent, SWT.NONE );
		this.detector2GotoMinButton.setText( "Goto " );
		
		this.emptyLabel1 = new Label( parent, SWT.NONE );
		this.emptyLabel2 = new Label( parent, SWT.NONE );
		
		this.detector1MaxLabel = new Label( parent, SWT.NONE );
		this.detector1MaxLabel.setText( "Max:" );
		
		this.detector1currentMaxLabel = new Label( parent, SWT.NONE );
		this.detector1currentMaxLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector1currentMaxLabel.setLayoutData( gridData );
		
		this.detector1GotoMaxButton = new Button( parent, SWT.NONE );
		this.detector1GotoMaxButton.setText( "Goto" );
		
		this.detector2MaxLabel = new Label( parent, SWT.NONE );
		this.detector2MaxLabel.setText( "Max:" );
		
		this.detector2currentMaxLabel = new Label( parent, SWT.NONE );
		this.detector2currentMaxLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector2currentMaxLabel.setLayoutData( gridData );
		
		this.detector2GotoMaxButton = new Button( parent, SWT.NONE );
		this.detector2GotoMaxButton.setText( "Goto" );
		
		
		this.emptyLabel3 = new Label( parent, SWT.NONE );
		this.emptyLabel4 = new Label( parent, SWT.NONE );
		
		this.detector1CenterLabel = new Label( parent, SWT.NONE );
		this.detector1CenterLabel.setText( "Center:" );
		
		this.detector1currentCenterLabel = new Label( parent, SWT.NONE );
		this.detector1currentCenterLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector1currentCenterLabel.setLayoutData( gridData );
		
		this.detector1GotoCenterButton = new Button( parent, SWT.NONE );
		this.detector1GotoCenterButton.setText( "Goto" );
		
		this.detector2CenterLabel = new Label( parent, SWT.NONE );
		this.detector2CenterLabel.setText( "Center:" );
		
		this.detector2currentCenterLabel = new Label( parent, SWT.NONE );
		this.detector2currentCenterLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector2currentCenterLabel.setLayoutData( gridData );
		
		this.detector2GotoCenterButton = new Button( parent, SWT.NONE );
		this.detector2GotoCenterButton.setText( "Goto" );
		
		
		this.emptyLabel5 = new Label( parent, SWT.NONE );
		this.emptyLabel6 = new Label( parent, SWT.NONE );
		
		this.detector1EdgeLabel = new Label( parent, SWT.NONE );
		this.detector1EdgeLabel.setText( "Edge:" );
		
		this.detector1currentEdgeLabel = new Label( parent, SWT.NONE );
		this.detector1currentEdgeLabel.setText( "?" );
		
		this.detector1GotoEdgeButton = new Button( parent, SWT.NONE );
		this.detector1GotoEdgeButton.setText( "Goto" );
		
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector1currentEdgeLabel.setLayoutData( gridData );
		
		this.detector2EdgeLabel = new Label( parent, SWT.NONE );
		this.detector2EdgeLabel.setText( "Edge:" );
		
		this.detector2currentEdgeLabel = new Label( parent, SWT.NONE );
		this.detector2currentEdgeLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector2currentCenterLabel.setLayoutData( gridData );
		
		this.detector2GotoEdgeButton = new Button( parent, SWT.NONE );
		this.detector2GotoEdgeButton.setText( "Goto" );
		
		
		this.emptyLabel7 = new Label( parent, SWT.NONE );
		this.emptyLabel8 = new Label( parent, SWT.NONE );
		
		
		this.detector1MeanLabel = new Label( parent, SWT.NONE );
		this.detector1MeanLabel.setText( "Mean:" );
		
		this.detector1currentMeanLabel = new Label( parent, SWT.NONE );
		this.detector1currentMeanLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		this.detector1currentMeanLabel.setLayoutData( gridData );
		
		this.detector2MeanLabel = new Label( parent, SWT.NONE );
		this.detector2MeanLabel.setText( "Mean:" );
		
		this.detector2currentMeanLabel = new Label( parent, SWT.NONE );
		this.detector2currentMeanLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		this.detector2currentMeanLabel.setLayoutData( gridData );
		
		this.emptyLabel9 = new Label( parent, SWT.NONE );
		this.emptyLabel10 = new Label( parent, SWT.NONE );;
		
		this.detector1StandDevLabel = new Label( parent, SWT.NONE );
		this.detector1StandDevLabel.setText( "StandDev:" );
		
		this.detector1currentStandDevLabel = new Label( parent, SWT.NONE );
		this.detector1currentStandDevLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		this.detector1currentStandDevLabel.setLayoutData( gridData );
		
		this.detector2StandDevLabel = new Label( parent, SWT.NONE );
		this.detector2StandDevLabel.setText( "StandDev:" );
		
		this.detector2currentStandDevLabel = new Label( parent, SWT.NONE );
		this.detector2currentStandDevLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		this.detector2currentStandDevLabel.setLayoutData( gridData );
		
		this.emptyLabel11 = new Label( parent, SWT.NONE );
		this.emptyLabel12 = new Label( parent, SWT.NONE );;
		
		this.detector1FWHMLabel = new Label( parent, SWT.NONE );
		this.detector1FWHMLabel.setText( "FWHM:" );
		
		this.detector1currentFWHMLabel = new Label( parent, SWT.NONE );
		this.detector1currentFWHMLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		this.detector1currentFWHMLabel.setLayoutData( gridData );
		
		this.detector2FWHMLabel = new Label( parent, SWT.NONE );
		this.detector2FWHMLabel.setText( "FWHM:" );
		
		this.detector2currentFWHMLabel = new Label( parent, SWT.NONE );
		this.detector2currentFWHMLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		gridData.horizontalSpan = 2;
		this.detector2currentFWHMLabel.setLayoutData( gridData );
		
	}

	public void measurementData( final MeasurementData measurementData ) {
		if( this.detectorChannel1 != null ) {
			if( this.detectorChannel1.getName().equals( measurementData.getName() ) ) {
				switch( measurementData.getDataModifyer() ) {
					case UNMODIFIED:
						this.detector1CurrentValueLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector1CurrentValueLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});
						break;
					case CENTER:
						this.detector1currentCenterLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector1currentCenterLabel.setText( measurementData.getValues().get( 0 ).toString() );
								
							}
							
						});
						break;
					case EDGE:
						this.detector1currentEdgeLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector1currentEdgeLabel.setText( measurementData.getValues().get( 0 ).toString() );
								
							}
							
						});
						break;
						
					case MIN:
						this.detector1currentMinLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector1currentMinLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});
						break;
						
					case MAX:
						this.detector1currentMaxLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector1currentMaxLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});
						break;
					case FWHM:
						this.detector1currentFWHMLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector1currentFWHMLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});
						break;
					case MEAN_VALUE:
						this.detector1currentMeanLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector1currentMeanLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});

						break;
					case STANDARD_DEVIATION:
						this.detector1currentStandDevLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector1currentStandDevLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});

						break;
				}
			}
		} else if( this.detectorChannel2 != null ) {
			if( this.detectorChannel2.getName().equals( measurementData.getName() ) ) {
				switch( measurementData.getDataModifyer() ) {
					case UNMODIFIED:
						this.detector2CurrentValueLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector2CurrentValueLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});
						break;
					case CENTER:
						this.detector2currentCenterLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector2currentCenterLabel.setText( measurementData.getValues().get( 0 ).toString() );
								
							}
							
						});
						break;
					case EDGE:
						this.detector2currentEdgeLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector2currentEdgeLabel.setText( measurementData.getValues().get( 0 ).toString() );
								
							}
							
						});
						break;
						
					case MIN:
						this.detector2currentMinLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector2currentMinLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});
						break;
						
					case MAX:
						this.detector2currentMaxLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector2currentMaxLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});
						break;
					case FWHM:
						this.detector2currentFWHMLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector2currentFWHMLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});
						break;
					case MEAN_VALUE:
						this.detector2currentMeanLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector2currentMeanLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});

						break;
					case STANDARD_DEVIATION:
						this.detector2currentStandDevLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector2currentStandDevLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});

						break;
				}
			}
		}
		
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public int getId() {
		return this.id;
	}
	
	public void setId( final int id ) {
		this.id = id;
	}

	public void setPlotWindow( final PlotWindow plotWindow ) {
		this.plotWindow = plotWindow;
		final Iterator< YAxis > yAxisIterator = this.plotWindow.getYAxisIterator();
		this.detectorChannel1 = yAxisIterator.next().getDetectorChannel();
		if( yAxisIterator.hasNext() ) {
			this.detectorChannel2 = yAxisIterator.next().getDetectorChannel();
		} else {
			this.detectorChannel2 = null;
		}
	}

}
