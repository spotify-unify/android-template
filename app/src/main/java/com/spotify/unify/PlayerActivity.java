package com.spotify.unify;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.PlayerStateCallback;
import com.spotify.unify.models.DataExchangeModule;
import com.spotify.unify.models.SerializableTrack;
import com.spotify.unify.service.SpotifyClient;
import com.spotify.unify.service.SpotifyPlaybackService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class PlayerActivity extends ActionBarActivity {

    public static final String TAG = PlayerActivity.class.getSimpleName();

    private SpotifyClient mSpotifyClient;
    private TextView mArtistText;
    private TextView mTrackText;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private ImageButton mPlayPauseButton;
    private SpotifyService mSpotifyService;
    private ImageView mCover;
    private View mView;
    private Player mPlayer;
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

            readPlayerTrack(playerState);
        }
    };

    private void readPlayerTrack(PlayerState playerState) {
        if (!playerState.trackUri.startsWith("spotify:track:")) {
            Log.d(TAG, "Invalid track uri: " + playerState.trackUri);
            return;
        }

        String trackUri = playerState.trackUri.substring("spotify:track:".length());
        if (mSpotifyApi == null) return;
        mSpotifyApi.getService().getTrack(trackUri, new Callback<Track>() {
            @Override
            public void success(Track track, Response response) {
                setTrackViewInfo(track.name, track.artists.get(0).name, track.album.images.get(0).url);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private DataExchangeModule mDataExchangeModule;
    private SerializableTrack mTrack;
    private String mPlaylistUri;
    private SpotifyApi mSpotifyApi;
    private String mPlaylistTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setUpView();
        mPlaylistUri = getIntent().getStringExtra(MainActivity.KEY_PLAYLIST_URI);
        mPlaylistTitle = getIntent().getStringExtra(MainActivity.KEY_PLAYLIST_NAME);
        setTitle(getString(R.string.app_name) + " - " + mPlaylistTitle);

        mSpotifyClient = new SpotifyClient(this, mPlayerServiceListener, new SpotifyClient.ClientListener() {
            @Override
            public void onClientReady(SpotifyApi spotifyApi) {
                mSpotifyApi = spotifyApi;
            }

            @Override
            public void onAccessError() {

            }
        });

    }

    private void setTrackViewInfo(String title, String artistName, String imageUrl) {
        mTrackText.setText(title);
        mArtistText.setText(artistName);

        Picasso.with(getApplicationContext())
                .load(imageUrl)
                .into(mCoverTarget);
    }

    private void setUpView() {
        mArtistText = (TextView) findViewById(R.id.artistText);
        mTrackText = (TextView) findViewById(R.id.trackText);
        mCover = (ImageView) findViewById(R.id.cover);
        mPlayPauseButton = (ImageButton) findViewById(R.id.playPauseButton);
        mNextButton = (ImageButton) findViewById(R.id.nextTrackButton);
        mPrevButton = (ImageButton) findViewById(R.id.prevTrackButton);
        mView = findViewById(R.id.background);
        mCover = (ImageView) findViewById(R.id.cover);

        Typeface typeFace= Typeface.createFromAsset(getAssets(), "JosefinSans-SemiBold.ttf");
        mArtistText.setTypeface(typeFace);
        mTrackText.setTypeface(typeFace);
    }

    private SpotifyPlaybackService.Listener mPlayerServiceListener = new SpotifyPlaybackService.Listener() {
        @Override
        public void onPlayerInitialized(final Player player) {
            mPlayer = player;
            player.play(mPlaylistUri); //mTrack.getUri());
            mPlayPauseButton.setImageResource(R.drawable.pause);
            player.setRepeat(true);
        }

        @Override
        public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
            readPlayerTrack(playerState);
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
        mPlayPauseButton.setImageResource(R.drawable.pause);
    }

    public void previous(View v) {
        mPlayer.skipToPrevious();
        mPlayPauseButton.setImageResource(R.drawable.pause);
    }


    public void pause(View v) {
        mPlayer.getPlayerState(mPlayerStateCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // This is for getting background color from the album cover
    private Target mCoverTarget = new Target() {

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mCover.setImageBitmap(bitmap);

            Palette palette = Palette.generate(bitmap);
            Palette.Swatch swatch = null;
            if (palette.getVibrantSwatch() != null) {
                swatch = palette.getVibrantSwatch();
            } else if (palette.getMutedSwatch() != null) {
                swatch = palette.getMutedSwatch();
            }

            if (swatch != null) {
                mView.setBackgroundColor(swatch.getRgb());
                mTrackText.setTextColor(swatch.getTitleTextColor());
                mArtistText.setTextColor(swatch.getBodyTextColor());
            } else {
                mView.setBackgroundColor(Color.parseColor("#333"));
                mTrackText.setTextColor(Color.parseColor("#eee"));
                mArtistText.setTextColor(Color.parseColor("#ccc"));
            }

        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };
}
