package visual;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import models.Dwelling;
import models.Saving;
import models.VisualSavingElement;
import processors.TreeShapeManager;

public class FruitsCreator implements VisualElementsCreator {

	private static final int FRUIT_SIZE = 22;

	private final Dwelling dwelling;

	public FruitsCreator(final Dwelling dwelling) {
		super();
		this.dwelling = dwelling;
	}

	@Override
	public List<VisualSavingElement> create(final Saving saving) {
		final List<VisualSavingElement> existingFruits = findExistingFruits(saving.year, saving.week);

		final TreeShapeManager treeShapeManager = new TreeShapeManager(saving.dwelling.getAvatar().name);
		final int fruitsNb = getFruitsNb(saving.week == Saving.FIRST_WEEK, saving);
		final List<VisualSavingElement> elements = new ArrayList();
		for (int i = 0; i < fruitsNb; i++) {
			final VisualSavingElement fruit = new VisualSavingElement(saving);
			setFruitPos(fruit, treeShapeManager, existingFruits, elements);
			elements.add(fruit);
		}
		return elements;
	}

	private List<VisualSavingElement> findExistingFruits(final int beforeYear, final int beforeWeek) {
		final List<VisualSavingElement> existingFruits = new ArrayList();
		final List<Saving> savings = Saving.findBefore(beforeYear, beforeWeek, dwelling);
		for (final Saving saving : savings) {
			existingFruits.addAll(saving.findVisualElements());
		}
		return existingFruits;
	}

	private void setFruitPos(final VisualSavingElement fruit, final TreeShapeManager treeShapeManager,
			final List<VisualSavingElement> existingFruits, final List<VisualSavingElement> currentElements) {
		final List<VisualSavingElement> allExisting = new ArrayList(existingFruits);
		allExisting.addAll(currentElements);
		Point point = null;
		for (int tryIndex = 0; tryIndex < 1000; tryIndex++) {
			point = coordinates(treeShapeManager);
			boolean existing = false;
			for (final VisualSavingElement element : allExisting) {
				// x fois la taille pour bien les espacer
				if (point.distance(element.x, element.y) < 1.2 * FRUIT_SIZE) {
					existing = true;
					break;
				}
			}
			if (!existing) {
				// Logger.debug("dwelling %s, ok at try n°%s", dwelling.id,
				// tryIndex);
				break;
			}
			/*
			 * if (tryIndex == 1000 && existing) {
			 * Logger.debug("dwelling %s, not ok after 1000 tries",
			 * dwelling.id); }
			 */
		}
		fruit.x = point.x;
		fruit.y = point.y;
	}

	private Point coordinates(final TreeShapeManager treeShapeManager) {
		final Point point = treeShapeManager.createPoint();
		point.x -= FRUIT_SIZE / 2;
		point.y -= FRUIT_SIZE / 2;
		return point;
	}

	private static int getFruitsNb(final boolean firstWeek, final Saving saving) {
		int nb = 0;
		if (firstWeek) {
			nb += new int[] { 0, 10, 8, 6, 4, 3, 2, 0 }[(int) saving.heatingSaving];
			nb += getFruitsNbFromPercent(saving.electricitySaving / 4);
			nb += getFruitsNbFromPercent(saving.waterSaving / 4);
		} else {
			nb = getFruitsNbFromPercent(saving.movingAverageSaving / 4);
		}
		return nb;
	}

	private static int getFruitsNbFromPercent(final double percent) {
		int nb = 0;
		if (percent > 0) {
			// changer aussi coté client si modif ! (Simulator.js)
			nb = (int) Math.round(percent * 33.3);
			if (nb == 0 && percent > 0) {
				nb = 1;
			}
		}
		return nb;
	}
}
