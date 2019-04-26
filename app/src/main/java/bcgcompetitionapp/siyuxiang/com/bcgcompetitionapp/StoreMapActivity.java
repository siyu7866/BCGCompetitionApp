package bcgcompetitionapp.siyuxiang.com.bcgcompetitionapp;

import android.Manifest;
import android.content.Context;
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
import android.widget.ListView;
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
                if (item == "Net Income") {
                    final String[] rangePrice = {"2.0M~2.2M", "1.8M~2.0M", "1.6M~1.8M", "1.4~1.6M"};
                    final boolean[] checkedRange = {false, false, false, false};
                    builder.setTitle("Net Income");
                    builder.setMultiChoiceItems(rangePrice, checkedRange, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if(isChecked) {
                                switch (which) {
                                    case 0: getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                            edit().putInt("maxnetincome", 2200000).putInt("minnetincome", 2000000).apply();
                                        break;
                                    case 1: getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                            edit().putInt("maxnetincome", 2000000).putInt("minnetincome", 1800000).apply();
                                        break;
                                    case 2: getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                            edit().putInt("maxnetincome", 1800000).putInt("minnetincome", 1600000).apply();
                                        break;
                                    case 3: getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                            edit().putInt("maxnetincome", 1600000).putInt("minnetincome", 1400000).apply();
                                        break;
                                }
                            }
                        }
                    });
                    builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int maxNetIncome = getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                    getInt("maxnetincome", 2200000);
                            int minNetIncome = getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                    getInt("minnetincome", 2000000);
                            try {
                                CaseDataSource ds = new CaseDataSource(StoreMapActivity.this);
                                ds.open();
                                storeAddresses = ds.getStoreAddressesOnNetIncome(maxNetIncome, minNetIncome);
                                ds.close();
                            }
                            catch (Exception e) {
                                Toast.makeText(StoreMapActivity.this, "Address(es) couldn't be retrieved", Toast.LENGTH_LONG).show();
                            }
                            gMap.clear();
                            for (int i = 0; i < storeAddresses.size(); i++) {
                                createMarker(storeAddresses.get(i).getAddress(), storeAddresses.get(i).getCity(), storeAddresses.get(i).getState(),
                                        storeAddresses.get(i).getZipCode(), storeAddresses.get(i).getNetIncome());
                            }
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                CaseDataSource ds = new CaseDataSource(StoreMapActivity.this);
                                ds.open();
                                storeAddresses = ds.getStoreAddresses();
                                ds.close();
                            }
                            catch (Exception e) {
                                Toast.makeText(StoreMapActivity.this, "Address(es) couldn't be retrieved", Toast.LENGTH_LONG).show();
                            }
                            for (int i = 0; i < storeAddresses.size(); i++) {
                                createMarker(storeAddresses.get(i).getAddress(), storeAddresses.get(i).getCity(), storeAddresses.get(i).getState(),
                                        storeAddresses.get(i).getZipCode(), storeAddresses.get(i).getNetIncome());
                            }
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
                else if (item == "Revenue") {
                    final String[] rangeRevenue = {"2.0M~2.2M", "1.8M~2.0M", "1.6M~1.8M", "1.4~1.6M"};
                    final boolean[] checkedRange = {false, false, false, false};
                    builder.setTitle("Revenue");
                    builder.setMultiChoiceItems(rangeRevenue, checkedRange, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            if(isChecked) {
                                switch (which) {
                                    case 0: getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                            edit().putInt("maxrevenue", 2200000).putInt("minrevenue", 2000000).apply();
                                        break;
                                    case 1: getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                            edit().putInt("maxrevenue", 2000000).putInt("minrevenue", 1800000).apply();
                                        break;
                                    case 2: getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                            edit().putInt("maxrevenue", 1800000).putInt("minrevenue", 1600000).apply();
                                        break;
                                    case 3: getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                            edit().putInt("maxrevenue", 1600000).putInt("minrevenue", 1400000).apply();
                                        break;
                                }
                            }
                        }
                    });
                    builder.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int maxRevenue = getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                    getInt("maxrevenue", 2200000);
                            int minRevenue= getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                    getInt("minrevenue", 2000000);
                            try {
                                CaseDataSource ds = new CaseDataSource(StoreMapActivity.this);
                                ds.open();
                                storeAddresses = ds.getStoreAddressesOnAverageRevenue(maxRevenue, minRevenue);
                                ds.close();
                            }
                            catch (Exception e) {
                                Toast.makeText(StoreMapActivity.this, "Address(es) couldn't be retrieved", Toast.LENGTH_LONG).show();
                            }
                            gMap.clear();
                            for (int i = 0; i < storeAddresses.size(); i++) {
                                createMarker(storeAddresses.get(i).getAddress(), storeAddresses.get(i).getCity(), storeAddresses.get(i).getState(),
                                        storeAddresses.get(i).getZipCode(), storeAddresses.get(i).getOrderRevenue());
                            }
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

    private List<String> getSpinnerData() {
        List<String> dataList = new ArrayList<String>();
        dataList.add("Net Income");
        dataList.add("Revenue");
        return dataList;
    }
}
