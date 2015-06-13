package com.strengthcoach.strengthcoach.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.models.Message;
import com.strengthcoach.strengthcoach.viewholders.ChatItemViewHolder;

import java.util.List;

/**
 * Created by varungupta on 6/8/15.
 */
public class ChatItemAdapter extends ArrayAdapter<Message> {
    public ChatItemAdapter(Context context, List<Message> objects) {
        super(context, R.layout.chat_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message message = getItem(position);

        ChatItemViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, parent, false);
            viewHolder = new ChatItemViewHolder();
            viewHolder.tvChatItem = (TextView)convertView.findViewById(R.id.tvChatItem);
            viewHolder.ivOtherProfileImage = (ImageView) convertView.findViewById(R.id.ivOtherProfileImage);
            viewHolder.ivUserProfileImage = (ImageView) convertView.findViewById(R.id.ivUserProfileImage);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ChatItemViewHolder)convertView.getTag();
        }

        viewHolder.tvChatItem.setText(message.getText());
        return convertView;
    }
}
