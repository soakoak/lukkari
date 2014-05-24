package fi.sokira.metropolialukkari;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

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

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fi.sokira.metropolialukkari.models.MetropoliaQuery;
import fi.sokira.metropolialukkari.models.Result;

public abstract class WebQueryTask<T extends MetropoliaQuery, K extends Result<?>> extends AsyncTask<T, Void, K> {

	private static final String TAG = "WebLoadTask";
	
	private String mApiKey = Secrets.METROPOLIA_API_KEY;
	private String mCharset = HTTP.UTF_8;
	private int mConnectionTimeout = 30000;
	private String mContentType = "application/json";
	private int mSoTimeout = 30000;
	
	private Class<K> mResultType;
	private Gson mGson;
	
	public WebQueryTask(Class<K> type) {
		mResultType = type;
		mGson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd'T'HH:mm")
			.disableHtmlEscaping()
			.create();
	}
	
	protected DefaultHttpClient createHttpClient() {
		DefaultHttpClient client = new DefaultHttpClient();
		
		HttpParams clientParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout( clientParams, mConnectionTimeout);
		HttpConnectionParams.setSoTimeout(clientParams, mSoTimeout);	
		
		return client;
	}
	
	protected StringEntity createStringEntity(String jsonQuery) {
		StringEntity entity = null;
		
		try {
			entity = new StringEntity( jsonQuery);
			entity.setContentType(mContentType);
			entity.setContentEncoding(mCharset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return entity;
	}
	
	@Override
	protected K doInBackground(T... params) {
		String jsonQuery = transformQueryToJson( params[0]);
		Log.d(TAG, "Lähetettävä JSON: " + jsonQuery);
		
		StringEntity entity = createStringEntity(jsonQuery);
		
		if( entity != null) {
			HttpPost postMethod = new HttpPost( getServiceUrl() + "/search?apiKey=" + mApiKey);
			postMethod.setEntity( entity);
			
			DefaultHttpClient client = createHttpClient();
			String resultStr = null;
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
					resultStr = EntityUtils.toString( response.getEntity(), mCharset);
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
			}

			Log.d(TAG, "Result string length: " + resultStr.length());
			if( resultStr.length() < 30) {
				Log.d(TAG, resultStr);
			}

			K result = transformJsonToResultObject(resultStr);
			
			Log.d(TAG, "Number of results: " + result.getResultCount());
			return result;
		}
		
		return null;
	}
	
	protected abstract String getServiceUrl();
	
	@Override
	protected abstract void onPostExecute(K result);
	
	protected K transformJsonToResultObject(String jsonResult) {
		return mGson.fromJson(jsonResult, mResultType);
	}
	
	protected String transformQueryToJson(T queryObject) {
		return mGson.toJson( queryObject);
	}
}
