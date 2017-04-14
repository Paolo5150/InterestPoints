package com.blogspot.androidcanteen.interestpoints;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Paolo on 14/04/2017.
 */

public class PlaceDetails_PhotosFragment extends Fragment {

    View rootView;

    public PlaceDetails_PhotosFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.placedetails_photos_fragment, container, false);



        return rootView;
    }

}
