package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.myapplication.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    List<Address> geoCoderList;
    private Marker currentLocation;
    List<Marker> locations = new ArrayList();

    // initializing our search view.
    SearchView searchView;

    boolean locationChanged=false;


    private static final int LOCATION_PERMISSION_CODE=1;
//    Now  we have to draw the polygon in the area aswell,
//    So we will create a var named polygon and the line to connect the markers
    Polygon area;
    Polyline distance;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    //initializing our searchview
        searchView = findViewById(R.id.idSearchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // on below line we are getting the
                // location name from search view.
                String location = searchView.getQuery().toString();

                // below line is to create a list of address
                // where we will store the list of all address.
                List<Address> addressList = null;

                // checking if the entered location is null or not.
                if (location != null || location.equals("")) {
                    // on below line we are creating and initializing a geo coder.
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);

                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    // on below line we are adding marker to that position.
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));

                    // below line is to animate camera to that position.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    locationChanged=true;

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        // at last we calling our map fragment to update.
        mapFragment.getMapAsync(this);

    }
    
    
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());

//        checking if the permission is granted
//        if(isLocationpermissionGranted()){
//            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                    .findFragmentById(R.id.map);
//            mapFragment.getMapAsync(this);
//            try{
//                geoCoderList= new Geocoder(this).getFromLocationName("12 Tealham Drive, Etobicoke",1);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            Double longitude=geoCoderList.get(0).getLongitude();
//            Double lattitude=geoCoderList.get(0).getLatitude();
//            Log.i("GOOGLE_MAP_TAG","Address has Longitude ::: "+String.valueOf(longitude)+ "and Lattitude:::  "+String.valueOf(lattitude));
//
//        }else{
//            requestLocationPermission();
//        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

//    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    
    LocationManager manager1;
    LocationListener listener1;
    
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        manager1 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener1 = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                
                setHomeMarker(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!isLocationpermissionGranted()){
            requestLocationPermission();
        }else{
            startUpdateLocation();
        }


        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(@NonNull Polyline polyline) {
                List<LatLng> test = polyline.getPoints();
                float[] results = new float[1];
                Location.distanceBetween(test.get(0).latitude, test.get(0).longitude,
                        test.get(1).latitude, test.get(1).longitude,
                        results);

                Toast.makeText(MapsActivity.this,"Total Distance Between points is: "+results[0]+" units", Toast.LENGTH_LONG).show();
                polyline.setTag("Something "+ results[0]);
            }
        });
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(@NonNull Polygon polygon) {
                List<LatLng> test = polygon.getPoints();
                float[] ABDistance = new float[1];
                Location.distanceBetween(test.get(0).latitude, test.get(0).longitude,
                        test.get(1).latitude, test.get(1).longitude,
                        ABDistance);

                float[] BCDistance = new float[1];
                Location.distanceBetween(test.get(0).latitude, test.get(0).longitude,
                        test.get(1).latitude, test.get(1).longitude,
                        BCDistance);

                float[] CDDistance = new float[1];
                Location.distanceBetween(test.get(0).latitude, test.get(0).longitude,
                        test.get(1).latitude, test.get(1).longitude,
                        CDDistance);

                float[] DADistance = new float[1];
                Location.distanceBetween(test.get(0).latitude, test.get(0).longitude,
                        test.get(1).latitude, test.get(1).longitude,
                        DADistance);
                float TotalDistance= ABDistance[0]+BCDistance[0]+CDDistance[0]+DADistance[0];
                Toast.makeText(MapsActivity.this, "Total Distance is: "+TotalDistance+" units", Toast.LENGTH_LONG).show();
            }
        });

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                setMarker(latLng);
            }
            private void setMarker(LatLng latLng) {
                if (locations.size() == 4)
                    clearMap();

                if (locations.size() == 0){
                    MarkerOptions options = new MarkerOptions().position(latLng)
                            .title("A");

                    // check if there are already the same number of markers, we clear the map.
                    locations.add(mMap.addMarker(options));
                }else if (locations.size() == 1){
                    MarkerOptions options = new MarkerOptions().position(latLng)
                            .title("B");

                    // check if there are already the same number of markers, we clear the map.
                    locations.add(mMap.addMarker(options));
                }else if (locations.size() == 2){
                    MarkerOptions options = new MarkerOptions().position(latLng)
                            .title("C");

                    // check if there are already the same number of markers, we clear the map.
                    locations.add(mMap.addMarker(options));
                }else if (locations.size() == 3){
                    MarkerOptions options = new MarkerOptions().position(latLng)
                            .title("D");

                    // check if there are already the same number of markers, we clear the map.
                    locations.add(mMap.addMarker(options));
                }

                if (locations.size() == 4)
                {
                    drawShape();
                    drawLine();
                }

            }

            private void clearMap() {
                for (Marker marker: locations)
                    marker.remove();

                locations.clear();
                area.remove();
                area = null;
            }


            private void drawLine() {
                PolylineOptions options1 = new PolylineOptions()
                        .color(Color.RED)
                        .width(10)
                        .add(locations.get(0).getPosition(), locations.get(1).getPosition());
                options1.clickable(true);
                options1.zIndex(2F);
                mMap.addPolyline(options1);

                PolylineOptions options2 = new PolylineOptions()
                        .color(Color.RED)
                        .width(10)
                        .add(locations.get(1).getPosition(), locations.get(2).getPosition());
                options2.clickable(true);
                options2.zIndex(2F);
                mMap.addPolyline(options2);

                PolylineOptions options3 = new PolylineOptions()
                        .color(Color.RED)
                        .width(10)
                        .add(locations.get(2).getPosition(), locations.get(3).getPosition());
                options3.clickable(true);
                options3.zIndex(2F);
                mMap.addPolyline(options3);

                PolylineOptions options4 = new PolylineOptions()
                        .color(Color.RED)
                        .width(10)
                        .add(locations.get(3).getPosition(), locations.get(0).getPosition());
                options4.clickable(true);
                options4.zIndex(2F);
                mMap.addPolyline(options4);


            }

            private void drawShape() {
                PolygonOptions options = new PolygonOptions()
                        .fillColor(0x5900FF00)
                        // 0xFF00FF00 is green and 59 instead of FF is 35% transparency
                        .strokeColor(Color.RED)
                        .strokeWidth(5);
                options.clickable(true);

                ArrayList<LatLng> sourcePoints = new ArrayList<>();

                for (int i=0; i<4; i++) {
                    options.add(locations.get(i).getPosition());
//                    sourcePoints.add(markers.get(i).getPosition());

                }
                mMap.addPolygon(options);
            }
        });

    }
    private boolean isLocationpermissionGranted(){
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }

    private void requestLocationPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
    }

    @SuppressLint("MissingPermission")
    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        manager1.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, listener1);
//        Location lastKnownLocation = manager1.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//        setHomeMarker(lastKnownLocation);

        manager1.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,0,listener1);
        Location lastlocation= manager1.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        setHomeMarker(lastlocation);
    }
    private void setHomeMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("Your Location");
        currentLocation = mMap.addMarker(options);
        if(!locationChanged)
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
        }
    }
    private boolean helper_Code(Point a, Point b, Point c) {
        return (b.y - a.y) * (c.x - b.x) - (b.x - a.x) * (c.y - b.y) > 0;
    }

    public ArrayList<Point> convexHull(ArrayList<Point> points)
    {
        int n = points.size();
        if (n <= 3) return points;

        ArrayList<Integer> next = new ArrayList<>();

        // find the leftmost point
        int leftMost = 0;
        for (int iterator = 1; iterator < n; iterator++)
            if (points.get(iterator).x < points.get(leftMost).x)
                leftMost = iterator;
        int p = leftMost, q;
        next.add(p);

        // iterate till p becomes leftMost
        do {
            q = (p + 1) % n;
            for (int i = 0; i < n; i++)
                if (helper_Code(points.get(p), points.get(i), points.get(q)))
                    q = i;
            next.add(q);
            p = q;
        } while (p != leftMost);

        ArrayList<Point> convexHullPoints = new ArrayList();
        for (int i = 0; i < next.size() - 1; i++) {
            int ix = next.get(i);
            convexHullPoints.add(points.get(ix));
        }

        return convexHullPoints;
    }
}