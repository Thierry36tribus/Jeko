package games;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.Dwelling;
import models.EcoTip;
import models.Saving;
import models.UiDwelling;
import controllers.Security;

public class GameType {

	public long id;
	public String title;
	public String description;
	public Object data;

	public GameType(final long id, final String title, final String description) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
	}

	@Override
	public String toString() {
		return "GameType [id=" + id + ", title=" + title + "]";
	}

	public static List<GameType> all() {
		final List<GameType> gameTypes = new ArrayList();

		final GameType game1 = new GameType(1, "Jeko-Quizz", "");
		game1.data = EcoTip.all().fetch();
		gameTypes.add(game1);

		final GameType game2 = new GameType(2, "Ma consommation en question", "");
		Dwelling dwelling = Dwelling.findByUser(Security.getConnectedUser());
		if (dwelling == null) {
			// admin ou visiteur
			dwelling = Dwelling.all().first();
		}
		final Saving saving = Saving.findLast(dwelling);
		final List<Saving> savingsOfWeek = Saving.findLastWeek();
		game2.data = new UiSavingForGame(saving, MeanConsumption.createFrom(savingsOfWeek), dwelling.surface);
		gameTypes.add(game2);

		final GameType game4 = new GameType(4, "C'est ma courbe !", "");
		final List<UiDwelling> threeDwellings = new ArrayList();
		// le mien en premier + 2 autres
		final Dwelling myDwelling = Dwelling.findMine();
		threeDwellings.add(UiDwelling.create(myDwelling, true));
		final List<Dwelling> dwellings = Dwelling.all().fetch();
		dwellings.remove(myDwelling);
		final Random random = new Random();
		final Dwelling d1 = dwellings.get(random.nextInt(dwellings.size() - 1));
		threeDwellings.add(UiDwelling.create(d1, true));
		dwellings.remove(d1);
		final Dwelling d2 = dwellings.get(random.nextInt(dwellings.size() - 1));
		threeDwellings.add(UiDwelling.create(d2, true));
		game4.data = threeDwellings;
		gameTypes.add(game4);

		return gameTypes;
	}

}
