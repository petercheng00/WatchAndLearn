package com.peterpeterallie.watchandlearnbeta.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

    private static final String TAG = "FileUtil";

    public static String fileToString(File file, Context context) {
        try {
            FileInputStream fis = context.openFileInput(file.getName());
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

    public static final String IMAGE_PREFIX = "image_";

    public static String getPhotoFileName(String guideId, String stepTextHash) {
        return IMAGE_PREFIX + guideId + stepTextHash + ".png";
    }

    public static void saveBitmapToFile(Bitmap bitmap, String path, Context context) {
        Log.e(TAG, "Path: " + path);
        String guideId = path.substring(7, 43);
        String stepTextHash = path.substring(44, path.length());
        String filename = getPhotoFileName(guideId, stepTextHash);

        FileOutputStream out = null;
        try {
            Log.e(TAG, "Saving image file: " + filename);
            out = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "saving image file failed: " + filename);
        } finally {
            try{
                if (out != null) out.close();
            } catch(Throwable ignore) {}
        }
    }
}
