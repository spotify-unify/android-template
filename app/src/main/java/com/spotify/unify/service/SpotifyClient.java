package com.spotify.unify.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import kaaes.spotify.webapi.android.SpotifyApi;

public class SpotifyClient {

    private SpotifyPlaybackService mSpotifyPlaybackService;
    private final Activity mActivity;
    private final SpotifyPlaybackService.Listener mListener;
    private final ClientListener mClientListener;

    private String mToken;
    private SpotifyApi mSpotifyApi;

    public SpotifyClient(Activity activity, SpotifyPlaybackService.Listener playbackServiceListener, ClientListener clientListener) {
        mActivity = activity;
        mListener = playbackServiceListener;
        mClientListener = clientListener;
    }

    public interface ClientListener {
        void onClientReady(SpotifyApi spotifyApi);
        void onAccessError();
    }

    private static final String TAG = SpotifyClient.class.getSimpleName();

    public void onActivityResult(int requestCode, int responseCode, Intent data) {
        mToken = Authenticator.getToken(requestCode, responseCode, data);
        if (TextUtils.isEmpty(mToken)) {
            Log.e(TAG, "Failed to retrieve token");
            mClientListener.onAccessError();
        } else {
            mSpotifyApi = new SpotifyApi();
            mSpotifyApi.setAccessToken(mToken);
            mClientListener.onClientReady(mSpotifyApi);
            if (mSpotifyPlaybackService != null) {
                mSpotifyPlaybackService.setListener(mListener);
                mSpotifyPlaybackService.initializeWithToken(mToken);
            }
        }
    }

    public void connect() {
        Intent intent = new Intent(mActivity.getApplicationContext(), SpotifyPlaybackService.class);
        mActivity.bindService(intent, mConnection, Activity.BIND_AUTO_CREATE);
    }

    public void disconnect() {
        mSpotifyPlaybackService.removeListener();
        mActivity.unbindService(mConnection);
    }

    private void onPlayerServiceConnected(SpotifyPlaybackService spotifyPlaybackService) {
        mSpotifyPlaybackService = spotifyPlaybackService;
        if (!mSpotifyPlaybackService.hasToken() || !mSpotifyPlaybackService.hasPlayer()) {
            Authenticator.authenticate(mActivity);
        } else {
            mToken = mSpotifyPlaybackService.getToken();
            mSpotifyApi = new SpotifyApi();
            mSpotifyApi.setAccessToken(mToken);
            mClientListener.onClientReady(mSpotifyApi);
            mSpotifyPlaybackService.setListener(mListener);
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
