package com.spotify.unify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.unify.service.SpotifyClient;
import com.spotify.unify.service.SpotifyService;


public class MainActivity extends ActionBarActivity {

	public static final String TAG = "MainActivity";

	private SpotifyClient mSpotifyClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mSpotifyClient = new SpotifyClient(this, mPlayerServiceListener);
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

	private SpotifyService.Listener mPlayerServiceListener = new SpotifyService.Listener() {
		@Override
		public void onPlayerInitialized(Player player) {
			player.play("spotify:track:2V6yO7x7gQuaRoPesMZ5hr");
		}

		@Override
		public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {

		}

		@Override
		public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String errorDetails) {

		}
	};

}
