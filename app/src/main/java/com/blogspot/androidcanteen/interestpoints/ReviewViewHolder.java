package com.blogspot.androidcanteen.interestpoints;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by Paolo on 17/04/2017.
 */
public class ReviewViewHolder extends RecyclerView.ViewHolder{

    TextView reviewText;
    TextView reviewAuthorName;
    TextView reviewTime;
    RatingBar rating;


    public ReviewViewHolder(View itemView) {
        super(itemView);

        reviewText = (TextView) itemView.findViewById(R.id.reviewText);
        reviewAuthorName = (TextView) itemView.findViewById(R.id.authorNameTextView);
        reviewTime = (TextView) itemView.findViewById(R.id.reviewTimeTextView);
        rating = (RatingBar) itemView.findViewById(R.id.reviewRatingBar);
    }
}
