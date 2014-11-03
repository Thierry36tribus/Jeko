package models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WeatherTest {

	@Test
	public void testWindDirection() {
		assertEquals("359", "N", Weather.getWindDirection(359.0));
		assertEquals("0", "N", Weather.getWindDirection(0));
		assertEquals("11", "N", Weather.getWindDirection(11));
		assertEquals("22", "NNE", Weather.getWindDirection(22));
		assertEquals("180", "S", Weather.getWindDirection(180));
		assertEquals("340", "NNO", Weather.getWindDirection(340));
		assertEquals("319", "NO", Weather.getWindDirection(319));
	}

	@Test
	public void testTemperatureConversion() {
		assertEquals(-17.778, Weather.getCelsius(0), 0.001);
		assertEquals(37.778, Weather.getCelsius(100), 0.001);
	}

	@Test
	public void testWindSpeedConversion() {
		assertEquals(160.93, Weather.getKmh(100), 0.1);
		assertEquals(16.42, Weather.getKmh(10.2), 0.1);
	}

}
