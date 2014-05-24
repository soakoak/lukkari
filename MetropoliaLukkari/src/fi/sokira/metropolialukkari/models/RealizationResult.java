package fi.sokira.metropolialukkari.models;

import java.util.ArrayList;

public class RealizationResult extends Result<Realization> {

	private  ArrayList<Realization> realizations;

	public ArrayList<Realization> getRealizations() {
		return realizations;
	}

	@Override
	public ArrayList<Realization> getResults() {
		return getRealizations();
	}

	@Override
	public int getResultCount() {
		return getRealizations().size();
	}
}
