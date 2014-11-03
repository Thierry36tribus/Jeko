package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class TipsFile extends Model {

	public Date uploadDate;
	public String name;
	public String localName;
	public boolean imported;

	public TipsFile(final Date uploadDate, final String name, final String localName) {
		super();
		this.uploadDate = uploadDate;
		this.name = name;
		this.localName = localName;
	}

	@Override
	public String toString() {
		return "TipsFile [uploadDate=" + uploadDate + ", name=" + name + ", localName=" + localName + ", imported="
				+ imported + "]";
	}

}
