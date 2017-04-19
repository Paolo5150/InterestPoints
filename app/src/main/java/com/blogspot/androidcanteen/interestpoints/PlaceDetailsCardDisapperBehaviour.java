package com.blogspot.androidcanteen.interestpoints;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;

import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Paolo on 21/03/2017.
 */

public class PlaceDetailsCardDisapperBehaviour extends CoordinatorLayout.Behavior<FloatingActionButton> {

   float maximumDifference = 0;

    public PlaceDetailsCardDisapperBehaviour(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {

        return dependency instanceof TabLayout;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton child, View dependency) {

        GlobalVariables.LogWithTag("UAAAHHHH");

        int targetLoc[] = new int[2];
        dependency.getLocationInWindow(targetLoc);

        int[] childLocation = new int[2];
        child.getLocationInWindow(childLocation);

        float YThreshold = GlobalVariables.DpToPx(100);

        if(maximumDifference==0)
            maximumDifference = childLocation[1] - YThreshold;

        float currentDifference = childLocation[1] - YThreshold;

        float perc = currentDifference / maximumDifference;

        if(perc>1.0f)
            perc = 1.0f;
        else if(perc<0)
            perc = 0;

        child.setScaleX(perc);
        child.setScaleY(perc);

        return false;
    }

}
