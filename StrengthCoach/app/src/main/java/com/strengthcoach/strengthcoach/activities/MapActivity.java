package com.strengthcoach.strengthcoach.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.helpers.GPSTracker;
import com.strengthcoach.strengthcoach.models.Gym;

import java.util.List;

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

                    m_map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            // TODO: pass gym information to main activity to show trainers only from there
                            String title = marker.getTitle();
                            String gymName = title.substring(0, title.indexOf("(") - 1);
                            Intent intent = new Intent();
                            intent.putExtra("gymName", gymName);
                            setResult(RESULT_OK, intent);
                            finish();
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        }
                    });

                    // Get coordinates
                    GPSTracker gpsTracker = new GPSTracker(getBaseContext());
                    double latitude = gpsTracker.getLatitude();
                    double longitude = gpsTracker.getLongitude();
                    final LatLng point = new LatLng(latitude, longitude);

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(point)      // Sets the center of the map to Mountain View
                            .zoom(0)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    m_map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(point)      // Sets the center of the map to Mountain View
                                    .zoom(15)                   // Sets the zoom
                                    .build();                   // Creates a CameraPosition from the builder
                            m_map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }

                        @Override
                        public void onCancel() {

                        }
                    });

                    ParseQuery<Gym> query = ParseQuery.getQuery("Gym");
                    query.include("address");
                    query.include("trainers");
                    query.findInBackground(new FindCallback<Gym>() {
                        public void done(List<Gym> gyms, com.parse.ParseException e) {
                            for (int i = 0; i < gyms.size(); i++) {
                                BitmapDescriptor defaultMarker =
                                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                                // Extract content from alert dialog
                                String title = gyms.get(i).getName() + " (" + gyms.get(i).getTrainers().size() + " trainers)";
                                String snippet = gyms.get(i).getAddress().toString();
                                ParseGeoPoint parseGeoPoint = gyms.get(i).point();
                                LatLng point = new LatLng(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude());

                                // Creates and adds marker to the map
                                Marker marker = m_map.addMarker(new MarkerOptions()
                                        .position(point)
                                        .title(title)
                                        .snippet(snippet)
                                        .icon(defaultMarker));
                                marker.showInfoWindow();
                            }
                        }
                    });
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
