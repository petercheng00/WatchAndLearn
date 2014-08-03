package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.parser.JsonDeserializer;
import com.peterpeterallie.watchandlearnbeta.service.DataLayerListenerService;
import com.peterpeterallie.watchandlearnbeta.util.BitmapUtil;
import com.peterpeterallie.watchandlearnbeta.util.FileUtil;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyActivity extends Activity implements WearableListView.ClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {

    private static final String TAG = "MyActivity";
    private static final String FILENAME_PREFIX = "guide_";

    private WearableListView listView;
    private GuideAdapter guideAdapter;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                listView = (WearableListView) stub.findViewById(R.id.listView);
                listView.setClickable(true);

                guideAdapter = new GuideAdapter(MyActivity.this, getGuides());
                listView.setAdapter(guideAdapter);
                listView.setClickListener(MyActivity.this);
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this.getApplicationContext())
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Guide guide = guideAdapter.getItem(viewHolder.getPosition());

        if (guide != null) {
            Intent intent = new Intent(this, GuideActivity.class);
            intent.putExtra(GuideActivity.FILENAME, FILENAME_PREFIX + guide.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.e(TAG, "Connected to Google Api Service");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.e(TAG, "DataItem deleted: " + event.getDataItem().getUri());
            } else if (event.getType() == DataEvent.TYPE_CHANGED) {
                Log.e(TAG, "DataItem changed: " + event.getDataItem().getUri());
                String path = event.getDataItem().getUri().getPath();
                if (!TextUtils.isEmpty(path) && path.startsWith(DataLayerListenerService.GUIDE_PATH)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    String filename = dataMapItem.getDataMap().getString(DataLayerListenerService.FILENAME_KEY);
                    String jsonGuide = dataMapItem.getDataMap().getString(DataLayerListenerService.JSON_GUIDE_KEY);
                    Log.e(TAG, "Guide updated: " + filename + " : " + jsonGuide);
                    saveJsonGuide(filename, jsonGuide);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            refresh();
                        }
                    });
                } else if (!TextUtils.isEmpty(path) && path.startsWith(DataLayerListenerService.IMAGE_PATH)) {
                    Log.e(TAG, "Image received!: " + path);
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    Asset photo = dataMapItem.getDataMap()
                            .getAsset(DataLayerListenerService.IMAGE_KEY);
                    final Bitmap bitmap = BitmapUtil.loadBitmapFromAsset(mGoogleApiClient, photo);
                    FileUtil.saveBitmapToFile(bitmap, path, this);
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended(): Connection to Google API client was suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + connectionResult);
    }

    private void saveJsonGuide(String filename, String jsonGuide) {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(jsonGuide.getBytes());
            outputStream.close();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refresh() {
        guideAdapter.refresh(getGuides());
    }

    private List<Guide> getGuides() {
        List<File> guideFiles = Arrays.asList(this.getFilesDir().listFiles());
        List<Guide> guides = new ArrayList<Guide>(guideFiles.size());
        for (File file : guideFiles) {
            if (file.isFile() && file.getName().startsWith("guide_")) {
                try {
                    Guide guide = JsonDeserializer.getGuide(FileUtil.fileToString(file, this));
                    if (guide.getSteps().size() > 0) {
                        guides.add(guide);
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return guides;
    }
}
