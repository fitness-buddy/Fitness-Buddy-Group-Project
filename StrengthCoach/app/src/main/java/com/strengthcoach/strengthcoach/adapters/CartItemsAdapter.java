package com.strengthcoach.strengthcoach.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.wallet.Cart;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.activities.CartActivity;
import com.strengthcoach.strengthcoach.helpers.Constants;
import com.strengthcoach.strengthcoach.models.BlockedSlots;
import com.strengthcoach.strengthcoach.models.Message;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;
import com.strengthcoach.strengthcoach.viewholders.CartItemViewHolder;
import com.strengthcoach.strengthcoach.viewholders.ChatItemViewHolder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Neeraja on 6/12/2015.
 */
public class CartItemsAdapter extends ArrayAdapter<BlockedSlots> {

    public CartItemsAdapter(Context context, List<BlockedSlots> objects) {
        super(context, R.layout.cart_item, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final BlockedSlots slots = getItem(position);
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
        viewHolder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseObject trainer = ParseObject.createWithoutData("Trainer", Trainer.currentTrainerObjectId);
                ParseObject user = ParseObject.createWithoutData("SimpleUser", SimpleUser.currentUserObjectId);
                ParseQuery<ParseObject> query = ParseQuery.getQuery("BlockedSlots");
                query.include("trainer_id");
                query.whereEqualTo("trainer_id", trainer);
                query.whereEqualTo("user_id", user);
                query.whereEqualTo("status", Constants.ADD_TO_CART);
                query.whereEqualTo("slot_date", slots.getSlotDate());
                query.whereEqualTo("slot_time", slots.getSlotTime());
                Log.v("delete ", "onclick of delete button");
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> selected, ParseException e) {
                        if (e == null) {
                            for(ParseObject ss : selected)
                            {
                                ss.deleteInBackground();
                                // remove element from arraylist and notifiy adapter about the change
                                CartActivity.alSlots.remove(position);
                                CartActivity.adSlots.notifyDataSetChanged();
                            }
                        } else {
                            //Handle condition here
                        }
                    }
                });
            }
        });
        return convertView;
    }
}
