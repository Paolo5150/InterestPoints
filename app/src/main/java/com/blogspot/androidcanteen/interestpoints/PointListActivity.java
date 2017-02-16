package com.blogspot.androidcanteen.interestpoints;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class PointListActivity extends AppCompatActivity {

    RecyclerView recView;
    LinearLayoutManager layoutManager;
    ItemTouchHelper touchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_list);

        recView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recView.setLayoutManager(layoutManager);

        final RecyclerAdapter adapter = new RecyclerAdapter();
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
    }
}
