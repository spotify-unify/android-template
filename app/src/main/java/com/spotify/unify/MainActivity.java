package com.spotify.unify;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.unify.models.DataExchangeModule;
import com.spotify.unify.service.Authenticator;
import com.spotify.unify.service.SpotifyClient;
import com.spotify.unify.service.SpotifyPlaybackService;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Playlist;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String KEY_PLAYLIST_URI = "KEY_PLAYLIST_URI";
    public static final String KEY_PLAYLIST_NAME = "KEY_PLAYLIST_NAME";

    private static final int PLAYER_ACTIVITY_REQUEST_CODE = 100;

    public static final String MIME_TEXT_PLAIN = "text/plain";
    private SpotifyClient mSpotifyClient;
    private NfcAdapter mNfcAdapter;
    private SpotifyService mSpotifyService;
    private DataExchangeModule mDataExchangeModule;
    private final Queue<Runnable> mTasks = new LinkedList<>();
    private static long mLastSpawn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Authenticator.authenticate(this);
        mSpotifyClient = new SpotifyClient(this, mPlayerServiceListener, mClientListener);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        handleIntent(getIntent());
    }

    private void executeTasks() {
        /*if(mSpotifyService != null) {
            while(!mTasks.isEmpty()) {
                mTasks.poll().run();
            }
        }*/
    }

    private SpotifyClient.ClientListener mClientListener = new SpotifyClient.ClientListener() {
        @Override
        public void onClientReady(SpotifyApi spotifyApi) {
            mSpotifyService = spotifyApi.getService();
            mDataExchangeModule = new DataExchangeModule(spotifyApi);
            executeTasks();
        }

        @Override
        public void onAccessError() {

        }
    };

    private SpotifyPlaybackService.Listener mPlayerServiceListener = new SpotifyPlaybackService.Listener() {
        @Override
        public void onPlayerInitialized(final Player player) {
        }

        @Override
        public void onPlaybackEvent(PlayerNotificationCallback.EventType eventType, PlayerState playerState) {
        }

        @Override
        public void onPlaybackError(PlayerNotificationCallback.ErrorType errorType, String errorDetails) {

        }
    };


    private void handleIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {

            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                spawnNfcTask(tag);

            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                    spawnNfcTask(tag);
                    break;
                }
            }
        }
    }

    private void spawnNfcTask(Tag tag) {
        if (System.currentTimeMillis() - mLastSpawn > 5000) {
            new NdefReaderTask().execute(tag);
            mLastSpawn = System.currentTimeMillis();
        }
    }

    private class NdefReaderTask extends AsyncTask<Tag, Void, String> {

        @Override
        protected String doInBackground(Tag... params) {
            Tag tag = params[0];

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                // NDEF is not supported by this Tag.
                return null;
            }

            NdefMessage ndefMessage = ndef.getCachedNdefMessage();

            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records) {
                if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
                    try {
                        return readText(ndefRecord);
                    } catch (UnsupportedEncodingException e) {
                        Log.e(TAG, "Unsupported Encoding", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(final String result) {
            if (result == null) {
                Log.e("YELL", "GOT NULL RESULT");
                return;
            }
            Log.d(TAG, "Read content");

            //we read an nfc tag

            new AsyncTask<Void, Void, PlaylistHolder>() {
                @Override
                protected PlaylistHolder doInBackground(Void... voids) {
                    Log.d(TAG, "Getting track. result: " + result);
                    Playlist playlist = mDataExchangeModule.getPlaylistByNFCID(result);
                    PlaylistHolder holder = new PlaylistHolder();
                    holder.name = playlist.name;
                    holder.uri = playlist.uri;
                    return holder;
                }

                @Override
                protected void onPostExecute(PlaylistHolder holder) {
                    Log.e("YELL", "FINISHING PLAYER ACTIVITY");
                    finishActivity(PLAYER_ACTIVITY_REQUEST_CODE);
                    Intent i = new Intent(MainActivity.this, PlayerActivity.class);
                    i.putExtra(KEY_PLAYLIST_URI, holder.uri);
                    i.putExtra(KEY_PLAYLIST_NAME, holder.name);
                    Log.e("YELL", "STARTING PLAYER ACTIVITY");
                    startActivityForResult(i, PLAYER_ACTIVITY_REQUEST_CODE);

                }
            }.execute();


            executeTasks();

        }
    }

    private static class PlaylistHolder {
        public String name;
        public String uri;
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

        byte[] payload = record.getPayload();

        String utf8 = "UTF-8";
        String utf16 = "UTF-16";
        // Get the Text Encoding
        String textEncoding = ((payload[0] & 128) == 0) ? utf8 : utf16;

        // Get the Language Code
        int languageCodeLength = payload[0] & 0063;

        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");
        // e.g. "en"

        // Get the Text
        return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter  The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
         * at the documentation.
         *
         * In our case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode != PLAYER_ACTIVITY_REQUEST_CODE)
            mSpotifyClient.onActivityResult(requestCode, resultCode, intent);
    }

}

