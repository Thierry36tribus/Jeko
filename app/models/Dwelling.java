package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.Logger;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;
import visual.FruitsCreator;
import visual.VisualElementsCreator;
import controllers.Security;

@Entity
public class Dwelling extends Model {

	@Required
	@Unique
	public String label;

	public int surface;

	@OneToOne
	public User user;

	@Override
	public boolean create() {
		final Avatar avatar = new Avatar("tree.svg");
		avatar.dwelling = this;
		avatar.create();
		return super.create();
	}

	public void updateWith(final Dwelling d) {
		Logger.debug("update dwelling %s with %s", this, d);
		label = d.label;
		user = d.user;
		surface = d.surface;
		save();
	}

	@Override
	public String toString() {
		return "Dwelling [label=" + label + ", user=" + user + "]";
	}

	public static Dwelling findByUser(final User user) {
		return find("byUser", user).first();
	}

	public static Dwelling findByLabel(final String dwellingLabel) {
		return find("label", dwellingLabel).first();
	}

	public boolean isAllowed() {
		final User currentUser = Security.getConnectedUser();
		return currentUser.isAdmin() || currentUser.id == user.id;
	}

	public Avatar getAvatar() {
		return Avatar.find("byDwelling", this).first();
	}

	public void updateVisualSavingsElements() {
		final VisualElementsCreator visualElementsCreator = new FruitsCreator(this);
		final List<Saving> savings = Saving.findByDwelling(this);
		for (final Saving saving : savings) {
			saving.updateVisualElements(visualElementsCreator);
		}
	}

	public static Dwelling findMine() {
		final User user = Security.getConnectedUser();
		Dwelling myDwelling = Dwelling.findByUser(user);
		if (myDwelling == null) {
			// pas un locataire, on prend le premier logement
			myDwelling = Dwelling.all().first();
		}
		return myDwelling;
	}
}
