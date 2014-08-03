package com.peterpeterallie.watchandlearnbeta;

import android.content.Context;
import android.support.wearable.view.GridPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.peterpeterallie.watchandlearnbeta.model.Guide;

public class StepsAdapter extends GridPagerAdapter {

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
        return guide.getSteps().size();
    }

    @Override
    protected Object instantiateItem(ViewGroup container, int row, int col) {
        final View view = LayoutInflater.from(context).inflate(R.layout.guide_item, container, false);
        final TextView textView = (TextView) view.findViewById(R.id.textView);

        if (guide.getSteps().size() > 0) {
            textView.setText(guide.getSteps().get(col).getText());
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
