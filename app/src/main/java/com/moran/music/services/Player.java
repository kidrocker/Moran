package com.moran.music.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.moran.music.MainActivity;
import com.moran.music.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;


/**
 * Created by Denis on 01/02/2016.
 */
public class Player extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener{

    Intent i;
    private IBinder musicBind=new MusicBinder();
    public MediaPlayer player;
    private MainActivity main;
    private ArrayList<String> uri;
    private ArrayList<String> tittle;
    private ArrayList<String> artist;
    private ArrayList<String> albumid;
    private int position=0;
    private boolean shuffle = false;
    public boolean endroad = false;
    Random random;
    public boolean playing = false;
    Handler handler;
    public boolean loopAll=false;
    NotificationManager nmm;
    public static final String ACTION_PLAY = "play";
    public static final String ACTION_PAUSE = "pause";
    public static final String ACTION_NEXT = "next";
    public static final String ACTION_PREV = "prev";
    public static final String ACTION_STOP = "stop";
    Context ctx;


    @Override
    public void onCreate(){
        super.onCreate();
        player= new MediaPlayer();
        main = new MainActivity();
        initMusic();
        i=new Intent();
        uri = new ArrayList<>();
        tittle= new ArrayList<>();
        artist= new ArrayList<>();
        random = new Random();

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
       /* String huh = intent.getBundleExtra("x")
        if (!huh.equals(null)) {
            if (huh.equals(ACTION_PREV)) {
                prevSong();
            } else if (huh.equals(ACTION_PLAY)) {
                if (checkPlay()) {
                    pausePlayer();
                } else {
                    resume();
                }
            } else if (huh.equals(ACTION_NEXT)) {
                nextSong();
            } else if (huh.equals(ACTION_STOP)) {
                stopper();
            }
        }*/
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }
    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }

    public void addAlot(ArrayList<String> send,ArrayList<String> title,ArrayList<String> artist1) {
        uri.addAll(send);
        tittle.addAll(title);
        artist.addAll(artist1);
    }
    public String returnTitle(){
        if (playing) {
            return tittle.get(position);
        }
        return null;
    }
    public String returnUri(){
        if (playing) {
            return uri.get(position);
        }
        return null;
    }
    public ArrayList returnArray(){
        if (playing) {
            return tittle;
        }
        return null;
    }
    public String returnArtist(){
        if (playing) {
            return artist.get(position);
        }return null;
    }
    public String returnNext(){
        int nexter = position;
        String next = "";
        if (playing) {

            int gets = tittle.size();
            if (gets == 1) {
                nexter = 0;
            } else if (gets > 1 && nexter != gets - 1) {
                nexter++;

            } else if (nexter == gets) {
                nexter = 0;
            }
            next= tittle.get(nexter);
        }
        return next;
    }


    //binder class
    public class MusicBinder extends Binder {
        public Player getService(){
            return Player.this;
        }
    }

    private void initMusic() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }
    public void resetArray(){
        uri.clear();
        tittle.clear();
        artist.clear();
        position=0;
    }
public void addSong(String urid, String titled, String artistd){

    uri.add(urid);
    tittle.add(titled);
    artist.add(artistd);


}

    public void playSong(){
        playing=false;

        if (shuffle && uri.size()>2){
            position = random.nextInt(uri.size());
        }
        player.reset();
        String urix = uri.get(position);
            try {
                player.setDataSource(getApplicationContext(), Uri.parse(urix));
            } catch (IOException e) {
                e.printStackTrace();
            }
        player.prepareAsync();

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playing=false;
            playNext();
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.JELLY_BEAN) {
            nmm.cancel(0);
            nmm.cancelAll();


        }
    }


    private void playNext() {
        position++;
        int ux = uri.size();
        //int ui = ux --;

        if (position >= ux && !player.isLooping() && loopAll){
            position=0;
            playSong();
            //player.reset();
        }else if (position < ux  && !player.isLooping() && loopAll){
            playSong();
        }else if ( player.isLooping()){
            position--;
            playSong();
        }else  {
            //player.release();
            endroad= true;
        }

    }
    public void Notify(){
        handler= new Handler();
        String uripath =uri.get(position);
        Bitmap image;
        MediaMetadataRetriever mdata = new MediaMetadataRetriever();
        mdata.setDataSource(uripath);
        try {
            byte art[] = mdata.getEmbeddedPicture();
            image = BitmapFactory.decodeByteArray(art, 0, art.length);
        } catch (Exception e) {
            image = null;
        }
        final int duration = player.getDuration();

        final String hmk = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(duration),
                TimeUnit.MILLISECONDS.toMinutes(duration) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration)),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.JELLY_BEAN) {



            final NotificationCompat.Builder mbuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(this);
                            if(image!=null) {
                                mbuilder.setLargeIcon(image);
                                    }
            Intent x = new Intent(this,Player.class);
            if (player.isPlaying()){
                x.setAction("prev");
            }
            PendingIntent prevPendingIntent =PendingIntent.getService(this, 0, x, 0);
            Intent y = new Intent(this,Player.class);
            if (player.isPlaying()){
                y.setAction("play");
            }
            PendingIntent listPendingIntent = null;
            PendingIntent playPendingIntent =PendingIntent.getService(this,0,x,0);
            Intent z = new Intent(this,Player.class);
            if (player.isPlaying()){
                x.setAction("next");
            }
            PendingIntent nextPendingIntent =PendingIntent.getService(this, 0, x, 0);
                                mbuilder.setSmallIcon(R.mipmap.ic_play)
                                        .setVisibility(Notification.VISIBILITY_PUBLIC)
                                        .addAction(R.mipmap.ic_playlist_alt,"",listPendingIntent)
                                        //.addAction(R.mipmap.ic_previous, " ", prevPendingIntent)
                                        .addAction(R.mipmap.ic_pause, " ", playPendingIntent)
                                        .addAction(R.mipmap.ic_next_alt," ",nextPendingIntent)
                                        .setOngoing(true);
            Intent i = new Intent(this, MainActivity.class);
            TaskStackBuilder st = TaskStackBuilder.create(this);
            st.addParentStack(MainActivity.class);
            st.addNextIntent(i);
            PendingIntent rpi = st.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mbuilder.setContentIntent(rpi);
            nmm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    int duration1 = player.getCurrentPosition();
                    if (duration == duration1){
                        nmm.cancel(0);
                        handler.removeCallbacks(this);

                    }else if(!playing) {
                        handler.removeCallbacks(this);
                    }else {

                        String hmd = String.format("%02d:%02d:%02d",
                                TimeUnit.MILLISECONDS.toHours(duration1),
                                TimeUnit.MILLISECONDS.toMinutes(duration1) -
                                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration1)),
                                TimeUnit.MILLISECONDS.toSeconds(duration1) -
                                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration1)));
                        mbuilder.setContentTitle(returnTitle())
                                .setTicker(returnTitle())

                                .setContentText(returnArtist() + "   " + hmd + " / " + hmk)
                                .setProgress(player.getDuration(), player.getCurrentPosition(), false);
                        nmm.notify(0, mbuilder.build());
                    }

                    if (playing) {
                        handler.postDelayed(this, 1000);
                    }else{
                        handler.removeCallbacks(this);
                    }
                    Log.e("dude","Player runnable");
                }
            };
            handler.postDelayed(runnable,1000);

        }else {

        }
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Notify();
        playing=true;
        endroad=false;


    }
    public void setPosition(int position1) {
        position = position1;
    }
    public void setProgress(int progress){
        player.seekTo(progress);
    }

    public boolean isPng() {
        return player.isPlaying();
    }
    public void setLoop(){

        if (!loopAll && !isLoop()){
            loopAll = true;

        }else if (loopAll && !isLoop()){
            loopAll = false;
            player.setLooping(true);

        }else if (!loopAll && isLoop()){
            player.setLooping(false);

        }else if (loopAll && isLoop()){
            player.setLooping(false);
            loopAll = false;

        }
    }
    public boolean isLoop(){
        return player.isLooping();
    }
    public void setShuffle(){
        if (shuffle)shuffle=false;
        else shuffle=true;
    }
    public void pausePlayer(){
        player.pause();
    }
    public void resume(){
        player.start();
    }
    public boolean checkPlay(){
        return playing;
    }
    public int getDurr(){
        return player.getDuration();
    }
    public int getSongPosn(){
        return player.getCurrentPosition();
    }
    public void seekupdate(int progress){
        player.seekTo(progress);
    }
    public void stopper(){
        player.reset();
        resetArray();
        playing =false;
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.JELLY_BEAN) {
            nmm.cancel(0);
            nmm.cancelAll();


        }
    }
    public void nextSong(){
        int ud = uri.size();
        int xu = position;
        if (playing && uri.size()>1 && ud != xu+1){
            position++;
            playSong();
        }else if (playing && ud ==1){
            Toast.makeText(this, "Only song in the list", Toast.LENGTH_SHORT).show();

        }else if (playing && ud == xu++ ){
            Toast.makeText(this,"Last Song on the list",Toast.LENGTH_SHORT).show();

        }

    }
    public void prevSong(){
        if (player.isPlaying() && position > 0){
            position--;
            playSong();
        }else if (player.isPlaying() && position ==0){
            playSong();
        }else {

        }
    }
    public void seeker(int psn){
        player.seekTo(psn);
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        Log.d("MUSIC SERVICE", "Shit happened here dude");
        return false;
    }

}
