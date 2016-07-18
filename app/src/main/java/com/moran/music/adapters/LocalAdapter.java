package com.moran.music.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.Integer.parseInt;
import android.support.v7.widget.RecyclerView;

import com.moran.music.R;
import com.moran.music.utils.user_model;

/**
 * Created by Denis on 01/02/2016.
 */
public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.MyviewHolder> {
    Context context;

    List<user_model> users= Collections.EMPTY_LIST;

    public LocalAdapter(Context context, List<user_model> users){
        this.context=context;
        this.users=users;

    }



    @Override
    public LocalAdapter.MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item,null);
        MyviewHolder holder = new MyviewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(LocalAdapter.MyviewHolder holder, int position) {

        user_model musicData =users.get(position);
        int millis = musicData.getDuration();
        holder.title.setText(musicData.getSongName());
        String checker = musicData.getArtistName();
        if(checker.equals("<unknown>")){
            holder.artist.setText("Unknown Artist");
        }else {
            holder.artist.setText(checker);
        }
        //holder.link.setText(musicData.getUri());
        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(Long.parseLong(String.valueOf(millis))),
                TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(String.valueOf(millis))) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(Long.parseLong(String.valueOf(millis)))),
                TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(String.valueOf(millis))) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.parseLong(String.valueOf(millis)))));

        holder.duration.setText(hms);
        String uripath = musicData.getUri();
        Bitmap image;
        MediaMetadataRetriever mdata = new MediaMetadataRetriever();
        mdata.setDataSource(uripath);
        try{
            byte art[] = mdata.getEmbeddedPicture();
            image=BitmapFactory.decodeByteArray(art, 0, art.length);
        }catch (Exception e){
            image=null;
        }
        if (image==null){

        }else {
            Bitmap resized = Bitmap.createScaledBitmap(image,200,200,true);
            holder.album.setImageBitmap(resized);
        }

    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title, artist, link, duration;
        ImageView album, add,fav;
        public MyviewHolder(final View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.web_song);
            artist = (TextView) view.findViewById(R.id.web_artist);
            album = (ImageView) view.findViewById(R.id.web_pic);
            duration = (TextView) view.findViewById(R.id.web_duration);
            add = (ImageView) view.findViewById(R.id.btadd);
            fav = (ImageView) view.findViewById(R.id.bt_fav);

        }


        @Override
        public void onClick(View v) {

        }
    }

}
