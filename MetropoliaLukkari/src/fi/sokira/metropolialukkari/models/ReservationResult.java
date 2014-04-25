package fi.sokira.metropolialukkari.models;

import java.util.ArrayList;

public class ReservationResult implements Result {

	private ArrayList<Reservation> reservations;
	
	public ArrayList<Reservation> getReservations() {
		return reservations;
	}
}
