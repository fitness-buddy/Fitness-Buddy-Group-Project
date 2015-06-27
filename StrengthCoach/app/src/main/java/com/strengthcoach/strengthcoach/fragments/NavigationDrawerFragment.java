package com.strengthcoach.strengthcoach.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.strengthcoach.strengthcoach.R;
import com.strengthcoach.strengthcoach.activities.HomeActivity;
import com.strengthcoach.strengthcoach.adapters.NavDrawerListAdapter;
import com.strengthcoach.strengthcoach.models.NavDrawerItem;
import com.strengthcoach.strengthcoach.views.CustomDrawerLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class NavigationDrawerFragment extends Fragment {

    private ActionBarDrawerToggle mDrawerToggle;
    private CustomDrawerLayout mDrawerLayout;
    private boolean mUserLearnedDrawer;
    private boolean mFromSavedInstanceState;

    public static final String PREF_FILE_NAME = "pref_file";
    public static final String KEY_USER_LEARNED_DRAWER = "user_learned_drawer";
    private View containerView;
    // nav drawer items
    private String[] navMenuTitles;
    // nav drawer icons
    private TypedArray navMenuIcons;
    private ArrayList<NavDrawerItem> navDrawerItems;
    private ListView mDrawerList;
    private NavDrawerListAdapter adapter;

    public NavigationDrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = Boolean.valueOf(readFromPreferenes(getActivity(), KEY_USER_LEARNED_DRAWER, "false"));
        if(savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerList = (ListView) view.findViewById(R.id.lvNavItems);
        View header = inflater.inflate(R.layout.fragment_header, mDrawerList, false);
        mDrawerList.addHeaderView(header);
        initView();
        return view;
    }


    public void setup(View view, CustomDrawerLayout drawerLayout, Toolbar toolbar) {
        containerView = view;
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if(!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    saveToPreferenes(getActivity(), KEY_USER_LEARNED_DRAWER, mUserLearnedDrawer+"");
                }
                // Activity will draw the actionbar again; because it is overlayed by the drawer
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Activity will draw the actionbar again; because it is overlayed by the drawer
                getActivity().invalidateOptionsMenu();
            }
        };

        if(!mUserLearnedDrawer && !mFromSavedInstanceState) {
           //mDrawerLayout.openDrawer(containerView);
        }
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
    }

    public static void saveToPreferenes(Context context, String prefName, String prefValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(prefName, prefValue);
        editor.apply();
    }

    public static String readFromPreferenes(Context context, String prefName, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(prefName, defaultValue);
    }

    private void initView() {

        // load nav drawer items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        navDrawerItems = new ArrayList<>();

        // add nav drawer items from string array to items list
        for (int i = 0; i < navMenuIcons.length(); i++) {
            // TODO: if the user is logged in, do not show the "sign up" option
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1)));
        }

        // Recycle the typed array
        navMenuIcons.recycle();

        // set the nav drawer adapter to populate the listview
        adapter = new NavDrawerListAdapter(getActivity(), navDrawerItems, mDrawerLayout);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawerLayout.closeDrawers();
                // Every view has two children; imageView and TextView
                // View represents the entire row
                // The tag is only attached to the imageview in the adapter
                String tag = ((RelativeLayout) view).getChildAt(0).getTag().toString();
                handleClick(tag);
            }
        });
    }

    private void handleClick(String tag) {
        switch (tag) {
            case "Home":
                ((HomeActivity) getActivity()).populateTrainers();
                break;
            case "Map":
                ((HomeActivity) getActivity()).launchMap();
                break;
            case "Favorites":
                ((HomeActivity) getActivity()).populateFavoriteTrainers();
                break;
            case "Sign Out":
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                SharedPreferences.Editor edit = pref.edit();
                edit.clear();
                edit.commit();
                break;
        }
    }
}
