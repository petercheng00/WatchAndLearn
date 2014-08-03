package com.peterpeterallie.watchandlearnbeta;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.wearable.view.GridPagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peterpeterallie.watchandlearnbeta.model.Guide;
import com.peterpeterallie.watchandlearnbeta.model.Step;
import com.peterpeterallie.watchandlearnbeta.util.BitmapUtil;
import com.peterpeterallie.watchandlearnbeta.util.FileUtil;

import java.io.File;

public class StepsAdapter extends GridPagerAdapter {

    private static final String TAG = "StepsAdapter";

    private Context context;
    private Guide guide;

    public StepsAdapter(Context context, Guide guide) {
        this.context = context;
        this.guide = guide;
    }

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        if (guide == null || guide.getSteps() == null || guide.getSteps().size() == 0) {
            return 1;
        }
        return guide.getSteps().size();
    }

    @Override
    protected Object instantiateItem(ViewGroup container, int row, int col) {
        final View view = LayoutInflater.from(context).inflate(R.layout.step_item, container, false);
        final TextView textView = (TextView) view.findViewById(R.id.textView);


        final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        if (guide.getSteps().size() > 0) {
            Step step = guide.getSteps().get(col);
            textView.setText(step.getText());

            if (!TextUtils.isEmpty(guide.getPhoto())) {
                imageView.setVisibility(View.VISIBLE);
                String filename = FileUtil.getPhotoFileName(guide.getId(), String.valueOf(step.getText().hashCode()));
                Log.e(TAG, "opening image file for step: " + filename);
                String fullFilePath = context.getFilesDir().getPath() + "/" + filename;

                Log.e(TAG, "fullFilePath: " + fullFilePath);
                Log.e(TAG, "exists? " + new File(fullFilePath).exists());

                for (File file : context.getFilesDir().listFiles()) {
                    Log.e(TAG, "file in files dir: " + file.getAbsolutePath());
                }

                Bitmap bitmap = BitmapUtil.openFileAsBitmap(fullFilePath);
                imageView.setImageBitmap(bitmap);
            } else {
                imageView.setVisibility(View.GONE);
            }
        } else {
            imageView.setVisibility(View.GONE);
        }

        container.addView(view);
        return view;
    }

    @Override
    protected void destroyItem(ViewGroup viewGroup, int i, int i2, Object view) {
        viewGroup.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}