package com.spotify.unify.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

public class SpotifyService extends Service implements PlayerNotificationCallback, ConnectionStateCallback {

	private static final String TAG = "PlayerService";

	private final IBinder mBinder = new LocalBinder();
	private Player mPlayer;
	private String mToken;

	public boolean hasToken() {
		return !TextUtils.isEmpty(mToken);
	}

	public Player getPlayer() {
		return mPlayer;
	}

	public boolean hasPlayer() {
		return mPlayer != null;
	}

	public interface Listener {
		void onPlayerInitialized(Player player);
		void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState);
		void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String errorDetails);
	}

	private static final Listener NO_OP = new Listener() {
		@Override
		public void onPlayerInitialized(Player player) {

		}

		@Override
		public void onPlaybackEvent(EventType eventType, PlayerState playerState) {

		}

		@Override
		public void onPlaybackError(ErrorType errorType, String errorDetails) {

		}
	};

	private Listener mListener = NO_OP;

	public void setListener(Listener listener) {
		mListener = listener;
	}

	public SpotifyService() {

	}

	@Override
	public IBinder onBind(Intent intent) {
		startService(intent); // ಠ_ಠ
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public SpotifyService getService() {
			return SpotifyService.this;
		}
	}

	public void initializeWithToken(String token) {
		mToken = token;
		Config playerConfig = new Config(this, token, Authenticator.CLIENT_ID);
		mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
			@Override
			public void onInitialized(Player player) {
				mPlayer.addConnectionStateCallback(SpotifyService.this);
				mPlayer.addPlayerNotificationCallback(SpotifyService.this);
				mListener.onPlayerInitialized(mPlayer);
			}

			@Override
			public void onError(Throwable throwable) {
				Log.e(TAG, "Could not initialize player: " + throwable.getMessage());
			}
		});
	}

	@Override
	public void onLoggedIn() {
		Log.d(TAG, "User logged in");
	}

	@Override
	public void onLoggedOut() {
		Log.d(TAG, "User logged out");
	}

	@Override
	public void onLoginFailed(Throwable error) {
		Log.d(TAG, "Login failed");
	}

	@Override
	public void onTemporaryError() {
		Log.d(TAG, "Temporary error occurred");
	}

	@Override
	public void onConnectionMessage(String message) {
		Log.d(TAG, "Received connection message: " + message);
	}

	@Override
	public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
		Log.d(TAG, "Playback event received: " + eventType.name());
		mListener.onPlaybackEvent(eventType, playerState);
	}

	@Override
	public void onPlaybackError(ErrorType errorType, String errorDetails) {
		Log.d(TAG, "Playback error received: " + errorType.name());
		mListener.onPlaybackError(errorType, errorDetails);
	}

	@Override
	public void onDestroy() {
		Spotify.destroyPlayer(this);
		super.onDestroy();
	}
}
