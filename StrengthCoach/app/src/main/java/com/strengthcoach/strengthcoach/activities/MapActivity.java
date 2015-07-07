package com.strengthcoach.strengthcoach.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

                                int count = gyms.get(i).getTrainers().size();

                                // Extract content from alert dialog
                                String title = gyms.get(i).getName() + " (" + count + " trainers)";
                                String snippet = gyms.get(i).getAddress().toString();
                                ParseGeoPoint parseGeoPoint = gyms.get(i).point();
                                LatLng point = new LatLng(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude());


                                Bitmap icon = null;
                                if (count < 10) {
                                    icon = drawTextToBitmap(getBaseContext(), R.drawable.pin_green, String.valueOf(count));
                                }
                                else {
                                    icon = drawTextToBitmap(getBaseContext(), R.drawable.pin_red, String.valueOf(count));
                                }

                                // Creates and adds marker to the map
                                Marker marker = m_map.addMarker(new MarkerOptions()
                                        .position(point)
                                        .title(title)
                                        .snippet(snippet)
                                        .icon(BitmapDescriptorFactory.fromBitmap(icon)));
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

    public Bitmap drawTextToBitmap(Context gContext,
                                   int gResId,
                                   String gText) {
        Resources resources = gContext.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap =
                BitmapFactory.decodeResource(resources, gResId);

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        Canvas canvas = new Canvas(bitmap);
        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#FFFFFF"));
        // text size in pixels
        paint.setTextSize((int) (20 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (int) ((bitmap.getHeight() + bounds.height())/2.5);

        canvas.drawText(gText, x, y, paint);

        return bitmap;
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
