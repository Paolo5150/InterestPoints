package com.blogspot.androidcanteen.interestpoints;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Paolo on 16/02/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewholder>  {

    public final static int VIEW_ON_MAP_REQUEST = 150000;

    List<InterestPoint> allPoints;
    MainActivity act;

    public RecyclerAdapter(MainActivity act)
    {

        this.act = act;

        allPoints = IPDatabase.getInstance().GetAllPoints();

        GlobalVariables.LogWithTag("Adapter constructor called");

    }



    public class MyViewholder extends RecyclerView.ViewHolder
    {



        TextView title;
        TextView decription;
        TextView address;
        ImageView placeOptions;

        FrameLayout cardFront;
        FrameLayout cardBack;
        CheckBox notifyBox;

        ObjectAnimator rotateToBack;
        ObjectAnimator frontAlpha;
        ObjectAnimator backAlpha;
        ObjectAnimator backMirror;

        HashMap<Integer,Boolean> flipStatus;

        public long rotationRudation = 600;

        public MyViewholder(final View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.theTitle);
            decription = (TextView) itemView.findViewById(R.id.theDescription);
            address = (TextView) itemView.findViewById(R.id.address);
            notifyBox = (CheckBox)itemView.findViewById(R.id.checkBox);
            cardFront = (FrameLayout) itemView.findViewById(R.id.card_front);
            cardBack = (FrameLayout) itemView.findViewById(R.id.card_back);
            placeOptions = (ImageView) itemView.findViewById(R.id.placeOptions);

            flipStatus = new HashMap<>(getItemCount());



            for(int i=0; i< getItemCount();i++)
            {
                flipStatus.put(i,false);
            }




            rotateToBack = ObjectAnimator.ofFloat(itemView, "rotationX", 0.0f, 180f);
            rotateToBack.setDuration(rotationRudation);


            rotateToBack.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                    ViewCompat.setElevation(itemView,0);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    GlobalVariables.LogWithTag("Rotation end");
                    ViewCompat.setElevation(itemView,GlobalVariables.DpToPx(5));

                    if(!flipStatus.get(getLayoutPosition()).booleanValue()) {
                        placeOptions.setClickable(true);
                        notifyBox.setClickable(true);
                    }
                    else {
                        placeOptions.setClickable(false);
                        notifyBox.setClickable(false);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            frontAlpha  = ObjectAnimator.ofFloat(cardFront, "alpha", 1.0f, 0.0f);
            frontAlpha.setDuration(0);
            frontAlpha.setStartDelay(rotationRudation/2);



            backAlpha = ObjectAnimator.ofFloat(cardBack, "alpha", 0.0f, 1.0f);
            backAlpha.setDuration(0);
            backAlpha.setStartDelay(rotationRudation/2);


            backMirror = ObjectAnimator.ofFloat(cardBack, "scaleY", 1.0f, -1.0f);
            backMirror.setDuration(rotationRudation/2);



    itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            GlobalVariables.LogWithTag("Layout position: " + getLayoutPosition());

            if(!flipStatus.get(getLayoutPosition()).booleanValue())
                FlipToBack();
            else
                FlipToFront();
        }
    });

        }

        public void FlipToBack() {
            rotateToBack.start();
            frontAlpha.start();

            backAlpha.start();
            backMirror.start();



            flipStatus.put(getLayoutPosition(),true);
        }

        public void FlipToFront() {
            rotateToBack.reverse();
            frontAlpha.reverse();

            backAlpha.reverse();
            // backMirror.reverse(); Dont do this one

            flipStatus.put(getLayoutPosition(),false);
        }

    }

    @Override
    public MyViewholder onCreateViewHolder(final ViewGroup parent, int viewType) {

        float scale = act.getResources().getDisplayMetrics().density;


        final View inflated = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_card,parent,false);

        inflated.setCameraDistance(10000 * scale);

        MyViewholder holder = new MyViewholder(inflated);

        AnimatorUtils.animateViewHolder(holder,0);

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewholder holder, final int position) {

       //
        allPoints = IPDatabase.getInstance().GetAllPoints();
        final InterestPoint point = allPoints.get(position);

        holder.title.setText(point.title);
        holder.decription.setText(point.description);
        holder.address.setText(point.address);
        holder.notifyBox.setChecked(point.notifyWhenClose);


        holder.placeOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu options = new PopupMenu(act,holder.placeOptions);


                options.getMenuInflater().inflate(R.menu.place_options_popup,options.getMenu());
                options.show();

                options.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        //Item CheckOnMap
                        if(item.getItemId() == R.id.viewOnMap) {

                          //  Intent pointReturned = new Intent();
                           // pointReturned.putExtra("place_title", point.title);
                            act.bottomSheet.Hide();

                            // GlobalVariables.LogWithTag("Main activity got: " + data.getStringExtra("place_title"));
                            InterestPoint point = IPDatabase.getInstance().getPointByTitle(allPoints.get(position).title);

                            act.mapCall.MoveGentlyToPosition(point.getLatLng(),18);
                          //  act.setResult(VIEW_ON_MAP_REQUEST, pointReturned);


                        }


                        //Edit description
                        else if(item.getItemId() == R.id.editDescription)
                        {

                            holder.FlipToBack();
                            NewPlaceDialog dialog = new NewPlaceDialog(act, point.title,new IDialogListener() {
                                @Override
                                public void OnOKButtonPressed(String description) {

                                    IPDatabase.getInstance().ReplacePointDescription(point.id,description);
                                    allPoints = IPDatabase.getInstance().GetAllPoints();
                                    notifyItemChanged(position);

                                }
                            },holder.rotationRudation);

                            dialog.desc.setText(point.description);
                        }


                        //Place details, will spawn new activity
                        else if(item.getItemId() == R.id.placeDetails)
                        {
                            Intent toDetails = new Intent(act,PlaceDetailsActivity.class);
                            toDetails.putExtra("Title",point.title);
                            act.startActivity(toDetails);
                        }


                    return true;
                    }
                });
            }
        });

        //Works, need to se ehow to use it
     /*   holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InterestPoint point = allPoints.get(position);

                String link = RequestUtils.getLinkForPlaceDetails(point.id);

                RequestPlaceDetailsAsyncTask task = new RequestPlaceDetailsAsyncTask();
                try {

                    String result = task.execute(link).get();

                    PlaceDetailsJsonObject jsonResult;
                    jsonResult = new PlaceDetailsJsonObject(result);

                    GlobalVariables.LogWithTag("Phone of " + point.title +": " + jsonResult.phone);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });*/

        holder.notifyBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                InterestPoint p = allPoints.get(position);

              IPDatabase.getInstance().ReplacePointBoolean(p.id,isChecked);

                IPDatabase.getInstance().printAllPoints();


            }
        });



    }




    @Override
    public int getItemCount() {
        if(allPoints!=null)
        return allPoints.size();
        else
            return 0;
    }
}


