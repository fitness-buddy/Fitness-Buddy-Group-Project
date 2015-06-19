package com.strengthcoach.strengthcoach.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.models.NavDrawerItem;

import java.util.List;


public class NavDrawerListAdapter extends ArrayAdapter<NavDrawerItem> {
    private static class ViewHolder {
        TextView tvTitle;
        ImageView ivIcon;
    }

    public NavDrawerListAdapter(Context context, List<NavDrawerItem> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = new ViewHolder();
        NavDrawerItem item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_drawer_list, parent, false);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(item.getTitle());
        viewHolder.ivIcon.setImageResource(0);
        Picasso.with(getContext()).load(item.getIcon()).fit().into(viewHolder.ivIcon);
        return convertView;
    }
}
