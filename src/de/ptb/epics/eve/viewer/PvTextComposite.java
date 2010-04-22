/**
 * 
 */
package de.ptb.epics.eve.viewer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.csstudio.platform.data.ValueUtil;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;

/**
 * 
 * Text widget registers its pvname with dal-epics and shows the value
 * in the text field. Text changes from GUI are sent to the pv on focus lost event
 * 
 * @author eden
 * 
 */
public class PvTextComposite extends Composite implements PVListener, SelectionListener {

	private PV pv;
	private String pvname;
	private Text textWidget;
	/**
	 * @param parent parent widget 
	 * @param style SWT style
	 * @param pvname the PV to connect to
	 */
	public PvTextComposite(Composite parent, int style, String pvname) {
		super(parent, style);
		this.pvname = pvname;
		textWidget = new Text(this, SWT.BORDER);
		this.setLayout(new FillLayout());
		textWidget.setToolTipText(pvname);
		textWidget.setText("unknown");
        try {
			pv = PVFactory.createPV(pvname);
	        pv.addListener(this);
	        pv.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// add a listener for the TextInput
		final String writepvname = pvname;
		textWidget.addFocusListener( new FocusListener() {
		
			
			public void focusLost(FocusEvent e) {

		        try
		        {
		            if ((pv == null) || !pv.isConnected())
		            {
		                return;
		            }
		            pv.setValue(textWidget.getText());
		        }
		        catch (Throwable ex)
		        {
		            //Plugin.getLogger().error(Messages.S_AdjustFailed, ex);
		            //updateStatus(Messages.S_AdjustFailed + ex.getMessage());
		            ex.printStackTrace();
		        }
			}
		
			public void focusGained(FocusEvent e) {
				textWidget.setForeground(Activator.getDefault().getColor("COLOR_PV_INITIAL"));		
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}


	public void errorOccured( final String error ) {
		System.err.println( "error  in " + pvname );
	}

	public void setText(String text){
		textWidget.setText( text );
	}

	public void setFont(Font font){
		textWidget.setFont( font );
	}

	public void dispose() {
        if (pv != null)
        {
            //Plugin.getLogger().debug("disposeChannel " + pv.getName()); //$NON-NLS-1$
        	Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.INFO, "Disposing text for " + pv.getName() ) );
            pv.removeListener(this);
            pv.stop();
            pv = null;
        }
		super.dispose();
	}

	@Override
	public void pvDisconnected(PV disconnectedPV) {

		final PV pv = disconnectedPV;
		this.getDisplay().syncExec( new Runnable() {

			public void run() {
				Color newColor = Activator.getDefault().getColor("COLOR_PV_ALARM");
				Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.INFO, pv.getName() + " connection lost" ) );
				textWidget.setForeground(newColor);
			}
		});
		
		
	}

	@Override
	public void pvValueUpdate(PV updatedPV) {
		final PV pv = updatedPV;
		if (!isDisposed()) getDisplay().asyncExec( new Runnable() {

			public void run() {
				
				if (!textWidget.isDisposed()){
					textWidget.setForeground(Activator.getDefault().getColor("COLOR_PV_OK"));
					try
					{
						textWidget.setText( ValueUtil.getString(pv.getValue()));
					}
					catch (Exception e)
					{
						//Plugin.getLogger().error("pvValueUpdate error", e); //$NON-NLS-1$
						//updateStatus(e.getMessage());
					}
				}
			}
		});
	}
}
