package models;

public class RawSaving {

	public String dwellingLabel;
	public double globalSaving;

	public double heatingSaving;
	public double heatingConsumption;
	public double heatingRef;

	public double electricitySaving;
	public double electricityConsumption;
	public double electricityRef;

	public double waterSaving;
	public double waterConsumption;
	public double waterRef;

	public double hotWaterSaving;
	public double hotWaterConsumption;
	public double hotWaterRef;

	@Override
	public String toString() {
		return "RawSaving [dwellingLabel=" + dwellingLabel + ", globalSaving=" + globalSaving + "]";
	}

}
