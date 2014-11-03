package processors;

import java.util.List;

import models.User;
import notifiers.EssendexSmsSender;
import notifiers.MailSender;
import play.Logger;

public class Notifier {

	public static void savingsUploaded(final int year, final int week) {
		final List<User> mailUsers = User.findMailsRecipients();
		Logger.debug("Notifying by mail: %s", mailUsers);
		for (final User user : mailUsers) {
			MailSender.savingsUploaded(user, year, week);
		}

		final List<User> phoneUsers = User.findSmsRecipients();
		Logger.debug("Notifying by SMS: %s", phoneUsers);
		for (final User user : phoneUsers) {
			EssendexSmsSender.savingsUploaded(user, year, week);
		}

	}
}
