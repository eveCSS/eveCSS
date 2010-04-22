/**
 * 
 */
package de.ptb.epics.eve.viewer;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.csstudio.utility.pv.PV;
import org.csstudio.utility.pv.PVFactory;
import org.csstudio.utility.pv.PVListener;
import org.csstudio.platform.data.ValueUtil;

/**
 * Label widget registers its pvname with ca and shows the value
 * in the label field. Foreground color depends on the connection-state
 * @author eden
 *
 */
public class PvLabelComposite extends Composite implements PVListener {

	private PV pv;
	private String pvname;
	private Label labelWidget;

	/**
	 * @param parent
	 * @param style
	 */

	public PvLabelComposite(Composite parent, int style, String pvname) {
		super(parent, style);
		this.pvname = pvname;
		labelWidget = new Label(this, style);
		this.setLayout(new FillLayout());
		labelWidget.setToolTipText(pvname);
        try {
			pv = PVFactory.createPV(pvname);
	        pv.addListener(this);
	        pv.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

/*	public void errorOccured( final String error ) {
		System.err.println( "error  in " + pvname );
	}

*/
	public void setText(String text){
		labelWidget.setText( text );
	}

	public void setFont(Font font){
		labelWidget.setFont( font );
	}

	public void dispose() {
        if (pv != null)
        {
            //Plugin.getLogger().debug("disposeChannel " + pv.getName()); //$NON-NLS-1$
        	Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.INFO, "Disposing label for " + pv.getName() ) );
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
				
				String newText = "connection lost";
				Color newColor = Activator.getDefault().getColor("COLOR_PV_ALARM");
				Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.INFO, pv.getName() + " changed connection state to " + newText + "." ) );
				labelWidget.setForeground(newColor);
			}
		});
		
	}

	@Override
	public void pvValueUpdate(PV updatedPV) {
		
		final PV pv = updatedPV;
		if (!isDisposed()) getDisplay().asyncExec( new Runnable() {

			public void run() {
				
				if (!labelWidget.isDisposed()){
					labelWidget.setForeground(Activator.getDefault().getColor("COLOR_PV_OK"));
					try
					{
						labelWidget.setText( ValueUtil.getString(pv.getValue()));
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
