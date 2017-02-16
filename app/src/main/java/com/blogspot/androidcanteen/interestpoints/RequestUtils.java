package com.blogspot.androidcanteen.interestpoints;

/**
 * Created by Paolo on 17/02/2017.
 */

public class RequestUtils {

    public static String getLinkForPlaceDetails(String id)
    {

        return "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + id + "&key=" + MainActivity.appCont.getString(R.string.google_api_key);
    }
}
