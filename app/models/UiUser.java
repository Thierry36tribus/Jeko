package models;

public class UiUser {

	public String login;
	public String name;
	public long dwellingId;
	public String dwellingLabel;
	public int points;
	public String avatarName;
	public boolean accepted;
	public int role;

	public boolean byEmail;
	public boolean bySms;

	public String phone;

	public int meVisits;

	public UiUser(final User user, final Dwelling dwelling) {
		this.login = user.login;
		this.name = user.name;
		this.byEmail = user.byEmail;
		this.bySms = user.bySms;
		this.phone = user.phone;
		Dwelling d = dwelling;
		if (d == null) {
			// admin ou visiteur, on prend le premier logement
			d = Dwelling.all().first();
		}
		this.dwellingId = d.id;
		this.dwellingLabel = d.label;
		final Avatar avatar = Avatar.findByDwelling(d);
		if (avatar != null) {
			this.avatarName = avatar.name;
		}
		this.accepted = user.accepted;
		this.role = user.role;
	}

	@Override
	public String toString() {
		return "UiUser [login=" + login + ", name=" + name + ", dwellingId=" + dwellingId + ", dwellingLabel="
				+ dwellingLabel + "]";
	}

	public static UiUser create(final User user) {
		final UiUser uiUser = new UiUser(user, Dwelling.findByUser(user));
		uiUser.points = PointsEvent.sumPoints(user);
		uiUser.meVisits = user.meVisits;
		return uiUser;
	}
}
