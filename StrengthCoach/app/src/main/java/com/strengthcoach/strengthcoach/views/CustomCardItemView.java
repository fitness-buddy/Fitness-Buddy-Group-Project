package com.strengthcoach.strengthcoach.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.model.CardItemView;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.models.Gym;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.util.List;

public class CustomCardItemView extends CardItemView<CustomCard> {
    ImageView ivImage;
    TextView tvPrice;
    TextView tvTrainerName;
    TextView tvAboutMe;
    TextView tvRating;

    // Default constructors
    public CustomCardItemView(Context context) {
        super(context);
    }

    public CustomCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(CustomCard card) {
        setImage(card.trainer.getProfileImageUrl());
        setName(card.trainer.getName());
        setPrice(card.trainer.getPriceFormatted());
        setAboutMe(card.trainer.getAboutMe());
        setGymNameAndCity(card.trainer);
        setRating(card.trainer.getRatings());
        setNumReviews(card.trainer);
    }

    private void setNumReviews(Trainer trainer) {
        final TextView tvNumReviews = (TextView) findViewById(R.id.tvNumReviews);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.whereEqualTo("reviewee", trainer.getObjectId());
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Number of reviews: " + count);
                    tvNumReviews.setText(count + " Reviews");
                } else {
                    Log.d("DEBUG", "Failed to get number of reviews");
                }
            }
        });
    }

    private void setRating(Double ratings) {
        tvRating = (TextView) findViewById(R.id.tvRating);
        tvRating.setText("Rating: " + ratings.toString());
    }

    private void setGymNameAndCity(Trainer trainer) {
        final TextView tvGymName = (TextView) findViewById(R.id.tvGymName);
        final TextView tvCity = (TextView) findViewById(R.id.tvCity);
        ParseQuery<Gym> query = ParseQuery.getQuery("Gym");
        query.whereEqualTo("trainers", trainer);
        query.include("address");
        query.findInBackground(new FindCallback<Gym>() {
            public void done(List<Gym> gyms, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Retrieved " + gyms.size() + " Gym");
                    Gym gym = gyms.get(0);
                    tvGymName.setText(gym.getName());
                    tvCity.setText(gym.getAddress().getCity());
                } else {
                    Log.d("DEBUG", "Failed to fetch Gym info. Error: " + e.getMessage());
                }
            }
        });
    }

    private void setAboutMe(String aboutMe) {
        tvAboutMe = (TextView) findViewById(R.id.tvAboutMe);
        tvAboutMe.setText(aboutMe);
    }

    private void setPrice(String price) {
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvPrice.setText(price);
    }

    private void setName(String name) {
        tvTrainerName = (TextView) findViewById(R.id.tvTrainerName);
        tvTrainerName.setText(name);
    }

    public void setImage(String imageUrl) {
        ivImage = (ImageView) findViewById(R.id.ivImage);
        Picasso.with(getContext()).load(imageUrl).centerInside().fit().into(ivImage);
    }
}
