package com.peterpeterallie.watchandlearnbeta;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import static com.peterpeterallie.watchandlearnbeta.R.drawable.ic_launcher;

/**
 * Created by chepeter on 8/3/14.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    View loading;

    public DownloadImageTask(ImageView bmImage, View loading) {
        this.bmImage = bmImage;
        this.loading = loading;
    }

    @Override
    protected void onPreExecute() {
        loading.setVisibility(View.VISIBLE);
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
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
        loading.setVisibility(View.INVISIBLE);
        bmImage.setImageBitmap(result);
    }
}
