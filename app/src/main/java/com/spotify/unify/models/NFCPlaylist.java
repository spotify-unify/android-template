package com.spotify.unify.models;

/**
 * Created by Natasha on 5/16/2015.
 */
public class NFCPlaylist {
    private String NFC_ID;
    private String owner_username;
    private String playlistURI;
    private String location;

    public NFCPlaylist(String NFC_ID, String owner_username,String playlistURI, String location) {
        this.NFC_ID = NFC_ID;
        this.owner_username = owner_username;
        this.playlistURI = playlistURI;
        this.location = location;
    }

    public String getNFC_ID() {
        return NFC_ID;
    }

    public String getOwner_username() {
        return owner_username;
    }

    public String getPlaylistURI() {
        return playlistURI;
    }

    public String getLocation() {
        return location;
    }
}
