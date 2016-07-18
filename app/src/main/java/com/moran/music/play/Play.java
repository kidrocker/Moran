package com.moran.music.play;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

import com.moran.music.R;
import com.moran.music.services.Player;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Kiura on 6/15/2016.
 */
public class Play extends AppCompatActivity {
    private static final String TAG = "play";
    Uri uri1;
    Player musicService;
    ListView lv;
    LinearLayout line_bot;
    private Intent playIntent;
    private boolean musicBound=false;
    private boolean paused= false, playbackPaused=false, playlist_show=false;
    private Toolbar toolbar;

    ImageView albumart;
    Button add, clear, save;
    ImageButton play, stop, next, previous ,shuffle, repeat, playlist, list, close;
    SeekBar seekBar;
    TextView nowplaying, albums, tvnext, artist, tvduration, tvtotal;
    RelativeLayout rela, playview;
    private String[] STAR = { "*" };
    private boolean loopAll= false;
    public boolean kill = true;
    private boolean fade = false;
    Handler handler;


    @Override
    public void onStart() {
        super.onStart();
            updateSeek();

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
        setContentView(R.layout.activity_play);
        musicService = new Player();
        handler = new Handler();
        doBindService();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (musicService.player != null && musicService.isPng()) {
                    updateSeek();
                    handler.removeCallbacks(this);
                    //handler.postDelayed(this,500);
                    Log.w(TAG, "onCreate Runnable");
                    Toast.makeText(Play.this,"Connection Occured",Toast.LENGTH_LONG).show();
                }
            }
        };
        handler.postDelayed(runnable, 500);


        rela  = (RelativeLayout) findViewById(R.id.relView);
        add = (Button) findViewById(R.id.bt_pl_add);
        clear = (Button) findViewById(R.id.bt_pl_clear);
        line_bot = (LinearLayout) findViewById(R.id.line_bot);
        save = (Button) findViewById(R.id.bt_pl_save);
        playview  = (RelativeLayout) findViewById(R.id.rel_playlist);
        playlist = (ImageButton) findViewById(R.id.bt_playlist);
        lv = (ListView) findViewById(R.id.play_list);
        play = (ImageButton) findViewById(R.id.btplay);
        next = (ImageButton) findViewById(R.id.bt_next);
        previous= (ImageButton) findViewById(R.id.btprev);
        seekBar= (SeekBar) findViewById(R.id.seekBar);
        nowplaying = (TextView) findViewById(R.id.tvplaying);
        albumart= (ImageView) findViewById(R.id.imAlbum);


        playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!playlist_show){
                    int trans = rela.getHeight() - 25;
                    rela.animate().translationY(trans);
                    playlist_show =true;
                    playlist.setImageResource(R.mipmap.ic_playlist_up);
                    populateplaylist();
                    playview.setVisibility(View.VISIBLE);
                    line_bot.setVisibility(View.INVISIBLE);
                    //show layout here
                }else {
                    rela.animate().translationY(0);
                    playlist_show=false;
                    playlist.setImageResource(R.mipmap.ic_playlist);
                    playview.setVisibility(View.INVISIBLE);
                    line_bot.setVisibility(View.VISIBLE);
                }
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
                    Toast.makeText(Play.this, "You need to select a song before you play", Toast.LENGTH_LONG).show();
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
                albumart.setImageResource(R.mipmap.avatar);
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



            Play.this.runOnUiThread(new Runnable() {
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
                        albumart.setImageResource(R.mipmap.avatar);
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
        //Toast.makeText(this, musicService.tester(), Toast.LENGTH_SHORT).show();

    }

}
