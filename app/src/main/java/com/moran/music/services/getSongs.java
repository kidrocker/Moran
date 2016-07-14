package com.moran.music.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.Toast;
import com.moran.music.database.SqliteHandle;

public class getSongs extends AsyncTask<String,Void,String > {
    public boolean firstTim = false;
    public static final String NAME_FIRST = "FIRST_TIME";
    public static final String KEY_FIRST = "FIRST_KEY";
    SharedPreferences prefs = null;
    Uri uri2;

    private  Context context;

        SqliteHandle db;
        public getSongs(final Uri uri2,Context ctx){
                this.uri2=uri2;
            this.context = ctx;
           db =new SqliteHandle(context);
        }
        private String[] STAR = { "*" };

        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";


    @Override
        protected void onPreExecute() {
                super.onPreExecute();
        db.open();
        Cursor c = db.getAllMusic();
        if (c.getCount() != 0 ){
            db.clearAll();
        }


        }



        @Override
        protected String doInBackground(String ...strings) {


                        Cursor cursor =context.getApplicationContext().getContentResolver().query(uri2, STAR, selection, null, null);
                        if (cursor !=null){
                                if (cursor.moveToFirst()){
                                        do {
                                                String urid = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                                                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                                                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                                                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                                                int duration = Integer.parseInt(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)));
                                                //byte[] image = cursor.getBlob(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                                                db.open();
                                                db.insertMusic(title, artist, album, duration, urid);
                                        }while (cursor.moveToNext());
                                }
                                cursor.close();
                                // Toast.makeText(getApplicationContext(), "SCAN COMPLETE", Toast.LENGTH_LONG).show();
                                db.close();

                                /*SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putBoolean("SCANNED", true);
                                editor.apply();
                                */

                        }else{
                                //Toast.makeText(context, "Shit happened", Toast.LENGTH_LONG).show();
                        }



                return null;
        }
        @Override
        protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Toast.makeText(context,"Refresh Done",Toast.LENGTH_LONG).show();

        }
    public static boolean isSdPresent()
    {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }


    public  boolean firstTime(){

        return prefs.getBoolean(KEY_FIRST, true);
    }


}