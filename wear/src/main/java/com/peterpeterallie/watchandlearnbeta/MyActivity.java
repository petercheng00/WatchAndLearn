package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.widget.Toast;

import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.parser.JsonDeserializer;
import com.peterpeterallie.watchandlearnbeta.util.AssetsProvider;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity implements WearableListView.ClickListener  {

    private static final String TAG = MyActivity.class.getSimpleName();

    private WearableListView listView;
    private GuideAdapter guideAdapter;

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
