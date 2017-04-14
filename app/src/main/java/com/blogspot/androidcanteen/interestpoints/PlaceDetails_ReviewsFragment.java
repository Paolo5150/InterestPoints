package com.blogspot.androidcanteen.interestpoints;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Paolo on 14/04/2017.
 */

public class PlaceDetails_ReviewsFragment extends Fragment {

    View rootView;

    public PlaceDetails_ReviewsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.placedetails_reviews_fragment, container, false);



        return rootView;
    }
}
