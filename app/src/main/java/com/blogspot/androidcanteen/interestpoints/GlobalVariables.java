package com.blogspot.androidcanteen.interestpoints;

import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * Created by Paolo on 13/02/2017.
 */

public class GlobalVariables {

    public static final String TAG = "InterestPoints";

    public static void LogWithTag(String message)
    {
        Log.d(TAG,message);
    }

    public static void ToastShort(String message)
    {
        Toast.makeText(MainActivity.appCont,message,Toast.LENGTH_SHORT).show();
    }

    public static float DpToPx(float dp){

        Resources r = MainActivity.appCont.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));


        return valueResult;
    }
}
