package games;

import models.Saving;

public class UiSavingForGame {

	public double global;
	public Double heating, electricity, water, hotWater;
	public Double heatingCons, electricityCons, waterCons, hotWaterCons;
	/** Conso moyenne de tous les logements cette semaine */
	public Double heatingConsMean, electricityConsMean, waterConsMean, hotWaterConsMean;
	public int surface;

	public UiSavingForGame(final Saving saving, final Double heatingConsMean, final Double electricityConsMean,
			final Double waterConsMean, final Double hotWaterConsMean, final int surface) {
		this.global = saving.globalSaving;
		this.heating = saving.heatingSaving;
		this.electricity = saving.electricitySaving;
		this.water = saving.waterSaving;
		this.hotWater = saving.hotWaterSaving;
		this.heatingCons = saving.heatingConsumption;
		this.electricityCons = saving.electricityConsumption;
		this.waterCons = saving.waterConsumption;
		this.hotWaterCons = saving.hotWaterConsumption;
		this.heatingConsMean = heatingConsMean;
		this.electricityConsMean = electricityConsMean;
		this.waterConsMean = waterConsMean;
		this.hotWaterConsMean = hotWaterConsMean;
		this.surface = surface;
	}

	public UiSavingForGame(final Saving saving, final MeanConsumption meanConsumption, final int surface) {
		this(saving, meanConsumption.heatingCons, meanConsumption.electricityCons, meanConsumption.waterCons,
				meanConsumption.hotWaterCons, surface);
	}
}
