package processors;

import models.RawSaving;

import org.apache.poi.xssf.usermodel.XSSFRow;

public class WeekSavingCreator extends SavingCreator {

	@Override
	public RawSaving createSaving(final XSSFRow row) {
		final RawSaving saving = new RawSaving();
		final String[] nameArray = row.getCell(1).getRichStringCellValue().getString().split(" - ");
		saving.dwellingLabel = nameArray[2];
		saving.globalSaving = getDoubleValue(row, 2);
		saving.heatingConsumption = getDoubleValue(row, 3);
		saving.heatingRef = getDoubleValue(row, 4);
		saving.heatingSaving = getDoubleValue(row, 5);
		saving.electricityConsumption = getDoubleValue(row, 6);
		saving.electricityRef = getDoubleValue(row, 7);
		saving.electricitySaving = getDoubleValue(row, 8);
		saving.waterConsumption = getDoubleValue(row, 9);
		saving.waterRef = getDoubleValue(row, 10);
		saving.waterSaving = getDoubleValue(row, 11);
		saving.hotWaterConsumption = getDoubleValue(row, 12);
		saving.hotWaterRef = getDoubleValue(row, 13);
		saving.hotWaterSaving = getDoubleValue(row, 14);
		return saving;
	}

}
