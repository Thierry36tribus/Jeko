package notifiers;

import models.User;

import org.apache.commons.mail.EmailException;

import play.Logger;
import play.mvc.Mailer;

public class MailSender extends Mailer {

	private static final String SENDER = "TODO";
	private static final String REPLY_TO = "TODO";

	public static void newUser(final User user) throws EmailException {
		Logger.info("send mail to new user %s", user);
		setCommons();
		addRecipient(user.login);
		setSubject("JEKO : votre compte a été créé");
		send(user);
	}

	public static void newPassword(final User user) {
		setCommons();
		addRecipient(user.login);
		setSubject("JEKO : votre nouveau mot de passe");
		send(user);
	}

	public static void savingsUploaded(final User user, final int year, final int week) {
		setCommons();
		addRecipient(user.login);
		setSubject("JEKO : les consommations de la semaine à la Grotte Rolland sont arrivées !");
		final String magicNumber = user.createMagicNumber();
		send(user, year, week, magicNumber);
	}

	private static void setCommons() {
		setFrom(SENDER);
		setReplyTo(REPLY_TO);
	}

	public static void test() {
		Logger.info("testing mail");
		setCommons();
		addRecipient("TODO");
		setSubject("JEKO : test");
		send();
	}
}
