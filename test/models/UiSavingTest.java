package models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UiSavingTest {

	@Test
	public void test_52_2014() {
		assertEquals(3, UiSaving.getWeekIndex(50, 2014, 52));
	}

	@Test
	public void test_1_2015() {
		assertEquals(4, UiSaving.getWeekIndex(50, 2015, 1));
	}

	@Test
	public void test_49_2015() {
		assertEquals(52, UiSaving.getWeekIndex(50, 2015, 49));
	}

	@Test
	public void test_52_2015() {
		assertEquals(3, UiSaving.getWeekIndex(50, 2015, 52));
	}

	@Test
	public void test_53_2015() {
		assertEquals(4, UiSaving.getWeekIndex(50, 2015, 53));
	}

	@Test
	public void test_1_2016() {
		assertEquals(5, UiSaving.getWeekIndex(50, 2016, 1));
	}

}
