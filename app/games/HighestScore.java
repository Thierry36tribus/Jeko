package games;

import java.util.List;

import javax.persistence.Query;

import models.User;
import play.db.jpa.JPA;

public class HighestScore {

	/** return 0 si pas le meilleur score, le nb de bonnes réponses sinon */
	public static int check(final User user) {
		if (user.isTenant()) {
			final Query query = JPA
					.em()
					.createQuery(
							"SELECT p.user,sum(p.nb) as score FROM PointsEvent p where p.origin='WIN_GAME' and p.user.role=0 group by p.user order by score desc");
			final List l = query.getResultList();
			if (!l.isEmpty()) {
				final Object[] first = (Object[]) l.get(0);
				final User theBest = (User) first[0];
				final int score = ((Number) first[1]).intValue();
				// Logger.debug("theBest is user %s, score  %s", theBest,
				// score);
				if (user.id == theBest.id) {
					return score;
				}
			}
		}
		return 0;
	}
}
