package com.strengthcoach.strengthcoach.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.activities.TrainerDetailsAnimatedActivity;
import com.strengthcoach.strengthcoach.models.LocalTrainer;
import com.strengthcoach.strengthcoach.models.Trainer;

public class TrainerListPagerAdapter extends CustomBasePagerAdapter {

    public TrainerListPagerAdapter(Context context, Trainer trainer) {
        super(context, trainer);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = mLayoutInflater.inflate(R.layout.trainer_image, container, false);
        final ImageView ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
        Picasso.with(mContext).load(trainer.getImages().get(position)).placeholder(R.drawable.ic_placeholder).into(ivImage);
        container.addView(itemView);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent;
                intent =  new Intent(mContext, TrainerDetailsAnimatedActivity.class);
                // Sending this separately to quickly load the image to support smooth shared element
                // transition
                intent.putExtra("imageUrl", trainer.getImages().get(position));
                LocalTrainer localTrainer = new LocalTrainer(trainer);
                intent.putExtra("localTrainer", localTrainer);

                // Shared element transition
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity)mContext, ivImage, "profilePicture");
                mContext.startActivity(intent, options.toBundle());
            }
        });

        return itemView;
    }
}
