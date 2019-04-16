package de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.prescancomposite;

import org.eclipse.swt.graphics.Image;

import de.ptb.epics.eve.data.DataTypes;
import de.ptb.epics.eve.data.scandescription.Prescan;
import de.ptb.epics.eve.data.scandescription.errors.IModelError;
import de.ptb.epics.eve.data.scandescription.errors.PrescanError;
import de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.ActionCompositeLabelProvider;

/**
 * <code>PrescanLabelProvider</code> is the label provider of the table viewer
 * defined in
 * {@link de.ptb.epics.eve.editor.views.scanmoduleview.classiccomposite.prescancomposite.PrescanComposite}
 * .
 * 
 * @author ?
 * @author Marcus Michalsky
 */
public class LabelProvider extends ActionCompositeLabelProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object prescan, final int colIndex) {
		if (colIndex == 0) {
			return getDeleteImage();
		} else if (colIndex == 1) {
			for (IModelError error : ((Prescan) prescan).getModelErrors()) {
				if (error instanceof PrescanError) {
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
	public String getColumnText(final Object prescan, final int colIndex) {

		final Prescan pos = (Prescan) prescan;

		switch (colIndex) {
		case 1:
			if(pos.isDevice()) {
				return pos.getAbstractDevice().getName();
			}
			return pos.getAbstractDevice().getParent().getName() + " "
					+ (char) 187 + " "
					+ ((Prescan) prescan).getAbstractDevice().getName();
		case 2:
			if (pos.getAbstractPrePostscanDevice().getValue().getType()
					.equals(DataTypes.ONOFF)) {
				// ONOFF -> als Value wird On oder Off gesetzt
				String[] werte = pos.getAbstractPrePostscanDevice().getValue()
						.getDiscreteValues().toArray(new String[0]);
				if (werte[0].equals(pos.getValue())) {
					// Erster Eintrag ist gesetzt, On anzeigen
					return "On";
				} else if (werte[1].equals(pos.getValue())) {
					// Zweiter Eintrag ist gesetzt, Off anzeigen
					return "Off";
				} else {
					return null;
				}
			} else if (pos.getAbstractPrePostscanDevice().getValue().getType()
					.equals(DataTypes.OPENCLOSE)) {
				// OPENCLOSE -> als Value wird Open oder Close gesetzt
				String[] werte = pos.getAbstractPrePostscanDevice().getValue()
						.getDiscreteValues().toArray(new String[0]);
				if (werte[0].equals(pos.getValue())) {
					// Erster Eintrag ist gesetzt, Open anzeigen
					return "Open";
				} else if (werte[1].equals(pos.getValue())) {
					// Zweiter Eintrag ist gesetzt, Close anzeigen
					return "Close";
				} else {
					return null;
				}
			} else {
				return (pos.getValue() != null) ? pos.getValue() : null;
			}
		}
		return null;
	}
}