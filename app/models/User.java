package models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;

import notifiers.MailSender;
import play.Logger;
import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;

@Entity
public class User extends Model {

	private static final SimpleDateFormat SDF_PW = new SimpleDateFormat("HHddss");

	public static final int ROLE_TENANT = 0;
	public static final int ROLE_ADMIN = 1;
	public static final int ROLE_VISITOR = 2;

	@Required
	public String name;
	@Required
	@Unique
	public String login;
	@Required
	@Password
	public String password;

	/**
	 * vrai si cet utilisateur a "accepté" son compte : il a été redirigé vers
	 * account la première fois
	 */
	public boolean accepted;
	public Date lastLogin;

	public int role;

	public boolean byEmail;
	public boolean bySms;

	public String phone;

	/* Nb de visites de la page Mon arbre */
	public int meVisits;

	@Transient
	public String lastError;

	@Override
	public String toString() {
		return login;
	}

	public boolean isTenant() {
		return role == ROLE_TENANT;
	}

	public boolean isAdmin() {
		return role == ROLE_ADMIN;
	}

	public boolean isVisitor() {
		return role == ROLE_VISITOR;
	}

	public static User findByLogin(final String login) {
		return User.find("byLogin", login).first();
	}

	public void updateWith(final User u) {
		name = u.name;
		login = u.login;
		password = u.password;
		phone = u.phone;
		if (isLastAdmin() && !u.isAdmin()) {
			Logger.warn("Can't delete role admin for last admin %s", this);
		} else {
			role = u.role;
		}
		save();
	}

	public boolean isLastAdmin() {
		return isAdmin() && User.count("role=?", ROLE_ADMIN) == 1;
	}

	public void setNewPassword() {
		password = "TODO"
	}

	public void createNewPassword() {
		Logger.info("Create new password for %s", this);
		setNewPassword();
		save();
		MailSender.newPassword(this);
	}

	public static List<User> findAdmins() {
		return find("byRole", ROLE_ADMIN).fetch();
	}

	public Avatar findAvatar() {
		final Dwelling dwelling = Dwelling.findByUser(this);
		if (dwelling != null) {
			return Avatar.findByDwelling(dwelling);
		}
		return null;
	}

	public static List<User> findMailsRecipients() {
		return User.find("login is not null and byEmail=?", true).fetch();
	}

	public static List<User> findSmsRecipients() {
		return User.find("phone is not null and bySms=?", true).fetch();
	}

	public static boolean checkMagicNumber(final String login, final String magicNumber) {
		final User user = User.findByLogin(login);
		if (user != null) {
			return user.checkMagicNumber(magicNumber);
		}
		return false;
	}

	private boolean checkMagicNumber(final String magicNumber) {
		return createMagicNumber().equals(magicNumber);
	}

	public String createMagicNumber() {
		return encode(id, login);
	}

	static String encode(final long id, final String login) {
		String s = "";
		for (int i = login.length() - 1; i >= 0; i--) {
			final long l = login.charAt(i) + id;
			s += String.valueOf(l);
		}
		return s;
	}

}
