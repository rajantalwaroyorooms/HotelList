package com.example.hotelslisting;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hotelslistingadapter.HotelListAdapter;
import com.example.model.Hotel;

@SuppressLint("NewApi")
public class HotelMainActivity extends Activity {
	private static final String API_KEY = "AIzaSyAIUn8Mj7hxSFn197OHKhOjs_PKl7nsXZ4";
	private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
	private static final String QUERY_TEXT = "hotelsinGurgaon";
	private static final String PAGE_TOKEN = "pagetoken=";
	private TextView tvShowJson;
	private EditText etQueryText;
	private Button btnFetchHotelList;
	private List<Hotel> hotelList = new ArrayList<Hotel>();
	private HotelListAdapter hotelAdapter;
	private ListView hotelListView;
	private String pageToken;
	private boolean loadMore = false;
    private boolean isLoadingData = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotel_activity_main);
		etQueryText = (EditText) findViewById(R.id.et_enter_query);
		btnFetchHotelList = (Button) findViewById(R.id.btn_fetch_hotel_list);
		hotelListView = (ListView) findViewById(R.id.lv_hotel_list);
		hotelAdapter = new HotelListAdapter(this, new ArrayList<Hotel>());
		hotelListView.setAdapter(hotelAdapter);
		implementPaginationCalls();
	}
	
	// google places return data of size 20 in one call
	private void implementPaginationCalls() {
		hotelListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			
				loadMore = (firstVisibleItem + visibleItemCount) >= totalItemCount;
				if(loadMore && !isLoadingData && totalItemCount != 0) {
					Log.d("navya21","navya21 making the api call");
					isLoadingData = true;
					loadMore = false;
					Log.d("navya21","navya21 pagetokenUrl::::" + makepageTokenUrl());
					new HttpAsyncTask().execute(makepageTokenUrl());
				}
				
			}
		});
	}
	
	public void fetchTheList(View view) {
		btnFetchHotelList.setVisibility(View.GONE);
		etQueryText.setVisibility(View.GONE);
		new HttpAsyncTask().execute(makeUrl());
	}
	
	/*https://maps.googleapis.com/maps/api/place/textsearch/json?pagetoken=*/	
	private String makepageTokenUrl() {
		StringBuilder pageTokenUrl = new StringBuilder(PLACES_SEARCH_URL);
		pageTokenUrl.append(PAGE_TOKEN);
		pageTokenUrl.append(pageToken);
		pageTokenUrl.append("&key=");
		pageTokenUrl.append(API_KEY);
		return pageTokenUrl.toString();
	}
	
	/*https://maps.googleapis.com/maps/api/place/textsearch/json?query=restaurants+in+Gurgaon&key=AIzaSyAIUn8Mj7hxSFn197OHKhOjs_PKl7nsXZ4
*/	private String makeUrl(){
		StringBuilder urlString = new StringBuilder(PLACES_SEARCH_URL);
		urlString.append("query=");
		urlString.append(etQueryText.getText().toString().replaceAll("\\s+","%20"));
		urlString.append("&key=");
		urlString.append(API_KEY);
		return urlString.toString();
	}


private String makeCall(String theurl)  {
		try {
			StringBuilder sb = new StringBuilder();
			URL url = new URL(theurl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()));
			String line;
			while ((line = bufferReader.readLine()) != null) {
				sb.append(line + "\n");
			}
			bufferReader.close();
			return sb.toString();
		} catch (Exception e) {
			Log.e("navya21","navya21 exceptione::::::::"+ e);
           return null;
		}
}


 private Hotel parseJson(JSONObject jsonObject) throws JSONException  {
	 Hotel hotel = new Hotel();
	 String hotelName = (String) jsonObject.get("name");
	// Log.e("navya2014","navya2014 hotelName:::::::" + hotelName);
	 String hotelAddress = (String) jsonObject.get("formatted_address");
	 JSONObject jsonLocation = jsonObject.getJSONObject("geometry"). getJSONObject("location");
	 
	 new FourSquareAsyncTask(jsonLocation.getString("lat"), 
			                 jsonLocation.getString("lng"), 
			                 hotelName).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	 
	 /*hotel.setLattitude(jsonLocation.getString("lat"));
	 hotel.setLongitude(jsonLocation.getString("lng"));  
	 hotel.setHotelAddress(hotelAddress);
	 hotel.setHotelName(hotelName);*/
	 return hotel;
 }

class HttpAsyncTask extends AsyncTask<String, Void, String> {
	
	private ProgressDialog dialog;
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		dialog = ProgressDialog.show(HotelMainActivity.this, "Please wait", "Loading....", false, true);
		dialog.show();
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... arg0) {
			return makeCall(arg0[0]);
		}
	
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		dialog.dismiss();
		isLoadingData = false;
		super.onPostExecute(result);
		try {
			JSONObject jsonObject = new JSONObject(result);
			pageToken = (String) jsonObject.get("next_page_token");
			Log.d("navya21","navya21 next_page_token::::" + jsonObject.get("next_page_token"));
			 JSONArray jsonArray = jsonObject.getJSONArray("results");
			 retrieveHotelList(jsonArray);
			 /*hotelAdapter.setHotelList(retrieveHotelList(jsonArray));
			 hotelAdapter.notifyDataSetChanged();*/
		} catch (JSONException e) {	
			// TODO Auto-generated catch block
			Log.e("JsonException","json  e :::::" + e);
			e.printStackTrace();
		}
		
	}
	
	
	private void retrieveHotelList(JSONArray jsonArray) throws JSONException{
		for(int i = 0; i < jsonArray.length(); i++) {
			parseJson((JSONObject)jsonArray.get(i));
			 //hotelList.add(parseJson((JSONObject)jsonArray.get(i)));
		}
		//return (ArrayList<Hotel>) hotelList;
	}
		
}

public class FourSquareAsyncTask extends AsyncTask<Hotel, Void, String> {
    private String FOUR_SQUARE_BASE_URL = "https://api.foursquare.com/v2/venues/search?ll=";
    private String CLIENT_ID="BEUDWXV0VQBWQBEFBJGAN53QM5C5INFMR1C5F2JWC3VJU4WX";
    private String CLIENT_SECRET = "10VUMJAFUILBAKD3IQRTNEM1QDHJCCNYX5GSLG2H2P5OW0MS";
    private String TARGET_COMPANY = "foursquare";
    private int checkInCounts;
    
    private String lat, longitude, name;
    
    public FourSquareAsyncTask(String lat, String longitude, String name) {
 	   this.lat = lat;
 	   this.longitude = longitude;
 	   this.name = name;
    }
    @Override
 	protected void onPreExecute() {
 		super.onPreExecute();
 	}
    
		@Override
		protected String doInBackground(Hotel... params) {
			StringBuilder sb = new StringBuilder();
			try {
			URL url = new URL(makeFourSquareUrl());
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()));
			String line;
			while ((line = bufferReader.readLine()) != null) {
				sb.append(line + "\n");
			}
			bufferReader.close();
			return sb.toString();
		} catch (Exception e) {
        return null;
		}
		}

		
		@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				try {
				JSONObject json = new JSONObject(result);
				JSONObject reponseJson= json.getJSONObject("response");
				hotelList.add(foursquareJsonParse(reponseJson.getJSONArray("venues")));
				Collections.sort(hotelList);
				hotelAdapter.setHotelList((ArrayList<Hotel>)hotelList);
				hotelAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					Log.e("navya15","navya15 exception e ::::::::" + e);
					e.printStackTrace();
				}
				// parse the json  and set the result in a text view
			}
		
		
		/*https://api.foursquare.com/v2/venues/search?ll=28.4824165,77.0891655&
	     client_id=BEUDWXV0VQBWQBEFBJGAN53QM5C5INFMR1C5F2JWC3VJU4WX
	     &client_secret=10VUMJAFUILBAKD3IQRTNEM1QDHJCCNYX5GSLG2H2P5OW0MS&v=20140806
	     &m=foursquare&query=The%20Leela%20Ambience%20Gurgaon%20Hotel%20&%20Residences&limit=1
		*/
		private String makeFourSquareUrl(){
			StringBuilder sb = new StringBuilder();
			sb.append(FOUR_SQUARE_BASE_URL);
			sb.append(lat+","+ longitude);
			sb.append("&client_id=");
			sb.append(CLIENT_ID);
			sb.append("&client_secret=");
			sb.append(CLIENT_SECRET);
			sb.append("&v=20140806");
			sb.append("&m=foursquare&query=");
			sb.append(name.replaceAll("\\s+","%20"));
			sb.append("&limit=1");
			Log.e("navya2014","navya2014 foursquare url ::::::"+ sb.toString());
			//return "https://api.foursquare.com/v2/venues/search?ll=28.4824165,77.0891655&client_id=BEUDWXV0VQBWQBEFBJGAN53QM5C5INFMR1C5F2JWC3VJU4WX&client_secret=10VUMJAFUILBAKD3IQRTNEM1QDHJCCNYX5GSLG2H2P5OW0MS&v=20140806&m=foursquare&query=The%20Leela%20Ambience%20Gurgaon%20Hotel%20&%20Residences&limit=1";
			return sb.toString();
		}
		
		
		
		private Hotel foursquareJsonParse(JSONArray jsonArray) throws JSONException { 
			Hotel hotel = new Hotel();
			JSONObject fjsonObject = jsonArray.getJSONObject(0);
			JSONObject fjosnArray = fjsonObject.getJSONObject("stats");
			Log.d("navya51","navya51 json Array ::::::::"+ fjsonObject.getJSONObject("location").getJSONArray("formattedAddress").length());
			hotel.setHotelAddress(fjsonObject.getJSONObject("location").getJSONArray("formattedAddress").join(", ").replaceAll("\"", ""));
			hotel.setCheckinCounts(fjosnArray.getString("checkinsCount"));
			hotel.setHotelName(fjsonObject.getString("name"));
			return hotel;
			
		}
	}

}
