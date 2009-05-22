package org.rifidi.edge.client.twodview.layers;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.rifidi.edge.client.twodview.exceptions.ReaderAlreadyInMapException;
import org.rifidi.edge.client.twodview.sfx.ReaderAlphaImageFigure;

/**
 * This layer holds the visual representations of the readers.
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 */
public class ObjectLayer extends XYLayer {

	/**
	 * Creates a new Layer with MouseListener and MouseMotionListener
	 */
	public ObjectLayer() {
		super();
	}

	/**
	 * Adds a reader to the layer.
	 * 
	 * @param reader
	 * @param location
	 * @throws ReaderAlreadyInMapException
	 */
	public void addReader(ReaderAlphaImageFigure reader, Point location)
			throws ReaderAlreadyInMapException {

		if (location != null) {
			reader.setBounds(new Rectangle(location.x, location.y, reader
					.getImage().getBounds().width, reader.getImage()
					.getBounds().height));
		}

		for (Object fig : this.getChildren()) {
			ReaderAlphaImageFigure raif = (ReaderAlphaImageFigure) fig;
			if (raif.getReaderId().equals(reader.getReaderId())) {
				throw new ReaderAlreadyInMapException("Drag and Drop failed");

			}
		}
		add(reader);
		return;

	}
}
