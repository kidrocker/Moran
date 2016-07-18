package com.moran.music.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * Created by Kiura on 11/07/2016.
 */
public class SongsManager {
    final String MEDIA_PATH = new String("/sdcard/");
    Context context;
    public SongsManager(){

    }
    public void getSongs(){
        File home  = new File(MEDIA_PATH);
        if (home.listFiles(new
                FileExtensionFilter()).length >0){
            for (File file :home.listFiles(new FileExtensionFilter())){
                String  path = file.getPath();
                Uri uri ;
                uri = Uri.parse(path);
                String[] proj =
                        {
                                MediaStore.Audio.Media.DATA,
                                MediaStore.Audio.Media.TITLE,
                                MediaStore.Audio.Media.ARTIST,
                                MediaStore.Audio.Media.ALBUM,
                                MediaStore.Audio.Media.DURATION
                        };

            }

        }
    }

     class FileExtensionFilter implements FilenameFilter {
         String pattern = ".mp3";
         public  boolean accept(File dir, String name){
             return (name.endsWith(pattern) || name.endsWith(".MP3"));
         }
    }
}
