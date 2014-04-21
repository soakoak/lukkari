package fi.sokira.metropolialukkari;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.sokira.metropolialukkari.models.MetropoliaQuery;
import fi.sokira.metropolialukkari.models.Realization;
import fi.sokira.metropolialukkari.models.RealizationQuery;
import fi.sokira.metropolialukkari.models.RealizationResult;
import fi.sokira.metropolialukkari.models.Result;

public class HakuFragment extends Fragment 
						implements OnClickListener,
							DatePickerDialog.OnDateSetListener {
	
	private EditText groupInput = null;
	private EditText startDateInput = null;
	
	private OnSearchListener listener = null;
	
	private final static String TAG = "HakuFragment";
	
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
				RealizationQuery query = new RealizationQuery();
				query.setStudentGroups( Arrays.asList(
						groupInput.getText().toString()));
				Date date = null;
				try {
					date = DateFormat.getDateInstance().parse(
							startDateInput.getText().toString());
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
				query.setStartDate( date);
				
				Log.d(TAG, groupInput.getText().toString());
			
				LukkariWebLoadTask task = 
						new LukkariWebLoadTask( LukkariWebLoadTask.QUERY_REALIZATION);
				task.execute( query);
				
				listener.onSearchInitiated();
			} else {
				Toast.makeText(getActivity(), "Virhe muodostaessa yhteyttä.", Toast.LENGTH_LONG).show();
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
			startDateInput.setText("");
			groupInput.setText("");
			break;
		default :
			break;
		}
	}
	
	public interface OnSearchListener {
		
		public int RESULT_REALIZATION = 1;
		public int RESULT_RESERVATION = 2;
		
		public void onSearchInitiated();
		
		public void onSearchFinished( Result result, int resultType);
	}
	
	private class LukkariWebLoadTask extends AsyncTask<MetropoliaQuery, Void, RealizationResult> {
		
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
		protected RealizationResult doInBackground(MetropoliaQuery... params) {

			String apikey = Secrets.METROPOLIA_API_KEY;
			String contentType = "application/json";
			String charset = HTTP.UTF_8;
			
			DefaultHttpClient client = new DefaultHttpClient();
			
			HttpParams clientParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout( clientParams, 30000);
			HttpConnectionParams.setSoTimeout(clientParams, 30000);	
			
			HttpPost postMethod = null;
			
			Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd'T'HH:mm")
				.disableHtmlEscaping()
				.create();
			
			switch( queryType) {
			
			case QUERY_REALIZATION:
				postMethod = new HttpPost( realizationServiceUrl + "/search?apiKey=" + apikey);
				
				RealizationQuery query = (RealizationQuery) params[0];
				
				try {
					StringEntity entity = new StringEntity( gson.toJson( query));
					entity.setContentType(contentType);
					entity.setContentEncoding( charset);
					postMethod.setEntity( entity);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				HttpResponse response = null;
				try {
					response = client.execute( postMethod);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				
				if( response != null) {
					try {
						String resultStr = EntityUtils.toString( response.getEntity(), charset);
		
						Log.d(TAG, "Result string length: " + resultStr.length());
						RealizationResult realzResult = gson.fromJson(
								resultStr,
								RealizationResult.class);
						
						Log.d(TAG, "Number of results: " + realzResult.getRealizations().size());
						for( Realization realz : realzResult.getRealizations())
						{
							Log.d(TAG, "Realization name: " + realz.getName());
						}
						
						return realzResult;
					} catch (ParseException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} 
				}
				break;
				
			case QUERY_RESERVATION:
				postMethod = new HttpPost( reservationServiceUrl + "/search?apiKey=" + apikey);
				//TODO jne.
				break;
			default:
				break;
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(RealizationResult result) {

			if( result != null) {
				listener.onSearchFinished( result, OnSearchListener.RESULT_REALIZATION);
			} else {
				Log.d(TAG, "No result");
			}
		}
		
	}

	@Override
	public void onDateSet(DatePicker view, int year, int monthOfYear,
			int dayOfMonth) {
		Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();
		startDateInput.setText( DateFormat.getDateInstance().format( date));
	}
}
