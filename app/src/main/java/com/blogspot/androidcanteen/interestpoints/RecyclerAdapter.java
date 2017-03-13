package com.blogspot.androidcanteen.interestpoints;

import android.app.Activity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
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

    List<InterestPoint> allPoints;
    Activity act;

    public RecyclerAdapter(Activity act)
    {

        this.act = act;
        allPoints = IPDatabase.getInstance().GetAllPoints();


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

        holder.title.setText(allPoints.get(position).title);
        holder.notifyBox.setChecked(allPoints.get(position).notifyWhenClose);
        holder.placeOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu options = new PopupMenu(act,holder.placeOptions);

                options.getMenuInflater().inflate(R.menu.place_options_popup,options.getMenu());
                options.show();
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
        return allPoints.size();
    }
}


