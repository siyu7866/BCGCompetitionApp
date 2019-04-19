package bcgcompetitionapp.siyuxiang.com.bcgcompetitionapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private String DB_PATH = "/data/data/bcgcompetitionapp.siyuxiang.com.bcgcompetitionapp/databases/";
    private static String DB_NAME = "casedata.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CaseDBHelper helper = new CaseDBHelper(this);
        try {
            helper.createDataBase();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        SQLiteDatabase database = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        String query = "SELECT DISTINCT(address) FROM competition_fakedata_190417";
        Cursor cursor = database.rawQuery(query, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndex("address"));
            Log.i("1111111111", address);
        }
    }
}
