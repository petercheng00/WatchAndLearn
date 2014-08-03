package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
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
import com.peterpeterallie.watchandlearnbeta.model.GuideInstructables;
import com.peterpeterallie.watchandlearnbeta.model.Step;
import com.peterpeterallie.watchandlearnbeta.util.BitmapUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created by chepeter on 8/2/14.
 */
public class SavedActivity extends Activity implements DataApi.DataListener,
        MessageApi.MessageListener, NodeApi.NodeListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

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

    private ListView savedList;
    private GuideAdapter guideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        savedList = (ListView) this.findViewById(R.id.saved_list);
        savedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Guide guide = guideAdapter.getItem(position);
                openGuide(guide);
            }
        });
        savedList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                File[] localFiles = getFilesDir().listFiles();
                if (position >= localFiles.length) {
                    return true;
                }
                File toDelete = localFiles[position];
                toDelete.delete();
                guideAdapter.remove(guideAdapter.getItem(position));
                guideAdapter.notifyDataSetChanged();
                return true;
            }
        });

        mHandler = new Handler();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        startWearableActivity();

        List<Guide> savedGuides = this.loadSavedGuides();
        displayGuides(savedGuides);

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

    private void openGuide(Guide guide){
        Intent intent = new Intent(this, ShowGuideActivity.class);
        Gson gson = new Gson();
        intent.putExtra("guide", gson.toJson(guide));
        startActivity(intent);
    }

    private List<Guide> loadSavedGuides() {
        List<File> guideFiles = Arrays.asList(this.getFilesDir().listFiles());
        List<Guide> guides = new ArrayList<Guide>(guideFiles.size());
        for (File file : guideFiles) {
            if (file.isFile()) {
                String guideJson = fileToString(file);
                Guide guide = Guide.fromJson(guideJson);
                if (guide.getNumSteps() > 0) {
                    guides.add(guide);
                    sendGuide(FileUtil.getGuideFilename(guide), guideJson);
                    for (Step step : guide.getSteps()) {
                        if (!TextUtils.isEmpty(step.getPhoto())) {
                            new loadPhotoTask(guide, step).execute();
                        }
                    }
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
        PutDataMapRequest dataMap = PutDataMapRequest.create(GUIDE_PATH + "/" + filename);
        dataMap.getDataMap().putString(FILENAME_KEY, filename);
        dataMap.getDataMap().putString(JSON_GUIDE_KEY, jsonGuide);
        PutDataRequest request = dataMap.asPutDataRequest();
        Log.e(TAG, "Generating guide request: " + request);
        PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        Log.e(TAG, "Sending guide was successful: " + dataItemResult.getStatus()
                                .isSuccess());
                    }
                });
    }

    private String getPhotoPath(Guide guide, Step step) {
        return IMAGE_PATH + "/" + guide.getId() + "/" + step.getText().hashCode();
    }

    private void sendPhoto(Bitmap bitmap, Guide guide, Step step) {
        Asset asset = BitmapUtil.toAsset(bitmap);
        PutDataMapRequest dataMap = PutDataMapRequest.create(getPhotoPath(guide, step));
        dataMap.getDataMap().putAsset(IMAGE_KEY, asset);
        dataMap.getDataMap().putLong("time", new Date().getTime());
        PutDataRequest request = dataMap.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient, request)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        Log.e(TAG, "Sending image was successful: " + dataItemResult.getStatus()
                                .isSuccess());
                    }
                });

    }

    private class loadPhotoTask extends AsyncTask<Void, Void, Void> {

        Guide guide;
        Step step;

        public loadPhotoTask(Guide guide, Step step) {
            this.guide = guide;
            this.step = step;
        }
        @Override
        protected Void doInBackground(Void... params) {
            Log.e(TAG, "Loading bitmap for guide: " + guide.getTitle());
            Bitmap bitmap = PhotoUtils.decodeSampledBitmap(step.getPhoto(), 100, 100);
            Log.e(TAG, "Sending photo to watch for guide: " + guide.getTitle());
            sendPhoto(bitmap, guide, step);
            return null;
        }
    }
}
