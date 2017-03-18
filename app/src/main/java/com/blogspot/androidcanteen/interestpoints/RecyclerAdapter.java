package com.blogspot.androidcanteen.interestpoints;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Paolo on 16/02/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewholder>  {

    public final static int VIEW_ON_MAP_REQUEST = 150000;

    List<InterestPoint> allPoints;
    Activity act;

    public RecyclerAdapter(Activity act)
    {

        this.act = act;

        allPoints = IPDatabase.getInstance().GetAllPoints();

        GlobalVariables.LogWithTag("Adapter constructor called");

    }



    public class MyViewholder extends RecyclerView.ViewHolder
    {

        TextView title;
        TextView decription;
        ImageView placeOptions;
        CheckBox notifyBox;


        public MyViewholder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.theTitle);
            decription = (TextView) itemView.findViewById(R.id.theDescription);
            notifyBox = (CheckBox)itemView.findViewById(R.id.checkBox);
            placeOptions = (ImageView) itemView.findViewById(R.id.placeOptions);




        }


    }

    @Override
    public MyViewholder onCreateViewHolder(ViewGroup parent, int viewType) {


        View inflated = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item,parent,false);

        return new MyViewholder(inflated);
    }

    @Override
    public void onBindViewHolder(final MyViewholder holder, final int position) {
        allPoints = IPDatabase.getInstance().GetAllPoints();
        final InterestPoint point = allPoints.get(position);

        holder.title.setText(point.title);
        holder.decription.setText(point.description);
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

                       // GlobalVariables.LogWithTag("Pressed: " + item.getTitle() + " view: " + point.title);


                        //Item CheckOnMap
                        if(item.getItemId() == R.id.viewOnMap) {

                            Intent pointReturned = new Intent();
                            pointReturned.putExtra("place_title", point.title);
                            act.setResult(VIEW_ON_MAP_REQUEST, pointReturned);

                            act.finish();
                        }

                        else if(item.getItemId() == R.id.editDescription)
                        {
                            NewPlaceDialog dialog = new NewPlaceDialog(act, new IDialogListener() {
                                @Override
                                public void OnOKButtonPressed(String description) {

                                   IPDatabase.getInstance().ReplacePointDescription(point.id,description);
                                    allPoints = IPDatabase.getInstance().GetAllPoints();
                                    notifyDataSetChanged();

                                }
                            });

                            dialog.desc.setText(point.description);
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

        AnimatorUtils.animateViewHolder(holder,position);

    }


    @Override
    public int getItemCount() {
        if(allPoints!=null)
        return allPoints.size();
        else
            return 0;
    }
}


