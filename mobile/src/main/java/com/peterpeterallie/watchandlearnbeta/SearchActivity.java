package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.model.GuideAdapter;
import com.peterpeterallie.watchandlearnbeta.model.GuideInstructables;
import com.peterpeterallie.watchandlearnbeta.model.GuideWikiHowImport;

import java.io.FileOutputStream;
import java.util.List;

/**
 * Created by chepeter on 8/3/14.
 */
public class SearchActivity extends Activity {

    private ListView savedList;
    private GuideAdapter guideAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        ((TextView)this.findViewById(R.id.saved_title)).setText("Search for Guides");

        savedList = (ListView) this.findViewById(R.id.saved_list);
        savedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Guide guide = guideAdapter.getItem(position);
                saveAndOpenGuide(guide);
            }
        });

        List<Guide> remoteGuides = GuideInstructables.parseInstructablesGuides(this);
        remoteGuides.addAll(GuideWikiHowImport.parseWikiHowGuides(this));
        displayGuides(remoteGuides);
    }

    private void saveAndOpenGuide(Guide guide){
        String json = guide.toJson();
        String filename = FileUtil.getGuideFilename(guide);
        FileOutputStream outputStream;

        // save to local file
        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(json.getBytes());
            outputStream.close();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, ShowGuideActivity.class);
        intent.putExtra("guide", json);
        startActivity(intent);
    }


    private void displayGuides(List<Guide> guides) {
        this.guideAdapter = new GuideAdapter(this, R.layout.saved_guide, guides);
        this.savedList.setAdapter(guideAdapter);
    }

}
