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
        playlists.put("france", new NFCPlaylist("france", "spotify_france","34yDEWwKE8oX1MbccPpuHq", "France"));
        playlists.put("usa", new NFCPlaylist("usa", "spotifyusa","042d5Yc8uMmAPJ5JhMLLIj", "USA"));
        playlists.put("india", new NFCPlaylist("india", "marilde","2BNUiaO5WyKjcSOGBcOxGC", "India"));
        playlists.put("brazil", new NFCPlaylist("brazil", "marilde","0T3jXqgQqlThEPST9j1njg", "Brazil"));
        playlists.put("spain", new NFCPlaylist("spain", "lunyadeli","0xoKD4FwJUipesIT0HwSno", "Spain"));
        playlists.put("italy", new NFCPlaylist("italy", "1299878201","2PhvlOCldoNhngelKuuSI6", "Italy"));
        playlists.put("japan", new NFCPlaylist("japan", "1237604494","5SgNct62VNMHlALs1c1vqh", "Japan"));
        playlists.put("finland", new NFCPlaylist("finland", "nightwishband","4wBDB1Bhd9CeI0F5D6aVRZ", "Finland"));
        playlists.put("mexico", new NFCPlaylist("mexico", "spotify","7MaHaNasfz3GySo2sQexI7", "Mexico"));
        playlists.put("sweden", new NFCPlaylist("sweden", "bassbanana","6Ixvm0VqC89ncw6xPwTW5Y", "Sweden"));
        playlists.put("russia", new NFCPlaylist("russia", "axel.stevensmalmberg","2fb6OMtd2gmzo7enkIsXGW", "Russia"));
        playlists.put("mali", new NFCPlaylist("mali","jaghetermoa","55MLLF5gQ75mCYYsdwKxEg","mali"));
        playlists.put("australia", new NFCPlaylist("australia","marilde","4sgZor289Mk5SFGpnwHn8y","australia"));

    }

    private void buildTrackDatabase(){
        tracks = new HashMap<String,NFCTrack>();
        tracks.put("france", new NFCTrack("france", "0DiWol3AO6WpXZgp0goxAV", "France"));
        tracks.put("usa", new NFCTrack("usa", "6OkSbOk5ajxGAEtFM751C4", "USA"));
        tracks.put("india", new NFCTrack("india", "0vUjDXHs7XwUnL42ntZaTS", "India"));
        tracks.put("brazil", new NFCTrack("brazil", "1rundCBCrbT8CNdBRrETGd", "Brazil"));
        tracks.put("spain", new NFCTrack("spain", "78dMkFd4FarAJYj7NUcutI", "Spain"));
        tracks.put("italy", new NFCTrack("italy", "7AgXyagsth2Rk5KQNYmQNz", "Italy"));
        tracks.put("japan", new NFCTrack("japan", "5oGk9tbzYDy5jheoCwVYNI", "Japan"));
        tracks.put("finland", new NFCTrack("finland", "1FSheI0xU7AjrRNGIsTJrB", "Finland"));
        tracks.put("mexico", new NFCTrack("mexico", "0i69ZiWitf3SFiaTA8249M","Mexico"));
        tracks.put("sweden", new NFCTrack("sweden", "7oDjaRDdf9zNQ8N3yRjQwF", "Sweden"));
        tracks.put("russia", new NFCTrack("russia", "596WZD5V9np8nRtuMClJXi", "Russia"));
        tracks.put("mali",new NFCTrack("mali","4jKe4NnyGnDKb59svENink","mali"));
        tracks.put("australia",new NFCTrack("australia","1gV9z71GcNfGwG5L7xY2Wy","australia"));

    }

    public Track getTrackByNFCID(String NFCID){
        if(!tracks.containsKey(NFCID)) return null;
        return api.getService().getTrack(tracks.get(NFCID).getTrackURI());
    }
    public Playlist getPlaylistByNFCID(String NFCID){
        if(!playlists.containsKey(NFCID)) return null;
        NFCPlaylist plist = playlists.get(NFCID);
        return api.getService().getPlaylist(plist.getOwner_username(), plist.getPlaylistURI());
    }
}
