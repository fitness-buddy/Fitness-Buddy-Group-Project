package com.strengthcoach.strengthcoach.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.util.ArrayList;

public class TrainerDetailsActivity extends ActionBarActivity {

    Trainer m_trainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_details);

        // Creating a fake trainer, Home activity will pass it
        // TODO: Get trainer from Home activity
        m_trainer = createFakeTrainer();

        // Setup view
        ImageView ivTrainerImages = (ImageView) findViewById(R.id.ivTrainerImages);
        Picasso.with(this).load(m_trainer.getImages().get(0)).into(ivTrainerImages);

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        Picasso.with(this).load(m_trainer.getProfileImageUrl()).into(ivProfileImage);

        TextView tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvPrice.setText(m_trainer.getPriceFormatted());

        TextView tvAboutTrainer = (TextView) findViewById(R.id.tvAboutTrainer);
        tvAboutTrainer.setText(m_trainer.getAboutMe());
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
        trainer.setName("Brendon Miller");
        trainer.setAboutMe("Good health is the key to living and enjoying life. If you enjoy the competition that participating in a sport offers or exploring the great Northwest, incorporating fitness and healthy nutritional choices helps each of us achieve our goals and enjoy our passions.\n" +
                "I am a driven, energetic and hardworking individual who has an inner drive to compete in sports and enjoy the great outdoors. Through fitness, I have been able to build physical and mental strength that allow me to compete in ice hockey and motocross and enjoy leisure activities like hiking, snowboarding, kayaking, waterskiing and snowshoeing. \n" +
                "Incorporating fitness into one’s lifestyle offers many rewards, including; improved overall health, increased self-esteem and greater focus. I enjoy the opportunity to work with individuals who are ready to make a change and enjoy the benefits of physical fitness. Together we will build a workout plan that is fun and sustainable.");

        ArrayList<String> educationAndCertifications = new ArrayList<>();
        educationAndCertifications.add("MS in Nutrition and Food Science from San Jose State University");
        educationAndCertifications.add("BodyBuilding Calorie Management System Certification");
        educationAndCertifications.add("CPR Certification");
        educationAndCertifications.add("National Academy of Sports Medicine - Corrective Exercise Specialist");
        educationAndCertifications.add("National Exercise and Sports Trainers Association - Personal Fitness Trainer");
        trainer.setEducationAndCertifications(educationAndCertifications);

        ArrayList<String> interestsAndAchievements = new ArrayList<>();
        interestsAndAchievements.add("Climbed Mount Everest in 2015");
        interestsAndAchievements.add("Completed Silicon Valley Marathon in 2014");
        interestsAndAchievements.add("Completed San Francisco Marathon in 2013");
        interestsAndAchievements.add("Climbed Kilimanjaro in 2011");
        interestsAndAchievements.add("Working towards finishing a century ride");
        trainer.setInterestsAndAchievements(interestsAndAchievements);
        trainer.setPrice(20);

        trainer.setRating(4.8);

        trainer.setProfileImageUrl("http://iptfitness.co.uk/wp-content/uploads/2015/03/Aimee-stevens-personal-trainer.jpg");

        ArrayList<String> images = new ArrayList<>();
        images.add("http://gumbofitness.com/wp-content/uploads/2014/11/header-photo1.jpg");
        images.add("http://gumbofitness.com/wp-content/uploads/2014/11/Depositphotos_10679691_original.jpg");
        trainer.setImages(images);

        return trainer;
    }

    public void onFavoriteClicked(View view) {
        // TODO: Get and set favorite information from trainer or user
        ImageView ivFavorite = (ImageView) findViewById(R.id.ivFavorite);
        ivFavorite.setImageResource(R.drawable.heart_selected);
    }
}
