package bcgcompetitionapp.siyuxiang.com.bcgcompetitionapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String DB_PATH = "/data/data/bcgcompetitionapp.siyuxiang.com.bcgcompetitionapp/databases/";
    private static String DB_NAME = "caseupdatedata_2.db";
    ArrayList<CaseData>  caseData;
    ArrayList<CaseData> storeAddresses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CaseDBHelper helper = new CaseDBHelper(this);
        try {
            helper.createDataBase();
        }
        catch (IOException e) {
            Log.e("1111111111", "e= "+e.toString());
            e.printStackTrace();
        }
        SQLiteDatabase database = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        Log.e("1111111111", "database= "+database.getPath());
        String query = "SELECT * FROM competition_fakedata_042519_1";
        Cursor cursor = database.rawQuery(query, null);
        Log.e("1111111111", "cursor= "+cursor);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndex("address"));
            Log.e("1111111111", address);
        }

        initButtonStoreLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater();
        return true;
    }

    public void onResume() {
        super.onResume();

        //CaseDataSource ds = new CaseDataSource(this);
        try {
            CaseDataSource ds = new CaseDataSource(this);
            ds.open();
            caseData = ds.getStoreAddressesOnRevenue();
            ds.close();

            ListView listView = (ListView) findViewById(R.id.listStore);
            listView.setAdapter(new StoreAdapter(this, caseData));
        }
        catch (Exception e) {
            e.getCause();
            Toast.makeText(this, "Error retrieving stores", Toast.LENGTH_LONG).show();
        }

        final Spinner mapFilterSpinner = (Spinner) findViewById(R.id.spinnerFilter);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, getSpinnerData());
        mapFilterSpinner.setAdapter(adapter);
        mapFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                String item = (String) mapFilterSpinner.getSelectedItem();
                if (item == "Net Income") {
                    final String[] rangePrice = {"2.0M~2.2M", "1.8M~2.0M", "1.6M~1.8M", "1.4M~1.6M"};
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
                                CaseDataSource ds = new CaseDataSource(MainActivity.this);
                                ds.open();
                                caseData = ds.getStoreAddressesOnNetIncome(maxNetIncome, minNetIncome);
                                ds.close();

                                ListView listView = (ListView) findViewById(R.id.listStore);
                                listView.setAdapter(new StoreAdapter(MainActivity.this, caseData));
                            }
                            catch (Exception e) {
                                e.getCause();
                                Toast.makeText(MainActivity.this, "Error retrieving stores", Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(MainActivity.this, "Max Net Income: " + String.valueOf(maxNetIncome) +
                            "Min Net Income: " + String.valueOf(minNetIncome), Toast.LENGTH_LONG).show();
                            //Log.e("11111111111", String.valueOf(which));
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                CaseDataSource ds = new CaseDataSource(MainActivity.this);
                                ds.open();
                                caseData = ds.getStoreAddressesOnRevenue();
                                ds.close();

                                ListView listView = (ListView) findViewById(R.id.listStore);
                                listView.setAdapter(new StoreAdapter(MainActivity.this, caseData));
                            }
                            catch (Exception e) {
                                e.getCause();
                                Toast.makeText(MainActivity.this, "Error retrieving stores", Toast.LENGTH_LONG).show();
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
                            int minRevenue = getSharedPreferences("BCGCompetitionPreferences", Context.MODE_PRIVATE).
                                    getInt("minrevenue", 2000000);
                            try {
                                CaseDataSource ds = new CaseDataSource(MainActivity.this);
                                ds.open();
                                caseData = ds.getStoreAddressesOnAverageRevenue(maxRevenue, minRevenue);
                                ds.close();

                                ListView listView = (ListView) findViewById(R.id.listStore);
                                listView.setAdapter(new StoreRevenueAdapter(MainActivity.this, caseData));
                            }
                            catch (Exception e) {
                                e.getCause();
                                Toast.makeText(MainActivity.this, "Error retrieving stores", Toast.LENGTH_LONG).show();
                            }
                            Toast.makeText(MainActivity.this, "Max Order Revenue: " + String.valueOf(maxRevenue) +
                                    "Min Order Revenue: " + String.valueOf(minRevenue), Toast.LENGTH_LONG).show();
                            //Log.e("11111111111", String.valueOf(which));
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
    }

    private void initButtonStoreLocation() {
        ImageButton buttonStoreLocation = (ImageButton) findViewById(R.id.buttonStoreLocation);
        buttonStoreLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StoreMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    private List<String> getSpinnerData() {
        List<String> dataList = new ArrayList<String>();
        dataList.add("Net Income");
        dataList.add("Revenue");
        return dataList;
    }
}
