package com.strengthcoach.strengthcoach.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.strengthcoach.strengthcoach.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.strengthcoach.strengthcoach.activities.CartActivity;
import com.strengthcoach.strengthcoach.activities.UpcomingEventsActivity;
import com.strengthcoach.strengthcoach.helpers.Constants;
import com.strengthcoach.strengthcoach.models.BlockedSlots;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;
import com.strengthcoach.strengthcoach.viewholders.CartItemViewHolder;
import java.util.List;

/**
 * Created by Neeraja on 6/12/2015.
 */
public class CartItemsAdapter extends ArrayAdapter<BlockedSlots> {
    String flag;

    public CartItemsAdapter(Context context, List<BlockedSlots> objects, String flag) {
        super(context, R.layout.cart_item, objects);
        this.flag = flag;
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
        String slot = slots.getSlotTime();
        String finalSlot ="";
        if(Integer.valueOf(slot) <= 11)
        {
            finalSlot = Integer.valueOf(slot) + " "+Constants.AM;
        } else if (Integer.valueOf(slot)==12) {
            finalSlot = Integer.valueOf(slot) + " " +Constants.PM;
        } else {
            finalSlot = (Integer.valueOf(slot) - 12) + " "+Constants.PM;
        }
        viewHolder.tvSlotTime.setText(finalSlot);
        viewHolder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUser;
                if (SimpleUser.currentUserObjectId == null){
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
                    currentUser = pref.getString("userId","");
                }else {
                    currentUser = SimpleUser.currentUserObjectId;
                }
                ParseObject trainer = ParseObject.createWithoutData("Trainer", Trainer.currentTrainerObjectId);
                ParseObject user = ParseObject.createWithoutData("SimpleUser", currentUser);
                ParseQuery<ParseObject> query = ParseQuery.getQuery("BlockedSlots");
                query.include("trainer_id");
                query.whereEqualTo("trainer_id", trainer);
                query.whereEqualTo("user_id", user);
                query.whereEqualTo("slot_date", slots.getSlotDate());
                query.whereEqualTo("slot_time", slots.getSlotTime());
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> selected, ParseException e) {
                        if (e == null) {
                            for(ParseObject ss : selected)
                            {
                                ss.deleteInBackground();
                                if(flag=="cart") {
                                    // remove element from arraylist and notifiy adapter about the change
                                    CartActivity.alSlots.remove(position);
                                    CartActivity.adSlots.notifyDataSetChanged();
                                } else {
                                    UpcomingEventsActivity.alSlots.remove(position);
                                    UpcomingEventsActivity.adSlots.notifyDataSetChanged();
                                }
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
