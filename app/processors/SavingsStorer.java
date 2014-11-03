package processors;

import java.util.List;

import models.Dwelling;
import models.RawSaving;
import models.Saving;
import play.Logger;
import visual.FruitsCreator;
import visual.VisualElementsCreator;

public class SavingsStorer {

	public void store(final int year, final int week, final List<RawSaving> rawSavings) {
		for (final RawSaving rawSaving : rawSavings) {
			store(year, week, rawSaving);
		}
	}

	private void store(final int year, final int week, final RawSaving rawSaving) {
		final Dwelling dwelling = Dwelling.findByLabel(rawSaving.dwellingLabel);
		if (dwelling == null) {
			Logger.warn("Dwelling %s not found, ignored", rawSaving.dwellingLabel);
			return;
		}

		final VisualElementsCreator visualElementsCreator = new FruitsCreator(dwelling);
		final Saving saving = new Saving(year, week, dwelling, rawSaving.globalSaving, rawSaving.heatingSaving,
				rawSaving.heatingConsumption, rawSaving.heatingRef, rawSaving.electricitySaving,
				rawSaving.electricityConsumption, rawSaving.electricityRef, rawSaving.waterSaving,
				rawSaving.waterConsumption, rawSaving.waterRef, rawSaving.hotWaterSaving,
				rawSaving.hotWaterConsumption, rawSaving.hotWaterRef);
		if (saving.heatingConsumption < 0) {
			saving.heatingConsumption = 0;
		}
		if (saving.electricityConsumption < 0) {
			saving.electricityConsumption = 0;
		}
		if (saving.waterConsumption < 0) {
			saving.waterConsumption = 0;
		}
		if (saving.hotWaterConsumption < 0) {
			saving.hotWaterConsumption = 0;
		}

		saving.createOrUpdate(visualElementsCreator);
	}
}
