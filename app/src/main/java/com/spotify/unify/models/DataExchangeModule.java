package com.spotify.unify.models;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Natasha on 5/16/2015.
 */
public class DataExchangeModule {
    SpotifyApi api;
    HashMap<String,NFCTrack> tracks;
    HashMap<String,NFCPlaylist> playlists;

    public DataExchangeModule(SpotifyApi api){
        this.api = api;
        buildTrackDatabase();
        buildPlaylistDatabase();
    }

    private void buildPlaylistDatabase() {
        playlists = new HashMap<String,NFCPlaylist>();
        playlists.put("1", new NFCPlaylist("1", "spotify_france","34yDEWwKE8oX1MbccPpuHq", "France"));
        playlists.put("2", new NFCPlaylist("2", "spotifyusa","042d5Yc8uMmAPJ5JhMLLIj", "USA"));
        playlists.put("3", new NFCPlaylist("3", "marilde","2BNUiaO5WyKjcSOGBcOxGC", "India"));
        playlists.put("4", new NFCPlaylist("4", "marilde","0T3jXqgQqlThEPST9j1njg", "Brazil"));
        playlists.put("5", new NFCPlaylist("5", "lunyadeli","0xoKD4FwJUipesIT0HwSno", "Spain"));
        playlists.put("6", new NFCPlaylist("6", "1299878201","2PhvlOCldoNhngelKuuSI6", "Italy"));
        playlists.put("7", new NFCPlaylist("7", "1237604494","5SgNct62VNMHlALs1c1vqh", "Japan"));
        playlists.put("8", new NFCPlaylist("8", "nightwishband","4wBDB1Bhd9CeI0F5D6aVRZ", "Finland"));
        playlists.put("9", new NFCPlaylist("9", "spotify","7MaHaNasfz3GySo2sQexI7", "Mexico"));
        playlists.put("10", new NFCPlaylist("10", "bassbanana","6Ixvm0VqC89ncw6xPwTW5Y", "Sweden"));
        playlists.put("11", new NFCPlaylist("11", "axel.stevensmalmberg","2fb6OMtd2gmzo7enkIsXGW", "Russia"));

    }

    private void buildTrackDatabase(){
        tracks = new HashMap<String,NFCTrack>();
        tracks.put("1", new NFCTrack("1", "0DiWol3AO6WpXZgp0goxAV", "France"));
        tracks.put("2", new NFCTrack("2", "6OkSbOk5ajxGAEtFM751C4", "USA"));
        tracks.put("3", new NFCTrack("3", "0vUjDXHs7XwUnL42ntZaTS", "India"));
        tracks.put("4", new NFCTrack("4", "1rundCBCrbT8CNdBRrETGd", "Brazil"));
        tracks.put("5", new NFCTrack("5", "78dMkFd4FarAJYj7NUcutI", "Spain"));
        tracks.put("6", new NFCTrack("6", "7AgXyagsth2Rk5KQNYmQNz", "Italy"));
        tracks.put("7", new NFCTrack("7", "5oGk9tbzYDy5jheoCwVYNI", "Japan"));
        tracks.put("8", new NFCTrack("8", "1FSheI0xU7AjrRNGIsTJrB", "Finland"));
        tracks.put("9", new NFCTrack("9", "0i69ZiWitf3SFiaTA8249M","Mexico"));
        tracks.put("10", new NFCTrack("10", "7oDjaRDdf9zNQ8N3yRjQwF", "Sweden"));
        tracks.put("11", new NFCTrack("11", "596WZD5V9np8nRtuMClJXi", "Russia"));


    }

    public Track getTrackByNFCID(String NFCID){
        if(!tracks.containsKey(NFCID)) return null;
        return api.getService().getTrack(tracks.get(NFCID).getTrackURI());
    }
    public Playlist getPlaylistByNFCID(String NFCID){
        if(!playlists.containsKey(NFCID)) return null;
        NFCPlaylist plist = playlists.get(NFCID);
        return api.getService().getPlaylist(plist.getOwner_username(),plist.getPlaylistURI());
    }
}
