package de.ptb.epics.eve.editor.views.scanmoduleview.postscancomposite;

import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
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
	 *  Name of images used to represent checkboxes
	 */
	public static final String CHECKED_IMAGE 	= "checked";
	/**
	 *  Name of images used to represent checkboxes
	 */
	public static final String UNCHECKED_IMAGE  = "unchecked";

	// For the checkbox images
	private static ImageRegistry imageRegistry = new ImageRegistry();

	/**
	 * Note: An image registry owns all of the image objects registered with it,
	 * and automatically disposes of them the SWT Display is disposed.
	 */ 
	static {
		//TODO Frage: Kann hier das image auch irgendwo von SWT hergeholt werden?
		// Wie muß der iconPath gesetzt sein, damit auch darüberliegende Verzeichnisse
		// durchsucht werden? (Hartmut 19.4.10)
		imageRegistry.put(CHECKED_IMAGE, ImageDescriptor.createFromFile(
				PostscanComposite.class, 
				CHECKED_IMAGE + ".gif"
				)
			);
		imageRegistry.put(UNCHECKED_IMAGE, ImageDescriptor.createFromFile(
				PostscanComposite.class, 
				UNCHECKED_IMAGE + ".gif"
				)
			);	
	}
	
	/*
	 * Returns the image with the given key, or <code>null</code> if not found.
	 */
	private Image getImage(boolean isSelected) {
		String key = isSelected ? CHECKED_IMAGE : UNCHECKED_IMAGE;
		return  imageRegistry.get(key);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getColumnImage(final Object postscan, final int colIndex) {
		if (colIndex == 2) {
			// TODO Fehlerbehandlung fehlt
			Image bild = getImage(((Postscan) postscan).isReset());
			return bild;
		}
		
		final Postscan pos = (Postscan)postscan;
		if( colIndex == 1 ) {
			final Iterator< IModelError > it = pos.getModelErrors().iterator();
			while( it.hasNext() ) {
				final IModelError modelError = it.next();
				if( modelError instanceof PostscanError ) {
					return PlatformUI.getWorkbench().getSharedImages().getImage( ISharedImages.IMG_OBJS_ERROR_TSK );
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
		switch( colIndex ) {
			case 0:
				return (pos.getAbstractPrePostscanDevice()!=null)?pos.getAbstractPrePostscanDevice().getFullIdentifyer():"";
			case 1:
				if (pos.getAbstractPrePostscanDevice().getValue().getType().equals(DataTypes.ONOFF)) {
					// Datentyp ONOFF vorhanden, als Value wird On oder Off gesetzt
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
				if (pos.isReset())
					return "yes";
				else
					return "no";
		}
		return "";
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