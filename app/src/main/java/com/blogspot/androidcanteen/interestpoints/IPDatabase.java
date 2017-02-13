package com.blogspot.androidcanteen.interestpoints;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paolo on 13/02/2017.
 */

public class IPDatabase extends SQLiteOpenHelper {

    private static IPDatabase instance;

    public static String DATABASE_NAME = "interest_points";
    public String TABLE_NAME = "interest_points_table";


    public String COLUMN_ID = "id";
    public String COLUMN_TITLE = "title";
    public String COLUMN_DESCRIPTION = "description";
    public String COLUMN_LATITUDE = "latitude";
    public String COLUMN_LONGITUDE = "longitude";
    public String COLUMN_NOTIFY = "notify";

    SQLiteDatabase db;

public static IPDatabase getInstance()
{
    if(instance==null)
        instance = new IPDatabase(MainActivity.appCont,DATABASE_NAME,null,1);

    return instance;
}

    public IPDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create table


        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," // cursror 0
                + COLUMN_TITLE + " TEXT,"                           // cursror 1
                + COLUMN_DESCRIPTION + " TEXT, "                          // cursror 2
                + COLUMN_LATITUDE + " TEXT, "                          // cursror 2
                + COLUMN_LONGITUDE + " TEXT, "                          // cursror 2
                + COLUMN_NOTIFY + " TEXT" + ")";                       // cursror 3

        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<InterestPoint> GetAllPoints()
    {
        List<InterestPoint> list = new ArrayList<>();
db = getReadableDatabase();

        Cursor  cursor = db.rawQuery("select * from " + TABLE_NAME,null);

        if (cursor .moveToFirst()) {
            while (cursor.isAfterLast() == false) {

                InterestPoint p = new InterestPoint(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),Boolean.parseBoolean(cursor.getString(5)));
                list.add(p);


                cursor.moveToNext();
            }
        }

        return list;
    }

    public void printAllPoints()
    {
        List<InterestPoint> list = GetAllPoints();

        for(InterestPoint p : list)
            GlobalVariables.LogWithTag(p.title);
    }

    public void printAllTables()
    {
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
               GlobalVariables.LogWithTag(c.getString(0));
                c.moveToNext();
            }
        }
    }

    public void DeleteInterestPointByTitle(String t)
    {
         db = getWritableDatabase();

        db.delete(TABLE_NAME,COLUMN_TITLE + " = ?",new String[]{t});
        db.close();

    }

    public void AddInterestPoint(InterestPoint ip)
    {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE,ip.title);
        cv.put(COLUMN_DESCRIPTION,ip.description);
        cv.put(COLUMN_LATITUDE,ip.lat);
        cv.put(COLUMN_LONGITUDE,ip.lng);
        cv.put(COLUMN_NOTIFY,String.valueOf(ip.notifyWhenClose));

        db = getWritableDatabase();

        db.insert(TABLE_NAME,null,cv);

        GlobalVariables.ToastShort(ip.title + " saved");

        printAllPoints();

    }
}
