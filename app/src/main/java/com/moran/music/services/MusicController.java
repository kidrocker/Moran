package com.moran.music.services;

import android.content.Context;
import android.widget.MediaController;

/**
 * Created by Denis on 24/02/2016.
 */
public class MusicController extends MediaController {
    /**
     * Create a new MediaController from a session's token.
     *
     * @param context The caller's context.

     */
    public MusicController(Context context) {
        super(context);
    }
    public void hide(){}
}
