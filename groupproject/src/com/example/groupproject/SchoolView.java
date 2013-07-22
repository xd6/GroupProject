package com.example.groupproject;

import com.example.groupproject.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class SchoolView extends Activity{
  
  @Override
  protected void onCreate (Bundle bundle)
  {
    super.onCreate(bundle);
   int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
   if(resp != ConnectionResult.SUCCESS)
      return;
   
   
   Toast.makeText(this,
        GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(this),
        Toast.LENGTH_LONG).show();
    
    setContentView(R.layout.schoolview);
/*    
    
    GoogleMap map = ((MapFragment) 
        getFragmentManager().findFragmentById(R.id.map))
        .getMap();
    if (map == null)return;
    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    
    LatLng coord = new LatLng(-85.66, 30.16);
    Marker pc = map.addMarker(new MarkerOptions().position(coord).title("Panama City"));*/
   
    
    
    
  }

}
