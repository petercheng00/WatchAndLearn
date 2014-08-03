package com.peterpeterallie.watchandlearnbeta.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.peterpeterallie.watchandlearnbeta.DownloadImageTask;
import com.peterpeterallie.watchandlearnbeta.PhotoUtils;
import com.peterpeterallie.watchandlearnbeta.R;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by chepeter on 8/2/14.
 */
public class GuideAdapter extends ArrayAdapter<Guide> {

    Context context;

    int layoutResourceId;

    List<Guide> guides;

    public GuideAdapter(Context context, int resource, List<Guide> objects) {
        super(context, resource, objects);

        this.context = context;
        this.layoutResourceId = resource;
        this.guides = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        GuideHolder holder;

        //TODO: threading is making funny things happen with image loading and view reuse
        //if(row == null)
        //{
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new GuideHolder();
            holder.icon = (ImageView)row.findViewById(R.id.guide_icon);
            holder.title = (TextView)row.findViewById(R.id.guide_title);
            holder.subTitle = (TextView)row.findViewById(R.id.guide_subtitle);

            row.setTag(holder);
        //}
//        else
//        {
//            holder = (GuideHolder)row.getTag();
//        }

        Guide guide = guides.get(position);
        holder.title.setText(guide.getTitle());
        holder.subTitle.setText("Unknown Creator");
        String photoFileName = guide.getPhoto();
        int index = 0;
        while ((photoFileName == null || photoFileName.isEmpty()) && index < guide.getNumSteps()) {
            photoFileName = guide.getStep(index++).getPhoto();
        }
        if (photoFileName != null && photoFileName.contains("http")) {
            new DownloadImageTask(holder.icon).execute(photoFileName);
        }
        else if (photoFileName != null && new File(photoFileName).isFile()) {
            Bitmap bitmap = PhotoUtils.decodeSampledBitmapFromFile(photoFileName, 100, 100);
            holder.icon.setImageBitmap(bitmap);
        }
        return row;
    }

    static class GuideHolder
    {
        ImageView icon;
        TextView title;
        TextView subTitle;
    }
}
