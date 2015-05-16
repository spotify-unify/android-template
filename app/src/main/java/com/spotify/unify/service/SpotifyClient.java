package com.spotify.unify.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import kaaes.spotify.webapi.android.SpotifyApi;

public class SpotifyClient {

    public void setActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void setSpotifyPlaybackServiceListener(SpotifyPlaybackService.Listener mListener) {
        this.mListener = mListener;
    }

    public void setClientListener(ClientListener mClientListener) {
        this.mClientListener = mClientListener;
    }

    public interface ClientListener {
        void onClientReady(SpotifyApi spotifyApi);
        void onAccessError();
    }

    private static final String TAG = SpotifyClient.class.getSimpleName();

    private SpotifyPlaybackService mSpotifyPlaybackService;
    private Activity mActivity;
    private SpotifyPlaybackService.Listener mListener;
    private ClientListener mClientListener;
    private String mToken;
    private SpotifyApi mSpotifyApi;


    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        mToken = Authenticator.getToken(requestCode, responseCode, data);
        if (TextUtils.isEmpty(mToken)) {
            Log.e(TAG, "Failed to retrieve token");
            mClientListener.onAccessError();
        } else {
            if (mSpotifyPlaybackService != null)
                mSpotifyPlaybackService.initializeWithToken(mToken);
            mSpotifyApi = new SpotifyApi();
            mSpotifyApi.setAccessToken(mToken);
            mClientListener.onClientReady(mSpotifyApi);
        }
    }

    public void connect() {
        Intent intent = new Intent(mActivity.getApplicationContext(), SpotifyPlaybackService.class);
        mActivity.bindService(intent, mConnection, Activity.BIND_AUTO_CREATE);
    }

    public void disconnect() {
        mActivity.unbindService(mConnection);
    }

    private void onPlayerServiceConnected(SpotifyPlaybackService spotifyPlaybackService) {
        mSpotifyPlaybackService = spotifyPlaybackService;
        mSpotifyPlaybackService.setListener(mListener);
        if (!mSpotifyPlaybackService.hasToken() || !mSpotifyPlaybackService.hasPlayer()) {
            Authenticator.authenticate(mActivity);
        } else {
            mListener.onPlayerInitialized(mSpotifyPlaybackService.getPlayer());
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            onPlayerServiceConnected(((SpotifyPlaybackService.LocalBinder) service).getService());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
