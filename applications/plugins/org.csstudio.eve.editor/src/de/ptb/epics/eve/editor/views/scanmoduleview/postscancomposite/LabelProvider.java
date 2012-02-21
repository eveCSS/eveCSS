package de.ptb.epics.eve.editor.views.scanmoduleview.postscancomposite;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PostscanError;

/**
 * <code>PostscanLabelProvider</code> is the label provider for the table 
 * viewer defined in 
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.postscancomposite.PostscanComposite}.
 * 
 * @author ?
 */
public class LabelProvider implements ITableLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object postscan, final int colIndex) {
		final Postscan pos = (Postscan)postscan;
		if (colIndex == 1) {
			for(IModelError error : pos.getModelErrors()) {
				if(error instanceof PostscanError) {
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
	public String getColumnText(final Object postscan, final int colIndex) {
		final Postscan pos = (Postscan)postscan;
		switch(colIndex) {
			case 0: // device column
				return (pos.getAbstractPrePostscanDevice() != null)
						? pos.getAbstractPrePostscanDevice().getFullIdentifyer()
						: null;
			case 1: // value column
				if (pos.getAbstractPrePostscanDevice().getValue().
						getType().equals(DataTypes.ONOFF)) {
					String[] werte = pos.getAbstractPrePostscanDevice().getValue().getDiscreteValues().toArray(new String[0]);
					if (werte[0].equals(pos.getValue()))
						// Erster Eintrag ist gesetzt, On anzeigen
						return "On";
					else if (werte[1].equals(pos.getValue()))
						// Zweiter Eintrag ist gesetzt, Off anzeigen
						return "Off";
					else
						return "";
				}
				else if (pos.getAbstractPrePostscanDevice().getValue().getType().equals(DataTypes.OPENCLOSE)) {
					// Datentyp OPENCLOSE vorhanden, als Value wird Open oder Close gesetzt
					String[] werte = pos.getAbstractPrePostscanDevice().getValue().getDiscreteValues().toArray(new String[0]);
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
					return (pos.getValue()!=null)?pos.getValue():"";
			case 2:
				return Boolean.toString(pos.isReset());
		}
		return null;
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
	public void addListener(final ILabelProviderListener arg0) {
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeListener(final ILabelProviderListener arg0) {
	}
}