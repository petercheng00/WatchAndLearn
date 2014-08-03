package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.parser.JsonDeserializer;
import com.peterpeterallie.watchandlearnbeta.service.DataLayerListenerService;
import com.peterpeterallie.watchandlearnbeta.util.AssetsProvider;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity implements WearableListView.ClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener, MessageApi.MessageListener,
        NodeApi.NodeListener {

    private static final String TAG = MyActivity.class.getSimpleName();

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        Guide guide = guideAdapter.getItem(viewHolder.getPosition());
        Toast.makeText(MyActivity.this, "You selected " + guide.getTitle(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, GuideActivity.class);
        intent.putExtra(GuideActivity.FILENAME, "guide1.json");
        startActivity(intent);
    }

    @Override
    public void onTopEmptyRegionClick() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleApiClient, this);
        Wearable.MessageApi.removeListener(mGoogleApiClient, this);
        Wearable.NodeApi.removeListener(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.e(TAG, "onConnected(): Successfully connected to Google API client");
        Wearable.DataApi.addListener(mGoogleApiClient, this);
        Wearable.MessageApi.addListener(mGoogleApiClient, this);
        Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.e(TAG, "onConnectionSuspended(): Connection to Google API client was suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + result);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.e(TAG, "onDataChanged(): " + dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        dataEvents.close();
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                if (DataLayerListenerService.IMAGE_PATH.equals(path)) {
//                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
//                    Asset photo = dataMapItem.getDataMap()
//                            .getAsset(DataLayerListenerService.IMAGE_KEY);
//                    final Bitmap bitmap = loadBitmapFromAsset(mGoogleApiClient, photo);
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d(TAG, "Setting background image..");
//                            mLayout.setBackground(new BitmapDrawable(getResources(), bitmap));
//                        }
//                    });

                } else if (DataLayerListenerService.COUNT_PATH.equals(path)) {
                    Log.e(TAG, "Data Changed for COUNT_PATH");
                } else if (DataLayerListenerService.GUIDE_PATH.equals(path)) {
                    Log.e(TAG, "Data Changed for GUIDE_PATH");
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                    String filename = dataMapItem.getDataMap().getString(DataLayerListenerService.FILENAME_KEY);
                    String jsonGuide = dataMapItem.getDataMap().getString(DataLayerListenerService.JSON_GUIDE_KEY);

                    Log.e(TAG, "jsonGuide Received: " + filename + " : " + jsonGuide);
                } else {
                    Log.e(TAG, "Unrecognized path: " + path);
                }

            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                Log.e(TAG, "Data Changed for TYPE_DELETED");
            } else {
                Log.e(TAG, "Data Changed for ELSE");
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.e(TAG, "onMessageReceived: " + messageEvent);
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.e(TAG, "Peer connected");
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.e(TAG, "Peer Disconnected");
    }

    private List<Guide> getGuides() {
        List<Guide> guides = new ArrayList<Guide>();
        String jsonGuide = AssetsProvider.openFileAsString(this, "guide1.json");
        try {
            guides.add(JsonDeserializer.getGuide(jsonGuide));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        jsonGuide = AssetsProvider.openFileAsString(this, "guide2.json");
        try {
            guides.add(JsonDeserializer.getGuide(jsonGuide));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        jsonGuide = AssetsProvider.openFileAsString(this, "guide3.json");
        try {
            guides.add(JsonDeserializer.getGuide(jsonGuide));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return guides;
    }
}
