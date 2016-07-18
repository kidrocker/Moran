package com.moran.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.moran.music.beta.Feedback;
import com.moran.music.database.SqliteHandle;
import com.moran.music.fragments.SongsFrag;
import com.moran.music.local.ViewLocal;
import com.moran.music.services.MusicController;
import com.moran.music.services.Player;
import com.moran.music.utils.Preferences;
import com.moran.music.utils.user_model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    List<user_model> users = Collections.EMPTY_LIST;
    ArrayList<String> songName;
    ArrayList<String> artistName;
    ArrayList<String> duration;
    ArrayList<String> uri;
    ArrayList<String> album;
    SqliteHandle db;
    public static final String TAG = "moran";
    Uri uri1;
    Player musicService;
    private Intent playIntent;
    private boolean musicBound=false;
    private boolean paused= false, playbackPaused=false;
    private Toolbar toolbar;
    private MusicController controller;
    ImageView albumart;
    ImageButton play, stop, next, previous ,shuffle, repeat, playlist, list, close;
    Button clear, add, save;
    SeekBar seekBar;
    TextView nowplaying, albums, tvnext, artist, tvduration, tvtotal;
    RelativeLayout rela ,playview;
    LinearLayout line_bot;
    ListView lv;
    private String[] STAR = { "*" };
    private boolean loopAll= false;
    public boolean kill = true;
    private boolean fade = false, playlist_show=false;
    Handler handler;


    @Override
    public void onStart() {
        super.onStart();
        if (playIntent==null){
            playIntent = new Intent(getApplicationContext(),Player.class);
            startService(playIntent);
            updateSeek();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        doBindService();
        if (paused) {
            updateSeek();
            paused=false;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        paused=true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler= new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
          if (musicService.player !=null && musicService.isPng()){
              updateSeek();
              handler.removeCallbacks(this);
              //handler.postDelayed(this,500);
              Log.w(TAG,"onCreate Runnable");
          }
            }
        };
        handler.postDelayed(runnable, 500);

/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        */



            db = new SqliteHandle(this);
            musicService = new Player();
            handler = new Handler();
            doBindService();
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent q = new Intent(MainActivity.this, Feedback.class);
                    startActivity(q);
                }
            });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);



            play = (ImageButton) findViewById(R.id.btplay);
       // close = (ImageButton) findViewById(R.id.bt_close);
            stop = (ImageButton) findViewById(R.id.bt_stop);
            next = (ImageButton) findViewById(R.id.bt_next);
            previous = (ImageButton) findViewById(R.id.btprev);
            shuffle = (ImageButton) findViewById(R.id.bt_shuffle);
            repeat = (ImageButton) findViewById(R.id.bt_loop);
            playlist = (ImageButton) findViewById(R.id.bt_playlist);
        add = (Button) findViewById(R.id.bt_pl_add);
        clear = (Button) findViewById(R.id.bt_pl_clear);
        save = (Button) findViewById(R.id.bt_pl_save);
        list = (ImageButton) findViewById(R.id.bt_list);

            seekBar = (SeekBar) findViewById(R.id.seekBar);

            nowplaying = (TextView) findViewById(R.id.tvplaying);
            tvduration = (TextView) findViewById(R.id.tvduration);
            tvtotal = (TextView) findViewById(R.id.tvtotal);
            artist = (TextView) findViewById(R.id.tvartist);
            tvnext = (TextView) findViewById(R.id.tvnext);
            //albums = (TextView) findViewById(R.id.tvalbum);


            rela = (RelativeLayout) findViewById(R.id.relView);
        playview = (RelativeLayout) findViewById(R.id.rel_playlist);
        line_bot = (LinearLayout) findViewById(R.id.line_bot);
        lv = (ListView) findViewById(R.id.play_list);
            albumart = (ImageView) findViewById(R.id.imAlbum);


            //rela.setVisibility(View.INVISIBLE);
            list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, SongsFrag.class);
                    startActivity(i);

                }
            });

        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playlist_show){

                    int trans = rela.getHeight() - 25;
                    rela.animate().translationY(trans);
                    playlist_show =true;
                    playlist.setImageResource(R.mipmap.ic_up);
                    populateplaylist();
                    playview.setVisibility(View.VISIBLE);
                    line_bot.setVisibility(View.INVISIBLE);
                    //show layout here
                }else {
                    rela.animate().translationY(0);
                    playlist_show=false;
                    playlist.setImageResource(R.mipmap.ic_drop);
                    playview.setVisibility(View.INVISIBLE);
                    line_bot.setVisibility(View.VISIBLE);
                }
            }
        });
            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicService.setShuffle();
                }
            });

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (musicService.isPng()) {
                        musicService.pausePlayer();
                        play.setImageResource(R.mipmap.ic_play);
                    } else if (musicService.checkPlay() && !musicService.isPng()) {
                        musicService.resume();
                        play.setImageResource(R.mipmap.ic_pause);

                    } else {
                        Toast.makeText(MainActivity.this, "Jump over to List and play a song first", Toast.LENGTH_LONG).show();
                    }

                }
            });
            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicService.stopper();
                    kill = false;
                    db.open();
                    db.clearDB();
                    db.close();

                }
            });
            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicService.setLoop();
                    if (!musicService.loopAll && !musicService.isLoop()){
                        repeat.setImageResource(R.mipmap.ic_loop);
                    }else if (musicService.loopAll && !musicService.isLoop()){
                        repeat.setImageResource(R.mipmap.ic_loop_single);

                    }else if (!musicService.loopAll && musicService.isLoop()){
                        repeat.setImageResource(R.mipmap.ic_loop_one);
                    }


                }
            });
            previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicService.prevSong();

                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    musicService.nextSong();

                }
            });
            if (musicService.player != null) {
                updateSeek();

            }


        rela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rela.getVisibility()==View.VISIBLE) {
                    Animation out = AnimationUtils.makeOutAnimation(MainActivity.this,true);
                    rela.startAnimation(out);
                    rela.setVisibility(View.INVISIBLE);
                }
            }
        });
        albumart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rela.getVisibility()==View.INVISIBLE) {


                    Animation in = AnimationUtils.makeInAnimation(MainActivity.this, true);
                    rela.startAnimation(in);
                    rela.setVisibility(View.VISIBLE);
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int proVal = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                proVal = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (musicService.isPng()) {
                    musicService.setProgress(proVal);
                }
            }
        });

    }

    private void populateplaylist() {
        ArrayList songName = musicService.returnArray();

        if (musicService.returnArray() == null){
            lv.setVisibility(View.INVISIBLE);
            clear.setEnabled(false);
            save.setEnabled(false);
            TextView alert = new TextView(this);
            alert.setText("Sorry It Seems no song is added on the playlist.\n Tap 'Add' and add a song or navigate to library");
            alert.setTextColor(R.color.buttonDecline);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            alert.setLayoutParams(lp);
            alert.setTextSize(20);
            playview.addView(alert);
        }else {
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songName);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    musicService.setPosition(position);
                    musicService.playSong();


                }
            });
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    doPopUp();

                    return true;
                }
            });

        }
    }
    private void doPopUp() {
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options,int rewWidth, int reqHeigth
    ){
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if(height >reqHeigth || width >rewWidth){
            final int halfHeight = height/2;
            final int halfWidht = width/2;
            while((halfHeight/inSampleSize) >reqHeigth && (halfWidht/inSampleSize) >rewWidth){
                inSampleSize *=2;
            }
        }
        return inSampleSize;
    }




    public void updateArt(){
        if (musicService.isPng()) {
            String uripath = musicService.returnUri();
            Bitmap image;
            MediaMetadataRetriever mdata = new MediaMetadataRetriever();
            mdata.setDataSource(uripath);
            try {
                byte art[] = mdata.getEmbeddedPicture();
                image = BitmapFactory.decodeByteArray(art, 0, art.length);
            } catch (Exception e) {
                image = null;
            }
            if (image == null) {
                albumart.setImageResource(R.mipmap.ic_small);
            } else {
                Bitmap resized = Bitmap.createScaledBitmap(image,300,450,true);
                albumart.setImageBitmap(resized);
                albumart.setAdjustViewBounds(true);
                albumart.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        }
}

    public void updateSeek() {

        if (musicService.player !=null && musicService.isPng()){

            final int duration = musicService.getDurr();
            play.setImageResource(R.mipmap.ic_pause);
            updateArt();


            seekBar.setIndeterminate(false);
            seekBar.setMax(duration);
            String hmd = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(duration),
                    TimeUnit.MILLISECONDS.toMinutes(duration)-
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                    TimeUnit.MILLISECONDS.toSeconds(duration)-
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));

            tvtotal.setText(hmd);



            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int current = musicService.getSongPosn();
                    if (!musicService.playing && !kill) {
                        tvduration.setText(" ");
                        nowplaying.setText(" ");
                        artist.setText(" ");
                        tvnext.setText(" ");
                        tvtotal.setText(" ");
                        seekBar.setProgress(0);
                        play.setImageResource(R.mipmap.ic_play);
                        handler.removeCallbacks(this);
                    } else if (musicService.endroad) {
                        tvduration.setText(" ");
                        nowplaying.setText(" ");
                        artist.setText(" ");
                        tvnext.setText(" ");
                        tvtotal.setText(" ");
                        seekBar.setProgress(0);
                        play.setImageResource(R.mipmap.ic_play);
                        albumart.setImageResource(R.mipmap.ic_small);
                        handler.removeCallbacks(this);
                    } else {

                        seekBar.setProgress(current);
                        String hms = String.format("%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(current),
                                TimeUnit.MILLISECONDS.toMinutes(current) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(current)),
                                TimeUnit.MILLISECONDS.toSeconds(current) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(current)));


                        tvduration.setText(hms);
                        nowplaying.setText(musicService.returnTitle());
                        String checker =musicService.returnArtist();
                        if(checker.equals("<unknown>")){
                            artist.setText("Unknown Artist");
                        }else {artist.setText(checker);
                        }
                        tvnext.setText("Next up: " + musicService.returnNext());
                        if (current > 1000) {
                            updateArt();
                        }
                        Log.e("moran","update time");
                        handler.postDelayed(this, 1000);
                    }

                }
            });

        }
    }

    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService= ((Player.MusicBinder)service).getService();
            musicBound = true;


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
            musicService=null;

        }
    };
    private void doBindService(){
        getApplicationContext().bindService(new Intent(this, Player.class), musicConnection, Context.BIND_AUTO_CREATE);
        musicBound=true;
        //Toast.makeText(this, "Service main", Toast.LENGTH_SHORT).show();

    }




    public static boolean isSdPresent()
    {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }
    public void scanAll(String comm){

        uri1 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;




    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(MainActivity.this,Preferences.class);
            startActivity(i);
            return true;
        }
        if (id == R.id.action_playall) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_prof) {
            Toast.makeText(this, "This is not available in this build. We are working on it. Thanks for patience", Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_online) {
            Toast.makeText(this, "This is not available in this build. We are working on it. Thanks for patience", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_prof) {
            Toast.makeText(this, "This is not available in this build. We are working on it. Thanks for patience", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_lib) {
            Intent i = new Intent(MainActivity.this, ViewLocal.class);
            startActivity(i);

        } else if (id == R.id.nav_share) {
            Toast.makeText(this, "Your feedback matters. Drop some", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_send) {
            Toast.makeText(this, "Sharing is caring.Tell a friend", Toast.LENGTH_LONG).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
