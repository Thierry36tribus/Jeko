package notifiers;

import models.User;
import play.Logger;
import esendex.sdk.java.ServiceFactory;
import esendex.sdk.java.model.domain.request.SmsMessageCollectionRequest;
import esendex.sdk.java.model.domain.request.SmsMessageRequest;
import esendex.sdk.java.model.domain.response.MessageResultResponse;
import esendex.sdk.java.service.BasicServiceFactory;
import esendex.sdk.java.service.MessagingService;
import esendex.sdk.java.service.auth.UserPassword;

public class EssendexSmsSender {

	public static void savingsUploaded(final User user, final int year, final int week) {

		/*
		 * if (true) { Logger.debug(
		 * "Envoi de SMS à %s (%s) désactivé pour ne pas tout dépenser...",
		 * user, user.phone); return; }
		 */

		final SmsMessageRequest m = new SmsMessageRequest(user.phone,
				"Vos consommations de la semaine sont arrivées sur JEKO : http://encerticus.36tribus.com");
		final SmsMessageCollectionRequest c = new SmsMessageCollectionRequest(getAccount(), m);
		m.setValidity(5);
		c.setFrom("JEKO");
		final BasicServiceFactory factory = ServiceFactory.createBasicAuthenticatingFactory(getUserPassword());
		final MessagingService messagingService = factory.getMessagingService();
		MessageResultResponse resp;
		try {
			resp = messagingService.sendMessages(c);
		} catch (final Exception e) {
			Logger.error(e, "SMS sending: error");
			return;
		}
		Logger.info("%s SMS sent", resp.getMessageIds().size());
		// pour savoir ce qui a marché / pas marché il suffit d'aller sur le
		// site d'Essendex

		// final SentService sentService = factory.getSentService();
		// final List<ResourceLinkResponse> messagesIds = ;
		// for (final ResourceLinkResponse messageId : messagesIds) {
		// try {
		// final SentMessageResponse messageSent =
		// sentService.getMessage(messageId.getId());
		// Logger.info("SMS sent: %s", messageSent);
		// } catch (final Exception e) {
		// Logger.error(e, "SMS error, unable to get status of sent messages");
		// }
		// }

	}

	private static String getAccount() {
		return "TODO";
	}

	private static UserPassword getUserPassword() {
		return new UserPassword("TODO","TODO");
	}

}
