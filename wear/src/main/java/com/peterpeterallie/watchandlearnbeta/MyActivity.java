package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.parser.JsonDeserializer;
import com.peterpeterallie.watchandlearnbeta.util.AssetsProvider;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {

    private static final String TAG = MyActivity.class.getSimpleName();

    private GridViewPager gridViewPager;
    private GuideAdapter guideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                gridViewPager = (GridViewPager) stub.findViewById(R.id.gridViewPager);
                gridViewPager.setClickable(true);

                guideAdapter = new GuideAdapter(MyActivity.this, getGuides());
                gridViewPager.setAdapter(guideAdapter);
                gridViewPager.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e(TAG, "Click: " + v);
                        Toast.makeText(MyActivity.this, "You've selected!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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
