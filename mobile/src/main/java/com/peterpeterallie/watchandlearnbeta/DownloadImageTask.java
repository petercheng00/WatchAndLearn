package com.peterpeterallie.watchandlearnbeta;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.Map;

import static com.peterpeterallie.watchandlearnbeta.R.drawable.ic_launcher;

/**
 * Created by chepeter on 8/3/14.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    View loading;
    Map<String, Bitmap> cache;
    String urldisplay;

    public DownloadImageTask(ImageView bmImage, View loading, Map<String, Bitmap> cache) {
        this.bmImage = bmImage;
        this.loading = loading;
        this.cache = cache;
    }

    @Override
    protected void onPreExecute() {
        bmImage.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
    }

    protected Bitmap doInBackground(String... urls) {
        urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            mIcon11 = PhotoUtils.decodeSampledBitmapFromURL(urldisplay, 100, 100);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
        bmImage.setImageBitmap(result);
        if (cache != null) {
            cache.put(urldisplay, result);
        }
    }
}
