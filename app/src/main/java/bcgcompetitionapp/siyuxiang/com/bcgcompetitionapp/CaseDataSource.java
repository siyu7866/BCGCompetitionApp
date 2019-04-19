package bcgcompetitionapp.siyuxiang.com.bcgcompetitionapp;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CaseDataSource {

    private SQLiteDatabase database;
    private CaseDBHelper dbHelper;

    public CaseDataSource (Context context) {
        dbHelper = new CaseDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ArrayList<CaseData> getStoreAddresses() {
        ArrayList<CaseData> storeAddresses = new ArrayList<CaseData>();
        Cursor cursor = null;

        try {
            String query = "SELECT DISTINCT(address), city, state, zip FROM competition_fakedata_190417";
            cursor = database.rawQuery(query, null);

            CaseData caseData;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                caseData = new CaseData();
                caseData.setAddress(cursor.getString(0));
                caseData.setCity(cursor.getString(1));
                caseData.setState(cursor.getString(2));
                caseData.setZipCode(cursor.getString(3));
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
}
