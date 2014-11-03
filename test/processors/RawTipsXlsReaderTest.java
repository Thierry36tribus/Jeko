package processors;

import static org.junit.Assert.assertEquals;

import java.io.File;

import models.EcoTip;
import models.RawTipsReadResult;

import org.junit.Test;

public class RawTipsXlsReaderTest {

	@Test
	public void test() {
		final RawTipsXlsReader reader = new RawTipsXlsReader();
		final RawTipsReadResult result = reader.read(new File(
				"resources/Encerticus_conseils économies_V12.1_ECOQUIZZ.xlsx"));
		assertEquals("Nombre de données", 100, result.tips.size());

		final EcoTip firstTip = result.tips.get(0);
		assertEquals(
				"premier conseil, question",
				"Une ampoule de type fluocompacte à la place d'une ampoule à incandescence c'est une économie d'énergie de :",
				firstTip.question.trim());
		assertEquals("premier conseil, bonne réponse", "75%", firstTip.goodAnswer);

		final EcoTip lastTip = result.tips.get(result.tips.size() - 1);
		assertEquals("dernier conseil", "Laquelle de ces affirmations est vraie ?", lastTip.question);
		assertEquals("dernier conseil, bonne réponse",
				"Un air intérieur non renouvellé peut être plus pollué que l'air extérieur ", lastTip.goodAnswer);

	}

}
