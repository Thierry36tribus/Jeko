package processors;

import static org.junit.Assert.assertEquals;

import java.io.File;

import models.RawSaving;
import models.RawSavingsReadResult;

import org.junit.Test;

public class RawSavingsXlsReaderTest {

	@Test
	public void testS20() {
		final RawSavingsXlsReader reader = new RawSavingsXlsReader(0);
		final RawSavingsReadResult result = reader.read(new File(
				"resources/ENCERTICUS_Programme_Matrice_V4_2014_S20.xlsx"));
		assertEquals("Année", 2014, result.year);
		assertEquals("Semaine", 20, result.week);
		assertEquals("Nombre de données", 45, result.savings.size());

		final RawSaving firstSaving = result.savings.get(0);
		assertEquals("appart 1 nom", "274", firstSaving.dwellingLabel);
		assertEquals("appart 1 éco globale", 0, firstSaving.globalSaving, 0.0001);

		final RawSaving lastSaving = result.savings.get(result.savings.size() - 1);
		assertEquals("appart dernier nom", "310", lastSaving.dwellingLabel);
		assertEquals("appart dernier éco globale", 0, lastSaving.globalSaving, 0.0001);
	}

	@Test
	public void testS21() {
		final RawSavingsXlsReader reader = new RawSavingsXlsReader(0);
		final RawSavingsReadResult result = reader.read(new File(
				"resources/ENCERTICUS_Programme_Matrice_V4_2014_S21.xlsx"));
		assertEquals("Semaine", 21, result.week);
		assertEquals("Nombre de données", 45, result.savings.size());

		final RawSaving firstSaving = result.savings.get(0);
		assertEquals("appart 1 nom", "274", firstSaving.dwellingLabel);
		assertEquals("appart 1 éco globale", 0.1708, firstSaving.globalSaving, 0.0001);

		assertEquals("appart 1 chauffage", 0.66, firstSaving.heatingSaving, 0.01);
		assertEquals("appart 1 chauffage conso", 0.25, firstSaving.heatingConsumption, 0.01);
		assertEquals("appart 1 chauffage ref", 0.73, firstSaving.heatingRef, 0.01);

		assertEquals("appart 1 elec", -0.05, firstSaving.electricitySaving, 0.01);
		assertEquals("appart 1 elec conso", 54.9, firstSaving.electricityConsumption, 0.01);
		assertEquals("appart 1 elec ref", 52.2, firstSaving.electricityRef, 0.01);

		assertEquals("appart 1 eau", -0.10, firstSaving.waterSaving, 0.01);
		assertEquals("appart 1 eau conso", 2200, firstSaving.waterConsumption, 0.01);
		assertEquals("appart 1 eau ref", 2000, firstSaving.waterRef, 0.01);

		assertEquals("appart 1 ecs", -0.125, firstSaving.hotWaterSaving, 0.01);
		assertEquals("appart 1 ecs conso", 0.49, firstSaving.hotWaterConsumption, 0.01);
		assertEquals("appart 1 ecs ref", 0.43, firstSaving.hotWaterRef, 0.01);

		final RawSaving lastSaving = result.savings.get(result.savings.size() - 1);
		assertEquals("appart dernier nom", "310", lastSaving.dwellingLabel);
		assertEquals("appart dernier éco globale", 0.3321, lastSaving.globalSaving, 0.0001);

	}

	@Test
	public void testS23() {
		final RawSavingsXlsReader reader = new RawSavingsXlsReader(0);
		final RawSavingsReadResult result = reader.read(new File(
				"resources/ENCERTICUS_Programme_Matrice_V4_2014_S23.xlsx"));
		assertEquals("Semaine", 23, result.week);
		assertEquals("Nombre de données", 45, result.savings.size());

		final RawSaving firstSaving = result.savings.get(0);
		assertEquals("appart 1 nom", "274", firstSaving.dwellingLabel);
		assertEquals("appart 1 éco globale", 0, firstSaving.globalSaving, 0.0001);

		final RawSaving aSaving = result.savings.get(26);
		assertEquals("appart 27 nom", "271", aSaving.dwellingLabel);
		assertEquals("appart 27 éco globale", -0.2497, aSaving.globalSaving, 0.0001);

		final RawSaving lastSaving = result.savings.get(result.savings.size() - 1);
		assertEquals("appart dernier nom", "310", lastSaving.dwellingLabel);
		assertEquals("appart dernier éco globale", 0, lastSaving.globalSaving, 0.0001);

	}

	@Test
	public void testS20AsFirstWeek() {
		final RawSavingsXlsReader reader = new RawSavingsXlsReader(20);
		final RawSavingsReadResult result = reader.read(new File(
				"resources/ENCERTICUS_Programme_Matrice_V4_2014_S20.xlsx"));
		assertEquals("Année", 2014, result.year);
		assertEquals("Semaine", 20, result.week);
		assertEquals("Nombre de données", 45, result.savings.size());

		final RawSaving firstSaving = result.savings.get(0);
		assertEquals("appart 1 nom", "274", firstSaving.dwellingLabel);
		assertEquals("appart 1 éco globale", 0, firstSaving.globalSaving, 0.0001);

		assertEquals("appart 1 chauffage", 4, firstSaving.heatingSaving, 0.01);
		assertEquals("appart 1 elec", -0.03, firstSaving.electricitySaving, 0.01);
		assertEquals("appart 1 eau", 0.02, firstSaving.waterSaving, 0.01);

		final RawSaving lastSaving = result.savings.get(result.savings.size() - 1);
		assertEquals("appart dernier nom", "310", lastSaving.dwellingLabel);
		assertEquals("appart dernier éco globale", 0, lastSaving.globalSaving, 0.0001);
		assertEquals("appart 1 chauffage", 5, lastSaving.heatingSaving, 0.01);
		assertEquals("appart 1 elec", 0.48, lastSaving.electricitySaving, 0.01);
		assertEquals("appart 1 eau", -0.5, lastSaving.waterSaving, 0.01);
	}

}
