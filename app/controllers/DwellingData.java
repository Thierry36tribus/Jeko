package controllers;

import models.Dwelling;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;
import controllers.minifymod.MinifyAndGzipResponse;

@With({ Secure.class, MinifyAndGzipResponse.class })
@Check("admin")
public class DwellingData extends Controller {

	public static void dwellings() {
		renderJSON(Utils.gson().toJson(Dwelling.find("order by label").fetch()));
	}

	public static void dwelling(final long dwellingId) {
		if (dwellingId == 0) {
			final Dwelling dwelling = new Dwelling();
			renderJSON(Utils.gson().toJson(dwelling));
		} else {
			renderJSON(Utils.gson().toJson(Dwelling.findById(dwellingId)));
		}
	}

	public static void saveDwelling(final long dwellingId, final String body) {
		final Dwelling dwelling = parseDwelling(body);
		final Dwelling existingDwelling = Dwelling.findById(dwelling.id);
		existingDwelling.updateWith(dwelling);
		renderJSON(Utils.gson().toJson(existingDwelling));
	}

	public static void deleteDwelling(final long dwellingId) {
		final Dwelling dwelling = Dwelling.findById(dwellingId);
		dwelling.delete();
		ok();
	}

	public static void createDwelling(final String body) {
		final Dwelling dwelling = parseDwelling(body);
		dwelling.create();
		renderJSON(Utils.gson().toJson(dwelling));
	}

	private static Dwelling parseDwelling(final String body) {
		Logger.debug("parseDwelling : %s", body);

		return Utils.gson().fromJson(body, Dwelling.class);
	}
}