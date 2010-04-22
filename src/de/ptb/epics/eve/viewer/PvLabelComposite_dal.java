/**
 * 
 */
package de.ptb.epics.eve.viewer;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.csstudio.platform.simpledal.ValueType;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.epics.css.dal.Timestamp;

/**
 * Label widget registers its pvname with dal-epics and shows the value
 * in the label field. Foreground color shows the connection-state
 * @author eden
 *
 */
public class PvLabelComposite_dal extends Composite implements IProcessVariableValueListener {

	private IProcessVariableAddress pv;
	private String pvname;
	private Label labelWidget;
	/**
	 * @param parent
	 * @param style
	 */
	public PvLabelComposite_dal(Composite parent, int style, String pvname) {
		super(parent, style);
		this.pvname = pvname;
		labelWidget = new Label(this, style);
		this.setLayout(new FillLayout());
		labelWidget.setToolTipText(pvname);
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance();
		pv = pvFactory.createProcessVariableAdress("dal-epics://" + pvname);
		service.register( this, pv, ValueType.STRING );
	}

	public void connectionStateChanged( final ConnectionState connectionState ) {
		this.getDisplay().syncExec( new Runnable() {

			public void run() {
				
				String newText = "";
				Color newColor=Activator.getDefault().getColor("COLOR_PV_INITIAL");
				switch( connectionState ) {
					case CONNECTED:
						newText = "connected";
						newColor = Activator.getDefault().getColor("COLOR_PV_OK");
						break;
					case CONNECTION_FAILED:
						newText = "connection failed";
						newColor = Activator.getDefault().getColor("COLOR_PV_ALARM");
						break;
					case CONNECTION_LOST:
						newText = "connection lost";
						newColor = Activator.getDefault().getColor("COLOR_PV_ALARM");
						break;
					case INITIAL:
						newText = "initial";
						newColor = Activator.getDefault().getColor("COLOR_PV_INITIAL");
						break;
					case UNKNOWN:
						newText = "unknown";
						newColor = Activator.getDefault().getColor("COLOR_PV_UNKNOWN");
				}
				Activator.getDefault().getMessagesContainer().addMessage( new ViewerMessage( MessageSource.APPLICATION, MessageTypes.INFO, pv.getFullName() + " changed connection state to " + newText + "." ) );
				labelWidget.setForeground(newColor);
			}
		});
	}

	public void errorOccured( final String error ) {
		System.err.println( "error  in " + pvname );
	}

	public void valueChanged( final Object value, final Timestamp timestamp ) {
		
		if (!isDisposed()) getDisplay().syncExec( new Runnable() {

			public void run() {
				
				if (!labelWidget.isDisposed()){
					labelWidget.setForeground(Activator.getDefault().getColor("COLOR_PV_OK"));
					labelWidget.setText( value.toString() );
				}
			}
		});
	}

	public void setText(String text){
		labelWidget.setText( text );
	}

	public void setFont(Font font){
		labelWidget.setFont( font );
	}

	public void dispose() {
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		//service.unregister( this );
		super.dispose();
	}

}
