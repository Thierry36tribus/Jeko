package processors;

import java.util.Date;

import models.Weather;
import play.libs.WS;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ForecastIoConnector {

	public void update(final Weather weather) {
		final JsonElement jsonElement = WS
				.url("https://api.forecast.io/forecast/4cd9286077ef26e58cc730b82c24274a/43.3,5.37").get().getJson();
		final JsonObject currently = jsonElement.getAsJsonObject().get("currently").getAsJsonObject();
		weather.current = currently.get("icon").getAsString();
		weather.lastTimeStamp = new Date(currently.get("time").getAsLong() * 1000);
		weather.nextHour = jsonElement.getAsJsonObject().get("currently").getAsJsonObject().get("icon").getAsString();

		// on récupère des mesures, qui seront normalement écrasées par celles
		// de la station météo. Si jamais celle-ci ne répond pas, on aura quand
		// même des mesures...
		weather.temperature = Weather.getCelsius(currently.get("temperature").getAsDouble());
		weather.windSpeed = Weather.getKmh(currently.get("windSpeed").getAsDouble());
		if (weather.windSpeed > 0) {
			weather.windDirection = Weather.getWindDirection(currently.get("windBearing").getAsDouble());
		} else {
			weather.windDirection = null;
		}
	}

}
