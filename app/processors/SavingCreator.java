package processors;

import models.RawSaving;

import org.apache.poi.xssf.usermodel.XSSFRow;

import play.Logger;

public abstract class SavingCreator {

	abstract RawSaving createSaving(XSSFRow row);

	protected static double getDoubleValue(final XSSFRow row, final int cellIndex) {
		try {
			return row.getCell(cellIndex).getNumericCellValue();
		} catch (final Exception e) {
			Logger.error(e, "Unable to read cell %s of row %s", cellIndex, row.getRowNum());
			// TODO UNKNOWN_VALUE ?
			return 0;
		}
	}

}
