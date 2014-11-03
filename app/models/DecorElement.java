package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import play.db.jpa.Model;

@Entity
public class DecorElement extends Model {

	public String image;
	public int price;
	public int x;
	public int y;

	public int z;

	@OneToOne
	public PointsEvent purchase;

	public boolean background;

	public DecorElement() {
	}

	public DecorElement(final String image, final int z, final boolean background) {
		super();
		this.image = image;
		this.z = z;
		this.background = background;
	}

	@Override
	public String toString() {
		return "DecorElement [image=" + image + ", price=" + price + ", x=" + x + ", y=" + y + ", z=" + z
				+ ", purchase=" + purchase + "]";
	}

	public static List<DecorElement> findToSell() {
		return find("purchase is null order by price desc").fetch();
	}

	public static List<DecorElement> findBought() {
		return find("purchase is not null order by z").fetch();
	}

}
