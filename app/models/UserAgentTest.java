package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;
import useragentutils.UserAgent;

@Entity
public class UserAgentTest extends Model {

	public Date uaDate;
	public String dwellingLabel;
	public String ua;
	public String result;

	public UserAgentTest(final Date uaDate, final String dwellingLabel, final String ua) {
		super();
		this.uaDate = uaDate;
		this.dwellingLabel = dwellingLabel;
		this.ua = ua;
	}

	public String parse() {
		final UserAgent userAgent = UserAgent.parseUserAgentString(ua);
		result = userAgent.getBrowser().getName() + " version " + userAgent.getBrowserVersion() + " - "
				+ userAgent.getOperatingSystem().getName();
		return result;
	}

	@Override
	public String toString() {
		return "UserAgentTest [uaDate=" + uaDate + ", dwellingLabel=" + dwellingLabel + ", ua=" + ua + ", result="
				+ result + "]";
	}

}
