package com.example.baswarajmamidgi.locationreceiver;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener, SearchView.OnSuggestionListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private LocationManager manager;
    private ProgressDialog progressDialog;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    final static int REQUEST_LOCATION = 199;
    private static Location mLastLocation;
    private final Marker[] marker = {null};
    private int proximity;
    private boolean alarmstate;
    private SharedPreferences preferences;
    private SearchView searchView;
    public static ArrayList<String> arrayList;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        FloatingActionButton favourites = new FloatingActionButton(this);
        FloatingActionButton mylocation = new FloatingActionButton(this);
        arrayList = new ArrayList<>();
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                getbusstops();
            }
        });
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        View mapView = mapFragment.getView();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        alarmstate = preferences.getBoolean("switch_preference_1", true);
        String distance = preferences.getString("list_preference_distance", "1000");
        proximity = Integer.parseInt(distance);
        boolean isconnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if (!isconnected) {
            Toast.makeText(this, "No Internet..Check your internet Connection ", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = getIntent();
        String trackid = i.getStringExtra("result");
        if (trackid != null) {
            receivelocation(trackid);
        }
        String alarmbus = i.getStringExtra("alarmbus");
        if (alarmbus != null) {
            receivelocation(alarmbus);
        }
        ArrayList<String> list = i.getStringArrayListExtra("searchresults");
        if (list != null) {
            showallbuses(list);
        }
        favourites = (FloatingActionButton) findViewById(R.id.favouritesbutton);
        favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Buseslist.class);
                i.putExtra("activity", "Favourites");
                startActivity(i);


            }
        });
        mylocation = (FloatingActionButton) findViewById(R.id.mylocation);
        mylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (location != null) {
                    LatLng currentlocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(currentlocation, 17)));
                } else {
                    Toast.makeText(MapsActivity.this, "GPS unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        mMap.setOnMarkerClickListener(this);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.INTERNET}
                        , 10);
                return;
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(17.375409, 78.476779), 12));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if(location!=null) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        CameraUpdate cameraupdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                        mMap.animateCamera(cameraupdate);
                    }
                    else{
                        Toast.makeText(MapsActivity.this, "Location is unavailable", Toast.LENGTH_SHORT).show();

                    }
                }
            },1000);


        } else {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latLng, 16)));

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mapmenu, menu);
        MenuItem searchitem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchitem);
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(manager.getSearchableInfo(new ComponentName(this, MapsActivity.class)));
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("Search Bus stop,Bus");
        searchView.setIconifiedByDefault(false);
        searchView.setOnSuggestionListener(this);
        searchView.setMaxWidth(10000);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    mMap.setMyLocationEnabled(true);


        }
    }


    public void receivelocation(final String id) {

        alarmstate = preferences.getBoolean("alarmstate", false);
        databaseReference = firebaseDatabase.getReference().child("busroutes").child(id);
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("alarmid", id);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mMap.clear();
                Map<String, String> map = (Map) dataSnapshot.getValue();
                double latitude = Double.parseDouble(map.get("latitude"));
                double longitude = Double.parseDouble(map.get("longitude"));
                LatLng location = new LatLng(latitude, longitude);

                String datestring = map.get("time");
                SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                        Locale.ENGLISH);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    Date date = sdf.parse(datestring);
                    dateFormat.format(date);
                    Date currentdate = new Date();
                    if ((currentdate.getTime() - date.getTime()) < 600000) {
                        marker[0] = mMap.addMarker(new MarkerOptions().position(location).title(id).icon(getBitmapDescriptor(R.drawable.ic_directions_bus_active)).draggable(false));

                    } else {
                        marker[0] = mMap.addMarker(new MarkerOptions().position(location).title(id).icon(getBitmapDescriptor(R.drawable.ic_directions_bus)).draggable(false));

                    }


                    Log.i("log date", dateFormat.format(date));
                    Log.i("sys date", dateFormat.format(currentdate));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
                CameraPosition position = new CameraPosition.Builder().target(location).zoom(15).bearing(10).tilt(30).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                double latitude = Double.parseDouble(map.get("latitude"));
                double longitude = Double.parseDouble(map.get("longitude"));
                LatLng location = new LatLng(latitude, longitude);
                Location destination = new Location("");
                destination.setLatitude(latitude);
                destination.setLongitude(longitude);
                animateMarker(destination, marker[0]);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(location));
                if (alarmstate) {
                    manager.addProximityAlert(latitude, longitude, proximity, 0, pendingIntent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        LatLng markerPositionlocation = marker.getPosition();
        marker.showInfoWindow();
        try {
            Log.i("log", "on marker click called");
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //return TODO;
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            LatLng userlocation = new LatLng(location.getLatitude(), location.getLongitude());
            sendRequest(markerPositionlocation, userlocation);
        } catch (Exception e) {
            Toast.makeText(this, "GPS NOT AVAILABLE", Toast.LENGTH_LONG).show();
            Log.i("marker exception", e.getLocalizedMessage());
        }
        return true;
    }

    private void sendRequest(LatLng origin, LatLng destination) {
        try {
            new DirectionFinder((DirectionFinderListener) this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding distance..!", true);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    @Override
    public void onDirectionFinderSuccess(Route route) {
        progressDialog.dismiss();
        CoordinatorLayout coordinatorlayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        Snackbar.make(coordinatorlayout, "Arrives in " + route.duration.text + " " + "     Distance " + route.distance.text, Snackbar.LENGTH_INDEFINITE).show();
    }


    public static void animateMarker(final Location destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());

            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.LinearFixed();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(5000); // duration 1 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setRotation(computeRotation(v, startRotation, destination.getBearing()));
                    } catch (Exception ex) {
                    }
                }
            });

            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        getLocation();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MapsActivity.this,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode)
        {
            case REQUEST_LOCATION:
                switch (resultCode)
                {
                    case Activity.RESULT_OK:
                    {
                        // All required changes were successfully made
                        break;
                    }
                    case Activity.RESULT_CANCELED:
                    {
                        Toast.makeText(MapsActivity.this, "Location not enabled.", Toast.LENGTH_LONG).show();
                        break;
                    }
                    default:
                    {
                        break;
                    }
                }
                break;
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();

        if(id==R.id.buses)
        {
            Intent i=new Intent(this,Buseslist.class);
            i.putExtra("activity","Buses list");
            startActivity(i);
        }
        if(id==R.id.alarm)
        {
            startActivity(new Intent(this,preference.class));
        }
        if(id==R.id.Feedback)
        {
            Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "baswarajmamidgi10@gmail.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Bus Tracker Feedback");
            Intent i=new Intent(MapsActivity.this,feedback.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public  void showallbuses(ArrayList<String> buslist) {
        Log.i("log", "in show all buses");
        for (final String bus : buslist) {
            databaseReference=firebaseDatabase.getReference().child("busroutes").child(bus);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, String> map = (Map)dataSnapshot.getValue();
                    double latitude = Double.parseDouble(map.get("latitude"));
                    double longitude = Double.parseDouble(map.get("longitude"));
                    LatLng location = new LatLng(latitude, longitude);
                    Location destination = new Location("");
                    destination.setLatitude(latitude);
                    destination.setLongitude(longitude);
                    mMap.addMarker(new MarkerOptions().position(location).title(bus).icon(getBitmapDescriptor(R.drawable.ic_directions_bus)));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        int id = (int) searchView.getSuggestionsAdapter().getItemId(position);
        ArrayList<String> busstops = arrayList;
        ArrayList<String> allstops = new ArrayList<>();
        ArrayList<String> busno = new ArrayList<>();

        for (String stops : busstops) {

            String[] stopslist = stops.split(",");

            for (String list : stopslist) {

                if(!allstops.contains(list.toLowerCase())) {
                    allstops.add(list.toLowerCase());
                }
            }
            busno.add(stopslist[0]);
            Log.i("log bus",stopslist[0]);
        }
        String query = allstops.get(id);
        Log.i("log query",query);
        if (busno.contains(query.toUpperCase())) {
            receivelocation(query.toUpperCase());
            searchView.clearFocus();



        } else {

        Intent i = new Intent(this, SearchResultsActivity.class);
        i.putExtra("data", query);
        startActivity(i);
    }

        return true;
    }

    public void getbusstops()
    {
        databaseReference=firebaseDatabase.getReference().child("busstops");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    String message = snapshot.getValue(String.class);
                    arrayList.add(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private BitmapDescriptor getBitmapDescriptor(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            VectorDrawable vectorDrawable = (VectorDrawable) getDrawable(id);

            assert vectorDrawable != null;
            int h = vectorDrawable.getIntrinsicHeight();
            int w = vectorDrawable.getIntrinsicWidth();

            vectorDrawable.setBounds(0, 0, w, h);

            Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            vectorDrawable.draw(canvas);

            return BitmapDescriptorFactory.fromBitmap(bm);

        } else {
            return BitmapDescriptorFactory.fromResource(id);
        }
    }

    void getLocation() {

        if (ContextCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        /*TODO!! INSERT CODE TO PROMPT USER TO GIVE PERMISSION*/

        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,this);
        }

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        Log.i("log","location changed called");

    }



}
