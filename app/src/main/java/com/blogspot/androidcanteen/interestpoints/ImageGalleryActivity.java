package com.blogspot.androidcanteen.interestpoints;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

public class ImageGalleryActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    String[] allLinks;
    String singlaImageLink = "";
    public PlaceholderFragment[] fragments;
    public List<RequestPlacePhotosAsyncTask> tasks;

    int currentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_gallery);

             allLinks = getIntent().getExtras().getStringArray("links");
        singlaImageLink = getIntent().getExtras().getString("image_link");

        fragments = new PlaceholderFragment[allLinks.length];
        tasks = new ArrayList<>();

        for(int i=0; i<fragments.length;i++) {

            fragments[i] = new PlaceholderFragment(this,allLinks[i]);

        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),allLinks);


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
      //  mViewPager.setOffscreenPageLimit(allLinks.length); //Load all pages

        int index = mSectionsPagerAdapter.getItemPosByLink(singlaImageLink);
        mViewPager.setCurrentItem(index);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_image_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    @SuppressLint("ValidFragment")
    public static class PlaceholderFragment extends Fragment implements IRequestListener {

        public PhotoView imageLarge;
        public ProgressBar prog;
        public TextView noPictureTextView;
        public String link;
        RequestPlacePhotosAsyncTask task;
        ImageGalleryActivity act;
        PhotoResult myResult = null;

        public int restartAttempts = 3;

        public PlaceholderFragment() {


        }




        public PlaceholderFragment(ImageGalleryActivity act, String linkToImage) {

            link = linkToImage;
            this.act = act;
        }



        public void setBitmap(Bitmap bmp)
        {
            imageLarge.setImageBitmap(bmp);
            imageLarge.setVisibility(View.VISIBLE);
            prog.setVisibility(View.INVISIBLE);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_image_gallery, container, false);
            imageLarge =(PhotoView) rootView.findViewById(R.id.largeImage);

            prog = (ProgressBar) rootView.findViewById(R.id.largeImageProg);
            noPictureTextView = (TextView) rootView.findViewById(R.id.noPictureTextView);

            prog.getIndeterminateDrawable().setColorFilter(0xFFFFFFFF, android.graphics.PorterDuff.Mode.MULTIPLY);

            imageLarge.setVisibility(View.INVISIBLE);
            noPictureTextView.setVisibility(View.INVISIBLE);

            task = new RequestPlacePhotosAsyncTask(this);
            act.tasks.add(task);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,link);
            } else {
                task.execute(link);

            }

           GlobalVariables.LogWithTag("CREATED FRAGMENT");


            return rootView;
        }

        @Override
        public void onPause() {
            super.onPause();

            task.cancel(true);

            task = null;

            GlobalVariables.LogWithTag("Technically task is interrupted");
        }

        @Override
        public void OnRequestCompleted(RequestTypes type, Object result) {
            switch (type)
            {
                case PHOTOS:

                    myResult = (PhotoResult)result;

                    if(result!=null)
                    setBitmap(myResult.imageBitmap);
                    else
                    {
                        if(restartAttempts>0) {
                            GlobalVariables.LogWithTag("Task restarted");
                            RestartTask();
                        }
                        else
                        {
                            noPictureTextView.setVisibility(View.VISIBLE);
                            prog.setVisibility(View.INVISIBLE);
                            imageLarge.setVisibility(View.INVISIBLE);

                        }
                    }

                    break;
            }

        }

        private void RestartTask() {


            task.cancel(true);
            act.tasks.remove(task);
            task = null;
            task = new RequestPlacePhotosAsyncTask(this);
            act.tasks.add(task);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,link);
            } else {
                task.execute(link);

            }
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        String[] allImagesLinks;
        int numOfImages = 0;

        public SectionsPagerAdapter(FragmentManager fm, String[] links) {

            super(fm);
            allImagesLinks = allLinks;
            numOfImages = allLinks.length;
        }



        @Override
        public PlaceholderFragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return fragments[position];
        }

        public int getItemPosByLink(String link)
        {
            for(int i=0; i<numOfImages;i++)
            {
                if(allImagesLinks[i].equalsIgnoreCase(link))
                {
                    return i;
                }
            }

            return -1;
        }

        @Override
        public int getCount() {

            return numOfImages;
        }


    }
}
