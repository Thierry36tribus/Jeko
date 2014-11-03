package controllers;

import games.HighestScore;

import java.util.List;

import models.Dwelling;
import models.PointsEvent;
import models.Saving;
import models.User;
import models.Weather;
import notifiers.MailSender;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;
import processors.PulsonicConnector;

@With(Secure.class)
@Check("super-admin")
public class Tools extends Controller {

	public static void testMail() {
		MailSender.test();
		renderText("Check your emails!");
	}

	public static void updateAllVisualSavingElements() {
		final List<Dwelling> dwellings = Dwelling.all().fetch();
		for (final Dwelling dwelling : dwellings) {
			dwelling.updateVisualSavingsElements();
		}
		renderText("done");
	}

	public static void updateMovingAverage() {
		final List<Saving> savings = Saving.all().fetch();
		for (final Saving saving : savings) {
			saving.calculateMovingAverage();
			saving.save();
		}
		renderText("done");
	}

	public static void updateWeather() {
		Weather.update();
		renderText("weather updated to " + Weather.getWeather().current);
	}

	public static void pulso() {
		new PulsonicConnector().update(Weather.getWeather());
		renderText("done");
	}

	public static void cancelTest2015() {
		final List<Saving> savings = Saving.all().fetch();
		for (final Saving saving : savings) {
			if (saving.week >= 50) {
				saving.week -= 30;
			} else {
				saving.week += 22;
				saving.year = 2014;
			}
			saving.save();
		}
		renderText("done");
	}

	public static void test2015() {
		final List<Saving> savings = Saving.all().fetch();
		for (final Saving saving : savings) {
			if (saving.week < 23) {
				saving.week += 30;
			} else {
				saving.week -= 22;
				saving.year = 2015;
			}
			saving.save();
		}
		renderText("done");
	}

	public static void checkScore() {
		renderText("highest score? " + HighestScore.check(Security.getConnectedUser()));
	}

	/** Donne 10 abeilles à chacun */
	public static void giftForAll() {
		Logger.debug("gift for all");
		final List<User> users = User.all().fetch();
		for (final User user : users) {
			PointsEvent.createPointsEvent(user, 10, true);
		}
		renderText("done");
	}
}
