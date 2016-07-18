package com.moran.music.utils;

/**
 * Created by Denis on 01/02/2016.
 */
public class user_model {

    private String songName, artistName, uri, album, artistfrag;
    private int duration;


    public user_model(String songName, String artistName, int duration, String uri, String album) {
        this.songName = songName;
        this.artistName = artistName;
        this.duration = duration;
        this.album = album;
        this.uri = uri;
        this.artistfrag = artistfrag;
    }

public String getArtistfrag(){
    return artistfrag;
}
    public String getSongName() {
        return songName;
    }
    public int getDuration(){
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public String getAlbum(){
        return album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }


    public String getArtistName(){
        return artistName;
    }

    public String getUri(){
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
    public void setArtistfrag(String artistfrag){
        this.artistfrag = artistfrag;
    }







    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }


    public void setSongName(String songName) {
        this.songName = songName;
    }


}

