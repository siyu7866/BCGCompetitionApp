package bcgcompetitionapp.siyuxiang.com.bcgcompetitionapp;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class CaseDataSource {

    private SQLiteDatabase database;
    private CaseDBHelper dbHelper;

    public CaseDataSource (Context context) {
        dbHelper = new CaseDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        Log.e("CaseDataSource","database= "+database);
    }

    public void close() {
        dbHelper.close();
    }

    public ArrayList<CaseData> getStoreAddresses() {
        ArrayList<CaseData> storeAddresses = new ArrayList<CaseData>();
        Cursor cursor = null;

        try {
            String query = "SELECT DISTINCT(address), city, state, zip, [net income] FROM competition_fakedata_042519_1";

            cursor = database.rawQuery(query, null);
            Log.e("CaseDataSource","cursor= "+cursor);
            CaseData caseData;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                caseData = new CaseData();
                caseData.setAddress(cursor.getString(0));
                caseData.setCity(cursor.getString(1));
                caseData.setState(cursor.getString(2));
                caseData.setZipCode(cursor.getString(3));
                caseData.setNetIncome(cursor.getInt(4));
                storeAddresses.add(caseData);
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e) {
            cursor.close();
        }
        return storeAddresses;
    }

    public ArrayList<CaseData> getStoreAddressesOnNetIncome(int maxNetIncome, int minNetIncome) {
        ArrayList<CaseData> storeAddresses = new ArrayList<CaseData>();
        Cursor cursor = null;

        try {
            String query = "SELECT address, city, state, zip, AVG([net income]) FROM competition_fakedata_042519_1 GROUP BY address";
            cursor = database.rawQuery(query, null);
            //CaseData caseData = new CaseData();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                CaseData caseData = new CaseData();
                caseData.setNetIncome(cursor.getInt(4));
                if (caseData.getNetIncome() < maxNetIncome && caseData.getNetIncome() > minNetIncome) {
                    caseData.setAddress(cursor.getString(0));
                    caseData.setCity(cursor.getString(1));
                    caseData.setState(cursor.getString(2));
                    caseData.setZipCode(cursor.getString(3));
                    storeAddresses.add(caseData);
                    cursor.moveToNext();
                } else {
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        catch (Exception e) {
            cursor.close();
        }
        return storeAddresses;
    }

    public ArrayList<CaseData> getStoreAddressesOnRevenue() {
        ArrayList<CaseData> storeAddresses = new ArrayList<CaseData>();
        Cursor cursor = null;

        try {
            String query = "SELECT address, city, state, zip, AVG([Order revenue]) FROM competition_fakedata_042519_1 GROUP BY address";
            cursor = database.rawQuery(query, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                CaseData caseData = new CaseData();
                //caseData.setOrderRevenue(cursor.getInt(4));
                caseData.setAddress(cursor.getString(0));
                caseData.setCity(cursor.getString(1));
                caseData.setState(cursor.getString(2));
                caseData.setZipCode(cursor.getString(3));
                caseData.setOrderRevenue(cursor.getInt(4));
                //String string = String.valueOf(caseData.getOrderRevenue());
                storeAddresses.add(caseData);
                cursor.moveToNext();
            }
            cursor.close();
        }
        catch (Exception e) {
            cursor.close();
        }
        return storeAddresses;
    }

    public ArrayList<CaseData> getStoreAddressesOnAverageRevenue(int maxRevenue, int minRevenue){
        ArrayList<CaseData> storeAddresses = new ArrayList<CaseData>();
        Cursor cursor = null;

        try {
            String query = "SELECT address, city, state, zip, AVG([Order Revenue]) FROM competition_fakedata_042519_1 GROUP BY address";
            cursor = database.rawQuery(query, null);
            //CaseData caseData = new CaseData();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                CaseData caseData = new CaseData();
                caseData.setOrderRevenue(cursor.getInt(4));
                if (caseData.getOrderRevenue() < maxRevenue && caseData.getOrderRevenue() > minRevenue) {
                    caseData.setAddress(cursor.getString(0));
                    caseData.setCity(cursor.getString(1));
                    caseData.setState(cursor.getString(2));
                    caseData.setZipCode(cursor.getString(3));
                    storeAddresses.add(caseData);
                    cursor.moveToNext();
                } else {
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        catch (Exception e) {
            cursor.close();
        }
        return storeAddresses;
    }
}
