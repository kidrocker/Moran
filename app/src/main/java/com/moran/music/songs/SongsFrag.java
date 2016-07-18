package com.moran.music.songs;

        import android.app.Activity;
        import android.content.ComponentName;
        import android.content.Context;
        import android.content.Intent;
        import android.content.ServiceConnection;
        import android.database.Cursor;
        import android.os.Bundle;
        import android.os.IBinder;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.view.GestureDetector;
        import android.view.MotionEvent;
        import android.view.View;
        import android.widget.Toast;

        import com.moran.music.R;
        import com.moran.music.database.SqliteHandle;
        import com.moran.music.services.Player;
        import com.moran.music.utils.user_model;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.List;

/**
 * Created by Denis on 01/02/2016.
 */
public class SongsFrag extends Activity {
    RecyclerView rc;
    Player musicSrv;
    LocalAdapter adapter;
    public boolean musicBound=false;
    List<user_model> users = Collections.EMPTY_LIST;
    ArrayList<String> songName;
    ArrayList<String> artistName;
    ArrayList<Integer> duration;
    ArrayList<String> uri;
    ArrayList<String> album;
    SqliteHandle db;

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicSrv = ((Player.MusicBinder)service).getService();
            musicBound = true;


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
            musicSrv=null;

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_local);
        rc =(RecyclerView)findViewById( R.id.song_list);
        doBindService();
        musicSrv = new Player();
        musicQuery();
        rc = (RecyclerView) findViewById(R.id.song_list);
        adapter=new LocalAdapter(this,users);
        rc.setAdapter(adapter);
        rc.setLayoutManager(new LinearLayoutManager(this));
        rc.addOnItemTouchListener(new recyclerTouch(this, rc, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                user_model dat = users.get(position);
                db.open();
                String artist1 = dat.getArtistName();
                String title1 = dat.getSongName();
                String album2 = dat.getAlbum();
                String uri3 = dat.getUri();
                int duration1 = dat.getDuration();



                // musicSrv.setSong(uri3);
                //musicSrv.playSong();

                db.clearDB();
                db.insertPlay(title1, artist1, album2, duration1,uri3 );
                db.close();


            }

            @Override
            public void onLongClick(View view, int position) {

                String pos = String.valueOf(position);
                user_model dat = users.get(position);
                db.open();
                String artist1 = dat.getArtistName();
                String title1 = dat.getSongName();
                String album2 = dat.getAlbum();
                String uri3 = dat.getUri();
                int duration1 = dat.getDuration();

                db.insertPlay(title1, artist1, album2, duration1, uri3);
                db.close();
            }
        }));
    }

    private void musicQuery() {
        db = new SqliteHandle(this);
        db.open();
        Cursor c = db.getAllMusic();
        String album1, title,artist, uri1;
        int duration1;
        songName = new ArrayList<>();
        artistName = new ArrayList<>();
        duration = new ArrayList<>();
        uri = new ArrayList<>();
        album = new ArrayList<>();
        try {


            c.moveToFirst();

            do {
                title  = c.getString(0);
                artist = c.getString(1);
                album1 = c.getString(4);
                duration1 = Integer.parseInt(c.getString(3));
                uri1 = c.getString(2);


                //String date = c.getString(3);
                //Toast.makeText(getContext(),duration1,Toast.LENGTH_SHORT).show();
                songName.add(title);
                artistName.add(artist);
                duration.add(duration1);
                album.add(album1);
                uri.add(uri1);


            } while (c.moveToNext());
            c.close();
            db.close();
            users = new ArrayList<>();
            for (int i = 0; i < songName.size(); i++) {
                users.add(new user_model(songName.get(i), artistName.get(i), duration.get(i),album.get(i),uri.get(i)));
            }

        } catch (Exception e) {
            e.printStackTrace();



        }
    }

    private void doBindService(){
        getApplicationContext().bindService(new Intent(this, Player.class), musicConnection, Context.BIND_AUTO_CREATE);
        musicBound=true;
        Toast.makeText(this, "Service Bound FRAG 3", Toast.LENGTH_LONG).show();
    }


    class recyclerTouch implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public recyclerTouch(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
            this.clickListener =clickListener;
            gestureDetector =new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e){

                    return true;
                }
                @Override
                public void onLongPress(MotionEvent e){
                    View chid = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (chid !=null && clickListener !=null){
                        clickListener.onLongClick(chid, recyclerView.getChildPosition(chid));
                    }
                }

            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View chid = rv.findChildViewUnder(e.getX(), e.getY());
            if (chid !=null&& clickListener !=null && gestureDetector.onTouchEvent(e)){
                clickListener.onClick(chid, rv.getChildPosition(chid));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    public static interface ClickListener{
        public void onClick(View view, int position);
        public void onLongClick(View view, int position);
    }
}
