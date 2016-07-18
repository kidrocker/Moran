package com.moran.music.artists;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.moran.music.R;
import com.moran.music.services.Player;
import com.moran.music.utils.user_model;

import java.util.Collections;
import java.util.List;

/**
 * Created by Denis on 08/03/2016.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.MyviewHolder> {
    Context context;

    List<user_model> users= Collections.EMPTY_LIST;
    Player musicSrv;
    boolean musicBound=false;
    public ArtistAdapter(Context context, List<user_model> users){
        this.context=context;
        this.users=users;

    }



    @Override
    public ArtistAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_artist,null);
        MyviewHolder holder = new MyviewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ArtistAdapter.MyviewHolder holder, int position) {

        user_model musicData;
        musicData=users.get(position);
        String checker;
        checker= musicData.getArtistfrag();
        if (checker.equals("null")){
            int yo = position++;
            musicData = users.get(yo);
             checker = musicData.getArtistfrag();
        }
            if (checker.equals("<unknown>")) {
                  holder.artist.setText("Unknown Artist");
            } else {
                holder.artist.setText(checker);
            }
            holder.album.setText("Album:");
            //holder.genre.setText("genre");


            String uripath = musicData.getUri();
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


            } else {

                //Drawable bd = new BitmapDrawable(Resources.getSystem(),image);
                holder.albumart.setAdjustViewBounds(true);
                holder.albumart.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Bitmap resized = Bitmap.createScaledBitmap(image, 500, 650, true);
                holder.albumart.setImageBitmap(resized);
                Palette.from(image).generate(new Palette.PaletteAsyncListener() {
                    public void onGenerated(Palette palette) {
                        Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                        if (vibrantSwatch != null) {
                            holder.linearPalette.setBackgroundColor(vibrantSwatch.getRgb());
                           // holder.artist.setTextColor(vibrantSwatch.getTitleTextColor());
                            //holder.album.setTextColor(vibrantSwatch.getBodyTextColor());
                        }
                    }
                });
            }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView album, artist, genre;
        ImageView albumart;
        LinearLayout linearPalette;
        public MyviewHolder(final View view) {
            super(view);
            album = (TextView) view.findViewById(R.id.artist_album);
            artist = (TextView) view.findViewById(R.id.main_artist);
            albumart = (ImageView) view.findViewById(R.id.rela_album);
            linearPalette = (LinearLayout) view.findViewById(R.id.linear_palette);

        }


        @Override
        public void onClick(View v) {

        }
    }

}

