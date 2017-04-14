package com.blogspot.androidcanteen.interestpoints;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

import me.grantland.widget.AutofitTextView;

public class PlaceDetailsActivity extends AppCompatActivity{

    CardView placeCard;
    public AutofitTextView titleTextView;
    public AutofitTextView typeTextView;
    public TextView addressTextView;
    public TextView phoneTextView;
    public TextView websiteTextView;
    public TextView openingHoursTextView;
    public TextView openingHoursDaysTextView;
    public LinearLayout mainContent;

    public NestedScrollView scroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detailsToolbar);
        setSupportActionBar(toolbar);



        String placeTitle = getIntent().getStringExtra("Title");

        final InterestPoint point = IPDatabase.getInstance().getPointByTitle(placeTitle);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(point.title);
                    isShow = true;
                } else if(isShow) {
                  //  collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });

       // getSupportActionBar().setTitle(point.title);

        placeCard = (CardView) findViewById(R.id.placeDetailsCard);
        titleTextView = (AutofitTextView) findViewById(R.id.theTitle);
        typeTextView = (AutofitTextView) findViewById(R.id.theType);
        addressTextView = (TextView)findViewById(R.id.address);
        phoneTextView = (TextView)findViewById(R.id.phoneNumber);
        websiteTextView = (TextView)findViewById(R.id.website);
        openingHoursTextView = (TextView) findViewById(R.id.openingHoursTextView);
        openingHoursDaysTextView = (TextView) findViewById(R.id.openingHoursDaysTextView);
        mainContent = (LinearLayout) findViewById(R.id.main_content_layout);
        scroll = (NestedScrollView) findViewById(R.id.my_nested_scroll);


        titleTextView.setMaxLines(1);
        titleTextView.setText(point.title);


        String link = RequestUtils.getLinkForPlaceDetails(point.id);

      /*  if(GlobalVariables.isNetworkAvailable())
        {
       // RequestPlaceDetailsAsyncTask task = new RequestPlaceDetailsAsyncTask(this);
     //   task.execute(link);}
        else {
            GlobalVariables.ToastShort("Internet connection required");
            finish();
        }*/







    }

}
