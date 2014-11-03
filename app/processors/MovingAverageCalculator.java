package processors;

import java.util.List;

import models.MovingAverageResult;
import models.Saving;

public class MovingAverageCalculator {

	private static int NB_WEEKS = 4;

	/** Liste des Saving dans l'ordre chrono inverse, plus récent en premier */
	public MovingAverageResult calculate(final List<Saving> savings, final int week) {
		int weekIndex = -1;
		int surface = 0;
		for (int i = 0; i < savings.size(); i++) {
			if (week == savings.get(i).week) {
				surface = savings.get(i).dwelling.surface;
				weekIndex = i;
				break;
			}
		}
		if (weekIndex >= 0) {
			double sumCons = 0;
			double sumRef = 0;
			double count = 0;
			for (int i = weekIndex; i < savings.size() && i < weekIndex + NB_WEEKS; i++) {
				final Saving s = savings.get(i);
				sumCons += sum(surface, s.heatingConsumption, s.electricityConsumption, s.waterConsumption,
						s.hotWaterConsumption);
				sumRef += sum(surface, s.heatingRef, s.electricityRef, s.waterRef, s.hotWaterRef);
				count++;
			}
			if (count > 0 && sumRef > 0) {
				final double avCons = sumCons / count;
				final double avRef = sumRef / count;
				return new MovingAverageResult(avCons, avRef, (avRef - avCons) / avRef);
			} else {
				return new MovingAverageResult(0, 0, 0);
			}
		}
		throw new IllegalArgumentException("Week " + week + " not found in savings: " + savings);
	}

	private double sum(final int surface, final double heating, final double electricity, final double water,
			final double hotWater) {
		// Ce coeff de conversion litres -> kWh est aussi dans Kite.js
		final double eqWater = water * 0.045;
		return heating * surface + electricity + eqWater + hotWater * surface;
	}
}
