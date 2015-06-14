package com.strengthcoach.strengthcoach.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.helpers.GPSTracker;

public class MapActivity extends ActionBarActivity {

    GoogleMap m_map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    m_map = map;

                    // Get coordinates
                    GPSTracker gpsTracker = new GPSTracker(getBaseContext());
                    double latitude = gpsTracker.getLatitude();
                    double longitude = gpsTracker.getLongitude();
                    LatLng point = new LatLng(latitude, longitude);
                    m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));

                    /*
                    BitmapDescriptor defaultMarker =
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    // Extract content from alert dialog
                    String title = m_trainer.getGym().getName();
                    String snippet = m_trainer.getGym().getAddress().toString();
                    // Creates and adds marker to the map
                    Marker marker = m_map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(title)
                            .snippet(snippet)
                            .icon(defaultMarker));
                    marker.showInfoWindow();
                    */
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
