package com.moran.music.songs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.moran.music.R;

/**
 * Created by Kiura on 6/22/2016.
 */
public class ViewLocal extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_local);
/*
        ViewSong songsFrag = new ViewSong();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragframe,songsFrag);
        ft.commit();
        */
    }
}
