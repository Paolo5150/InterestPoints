package com.blogspot.androidcanteen.interestpoints;

/**
 * Created by Paolo on 7/04/2017.
 */

public interface IDatabaseListener {

    enum DATABASE_OPERATION
    {ADD,
    DELETE,
        EDIT}

    void OnDatabaseChange(DATABASE_OPERATION operation);
}
