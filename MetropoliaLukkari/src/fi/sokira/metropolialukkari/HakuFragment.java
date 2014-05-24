package fi.sokira.metropolialukkari;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import fi.sokira.metropolialukkari.models.RealizationQuery;
import fi.sokira.metropolialukkari.models.RealizationResult;
import fi.sokira.metropolialukkari.models.ReservationQuery;
import fi.sokira.metropolialukkari.models.ReservationResult;
import fi.sokira.metropolialukkari.models.Result;

public class HakuFragment extends Fragment 
						implements OnClickListener,
							DatePickerDialog.OnDateSetListener,
							AdapterView.OnItemSelectedListener {
	
	private String[] spinnerChoices = null;
	
	private View realizationView = null;
	
	private View reservationView = null;
	private EditText subjectInput = null;
	
	private EditText groupInput = null;
	private EditText startDateInput = null;
	
	private OnSearchListener listener = null;
	
	private final static String TAG = "HakuFragment";
	
	public static final int QUERY_RESERVATION = 1;
	public static final int QUERY_REALIZATION = 2;
	
	protected int getQueryType() {
		if(realizationView.getVisibility() == View.VISIBLE) {
			return QUERY_REALIZATION;
		} else if( reservationView.getVisibility() == View.VISIBLE) {
			return QUERY_RESERVATION;
		}
		
		return -1; 
	}
	
	protected boolean isNetworkAvailable() {
		ConnectivityManager connMgr = 
				(ConnectivityManager) getActivity().getSystemService( 
						Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		
		if( netInfo == null) {
			return false;
		} else {
			return netInfo.isConnected();
		}
	}
	
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
		
		spinnerChoices = getResources().getStringArray( R.array.array_fetch_modes);
		Spinner spinner = (Spinner) v.findViewById( R.id.spinner_mode);
		spinner.setOnItemSelectedListener( this);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(getActivity(), 
				android.R.layout.simple_spinner_item, spinnerChoices);
		adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		realizationView = v.findViewById( R.id.realization_layout);
		
		reservationView = v.findViewById( R.id.reservation_layout);
		subjectInput = (EditText) v.findViewById( R.id.input_subject);
		
		startDateInput = (EditText) v.findViewById( R.id.input_start_date);
		startDateInput.setOnClickListener( this);
		
		groupInput = (EditText) v.findViewById( R.id.input_group);
		Button searchBtn = (Button) v.findViewById( R.id.search_button);
		searchBtn.setOnClickListener( this);
		
		Button clearBtn = (Button) v.findViewById( R.id.button_clear);
		clearBtn.setOnClickListener( this);
		
		return v;
	}
	
	@Override
	public void onClick(View v) {
		switch( v.getId()) {
		case R.id.search_button :
			if( isNetworkAvailable() == true) {
				int queryType = getQueryType();
				
				String input = groupInput.getText().toString();
				List<String> studentGroups = null;
				if( !input.isEmpty()) {
					studentGroups = Arrays.asList(input);
				}
				
				input = startDateInput.getText().toString();
				Date startDate = parseDateFromString( input);
				
				if( queryType == QUERY_REALIZATION) {
					RealizationQuery query = new RealizationQuery()
						.setStudentGroups( studentGroups)
						.setStartDate( startDate);
					new RealizationWebTask().execute(query);
				} else if (queryType == QUERY_RESERVATION) {
					ReservationQuery query = new ReservationQuery()
						.setStudentGroup( studentGroups)
						.setStartDate( startDate)
						.setSubject( subjectInput.getText().toString());
					new ReservationWebTask().execute(query);
				}
				
				listener.onSearchInitiated();
			} else {
				Toast.makeText(getActivity(), "Virhe muodostaessa yhteytt�", 
						Toast.LENGTH_LONG).show();
			}
			
			break;
		case R.id.input_start_date:
			Calendar cal = Calendar.getInstance();
			
			if( !startDateInput.getText().toString().isEmpty()) {			 
				Date date = null;
				try {
					date = DateFormat.getDateInstance().parse( 
							startDateInput.getText().toString());
					cal.setTime(date);
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
			
			new DatePickerDialog(
					getActivity(), 
					this, 
					cal.get( Calendar.YEAR), 
					cal.get( Calendar.MONTH), 
					cal.get( Calendar.DAY_OF_MONTH)).show();
			
			break;
		case R.id.button_clear:
			subjectInput.setText("");
			startDateInput.setText("");
			groupInput.setText("");
			break;
		default :
			break;
		}
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
		startDateInput.setText( DateFormat.getDateInstance().format( date));
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {		
		Object item = parent.getItemAtPosition( position);
		if( item.toString().equals( spinnerChoices[0])) {
			realizationView.setVisibility( View.VISIBLE);
			reservationView.setVisibility( View.GONE);
		} else if( item.toString().equals( spinnerChoices[1])) {
			realizationView.setVisibility( View.GONE);
			reservationView.setVisibility( View.VISIBLE);
		}
		Log.d(TAG, "Mode spinner: " + item.toString() + " selected.");
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Log.d(TAG, "Mode spinner: no selection made.");
	}
	
	/*
	 * Returns null if @param str is empty.
	 */
	public static Date parseDateFromString(String str) {
		if( !str.isEmpty()) {
			try {
				return DateFormat.getDateInstance().parse(str);
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public interface OnSearchListener {
		
		public int RESULT_REALIZATION = 1;
		public int RESULT_RESERVATION = 2;
		
		public void onSearchInitiated();
		
		public void onSearchFinished( Result<?> result, int resultType);
	}
	
	private class ReservationWebTask 
			extends WebQueryTask<ReservationQuery, ReservationResult> {

		private static final String reservationServiceUrl = 
				"https://opendata.metropolia.fi/r1/reservation";
		
		public ReservationWebTask() {
			super(ReservationResult.class);
		}

		@Override
		protected String getServiceUrl() {
			return reservationServiceUrl;
		}

		@Override
		protected void onPostExecute(ReservationResult result) {
			listener.onSearchFinished( result, OnSearchListener.RESULT_RESERVATION);
		}
	};
	
	private class RealizationWebTask 
			extends WebQueryTask<RealizationQuery, RealizationResult> {

		private static final String realizationServiceUrl = 
				"https://opendata.metropolia.fi/r1/realization";
		
		public RealizationWebTask() {
			super(RealizationResult.class);
		}

		@Override
		protected String getServiceUrl() {
			return realizationServiceUrl;
		}

		@Override
		protected void onPostExecute(RealizationResult result) {
			listener.onSearchFinished( result, OnSearchListener.RESULT_REALIZATION);
		}
		
	}
}
