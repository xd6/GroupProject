
/*Xavier Gagnon 2013*/

package com.example.groupproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SchoolView extends Activity {

  static TextView text;
  static GoogleMap map;
  static boolean click = false;
  static String zipCode = "";

  //this is my Google API key (for Places API) 
  //other api key (each one has a 1k/day access limit) 
  //    AIzaSyD-lHpxNV6YKsLkvV9Wxudlwv9nExs6TIQ
  
  private final String key = "AIzaSyCisJv4V2lpdUFlBFewuYPb4hn4gmJNNQ4";

  //radius within which to search
  protected static int radius = 12500;

  protected static String 
  placesSearchString="", 
  searchType ="", 
  JSONstring="";

  
  @Override
  protected void onCreate (Bundle bundle)
  {
    super.onCreate(bundle);

    setContentView(R.layout.schoolview);

    text = (TextView)findViewById(R.id.mapText);

    map = ((MapFragment)
        getFragmentManager().findFragmentById(R.id.map))
        .getMap();

 }
  
  @Override
  protected void onStart ()
  {
    super.onStart();
    
    LatLng latlng = new LatLng(MainActivity.latitude, MainActivity.longitude);
    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12));
    
    zipCode = getIntent().getExtras().getString("zipCode");
    //Toast.makeText(this,  zipCode, Toast.LENGTH_SHORT).show();
    
    //Search string
    searchType = "school";
 
    //for Places API, return JSON string with info needed
    placesSearchString = "https://maps.googleapis.com/maps/api/place/textsearch/" 
        +"json?location="
        +latlng.latitude +","+ latlng.longitude
        +"&radius=" + radius 
        +"&sensor=true" 
        +"&query=school"
        +"&types=" + searchType
        +"&key=" + key;
    new PlacesSearch().execute(placesSearchString);
  }

  //JSON parsing class for Places API
  class PlacesSearch extends AsyncTask<String, Void, List<POI> >{

    @Override
    protected List<POI> doInBackground(String... urls) {

      
      //these POI's will be used to add markers to the map later on
      List<POI>markers = new ArrayList<POI>();
      String 
      lat="",
      lng="",
      name="",
      icon=""; 


      JSONArray jArray = null;
      JSONObject json = null;

      json = getJSONFromUrl(urls[0]);
      try{
        jArray = json.getJSONArray("results");

        for (int i = 0; i < jArray.length(); i++)
        {
          JSONObject o = jArray.getJSONObject(i);
          JSONObject geom = o.getJSONObject("geometry");
          //location is within the geometry element
          JSONObject location = geom.getJSONObject("location");

          icon = o.getString("formatted_address");
          lat  = location.getString("lat");
          lng  = location.getString("lng");

          name = o.getString("name");

          
          //add the new POI to the list
          markers.add(new POI(lat, lng, name, icon));
        }
      }
      catch(JSONException e)
      {
        markers.add(new POI("0", "0", e.getLocalizedMessage(), icon));
      }
      return markers;
    }

    @Override
    protected void onPostExecute(List<POI> markerList) {

      for (int i = 0; i < markerList.size(); i++)
      {

        POI poi = markerList.get(i);
        map.addMarker(new MarkerOptions()
        .position(new LatLng(Double.valueOf(poi.lat),
            Double.valueOf(poi.lng)))
            .title(poi.name)
            .snippet(poi.icon));


        //zoom in on the first location returned 
        if (i == markerList.size()){
          map.animateCamera(CameraUpdateFactory
              .newLatLngZoom(new LatLng(Double.parseDouble(poi.lat), 
                  Double.parseDouble(poi.lng)),
                  10));

        }
        text.setText("Showing schools near: " + zipCode);

      }
      
      map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){

        @Override
        public void onInfoWindowClick(Marker marker) {
          String url = marker.getTitle() + " " + zipCode;
          //url.replaceAll("\\s","+");
          try {
            url = URLEncoder.encode(url, "UTF-8");
          } catch (UnsupportedEncodingException e) {
            url="";
          }
          url = "http://google.com/search?&q=" + url;
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setData(Uri.parse(url));
          startActivity(intent);
        }
        
      });
    }



    private JSONObject getJSONFromUrl (String url)
    {
      //gets the JSON data and places it in JSONstring
      JSONObject jsonobj = null;

      //JSONstring parsing
      try{
        jsonobj = 
            new JSONObject(downloadJSONString(url));
      }
      catch (JSONException e)
      {
        Toast.makeText(SchoolView.this,  "Error loading points of interest.", Toast.LENGTH_LONG).show();
      }

      return jsonobj;
    }

    private String downloadJSONString(String urlString) {

      HttpClient placesClient = new DefaultHttpClient();
      StringBuilder placesBuilder = new StringBuilder();

      try{
        HttpGet placesGet = new HttpGet(urlString);
        HttpResponse placesResponse = placesClient.execute(placesGet);
        StatusLine placeSearchStatus = placesResponse.getStatusLine();

        if (placeSearchStatus.getStatusCode() == 200)
        {
          HttpEntity placesEntity = placesResponse.getEntity();
          InputStream placesContent = placesEntity.getContent();
          InputStreamReader placesInput = new InputStreamReader(placesContent);
          BufferedReader placesReader = new BufferedReader(placesInput);
          String lineIn;
          while ((lineIn = placesReader.readLine()) != null) {
            placesBuilder.append(lineIn);
          }
        }
      }
      catch (IOException e)
      { placesBuilder.append("Error loading data");}
      return placesBuilder.toString();
    }

  }

  class POI  
  {
    String lat, lng, name, icon;

    POI(String lat2, String lng2, String name2, String icon2)
    {
      lat=lat2;
      lng=lng2;
      name=name2;
      icon=icon2;
    }

  }
}
