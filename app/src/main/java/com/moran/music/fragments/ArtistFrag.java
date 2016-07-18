package com.moran.music.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.moran.music.adapters.ArtistAdapter;
import com.moran.music.R;
import com.moran.music.database.SqliteHandle;
import com.moran.music.services.Player;
import com.moran.music.utils.user_model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Denis on 08/03/2016.
 */
public class ArtistFrag extends Fragment {

    RecyclerView rc;
    Player musicSrv;
    ArtistAdapter adapter;
    public boolean musicBound=false;
    List<user_model> users = Collections.EMPTY_LIST;
    ArrayList<String> songName;
    ArrayList<String> artistName;
    ArrayList<String> artistFrag;
    ArrayList<Integer> duration;
    ArrayList<String> uri;
    ArrayList<String> album;
    SqliteHandle.DatabaseHelper dm;
    SqliteHandle db;
    public ArtistFrag() {
        // Required empty public constructor
    }

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBindService();
        musicSrv = new Player();
        dm = new SqliteHandle.DatabaseHelper(getContext());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.artist_frag, container, false);
        doBindService();
        musicQuery();
        musicSrv = new Player();
        rc = (RecyclerView) view.findViewById(R.id.song_list);
        adapter=new ArtistAdapter(getContext(),users);
        rc.setAdapter(adapter);
        rc.setLayoutManager(new LinearLayoutManager(getContext()));
        rc.addOnItemTouchListener(new recyclerTouch(getContext(), rc, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                user_model dat = users.get(position);
                String artist = dat.getArtistName();

                db.open();
               Cursor c= dm.getData(artist);
                String x = c.getString(1);
                Toast.makeText(getContext(),x,Toast.LENGTH_LONG).show();


            }
            @Override
            public void onLongClick(final View view, final int position) {

            }


        }));
        return view;
    }
    private void musicQuery() {
        db = new SqliteHandle(getContext());
        db.open();
        Cursor c = db.getAllMusic();
        String album1, title,artist, uri1,albumid;
        int duration1;
        ArrayList<String> albumID = new ArrayList<>();
        songName = new ArrayList<>();
        artistName = new ArrayList<>();
        artistFrag = new ArrayList<>();
        duration = new ArrayList<>();
        uri = new ArrayList<>();
        album = new ArrayList<>();

        try {


            c.moveToFirst();

            do {
                title  = c.getString(0);
                artist = c.getString(1);
                album1 = c.getString(4);
                duration1 =c.getInt(3);
                uri1 = c.getString(2);
                albumid =c.getString(5);


                if (artistFrag.size()>=1){
                   int v = artistFrag.size();
                    for (int d = 0; v != 0 && d <= v; d++){
                        if (artist.equals(artistFrag.get(d))){
                            artistFrag.add("null");
                        }else {
                            artistFrag.add(artist);
                        }
                    }
                }else{
                    artistFrag.add(artist);
                }
                songName.add(title);
                artistName.add(artist);
                duration.add(duration1);
                album.add(album1);
                uri.add(uri1);
                albumID.add(albumid);


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
        getContext().getApplicationContext().bindService(new Intent(getContext(), Player.class), musicConnection, Context.BIND_AUTO_CREATE);
        musicBound=true;
        //Toast.makeText(this, "Service Local", Toast.LENGTH_SHORT).show();
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
