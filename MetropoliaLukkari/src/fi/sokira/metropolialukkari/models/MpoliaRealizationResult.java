package fi.sokira.metropolialukkari.models;

import java.util.ArrayList;

public class MpoliaRealizationResult extends MpoliaResult<MpoliaRealization> {

	private  ArrayList<MpoliaRealization> realizations;

	public ArrayList<MpoliaRealization> getRealizations() {
		return realizations;
	}

	@Override
	public ArrayList<MpoliaRealization> getResults() {
		return getRealizations();
	}

	@Override
	public int getResultCount() {
		return getRealizations().size();
	}
}
