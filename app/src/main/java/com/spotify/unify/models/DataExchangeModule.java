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
        playlists.put("France", new NFCPlaylist("France", "spotify_france","34yDEWwKE8oX1MbccPpuHq", "France"));
        playlists.put("USA", new NFCPlaylist("USA", "spotifyusa","042d5Yc8uMmAPJ5JhMLLIj", "USA"));
        playlists.put("India", new NFCPlaylist("India", "marilde","2BNUiaO5WyKjcSOGBcOxGC", "India"));
        playlists.put("Brazil", new NFCPlaylist("Brazil", "marilde","0T3jXqgQqlThEPST9j1njg", "Brazil"));
        playlists.put("Spain", new NFCPlaylist("Spain", "lunyadeli","0xoKD4FwJUipesIT0HwSno", "Spain"));
        playlists.put("Italy", new NFCPlaylist("Italy", "1299878201","2PhvlOCldoNhngelKuuSI6", "Italy"));
        playlists.put("Japan", new NFCPlaylist("Japan", "1237604494","5SgNct62VNMHlALs1c1vqh", "Japan"));
        playlists.put("Finland", new NFCPlaylist("Finland", "nightwishband","4wBDB1Bhd9CeI0F5D6aVRZ", "Finland"));
        playlists.put("Mexico", new NFCPlaylist("Mexico", "spotify","7MaHaNasfz3GySo2sQexI7", "Mexico"));
        playlists.put("Sweden", new NFCPlaylist("Sweden", "bassbanana","6Ixvm0VqC89ncw6xPwTW5Y", "Sweden"));
        playlists.put("Russia", new NFCPlaylist("Russia", "axel.stevensmalmberg","2fb6OMtd2gmzo7enkIsXGW", "Russia"));

    }

    private void buildTrackDatabase(){
        tracks = new HashMap<String,NFCTrack>();
        tracks.put("France", new NFCTrack("France", "0DiWol3AO6WpXZgp0goxAV", "France"));
        tracks.put("USA", new NFCTrack("USA", "6OkSbOk5ajxGAEtFM751C4", "USA"));
        tracks.put("India", new NFCTrack("India", "0vUjDXHs7XwUnL42ntZaTS", "India"));
        tracks.put("Brazil", new NFCTrack("Brazil", "1rundCBCrbT8CNdBRrETGd", "Brazil"));
        tracks.put("Spain", new NFCTrack("Spain", "78dMkFd4FarAJYj7NUcutI", "Spain"));
        tracks.put("Italy", new NFCTrack("Italy", "7AgXyagsth2Rk5KQNYmQNz", "Italy"));
        tracks.put("Japan", new NFCTrack("Japan", "5oGk9tbzYDy5jheoCwVYNI", "Japan"));
        tracks.put("Finland", new NFCTrack("Finland", "1FSheI0xU7AjrRNGIsTJrB", "Finland"));
        tracks.put("Mexico", new NFCTrack("Mexico", "0i69ZiWitf3SFiaTA8249M","Mexico"));
        tracks.put("Sweden", new NFCTrack("Sweden", "7oDjaRDdf9zNQ8N3yRjQwF", "Sweden"));
        tracks.put("Russia", new NFCTrack("Russia", "596WZD5V9np8nRtuMClJXi", "Russia"));


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
