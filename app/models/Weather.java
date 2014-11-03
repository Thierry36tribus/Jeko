package models;

import java.util.Date;

import javax.persistence.Entity;

import play.Logger;
import play.db.jpa.Model;
import processors.ForecastIoConnector;
import processors.PulsonicConnector;

@Entity
public class Weather extends Model {

	private static String[] WIND_DIRECTIONS = { "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSO", "SO",
			"OSO", "O", "ONO", "NO", "NNO" };
	public String current;
	public String nextHour;

	public double temperature;
	public double windSpeed;
	public String windDirection;
	public double rain;

	public Date lastUpdate;
	public Date lastTimeStamp;

	@Override
	public String toString() {
		return "Weather [current=" + current + ", temperature=" + temperature + ", windSpeed=" + windSpeed
				+ ", windDirection=" + windDirection + ", rain=" + rain + ", lastTimeStamp=" + lastTimeStamp + "]";
	}

	public static void update() {
		Weather weather = getWeather();
		if (weather == null) {
			weather = new Weather();
		}
		try {
			new ForecastIoConnector().update(weather);
		} catch (final Exception e) {
			Logger.error(e, "Unable to get weather from forecast.io");
		}
		Logger.info("Weather from forecast.io: %s", weather);
		try {
			new PulsonicConnector().update(weather);
		} catch (final Exception e) {
			Logger.error(e, "Unable to get weather from Pulsonic station");
		}
		Logger.info("Weather from pulsonic station: %s", weather);
		weather.lastUpdate = new Date();
		weather.save();
	}

	public static Weather getWeather() {
		return Weather.all().first();
	}

	public static double getCelsius(final double fahrenheit) {
		return (fahrenheit - 32) * 5 / 9;
	}

	public static String getWindDirection(final double windBearing) {
		final int val = (int) ((windBearing / 22.5) + .5);
		return Weather.WIND_DIRECTIONS[val % 16];
	}

	public static double getKmh(final double milesPerHour) {
		return 1.609 * milesPerHour;
	}

}
