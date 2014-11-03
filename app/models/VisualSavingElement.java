package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

/** les éléments qui représentent une économie hebdo */
@Entity
public class VisualSavingElement extends Model {

	@ManyToOne
	public Saving saving;

	/**
	 * Position relative à leur conteneur (l'arbre)
	 * 
	 */
	public int x;
	public int y;

	public VisualSavingElement(final Saving saving) {
		this.saving = saving;
	}

	@Override
	public String toString() {
		return "VisualSavingElement [saving=" + saving + ", x=" + x + ", y=" + y + "]";
	}

}
