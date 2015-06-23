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
import com.strengthcoach.strengthcoach.models.ChatPerson;
import com.strengthcoach.strengthcoach.models.Message;
import com.strengthcoach.strengthcoach.viewholders.ChatItemViewHolder;

import java.util.List;

/**
 * Created by varungupta on 6/8/15.
 */
public class ChatItemAdapter extends ArrayAdapter<Message> {

    ChatPerson m_me;
    ChatPerson m_other;

    public ChatItemAdapter(Context context, List<Message> objects, ChatPerson me, ChatPerson other) {
        super(context, R.layout.chat_item, objects);
        m_me = me;
        m_other = other;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Message message = getItem(position);

        ChatItemViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, parent, false);
            viewHolder = new ChatItemViewHolder();
            viewHolder.tvChatItemLeftAligned = (TextView)convertView.findViewById(R.id.tvChatItemLeftAligned);
            viewHolder.tvChatItemRightAligned = (TextView)convertView.findViewById(R.id.tvChatItemRightAligned);
            viewHolder.ivOtherProfileImage = (ImageView) convertView.findViewById(R.id.ivOtherProfileImage);
            viewHolder.ivUserProfileImage = (ImageView) convertView.findViewById(R.id.ivUserProfileImage);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ChatItemViewHolder)convertView.getTag();
        }

        if (message.getFromObjectId().equals(m_me.objectId)) {
            // Message is from current user
            viewHolder.tvChatItemRightAligned.setVisibility(View.VISIBLE);
            viewHolder.tvChatItemRightAligned.setText(message.getText());

            viewHolder.ivUserProfileImage.setVisibility(View.VISIBLE);
            if (m_me.imageUrl.equals("")) {
                viewHolder.ivUserProfileImage.setImageResource(R.drawable.default_profile_image);
            }
            else {
                Picasso.with(getContext()).load(m_me.imageUrl).into(viewHolder.ivUserProfileImage);
            }

            viewHolder.ivOtherProfileImage.setVisibility(View.INVISIBLE);
            viewHolder.tvChatItemLeftAligned.setVisibility(View.GONE);
        }
        else {
            viewHolder.tvChatItemLeftAligned.setVisibility(View.VISIBLE);
            viewHolder.tvChatItemLeftAligned.setText(message.getText());

            viewHolder.ivOtherProfileImage.setVisibility(View.VISIBLE);
            if (m_other.imageUrl.equals("")) {
                viewHolder.ivOtherProfileImage.setImageResource(R.drawable.default_profile_image);
            }
            else {
                Picasso.with(getContext()).load(m_other.imageUrl).into(viewHolder.ivOtherProfileImage);
            }
            viewHolder.ivUserProfileImage.setVisibility(View.INVISIBLE);
            viewHolder.tvChatItemRightAligned.setVisibility(View.GONE);
        }

        return convertView;
    }
}
