package com.blogspot.androidcanteen.interestpoints;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

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

    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) MainActivity.appCont.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    public static float distFrom(LatLng l1, LatLng l2) {

        double lat1 = l1.latitude;
        double lat2 = l2.latitude;

        double lng1 = l1.longitude;
        double lng2 = l2.longitude;


        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }

    public static void ShowAlertDialog(Activity act, String message) {

        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(act);

        builder.setTitle("Info");
        builder.setMessage(message);


        dialog = builder.create();
        dialog.setCancelable(false);

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    public static void ShowAlertDialog(Activity act, String message, long delay) {

        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(act);

        builder.setTitle("Info");
        builder.setMessage(message);


        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        android.os.Handler hand = new android.os.Handler(Looper.getMainLooper());
        hand.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        },delay);



    }

    public static void ShowAlertDialog(Activity act, String message, final IDialogListener listener) {

        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(act);

        builder.setTitle("Info");
        builder.setMessage(message);


        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                listener.OnOKButtonPressed("");
            }
        });

        dialog.show();


    }
}
