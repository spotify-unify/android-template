package com.spotify.unify;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.unify.models.DataExchangeModule;
import com.spotify.unify.service.SpotifyClient;
import com.spotify.unify.service.SpotifyPlaybackService;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.User;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PlayerActivity extends ActionBarActivity {

    public static final String TAG = PlayerActivity.class.getSimpleName();

    private SpotifyClient mSpotifyClient;
    private SpotifyService mSpotifyService;
    private ImageView mCover;
    private Player mPlayer;
    private DataExchangeModule dataExchangeModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mCover = (ImageView) findViewById(R.id.cover);
        mSpotifyClient = new SpotifyClient(this, mPlayerServiceListener, mClientListener);
    }

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

    private SpotifyPlaybackService.Listener mPlayerServiceListener = new SpotifyPlaybackService.Listener() {
        @Override
        public void onPlayerInitialized(final Player player) {
            Log.d(TAG, "TRYING TO PLAY SONG");
            new AsyncTask<String, Void, Void>() {
                @Override
                protected Void doInBackground(String... strings) {
                    System.out.println(strings[0]);
                    Track track = dataExchangeModule.getTrackByNFCID(strings[0]);
                    player.play(track.uri);
                    return null;
                }
            }.execute("1");

            mPlayer = player;
        }

        @Override
        public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
            switch (eventType) {
                case PLAY:

                    mSpotifyService.getTrack(playerState.trackUri, new Callback<Track>() {
                        @Override
                        public void success(Track track, Response response) {
                            final List<Image> images = track.album.images;
                            if (!images.isEmpty()) {
                                Collections.shuffle(images);
                                final Image randomImage = images.get(0);
                                Picasso.with(getApplicationContext())
                                        .load(randomImage.url)
                                        .into(mCover);
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });

                    break;
                case PAUSE:
                    break;
            }
        }

        @Override
        public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String errorDetails) {

        }
    };

    private SpotifyClient.ClientListener mClientListener = mClientListener = new SpotifyClient.ClientListener() {
        @Override
        public void onClientReady(SpotifyApi spotifyApi) {
            mSpotifyService = spotifyApi.getService();
            dataExchangeModule = new DataExchangeModule(spotifyApi);
            final SpotifyService spotifyService = spotifyApi.getService();
            spotifyService.getTrack("2V6yO7x7gQuaRoPesMZ5hr", new Callback<Track>() {
                @Override
                public void success(Track track, Response response) {
                    final List<Image> images = track.album.images;
                    if (!images.isEmpty()) {
                        Collections.shuffle(images);
                        final Image randomImage = images.get(0);
                        Picasso.with(getApplicationContext())
                                .load(randomImage.url)
                                .into(mCover);
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });

            spotifyService.getMe(new Callback<User>() {
                @Override
                public void success(User user, Response response) {

                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }

        @Override
        public void onAccessError() {

        }
    };
}
