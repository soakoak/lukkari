package fi.sokira.metropolialukkari;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.sokira.metropolialukkari.models.MetropoliaQuery;
import fi.sokira.metropolialukkari.models.Realization;
import fi.sokira.metropolialukkari.models.RealizationQuery;
import fi.sokira.metropolialukkari.models.RealizationResult;
import fi.sokira.metropolialukkari.models.Reservation;
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
	
	protected int getQueryType() {
		if(realizationView.getVisibility() == View.VISIBLE) {
			return LukkariWebLoadTask.QUERY_REALIZATION;
		} else if( reservationView.getVisibility() == View.VISIBLE) {
			return LukkariWebLoadTask.QUERY_RESERVATION;
		}
		
		return -1; 
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
			ConnectivityManager connMgr = 
				(ConnectivityManager) getActivity().getSystemService( 
						Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
			
			if( netInfo != null && netInfo.isConnected()) {
				int queryType = getQueryType();
				MetropoliaQuery query = null; 
				
				String input = groupInput.getText().toString();
				List<String> studentGroups = null;
				if( !input.isEmpty()) {
					studentGroups = Arrays.asList(input);
				}
				
				input = startDateInput.getText().toString();
				Date startDate = parseDateFromString( input);
						
				switch( queryType) {
				case LukkariWebLoadTask.QUERY_REALIZATION :
					query = new RealizationQuery()
						.setStudentGroups( studentGroups)
						.setStartDate( startDate);
					break;
				case LukkariWebLoadTask.QUERY_RESERVATION :
					query = new ReservationQuery()
						.setStudentGroup( studentGroups)
						.setStartDate( startDate)
						.setSubject( subjectInput.getText().toString());
					break;
				}
				
				Log.d(TAG, groupInput.getText().toString());
			
				LukkariWebLoadTask task = 
						new LukkariWebLoadTask( queryType);
				task.execute( query);
				
				listener.onSearchInitiated();
			} else {
				Toast.makeText(getActivity(), "Virhe muodostaessa yhteyttä.", 
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
		
		public void onSearchFinished( Result result, int resultType);
	}
	
	private class LukkariWebLoadTask extends AsyncTask<MetropoliaQuery, Void, Result> {
		
		public static final int QUERY_RESERVATION = 1;
		public static final int QUERY_REALIZATION = 2;

		private static final String TAG = "LukkariWebLoadTask";
		
		private static final String reservationServiceUrl = 
				"https://opendata.metropolia.fi/r1/reservation";
		private static final String realizationServiceUrl = 
				"https://opendata.metropolia.fi/r1/realization";
		
		private int queryType;
		
		public LukkariWebLoadTask(int queryType) {
			this.queryType = queryType;
		}
		
		@Override
		protected Result doInBackground(MetropoliaQuery... params) {

			String apikey = Secrets.METROPOLIA_API_KEY;
			String contentType = "application/json";
			String charset = HTTP.UTF_8;
			
			DefaultHttpClient client = new DefaultHttpClient();
			
			HttpParams clientParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout( clientParams, 30000);
			HttpConnectionParams.setSoTimeout(clientParams, 30000);	
			
			HttpPost postMethod = null;
			Result result = null;
			
			Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm")
				.disableHtmlEscaping()
				.create();
			
			StringEntity entity = null;
			
			switch( queryType) {
			case QUERY_REALIZATION:
				postMethod = new HttpPost( realizationServiceUrl + "/search?apiKey=" + apikey);
				
				try {
					entity = new StringEntity( 
							gson.toJson( (RealizationQuery) params[0]));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
				
			case QUERY_RESERVATION:
				postMethod = new HttpPost( reservationServiceUrl + "/search?apiKey=" + apikey);

				try {
					entity = new StringEntity( 
							gson.toJson( (ReservationQuery) params[0]));
					Log.d(TAG, "Lähetettävä Json: " + gson.toJson( (ReservationQuery) params[0]));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
			
			if( entity != null) {
				entity.setContentType(contentType);
				entity.setContentEncoding( charset);
				postMethod.setEntity( entity);
				
				HttpResponse response = null;
				String resultStr = null;
				
				try {
					response = client.execute( postMethod);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				
				if( response != null) {
					try {
						resultStr = EntityUtils.toString( response.getEntity(), charset);
		
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}

				Log.d(TAG, "Result string length: " + resultStr.length());
			
				switch( queryType) {
				case QUERY_REALIZATION :
					result = gson.fromJson(
							resultStr,
							RealizationResult.class);
					
					RealizationResult realzResult = (RealizationResult) result;
					Log.d(TAG, "Number of results: " + realzResult.getRealizations().size());
					for( Realization realz : realzResult.getRealizations())
					{
						Log.d(TAG, "Realization name: " + realz.getName());
					}
					break;
					
				case QUERY_RESERVATION :
//					Log.d(TAG, resultStr);
					result = gson.fromJson(
							resultStr,
							ReservationResult.class);
					ReservationResult reservResult = (ReservationResult) result;
					Log.d(TAG, "Number of results: " + reservResult.getReservations().size());
					for( Reservation reserv : reservResult.getReservations())
					{
						Log.d(TAG, "Reservation subject: " + reserv.getSubject());
					}
					break;
				default:
					break;
				}
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(Result result) {

			if( result != null) {
				switch( queryType) {
				case QUERY_REALIZATION :
					listener.onSearchFinished( result, OnSearchListener.RESULT_REALIZATION);
					break;
				case QUERY_RESERVATION :
					listener.onSearchFinished( result, OnSearchListener.RESULT_RESERVATION);
					break;
				default:
					Log.d(TAG, "Unknown result type");
					break;
				}
			} else {
				Log.d(TAG, "No result");
			}
		}
	}
}
