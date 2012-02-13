package de.ptb.epics.eve.editor.views.scanmoduleview.prescancomposite;

import java.util.Iterator;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PrescanError;

/**
 * <code>PrescanLabelProvider</code> is the label provider of the table viewer 
 * defined in 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.prescancomposite.PrescanComposite}.
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class LabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object prescan, final int colIndex) {
		final Prescan pos = (Prescan)prescan;
		if(colIndex == 1) {
			final Iterator<IModelError> it = pos.getModelErrors().iterator();
			while(it.hasNext()) {
				final IModelError modelError = it.next();
				if(modelError instanceof PrescanError) {
					return PlatformUI.getWorkbench().getSharedImages().
							getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
				}
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(final Object prescan, final int colIndex) {
		
		final Prescan pos = (Prescan)prescan;
		
		switch(colIndex) {
			
			case 0:
				return (pos.getAbstractPrePostscanDevice() != null)
						? pos.getAbstractPrePostscanDevice().getFullIdentifyer()
						: "";
			case 1:
				if(pos.getAbstractPrePostscanDevice().getValue().getType().
						equals(DataTypes.ONOFF)) {
					// ONOFF -> als Value wird On oder Off gesetzt
					String[] werte = pos.getAbstractPrePostscanDevice().
						getValue().getDiscreteValues().toArray(new String[0]);
					if (werte[0].equals(pos.getValue()))
						// Erster Eintrag ist gesetzt, On anzeigen
						return "On";
					else if (werte[1].equals(pos.getValue()))
						// Zweiter Eintrag ist gesetzt, Off anzeigen
						return "Off";
					else
						return "";
				}
				else if(pos.getAbstractPrePostscanDevice().getValue().getType().
						equals(DataTypes.OPENCLOSE)) {
					// OPENCLOSE -> als Value wird Open oder Close gesetzt
					String[] werte = pos.getAbstractPrePostscanDevice().
						getValue().getDiscreteValues().toArray(new String[0]);
					if (werte[0].equals(pos.getValue()))
						// Erster Eintrag ist gesetzt, Open anzeigen
						return "Open";
					else if (werte[1].equals(pos.getValue()))
						// Zweiter Eintrag ist gesetzt, Close anzeigen
						return "Close";
					else
						return "";
				}
				else
					return (pos.getValue() != null)
							? pos.getValue()
							: "";
		}
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addListener(final ILabelProviderListener arg0) {
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
	public boolean isLabelProperty(final Object arg0, String arg1) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(final ILabelProviderListener arg0) {
	}
}