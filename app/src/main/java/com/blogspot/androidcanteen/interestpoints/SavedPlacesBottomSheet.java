package com.blogspot.androidcanteen.interestpoints;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

/**
 * Created by Paolo on 7/04/2017.
 */

public class SavedPlacesBottomSheet implements IDatabaseListener {

    Activity act;

    NestedScrollView bottomSheet;
    BottomSheetBehavior bsb;

    RecyclerView recView;
    LinearLayoutManager layoutManager;
    ItemTouchHelper touchHelper;
    RecyclerAdapter adapter = null;

    Infodialog di;



    public SavedPlacesBottomSheet(final Activity act, View mainView)
    {
        this.act = act;
        bottomSheet = (NestedScrollView) mainView;

        di = new Infodialog(act);

        bsb = BottomSheetBehavior.from(bottomSheet);

        bsb.setPeekHeight(120);
        bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bsb.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bsb.setPeekHeight(120);
                    di.StopShowDelay();
                }

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    di.create(act.getString(R.string.infoDialogPlacesActivity),null,500);
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        IPDatabase.getInstance().AddListener(this);

        recView = (RecyclerView) bottomSheet.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(act);
        recView.setLayoutManager(layoutManager);

        adapter = new RecyclerAdapter(act);

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

    public void Hide()
    {


        bottomSheet.fullScroll(View.FOCUS_UP);
        bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void Show()
    {

        bottomSheet.fullScroll(View.FOCUS_UP);
        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);


    }

    @Override
    public void OnDatabaseChange() {

        GlobalVariables.LogWithTag("Listener called");
        adapter = new RecyclerAdapter(act);

        recView.setAdapter(adapter);
    }
}
