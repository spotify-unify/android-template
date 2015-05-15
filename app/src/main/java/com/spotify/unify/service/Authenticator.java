package com.spotify.unify.service;

import android.app.Activity;
import android.content.Intent;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class Authenticator {

	public static final String CLIENT_ID = "86bb0f34c1aa4fea969be1d75a1dd6d6";
	public static final String REDIRECT_URI = "unifyprotocol://callback";

	// Request code that will be used to verify if the result comes from correct activity
	public static final int REQUEST_CODE = 1337;

	public static void authenticate(Activity targetActivity) {

		AuthenticationRequest.Builder builder =
				new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
		builder.setScopes(new String[]{"user-read-private", "streaming"});
		AuthenticationRequest request = builder.build();

		AuthenticationClient.openLoginActivity(targetActivity, REQUEST_CODE, request);
	}

	public static String getToken(int requestCode, int resultCode, Intent intent) {
		String token = "";
		if (requestCode == Authenticator.REQUEST_CODE) {
			AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
			if (response.getType() == AuthenticationResponse.Type.TOKEN) {
				token = response.getAccessToken();
			}
		}
		return token;
	}



}
