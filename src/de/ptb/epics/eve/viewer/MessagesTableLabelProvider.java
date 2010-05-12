package de.ptb.epics.eve.viewer;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import java.util.Calendar;

public class MessagesTableLabelProvider implements ITableLabelProvider {

	public Image getColumnImage( final Object element, final int columnIndex ) {
		return null;
	}

	public String getColumnText( final Object element, final int columnIndex ) {
		ViewerMessage message = (ViewerMessage)element;
		if( columnIndex == 0 ) {
			return message.getMessageDateTime().get( Calendar.HOUR_OF_DAY ) + ":" + message.getMessageDateTime().get( Calendar.MINUTE ) + ":" + message.getMessageDateTime().get( Calendar.SECOND ) + "." + message.getMessageDateTime().get( Calendar.MILLISECOND);
//			return message.getMessageDateTime().get( Calendar.YEAR ) + "-" + message.getMessageDateTime().get( Calendar.MONTH ) + "-" + message.getMessageDateTime().get( Calendar.DAY_OF_MONTH ) + " " + message.getMessageDateTime().get( Calendar.HOUR ) + ":" + message.getMessageDateTime().get( Calendar.MINUTE ) + ":" + message.getMessageDateTime().get( Calendar.SECOND );
		} else if( columnIndex == 1 ) {
			return message.getMessageSource().toString();
		} else if( columnIndex == 2 ) {
			return message.getMessageType().toString();
		} else if( columnIndex == 3 ) {
			return message.getMessage();
		}
		
		return null;
	}

	public void addListener( final ILabelProviderListener listener ) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty( final Object element, final String property ) {
		return false;
	}

	public void removeListener( final ILabelProviderListener listener ) {
	}

}
