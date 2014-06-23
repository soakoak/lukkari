package fi.sokira.metropolialukkari.models;

import java.util.ArrayList;

public abstract class MpoliaResult<T> {

	public abstract ArrayList<T> getResults();
	
	public int getResultCount() {
		return getResults().size();
	}
}
