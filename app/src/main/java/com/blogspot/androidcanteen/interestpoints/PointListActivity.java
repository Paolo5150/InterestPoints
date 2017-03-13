package com.blogspot.androidcanteen.interestpoints;

import android.animation.ValueAnimator;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import java.sql.Time;

public class PointListActivity extends AppCompatActivity {

    RecyclerView recView;
    LinearLayoutManager layoutManager;
    ItemTouchHelper touchHelper;
    Toolbar tb;

    ImageView im;
    ImageView im2;




    int bitmapHeight;
    int bitmapWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_list);

        recView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recView.setLayoutManager(layoutManager);
        tb = (Toolbar) findViewById(R.id.toolbar);




        tb.setTitle("Interest Points");
        setSupportActionBar(tb);




        final RecyclerAdapter adapter = new RecyclerAdapter(PointListActivity.this);
        recView.setAdapter(adapter);

      ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
          @Override
          public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {




              return false;
          }

          @Override
          public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

              RecyclerAdapter.MyViewholder viewHolder2 = (RecyclerAdapter.MyViewholder) viewHolder;

              GlobalVariables.LogWithTag("Swiped " + viewHolder2.title.getText() + " direction " + direction);
              adapter.allPoints.remove(viewHolder.getAdapterPosition());
              adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
              IPDatabase.getInstance().DeleteInterestPointByTitle(viewHolder2.title.getText().toString());

          }
      };



        touchHelper = new ItemTouchHelper(callback);

        touchHelper.attachToRecyclerView(recView);


        im = (ImageView) findViewById(R.id.backgroun_one);
        im2 = (ImageView) findViewById(R.id.background_two);

        BitmapDrawable bitmap = (BitmapDrawable) this.getResources().getDrawable(R.drawable.wm2);
        bitmapHeight= bitmap .getBitmap().getHeight();
        bitmapWidth = bitmap .getBitmap().getWidth();


        im.getLayoutParams().height = bitmapHeight;
        im2.getLayoutParams().height = bitmapHeight;
        im.getLayoutParams().width = bitmapWidth;
        im2.getLayoutParams().width = bitmapWidth;

        im.setImageResource(R.drawable.wm2);
        im2.setImageResource(R.drawable.wm2);

        im.setX(0);
        im2.setX(bitmapWidth);

        im.setY(im.getY()+400);
        im2.setY(im2.getY()+400);



        //scrollImages.start();

        TranslateAnimation anim = new TranslateAnimation(0,-bitmapWidth,0,0);

        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(200000);
        anim.setRepeatMode(Animation.INFINITE);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setFillAfter( true );
        im.startAnimation(anim);
        im2.startAnimation(anim);



    }


    protected void onPause()
    {
        super.onPause();
        GlobalVariables.LogWithTag("Point list act paused");


    }
}
