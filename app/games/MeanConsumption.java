package games;

import java.util.List;

import models.Saving;

public class MeanConsumption {

	public Double heatingCons, electricityCons, waterCons, hotWaterCons;

	public MeanConsumption(final Double heatingCons, final Double electricityCons, final Double waterCons,
			final Double hotWaterCons) {
		super();
		this.heatingCons = heatingCons;
		this.electricityCons = electricityCons;
		this.waterCons = waterCons;
		this.hotWaterCons = hotWaterCons;
	}

	@Override
	public String toString() {
		return "MeanConsumption [heatingCons=" + heatingCons + ", electricityCons=" + electricityCons + ", waterCons="
				+ waterCons + ", hotWaterCons=" + hotWaterCons + "]";
	}

	public static MeanConsumption createFrom(final List<Saving> savings) {
		final MeanConsumption mean = new MeanConsumption(0d, 0d, 0d, 0d);
		for (final Saving saving : savings) {
			mean.heatingCons += saving.heatingConsumption;
			mean.electricityCons += saving.electricityConsumption;
			mean.waterCons += saving.waterConsumption;
			mean.hotWaterCons += saving.hotWaterConsumption;
		}
		mean.heatingCons = mean.heatingCons / savings.size();
		mean.electricityCons = mean.electricityCons / savings.size();
		mean.waterCons = mean.waterCons / savings.size();
		mean.hotWaterCons = mean.hotWaterCons / savings.size();
		return mean;
	}
}
