package processors;

import java.io.File;
import java.io.FileInputStream;

import models.RawSavingsReadResult;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import play.Logger;

public class RawSavingsXlsReader {

	private static final int FIRST_ROW = 32;
	private static final int LAST_ROW = 80;

	private static final String SHEET_SAVINGS = "3_SORTIE";
	private static final String SHEET_BASE = "6_BASE";

	private final int firstWeek;
	private XSSFWorkbook workbook;

	public RawSavingsXlsReader(final int firstWeek) {
		super();
		this.firstWeek = firstWeek;
	}

	public RawSavingsReadResult read(final File file) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			workbook = new XSSFWorkbook(fis);
			return readSheet(getSheet(SHEET_SAVINGS));
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private XSSFSheet getSheet(final String name) {
		for (final XSSFSheet sheet : workbook) {
			if (sheet.getSheetName().toUpperCase().startsWith(name)) {
				return sheet;
			}
		}
		return null;
	}

	private RawSavingsReadResult readSheet(final XSSFSheet sheet) {
		final int year = (int) sheet.getRow(4).getCell(1).getNumericCellValue();
		final String weekAsString = sheet.getRow(6).getCell(1).getRichStringCellValue().getString().substring(1);
		log("week: " + weekAsString);
		final int week = Integer.parseInt(weekAsString);

		if (week == firstWeek) {
			return readSheet(year, week, getSheet(SHEET_BASE), new BaseSavingCreator());
		} else {
			return readSheet(year, week, getSheet(SHEET_SAVINGS), new WeekSavingCreator());
		}
	}

	private RawSavingsReadResult readSheet(final int year, final int week, final XSSFSheet sheet,
			final SavingCreator savingCreator) {
		final RawSavingsReadResult result = new RawSavingsReadResult(year, week);
		for (int rowIndex = FIRST_ROW; rowIndex < LAST_ROW; rowIndex++) {
			if (sheet.getRow(rowIndex) != null && sheet.getRow(rowIndex).getCell(1) != null
					&& sheet.getRow(rowIndex).getCell(1).getRichStringCellValue() != null) {
				final String label = sheet.getRow(rowIndex).getCell(1).getRichStringCellValue().getString();
				log("row " + rowIndex + ": " + label);
				if (label.contains("Ratio - ") && !label.contains("MOYENNE")) {
					result.savings.add(savingCreator.createSaving(sheet.getRow(rowIndex)));
				}
			}
		}
		return result;

	}

	private void log(final String s) {
		Logger.debug(s);
		// System.out.println(s);

	}
}
