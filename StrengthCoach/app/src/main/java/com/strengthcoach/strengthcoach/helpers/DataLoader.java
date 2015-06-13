package com.strengthcoach.strengthcoach.helpers;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.strengthcoach.strengthcoach.models.Address;
import com.strengthcoach.strengthcoach.models.Gym;
import com.strengthcoach.strengthcoach.models.Review;
import com.strengthcoach.strengthcoach.models.SimpleUser;
import com.strengthcoach.strengthcoach.models.Trainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class DataLoader {
    Address address;
    Gym gym;
    Trainer trainer;
    SimpleUser user;

    public void populate() {
        user = new SimpleUser();
        user.setPhoneNumber("555-555-5555");
        user.setName("Mickey Mouse");
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    // Saved successfully.
                    Log.d("DEBUG", "User update saved!");
                    String id = user.getObjectId();
                    Log.d("DEBUG", "The object id is: " + id);
                    instantiateAddress();
                    instantiateTrainer();
                    instantiateGym();
                } else {
                    // The save failed.
                    Log.d("DEBUG", "User update error: " + e);
                }
            }
        });
    }

    private void instantiateTrainer() {
        String profileImageUrl = "http://img05.deviantart.net/8d7f/i/2013/149/c/b/south_park_action_poses___kenny_18_by_megasupermoon-d670y5f.jpg";
        String image1 = "https://developer.cdn.mozilla.net/media/uploads/demos/d/a/daniel.moura/5518edae24034cecedeb89bf3c1db5c2/1370528531_screenshot_1.png";
        String image2 = "http://imgur.com/gallery/xfzQez6";
        trainer = new Trainer();
        trainer.setName("Mike Chang");
        trainer.setAboutMe("I am the best!!");
        trainer.setPhoneNumber("222-222-2222");
        ArrayList<SimpleUser> clients = new ArrayList<>();
        clients.add(user);
        trainer.setClients(clients);
        trainer.setRating(5);
        trainer.setProfileImageUrl(profileImageUrl);
        ArrayList<String> images = new ArrayList<>();
        images.add(image1);
        images.add(image2);
        trainer.setImages(images);
    }

    private void instantiateGym() {
        gym = new Gym();
        gym.setName("24 Hour Fitness");
        gym.setAddress(address);
        gym.setLocation(37.404324, -122.108046);
        ArrayList<Trainer> trainers = new ArrayList<>();
        trainers.add(trainer);
        gym.setTrainers(trainers);
        gym.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                } else {
                    instantiateReview();
                }
            }
        });
    }

    // Creates an address object and saves in Parse cloud
    private void instantiateAddress() {
        address = new Address();
        address.setAddressLine1("2550 W El Camino Real");
        address.setAddressLine2("");
        address.setCity("Mountain View");
        address.setState("CA");
        address.setZip("94040");
    }


    public void addZombieTrainersToNewGym() {
        // Get all trainers
        ParseQuery<Trainer> query = ParseQuery.getQuery("Trainer");
        query.findInBackground(new FindCallback<Trainer>() {
            public void done(List<Trainer> objects, ParseException e) {
                Log.d("DEBUG", "Count of ALL trainers: " + objects.size());
                instantiateOneGymAndAddAllZombieTrainers(new ArrayList<Trainer>(objects));
            }
        });
    }

    private void instantiateOneGymAndAddAllZombieTrainers(final ArrayList<Trainer> allTrainers) {
        gym = new Gym();
        address = new Address();
        address.setAddressLine1("2624 Fayette Dr # D");
        address.setAddressLine2("");
        address.setCity("Mountain View");
        address.setState("CA");
        address.setZip("94040");
        final HashMap<String, String> trainerMap = new HashMap<>();
        address.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                gym.setName("Integrate Performance Fitness");
                gym.setAddress(address);
                gym.setLocation(37.404197, -122.112924);
                // Get the trainers which are already added to the other gym
                ParseQuery<Gym> query = ParseQuery.getQuery("Gym");
                query.whereEqualTo("objectId", "dKTaTlOuRw");
                query.findInBackground(new FindCallback<Gym>() {
                    public void done(List<Gym> gyms, ParseException e) {
                        ArrayList<Trainer> existingTrainers = gyms.get(0).getTrainers();
                        Log.d("DEBUG", "Count of existing trainers: " + existingTrainers.size());
                        // Create a hashmap of existing trainers
                        for (Trainer existingTrainer : existingTrainers) {
                            trainerMap.put(existingTrainer.getObjectId(), existingTrainer.getName());
                        }

                        ArrayList<Trainer> nonDuplicateTrainers = new ArrayList<Trainer>();
                        for (Trainer eachTrainer : allTrainers) {
                            if (!trainerMap.containsKey(eachTrainer.getObjectId())) {
                                nonDuplicateTrainers.add(eachTrainer);
                            }
                        }
                        gym.setTrainers(nonDuplicateTrainers);
                        Log.d("DEBUG", "Count of newly added zombie trainers: " + nonDuplicateTrainers.size());

                        gym.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                // Get all trainers and add to this gym
                                if (e == null) {
                                    Log.d("DEBUG", "Successfully added all trainers to gym");
                                } else {
                                    Log.d("DEBUG", "Failed to add all trainers to gym");
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void instantiateReview() {
        Review review = new Review();
        review.setReviewer(user.getObjectId());
        review.setReviewee(trainer.getObjectId());
        review.setRating(4);
        review.setReviewText("I had a great time with Mike  --Mickey");
        review.saveInBackground();
    }

    // Populates genuine data in Parse
    public void PopulateDataInParse() {
        ParseQuery<Trainer> query = ParseQuery.getQuery("Trainer");
        query.findInBackground(new FindCallback<Trainer>() {
            public void done(List<Trainer> objects, ParseException e) {
                updateTrainerImages(objects);
            }
        });
    }

    private void updateTrainerImages(List<Trainer> trainers) {
        ArrayList<String> profilepics = new ArrayList<>();
        ArrayList<String> trainerNames = new ArrayList<>();

        // AMANDA BISK
        trainerNames.add("Amanda Bisk");
        ArrayList<String> images1 = new ArrayList<>();
        images1.add("http://muuve.com/wp-content/uploads/2014/11/Amanda-Bisk-8.jpg");
        images1.add("http://media3.popsugar-assets.com/files/2014/06/10/145/n/4852708/f1c38da84153ed4d_10349603_325227150935915_55048470_n.xxxlarge/i/Amanda-Bisk-making-most-sunshine.jpg");
        images1.add("https://s-media-cache-ak0.pinimg.com/736x/c2/43/39/c243398382fb8c1e11efdef4a4ec5c82.jpg");
        images1.add("http://www.trimmedandtoned.com/wp-content/uploads/2014/07/amandabisk-33.jpg");

        // Neal Maddox
        trainerNames.add("Neal Maddox");
        ArrayList<String> images2 = new ArrayList<>();
        images2.add("http://thh.brandtopix.netdna-cdn.com/wp-content/uploads/2014/11/nealmaddox.jpg");
        images2.add("https://scontent.cdninstagram.com/hphotos-xfa1/t51.2885-15/e15/11005019_881901125201126_1363370091_n.jpg");
        images2.add("http://www.isaac-payne.com/wp-content/uploads/2014/12/neal-maddox.jpg");
        images2.add("http://theathleticbuild.com/wp-content/uploads/2014/03/1932396_10152298672233130_176684053_n.jpg");

        // NATALIE UHLING
        trainerNames.add("Natalie Uhling");
        ArrayList<String> images3 = new ArrayList<>();
        images3.add("https://s-media-cache-ak0.pinimg.com/736x/d9/ce/05/d9ce058c9f160d2ca0a021cb664873cc.jpg");
        images3.add("http://static1.squarespace.com/static/5330664de4b0c8441aea50d8/t/5509e3b5e4b0427e9ff24ada/1426711478620/Hipsters+of+the+Brooklyn+Half");
        images3.add("https://scontent.cdninstagram.com/hphotos-xfa1/t51.2885-15/e15/11247122_854858087900543_965271064_n.jpg");

        // Lazar Angelov
        trainerNames.add("Lazar Angelov");
        ArrayList<String> images4 = new ArrayList<>();
        images4.add("https://scontent.cdninstagram.com/hphotos-xaf1/t51.2885-15/e15/11007970_1588384734708143_1801622793_n.jpg");
        images4.add("http://photos-f.ak.instagram.com/hphotos-ak-xaf1/t51.2885-15/10852990_910938148917653_1362364273_n.jpg");
        images4.add("http://scontent.cdninstagram.com/hphotos-xpf1/t51.2885-15/e15/10471894_1415741042064679_367634735_n.jpg");

        // Mateus Verdelho
        trainerNames.add("Mateus Verdelho");
        ArrayList<String> images5 = new ArrayList<>();
        images5.add("https://s-media-cache-ak0.pinimg.com/736x/06/46/c7/0646c7671ba1328d86efb684a306a206.jpg");
        images5.add("http://www.ofuxico.com.br/img/galeria/2013/12/186846.jpg");
        images5.add("http://s2.glbimg.com/Hau_vNIbu4_NtyYQlf8tDPp9Gy01369R_U0B7tFyud9Ioz-HdGixxa_8qOZvMp3w/e.glbimg.com/og/ed/f/original/2013/12/12/3a0b2646639211e39f7d12fefc9790e3_8.jpg");
        images5.add("http://4.bp.blogspot.com/-r_qLYAnffpM/VEWQ9ekhNCI/AAAAAAABT_o/-_ydkDqShTI/s1600/535908_482105068597827_6164283456221712743_n.jpg");

        // Tobias Sorensen
        trainerNames.add("Tobias Sorensen");
        ArrayList<String> images6 = new ArrayList<>();
        images6.add("http://image.thefashionisto.com/wp-content/uploads/2014/08/Tobias-Sorensen.jpg");
        images6.add("http://image.thefashionisto.com/wp-content/uploads/2014/10/Tobias-Sorensen.jpg");
        images6.add("http://41.media.tumblr.com/228ffc67796055f40869e597af8bbf75/tumblr_n459n0DiEc1rb85ddo1_1280.jpg");

        // Kiana Tom
        trainerNames.add("Kiana Tom");
        ArrayList<String> images7 = new ArrayList<>();
        images7.add("http://media.kaigaidrama.jp/images/0300001488_94YSnKdkr3Rs8wnqEGD9z0mE_s.jpg");
        images7.add("http://www.kiana.com/wp-content/uploads/10899536_554554324682113_382356206_n.jpg");
        images7.add("http://www.kiana.com/wp-content/uploads/10848108_411467045677556_1629011863_n.jpg");

        // Jamie Eason
        trainerNames.add("Jamie Eason");
        ArrayList<String> images8 = new ArrayList<>();
        images8.add("https://healthandfitnessdm.files.wordpress.com/2014/12/jamie-eason.jpg");
        images8.add("https://s-media-cache-ak0.pinimg.com/originals/2d/02/1c/2d021c91f2fd1e77faee498cb5ab6358.jpg");
        images8.add("https://s-media-cache-ak0.pinimg.com/736x/a7/72/a5/a772a5356a2a42944eba00b45e4caa54.jpg");
        images8.add("https://s-media-cache-ak0.pinimg.com/736x/6e/dc/d2/6edcd2138dacf56b877fccdaf5e24aec.jpg");

        // Lucas Bernardini
        trainerNames.add("Lucas Bernardini");
        ArrayList<String> images9 = new ArrayList<>();
        images9.add("https://s-media-cache-ak0.pinimg.com/736x/a8/ef/59/a8ef593b6feb75a5c610986bb3804774.jpg");
        images9.add("http://3.bp.blogspot.com/-_RNcf2_Gu00/VO6TjKdbdeI/AAAAAAABk70/uZGsrEP3lEk/s1600/Unknown-26.jpe");
        images9.add("http://www.underwearexpert.com/wp-content/uploads/2013/11/Lucas-Bernardini.jpg?f96d38");
        images9.add("http://www.underwearexpert.com/wp-content/uploads/2013/11/Lucas-Bernardini2.jpg?f96d38");

        // Ava Cowan
        trainerNames.add("Ava Cowan");
        ArrayList<String> images10 = new ArrayList<>();
        images10.add("https://s-media-cache-ak0.pinimg.com/736x/ed/e2/f2/ede2f2ade968d8bbbacc36b0185827aa.jpg");
        images10.add("http://media2.whosaystatic.com/234430/234430_640x640wc.jpg");
        images10.add("http://media2.whosaystatic.com/168814/1/168814_640x640wc.jpg");

        // Marlon Texeira
        trainerNames.add("Marlon Texeira");
        ArrayList<String> images11 = new ArrayList<>();
        images11.add("http://scontent-a.cdninstagram.com/hphotos-xfa1/t51.2885-15/e15/10995221_537485156354244_650136307_n.jpg");
        images11.add("http://image.thefashionisto.com/wp-content/uploads/2014/05/Juan-Betancourt1.jpg");
        images11.add("http://www.ofuxico.com.br/img/galeria/2014/12/244538.jpg");
        images11.add("https://scontent.cdninstagram.com/hphotos-xaf1/t51.2885-15/e15/11055768_433822633462314_211037265_n.jpg");

        // carson calhoun
        trainerNames.add("Carson Calhoun");
        ArrayList<String> images12 = new ArrayList<>();
        images12.add("http://photos-a.ak.instagram.com/hphotos-ak-xaf1/10748280_1501376653456368_1089551998_n.jpg");
        images12.add("http://scontent-b.cdninstagram.com/hphotos-xfa1/t51.2885-15/10725197_648112435306621_1172805169_n.jpg");
        images12.add("https://scontent.cdninstagram.com/hphotos-xap1/t51.2885-15/e15/10561085_1591111471132686_1284696806_n.jpg");
        images12.add("http://www.igmodelsearch.com/wp-content/uploads/2014/09/carsonclaycalhoun.jpg");


        // Elisabeth Akinwale
        trainerNames.add("Elisabeth Akinwale");
        ArrayList<String> images13 = new ArrayList<>();
        images13.add("https://phattlife.files.wordpress.com/2014/08/elizabeth-akinwale.jpg");
        images13.add("https://scontent.cdninstagram.com/hphotos-xaf1/l/t51.2885-15/e15/11111368_771337902974379_148068983_n.jpg");
        images13.add("http://distilleryimage0.s3.amazonaws.com/c8bf4cb6991411e3b67e0e5d8cd45880_8.jpg");
        images13.add("https://scontent.cdninstagram.com/hphotos-xaf1/t51.2885-15/e15/11377606_451349081706865_45407388_n.jpg");

        // Alicia Marie
        trainerNames.add("Alicia Marie");
        ArrayList<String> images14 = new ArrayList<>();
        images14.add("https://s-media-cache-ak0.pinimg.com/736x/01/47/04/0147044280dfb342daea5d80855b4e80.jpg");
        images14.add("https://s-media-cache-ak0.pinimg.com/736x/87/bf/3f/87bf3fe9afabecb29398d9df534cbaff.jpg");

        // Izabel Goulart
        trainerNames.add("Izabel Goulart");
        ArrayList<String> images15 = new ArrayList<>();
        images15.add("http://41.media.tumblr.com/8833fecbf371609514e3e7d494afdb53/tumblr_no1bnuI9Qk1tg2b2uo1_1280.jpg");
        images15.add("http://www.fashiongonerogue.com/wp-content/uploads/2014/03/izabel-gym.jpg");
        images15.add("http://www.fashiongonerogue.com/wp-content/uploads/2014/06/izabel-selfie-weights.jpg");
        images15.add("http://www.fashiongonerogue.com/wp-content/uploads/2014/06/izabel-isometric-training.jpg");

        // tracy anderson method
        trainerNames.add("Tracy Anderson");
        ArrayList<String> images16 = new ArrayList<>();
        images16.add("https://s-media-cache-ak0.pinimg.com/736x/a7/5b/14/a75b144156e3d9700178109bf22cbe21.jpg");
        images16.add("https://fittyblog.files.wordpress.com/2015/05/928811_686636271465329_1439380661_n.jpg");
        images16.add("https://s-media-cache-ak0.pinimg.com/736x/72/66/32/726632e21986ebc1126a9b3ad519cae0.jpg");

        // Jason Khalipa
        trainerNames.add("Jason Khalipa");
        ArrayList<String> images17 = new ArrayList<>();
        images17.add("http://36.media.tumblr.com/3140b50c223e65889fae4778f8b9749b/tumblr_nc4kikKohm1sfyn6go1_1280.jpg");
        images17.add("http://41.media.tumblr.com/898292b681157df383b3ce702af00766/tumblr_n14bolimW71sfyn6go1_1280.jpg");
        images17.add("http://41.media.tumblr.com/4e4625c7daa569c3373f76214f6f7786/tumblr_nax403Jwer1sfb91wo1_1280.jpg");
        images17.add("http://leilifts.com/wp-content/uploads/2014/12/1972507_1014097268606918_2461957236029205452_n.jpg");

        // michael thurston
        trainerNames.add("Michael Thurston");
        ArrayList<String> images18 = new ArrayList<>();
        images18.add("http://4.bp.blogspot.com/-8mOE39HZF7A/U4qXY-BszHI/AAAAAAABAqg/6ZpuA0krveo/s1600/Michael+Thurston+2+(4).jpg");
        images18.add("https://s-media-cache-ak0.pinimg.com/736x/03/a2/26/03a226a28d55429a0a56bbc289e78a32.jpg");
        images18.add("https://scontent.cdninstagram.com/hphotos-xaf1/t51.2885-15/e15/11033022_926830127356782_429873230_n.jpg");
        images18.add("http://facegram.io/check_picture.php?m_id=53ae03ce8ead0e66548cefdd&u_n=broosk_saib&u_id=260816240&url=http://scontent-a.cdninstagram.com/hphotos-xpf1/t51.2885-15/10432051_1507249562820923_947225113_n.jpg");

        // Cassey Ho
        trainerNames.add("Cassey Ho");
        ArrayList<String> images19 = new ArrayList<>();
        images19.add("https://s-media-cache-ak0.pinimg.com/736x/9f/71/e5/9f71e57e86fb5090b2523f779d68c30b.jpg");
        images19.add("https://asimplesouthernlife.files.wordpress.com/2014/09/blogilates.jpg");
        images19.add("https://femmatouse.files.wordpress.com/2013/10/cassey-ho.jpg");
        images19.add("https://s-media-cache-ak0.pinimg.com/736x/6b/da/eb/6bdaeb216c85de8066c9918e6bf84f97.jpg");

        // Dom Mazzetti
        trainerNames.add("Don Mazzetti");
        ArrayList<String> images20 = new ArrayList<>();
        images20.add("http://40.media.tumblr.com/39764e9d817b9210299ede396877d010/tumblr_n9svrgdksm1s07p0co1_1280.jpg");
        images20.add("https://pbs.twimg.com/profile_images/378800000612214338/e76d2d7f5b011953dfac2b6a16ac1a3d.jpeg");
        images20.add("http://scontent-b.cdninstagram.com/hphotos-xpf1/t51.2885-15/1172177_619916874732085_250317989_n.jpg");
        images20.add("http://scontent.cdninstagram.com/hphotos-xaf1/t51.2885-15/e15/11049433_401623796666317_1808272087_n.jpg");

        ArrayList<ArrayList<String>> data = new ArrayList<>();
        data.add(images1);
        data.add(images2);
        data.add(images3);
        data.add(images4);
        data.add(images5);
        data.add(images6);
        data.add(images7);
        data.add(images8);
        data.add(images9);
        data.add(images10);
        data.add(images11);
        data.add(images12);
        data.add(images13);
        data.add(images14);
        data.add(images15);
        data.add(images16);
        data.add(images17);
        data.add(images18);
        data.add(images19);
        data.add(images20);

        for (int i = 0; i < data.size(); i++) {
            Trainer trainer = trainers.get(i);
            trainer.setImages(data.get(i));
            trainer.setName(trainerNames.get(i));
            trainer.saveInBackground();
        }
    }



    // Returns an arraylist of 3 certifications that are randomly picked from the list.
    private ArrayList<String> getCertifcationsAndEducation() {
        ArrayList<String> educationAndCertifications = new ArrayList<>();
        educationAndCertifications.add("MS in Nutrition and Food Science from San Jose State University");
        educationAndCertifications.add("Calorie Management System Certification");
        educationAndCertifications.add("CPR Certification");
        educationAndCertifications.add("National Academy of Sports Medicine - Corrective Exercise Specialist");
        educationAndCertifications.add("National Exercise and Sports Trainers Association - Personal Fitness Trainer");
        educationAndCertifications.add("Several years of experience in strength training and professional body building");

        int index = 0;
        int min = 0;
        int max = 5;

        ArrayList<String> result = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            index = randInt(min, max);
            result.add(educationAndCertifications.get(index));
            if ((min + 1) < max) {
                min++;
            } else {
                min--;
            }
        }
        return result;
    }

    // Returns an arraylist of 3 interesets that are randomly picked from the list.
    private ArrayList<String> getInterestsAndAchievements() {
        ArrayList<String> interestsAndAchievements = new ArrayList<>();
        interestsAndAchievements.add("Completed Silicon Valley Marathon in 2014");
        interestsAndAchievements.add("Completed San Francisco Marathon in 2013");
        interestsAndAchievements.add("Climbed Kilimanjaro in 2011");
        interestsAndAchievements.add("Working towards finishing a century ride");
        interestsAndAchievements.add("Participating in the strongman competition since 2010");
        interestsAndAchievements.add("Came in 2nd positon at the ToughMudder race 2015, San Jose");

        int index = 0;
        int min = 0;
        int max = 5;

        ArrayList<String> result = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            index = randInt(min, max);
            result.add(interestsAndAchievements.get(index));
            if ((min + 1) < max) {
                min++;
            } else {
                min--;
            }
        }
        return result;
    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public void updateAllTrainersWithCertificationInfo() {
        ParseQuery<Trainer> query = ParseQuery.getQuery("Trainer");
        query.findInBackground(new FindCallback<Trainer>() {
            public void done(List<Trainer> objects, ParseException e) {
                if (e == null) {
                    for (Trainer trainer : objects) {
                        trainer.setEducationAndCertifications(getCertifcationsAndEducation());
                        trainer.setInterestsAndAchievements(getInterestsAndAchievements());
                        trainer.saveInBackground();
                        Log.d("DEBUG", "Found " + objects.size() + " trainer objects");
                    }
                } else {
                    Log.d("DEBUG", "Find all trainer objects query FAILED");
                }
            }
        });
    }
}
