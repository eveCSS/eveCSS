package de.ptb.epics.eve.viewer.messages;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;


import java.util.Calendar;

/**
 * <code>MessagesTableLabelProvider</code> is the label provider for the table 
 * viewer of the Messages View.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class MessagesTableLabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(final Object element, final int columnIndex) {
		ViewerMessage message = (ViewerMessage) element;
		
		if( columnIndex == 0 ) {
			// first column is the time
			return message.getTimeStamp().toMONDDYYYY();
			/*
			return message.getMessageDateTime().get( Calendar.HOUR_OF_DAY ) + 
			       ":" + message.getMessageDateTime().get( Calendar.MINUTE ) + 
			       ":" + message.getMessageDateTime().get( Calendar.SECOND ) + 
			       "." + message.getMessageDateTime().get( Calendar.MILLISECOND);
			       */
//			return message.getMessageDateTime().get( Calendar.YEAR ) + "-" + message.getMessageDateTime().get( Calendar.MONTH ) + "-" + message.getMessageDateTime().get( Calendar.DAY_OF_MONTH ) + " " + message.getMessageDateTime().get( Calendar.HOUR ) + ":" + message.getMessageDateTime().get( Calendar.MINUTE ) + ":" + message.getMessageDateTime().get( Calendar.SECOND );
		} else if(columnIndex == 1) {
			// second column is the source
			return message.getMessageSource().toString();
		} else if(columnIndex == 2) {
			// third column is the type
			return message.getMessageType().toString();
		} else if(columnIndex == 3) {
			// fourth column is the message
			return message.getMessage();
		}	
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(final ILabelProviderListener listener) {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(final ILabelProviderListener listener) {
	}
}