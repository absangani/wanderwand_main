package com.example.wanderwand;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.wanderwand.clickImage.SelectImageActivity;
import com.example.wanderwand.destinations.CityFragment;
import com.example.wanderwand.friend.MyFriendsFragment;
import com.example.wanderwand.mytrips.MyTripsFragment;
import com.example.wanderwand.travel.HotelsActivity;
import com.example.wanderwand.utilities.UtilitiesFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
public class HomeFragment extends Fragment {

    private AppCompatActivity mActivity;
    @BindView(R.id.materialCardView2)
    CardView mHotelBookingView;
    @BindView(R.id.materialCardView21)
    CardView mFriendsView;
    @BindView(R.id.materialCardView3)
    CardView mTripsView;
    @BindView(R.id.popular_cities_home)
    CardView mCitiesView;
    @BindView(R.id.get_city_details)
    CardView mGetCityDetails;
    @BindView(R.id.utilities_home)
    CardView mUtilitiesView;

    private FragmentManager mFragmentManager;

    public HomeFragment() {
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootview);
        mFragmentManager = getFragmentManager();
        mHotelBookingView.setOnClickListener(v -> {
            Intent hotelIntent = HotelsActivity.getStartIntent(mActivity);
            startActivity(hotelIntent);
        });
        mFriendsView.setOnClickListener(v1 -> {
            Fragment friendsFragment = new MyFriendsFragment();
            transactFragment(friendsFragment);
        });
        mTripsView.setOnClickListener(v -> {
            Fragment tripsFragment = new MyTripsFragment();
            transactFragment(tripsFragment);
        });
        mCitiesView.setOnClickListener(v -> {
            Fragment citiesFragment = new CityFragment();
            transactFragment(citiesFragment);
        });
        mGetCityDetails.setOnClickListener(v -> {
            Intent intent;
            intent = new Intent(this.mActivity, SelectImageActivity.class);
                startActivity(intent);
        });
        mUtilitiesView.setOnClickListener(v -> {
            Fragment utilitiesFragment = new UtilitiesFragment();
            transactFragment(utilitiesFragment);
        });
        return rootview;
    }

    /**
     * This function handles the transaction of one fragment to another.
     * @param fragment
     */
    private void transactFragment(Fragment fragment) {
        mFragmentManager.beginTransaction().replace(R.id.parent_home,
                fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (AppCompatActivity) context;
    }
}
