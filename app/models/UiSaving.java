package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class UiSaving {

	// N° semaine depuis la première de la période (index, commence à 1)
	public int week;

	public double global;
	// peut être null si pas détaillé
	public Double heating, electricity, water, hotWater;
	public Double heatingCons, electricityCons, waterCons, hotWaterCons;

	public List<UiVisualSavingElement> elts;

	public UiSaving(final int year, final int week, final double global, final Double heating,
			final Double electricity, final Double water, final Double hotWater, final Double heatingCons,
			final Double electricityCons, final Double waterCons, final Double hotWaterCons) {
		super();
		this.week = getWeekIndex(Saving.FIRST_WEEK, year, week);
		this.global = global;
		this.heating = heating;
		this.electricity = electricity;
		this.water = water;
		this.hotWater = hotWater;
		this.heatingCons = heatingCons;
		this.electricityCons = electricityCons;
		this.waterCons = waterCons;
		this.hotWaterCons = hotWaterCons;

		this.elts = new ArrayList();
	}

	public UiSaving(final int year, final int week, final double global) {
		this(year, week, global, null, null, null, null, null, null, null, null);
	}

	public UiSaving(final int year, final int week, final double global, final double heating,
			final double electricity, final double water, final double hotWater) {
		this(year, week, global, heating, electricity, water, hotWater, null, null, null, null);
	}

	public void addElement(final VisualSavingElement element) {
		elts.add(new UiVisualSavingElement(element.x, element.y));
	}

	@Override
	public String toString() {
		return "UiSaving [week=" + week + ", global=" + global + ", heating=" + heating + ", electricity="
				+ electricity + ", water=" + water + ", hotWater=" + hotWater + ", heatingCons=" + heatingCons
				+ ", electricityCons=" + electricityCons + ", waterCons=" + waterCons + ", hotWaterCons="
				+ hotWaterCons + "]";
	}

	static int getWeekIndex(final int firstWeek, final int year, final int week) {
		final int lastWeekOfYear = getLastWeekOf(year - 1);
		int weekIndex;
		if (week >= firstWeek) {
			weekIndex = week - firstWeek;
		} else {
			weekIndex = lastWeekOfYear + week - firstWeek;
		}
		return weekIndex + 1;
	}

	public static int getLastWeekOf(final int year) {
		final GregorianCalendar cal = new GregorianCalendar();
		cal.set(year, 0, 1);
		return cal.getActualMaximum(Calendar.WEEK_OF_YEAR);
	}

}
