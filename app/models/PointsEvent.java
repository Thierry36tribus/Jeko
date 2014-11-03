package models;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Query;

import org.apache.commons.lang.time.DateUtils;

import play.Logger;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Model;

@Entity
public class PointsEvent extends Model {

	public static final String ORIGIN_DECOR = "DECOR";
	public static final String ORIGIN_GIFT = "GIFT";
	public static final String ORIGIN_WIN_GAME = "WIN_GAME";

	@Required
	public Date eventDate;

	@Required
	@ManyToOne
	public User user;

	@Required
	public String origin;

	public int nb;

	public PointsEvent(final Date date, final User user, final String origin, final int nb) {
		super();
		this.eventDate = date;
		this.user = user;
		this.origin = origin;
		this.nb = nb;
	}

	@Override
	public String toString() {
		return "PointsEvent [eventDate=" + eventDate + ", user=" + user + ", origin=" + origin + ", nb=" + nb + "]";
	}

	public static PointsEvent createPointsEvent(final User user, final Object origin) {
		return createPointsEvent(user, origin, false);
	}

	public static PointsEvent createPointsEvent(final User user, final Object origin, final boolean gift) {
		int nb;
		if (origin instanceof Integer) {
			return createPointsEvent(user, (gift ? ORIGIN_GIFT : ORIGIN_WIN_GAME), ((Integer) origin).intValue());
		}
		nb = PointsScale.getNbPoints(origin);
		return createPointsEvent(user, origin, nb);
	}

	public static PointsEvent createPointsEvent(final User user, final Object origin, final int nb) {
		if (nb != 0) {
			String originAsText;
			if (origin instanceof String) {
				originAsText = (String) origin;
			} else if (origin instanceof DecorElement) {
				originAsText = ORIGIN_DECOR;
			} else {
				originAsText = String.valueOf(origin);
			}
			final PointsEvent event = new PointsEvent(new Date(), user, originAsText, nb);
			Logger.debug("PointsEvent created: %s", event);

			event.create();
			return event;
		}
		return null;
	}

	public static int sumPoints(final User user) {
		final Query query = JPA.em().createQuery("select SUM(evt.nb) from PointsEvent evt where evt.user=?1");
		query.setParameter(1, user);
		final Object result = query.getSingleResult();
		if (result instanceof Number) {
			// non null, quoi...
			return ((Number) result).intValue();
		}
		return 0;
	}

	public static PointsEvent find(final User user, final Date date, final String origin) {
		final Date from = DateUtils.truncate(new Date(), Calendar.DATE);
		final Date to = DateUtils.addDays(from, 1);
		return PointsEvent.find("user=? and origin=? and eventDate>=? and eventDate <?", user, origin, from, to)
				.first();
	}

	public static List<PointsEvent> findByUser(final User user, final int nbToFetch) {
		return PointsEvent.find("user=? order by eventDate desc", user).fetch(nbToFetch);
	}
}
