package com.blogspot.androidcanteen.interestpoints;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Paolo on 17/02/2017.
 */

public class RequestPlaceDetailsAsyncTask extends AsyncTask<String,Void,String> {

    PlaceDetailsActivity detailsActivity;
    ProgressDialog dialog;

    public RequestPlaceDetailsAsyncTask(PlaceDetailsActivity detailsActivity)
    {
     this.detailsActivity = detailsActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        detailsActivity.mainContent.setAlpha(0);
        dialog = new ProgressDialog(detailsActivity);
        this.dialog.setMessage("Getting place details...");
        this.dialog.show();
    }

    @Override
    protected void onPostExecute(String result) {

        this.dialog.dismiss();
        detailsActivity.mainContent.setAlpha(1);
        PlaceDetailsJsonObject jsonResult = new PlaceDetailsJsonObject(result);

        detailsActivity.addressTextView.setText(jsonResult.formattedAddress);
        detailsActivity.phoneTextView.setText(jsonResult.phone);
        detailsActivity.websiteTextView.setText(jsonResult.website);

        TextView ratingsTextView = (TextView) detailsActivity.findViewById(R.id.ratingNumber);
        ratingsTextView.setText(jsonResult.rating);

        RatingBar bars = (RatingBar) detailsActivity.findViewById(R.id.ratingStars);
        bars.setIsIndicator(true);
        bars.setRating(jsonResult.getRatingsFloat());
        super.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";

        try {

            URL url = new URL(params[0]);
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();

            InputStream in = connection.getInputStream();
            InputStreamReader reader = new InputStreamReader(in);

            int data = reader.read();

            while(data!=-1)
            {
                char c = (char)data;
                result += c;
                data = reader.read();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
