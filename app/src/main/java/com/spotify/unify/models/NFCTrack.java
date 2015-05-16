package com.spotify.unify.models;

/**
 * Created by Natasha on 5/16/2015.
 */
public class NFCTrack {
    private String NFC_ID;
    private String trackURI;
    private String location;

    public NFCTrack(String NFC_ID,String trackURI, String location) {
        this.NFC_ID = NFC_ID;
        this.trackURI = trackURI;
        this.location = location;
    }

    public String getNFC_ID() {
        return NFC_ID;
    }

    public String getTrackURI() {
        return trackURI;
    }

    public String getLocation() {
        return location;
    }
}
