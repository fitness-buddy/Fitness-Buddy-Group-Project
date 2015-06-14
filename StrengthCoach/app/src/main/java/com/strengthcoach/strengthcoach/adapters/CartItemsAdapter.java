package com.strengthcoach.strengthcoach.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseObject;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.models.BlockedSlots;
import com.strengthcoach.strengthcoach.models.Message;
import com.strengthcoach.strengthcoach.models.Trainer;
import com.strengthcoach.strengthcoach.viewholders.CartItemViewHolder;
import com.strengthcoach.strengthcoach.viewholders.ChatItemViewHolder;

import java.util.List;

/**
 * Created by Neeraja on 6/12/2015.
 */
public class CartItemsAdapter extends ArrayAdapter<BlockedSlots> {

    public CartItemsAdapter(Context context, List<BlockedSlots> objects) {
        super(context, R.layout.cart_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("BlockedSLots", "position------------------------     "+position);
        BlockedSlots slots = getItem(position);
        Log.v("BlockedSLots", "slots------------------------     "+slots.getSlotTime());
        CartItemViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cart_item, parent, false);
            viewHolder = new CartItemViewHolder();
            viewHolder.tvSerialNo = (TextView)convertView.findViewById(R.id.tvSerialNo);
            viewHolder.tvTrainerName = (TextView)convertView.findViewById(R.id.tvTrainerName);
            viewHolder.tvSlotDate = (TextView)convertView.findViewById(R.id.tvSlotDate);
            viewHolder.tvSlotTime = (TextView)convertView.findViewById(R.id.tvSlotTime);
            viewHolder.ibDelete = (ImageButton)convertView.findViewById(R.id.ibDelete);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (CartItemViewHolder) convertView.getTag();
        }

        viewHolder.tvSerialNo.setText((position+1)+"");
        viewHolder.tvTrainerName.setText(Trainer.currentTrainerName);
        viewHolder.tvSlotDate.setText(slots.getSlotDate());
        viewHolder.tvSlotTime.setText(slots.getSlotTime());
        return convertView;
    }
}
