package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.postscancomposite;

import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Postscan;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PostscanError;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.ActionCompositeLabelProvider;

/**
 * <code>PostscanLabelProvider</code> is the label provider for the table viewer
 * defined in
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.postscancomposite.PostscanComposite}
 * .
 * 
 * @author ?
 */
public class LabelProvider extends ActionCompositeLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object postscan, final int colIndex) {
		final Postscan pos = (Postscan) postscan;
		if (colIndex == 0) {
			return getDeleteImage();
		} else if (colIndex == 1) {
			for (IModelError error : pos.getModelErrors()) {
				if (error instanceof PostscanError) {
					return getErrorImage();
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
		final Postscan pos = (Postscan) postscan;
		switch (colIndex) {
		case 1: // device column
			if(pos.isDevice()) {
				return pos.getAbstractDevice().getName();
			}
			return pos.getAbstractDevice().getParent().getName() + " "
					+ (char) 187 + " " + pos.getAbstractDevice().getName();
		case 2: // value column
			if (pos.getAbstractPrePostscanDevice().getValue().getType()
					.equals(DataTypes.ONOFF)) {
				String[] werte = pos.getAbstractPrePostscanDevice().getValue()
						.getDiscreteValues().toArray(new String[0]);
				if (werte[0].equals(pos.getValue())) {
					// Erster Eintrag ist gesetzt, On anzeigen
					return "On";
				} else if (werte[1].equals(pos.getValue())){
					// Zweiter Eintrag ist gesetzt, Off anzeigen
					return "Off";
				} else {
					return "";
				}
			} else if (pos.getAbstractPrePostscanDevice().getValue().getType()
					.equals(DataTypes.OPENCLOSE)) {
				// Datentyp OPENCLOSE vorhanden, als Value wird Open oder Close
				// gesetzt
				String[] werte = pos.getAbstractPrePostscanDevice().getValue()
						.getDiscreteValues().toArray(new String[0]);
				if (werte[0].equals(pos.getValue())) {
					// Erster Eintrag ist gesetzt, Open anzeigen
					return "Open";
				} else if (werte[1].equals(pos.getValue())) {
					// Zweiter Eintrag ist gesetzt, Close anzeigen
					return "Close";
				} else {
					return "";
				}
			} else {
				return (pos.getValue() != null) ? pos.getValue() : "";
			}
		case 3:
			return Boolean.toString(pos.isReset());
		}
		return null;
	}
}