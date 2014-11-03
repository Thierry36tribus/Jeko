package models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PulsonicValue {

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmm");

	public Date date;
	public double value;

	public PulsonicValue(final Date date, final double value) {
		super();
		this.date = date;
		this.value = value;
	}

	@Override
	public String toString() {
		return "PulsonicValue [date=" + date + ", value=" + value + "]";
	}

	public static PulsonicValue createFrom(final String string) {
		final String[] items = string.split(",");
		try {
			final Date utcDate = SDF.parse(items[0]);
			final Date localDate = new Date(utcDate.getTime() + TimeZone.getDefault().getOffset(utcDate.getTime()));
			return new PulsonicValue(localDate, Double.valueOf(items[1]));
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
