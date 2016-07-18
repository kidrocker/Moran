package com.moran.music.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.moran.music.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * Created by Denis on 01/02/2016.
 */
public class Player extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener,MediaPlayer.OnErrorListener{

    Intent i;
    private IBinder musicBind=new MusicBinder();
    public MediaPlayer player;
    private ArrayList<String> uri;
    private ArrayList<String> tittle;
    private ArrayList<String> artist;
    private int position=0;
    private boolean shuffle = false;
    public boolean endroad = false;
    Random random;
    public boolean playing = false;

    public boolean loopAll=false;



    @Override
    public void onCreate(){
        super.onCreate();
        player= new MediaPlayer();
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
        if (playing) {
            uri.clear();
            tittle.clear();
            artist.clear();
            position = 0;
        }
    }
    public ArrayList returnArray(){
        if (playing) {
            return tittle;
        }
        return null;
    }
    public  String tester(){

        String dude = " dude";
        return dude;
    }
public void addSong(String urid, String titled, String artistd){
    uri.add(urid);
    tittle.add(titled);
    artist.add(artistd);


}

    public void playSong(){
        playing=false;


        player.reset();
        String urix = uri.get(position);
            try {
                player.setDataSource(this, Uri.parse(urix));
            } catch (IOException e) {
                e.printStackTrace();
            }
        player.prepareAsync();

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playing=false;
            playNext();



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



    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        playing=true;
        endroad=false;


    }
    public void setPosition(int position1) {
        position = position1;
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
        Collections.shuffle(uri);
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
