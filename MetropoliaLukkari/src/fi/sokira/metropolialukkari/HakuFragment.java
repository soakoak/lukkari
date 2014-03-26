package fi.sokira.metropolialukkari;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class HakuFragment extends Fragment implements OnClickListener {
	
	private EditText groupInput = null;
	
	private OnSearchListener listener = null;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		try {
			listener = (OnSearchListener) activity;
		} catch ( ClassCastException e) {
			throw new ClassCastException(
				activity.toString() + 
				" must implement " + listener.getClass().getSimpleName());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_haku, container, false);
		
		groupInput = (EditText) v.findViewById( R.id.input_group);
		
		Button searchBtn = (Button) v.findViewById( R.id.search_button);
		searchBtn.setOnClickListener( this);
		
		return v;
	}
	
	@Override
	public void onClick(View v) {
		switch( v.getId()) {
		case R.id.search_button :
//			Toast.makeText( 
//				getActivity(), 
//				"Fetch " + groupInput.getText().toString() + "!", 
//				Toast.LENGTH_LONG).show();
			listener.onSearchInitiated();
			groupInput.setText( "");
			break;
		default :
			break;
		}
	}
	
	public interface OnSearchListener {
		
		public void onSearchInitiated();
	}
}
