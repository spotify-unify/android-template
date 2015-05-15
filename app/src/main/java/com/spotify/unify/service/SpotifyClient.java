package com.spotify.unify.service;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class SpotifyClient {

	private static final String TAG = "SpotifyClient";
	private final Activity mActivity;
	private SpotifyService mSpotifyService;
	private SpotifyService.Listener mListener;


	public SpotifyClient(Activity activity, SpotifyService.Listener listener) {
		mActivity = activity;
		mListener = listener;
	}

	public void onActivityResult(int requestCode, int responseCode, Intent data) {
		String token = Authenticator.getToken(requestCode, responseCode, data);
		if (TextUtils.isEmpty(token)) {
			Log.e(TAG, "Failed to retrieve token");
		} else {
			mSpotifyService.initializeWithToken(token);
		}
	}

	public void connect() {
		Intent intent = new Intent(mActivity.getApplicationContext(), SpotifyService.class);
		mActivity.bindService(intent, mConnection, Activity.BIND_AUTO_CREATE);
	}

	public void disconnect() {
		mActivity.unbindService(mConnection);
	}

	private void onPlayerServiceConnected(SpotifyService spotifyService) {
		mSpotifyService = spotifyService;
		mSpotifyService.setListener(mListener);
		if (!mSpotifyService.hasToken() || !mSpotifyService.hasPlayer()) {
			Authenticator.authenticate(mActivity);
		} else {
			mListener.onPlayerInitialized(mSpotifyService.getPlayer());
		}
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			onPlayerServiceConnected(((SpotifyService.LocalBinder) service).getService());
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {

		}
	};
}
