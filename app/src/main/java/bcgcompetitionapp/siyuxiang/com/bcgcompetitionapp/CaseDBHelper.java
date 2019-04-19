package bcgcompetitionapp.siyuxiang.com.bcgcompetitionapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CaseDBHelper extends SQLiteOpenHelper {

    private Context myContext;
    private String DB_PATH = "/data/data/bcgcompetitionapp.siyuxiang.com.bcgcompetitionapp/databases/";
    private static String DB_NAME = "casedata.db";
    private static String ASSETS_NAME = "casedata.db";
    private SQLiteDatabase myDatabase = null;

    public CaseDBHelper (Context context) {
        super(context, "casedata.db", null, 1);
        this.myContext = context;
    }

    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();

        if (!dbExist) {
            try {
                File dir = new File(DB_PATH);
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File dbf = new File(DB_PATH + DB_NAME);
                if (dbf.exists()) {
                    dbf.delete();
                }
                SQLiteDatabase.openOrCreateDatabase(dbf, null);
                copyDataBase();
            } catch (IOException e) {
                throw new Error("数据库创建失败");
            }
        }
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = null;
        try {
            myInput = myContext.getAssets().open(ASSETS_NAME);
            String outFileName = DB_PATH + DB_NAME;
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        String myPath = DB_PATH + DB_NAME;
        try {
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (SQLiteException e) {

        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    @Override
    public synchronized void close() {
        if (myDatabase != null) {
            myDatabase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
