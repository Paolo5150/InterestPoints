package com.blogspot.androidcanteen.interestpoints;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Paolo on 14/04/2017.
 */

public class PlaceDetails_ReviewsFragment extends Fragment {

    View rootView;
    PlaceDetailsTabActivity act;

    RecyclerView reviewsList;
    TextView warning;
    ProgressBar progBar;

    public PlaceDetails_ReviewsFragment() {

    }

    public PlaceDetails_ReviewsFragment(PlaceDetailsTabActivity placeDetailsTabActivity) {
        act = placeDetailsTabActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.placedetails_reviews_fragment, container, false);

        reviewsList = (RecyclerView) rootView.findViewById(R.id.reviewsList);
        warning = (TextView)rootView.findViewById(R.id.reviewsWarningTextView);
        progBar = (ProgressBar) rootView.findViewById(R.id.reviewsProgressBar);

        warning.setVisibility(View.INVISIBLE);

        return rootView;
    }

    public void UpdateData(PlaceDetailsJsonObject jsonResult)
    {

       progBar.setVisibility(View.INVISIBLE);

        int numOfReviews = jsonResult.allReviews.length;

        if(numOfReviews==0)
        {
            warning.setVisibility(View.VISIBLE);
            warning.setText("No reviews available.");
            return;
        }

        ReviewsRecyclerAdapter adapter = new ReviewsRecyclerAdapter(act,jsonResult.allReviews);
        reviewsList.setLayoutManager(new LinearLayoutManager(act));

        reviewsList.setAdapter(adapter);
    }
}
