package com.peterpeterallie.watchandlearnbeta;

import android.content.Context;
import android.support.wearable.view.GridPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.peterpeterallie.watchandlearnbeta.model.Guide;

import java.util.List;

public class GuideAdapter extends GridPagerAdapter {

    private Context context;
    private List<Guide> guides;

    public GuideAdapter(Context context, List<Guide> guides) {
        this.context = context;
        this.guides = guides;
    }

    @Override
    public int getRowCount() {
        return Math.max(guides.size(), 1);
    }

    @Override
    public int getColumnCount(int i) {
        return 1;
    }

    @Override
    protected Object instantiateItem(ViewGroup container, int row, int col) {
        final View view = LayoutInflater.from(context).inflate(R.layout.guide_item, container, false);
        final TextView textView = (TextView) view.findViewById(R.id.textView);

        if (guides.size() > 0) {
            textView.setText(guides.get(row).getTitle());
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
