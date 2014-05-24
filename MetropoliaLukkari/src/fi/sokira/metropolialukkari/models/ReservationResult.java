package fi.sokira.metropolialukkari.models;

import java.util.ArrayList;

public class ReservationResult extends Result<Reservation> {

	private ArrayList<Reservation> reservations;
	
	public ArrayList<Reservation> getReservations() {
		return reservations;
	}
	
	@Override
	public ArrayList<Reservation> getResults() {
		return reservations;
	}

	@Override
	public int getResultCount() {
		return reservations.size();
	}
}
