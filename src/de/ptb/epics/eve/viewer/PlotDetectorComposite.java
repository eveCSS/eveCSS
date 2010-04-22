/**
 * 
 */
package de.ptb.epics.eve.viewer;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ValueType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.ptb.epics.eve.ecp1.client.interfaces.IChainStatusListener;
import de.ptb.epics.eve.ecp1.intern.ChainStatus;
import de.ptb.epics.eve.ecp1.intern.ChainStatusCommand;
import de.ptb.epics.eve.ecp1.intern.DataModifier;

/**
 * @author eden
 *
 */
public class PlotDetectorComposite extends Composite implements IChainStatusListener{

	private Label detector1MinLabel;
	private EngineDataLabel detector1EngineMinLabel;
	private Button detector1GotoMinButton;
	private Label detector1MaxLabel;
	private EngineDataLabel detector1EngineMaxLabel;
	private Button detector1GotoMaxButton;
	private Label detector1CenterLabel;
	private EngineDataLabel detector1EngineCenterLabel;
	private Button detector1GotoCenterButton;
	private Label detector1EdgeLabel;
	private EngineDataLabel detector1EngineEdgeLabel;
	private Button detector1GotoEdgeButton;
	private Label detector1AverageLabel;
	private EngineDataLabel detector1EngineAverageLabel;
	private Label detector1DeviationLabel;
	private EngineDataLabel detector1EngineDeviationLabel;
	private Label detector1FWHMLabel;
	private EngineDataLabel detector1EngineFWHMLabel;

	private Label detector2MinLabel;
	private EngineDataLabel detector2EngineMinLabel;
	private Button detector2GotoMinButton;
	private Label detector2MaxLabel;
	private EngineDataLabel detector2EngineMaxLabel;
	private Button detector2GotoMaxButton;
	private Label detector2CenterLabel;
	private EngineDataLabel detector2EngineCenterLabel;
	private Button detector2GotoCenterButton;
	private Label detector2EdgeLabel;
	private EngineDataLabel detector2EngineEdgeLabel;
	private Button detector2GotoEdgeButton;
	private Label detector2AverageLabel;
	private EngineDataLabel detector2EngineAverageLabel;
	private Label detector2DeviationLabel;
	private EngineDataLabel detector2EngineDeviationLabel;
	private Label detector2FWHMLabel;
	private EngineDataLabel detector2EngineFWHMLabel;

	private String motorId;
	private String detector1Id;
	private String detector2Id;
	private String motorPv;
	private int chid;
	private int smid;

	public PlotDetectorComposite(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub

		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		setLayout( gridLayout );

	}
	
	protected void gotoPosition(String text) {

		if ((motorPv == null) || (text == null) || (text.length() == 0)) return;
		
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance();
		String prefix = "dal-epics://";
		IProcessVariableAddress pv = pvFactory.createProcessVariableAdress("dal-epics://" + motorPv);

		try {
			service.writeValueSynchronously( pv, text, ValueType.STRING );
		} catch( final ConnectionException e1 ) {
			e1.printStackTrace();
		}
		
	}

	public void refresh(int chid, int smid, String motorId, String motorPv, String detector1Id, String detector2Id) {
		
		
		this.detector1Id = detector1Id;
		this.detector2Id = detector2Id;
		this.motorId = motorId;
		this.motorPv = motorPv;
		
		Font newFont = Activator.getDefault().getFont("VIEWERFONT");
		final Image gotoIcon = Activator.getDefault().getImageRegistry().get("GREENGO12");

		GridData gridData;

		// detector 1
		if (detector1Id != null){
			// Min
			if (this.detector1MinLabel == null) {
				this.detector1MinLabel = new Label( this, SWT.NONE );
				this.detector1MinLabel.setFont(newFont);
				this.detector1MinLabel.setText( "Min:" );
			}
			if (this.detector1EngineMinLabel == null) {
				this.detector1EngineMinLabel = new EngineDataLabel( this, SWT.NONE, detector1Id );
				this.detector1EngineMinLabel.setFont(newFont);
				this.detector1EngineMinLabel.setDataModifier(DataModifier.MIN);
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				this.detector1EngineMinLabel.setLayoutData( gridData );
			}
			else {
				this.detector1EngineMinLabel.setDataId(detector1Id);
				this.detector1EngineMinLabel.setText("          ");
			}
			if (this.detector1GotoMinButton == null) {
				this.detector1GotoMinButton = new Button( this, SWT.NONE );
				this.detector1GotoMinButton.setImage(gotoIcon);
				this.detector1GotoMinButton.addSelectionListener( new SelectionListener() {
				
					@Override
					public void widgetSelected(SelectionEvent e) {
						gotoPosition(detector1EngineMinLabel.getText());
					}
	
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
			}
			
			// Max
			if (this.detector1MaxLabel == null) {
				this.detector1MaxLabel = new Label( this, SWT.NONE );
				this.detector1MaxLabel.setText( "Max:" );
				this.detector1MaxLabel.setFont(newFont);
			}
			if (this.detector1EngineMaxLabel == null) {
				this.detector1EngineMaxLabel = new EngineDataLabel( this, SWT.NONE, detector1Id );
				this.detector1EngineMaxLabel.setFont(newFont);
				this.detector1EngineMaxLabel.setDataModifier(DataModifier.MAX);
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				gridData.verticalAlignment = SWT.FILL;
				this.detector1EngineMaxLabel.setLayoutData( gridData );
			}
			else {
				this.detector1EngineMaxLabel.setDataId(detector1Id);
				this.detector1EngineMaxLabel.setText("          ");
			}
			if (this.detector1GotoMaxButton == null) {
				this.detector1GotoMaxButton = new Button( this, SWT.NONE );
				this.detector1GotoMaxButton.setImage(gotoIcon);
				this.detector1GotoMaxButton.addSelectionListener( new SelectionListener() {
				
					@Override
					public void widgetSelected(SelectionEvent e) {
						gotoPosition(detector1EngineMaxLabel.getText());
					}
	
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
			}
			
			// Center
			if (this.detector1CenterLabel == null) {
				this.detector1CenterLabel = new Label( this, SWT.NONE );
				this.detector1CenterLabel.setText( "Center:" );
				this.detector1CenterLabel.setFont(newFont);
			}
			if (this.detector1EngineCenterLabel == null) {
				this.detector1EngineCenterLabel = new EngineDataLabel( this, SWT.NONE, detector1Id );
				this.detector1EngineCenterLabel.setFont(newFont);
				this.detector1EngineCenterLabel.setDataModifier(DataModifier.CENTER);
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				this.detector1EngineCenterLabel.setLayoutData( gridData );
			}
			else {
				this.detector1EngineCenterLabel.setDataId(detector1Id);
				this.detector1EngineCenterLabel.setText("          ");
			}
			if (this.detector1GotoCenterButton == null) {
				this.detector1GotoCenterButton = new Button( this, SWT.NONE );
				this.detector1GotoCenterButton.setImage(gotoIcon);
				this.detector1GotoCenterButton.addSelectionListener( new SelectionListener() {
				
					@Override
					public void widgetSelected(SelectionEvent e) {
						gotoPosition(detector1EngineCenterLabel.getText());
					}
	
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
			}
			
			// Edge
			if (this.detector1EdgeLabel == null) {
				this.detector1EdgeLabel = new Label( this, SWT.NONE );
				this.detector1EdgeLabel.setText( "Edge:" );
				this.detector1EdgeLabel.setFont(newFont);
			}
			if (this.detector1EngineEdgeLabel == null) {
				this.detector1EngineEdgeLabel = new EngineDataLabel( this, SWT.NONE, detector1Id );
				this.detector1EngineEdgeLabel.setFont(newFont);
				this.detector1EngineEdgeLabel.setDataModifier(DataModifier.EDGE);
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				this.detector1EngineEdgeLabel.setLayoutData( gridData );
			}
			else {
				this.detector1EngineEdgeLabel.setDataId(detector1Id);
				this.detector1EngineEdgeLabel.setText("          ");
			}
			if (this.detector1GotoEdgeButton == null) {
				this.detector1GotoEdgeButton = new Button( this, SWT.NONE );
				this.detector1GotoEdgeButton.setImage(gotoIcon);
				this.detector1GotoEdgeButton.addSelectionListener( new SelectionListener() {
				
					@Override
					public void widgetSelected(SelectionEvent e) {
						gotoPosition(detector1EngineEdgeLabel.getText());
					}
	
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
			}
			// Average
			if (this.detector1AverageLabel == null) {
				this.detector1AverageLabel = new Label( this, SWT.NONE );
				this.detector1AverageLabel.setText( "Average:" );
				this.detector1AverageLabel.setFont(newFont);
			}
			if (this.detector1EngineAverageLabel == null) {
				this.detector1EngineAverageLabel = new EngineDataLabel( this, SWT.NONE, detector1Id );
				this.detector1EngineAverageLabel.setFont(newFont);
				this.detector1EngineAverageLabel.setDataModifier(DataModifier.MEAN_VALUE);
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				gridData.horizontalSpan = 2;
				this.detector1EngineAverageLabel.setLayoutData( gridData );
			}
			else {
				this.detector1EngineAverageLabel.setDataId(detector1Id);
				this.detector1EngineAverageLabel.setText("          ");
			}

			// deviation
			if (this.detector1DeviationLabel == null) {
				this.detector1DeviationLabel = new Label( this, SWT.NONE );
				this.detector1DeviationLabel.setText( "Deviation:" );
				this.detector1DeviationLabel.setFont(newFont);
			}
			if (this.detector1EngineDeviationLabel == null) {
				this.detector1EngineDeviationLabel = new EngineDataLabel( this, SWT.NONE, detector1Id );
				this.detector1EngineDeviationLabel.setFont(newFont);
				this.detector1EngineDeviationLabel.setDataModifier(DataModifier.STANDARD_DEVIATION);
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				gridData.horizontalSpan = 2;
				this.detector1EngineDeviationLabel.setLayoutData( gridData );
			}
			else {
				this.detector1EngineDeviationLabel.setDataId(detector1Id);
				this.detector1EngineDeviationLabel.setText("          ");
			}
			// FWHM
			if (this.detector1FWHMLabel == null) {
				this.detector1FWHMLabel = new Label( this, SWT.NONE );
				this.detector1FWHMLabel.setText( "FWHM:" );
				this.detector1FWHMLabel.setFont(newFont);
			}
			if (this.detector1EngineFWHMLabel == null) {
				this.detector1EngineFWHMLabel = new EngineDataLabel( this, SWT.NONE, detector1Id );
				this.detector1EngineFWHMLabel.setFont(newFont);
				this.detector1EngineFWHMLabel.setDataModifier(DataModifier.FWHM);
				gridData = new GridData();
				gridData.horizontalSpan = 2;
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				this.detector1EngineFWHMLabel.setLayoutData( gridData );
			}
			else {
				this.detector1EngineFWHMLabel.setDataId(detector1Id);
				this.detector1EngineFWHMLabel.setText("          ");
			}
		}
		else {
			if (detector1MinLabel != null) { detector1MinLabel.dispose(); detector1MinLabel = null; }
			if (detector1EngineMinLabel != null) { detector1EngineMinLabel.dispose(); detector1EngineMinLabel = null; }
			if (detector1GotoMinButton != null) { detector1GotoMinButton.dispose(); detector1GotoMinButton = null; }
			if (detector1MaxLabel != null) { detector1MaxLabel.dispose(); detector1MaxLabel = null; }
			if (detector1EngineMaxLabel != null) { detector1EngineMaxLabel.dispose(); detector1EngineMaxLabel = null; }
			if (detector1GotoMaxButton != null) { detector1GotoMaxButton.dispose(); detector1GotoMaxButton = null; }
			if (detector1CenterLabel != null) { detector1CenterLabel.dispose(); detector1CenterLabel = null; }
			if (detector1EngineCenterLabel != null) { detector1EngineCenterLabel.dispose(); detector1EngineCenterLabel = null; }
			if (detector1GotoCenterButton != null) { detector1GotoCenterButton.dispose(); detector1GotoCenterButton = null; }
			if (detector1EdgeLabel != null) { detector1EdgeLabel.dispose(); detector1EdgeLabel = null; }
			if (detector1EngineEdgeLabel != null) { detector1EngineEdgeLabel.dispose(); detector1EngineEdgeLabel = null; }
			if (detector1GotoEdgeButton != null) { detector1GotoEdgeButton.dispose(); detector1GotoEdgeButton = null; }
			if (detector1AverageLabel != null) { detector1AverageLabel.dispose(); detector1AverageLabel = null; }
			if (detector1EngineAverageLabel != null) { detector1EngineAverageLabel.dispose(); detector1EngineAverageLabel = null; }
			if (detector1DeviationLabel != null) { detector1DeviationLabel.dispose(); detector1DeviationLabel = null; }
			if (detector1EngineDeviationLabel != null) { detector1EngineDeviationLabel.dispose(); detector1EngineDeviationLabel = null; }
			if (detector1FWHMLabel != null) { detector1FWHMLabel.dispose(); detector1FWHMLabel = null; }
			if (detector1EngineFWHMLabel != null) { detector1EngineFWHMLabel.dispose(); detector1EngineFWHMLabel = null; }
		}

		// detector2
		if (detector2Id != null){
			// Min
			if (this.detector2MinLabel == null) {
				this.detector2MinLabel = new Label( this, SWT.NONE );
				this.detector2MinLabel.setFont(newFont);
				this.detector2MinLabel.setText( "Min:" );
			}
			if (this.detector2EngineMinLabel == null) {
				this.detector2EngineMinLabel = new EngineDataLabel( this, SWT.NONE, detector2Id );
				this.detector2EngineMinLabel.setFont(newFont);
				this.detector2EngineMinLabel.setDataModifier(DataModifier.MIN);
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				this.detector2EngineMinLabel.setLayoutData( gridData );
			}
			else {
				this.detector2EngineMinLabel.setDataId(detector2Id);
				this.detector2EngineMinLabel.setText("          ");
			}
			if (this.detector2GotoMinButton == null) {
				this.detector2GotoMinButton = new Button( this, SWT.NONE );
				this.detector2GotoMinButton.setImage(gotoIcon);
				this.detector2GotoMinButton.addSelectionListener( new SelectionListener() {
				
					@Override
					public void widgetSelected(SelectionEvent e) {
						gotoPosition(detector2EngineMinLabel.getText());
					}
	
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
			}
			
			// Max
			if (this.detector2MaxLabel == null) {
				this.detector2MaxLabel = new Label( this, SWT.NONE );
				this.detector2MaxLabel.setText( "Max:" );
				this.detector2MaxLabel.setFont(newFont);
			}
			if (this.detector2EngineMaxLabel == null) {
				this.detector2EngineMaxLabel = new EngineDataLabel( this, SWT.NONE, detector2Id );
				this.detector2EngineMaxLabel.setFont(newFont);
				this.detector2EngineMaxLabel.setDataModifier(DataModifier.MAX);
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				gridData.verticalAlignment = SWT.FILL;
				this.detector2EngineMaxLabel.setLayoutData( gridData );
			}
			else {
				this.detector2EngineMaxLabel.setDataId(detector2Id);
				this.detector2EngineMaxLabel.setText("          ");
			}
			if (this.detector2GotoMaxButton == null) {
				this.detector2GotoMaxButton = new Button( this, SWT.NONE );
				this.detector2GotoMaxButton.setImage(gotoIcon);
				this.detector2GotoMaxButton.addSelectionListener( new SelectionListener() {
				
					@Override
					public void widgetSelected(SelectionEvent e) {
						gotoPosition(detector2EngineMaxLabel.getText());
					}
	
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
			}
			
			// Center
			if (this.detector2CenterLabel == null) {
				this.detector2CenterLabel = new Label( this, SWT.NONE );
				this.detector2CenterLabel.setText( "Center:" );
				this.detector2CenterLabel.setFont(newFont);
			}
			if (this.detector2EngineCenterLabel == null) {
				this.detector2EngineCenterLabel = new EngineDataLabel( this, SWT.NONE, detector2Id );
				this.detector2EngineCenterLabel.setFont(newFont);
				this.detector2EngineCenterLabel.setDataModifier(DataModifier.CENTER);
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				this.detector2EngineCenterLabel.setLayoutData( gridData );
			}
			else {
				this.detector2EngineCenterLabel.setDataId(detector2Id);
				this.detector2EngineCenterLabel.setText("          ");
			}
			if (this.detector2GotoCenterButton == null) {
				this.detector2GotoCenterButton = new Button( this, SWT.NONE );
				this.detector2GotoCenterButton.setImage(gotoIcon);
				this.detector2GotoCenterButton.addSelectionListener( new SelectionListener() {
				
					@Override
					public void widgetSelected(SelectionEvent e) {
						gotoPosition(detector2EngineCenterLabel.getText());
					}
	
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
			}
			
			// Edge
			if (this.detector2EdgeLabel == null) {
				this.detector2EdgeLabel = new Label( this, SWT.NONE );
				this.detector2EdgeLabel.setText( "Edge:" );
				this.detector2EdgeLabel.setFont(newFont);
			}
			if (this.detector2EngineEdgeLabel == null) {
				this.detector2EngineEdgeLabel = new EngineDataLabel( this, SWT.NONE, detector2Id );
				this.detector2EngineEdgeLabel.setFont(newFont);
				this.detector2EngineEdgeLabel.setDataModifier(DataModifier.EDGE);
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				this.detector2EngineEdgeLabel.setLayoutData( gridData );
			}
			else {
				this.detector2EngineEdgeLabel.setDataId(detector2Id);
				this.detector2EngineEdgeLabel.setText("          ");
			}
			if (this.detector2GotoEdgeButton == null) {
				this.detector2GotoEdgeButton = new Button( this, SWT.NONE );
				this.detector2GotoEdgeButton.setImage(gotoIcon);
				this.detector2GotoEdgeButton.addSelectionListener( new SelectionListener() {
				
					@Override
					public void widgetSelected(SelectionEvent e) {
						gotoPosition(detector2EngineEdgeLabel.getText());
					}
	
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});
			}
			// Average
			if (this.detector2AverageLabel == null) {
				this.detector2AverageLabel = new Label( this, SWT.NONE );
				this.detector2AverageLabel.setText( "Average:" );
				this.detector2AverageLabel.setFont(newFont);
			}
			if (this.detector2EngineAverageLabel == null) {
				this.detector2EngineAverageLabel = new EngineDataLabel( this, SWT.NONE, detector2Id );
				this.detector2EngineAverageLabel.setFont(newFont);
				this.detector2EngineAverageLabel.setDataModifier(DataModifier.MEAN_VALUE);
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				gridData.horizontalSpan = 2;
				this.detector2EngineAverageLabel.setLayoutData( gridData );
			}
			else {
				this.detector2EngineAverageLabel.setDataId(detector2Id);
				this.detector2EngineAverageLabel.setText("          ");
			}

			// deviation
			if (this.detector2DeviationLabel == null) {
				this.detector2DeviationLabel = new Label( this, SWT.NONE );
				this.detector2DeviationLabel.setText( "Deviation:" );
				this.detector2DeviationLabel.setFont(newFont);
			}
			if (this.detector2EngineDeviationLabel == null) {
				this.detector2EngineDeviationLabel = new EngineDataLabel( this, SWT.NONE, detector2Id );
				this.detector2EngineDeviationLabel.setFont(newFont);
				this.detector2EngineDeviationLabel.setDataModifier(DataModifier.STANDARD_DEVIATION);
				gridData = new GridData();
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				gridData.horizontalSpan = 2;
				this.detector2EngineDeviationLabel.setLayoutData( gridData );
			}
			else {
				this.detector2EngineDeviationLabel.setDataId(detector2Id);
				this.detector2EngineDeviationLabel.setText("          ");
			}
			// FWHM
			if (this.detector2FWHMLabel == null) {
				this.detector2FWHMLabel = new Label( this, SWT.NONE );
				this.detector2FWHMLabel.setText( "FWHM:" );
				this.detector2FWHMLabel.setFont(newFont);
			}
			if (this.detector2EngineFWHMLabel == null) {
				this.detector2EngineFWHMLabel = new EngineDataLabel( this, SWT.NONE, detector2Id );
				this.detector2EngineFWHMLabel.setFont(newFont);
				this.detector2EngineFWHMLabel.setDataModifier(DataModifier.FWHM);
				gridData = new GridData();
				gridData.horizontalSpan = 2;
				gridData.grabExcessHorizontalSpace = true;
				gridData.grabExcessVerticalSpace = true;
				gridData.horizontalAlignment = SWT.FILL;
				this.detector2EngineFWHMLabel.setLayoutData( gridData );
			}
			else {
				this.detector2EngineFWHMLabel.setDataId(detector2Id);
				this.detector2EngineFWHMLabel.setText("          ");
			}
		}
		else {
			if (detector2MinLabel != null) { detector2MinLabel.dispose(); detector2MinLabel = null; }
			if (detector2EngineMinLabel != null) { detector2EngineMinLabel.dispose(); detector2EngineMinLabel = null; }
			if (detector2GotoMinButton != null) { detector2GotoMinButton.dispose(); detector2GotoMinButton = null; }
			if (detector2MaxLabel != null) { detector2MaxLabel.dispose(); detector2MaxLabel = null; }
			if (detector2EngineMaxLabel != null) { detector2EngineMaxLabel.dispose(); detector2EngineMaxLabel = null; }
			if (detector2GotoMaxButton != null) { detector2GotoMaxButton.dispose(); detector2GotoMaxButton = null; }
			if (detector2CenterLabel != null) { detector2CenterLabel.dispose(); detector2CenterLabel = null; }
			if (detector2EngineCenterLabel != null) { detector2EngineCenterLabel.dispose(); detector2EngineCenterLabel = null; }
			if (detector2GotoCenterButton != null) { detector2GotoCenterButton.dispose(); detector2GotoCenterButton = null; }
			if (detector2EdgeLabel != null) { detector2EdgeLabel.dispose(); detector2EdgeLabel = null; }
			if (detector2EngineEdgeLabel != null) { detector2EngineEdgeLabel.dispose(); detector2EngineEdgeLabel = null; }
			if (detector2GotoEdgeButton != null) { detector2GotoEdgeButton.dispose(); detector2GotoEdgeButton = null; }
			if (detector2AverageLabel != null) { detector2AverageLabel.dispose(); detector2AverageLabel = null; }
			if (detector2EngineAverageLabel != null) { detector2EngineAverageLabel.dispose(); detector2EngineAverageLabel = null; }
			if (detector2DeviationLabel != null) { detector2DeviationLabel.dispose(); detector2DeviationLabel = null; }
			if (detector2EngineDeviationLabel != null) { detector2EngineDeviationLabel.dispose(); detector2EngineDeviationLabel = null; }
			if (detector2FWHMLabel != null) { detector2FWHMLabel.dispose(); detector2FWHMLabel = null; }
			if (detector2EngineFWHMLabel != null) { detector2EngineFWHMLabel.dispose(); detector2EngineFWHMLabel = null; }
		}
		
		this.layout();
		this.redraw();
		this.getParent().layout();
		this.getParent().redraw();

	}
	
	void setActive(boolean active){
		if (detector1EngineMinLabel != null) detector1EngineMinLabel.setActive(active);
		if (detector1EngineMaxLabel != null) detector1EngineMaxLabel.setActive(active);
		if (detector1EngineCenterLabel != null) detector1EngineCenterLabel.setActive(active);
		if (detector1EngineEdgeLabel != null) detector1EngineEdgeLabel.setActive(active);
		if (detector1EngineAverageLabel != null) detector1EngineAverageLabel.setActive(active);
		if (detector1EngineDeviationLabel != null) detector1EngineDeviationLabel.setActive(active);
		if (detector1EngineFWHMLabel != null) detector1EngineFWHMLabel.setActive(active);
		if (detector2EngineMinLabel != null) detector2EngineMinLabel.setActive(active);
		if (detector2EngineMaxLabel != null) detector2EngineMaxLabel.setActive(active);
		if (detector2EngineCenterLabel != null) detector2EngineCenterLabel.setActive(active);
		if (detector2EngineEdgeLabel != null) detector2EngineEdgeLabel.setActive(active);
		if (detector2EngineAverageLabel != null) detector2EngineAverageLabel.setActive(active);
		if (detector2EngineDeviationLabel != null) detector2EngineDeviationLabel.setActive(active);
		if (detector2EngineFWHMLabel != null) detector2EngineFWHMLabel.setActive(active);
	}

	@Override
	public void chainStatusChanged(ChainStatusCommand chainStatusCommand) {

		int chid = chainStatusCommand.getChainId();
		int smid = chainStatusCommand.getScanModulId();
		boolean isActive;
		if ((this.chid == chid) && (this.smid == smid) && (chainStatusCommand.getChainStatus() == ChainStatus.EXECUTING_SM)){
			isActive = true;
		}
		else {
			isActive = false;
		}
		setActive(isActive);
	}

}
