package fi.sokira.metropolialukkari;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class LukkariActivity extends Activity
		implements
			HakuFragment.OnSearchListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lukkari);
		
		if( savedInstanceState == null) {
			getFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, 
					new HakuFragment())
			.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.lukkari, menu);
		return true;
	}

	@Override
	public void onSearchInitiated() {
		getFragmentManager()
			.beginTransaction()
			.replace(android.R.id.content, new ToteutusListFragment())
			.addToBackStack( null)
			.commit();
	}
}
