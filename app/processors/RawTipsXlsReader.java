package processors;

import java.io.File;
import java.io.FileInputStream;

import models.EcoTip;
import models.RawTipsReadResult;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RawTipsXlsReader {

	private static final int FIRST_ROW = 1;

	private XSSFWorkbook workbook;

	public RawTipsReadResult read(final File file) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			workbook = new XSSFWorkbook(fis);
			return readSheet(getSheet("ECO-QUIZZ"));
		} catch (final Exception e) {
			// e.printStackTrace(System.out);
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

	private String getValue(final XSSFRow row, final int cellIndex) {
		final DataFormatter df = new DataFormatter();
		final XSSFCell cell = row.getCell(cellIndex);
		return df.formatCellValue(cell);
	}

	private RawTipsReadResult readSheet(final XSSFSheet sheet) {
		final RawTipsReadResult result = new RawTipsReadResult();
		for (int rowIndex = FIRST_ROW; rowIndex < sheet.getLastRowNum() + 1; rowIndex++) {
			if (sheet.getRow(rowIndex) != null) {
				final XSSFCell questionCell = sheet.getRow(rowIndex).getCell(1);
				if (questionCell != null && questionCell.getRichStringCellValue() != null) {
					final XSSFRow row = sheet.getRow(rowIndex);
					final String question = questionCell.getRichStringCellValue().getString();
					final String goodAnswer = getValue(row, 2);
					final String answer2 = getValue(row, 3);
					final String answer3 = getValue(row, 4);
					final String answer4 = getValue(row, 5);
					if (question != null && !question.isEmpty() && goodAnswer != null && !goodAnswer.isEmpty()) {
						final EcoTip tip = new EcoTip(question, goodAnswer, answer2, answer3, answer4);
						// System.out.println("row " + rowIndex + " : " + tip);
						result.tips.add(tip);
					}
				}
			}
		}
		return result;
	}
}
