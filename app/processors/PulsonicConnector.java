package processors;

import java.util.List;

import models.PulsonicValue;
import models.Weather;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.Http.Header;

public class PulsonicConnector {

	private static final String URL = "http://5.39.106.146/WEBSMETEOVISION_WEB/awws/WebSMeteoVision.awws";

	private static final String BODY_PREFIX = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\"><soap:Body>";
	private static final String BODY_SUFFIX = "</soap:Body></soap:Envelope>";

	private String cookie;

	public void update(final Weather weather) {
		final String openResult = openConnection();
		if ("1".equals(openResult)) {
			updateValues(weather);
			closeConnection();
		} else {
			Logger.error("Cannot connect to Pulsonic station, OPenConnection returns %s", openResult);
		}
	}

	private void updateValues(final Weather weather) {
		final PulsonicValue tempValue = getLastValue("Temp._inst");
		final PulsonicValue windValue = getLastValue("FF_moy");
		final PulsonicValue windDirValue = getLastValue("Dir._moy");
		final PulsonicValue rainValue = getLastValue("Cum._pluie");
		Logger.debug("values: %s, wind %s, wind dir %s, rain %s", tempValue, windValue, windDirValue, rainValue);
		weather.lastTimeStamp = tempValue.date;
		weather.temperature = tempValue.value;
		weather.windSpeed = windValue.value * 3600 / 1000;
		weather.windDirection = Weather.getWindDirection(windDirValue.value);
		weather.rain = rainValue.value;
		if (weather.rain > 0) {
			weather.current = "rain";
		}
	}

	private String openConnection() {
		final String postBody = "<OPenConnection xmlns=\"urn:WebSMeteoVision\"><sLogin xsd:type=\"xsd:string\">WebSrcTest1</sLogin><sPassword xsd:type=\"xsd:string\">gDV6ncUk55E</sPassword><IdClient xsd:type=\"xsd:string\">03590</IdClient></OPenConnection>";
		return postAction("OPenConnection", postBody);
	}

	private String closeConnection() {
		final String postBody = "<CloseConnection xmlns=\"urn:WebSMeteoVision\"></CloseConnection>";
		return postAction("CloseConnection", postBody);
	}

	private PulsonicValue getLastValue(final String param) {
		final String postBody = "<GetLastValue xmlns=\"urn:WebSMeteoVision\"><sNomStation xsd:type=\"xsd:string\">13_Marseille_GR</sNomStation><sListeParametre xsd:type=\"xsd:string\">"
				+ param
				+ "</sListeParametre><sTypeData xsd:type=\"xsd:string\">H</sTypeData><sFenetre xsd:type=\"xsd:string\">90</sFenetre></GetLastValue>";
		return PulsonicValue.createFrom(postAction("GetLastValue", postBody));
	}

	/* @return la chaine de caractères contenue dans le tag '<actionName>Result' */
	private String postAction(final String actionName, final String actionBody) {
		final String postBody = BODY_PREFIX + actionBody + BODY_SUFFIX;
		// Logger.debug("posting %s : %s", actionName, postBody);
		final WSRequest request = WS.url(URL).setHeader("Content-Type", "application/soap+xml")
				.setHeader("SOAPAction", "");
		if (cookie != null) {
			// Logger.debug("posting with cookie %s", cookie);
			request.setHeader("Cookie", cookie);
		}

		final HttpResponse httpResponse = request.body(postBody).post();
		// Logger.debug("response status %s :  %s", httpResponse.getStatus(),
		// httpResponse.getString());
		if (httpResponse.getStatus() == 200) {
			final List<Header> headers = httpResponse.getHeaders();
			for (final Header header : headers) {
				if (header.name.equals("Set-Cookie")) {
					cookie = header.values.get(0);
				}
			}
			// Logger.debug("cookie in response %s", cookie);
			return parseResponse(actionName, httpResponse.getString());
		} else {
			Logger.error("Pulsonic Station Http error %s", httpResponse.getStatus());
		}
		return null;
	}

	static String parseResponse(final String actionName, final String response) {
		return StringUtils.substringBetween(response, "<" + actionName + "Result>", "</" + actionName + "Result>");
	}
}
