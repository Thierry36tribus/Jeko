package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Visit extends Model {

	public Date vDate;

	public String userAgent;

	@ManyToOne
	public User user;

	public int sw;
	public int sh;
	public int ww;
	public int wh;

	public Visit(final Date vDate, final String userAgent, final User user, final int sw, final int sh, final int ww,
			final int wh) {
		super();
		this.vDate = vDate;
		this.userAgent = userAgent;
		this.user = user;
		this.sw = sw;
		this.sh = sh;
		this.ww = ww;
		this.wh = wh;
	}

	@Override
	public String toString() {
		return "Visit [vDate=" + vDate + ", userAgent=" + userAgent + ", user=" + user + ", sw=" + sw + ", sh=" + sh
				+ ", ww=" + ww + ", wh=" + wh + "]";
	}

	public static List<Visit> findByUser(final long userId) {
		return find("user =? order by vDate desc", User.findById(userId)).fetch(100);
	}

	public static long countByUser(final long userId) {
		return Visit.count("user =?", User.findById(userId));
	}

}
