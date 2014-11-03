package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class ConsumptionFile extends Model {

	public Date uploadDate;
	public String name;
	public String localName;
	public int year;
	public int week;
	public boolean imported;

	public ConsumptionFile(final Date uploadDate, final String name, final String localName, final int year,
			final int week) {
		super();
		this.uploadDate = uploadDate;
		this.name = name;
		this.localName = localName;
		this.year = year;
		this.week = week;
	}

	@Override
	public String toString() {
		return "ConsumptionFile [uploadDate=" + uploadDate + ", name=" + name + ", year=" + year + ", week=" + week
				+ "]";
	}

}
