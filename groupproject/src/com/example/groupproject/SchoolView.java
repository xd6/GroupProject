package com.example.groupproject;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class SchoolView extends Activity{
  
  LocationClient coord;
  TextView text;
  
  
  @Override
  protected void onCreate (Bundle bundle)
  {
    super.onCreate(bundle);
   coord = MainActivity.location_client;
/*   Toast.makeText(this,
        GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this),
        Toast.LENGTH_LONG).show();*/
    
    
   setContentView(R.layout.schoolview);
   
   text = (TextView)findViewById(R.id.mapText);
   text.setText(MainActivity.latitude + ", " + MainActivity.longitude);
    GoogleMap map = ((MapFragment)
        getFragmentManager().findFragmentById(R.id.map))
        .getMap();
    if (map == null)return;
    
    
    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    
    LatLng latlng = new LatLng(MainActivity.latitude, MainActivity.longitude);
    Marker marker1 = map.addMarker(new MarkerOptions().position(latlng).title("Panama City"));
    
    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 13));
   
   marker1.setDraggable(true);
   map.setOnMarkerDragListener(new OnMarkerDragListener(){

    @Override
    public void onMarkerDrag(Marker marker) {
      marker.setTitle(marker.getPosition().latitude + ", " + marker.getPosition().longitude);
      
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
      
      
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
      // TODO Auto-generated method stub
      
    }
     
   });
    
    
    
  }

}
