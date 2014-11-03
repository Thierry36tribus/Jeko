package models;

public class PointsScale {

	public static int getNbPoints(final Object origin) {
		if (origin instanceof String) {
			return getNbPointsFromStringOrigin((String) origin);
		} else if (origin instanceof DecorElement) {
			return ((DecorElement) origin).price * -1;
		} else if (origin instanceof Number) {
			return ((Number) origin).intValue();
		}
		return 0;
	}

	private static int getNbPointsFromStringOrigin(final String origin) {
		return 0;
	}

}
