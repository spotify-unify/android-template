package com.spotify.unify;

import android.app.Application;

import com.spotify.unify.service.SpotifyClient;

/**
 * Created by apals on 16/05/15.
 */
public class UnifyApplication extends Application {

    private SpotifyClient mSpotifyClient;

    public SpotifyClient getSpotifyClient() {
        if(mSpotifyClient == null)
            mSpotifyClient = new SpotifyClient();
        return mSpotifyClient;
    }


}
