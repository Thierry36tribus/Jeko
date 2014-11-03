package processors;

import models.RawSaving;

import org.apache.poi.xssf.usermodel.XSSFRow;

public class BaseSavingCreator extends SavingCreator {

	@Override
	public RawSaving createSaving(final XSSFRow row) {
		final RawSaving saving = new RawSaving();
		final String[] nameArray = row.getCell(1).getRichStringCellValue().getString().split(" - ");
		saving.dwellingLabel = nameArray[2];
		saving.globalSaving = 0;
		saving.heatingSaving = getHeatingValue(row.getCell(3).getStringCellValue());
		saving.electricitySaving = getDoubleValue(row, 7);
		saving.waterSaving = getDoubleValue(row, 11);
		return saving;
	}

	/* Etiquette DPE : A->1, b->2, etc. */
	private double getHeatingValue(final String stringValue) {
		if (stringValue.length() == 1) {
			final char c = stringValue.charAt(0);
			final int val = c - 'A' + 1;
			if (val > 0 && val < 8) {
				return val;
			}
		}
		return 0;
	}
}
