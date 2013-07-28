package com.example.groupproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SchoolView extends Activity{

  static TextView text;
  GoogleMap map;
  String searchText="";
  
  //this is my Google API key (for Places API)
  private final String key = "AIzaSyCM_9XOwDOglYF0-p86M8mlZhLeg2gVBfk";
  
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
    LatLng latlng = new LatLng(MainActivity.latitude, MainActivity.longitude);

    //Search string
    searchType = "school";

    //for Places API, return JSON string with info needed
    placesSearchString = "" +
        "https://maps.googleapis.com/maps/api/place/textsearch/" 
        +"json?location="
        +latlng.latitude +","+ latlng.longitude
        +"&radius=" + radius 
        +"&sensor=true" 
        +"&query=school"
        +"&types=" + searchType
        +"&key=" + key;
    
    setContentView(R.layout.schoolview);

    text = (TextView)findViewById(R.id.mapText);

     map = ((MapFragment)
        getFragmentManager().findFragmentById(R.id.map))
        .getMap();
    // if (map == null)return;
    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 11));
    
    new PlacesSearch().execute(placesSearchString);
  }


  //JSON parsing class for Places API
  class PlacesSearch extends AsyncTask<String, Void, List<POI>>{

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
      
   BitmapDescriptor bmp;
   Marker mark;
     for (int i = 0; i < markerList.size(); i++)
      {

       POI poi = markerList.get(i);
      mark = map.addMarker(new MarkerOptions()
      .position(new LatLng(Double.valueOf(poi.lat),
                           Double.valueOf(poi.lng)))
                  .title(poi.name)
                  .snippet(poi.icon));
      if (i < 1){
        map.animateCamera(CameraUpdateFactory
            .newLatLngZoom(new LatLng(Double.parseDouble(poi.lat), 
                                      Double.parseDouble(poi.lng)),
             10));
      }
      
      try{
        bmp = BitmapDescriptorFactory.fromBitmap(
            BitmapFactory.decodeStream(
                downloadUrl(poi.icon)));
      }
      catch (Exception e)
      {
        bmp = BitmapDescriptorFactory.defaultMarker();
      }
      mark.setIcon(bmp);
      text.setText("Results found: " + i+1);
      
       }
     

      
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
    
    private InputStream downloadUrl(String urlString) 
        throws IOException
        {
      URL url = new URL(urlString);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setReadTimeout(10000 );
      conn.setConnectTimeout(15000);
      conn.setRequestMethod("GET");
      conn.setDoInput(true);
      conn.connect();
      return conn.getInputStream();
        }

  }

  class POI  
  {
    //String icon, name, rating, lat, lng, reference;
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
