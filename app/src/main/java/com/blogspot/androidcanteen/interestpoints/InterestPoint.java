package com.blogspot.androidcanteen.interestpoints;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Paolo on 13/02/2017.
 */

public class InterestPoint {

    public String title;
    public String description;
    public String lat;
    public String lng;
    public boolean notifyWhenClose;
    public int id;

    public InterestPoint(String title,String description, String lat, String lng, boolean notifyWhenClose ) {
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.notifyWhenClose = notifyWhenClose;
        this.title = title;
    }

    public InterestPoint(int id, String title,String description, String lat, String lng, boolean notifyWhenClose) {
        this.id = id;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.notifyWhenClose = notifyWhenClose;
        this.title = title;
    }

    public LatLng getLatLng()
    {
        return new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
    }


}