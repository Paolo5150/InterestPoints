package com.blogspot.androidcanteen.interestpoints;

import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

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
}
