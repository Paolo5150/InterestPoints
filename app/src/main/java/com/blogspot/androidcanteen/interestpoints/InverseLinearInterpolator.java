package com.blogspot.androidcanteen.interestpoints;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by Paolo on 21/03/2017.
 */

public class InverseLinearInterpolator extends LinearInterpolator {

    private final Interpolator delegate;

    public InverseLinearInterpolator(Interpolator delegate){
        this.delegate = delegate;
    }

    public InverseLinearInterpolator(){
        this(new LinearInterpolator());
    }

    @Override
    public float getInterpolation(float input) {
        return 1 - delegate.getInterpolation(input);
    }
}
