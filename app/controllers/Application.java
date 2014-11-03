package controllers;

import games.GameType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import models.Avatar;
import models.Customization;
import models.DecorElement;
import models.Dwelling;
import models.PointsEvent;
import models.UiDecorElement;
import models.UiDwelling;
import models.UiUser;
import models.User;
import models.Weather;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;
import controllers.minifymod.MinifyAndGzipResponse;

@With({ Secure.class, MinifyAndGzipResponse.class })
public class Application extends Controller {

	public static void app() {
		redirect("/app/");
	}

	public static void administration() {
		redirect("/app/#/admin/");
	}

	public static void configuration() {
		redirect("/app/#/config/");
	}

	public static void checkAdmin() {
		if (Security.getConnectedUser().isAdmin()) {
			final String[] infos = { "", "" };
			final User user = Security.getConnectedUser();
			infos[0] = user.login;
			infos[1] = user.name;
			renderJSON(infos);
		}
		renderJSON(new String[] {});
	}

	public static void uiUser() {
		renderJSON(Utils.gson().toJson(UiUser.create(Security.getConnectedUser())));
	}

	public static void uiDwelling(final long dwellingId) {
		// le dwelling demandé si autorisé, sinon le dwelling du user courant
		Dwelling dwelling = Dwelling.findById(dwellingId);
		final User user = Security.getConnectedUser();
		if (user.isTenant()) {
			if (dwelling != null && dwelling.user.id != user.id) {
				dwelling = Dwelling.findByUser(user);
			}
		}
		renderJSON(Utils.gson().toJson(UiDwelling.create(dwelling, true)));
	}

	/**
	 * Le dwelling demandé en premier
	 */
	public static void uiDwellingsMineFirst(final long dwellingId) {
		Dwelling myDwelling;
		if (dwellingId == 0) {
			myDwelling = Dwelling.findMine();
		} else {
			myDwelling = Dwelling.findById(dwellingId);
		}
		final List<Dwelling> dwellings = Dwelling.all().fetch();
		final List<UiDwelling> uiDwellings = new ArrayList();
		uiDwellings.add(UiDwelling.create(myDwelling, true));
		for (final Dwelling dwelling : dwellings) {
			if (dwelling.id != myDwelling.id) {
				uiDwellings.add(UiDwelling.create(dwelling, true));
			}
		}
		renderJSON(Utils.gson().toJson(uiDwellings));
	}

	public static void weather() {
		renderJSON(Utils.gson().toJson(Weather.getWeather()));
	}

	public static void saveUser(final String body) {
		final User newUser = Utils.gson().fromJson(body, User.class);
		final User user = Security.getConnectedUser();
		user.name = newUser.name;
		user.byEmail = newUser.byEmail;
		user.bySms = newUser.bySms;
		user.phone = newUser.phone;
		user.accepted = true;
		user.save();
		uiUser();
	}

	public static void setPassword(final String pwd) {
		final User user = Security.getConnectedUser();
		Logger.info("change password of %s", user);
		user.password = pwd;
		user.save();
		ok();
	}

	public static void customize(final String body) {
		final User user = Security.getConnectedUser();
		final Avatar avatar = user.findAvatar();
		final Customization custom = Utils.gson().fromJson(body, Customization.class);
		avatar.name = custom.avatar;
		avatar.save();
		final Dwelling dwelling = Dwelling.findByUser(user);
		if (dwelling != null) {
			dwelling.updateVisualSavingsElements();
		}
		ok();
	}

	public static void shop() {
		final List<DecorElement> elements = DecorElement.findToSell();
		renderJSON(elements);
	}

	public static void buy(final String body) {
		final DecorElement[] elements = Utils.gson().fromJson(body, DecorElement[].class);
		int total = 0;
		for (final DecorElement elt : elements) {
			total += elt.price;
		}
		if (total > PointsEvent.sumPoints(Security.getConnectedUser())) {
			error(500, "Pas assez de points");
		} else {
			for (final DecorElement elt : elements) {
				final DecorElement element = DecorElement.findById(elt.id);
				element.purchase = PointsEvent.createPointsEvent(Security.getConnectedUser(), element);
				element.save();
			}
			ok();
		}
	}

	public static void gift(final int nb) {
		PointsEvent.createPointsEvent(Security.getConnectedUser(), nb, true);
		renderText("Cadeau !!! " + nb + " abeilles de plus");
	}

	public static void decor() {
		final List<UiDecorElement> decor = new ArrayList();
		final List<DecorElement> elements = DecorElement.findBought();
		elements.add(new DecorElement("sky.svg", 1000, true));
		elements.add(new DecorElement("hill1.svg", 730, true));
		elements.add(new DecorElement("hill2.svg", 720, true));
		elements.add(new DecorElement("meadow.svg", 50, true));

		Collections.sort(elements, new Comparator<DecorElement>() {
			@Override
			public int compare(final DecorElement d1, final DecorElement d2) {
				return Double.compare(d1.z, d2.z);
			}
		});

		for (final DecorElement element : elements) {
			decor.add(new UiDecorElement(element));
		}

		renderJSON(decor);
	}

	public static void gameTypes() {
		final List<GameType> gameTypes = GameType.all();
		renderJSON(gameTypes);
	}

	/** Provisoire */
	public static void resetOrchard() {
		final List<DecorElement> decorElements = DecorElement.all().fetch();
		for (final DecorElement decor : decorElements) {
			decor.purchase = null;
			decor.save();
		}

		final List<Avatar> avatars = Avatar.all().fetch();
		for (final Avatar avatar : avatars) {
			avatar.name = "default-tree.svg";
			avatar.save();
		}
		final List<Dwelling> dwellings = Dwelling.all().fetch();
		for (final Dwelling dwelling : dwellings) {
			dwelling.updateVisualSavingsElements();
		}

		ok();
	}

	/** Provisoire */
	public static void dummyGift() {
		Logger.debug("gift");
		PointsEvent.createPointsEvent(Security.getConnectedUser(), 100, true);
		ok();
	}
}