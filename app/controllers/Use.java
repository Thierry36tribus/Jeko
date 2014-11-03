package controllers;

import java.util.Date;

import models.Saving;
import models.UiSaving;
import models.UiUser;
import models.User;
import models.Visit;
import play.mvc.Controller;
import play.mvc.With;
import processors.AppUse;
import controllers.minifymod.MinifyAndGzipResponse;

@With({ Secure.class, MinifyAndGzipResponse.class })
public class Use extends Controller {

	public static void visit(final int sw, final int sh, final int ww, final int wh) {
		final Visit visit = new Visit(new Date(), request.headers.get("user-agent").value(),
				Security.getConnectedUser(), sw, sh, ww, wh);
		visit.create();

		final int firstYear = Saving.FIRST_YEAR;
		final int firstWeek = Saving.FIRST_WEEK;
		final int lastWeekOfYear = UiSaving.getLastWeekOf(firstYear);

		renderJSON(new Object[] { UiUser.create(Security.getConnectedUser()), firstYear, firstWeek, lastWeekOfYear });
	}

	public static void winGame(final int points) {
		AppUse.winGame(points);
		Points.renderPointsArray();
	}

	/**
	 * Première ouverture du dialogue courbes détaillées lors d'une visite de la
	 * page 'Mon arbre'
	 */
	public static void charts() {
		final User user = Security.getConnectedUser();
		if (user.isTenant()) {
			user.meVisits++;
			user.save();
		}
		renderJSON(new Object[] { user.meVisits });
	}
}
