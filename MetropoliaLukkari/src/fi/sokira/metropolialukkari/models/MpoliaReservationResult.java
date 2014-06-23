package fi.sokira.metropolialukkari.models;

import java.util.ArrayList;

public class MpoliaReservationResult extends MpoliaResult<MpoliaReservation> {

	private ArrayList<MpoliaReservation> reservations;
	
	public ArrayList<MpoliaReservation> getReservations() {
		return reservations;
	}
	
	@Override
	public ArrayList<MpoliaReservation> getResults() {
		return reservations;
	}

	@Override
	public int getResultCount() {
		return reservations.size();
	}
}
