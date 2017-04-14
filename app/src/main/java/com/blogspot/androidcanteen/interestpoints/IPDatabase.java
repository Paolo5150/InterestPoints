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
    public String COLUMN_LINK = "link";
    public String COLUMN_TITLE = "title";
    public String COLUMN_ADDRESS = "address";
    public String COLUMN_DESCRIPTION = "description";
    public String COLUMN_LATITUDE = "latitude";
    public String COLUMN_LONGITUDE = "longitude";
    public String COLUMN_NOTIFY = "notify";

    SQLiteDatabase db;

    public ArrayList<IDatabaseListener> listeners;

public static IPDatabase getInstance()
{
    if(instance==null)
        instance = new IPDatabase(MainActivity.appCont,DATABASE_NAME,null,1);

    return instance;
}

    public IPDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

        listeners = new ArrayList<>();
        db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Create table


        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + COLUMN_ID + " TEXT PRIMARY KEY," // cursror 0
                + COLUMN_TITLE + " TEXT,"                           // cursror 1
                + COLUMN_ADDRESS + " TEXT,"                           // cursror 1
                + COLUMN_DESCRIPTION + " TEXT, "                          // cursror 2
                + COLUMN_LATITUDE + " TEXT, "                          // cursror 2
                + COLUMN_LONGITUDE + " TEXT, "                          // cursror 2
                + COLUMN_NOTIFY + " TEXT" + ")";                       // cursror 3

        db.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        GlobalVariables.LogWithTag("Upgrade called");
    }

    private void NotifyAllListeners(IDatabaseListener.DATABASE_OPERATION operation, int itemPos)
    {

        int iii = 0;
        for(IDatabaseListener lis : listeners) {
            iii++;
            lis.OnDatabaseChange(operation,itemPos);
        }

        GlobalVariables.LogWithTag("Num of listeners " + iii);
    }

    public boolean ReplacePointBoolean(String id,boolean value)
    {
        db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        GlobalVariables.LogWithTag("Value passed is " + value);


        cv.put(COLUMN_NOTIFY, String.valueOf(value));



       boolean ret =  db.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[]{id}) > 0;

        InterestPoint p = getPointById(id);
        int index = GetIndexOfItem(p.title);
        NotifyAllListeners(IDatabaseListener.DATABASE_OPERATION.EDIT_BOOL,index);
        return ret;
        //  db.replace(TABLE_NAME,null,cv);
        //db.close();

      /*  InterestPoint p = getPointById(id);

        if(p!=null)
        {
            GlobalVariables.LogWithTag(p.title + " boolean in database is " + p.notifyWhenClose);
        }*/



    }

    public int GetIndexOfItem(String title)
    {
        List<InterestPoint> points = GetAllPoints();

        for(int i=0; i<points.size();i++)
        {
            if(points.get(i).title.equalsIgnoreCase(title))
                return i;
        }

        return -1;
    }

    public boolean ReplacePointDescription(String id,String value, int viewPosition)
    {
        db = getWritableDatabase();

        ContentValues cv = new ContentValues();
     //   GlobalVariables.LogWithTag("Value passed is " + value);


        cv.put(COLUMN_DESCRIPTION, String.valueOf(value));

        boolean ret =   db.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[]{id}) > 0;


       NotifyAllListeners(IDatabaseListener.DATABASE_OPERATION.EDIT_DESC,viewPosition);

        return ret;


    }

    public List<InterestPoint> GetAllPoints()
    {


        List<InterestPoint> list = new ArrayList<>();
db = getReadableDatabase();

        Cursor  cursor = db.rawQuery("select * from " + TABLE_NAME,null);

        if (cursor .moveToFirst()) {
            while (cursor.isAfterLast() == false) {

                InterestPoint p = new InterestPoint(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),Boolean.parseBoolean(cursor.getString(6)));
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
            GlobalVariables.LogWithTag(p.toString());
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



    public void DeleteInterestPointByTitle(String title, int viewPosition)
    {
        InterestPoint p = getPointByTitle(title);
        db = getWritableDatabase();

        db.delete(TABLE_NAME,COLUMN_TITLE + " = ?",new String[]{title});
        db.close();

        GlobalVariables.ToastShort(p.title + " deleted");

        NotifyAllListeners(IDatabaseListener.DATABASE_OPERATION.DELETE,viewPosition);

    }

    public void AddListener(IDatabaseListener listener)
    {
        listeners.add(listener);
    }

    public InterestPoint getPointByTitle(String title)
    {
        List<InterestPoint> allPoints = GetAllPoints();

        for(InterestPoint p : allPoints)
        {
            if(p.title.equalsIgnoreCase(title))
                return p;
        }

        return null;
    }

    public InterestPoint getPointById(String id)
    {
        List<InterestPoint> allPoints = GetAllPoints();
    InterestPoint point = null;
        for(InterestPoint p : allPoints)
        {
            if(p.id.equalsIgnoreCase(id)) {
                point = p;

            }
        }


        return point;
    }

    public void AddInterestPoint(InterestPoint ip)
    {
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE,ip.title);
        cv.put(COLUMN_ID,ip.id);
        cv.put(COLUMN_DESCRIPTION,ip.description);
        cv.put(COLUMN_LATITUDE,ip.lat);
        cv.put(COLUMN_LONGITUDE,ip.lng);
        cv.put(COLUMN_ADDRESS,ip.address);
        cv.put(COLUMN_NOTIFY,String.valueOf(ip.notifyWhenClose));

        db = getWritableDatabase();

        db.insert(TABLE_NAME,null,cv);

        GlobalVariables.ToastShort(ip.title + " saved");

      //  printAllPoints();

        NotifyAllListeners(IDatabaseListener.DATABASE_OPERATION.ADD,-1);

    }
}
