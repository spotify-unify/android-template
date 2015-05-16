package com.spotify.unify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;

import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.unify.models.DataExchangeModule;
import com.spotify.unify.models.SerializableTrack;
import com.spotify.unify.service.SpotifyClient;
import com.spotify.unify.service.SpotifyPlaybackService;
import com.squareup.picasso.Picasso;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;


public class PlayerActivity extends ActionBarActivity {

    public static final String TAG = PlayerActivity.class.getSimpleName();

    private SpotifyClient mSpotifyClient;
    private SpotifyService mSpotifyService;
    private ImageView mCover;
    private Player mPlayer;
    private DataExchangeModule dataExchangeModule;
    private SerializableTrack mTrack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mCover = (ImageView) findViewById(R.id.cover);
        mSpotifyClient = ((UnifyApplication) getApplication()).getSpotifyClient();
        mSpotifyClient.setSpotifyPlaybackServiceListener(mPlayerServiceListener);
        mSpotifyClient.setClientListener(new SpotifyClient.ClientListener() {
            @Override
            public void onClientReady(SpotifyApi spotifyApi) {

            }

            @Override
            public void onAccessError() {

            }
        });
        mSpotifyClient.setActivity(this);
        mTrack = (SerializableTrack) getIntent().getSerializableExtra(MainActivity.KEY_TRACK);
        Picasso.with(getApplicationContext())
                .load(mTrack.getImageUrl())
                .into(mCover);
    }

    private SpotifyPlaybackService.Listener mPlayerServiceListener = new SpotifyPlaybackService.Listener() {
        @Override
        public void onPlayerInitialized(final Player player) {
            player.play(mTrack.getUri());
        }

        @Override
        public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {

        }

        @Override
        public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String errorDetails) {

        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        mSpotifyClient.connect();
    }

    @Override
    protected void onStop() {
        mSpotifyClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mSpotifyClient.onActivityResult(requestCode, resultCode, intent);
    }

    public void next(View v) {
        mPlayer.skipToNext();
    }

    public void previous(View v) {
        mPlayer.skipToPrevious();
    }

    public void pause(View v) {
        mPlayer.pause();
    }


}
