package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
                guideAdapter = new GuideAdapter(MyActivity.this);
                gridViewPager.setAdapter(guideAdapter);
                gridViewPager.setClickable(true);
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
}
