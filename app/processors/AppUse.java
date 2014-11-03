package processors;

import models.PointsEvent;
import controllers.Security;

public class AppUse {

	public static void winGame(final int points) {
		PointsEvent.createPointsEvent(Security.getConnectedUser(), points);
	}

}
