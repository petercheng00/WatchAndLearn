package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.model.GuideAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by chepeter on 8/2/14.
 */
public class SavedActivity extends Activity implements DataApi.DataListener,
        MessageApi.MessageListener, NodeApi.NodeListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener {

    private static final String TAG = "SavedActivity";
    private static final int REQUEST_RESOLVE_ERROR = 1000;

    private static final String START_ACTIVITY_PATH = "/start-activity";
    private static final String IMAGE_PATH = "/image";
    private static final String IMAGE_KEY = "photo";
    private static final String COUNT_KEY = "count";
    private static final String FILENAME_KEY = "filename";
    private static final String JSON_GUIDE_KEY = "jsonGuide";
    private static final String GUIDE_PATH = "/guide";

    private GoogleApiClient mGoogleApiClient;

    private boolean mResolvingError = false;

    private Handler mHandler;
    private ScheduledExecutorService mGeneratorExecutor;

    private ListView savedList;
    private GuideAdapter guideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        savedList = (ListView) this.findViewById(R.id.saved_list);
        savedList.setOnItemClickListener(this);

        List<Guide> savedGuides = this.loadSavedGuides();
        displayGuides(savedGuides);

        mHandler = new Handler();
        mGeneratorExecutor = new ScheduledThreadPoolExecutor(1);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        startWearableActivity();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (!mResolvingError) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            Wearable.NodeApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        mDataItemGeneratorFuture.cancel(true /* mayInterruptIfRunning */);
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mDataItemGeneratorFuture = mGeneratorExecutor.scheduleWithFixedDelay(
//                new DataItemGenerator(), 1, 5, TimeUnit.SECONDS);
//    }

    private List<Guide> loadSavedGuides() {
        List<File> guideFiles = Arrays.asList(this.getFilesDir().listFiles());
        List<Guide> guides = new ArrayList<Guide>(guideFiles.size());
        Gson gson = new Gson();
        for (File file : guideFiles) {
            if (file.isFile()) {
                Guide g = gson.fromJson(fileToString(file), Guide.class);
                if (g.getNumSteps() > 0) {
                    guides.add(g);
                }
            }
        }
        return guides;
    }

    public String fileToString(File file) {
        try {
            FileInputStream fis = openFileInput(file.getName());
            StringBuffer fileContent = new StringBuffer("");

            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, n));
            }
            return fileContent.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void displayGuides(List<Guide> guides) {
        this.guideAdapter = new GuideAdapter(this, R.layout.saved_guide, guides);
        this.savedList.setAdapter(guideAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Push this guide to watch!
        Guide guide = guideAdapter.getItem(position);
        List<File> guideFiles = Arrays.asList(this.getFilesDir().listFiles());
        String jsonString = "";
        String filename = "";
        for (File file : guideFiles) {
            if (file.isFile()) {
                if (file.getName().contains(guide.getId())) {
                    filename = file.getName();
                    jsonString = fileToString(file);
                    Log.e(TAG, "file found: " + file.getName() + " : " + jsonString);
                }
            }
        }

        if (!TextUtils.isEmpty(jsonString) && mGoogleApiClient.isConnected()) {
            sendGuide(filename, jsonString);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "Google API Client was connected");
        mResolvingError = false;
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection to Google API client was suspended");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.e(TAG, "onDataChanged: " + dataEvents);
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (DataEvent event : events) {
                    if (event.getType() == DataEvent.TYPE_CHANGED) {
                        //mDataItemListAdapter.add(new Event("DataItem Changed", event.getDataItem().toString()));
                    } else if (event.getType() == DataEvent.TYPE_DELETED) {
                        //mDataItemListAdapter.add(new Event("DataItem Deleted", event.getDataItem().toString()));
                    }
                }
            }
        });
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.e(TAG, "onMessageReceived() A message from watch was received:" + messageEvent
                .getRequestId() + " " + messageEvent.getPath());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //mDataItemListAdapter.add(new Event("Message from watch", messageEvent.toString()));
            }
        });
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.e(TAG, "onPeerConnected: " + node);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //mDataItemListAdapter.add(new Event("Connected", peer.toString()));
            }
        });
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.e(TAG, "onPeerDisconnected: " + node);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //mDataItemListAdapter.add(new Event("Disconnected", peer.toString()));
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            Log.e(TAG, "Connection to Google API client has failed");
            mResolvingError = false;
            //mStartActivityBtn.setEnabled(false);
            //mSendPhotoBtn.setEnabled(false);
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            Wearable.MessageApi.removeListener(mGoogleApiClient, this);
            Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        }
    }

    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node);
            }
            return null;
        }
    }

    private void sendStartActivityMessage(String node) {
        Wearable.MessageApi.sendMessage(
                mGoogleApiClient, node, START_ACTIVITY_PATH, new byte[0]).setResultCallback(
                new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "Failed to send message with status code: "
                                    + sendMessageResult.getStatus().getStatusCode());
                        }
                    }
                }
        );
    }

    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<String>();
        NodeApi.GetConnectedNodesResult nodes =
                Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

        for (Node node : nodes.getNodes()) {
            results.add(node.getId());
        }

        return results;
    }

    /**
     * Sends an RPC to start a fullscreen Activity on the wearable.
     */
    public void startWearableActivity() {
        Log.e(TAG, "Generating RPC");

        // Trigger an AsyncTask that will query for a list of connected nodes and send a
        // "start-activity" message to each connected node.
        new StartWearableActivityTask().execute();
    }

    private void sendGuide(String filename, String jsonGuide) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(GUIDE_PATH);
        putDataMapRequest.getDataMap().putString(FILENAME_KEY, filename);
        putDataMapRequest.getDataMap().putString(JSON_GUIDE_KEY, jsonGuide);
        PutDataRequest request = putDataMapRequest.asPutDataRequest();

        Log.e(TAG, "Generating DataItem: " + request);
        if (!mGoogleApiClient.isConnected()) {
            Log.e(TAG, "Not connected to Google Api Client");
            return;
        }

        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        dataItemResult.getStatus();
                        Log.e(TAG, "putDataItem, status code: "
                                + dataItemResult.getStatus().getStatusCode());
                    }
                });
    }
}
