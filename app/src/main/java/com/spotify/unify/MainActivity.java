package com.spotify.unify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;


public class MainActivity extends ActionBarActivity implements
		PlayerNotificationCallback, ConnectionStateCallback {

	private static final String CLIENT_ID = "86bb0f34c1aa4fea969be1d75a1dd6d6";
	private static final String REDIRECT_URI = "unifyprotocol://callback";

	// Request code that will be used to verify if the result comes from correct activity
	private static final int REQUEST_CODE = 1337;

	private Player mPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AuthenticationRequest.Builder builder =
				new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
		builder.setScopes(new String[]{"user-read-private", "streaming"});
		AuthenticationRequest request = builder.build();

		AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		// Check if result comes from the correct activity
		if (requestCode == REQUEST_CODE) {
			AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
			if (response.getType() == AuthenticationResponse.Type.TOKEN) {
				Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
				mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
					@Override
					public void onInitialized(Player player) {
						mPlayer.addConnectionStateCallback(MainActivity.this);
						mPlayer.addPlayerNotificationCallback(MainActivity.this);
						mPlayer.play("spotify:track:2TpxZ7JUBn3uw46aR7qd6V");
					}

					@Override
					public void onError(Throwable throwable) {
						Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
					}
				});
			}
		}
	}

	@Override
	public void onLoggedIn() {
		Log.d("MainActivity", "User logged in");
	}

	@Override
	public void onLoggedOut() {
		Log.d("MainActivity", "User logged out");
	}

	@Override
	public void onLoginFailed(Throwable error) {
		Log.d("MainActivity", "Login failed");
	}

	@Override
	public void onTemporaryError() {
		Log.d("MainActivity", "Temporary error occurred");
	}

	@Override
	public void onConnectionMessage(String message) {
		Log.d("MainActivity", "Received connection message: " + message);
	}

	@Override
	public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
		Log.d("MainActivity", "Playback event received: " + eventType.name());
		switch (eventType) {
			// Handle event type as necessary
			default:
				break;
		}
	}

	@Override
	public void onPlaybackError(ErrorType errorType, String errorDetails) {
		Log.d("MainActivity", "Playback error received: " + errorType.name());
		switch (errorType) {
			// Handle error type as necessary
			default:
				break;
		}
	}

	@Override
	protected void onDestroy() {
		// VERY IMPORTANT! This must always be called or else you will leak resources
		Spotify.destroyPlayer(this);
		super.onDestroy();
	}
}
