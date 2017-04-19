package com.blogspot.androidcanteen.interestpoints;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paolo on 14/04/2017.
 */

public class PlaceDetails_PhotosFragment extends Fragment implements IRequestListener {

    public boolean imagesDownloaded = false;

    View rootView;
    PlaceDetailsTabActivity act;
    ProgressBar progBar;
    TextView warning;
    GridLayout grid;
    String[] allLinks;
    
    public List<RequestPlacePhotosAsyncTask> tasks;


    boolean imageSet = false;


    public PlaceDetails_PhotosFragment(PlaceDetailsTabActivity act) {
        this.act =act;

        tasks = new ArrayList<>();
        imagesDownloaded = false;


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.placedetails_photos_fragment, container, false);

        warning = (TextView) rootView.findViewById(R.id.photosWarningTextView);
        grid = (GridLayout) rootView.findViewById(R.id.photosGrid);
        progBar = (ProgressBar) rootView.findViewById(R.id.photoFragmentProgBar);

    warning.setVisibility(View.INVISIBLE);

        return rootView;
    }

    public void InterruptAllDownloadTaskss()
    {
        for(RequestPlacePhotosAsyncTask task : tasks)
            task.cancel(true);
    }

    public void WarnOfNoPhotos()
    {
        warning.setVisibility(View.VISIBLE);
        progBar.setVisibility(View.INVISIBLE);
        warning.setText("No photos available.");
    }

    //Call by activity in request listener
    public void DownloadAllPhotos(String[] allRefs)
    {

       if(imagesDownloaded)
           return;

        imagesDownloaded = true;
        GlobalVariables.LogWithTag("Photo fragment is downloading the images");
        allLinks = new String[allRefs.length];

        int total = allLinks.length;

        grid.setColumnCount(2);
        grid.setRowCount((total/2)+1);

        for(int i=0; i<allLinks.length;i++) {

            allLinks[i] = RequestUtils.getLinkForPhoto(allRefs[i], 1600);

            RequestPlacePhotosAsyncTask task;
            task = new RequestPlacePhotosAsyncTask(this);
            tasks.add(task);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,allLinks[i]);
            } else {
                task.execute(allLinks[i]);

            }

        }
    }

    @Override
    public void OnRequestCompleted(RequestTypes type, Object resulte) {

        switch (type)
        {
            case PHOTOS:

               // GlobalVariables.LogWithTag("Obtained a bitmap!");

                if(resulte==null)
                    break;

                progBar.setVisibility(View.INVISIBLE);

                final PhotoResult myRes = (PhotoResult)resulte;

                ImageView oImageView = new ImageView(act);
                oImageView.setImageBitmap(myRes.imageBitmap);

                android.widget.GridLayout.LayoutParams params = new android.widget.GridLayout.LayoutParams();
                params.height = (int) GlobalVariables.DpToPx(150);
                params.width = (int) GlobalVariables.DpToPx(150);
                params.setMargins(0,(int) GlobalVariables.DpToPx(8),(int) GlobalVariables.DpToPx(8),(int) GlobalVariables.DpToPx(8));


                oImageView.setLayoutParams(params);

                oImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        Intent toGallery = new Intent(act, ImageGalleryActivity.class);
                        toGallery.putExtra("links",allLinks);
                        toGallery.putExtra("image_link",myRes.link);
                        act.startActivity(toGallery);
                    }
                });



                grid.addView(oImageView);



                break;
        }
    }

}
