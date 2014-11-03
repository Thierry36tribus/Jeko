package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class UserEvent extends Model {

	public static final int TYPE_LOGIN = 1;
	public static final int TYPE_LOGOUT = 2;

	@Required
	public Date eventDate;

	@Required
	@ManyToOne
	public User user;

	@Required
	public int eventType;

	public UserEvent(final Date eventDate, final User user, final int eventType) {
		super();
		this.eventDate = eventDate;
		this.user = user;
		this.eventType = eventType;
	}

	public static List<UserEvent> findByUser(final long userId) {
		return find("user =? order by eventDate desc", User.findById(userId)).fetch(100);
	}

	@Override
	public String toString() {
		return "UserEvent [eventDate=" + eventDate + ", user=" + user + ", eventType=" + eventType + "]";
	}

}
