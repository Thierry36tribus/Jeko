package controllers;

import java.util.Date;

import models.User;
import models.UserEvent;
import play.Logger;

public class Security extends Secure.Security {

	static boolean authentify(final String username, final String password) {
		if (User.count() == 0) {
			return "admin".equalsIgnoreCase(username) && "theboss".equalsIgnoreCase(password);
		} else {
			final User user = User.findByLogin(username);
			final boolean ok = (user != null && password != null && password.equals(user.password));
			if (ok) {
				user.lastLogin = new Date();
				user.save();
			}
			return ok;
		}
	}

	public static User getConnectedUser() {
		return User.findByLogin(connected());
	}

	static boolean check(final String profile) {
		final User user = getConnectedUser();
		if (user != null && "super-admin".equals(profile)) {
			return "chaman@36tribus.com".equals(user.login);
		}
		if (user != null && "admin".equals(profile)) {
			return user.role == User.ROLE_ADMIN;
		} else {
			return false;
		}
	}

	static void onAuthenticated() {
		Logger.debug("onAuthenticated, user: %s", getConnectedUser());
		createUserEvent(UserEvent.TYPE_LOGIN);
	}

	static void onDisconnect() {
		Logger.debug("onDisconnect, user: %s", getConnectedUser());
		createUserEvent(UserEvent.TYPE_LOGOUT);
	}

	private static final void createUserEvent(final int type) {
		final UserEvent event = new UserEvent(new Date(), getConnectedUser(), type);
		event.create();
	}

}
