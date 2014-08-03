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

    public void refresh(List<Guide> guides) {
        this.guides = guides;
        notifyDataSetChanged();
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new WearableListView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.guide_item, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int i) {
        TextView textView = (TextView) viewHolder.itemView.findViewById(R.id.textView);
        if (guides.size() != 0) {
            Guide guide = guides.get(i);
            textView.setText(guide.getTitle());
        } else {
            textView.setText("No Guides");
        }
    }

    public Guide getItem(int position) {
        if (guides.size() == 0) {
            return null;
        }
        return guides.get(position);
    }

    @Override
    public int getItemCount() {
        return Math.max(guides.size(), 1);
    }

    @Override
    public long getItemId(int position) {
        if (guides.get(position) == null) {
            return 0;
        }
        return guides.get(position).getId().hashCode();
    }
}
