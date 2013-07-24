package com.example.groupproject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener,
com.google.android.gms.location.LocationListener {

  TextView latitude_text;
  TextView longitude_text;
  TextView connection_status;
  public static TextView search;
  LocationClient location_client;
  boolean playSvcSuccess;

  /* Default to panama city, FL */
  public static double longitude = -85.66;
  public static double latitude = 30.16;

  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    latitude_text = (TextView)findViewById(R.id.latitude_text);
    longitude_text = (TextView)findViewById(R.id.longitude_text);
    connection_status = (TextView)findViewById(R.id.connection_status);

    /* Check if google play services are available.  If they are available,
     * we use them to determine location.  If they are not available,
     * then we have to use the default location.
     */
    int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if(resp == ConnectionResult.SUCCESS) {
      location_client = new LocationClient(this, this, this);
      location_client.connect();
      playSvcSuccess=true;
    }
    else {
      Toast.makeText(this, "Google Play Service Error " + resp, Toast.LENGTH_LONG).show();
      playSvcSuccess=false;
    }

    search = (TextView)findViewById(R.id.searchText);

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  public void weatherButtonClick(View v) {
    //String url = String.format("http://api.openweathermap.org/data/2.5/forecast/daily?lat=%s&lon=%s&cnt=5&mode=xml&units=imperial", latitude, longitude);
    //loadPage(url);

    Intent intent = new Intent(this, Weather.class);
    startActivity(intent);
  }

  public void newsFeedClick(View v)
  {
    Intent intent = new Intent(this, NewsFeed.class);
    startActivity(intent);
  }  
  public void schoolViewClick(View v)
  {
    if (playSvcSuccess)
    {
      Intent intent = new Intent(this, SchoolView.class);
      startActivity(intent);
    }
    else
      Toast.makeText(this, "Google Play Service Error - Cannot load. ", Toast.LENGTH_LONG).show();
  }

  /********************************** Location **********************************/

  /* When the activity is destroyed, disconnect the location client
   * because there is no point to running it
   */
  protected void onDestroy() {
    super.onDestroy();
    if(location_client != null) {
      location_client.disconnect();
    }
  }

  /* When the LocationClient has connected, set it to request an updated
   * location every 10 seconds.
   */
  public void onConnected(Bundle connectionHint) {
    connection_status.setText("Connection Status : Connected");

    LocationRequest location_request = LocationRequest.create();
    location_request.setInterval(10000);
    location_client.requestLocationUpdates(location_request, this);
  }

  /* When the LocationClient has connected..
   */
  public void onDisconnected() {
    connection_status.setText("Connection Status : Disconnected");
  }

  /* When the LocationClient failed to connect.
   */
  public void onConnectionFailed(ConnectionResult result) {
    connection_status.setText("Connection Status : Fail");
  }

  /* When the location changes, update the latitude and longitude. */
  public void onLocationChanged(Location location) {
    if(location!=null) {
      latitude_text.setText("Latitude (received): " + String.valueOf(location.getLatitude()));
      longitude_text.setText("Longitude (received): " + String.valueOf(location.getLongitude()));

      latitude = location.getLatitude();
      longitude = location.getLongitude();
    }
  }
}
