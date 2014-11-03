package models;

import java.util.ArrayList;
import java.util.List;

public class RawSavingsReadResult {

	public int year;
	public int week;

	public List<RawSaving> savings;

	public RawSavingsReadResult(final int year, final int week) {
		super();
		this.year = year;
		this.week = week;
		this.savings = new ArrayList();
	}

}
