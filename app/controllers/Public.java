package controllers;

import java.util.Date;

import models.User;
import models.UserAgentTest;
import play.Logger;
import play.mvc.Controller;

public class Public extends Controller {

	public static void forget() {
		render();
	}

	public static void sendNewPassword(final String email) {
		final User user = User.findByLogin(email);
		if (user != null) {
			user.createNewPassword();
		}
		Application.app();
	}

	public static void fromMail(final String userName, final String magicNumber) {
		if (User.checkMagicNumber(userName, magicNumber)) {
			session.put("username", userName);
		}
		Application.app();
	}

	public static void test(final String dwellingLabel) {
		final String userAgentString = request.headers.get("user-agent").value();
		final UserAgentTest uaTest = new UserAgentTest(new Date(), dwellingLabel, userAgentString);
		uaTest.parse();
		uaTest.save();
		Logger.debug("User Agent test for dwelling %s: %s **** ua: %s", dwellingLabel, uaTest.result, userAgentString);
		render(uaTest);
	}
}