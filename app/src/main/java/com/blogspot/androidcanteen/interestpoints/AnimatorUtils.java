package com.blogspot.androidcanteen.interestpoints;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by Paolo on 17/02/2017.
 */

public class AnimatorUtils {


    public static void animateViewHolder(RecyclerView.ViewHolder holder,int position)
    {
        GlobalVariables.LogWithTag("View animated");
        holder.itemView.setAlpha(0);

        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.itemView,"alpha",0,1);
        animator.setDuration(1500);
        animator.setStartDelay(position*150);

        ObjectAnimator translateX = ObjectAnimator.ofFloat(holder.itemView,"translationX",-300,0);
        translateX.setDuration(1000);
        translateX.setStartDelay(position*150);

        animator.start();
        translateX.start();
    }

    public static BitmapDescriptor getMarkerIcon(String color) {
        float[] hsv = new float[3];
        Color.colorToHSV(Color.parseColor(color), hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }
}


