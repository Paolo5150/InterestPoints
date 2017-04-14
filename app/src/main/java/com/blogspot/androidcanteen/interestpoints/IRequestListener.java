package com.blogspot.androidcanteen.interestpoints;

/**
 * Created by Paolo on 14/04/2017.
 */

public interface IRequestListener {

    enum RequestTypes
    {DETAILS,
    PHOTOS}

    public void OnRequestCompleted(RequestTypes type, Object result);
}
