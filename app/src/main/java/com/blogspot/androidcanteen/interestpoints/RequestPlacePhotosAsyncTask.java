package com.blogspot.androidcanteen.interestpoints;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Paolo on 14/04/2017.
 */

public class RequestPlacePhotosAsyncTask extends AsyncTask<String,Void,PhotoResult> {

    IRequestListener listener;


   public Bitmap[] bitmapArray;


    public RequestPlacePhotosAsyncTask(IRequestListener listener)
    {
        this.listener = listener;
    }


    @Override
    protected void onPostExecute(PhotoResult result) {

      //  listener.OnRequestCompleted(IRequestListener.RequestTypes.PHOTOS,result);

        if(result!=null ) {
            if(listener!=null)
            listener.OnRequestCompleted(IRequestListener.RequestTypes.PHOTOS, result);
        }
        else
            GlobalVariables.LogWithTag("GOT A NULL");


        super.onPostExecute(result);
    }

    @Override
    protected PhotoResult doInBackground(String... params) {


        PhotoResult res = new PhotoResult();
        int count = 0;
        InputStream in = null;

        ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);

        try {
                URL myUrl = new URL(params[0]);
                 res.link = params[0];
                HttpURLConnection con = (HttpURLConnection) myUrl.openConnection();
               // con.setRequestProperty("Accept-Encoding", "identity");  //Allow to know file size through getContentLength
               // con.setDoInput(true);
                con.connect();

                byte[] b = new byte[1024];

                in = con.getInputStream();

                while ((count = in.read(b)) != -1) {
                    baos.write(b, 0, count);

                }

                res.imageBitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length);

            baos.flush();
            baos.close();
            in.close();

        } catch (MalformedURLException e) {

            GlobalVariables.LogWithTag("MALFORMED");



            e.printStackTrace();
        } catch (IOException e) {




            GlobalVariables.LogWithTag("IO EXCEPTION: " + e.getMessage());

            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            String s = writer.toString();

            if(listener!=null)
                listener.OnRequestCompleted(IRequestListener.RequestTypes.PHOTOS, null);

        //    GlobalVariables.LogWithTag("Stack: " + s);

        }


        return res;
    }
}
