package com.strengthcoach.strengthcoach.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.squareup.picasso.Picasso;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.models.Address;
import com.strengthcoach.strengthcoach.models.Gym;
import com.strengthcoach.strengthcoach.models.Review;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TrainerDetailsActivity extends ActionBarActivity {

    Trainer m_trainer;
    GoogleMap m_map;
    Button bBookSlot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_details);

        // Creating a fake trainer, Home activity will pass it
        // TODO: Get trainer from Home activity
        m_trainer = createFakeTrainer();

        // Set toolbar
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(m_trainer.getName());

        ImageView iv_compose_actionbar_cancel = (ImageView) findViewById(R.id.iv_compose_actionbar_cancel);
        iv_compose_actionbar_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Setup view
        ImageView ivTrainerImages = (ImageView) findViewById(R.id.ivTrainerImages);
        Picasso.with(this).load(m_trainer.getImages().get(0)).into(ivTrainerImages);

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        Picasso.with(this).load(m_trainer.getProfileImageUrl()).into(ivProfileImage);

        TextView tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvPrice.setText(m_trainer.getPriceFormatted());

        TextView tvAboutTrainer = (TextView) findViewById(R.id.tvAboutTrainer);
        tvAboutTrainer.setText(m_trainer.getAboutMe());

        TextView tvTrainerEducation = (TextView) findViewById(R.id.tvTrainerEducation);
        String educationAndCertifications = "";
        ArrayList<String> educationAndCertificationsArrayList = m_trainer.getEducationAndCertifications();
        for (int i = 0; i < educationAndCertificationsArrayList.size(); i++) {
            educationAndCertifications += "&#8226; " + educationAndCertificationsArrayList.get(i);

            if (i != educationAndCertificationsArrayList.size() - 1) {
                educationAndCertifications += "<br/>";
            }
        }
        tvTrainerEducation.setText(Html.fromHtml(educationAndCertifications));

        TextView tvTrainerInterests = (TextView) findViewById(R.id.tvTrainerInterests);
        String interestsAndAchievements = "";
        ArrayList<String> interestsAndAchievementsArrayList = m_trainer.getInterestsAndAchievements();
        for (int i = 0; i < interestsAndAchievementsArrayList.size(); i++) {
            interestsAndAchievements += "&#8226; " + interestsAndAchievementsArrayList.get(i);

            if (i != interestsAndAchievementsArrayList.size() - 1) {
                interestsAndAchievements += "<br/>";
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
                    m_map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16));

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
        tvReviewsCount.setText(m_trainer.getReviews().size() + " Reviews");

        LinearLayout llReviews = (LinearLayout) findViewById(R.id.llReviews);
        addReviewsInView(llReviews);

        TextView tvContactTrainer = (TextView) findViewById(R.id.tvContactTrainer);
        tvContactTrainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                intent.putExtra("trainer", m_trainer);
                startActivity(intent);
            }
        });

        // added by neeraja for booking slots starts
        bBookSlot= (Button) findViewById(R.id.bBookSlot);
        bBookSlot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Trainer.currentTrainerObjectId = m_trainer.getObjectId();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        // added by neeraja for booking slots ends
    }

    private void addReviewsInView(LinearLayout llReviews){
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        ArrayList<Review> reviews = m_trainer.getReviews();
        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);

            View v = vi.inflate(R.layout.review_item, null);

            TextView tvReviewerName = (TextView) v.findViewById(R.id.tvReviewerName);
            tvReviewerName.setText(review.getReviewee());

            String PATTERN="MMM yyyy";
            SimpleDateFormat dateFormat=new SimpleDateFormat();
            dateFormat.applyPattern(PATTERN);
            String date = dateFormat.format(review.getDate());
            TextView tvReviewDate = (TextView) v.findViewById(R.id.tvReviewDate);
            tvReviewDate.setText(date);

            TextView tvReviewText = (TextView) v.findViewById(R.id.tvReviewText);
            tvReviewText.setText(review.getReviewBody());

            llReviews.addView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trainer_details, menu);
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

    private Trainer createFakeTrainer() {
        Trainer trainer = new Trainer();
        trainer.setId(1);
        trainer.setName("Brendon Miller");
        trainer.setAboutMe("Whether you want to lose weight and keep it off, build lean muscle, or just look and feel better in the clothes you’re in, I'll help you get there.");

        ArrayList<String> educationAndCertifications = new ArrayList<>();
        educationAndCertifications.add("MS in Nutrition and Food Science from San Jose State University");
        educationAndCertifications.add("Calorie Management System Certification");
        educationAndCertifications.add("CPR Certification");
        educationAndCertifications.add("National Academy of Sports Medicine - Corrective Exercise Specialist");
        educationAndCertifications.add("National Exercise and Sports Trainers Association - Personal Fitness Trainer");
        trainer.setEducationAndCertifications(educationAndCertifications);

        ArrayList<String> interestsAndAchievements = new ArrayList<>();
        interestsAndAchievements.add("Completed Silicon Valley Marathon in 2014");
        interestsAndAchievements.add("Completed San Francisco Marathon in 2013");
        interestsAndAchievements.add("Climbed Kilimanjaro in 2011");
        interestsAndAchievements.add("Working towards finishing a century ride");
        trainer.setInterestsAndAchievements(interestsAndAchievements);
        trainer.setPrice(20);

        trainer.setRating(4.5);

        trainer.setProfileImageUrl("http://iptfitness.co.uk/wp-content/uploads/2015/03/Aimee-stevens-personal-trainer.jpg");

        ArrayList<String> images = new ArrayList<>();
        images.add("http://gumbofitness.com/wp-content/uploads/2014/11/header-photo1.jpg");
        images.add("http://gumbofitness.com/wp-content/uploads/2014/11/Depositphotos_10679691_original.jpg");
        trainer.setImages(images);

        Address address = new Address();
        address = new Address();
        address.setAddressLine1("2550 W El Camino Real");
        address.setAddressLine2("");
        address.setCity("Mountain View");
        address.setState("CA");
        address.setZip("94040");

        Gym gym = new Gym();
        gym.setName("24 hour fitness");
        gym.setAddress(address);
        gym.setLocation(37.364511, -122.031336);
        trainer.setGym(gym);

        ArrayList<Review> reviews = new ArrayList<>();
        Review review1 = new Review();
        review1.setRating(4);
        review1.setReviewee("Bob");
        review1.setDate(strToDate("2015/03/18"));
        review1.setReviewText("Brendon Miller is an awesome trainer. He helped me loose 40 lbs in 4 months.");
        reviews.add(review1);

        Review review2 = new Review();
        review2.setRating(5);
        review2.setReviewee("Alisha");
        review2.setDate(strToDate("2011/11/04"));
        review2.setReviewText("I am working out with Brendon for the last 2 months. She is really friendly and knows how to help someone reach their fitness goals");
        reviews.add(review2);

        trainer.setReviews(reviews);

        return trainer;
    }

    private Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = formatter.parse(strDate);//catch exception
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return date;
    }

    public void onFavoriteClicked(View view) {
        // TODO: Get and set favorite information from trainer or user
        ImageView ivFavorite = (ImageView) findViewById(R.id.ivFavorite);
        ivFavorite.setImageResource(R.drawable.heart_selected);
    }

    // Display the alert that adds the marker
    private void showAlertDialogForPoint(final LatLng point) {
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
}
