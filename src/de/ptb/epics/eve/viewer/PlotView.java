package de.ptb.epics.eve.viewer;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import de.ptb.epics.eve.data.measuringstation.DetectorChannel;
import de.ptb.epics.eve.data.scandescription.PlotWindow;
import de.ptb.epics.eve.data.scandescription.YAxis;
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
	
	private Label detector1PeakLabel;
	private Label detector1currentPeakLabel;
	
	private Label detector2PeakLabel;
	private Label detector2currentPeakLabel;
	
	private Label emptyLabel1;
	private Label emptyLabel2;
	
	private Label detector1MeanLabel;
	private Label detector1currentMeanLabel;
	
	private Label detector2MeanLabel;
	private Label detector2currentMeanLabel;
	
	private Label emptyLabel3;
	private Label emptyLabel4;
	
	private Label detector1StandDevLabel;
	private Label detector1currentStandDevLabel;
	
	private Label detector2StandDevLabel;
	private Label detector2currentStandDevLabel;

	private PlotWindow plotWindow;
	
	{
		
	}
	
	@Override
	public void createPartControl( final Composite parent ) {
		
		final GridLayout gridLayout = new GridLayout();
		
		gridLayout.numColumns = 6;

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
		
		this.detector1CurrentValueLabel.setLayoutData( gridData );
		
		this.detector2Label = new Label( parent, SWT.NONE );
		this.detector2Label.setText( "Detector 2:" );
		
		this.detector2CurrentValueLabel = new Label( parent, SWT.NONE );
		this.detector2CurrentValueLabel.setText( "?" );
		
		gridData = new GridData();
		
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		
		this.detector2CurrentValueLabel.setLayoutData( gridData );
		
		this.normalizeLabel = new Label( parent, SWT.NONE );
		this.normalizeLabel.setText( "Normalize:" );
		
		this.normalizeComboBox = new Combo( parent, SWT.NONE );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.normalizeComboBox.setLayoutData( gridData );
		
		this.detector1PeakLabel = new Label( parent, SWT.NONE );
		this.detector1PeakLabel.setText( "Peak:" );
		
		this.detector1currentPeakLabel = new Label( parent, SWT.NONE );
		this.detector1currentPeakLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector1currentPeakLabel.setLayoutData( gridData );
		
		this.detector2PeakLabel = new Label( parent, SWT.NONE );
		this.detector2PeakLabel.setText( "Peak:" );
		
		this.detector2currentPeakLabel = new Label( parent, SWT.NONE );
		this.detector2currentPeakLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector2currentPeakLabel.setLayoutData( gridData );
		
		this.emptyLabel1 = new Label( parent, SWT.NONE );
		this.emptyLabel2 = new Label( parent, SWT.NONE );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.emptyLabel2.setLayoutData( gridData );
		
		
		this.detector1MeanLabel = new Label( parent, SWT.NONE );
		this.detector1MeanLabel.setText( "Mean:" );
		
		this.detector1currentMeanLabel = new Label( parent, SWT.NONE );
		this.detector1currentMeanLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector1currentMeanLabel.setLayoutData( gridData );
		
		this.detector2MeanLabel = new Label( parent, SWT.NONE );
		this.detector2MeanLabel.setText( "Mean:" );
		
		this.detector2currentMeanLabel = new Label( parent, SWT.NONE );
		this.detector2currentMeanLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector2currentMeanLabel.setLayoutData( gridData );
		
		this.emptyLabel3 = new Label( parent, SWT.NONE );
		this.emptyLabel4 = new Label( parent, SWT.NONE );
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.emptyLabel4.setLayoutData( gridData );
		

		this.detector1StandDevLabel = new Label( parent, SWT.NONE );
		this.detector1StandDevLabel.setText( "StandDev:" );
		
		this.detector1currentStandDevLabel = new Label( parent, SWT.NONE );
		this.detector1currentStandDevLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector1currentStandDevLabel.setLayoutData( gridData );
		
		this.detector2StandDevLabel = new Label( parent, SWT.NONE );
		this.detector2StandDevLabel.setText( "StandDev:" );
		
		this.detector2currentStandDevLabel = new Label( parent, SWT.NONE );
		this.detector2currentStandDevLabel.setText( "?" );
		
		gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = SWT.FILL;
		this.detector2currentStandDevLabel.setLayoutData( gridData );
		
	}

	public void measurementData( final MeasurementData measurementData ) {
		if( this.detectorChannel1 != null ) {
			if( this.detectorChannel1.getName().equals( measurementData.getName() ) ) {
				switch( measurementData.getDataModifyer() ) {
					case UNMODIFIED:
						this.detector1CurrentValueLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector1currentPeakLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});
						break;
					case CENTER:
						break;
					case EDGE:
						break;
					case MIN:
						break;
					case MAX:
						this.detector1currentPeakLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector1currentPeakLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});
						break;
					case FWHM:
						break;
					case MEAN_VALUE:
						this.detector1currentMeanLabel.getDisplay().syncExec( new Runnable() {

							public void run() {
								detector1currentMeanLabel.setText( measurementData.getValues().get( 0 ).toString() );
							}
							
						});

						break;
					case STANDARD_DEVIATION:
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
