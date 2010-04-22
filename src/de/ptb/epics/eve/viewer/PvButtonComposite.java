package de.ptb.epics.eve.viewer;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.platform.model.pvs.ProcessVariableAdressFactory;
import org.csstudio.platform.simpledal.ConnectionException;
import org.csstudio.platform.simpledal.ConnectionState;
import org.csstudio.platform.simpledal.IProcessVariableConnectionService;
import org.csstudio.platform.simpledal.IProcessVariableValueListener;
import org.csstudio.platform.simpledal.ProcessVariableConnectionServiceFactory;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.epics.css.dal.Timestamp;

import de.ptb.epics.eve.data.TypeValue;

/**
 * 
 * Button send a value to the specified pvname if pressed 
 * 
 * @author eden
 * 
 */

public class PvButtonComposite extends Composite implements SelectionListener {

	private String pvname;
	private Button buttonWidget;
	private TypeValue value;

	public PvButtonComposite(Composite parent, int style, String pvname, TypeValue value) {
		super(parent, style);
		this.pvname = pvname;
		this.value = value;
		buttonWidget = new Button(this, style);
		this.setLayout(new FillLayout());
		buttonWidget.setToolTipText(pvname);
		buttonWidget.addSelectionListener( this );
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		IProcessVariableConnectionService service = ProcessVariableConnectionServiceFactory.getDefault().getProcessVariableConnectionService();
		ProcessVariableAdressFactory pvFactory = ProcessVariableAdressFactory.getInstance(); 
		IProcessVariableAddress pv = pvFactory.createProcessVariableAdress("dal-epics://" + pvname);
		Object o;
		switch( value.getType() ) {
			
			case INT:
				o = Integer.parseInt( value.getValues() );
				break;
				
			case DOUBLE: 
				o = Double.parseDouble( value.getValues() );
				break;
			
			default:
				o = value.getValues();
		
		}
		try {
			service.writeValueSynchronously( pv, o, Helper.dataTypesToValueType( value.getType() ) );
		} catch( final ConnectionException e1 ) {
			e1.printStackTrace();
		}
	}

	public void setText(String text){
		buttonWidget.setText( text );
	}

	public void setFont(Font font){
		buttonWidget.setFont( font );
	}

}
