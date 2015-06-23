package com.strengthcoach.strengthcoach.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.adapters.TrainerDetailPagerAdapter;
import com.strengthcoach.strengthcoach.models.ChatPerson;
import com.strengthcoach.strengthcoach.models.Gym;
import com.strengthcoach.strengthcoach.models.Review;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TrainerDetailsAnimatedActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    private View mOverlayView;
    private ObservableScrollView mScrollView;
    private TextView mTitleView;
    private View mFab;
    private int mActionBarSize;
    private int mFlexibleSpaceShowFabOffset;
    private int mFlexibleSpaceImageHeight;
    private int mFabMargin;
    private boolean mFabIsShown;
    private RelativeLayout mImageContainer;
    int LOGIN_FOR_CHAT_ACTIVITY_ID = 997;


    Trainer m_trainer;
    ArrayList<Review> reviews;
    GoogleMap m_map;
    Button bBookSlot;
    TrainerDetailPagerAdapter mDetailPagerAdapter;
    ViewPager mViewPager;
    String trainerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_details_animated);

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mFlexibleSpaceShowFabOffset = getResources().getDimensionPixelSize(R.dimen.flexible_space_show_fab_offset);
        mActionBarSize = getActionBarSize();
        mOverlayView = findViewById(R.id.overlay);
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mTitleView = (TextView) findViewById(R.id.title);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mImageContainer = (RelativeLayout) findViewById(R.id.rlImageContainer);

        // Get the trainer object from parse and setup the view
        trainerId = getIntent().getStringExtra("trainerId");
        ParseQuery<Trainer> query = ParseQuery.getQuery("Trainer");
        query.whereEqualTo("objectId", trainerId);
        query.include("favorited_by");
        query.findInBackground(new FindCallback<Trainer>() {
            @Override
            public void done(List<Trainer> list, com.parse.ParseException e) {
                Log.d("DEBUG", ((Trainer) list.get(0)).getName());
                m_trainer = list.get(0);
                mTitleView.setText(m_trainer.getName());
                setTitle(null);
                setupViewPager();

                // Get the gym where the trainer goes to workout
                getTrainerGym();
            }
        });

        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUserId = getLoggedInUserId();
                if (currentUserId.equals("")) {
                    // Start login activity
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivityForResult(intent, LOGIN_FOR_CHAT_ACTIVITY_ID);
                    overridePendingTransition(R.anim.enter_from_bottom, R.anim.stay_in_place);
                }
                else {
                    getCurrentUserAndStartChat(currentUserId);
                }
            }
        });
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);
        ViewHelper.setScaleX(mFab, 0);
        ViewHelper.setScaleY(mFab, 0);

        ScrollUtils.addOnGlobalLayoutListener(mScrollView, new Runnable() {
            @Override
            public void run() {
                mScrollView.scrollTo(0, mFlexibleSpaceImageHeight - mActionBarSize);

                // If you'd like to start from scrollY == 0, don't write like this:
                //mScrollView.scrollTo(0, 0);
                // The initial scrollY is 0, so it won't invoke onScrollChanged().
                // To do this, use the following:
                onScrollChanged(0, false, false);

                // You can also achieve it with the following codes.
                // This causes scroll change from 1 to 0.
                mScrollView.scrollTo(0, 1);
                mScrollView.scrollTo(0, 0);
            }
        });
    }

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    private void getTrainerGym() {
        ParseQuery<Gym> query = ParseQuery.getQuery("Gym");
        query.whereEqualTo("trainers", m_trainer);
        query.include("address");
        query.findInBackground(new FindCallback<Gym>() {
            public void done(List<Gym> gyms, com.parse.ParseException e) {
                // Associate the gym with the trainer
                m_trainer.setGym(gyms.get(0));

                // Get all the reviews for the trainer
                getReviewsAndSetupViews(m_trainer.getObjectId());
            }
        });
    }

    // Reviews are in a separate table so it needs to be fetched separately
    private void getReviewsAndSetupViews(String trainerId) {
        ParseQuery<Review> query = ParseQuery.getQuery("Review");
        query.whereEqualTo("reviewee", m_trainer);
        query.include("reviewer");
        query.include("reviewee");
        query.findInBackground(new FindCallback<Review>() {
            @Override
            public void done(List<Review> reviews, com.parse.ParseException e) {
                TrainerDetailsAnimatedActivity.this.reviews = new ArrayList<Review>(reviews);
                setupTrainerView();
            }
        });
    }

    public void onFavoriteClicked(View view) {
        if (m_trainer.isFavorite()) {
            // If the trainer is already favorited; reset the icon; undo favorite
            ((ImageView) view).setImageResource(0);
            ((ImageView) view).setImageResource(R.drawable.heart);
            zoomAnimation((ImageView) view);
            m_trainer.getFavoritedBy().remove(SimpleUser.currentUserObject);
            m_trainer.saveInBackground(new SaveCallback() {
                @Override
                public void done(com.parse.ParseException e) {
                    if (e == null) {
                        Log.d("DEBUG", "Successfully removed favorite trainer");
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            // Change icon and save in parse
            ((ImageView) view).setImageResource(0);
            ((ImageView) view).setImageResource(R.drawable.heart_selected);
            zoomAnimation((ImageView) view);
            ArrayList<SimpleUser> favorites = m_trainer.getFavoritedBy();
            if (favorites == null) {
                // This is to handle the case where current user is the one to mark the trainer
                // as favorite
                ArrayList<SimpleUser> favoritedBy = new ArrayList<SimpleUser>();
                favoritedBy.add(SimpleUser.currentUserObject);
                m_trainer.setFavoritedBy(favoritedBy);
                m_trainer.saveInBackground();
            } else {
                m_trainer.getFavoritedBy().add(SimpleUser.currentUserObject);
            }
            m_trainer.getFavoritedBy().add(SimpleUser.currentUserObject);
            m_trainer.saveInBackground();
        }
    }

    private void zoomAnimation(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom);
        view.startAnimation(animation);
    }

    private void setupTrainerView() {
        ImageView ivFavorite = (ImageView) findViewById(R.id.ivFavorite);
        if (m_trainer.isFavorite()) {
            ivFavorite.setImageResource(R.drawable.heart_selected);
        } else {
            ivFavorite.setImageResource(R.drawable.heart);
        }

        TextView tvAboutTrainer = (TextView) findViewById(R.id.tvAboutTrainer);
        tvAboutTrainer.setText(m_trainer.getAboutMe());

        TextView tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvPrice.setText(m_trainer.getPriceFormatted());

        TextView tvTrainerEducation = (TextView) findViewById(R.id.tvTrainerEducation);
        String educationAndCertifications = "";
        ArrayList<String> educationAndCertificationsArrayList = m_trainer.getEducationAndCertifications();
        for (int i = 0; i < educationAndCertificationsArrayList.size(); i++) {
            educationAndCertifications +=  educationAndCertificationsArrayList.get(i);

            if (i != educationAndCertificationsArrayList.size() - 1) {
                educationAndCertifications += " &#8226; ";
            }
        }
        tvTrainerEducation.setText(Html.fromHtml(educationAndCertifications));

        TextView tvTrainerInterests = (TextView) findViewById(R.id.tvTrainerInterests);
        String interestsAndAchievements = "";
        ArrayList<String> interestsAndAchievementsArrayList = m_trainer.getInterestsAndAchievements();
        for (int i = 0; i < interestsAndAchievementsArrayList.size(); i++) {
            interestsAndAchievements += interestsAndAchievementsArrayList.get(i);

            if (i != interestsAndAchievementsArrayList.size() - 1) {
                interestsAndAchievements += " &#8226; ";
            }
        }
        tvTrainerInterests.setText(Html.fromHtml(interestsAndAchievements));

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        ratingBar.setRating((float) m_trainer.getRatings());
        Drawable progress = ratingBar.getProgressDrawable();
        DrawableCompat.setTint(progress, Color.parseColor("#FFD700"));

        ImageView ivProfileImage2 = (ImageView) findViewById(R.id.ivProfileImage2);
        Picasso.with(this).load(m_trainer.getProfileImageUrl()).into(ivProfileImage2);

        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    m_map = map;
                    ParseGeoPoint parseGeoPoint = m_trainer.getGym().point();
                    LatLng point = new LatLng(parseGeoPoint.getLatitude(), parseGeoPoint.getLongitude());
                    m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 15));

                    BitmapDescriptor defaultMarker =
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    // Extract content from alert dialog
                    String title = m_trainer.getGym().getName();
                    String snippet = m_trainer.getGym().getAddress().toString();
                    // Creates and adds marker to the map
                    Marker marker = m_map.addMarker(new MarkerOptions()
                            .position(point)
                            .title(title)
                            .snippet(snippet)
                            .icon(defaultMarker));
                    marker.showInfoWindow();
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

        TextView tvReviewsCount = (TextView) findViewById(R.id.tvReviewsCount);
        tvReviewsCount.setText(getReviewsCount());

        LinearLayout llReviews = (LinearLayout) findViewById(R.id.llContent);
        addReviewsInView(llReviews);

        // added by neeraja for booking slots starts
        bBookSlot= (Button) findViewById(R.id.bBookSlot);
        bBookSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Trainer.currentTrainerObjectId = trainerId;
                Trainer.currentTrainerName = m_trainer.getName();
                Trainer.currentPrice  = m_trainer.getPrice();
                Intent intent = new Intent(getBaseContext(), BlockSlotActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_from_bottom, R.anim.stay_in_place);
            }
        });
        // added by neeraja for booking slots ends

    }

    private void setupViewPager() {
        mDetailPagerAdapter = new TrainerDetailPagerAdapter(this, m_trainer);
        mViewPager.setAdapter(mDetailPagerAdapter);
    }

    private void addReviewsInView(LinearLayout llReviews){
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);

            View v = vi.inflate(R.layout.review_item, null);

            TextView tvReviewerName = (TextView) v.findViewById(R.id.tvReviewerName);
            tvReviewerName.setText(((SimpleUser)review.getReviewer()).getName());

            String PATTERN="MMM yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat();
            dateFormat.applyPattern(PATTERN);
            String date = dateFormat.format(review.getCreatedAt());
            TextView tvReviewDate = (TextView) v.findViewById(R.id.tvReviewDate);
            tvReviewDate.setText(date);

            TextView tvReviewText = (TextView) v.findViewById(R.id.tvReviewText);
            tvReviewText.setText(review.getReviewBody());

            llReviews.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }

    private String getReviewsCount() {
        if (reviews.size() == 1) {
            return reviews.size() + " Review";
        } else {
            return reviews.size() + " Reviews";
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        // Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(mImageContainer, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));
//        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

        // Translate FAB
        int maxFabTranslationY = mFlexibleSpaceImageHeight - mFab.getHeight() / 2;
        float fabTranslationY = ScrollUtils.getFloat(
                -scrollY + mFlexibleSpaceImageHeight - mFab.getHeight() / 2,
                mActionBarSize - mFab.getHeight() / 2,
                maxFabTranslationY);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // On pre-honeycomb, ViewHelper.setTranslationX/Y does not set margin,
            // which causes FAB's OnClickListener not working.
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mFab.getLayoutParams();
            lp.leftMargin = mOverlayView.getWidth() - mFabMargin - mFab.getWidth();
            lp.topMargin = (int) fabTranslationY;
            mFab.requestLayout();
        } else {
            ViewHelper.setTranslationX(mFab, mOverlayView.getWidth() - mFabMargin - mFab.getWidth());
            ViewHelper.setTranslationY(mFab, fabTranslationY);
        }

        // Show/hide FAB
        if (fabTranslationY < mFlexibleSpaceShowFabOffset) {
            hideFab();
        } else {
            showFab();
        }
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private void showFab() {
        if (!mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            mFabIsShown = true;
        }
    }

    private void hideFab() {
        if (mFabIsShown) {
            ViewPropertyAnimator.animate(mFab).cancel();
            ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            mFabIsShown = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trainer_details_animated, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCurrentUserAndStartChat(String userObjectId) {
        ParseQuery<SimpleUser> query = ParseQuery.getQuery("SimpleUser");
        query.whereEqualTo("objectId", userObjectId);
        query.getFirstInBackground(new GetCallback<SimpleUser>() {
            public void done(SimpleUser user, com.parse.ParseException e) {
                if (e == null) {
                    ChatPerson me = new ChatPerson();
                    me.name = user.getName();
                    me.objectId = user.getObjectId();
                    me.imageUrl = "";

                    ChatPerson other = new ChatPerson();
                    other.name = m_trainer.getName();
                    other.objectId = m_trainer.getObjectId();
                    other.imageUrl = m_trainer.getProfileImageUrl();

                    Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                    intent.putExtra("me", me);
                    intent.putExtra("other", other);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_from_bottom, R.anim.stay_in_place);
                } else {
                    Log.d("DEBUG", "Error: " + e.getMessage());
                }
            }

        });
    }

    private String getLoggedInUserId() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userId = pref.getString("userId", "");
        return userId;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_FOR_CHAT_ACTIVITY_ID) {
            if(resultCode == RESULT_OK){
                String currentUserId = getLoggedInUserId();
                getCurrentUserAndStartChat(currentUserId);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        finish();
        overridePendingTransition(R.anim.stay_in_place, R.anim.exit_to_right);
    }
}
