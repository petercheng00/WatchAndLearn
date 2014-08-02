package com.peterpeterallie.watchandlearnbeta;

import android.content.Context;
import android.support.wearable.view.GridPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class GuideAdapter extends GridPagerAdapter {

    private Context context;

    public GuideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getRowCount() {
        return 5;
    }

    @Override
    public int getColumnCount(int i) {
        return 5;
    }

    @Override
    protected Object instantiateItem(ViewGroup container, int row, int col) {
        final View view = LayoutInflater.from(context).inflate(R.layout.guide_item, container, false);
        final TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(String.format("Page:\n%1$s, %2$s", row, col));
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
