package com.peterpeterallie.watchandlearnbeta;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.model.GuideAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chepeter on 8/2/14.
 */
public class SavedActivity extends Activity {

    private ListView savedList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        savedList = (ListView) this.findViewById(R.id.saved_list);

        List<Guide> savedGuides = this.loadSavedGuides();
        displayGuides(savedGuides);
    }

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
        GuideAdapter ga = new GuideAdapter(this, R.layout.saved_guide, guides);
        this.savedList.setAdapter(ga);
    }

}
