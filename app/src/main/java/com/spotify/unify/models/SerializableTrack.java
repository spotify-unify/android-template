package com.spotify.unify.models;

import java.io.Serializable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by apals on 16/05/15.
 */
public class SerializableTrack implements Serializable {

    private String uri;

    public String getUri() {
        return uri;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getArtistName() {
        return artistName;
    }

    private String imageUrl;
    private String title;
    private String artistName;

    public SerializableTrack(Track track) {
        this.uri = track.uri;
        this.imageUrl = track.album.uri;
        this.title = track.name;
        this.artistName = track.artists.get(0).name;
    }

    public SerializableTrack() {}
}
