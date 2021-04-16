package com.example.wanderwand.clickImage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.wanderwand.R;
import com.example.wanderwand.destinations.CityFragment;

import butterknife.ButterKnife;


public class ListOfPlacesFragment extends Fragment {

    private FragmentManager mFragmentManager;

    public ListOfPlacesFragment() {
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_list_of_places, container, false);
        ButterKnife.bind(this, rootview);
        mFragmentManager = getFragmentManager();

            Fragment citiesFragment = new CityFragment();
            transactFragment(citiesFragment);


        return rootview;
    }

    private void transactFragment(Fragment fragment) {
        mFragmentManager.beginTransaction().replace(R.id.place_name,
                fragment)
                .addToBackStack(null)
                .commit();
    }
}