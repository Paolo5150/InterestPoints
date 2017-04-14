package com.blogspot.androidcanteen.interestpoints;

import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;

/**
 * Created by Paolo on 14/04/2017.
 */

public class PlaceDetails_InfoFragment extends Fragment{

    public TextView addressTextView;
    public TextView titleTextView;
    public TextView phoneTextView;
    public TextView websiteTextView;
    public TextView openingHoursTextView;
    public TextView openingHoursDaysTextView;
    public ProgressBar progBar;
    public LinearLayout mainContent;

    public View rootView;



    public PlaceDetails_InfoFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       rootView = inflater.inflate(R.layout.placedetails_info_fragment, container, false);

        addressTextView = (TextView)rootView.findViewById(R.id.address);
        titleTextView = (TextView)rootView.findViewById(R.id.titleTextView);
        phoneTextView = (TextView)rootView.findViewById(R.id.phoneNumber);
        websiteTextView = (TextView)rootView.findViewById(R.id.website);
        openingHoursTextView = (TextView) rootView.findViewById(R.id.openingHoursTextView);
        openingHoursDaysTextView = (TextView) rootView.findViewById(R.id.openingHoursDaysTextView);
        mainContent = (LinearLayout) rootView.findViewById(R.id.main_content_layout);

        progBar = (ProgressBar) rootView.findViewById(R.id.progBar);
        progBar.setVisibility(View.VISIBLE);
        mainContent.setAlpha(0);



        return rootView;
    }

    public void UpdateData(PlaceDetailsJsonObject jsonResult )
    {
        progBar.setVisibility(View.INVISIBLE);
        mainContent.setAlpha(1);

        addressTextView.setText(jsonResult.formattedAddress);
        phoneTextView.setText(jsonResult.phone);
        websiteTextView.setText(jsonResult.website);
        titleTextView.setText(jsonResult.name);
        // infoFragment.typeTextView.setText(jsonResult.types);

        TextView ratingsTextView = (TextView) rootView.findViewById(R.id.ratingNumber);
        ratingsTextView.setText(jsonResult.rating);

        RatingBar bars = (RatingBar) rootView.findViewById(R.id.ratingStars);
        bars.setIsIndicator(true);
        bars.setRating(jsonResult.getRatingsFloat());

        openingHoursTextView.setText(jsonResult.openingHours);
        openingHoursDaysTextView.setText(jsonResult.days);
    }

}
