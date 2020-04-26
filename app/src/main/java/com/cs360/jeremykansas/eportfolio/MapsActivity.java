package com.cs360.jeremykansas.eportfolio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 99;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    // demo/hardcoded job interview is at SNHU
    private final LatLng jobLoc = new LatLng(43.0372, -71.4523);
    private LatLng currentLoc;

    private Marker jobMarker;
    private Marker currentMarker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);


        // location provider lets us get current ('last') location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // check for permission for coarse location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // no permission -- so request it:
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            // already have permission, so get location
            getLocation();
        }

    }

    // get location if permitted
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // permission granted, so get location
            getLocation();
        }
    }


    // store the map once its available, then update markers and camera
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // update map markers and camera
        updateMarkers();
        updateCamera();
    }

    // add menu buttons to top bar - launch maps app for directions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_map, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // run appropriate code if menu option selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.get_directions) {

            // build query string for map url (opens in app or web if app unavailable)
            // origin omitted because it defaults to current location
            // and we may not have it if location permission is denied
            String destination = jobLoc.latitude + "," + jobLoc.longitude;
            String url = "https://www.google.com/maps/dir/?api=1&destination=" + destination;

            // and run it
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    // get user's last location and, on success, store it in currentLoc
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            currentLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            // update markers and camera
                            updateMarkers();
                            updateCamera();
                        }
                    }
                });
    }

    // I don't think this is the best way to go about this, but it works for now...
    // want to be able to create markers and update camera based on not knowing whether other
    // methods will succeed or when they will complete
    private void updateMarkers() {
        // if needed, add marker for job location, show its info window
        if (jobMarker == null) {
            jobMarker = mMap.addMarker(new MarkerOptions().position(jobLoc).title("Job Interview at SNHU"));
            jobMarker.showInfoWindow();
        }
        // if needed, add marker for current location
        if (currentLoc != null && currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(currentLoc).title("Current Location"));
        }

    }

    // setup camera using lat/lng of both locations, or just jobLoc if no currentLoc
    private void updateCamera() {
        if (currentLoc != null){
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(jobLoc).include(currentLoc);
            LatLngBounds bounds = builder.build();                       // 200dp padding, somewhat arbitrary...
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200 * (int) this.getResources().getDisplayMetrics().density));
        } else {                                                    // 13 is between city and street level zoom
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(jobLoc, 13));
        }
    }
}
