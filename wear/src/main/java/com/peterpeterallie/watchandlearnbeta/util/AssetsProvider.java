package com.peterpeterallie.watchandlearnbeta.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

public class AssetsProvider {

    public static String openFileAsString(Context context, String filename) {
        AssetManager assetManager = context.getResources().getAssets();
        InputStream inputStream = null;

        try {
            inputStream = assetManager.open(filename);
            if (inputStream != null) {
                String rtn = convertStreamToString(inputStream);
                inputStream.close();
                return rtn;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String convertStreamToString(InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
