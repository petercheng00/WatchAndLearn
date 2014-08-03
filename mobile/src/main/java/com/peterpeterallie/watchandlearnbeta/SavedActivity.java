package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.model.GuideAdapter;
import com.peterpeterallie.watchandlearnbeta.model.GuideInstructables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chepeter on 8/2/14.
 */
public class SavedActivity extends Activity {

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

        List<Guide> savedGuides = this.loadSavedGuides();
        savedGuides.addAll(GuideInstructables.parseInstructablesGuides(this));
        displayGuides(savedGuides);
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
                Guide g = Guide.fromJson(fileToString(file));
                if (g.getNumSteps() > 0) {
                    guides.add(g);
                }
            }
        }
        return guides;
    }

    public String fileToString (File file) {
        try {
            FileInputStream fis = openFileInput(file.getName());
            StringBuffer fileContent = new StringBuffer("");

            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1)
            {
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
        guideAdapter = new GuideAdapter(this, R.layout.saved_guide, guides);
        this.savedList.setAdapter(guideAdapter);
    }

}
