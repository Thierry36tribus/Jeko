package models;

import games.HighestScore;

import java.util.ArrayList;
import java.util.List;

import controllers.Security;

public class UiDwelling {

	public long id;
	public String label;
	public int surface;
	public String avatar;
	public boolean canSee;
	public List<UiSaving> savings;
	public int points;
	public boolean hasHighestScore;

	public UiDwelling(final long dwellingId, final String dwellingLabel, final int surface, final Avatar avatar,
			final boolean canSee) {
		super();
		this.id = dwellingId;
		this.label = dwellingLabel;
		this.surface = surface;
		this.avatar = avatar.name;
		this.canSee = canSee;
		this.savings = new ArrayList();
	}

	public void addSaving(final Saving saving, final boolean withConsumptions) {
		UiSaving uiSaving;
		if (withConsumptions) {
			uiSaving = new UiSaving(saving.year, saving.week, saving.movingAverageSaving, saving.heatingSaving,
					saving.electricitySaving, saving.waterSaving, saving.hotWaterSaving, saving.heatingConsumption,
					saving.electricityConsumption, saving.waterConsumption, saving.hotWaterConsumption);
		} else {
			uiSaving = new UiSaving(saving.year, saving.week, saving.movingAverageSaving, saving.heatingSaving,
					saving.electricitySaving, saving.waterSaving, saving.hotWaterSaving);
		}
		final List<VisualSavingElement> elements = saving.findVisualElements();
		for (final VisualSavingElement element : elements) {
			uiSaving.addElement(element);
		}
		savings.add(uiSaving);
	}

	@Override
	public String toString() {
		return "UiDwelling [dwellingId=" + id + ", dwellingLabel=" + label + ", savings=" + savings + "]";
	}

	public static UiDwelling create(final Dwelling dwelling, final boolean details) {
		final User user = Security.getConnectedUser();
		final boolean canSee = user.isAdmin() || user.isVisitor()
				|| (dwelling.user != null && (dwelling.user.id == user.id));
		final UiDwelling uiDwelling = new UiDwelling(dwelling.id, dwelling.label, dwelling.surface,
				dwelling.getAvatar(), canSee);
		final List<Saving> savings = Saving.findByDwelling(dwelling);
		for (final Saving saving : savings) {
			uiDwelling.addSaving(saving, details);
		}
		uiDwelling.points = PointsEvent.sumPoints(user);
		if (dwelling.user != null) {
			uiDwelling.hasHighestScore = HighestScore.check(dwelling.user) > 0;
		}
		return uiDwelling;
	}
}
