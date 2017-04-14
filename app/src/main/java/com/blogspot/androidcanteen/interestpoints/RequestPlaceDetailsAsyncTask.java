package com.blogspot.androidcanteen.interestpoints;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
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

    IRequestListener listener;


    public RequestPlaceDetailsAsyncTask(IRequestListener listener)
    {
     this.listener = listener;
    }



    @Override
    protected void onPostExecute(String result) {

      listener.OnRequestCompleted(IRequestListener.RequestTypes.DETAILS,result);


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
