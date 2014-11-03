package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import controllers.Security;

public class UiPointsEvent {

	public Date eventDate;
	public String origin;
	public int nb;

	public UiPointsEvent(final Date eventDate, final String origin, final int nb) {
		super();
		this.eventDate = eventDate;
		this.origin = origin;
		this.nb = nb;
	}

	@Override
	public String toString() {
		return "UiPointsEvent [eventDate=" + eventDate + ", origin=" + origin + ", nb=" + nb + "]";
	}

	public static UiPointsEvent create(final PointsEvent event) {
		final UiPointsEvent uiEvent = new UiPointsEvent(event.eventDate, event.origin, event.nb);
		return uiEvent;
	}

	public static List<UiPointsEvent> createList(final User user, final int nb) {
		final List<PointsEvent> events = PointsEvent.findByUser(user, nb);
		final List<UiPointsEvent> uiEvents = new ArrayList();
		for (final PointsEvent event : events) {
			uiEvents.add(UiPointsEvent.create(event));
		}
		return uiEvents;
	}

	public static List<UiPointsEvent> createList() {
		return createList(Security.getConnectedUser(), 10);
	}

}
