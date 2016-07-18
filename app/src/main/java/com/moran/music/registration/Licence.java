package com.moran.music.registration;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.moran.music.MainActivity;
import com.moran.music.R;
import com.moran.music.fragments.SongsFrag;
import com.moran.music.services.getSongs;

/**
 * Created by Denis on 07/02/2016.
 */
public class Licence extends Activity {
    Button login, reg;
    TextView later;
    Uri uri1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new getSongs(uri1 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this).execute("");
        //new getSongs(uri1 = MediaStore.Audio.Media.INTERNAL_CONTENT_URI, this).execute("");
        setContentView(R.layout.license_view);

        later = (TextView) findViewById(R.id.tv_skip);
        login = (Button) findViewById(R.id.bt_login);
        reg = (Button) findViewById(R.id.bt_reg);


        later.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent y = new Intent(Licence.this,SongsFrag.class);
        startActivity(y);
        Licence.this.finish();

    }
});
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent x = new Intent(Licence.this, Login.class);
                startActivity(x);
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent q = new Intent(Licence.this, Register.class);
                startActivity(q);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}
