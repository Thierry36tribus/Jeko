package controllers;

import games.HighestScore;
import models.PointsEvent;
import models.UiPointsEvent;
import play.mvc.Controller;
import play.mvc.With;
import controllers.minifymod.MinifyAndGzipResponse;

@With({ Secure.class, MinifyAndGzipResponse.class })
public class Points extends Controller {

	public static void uiPointsEvents() {
		renderJSON(Utils.gson().toJson(UiPointsEvent.createList()));
	}

	public static void renderPointsArray() {
		renderJSON(Utils.gson().toJson(
				new Object[] { PointsEvent.sumPoints(Security.getConnectedUser()), UiPointsEvent.createList(),
						HighestScore.check(Security.getConnectedUser()) }));
	}
}