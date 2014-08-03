package com.peterpeterallie.watchandlearnbeta;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.peterpeterallie.watchandlearnbeta.model.Guide;

import java.util.List;

public class GuideAdapter extends WearableListView.Adapter {

    private Context context;
    private List<Guide> guides;

    public GuideAdapter(Context context, List<Guide> guides) {
        this.context = context;
        this.guides = guides;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new WearableListView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.guide_item, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
        Guide guide = guides.get(i);
        TextView textView = (TextView) viewHolder.itemView.findViewById(R.id.textView);
        textView.setText(guide.getTitle());
    }

    public Guide getItem(int position) {
        return guides.get(position);
    }

    @Override
    public int getItemCount() {
        return guides.size();
    }

    @Override
    public long getItemId(int position) {
        return guides.get(position).getId().hashCode();
    }
}
