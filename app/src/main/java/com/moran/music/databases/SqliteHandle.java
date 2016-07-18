package com.moran.music.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Denis on 01/02/2016.
 */
public class SqliteHandle {
    //commons
    public static final String KEY_ROWID = "_id";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "music";

    //MUSIC info
    private static final String DATABASE_NAME = "AllMusicBD";
    public static final String KEY_NAME = "title";
    public static final String KEY_ARTIST = "artist";
    public static final String KEY_ALBUM = "album";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_URI = "uri";
    public static final String TAG = "tag";
    public static final String KEY_IMAGE = "albumid";
    private static final String DATABASE_CREATE =
            "create table music (_id integer primary key autoincrement, "
                    + "title text not null, artist text not null, duration text not null, uri text not null, album text not null, albumid text not null);";



    //playlist info
    private static final String DATABASE_TABLE_2="playlist";
    private String PLAY_NAME = "ptitle";
    private String PLAY_ARTIST = "partist";
    private String PLAY_ALBUM ="palbum";
    private String PLAY_DURATION="pduration";
    private String PLAY_URI="puri";
    private static final String DATABASE_MAKE =
            "create table playlist (_id integer primary key autoincrement, "
                    + "ptitle text not null, partist text not null, palbum text not null, pduration text not null, puri text not null);";


    //initializaion
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public SqliteHandle(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    //---opens the database---
    public SqliteHandle open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close() {
        DBHelper.close();
    }

    //---insert all values


    public long insertMusic(String title, String artist, String album, int duration, String urid) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, title);
        initialValues.put(KEY_ARTIST, artist);
        initialValues.put(KEY_ALBUM, album);
        initialValues.put(KEY_DURATION, duration);
        initialValues.put(KEY_URI, urid);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public long insertPlay(String title, String artist, String album, int duration, String urid) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(PLAY_NAME, title);
        initialValues.put(PLAY_ARTIST, artist);
        initialValues.put(PLAY_ALBUM, album);
        initialValues.put(PLAY_DURATION, duration);
        initialValues.put(PLAY_URI, urid);
        return db.insert(DATABASE_TABLE_2, null, initialValues);
    }



    //delete specific track
    public boolean deleteTrack(long rowId) {
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }



    //---retrieves all the MUSIC---
    public Cursor getAllMusic() {
        return db.query(DATABASE_TABLE, new String[]{KEY_NAME, KEY_ARTIST, KEY_ALBUM, KEY_DURATION,KEY_URI,KEY_IMAGE}, null, null, null, null, null);
    }
    public Cursor getPlay(){
        return  db.query(DATABASE_TABLE_2, new String[]{PLAY_NAME, PLAY_ARTIST, PLAY_ALBUM, PLAY_DURATION, PLAY_URI},null, null,null,null,null);
    }
    public void clearDB(){
        String clearQuery= "DELETE FROM " + DATABASE_TABLE_2;
        try {
            db.execSQL(clearQuery);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }



    //---retrieves a particular contact---
    public Cursor getTrack(String dude) throws SQLException {
        Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,
                        KEY_NAME, KEY_ARTIST, KEY_ALBUM, KEY_DURATION, KEY_URI}, KEY_ARTIST + "=" + dude, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a contact---
    public boolean updateMusic(long rowId, String title, String artist, String album, String duration, String urid,String albumid) {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, title);
        args.put(KEY_ARTIST, artist);
        args.put(KEY_ALBUM, album);
        args.put(KEY_DURATION, duration);
        args.put(KEY_URI, urid);
        args.put(KEY_IMAGE, albumid);
        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        Context context;

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
                db.execSQL(DATABASE_MAKE);
                //Toast.makeText(context,"database created", Toast.LENGTH_LONG).show();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS music");
            Toast.makeText(context, "database upgraded", Toast.LENGTH_LONG).show();
            onCreate(db);
        }

        public ArrayList AllMusic() {
            ArrayList array = new ArrayList();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery("select * from music", null);
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                array.add(c.getString(c.getColumnIndex(KEY_NAME)));
                c.moveToNext();
            }
            return array;
        }
        public ArrayList AllArtist() {
            ArrayList artist = new ArrayList();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery("select * from music", null);
            c.moveToFirst();
            while (c.isAfterLast() == false) {
                artist.add(c.getString(c.getColumnIndex(KEY_ARTIST)));
                c.moveToNext();
            }
            return artist;
        }

        public Cursor getData(String dud) {
            // TODO Auto-generated method stub
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor c = db.rawQuery("select * from music where artist=" + dud + "", null);
            return c;
        }









    }
}

