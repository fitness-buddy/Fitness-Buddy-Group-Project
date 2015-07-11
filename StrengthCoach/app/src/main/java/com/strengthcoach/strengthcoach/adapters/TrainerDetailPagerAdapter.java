package com.strengthcoach.strengthcoach.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.models.Trainer;

public class TrainerDetailPagerAdapter extends CustomBasePagerAdapter {

    public TrainerDetailPagerAdapter(Context context, Trainer trainer) {
        super(context, trainer);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.trainer_image, container, false);
        ImageView ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        Picasso.with(mContext).load(trainer.getImages().get(position)).placeholder(R.drawable.app_icon).into(ivImage);
        container.addView(itemView);
        return itemView;
    }
}
