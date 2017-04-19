package com.blogspot.androidcanteen.interestpoints;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Paolo on 17/04/2017.
 */

public class ReviewsRecyclerAdapter extends RecyclerView.Adapter<ReviewViewHolder> {

    Activity act;
    ReviewObject[] allReviews;

    public ReviewsRecyclerAdapter(Activity act, ReviewObject[] allReviews)
    {
        this.act = act;
        this.allReviews = allReviews;

    }
    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final View inflated = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_list_item,parent,false);

        ReviewViewHolder holder = new ReviewViewHolder(inflated);
        return holder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {

        ReviewObject review = allReviews[position];

        holder.reviewAuthorName.setText(review.author);
        holder.reviewText.setText(review.text);
        holder.rating.setRating(review.getRatingFloat());
        holder.reviewTime.setText(review.getTimeText());

    }

    @Override
    public int getItemCount() {
        return allReviews.length;
    }
}
