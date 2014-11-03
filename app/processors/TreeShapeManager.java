package processors;

import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Random;

import org.apache.batik.ext.awt.geom.ExtendedGeneralPath;
import org.apache.batik.parser.AWTPathProducer;
import org.apache.batik.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import play.Logger;
import play.libs.XML;

public class TreeShapeManager {

	private static final String AVATARS_PATH = "ng/app/img/avatars";

	private final String avatar;
	private ExtendedGeneralPath shapePath;
	private Rectangle bounds;
	private final Random random = new Random();

	public TreeShapeManager(final String avatar) {
		this.avatar = avatar;
	}

	public Point createPoint() {
		if (shapePath == null) {
			try {
				shapePath = init(avatar);
				bounds = shapePath.getBounds();
			} catch (final Exception e) {
				Logger.error(e, "Unable to read path of avatar %s", avatar);
			}
		}
		// pour être plus à l'intérieur de la forme
		final int offsetX = (int) (bounds.width * 0.2);
		final int offsetY = (int) (bounds.height * 0.2);

		final Point point = new Point(0, 0);
		final int xMin = (bounds.x > 0 ? bounds.x : 0) + offsetX;
		final int xMax = bounds.width - 2 * offsetX;

		final int yMin = (bounds.y > 0 ? bounds.y : 0) + offsetY;
		final int yMax = bounds.height - 2 * offsetY;
		// Logger.debug("bounds: %s, %s, %s, %s", xMin, yMax, yMin, yMax);
		// on limite le nb d'essais pour être sur de ne pas avoir de boucle
		// infinie
		for (int i = 0; i < 100000; i++) {
			final int x = random.nextInt(xMax) + xMin;
			final int y = random.nextInt(yMax) + yMin;
			// Logger.debug("try n° %s: %s, %s", i, x, y);
			if (shapePath.contains(new Point(x, y))) {
				// Logger.debug("Yes, contains!");
				point.x = x;
				point.y = y;
				break;
			}
		}
		if (point.x == 0 && point.y == 0) {
			point.x = (int) bounds.getCenterX();
			point.y = (int) bounds.getCenterY();
		}
		return point;
	}

	public static ExtendedGeneralPath init(final String fileName) throws ParseException, IOException {
		// Logger.debug("init TreeShapeManager for %s", fileName);
		final Document doc = XML.getDocument(new File(AVATARS_PATH, fileName));
		final NodeList nodeList = doc.getElementsByTagName("path");
		// Logger.debug("nodeList size: %s", nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			if ("TreeShape".equals(((Element) nodeList.item(i)).getAttribute("id"))) {
				final Element elt = (Element) nodeList.item(i);
				// System.out.println("found, d=" + elt.getAttribute("d"));
				final String path = elt.getAttribute("d");
				// Logger.debug("path TreeShape found: %s", path);
				return (ExtendedGeneralPath) AWTPathProducer.createShape(new StringReader(path), 0);
			}
		}
		return null;
	}

}
