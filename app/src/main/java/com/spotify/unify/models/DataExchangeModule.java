package com.spotify.unify.models;

import java.util.HashMap;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Natasha on 5/16/2015.
 */
public class DataExchangeModule {
    SpotifyApi api;
    HashMap<String,NFCTrack> db;

    public DataExchangeModule(SpotifyApi api){
        this.api = api;
        buildDatabase();
    }
    private void buildDatabase(){
        db = new HashMap<String,NFCTrack>();
        db.put("1",new NFCTrack("1","spotify:track:2V6yO7x7gQuaRoPesMZ5hr","Location 1"));
        db.put("2",new NFCTrack("2","spotify:track:6OkSbOk5ajxGAEtFM751C4","Location 2"));

        //TODO: add a bunch of tracks
    }

    public Track getTrackByNFCID(String NFCID){
        return api.getService().getTrack(db.get(NFCID).getTrackURI());
    }
}
