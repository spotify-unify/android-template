package com.spotify.unify;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.unify.service.SpotifyClient;
import com.spotify.unify.service.SpotifyPlaybackService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
    private TextView  mArtistText, mTrackText;
    private ImageButton mNextButton, mPrevButton, mPlayPauseButton;
    private ImageView mCover;
    private View mView;
    private Player mPlayer;
    private String trackID; // spotify:track:
    private PlayerStateCallback mPlayerStateCallback = new PlayerStateCallback() {
        @Override
        public void onPlayerState(PlayerState playerState) {
            if (playerState.playing) {
                mPlayer.pause();
                mPlayPauseButton.setImageResource(R.drawable.play);
            } else {
                mPlayer.resume();
                mPlayPauseButton.setImageResource(R.drawable.pause);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setUpView();
        mSpotifyClient = new SpotifyClient(this, mPlayerServiceListener, mClientListener);

        trackID = "2GHNZvjA57wLRl4FVk0MhS";
    }

    private void setUpView() {
        mArtistText = (TextView) findViewById(R.id.artistText);
        mTrackText = (TextView) findViewById(R.id.trackText);
        mCover = (ImageView) findViewById(R.id.cover);
        mPlayPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
        mNextButton = (ImageButton) findViewById(R.id.nextTrackButton);
        mPrevButton = (ImageButton) findViewById(R.id.prevTrackButton);
        mView = findViewById(R.id.background);
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
        mPlayer.getPlayerState(mPlayerStateCallback);
    }

    private SpotifyPlaybackService.Listener mPlayerServiceListener = new SpotifyPlaybackService.Listener() {
        @Override
        public void onPlayerInitialized(Player player) {
            mPlayer = player;
            player.play("spotify:track:" + trackID);
            mPlayPauseButton.setImageResource(R.drawable.pause);
            mPlayer.setRepeat(true);
        }

        @Override
        public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
            switch(eventType) {
                case PLAY:
                    break;
                case PAUSE:
                    break;
            }
        }

        @Override
        public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String errorDetails) {

        }
    };

    // This is for getting background color from the album cover
    private Target mCoverTarget = new Target() {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mCover.setImageBitmap(bitmap);

            Palette palette = Palette.generate(bitmap);
            Palette.Swatch swatch = palette.getVibrantSwatch();
            mView.setBackgroundColor(swatch.getRgb());
            mTrackText.setTextColor(swatch.getTitleTextColor());
            mArtistText.setTextColor(swatch.getBodyTextColor());
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private SpotifyClient.ClientListener mClientListener = mClientListener = new SpotifyClient.ClientListener() {
        @Override
        public void onClientReady(SpotifyApi spotifyApi) {
            final SpotifyService spotifyService = spotifyApi.getService();

            spotifyService.getTrack(trackID, new Callback<Track>() {
                @Override
                public void success(Track track, Response response) {
                    final List<Image> images = track.album.images;
                    if (!images.isEmpty()) {
                        //Collections.shuffle(images); This made the image tiny...
                        final Image randomImage = images.get(0);
                        Picasso.with(getApplicationContext())
                                .load(randomImage.url)
                                .into(mCoverTarget);
                    }

                    mTrackText.setText(track.name);
                    mArtistText.setText(track.artists.get(0).name);
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
