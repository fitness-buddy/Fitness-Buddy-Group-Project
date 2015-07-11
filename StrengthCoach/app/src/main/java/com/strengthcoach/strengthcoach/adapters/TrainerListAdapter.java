package com.strengthcoach.strengthcoach.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.activities.HomeActivity;
import com.strengthcoach.strengthcoach.activities.TrainerDetailsAnimatedActivity;
import com.strengthcoach.strengthcoach.helpers.Constants;
import com.strengthcoach.strengthcoach.models.LocalTrainer;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TrainerListAdapter extends RecyclerView.Adapter<TrainerListAdapter.TrainerViewHolder> {
    private LayoutInflater inflater;
    List<Trainer> trainers;
    Context context;
    private final int LOGIN_FOR_MARKING_FAVORITES = 117;

    public TrainerListAdapter(Context context, List<Trainer> trainers) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.trainers = trainers;
    }

    @Override
    public TrainerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.trainer_list_row, parent, false);
        // Create the viewholder obj
        TrainerViewHolder holder = new TrainerViewHolder(view, new TrainerViewHolder.IMyViewHolderClicks() {

            // Handles clicks on favorite icon
            @Override
            public void favoriteClick(final View view, final Trainer trainer) {
                String currentUserId = ((HomeActivity) context).getLoggedInUserId();
                if (!currentUserId.equals("")) {
                    zoomAnimation(view, trainer);
                    if (SimpleUser.currentUserObject == null) {
                        ParseQuery<SimpleUser> query = ParseQuery.getQuery("SimpleUser");
                        query.whereEqualTo("objectId", currentUserId);
                        query.getFirstInBackground(new GetCallback<SimpleUser>() {
                            @Override
                            public void done(SimpleUser simpleUser, ParseException e) {
                                SimpleUser.currentUserObject = simpleUser;
                                markFavorite(trainer);
                            }
                        });
                    } else {
                        markFavorite(trainer);
                    }
                } else {
                    // User needs to signup
                    zoomAnimation(view, trainer);
                    // Used to later mark the trainer as favorite over the network call
                    ((HomeActivity) context).markedFavorite = trainer;
                    ((HomeActivity) context).launchLoginActivity(LOGIN_FOR_MARKING_FAVORITES);
                }
            }

            public void zoomAnimation(View view, Trainer trainer) {
                ((ImageView) view).setImageResource(0);
                // Give immediate feedback by changing drawable
                if (trainer.isFavorite()) {
                    ((ImageView) view).setImageResource(R.drawable.heart);
                } else {
                    // Mark Favorite
                    ((ImageView) view).setImageResource(R.drawable.heart_selected);
                }
//                Animation animation = AnimationUtils.loadAnimation(context, R.anim.zoom);
//                view.startAnimation(animation);


            }
        });
        return holder;
    }

    public void markFavorite(Trainer trainer) {
        if (trainer.isFavorite()) {
            // If the trainer is already favorited; reset the icon; undo favorite
            trainer.getFavoritedBy().remove(SimpleUser.currentUserObject);
            trainer.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("DEBUG", "Successfully removed favorite trainer");
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            ArrayList<SimpleUser> favorites = trainer.getFavoritedBy();
            if (favorites == null) {
                // This is to handle the case where current user is the one to mark the trainer
                // as favorite
                ArrayList<SimpleUser> favoritedBy = new ArrayList<SimpleUser>();
                favoritedBy.add(SimpleUser.currentUserObject);
                trainer.setFavoritedBy(favoritedBy);
                trainer.saveInBackground();
            } else {
                trainer.getFavoritedBy().add(SimpleUser.currentUserObject);
            }
            trainer.getFavoritedBy().add(SimpleUser.currentUserObject);
            Log.d("DEBUG", "Successfully marked favorite trainer");
            trainer.saveInBackground();
        }
    }

    @Override
    public void onBindViewHolder(TrainerViewHolder holder, int position) {
        final Trainer trainer = trainers.get(position);

        // Initialize ViewPager adapter
        holder.mTrainerListPagerAdapter = new TrainerListPagerAdapter(context, trainer);
        holder.mViewPager.setAdapter(holder.mTrainerListPagerAdapter);

        // Set the profile image
        holder.ivProfileImage.setImageResource(0);
		holder.ivProfileImage.setBorderColor(context.getResources().getColor(R.color.white));
        holder.ivProfileImage.setBorderWidth(Constants.PROFILE_PIC_BORDER_WIDTH);
        holder.ivProfileImage.setBorderOverlay(true);
        Picasso.with(context).load(trainer.getProfileImageUrl()).placeholder(R.drawable.ic_placeholder).into(holder.ivProfileImage);

        // Set the favorite icon
        holder.ivFavorite.setImageResource(0);
        if (trainer.isFavorite()) {
            holder.ivFavorite.setImageResource(R.drawable.heart_selected);
        } else {
            holder.ivFavorite.setImageResource(R.drawable.heart);
        }

        holder.tvTrainerName.setText(trainer.getName());
        holder.tvPrice.setText(trainer.getPriceFormatted());
        holder.tvAboutMe.setText(trainer.getAboutMe());
//        setGymNameAndCity(holder, trainer);
        holder.ratingBar.setRating((float) trainer.getRatings());
        Drawable progress = holder.ratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, context.getResources().getColor(R.color.amber));

        setNumReviews(holder, trainer);
        // Embed the trainer object in the view
        holder.trainer = trainer;
        // Animate item
        animate(holder);
    }

    private void animate(TrainerViewHolder holder) {
        YoYo.with(Techniques.BounceInUp)
                .duration(2000)
                .playOn(holder.itemView);
    }

    private void setNumReviews(TrainerViewHolder holder, Trainer trainer) {
        final TextView tvNumReviews = holder.tvNumReviews;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Review");
        query.whereEqualTo("reviewee", trainer);
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    Log.d("DEBUG", "Number of reviews: " + count);
                    if (count == 1) {
                        tvNumReviews.setText(count + " Review");
                    } else {
                        tvNumReviews.setText(count + " Reviews");
                    }
                } else {
                    Log.d("DEBUG", "Failed to get number of reviews");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return trainers.size();
    }

    // Custom ViewHolder
    static class TrainerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView ivProfileImage;
        TextView tvPrice;
        TextView tvTrainerName;
        TextView tvAboutMe;
        RatingBar ratingBar;
        TextView tvNumReviews;
        ImageView ivFavorite;
        ImageView ivAnimatedHeart;
        public IMyViewHolderClicks mListener;
        private final Context context;
        TrainerListPagerAdapter mTrainerListPagerAdapter;
        ViewPager mViewPager;
        // Bind the trainer object with every viewholder object
        Trainer trainer;

        public TrainerViewHolder(View itemView, IMyViewHolderClicks listener) {
            super(itemView);

            // Get reference to context
            context = itemView.getContext();

            // Get the reference to views
            ivProfileImage = (CircleImageView) itemView.findViewById(R.id.ivProfileImage);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvTrainerName = (TextView) itemView.findViewById(R.id.tvTrainerName);
            tvAboutMe = (TextView) itemView.findViewById(R.id.tvAboutMe);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            tvNumReviews = (TextView) itemView.findViewById(R.id.tvNumReviews);
            ivFavorite = (ImageView) itemView.findViewById(R.id.ivFavorite);
            ivAnimatedHeart = (ImageView) itemView.findViewById(R.id.ivAnimatedHeart);
            mListener = listener;
            // Set the click listeners for the views
            ivFavorite.setOnClickListener(this);
            itemView.setOnClickListener(this);
            // Get the reference to viewpager
            mViewPager = (ViewPager) itemView.findViewById(R.id.pager);
        }

        @Override
        public void onClick(View view) {
            // View specific clicks will be handled here
            if (view == ivFavorite) {
                mListener.favoriteClick(view, trainer);
                YoYo.with(Techniques.TakingOff)
                        .duration(1000).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        ivAnimatedHeart.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ivAnimatedHeart.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                        .playOn(ivAnimatedHeart);
            } else {
                // Launch Trainer details activity
                final Intent intent;
                intent =  new Intent(context, TrainerDetailsAnimatedActivity.class);
                LocalTrainer localTrainer = new LocalTrainer(trainer);
                intent.putExtra("localTrainer", localTrainer);
                // Sending this separately to quickly load the image to support smooth shared element
                // transition
                intent.putExtra("imageUrl", trainer.getImages().get(0));

                // Shared element transition
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((HomeActivity)context, mViewPager, "profilePicture");
                context.startActivity(intent, options.toBundle());
            }

        }

        public interface IMyViewHolderClicks {
            void favoriteClick(View view, Trainer trainer);
        }
    }
}
