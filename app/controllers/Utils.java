package controllers;

import java.util.Calendar;
import java.util.Date;

import models.DateGsonDeserializer;
import models.DateGsonSerializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {

	public static Gson gson() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Date.class, new DateGsonSerializer());
		gsonBuilder.registerTypeAdapter(Date.class, new DateGsonDeserializer());
		return gsonBuilder.create();
	}

	public static Date getFirstDateOfWeek(final int year, final int week) {
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.WEEK_OF_YEAR, week);
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return cal.getTime();
	}

}
