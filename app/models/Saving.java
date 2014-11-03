package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;
import processors.MovingAverageCalculator;
import visual.VisualElementsCreator;

@Entity
public class Saving extends Model {

	public static int FIRST_WEEK = 20;
	public static int FIRST_YEAR = 2014;

	public static final double UNKNOWN_VALUE = 99999;

	public int year;
	public int week;
	@Required
	@ManyToOne
	public Dwelling dwelling;

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

	public double movingAverage;
	public double movingAverageRef;
	public double movingAverageSaving;

	public Saving(final int year, final int week, final Dwelling dwelling, final double globalSaving,
			final double heatingSaving, final double heatingConsumption, final double heatingRef,
			final double electricitySaving, final double electricityConsumption, final double electricityRef,
			final double waterSaving, final double waterConsumption, final double waterRef,
			final double hotWaterSaving, final double hotWaterConsumption, final double hotWaterRef) {
		super();
		this.year = year;
		this.week = week;
		this.dwelling = dwelling;

		this.globalSaving = globalSaving;

		this.heatingSaving = heatingSaving;
		this.heatingConsumption = heatingConsumption;
		this.heatingRef = heatingRef;

		this.electricitySaving = electricitySaving;
		this.electricityConsumption = electricityConsumption;
		this.electricityRef = electricityRef;

		this.waterSaving = waterSaving;
		this.waterConsumption = waterConsumption;
		this.waterRef = waterRef;

		this.hotWaterSaving = hotWaterSaving;
		this.hotWaterConsumption = hotWaterConsumption;
		this.hotWaterRef = hotWaterRef;
	}

	public void createOrUpdate(final VisualElementsCreator visualElementsCreator) {
		final Saving saving = createOrUpdateWithoutElements();
		saving.createVisualElements(visualElementsCreator);
	}

	private void createVisualElements(final VisualElementsCreator visualElementsCreator) {
		final List<VisualSavingElement> elements = visualElementsCreator.create(this);
		for (final VisualSavingElement element : elements) {
			element.create();
		}
	}

	public void updateVisualElements(final VisualElementsCreator visualElementsCreator) {
		deleteVisualSavingElements();
		createVisualElements(visualElementsCreator);
	}

	private Saving createOrUpdateWithoutElements() {
		final Saving existingSaving = Saving.find(year, week, dwelling);
		if (existingSaving == null) {
			calculateMovingAverage();
			create();
			return this;
		} else {
			existingSaving.globalSaving = globalSaving;

			existingSaving.heatingSaving = heatingSaving;
			existingSaving.heatingConsumption = heatingConsumption;
			existingSaving.heatingRef = heatingRef;

			existingSaving.electricitySaving = electricitySaving;
			existingSaving.electricityConsumption = electricityConsumption;
			existingSaving.electricityRef = electricityRef;

			existingSaving.waterSaving = waterSaving;
			existingSaving.waterConsumption = waterConsumption;
			existingSaving.waterRef = waterRef;

			existingSaving.hotWaterSaving = hotWaterSaving;
			existingSaving.hotWaterConsumption = hotWaterConsumption;
			existingSaving.hotWaterRef = hotWaterRef;

			existingSaving.calculateMovingAverage();
			existingSaving.save();
			existingSaving.deleteVisualSavingElements();
			return existingSaving;
		}
	}

	public void calculateMovingAverage() {
		final List<Saving> savings = new ArrayList();
		savings.add(this);
		savings.addAll(Saving.findBeforeDesc(this.year, this.week, this.dwelling));
		Logger.debug("calculateMovingAverage for saving %s with savings %s", this, savings);
		final MovingAverageResult result = new MovingAverageCalculator().calculate(savings, this.week);
		Logger.debug("calculateMovingAverage for %s: %s", this, result);
		movingAverage = result.movingAverageConsumption;
		movingAverageRef = result.movingAverageRef;
		movingAverageSaving = result.saving;
	}

	@Override
	public String toString() {
		return "Saving [year=" + year + ", week=" + week + ", dwelling=" + dwelling + ", movingAverageSaving="
				+ movingAverageSaving + "]";
	}

	public static List<Saving> find(final int year, final int week) {
		return find("year=? and week=? order by year,week desc", year, week).fetch();
	}

	public static Saving find(final int year, final int week, final Dwelling dwelling) {
		return find("year=? and week=? and dwelling=?", year, week, dwelling).first();
	}

	public static List<Saving> findBefore(final int year, final int week, final Dwelling dwelling) {
		return find("year * 100 + week <? and dwelling=?", year * 100 + week, dwelling).fetch();
	}

	public static List<Saving> findBeforeDesc(final int year, final int week, final Dwelling dwelling) {
		return find("year * 100 + week <? and dwelling=? order by year desc,week desc", year * 100 + week, dwelling)
				.fetch();
	}

	public static List<Saving> findByDwelling(final Dwelling dwelling) {
		return find("dwelling=? order by year desc,week desc", dwelling).fetch();
	}

	public static Saving findLast(final Dwelling dwelling) {
		return find("dwelling=? order by year desc,week desc", dwelling).first();
	}

	public static List<Saving> findLastWeek() {
		final int nbDwellings = (int) Dwelling.count();
		return find("order by year desc,week desc").fetch(nbDwellings);
	}

	public List<VisualSavingElement> findVisualElements() {
		return VisualSavingElement.find("bySaving", this).fetch();
	}

	public void deleteVisualSavingElements() {
		VisualSavingElement.delete("saving=?", this);

	}

}
