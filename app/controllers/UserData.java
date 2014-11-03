package controllers;

import models.PointsEvent;
import models.UiPointsEvent;
import models.User;
import models.UserEvent;
import models.Visit;
import notifiers.MailSender;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;
import controllers.minifymod.MinifyAndGzipResponse;

@With({ Secure.class, MinifyAndGzipResponse.class })
@Check("admin")
public class UserData extends Controller {

	public static void users() {
		renderJSON(Utils.gson().toJson(User.find("order by name").fetch()));
	}

	public static void user(final long userId) {
		if (userId == 0) {
			final User user = new User();
			renderJSON(Utils.gson().toJson(user));
		} else {
			renderJSON(Utils.gson().toJson(User.findById(userId)));
		}

	}

	public static void saveUser(final long userId, final String body) {
		Logger.debug("saveUser(%s,%s)", userId, body);
		final User user = parseUser(body);
		final User existingUser = User.findById(user.id);
		Logger.debug("update user %s with %s", existingUser, user);
		existingUser.updateWith(user);
		renderJSON(Utils.gson().toJson(existingUser));
	}

	public static void deleteUser(final long userId) {
		Logger.debug("deleteUser(%s)", userId);
		final User user = User.findById(userId);
		if (user.isLastAdmin()) {
			Logger.warn("Can't delete last admin %s", user);
			user.lastError = "Impossible de supprimer le dernier administrateur";
			renderJSON(Utils.gson().toJson(user));
		} else {
			try {
				user.delete();
			} catch (final Exception e) {
				Logger.error(e, "Unable to delete %s", user);
				user.lastError = "Suppression impossible";
				renderJSON(Utils.gson().toJson(user));
			}
		}
		ok();
	}

	public static void createUser(final String body) {
		final User user = parseUser(body);
		if (user.findByLogin(user.login) == null) {
			Logger.debug("create new user %s", user);
			user.setNewPassword();
			user.create();
		} else {
			Logger.warn("Création user %s refusée, login existant", user);
			user.lastError = "Adresse déjà utilisée";
		}
		renderJSON(Utils.gson().toJson(user));
	}

	public static void sendNewUser(final long userId) {
		try {
			MailSender.newUser((User) User.findById(userId));
			ok();
		} catch (final Exception e) {
			Logger.error(e, "Unable to send mail to user %s", userId);
			error(500, "Unable to send mail");
		}
	}

	public static void events(final long userId) {
		renderJSON(Utils.gson().toJson(UserEvent.findByUser(userId)));
	}

	public static void visits(final long userId) {
		renderJSON(Utils.gson().toJson(new Object[] { Visit.countByUser(userId), Visit.findByUser(userId) }));
	}

	public static void pointsEvents(final long userId) {
		final User user = User.findById(userId);
		renderJSON(Utils.gson().toJson(
				new Object[] { PointsEvent.sumPoints(user), UiPointsEvent.createList(user, 100) }));
	}

	private static User parseUser(final String body) {
		return Utils.gson().fromJson(body, User.class);
	}

}