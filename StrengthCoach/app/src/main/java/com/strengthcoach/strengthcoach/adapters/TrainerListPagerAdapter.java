package com.strengthcoach.strengthcoach.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.activities.TrainerDetailsActivity;
import com.strengthcoach.strengthcoach.models.Trainer;

public class TrainerListPagerAdapter extends CustomBasePagerAdapter {

    public TrainerListPagerAdapter(Context context, Trainer trainer) {
        super(context, trainer);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.trainer_image, container, false);
        ImageView ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        Picasso.with(mContext).load(trainer.getImages().get(position)).into(ivImage);
        container.addView(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                intent =  new Intent(mContext, TrainerDetailsActivity.class);
                mContext.startActivity(intent);
            }
        });

        return itemView;
    }
}
