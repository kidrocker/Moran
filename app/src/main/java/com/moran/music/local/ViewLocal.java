package com.moran.music.local;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.moran.music.R;
import com.moran.music.adapters.LocalAdapter;
import com.moran.music.database.SqliteHandle;
import com.moran.music.fragments.AlbumsFrag;
import com.moran.music.fragments.ArtistFrag;
import com.moran.music.fragments.SongsFrag;
import com.moran.music.services.Player;
import com.moran.music.services.getSongs;
import com.moran.music.utils.user_model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Denis on 01/02/2016.
 */
public class ViewLocal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView rc;
    Player musicSrv;
    LocalAdapter adapter;
    public boolean musicBound=false;
    List<user_model> users = Collections.EMPTY_LIST;
    ArrayList<String> songName;
    ArrayList<String> artistName;
    ArrayList<String> duration;
    ArrayList<String> uri;
    ArrayList<String> album;
    ArrayList<String> popupList;
    SqliteHandle db;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_local);
        db = new SqliteHandle(this);
        //rc =(RecyclerView)findViewById( R.id.song_list);


/*
        SongsFrag songsFrag = new SongsFrag();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragframe,songsFrag);
        ft.commit();
        */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_local);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_local);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_local);
        navigationView.setNavigationItemSelectedListener(this);







    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_songs) {
            SongsFrag songsFrag = new SongsFrag();
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
           // ft.replace(R.id.fragframe,songsFrag);
            ft.commit();
        } else if (id == R.id.nav_artist) {
            ArtistFrag songsFrag = new ArtistFrag();
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragframe,songsFrag);
            ft.commit();
        } else if (id == R.id.nav_album) {
            AlbumsFrag songsFrag = new AlbumsFrag();
            android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragframe,songsFrag);
            ft.commit();

        } else if (id == R.id.nav_genre) {
            Intent i = new Intent(ViewLocal.this, ViewLocal.class);
            startActivity(i);

        } else if (id == R.id.nav_share_local) {
            Toast.makeText(this, "Your feedback matters. Drop some", Toast.LENGTH_LONG).show();

        } else if (id == R.id.nav_send_local) {
            Toast.makeText(this, "Sharing is caring.Tell a friend", Toast.LENGTH_LONG).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_local);
        drawer.closeDrawer(GravityCompat.START);
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
            return true;
        }
        if (id == R.id.action_playall) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
