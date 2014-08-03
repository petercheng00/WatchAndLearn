package com.peterpeterallie.watchandlearnbeta.model;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.peterpeterallie.watchandlearnbeta.PhotoUtils;
import com.peterpeterallie.watchandlearnbeta.R;

import java.io.File;
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
        View row = convertView;
        GuideHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new GuideHolder();
            holder.icon = (ImageView)row.findViewById(R.id.guide_icon);
            holder.title = (TextView)row.findViewById(R.id.guide_title);
            holder.subTitle = (TextView)row.findViewById(R.id.guide_subtitle);

            row.setTag(holder);
        }
        else
        {
            holder = (GuideHolder)row.getTag();
        }

        Guide guide = guides.get(position);
        holder.title.setText(guide.getTitle());
        holder.subTitle.setText("date created should go here");
        String photoFileName = guide.getPhoto();
        if (photoFileName == null || photoFileName.isEmpty()) {
            photoFileName = guide.getStep(0).getPhoto();
        }
        Bitmap bitmap = PhotoUtils.decodeSampledBitmapFromFile(photoFileName, 100, 100);
        holder.icon.setImageBitmap(bitmap);
        return row;
    }

    static class GuideHolder
    {
        ImageView icon;
        TextView title;
        TextView subTitle;
    }
}
