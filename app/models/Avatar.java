package models;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

@Entity
public class Avatar extends Model {

	public String name;

	@OneToOne
	public Dwelling dwelling;

	public Avatar(final String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return "Avatar [name=" + name + ", dwelling=" + dwelling + "]";
	}

	public static Avatar findByDwelling(final Dwelling dwelling) {
		return find("dwelling=?", dwelling).first();
	}
}
