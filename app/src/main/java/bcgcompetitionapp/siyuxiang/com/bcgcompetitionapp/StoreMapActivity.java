package bcgcompetitionapp.siyuxiang.com.bcgcompetitionapp;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        com.google.android.gms.location.LocationListener {

    GoogleMap gMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    CaseData mCaseData;

    ArrayList<CaseData> storeAddresses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        try {
            CaseDataSource ds = new CaseDataSource(StoreMapActivity.this);
            ds.open();
            storeAddresses = ds.getStoreAddresses();
            ds.close();
        }
        catch (Exception e) {
            Toast.makeText(this, "Address(es) couldn't be retrieved", Toast.LENGTH_LONG).show();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mapFragment.getMapAsync(this);
        createLocationRequest();

        final Spinner mapFilterSpinner = (Spinner) findViewById(R.id.spinnerFilter);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(StoreMapActivity.this,
                android.R.layout.simple_spinner_item, getSpinnerData());
        mapFilterSpinner.setAdapter(adapter);
        mapFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(StoreMapActivity.this);
                String item = (String) mapFilterSpinner.getSelectedItem();
                if (item == "Price") {
                    final String[] rangePrice = {"30000~50000", "50000~70000"};
                    builder.setTitle("Price");
                    builder.setMultiChoiceItems(rangePrice, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            Toast.makeText(StoreMapActivity.this, "Success", Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        initButtonStoreChart();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        for (int i = 0; i < storeAddresses.size(); i++) {
            createMarker(storeAddresses.get(i).getAddress(), storeAddresses.get(i).getCity(), storeAddresses.get(i).getState(),
                    storeAddresses.get(i).getZipCode(), storeAddresses.get(i).getNetIncome());
        }

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        gMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    private List<String> getSpinnerData() {
        List<String> dataList = new ArrayList<String>();
        dataList.add("Price");
        dataList.add("Revenue");
        return dataList;
    }

    private void initButtonStoreChart() {
        ImageButton buttonStoreChart = (ImageButton) findViewById(R.id.buttonStoreChart);
        buttonStoreChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoreMapActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionFailed (@NonNull ConnectionResult connectionResult) {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void startLocationUpdates () {
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getBaseContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            return;
        }
        gMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(getBaseContext(), "Lat: " + location.getLatitude() +
                        " Long: " + location.getLongitude() +
                        " Accuracy: " + location.getAccuracy(),
                Toast.LENGTH_LONG).show();
    }

    protected Marker createMarker(String streetAddress, String city, String state, String zipCode, int netIncome) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        Geocoder geo = new Geocoder(this);
        List<Address> addresses = null;

        String address = streetAddress + ", " +
                city + ", " +
                state + ", " +
                zipCode;
        try {
            addresses = geo.getFromLocationName(address, 1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        LatLng point = new LatLng(addresses.get(0).getLatitude(),
                addresses.get(0).getLongitude());
        builder.include(point);

        return  gMap.addMarker(new MarkerOptions().position(point).
                title(String.valueOf(netIncome)).snippet(address));
    }
}
