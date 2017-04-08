package com.blogspot.androidcanteen.interestpoints;

import android.app.Activity;
import android.support.annotation.FractionRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Paolo on 7/04/2017.
 */

public class SavedPlacesBottomSheet implements IDatabaseListener {

    private static final float SINGLE_CARD_HEIGHT = 245 ;
    final int PEEK_HEIGHT = 150;
    final int SHEET_HEIGHT = 550;

    MainActivity act;

    FrameLayout bottomSheet;
    BottomSheetBehavior bsb;

    RecyclerView recView;
    LinearLayoutManager layoutManager;
    ItemTouchHelper touchHelper;
    RecyclerAdapter adapter = null;

    Infodialog di;

    int currentHeight = 0;


    public SavedPlacesBottomSheet(final MainActivity act, View mainView)
    {
        this.act = act;
        bottomSheet = (FrameLayout) mainView;

        di = new Infodialog(act);

        bsb = BottomSheetBehavior.from(bottomSheet);

        bsb.setPeekHeight(PEEK_HEIGHT);
        bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);

        SetBottomSheetHeightAccordingToContent();

        bsb.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {


                    recView.scrollToPosition(0);
                    SetBottomSheetHeightAccordingToContent();
                    di.StopShowDelay();
                }

                else if (newState == BottomSheetBehavior.STATE_EXPANDED) {

                    di.create(act.getString(R.string.infoDialogPlacesActivity),null,1500);
                }
                else if(newState == BottomSheetBehavior.STATE_DRAGGING)
                {
                  //  LimitHeight();
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        IPDatabase.getInstance().AddListener(this);

        recView = (RecyclerView) bottomSheet.findViewById(R.id.recyclerView);
        recView.setFocusable(false); //Very important to display the bottom sheet on the top!
      //  recView.setNestedScrollingEnabled(false);


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

    public void ShowSingleCard(String title)
    {


        int index =  IPDatabase.getInstance().GetIndexOfItem(title);



        CoordinatorLayout.LayoutParams param = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();

            param.height = (int) GlobalVariables.DpToPx(SINGLE_CARD_HEIGHT);

        bottomSheet.setLayoutParams(param);


       Show();
        recView.scrollToPosition(index);
    }

    public void Hide()
    {


      //  bottomSheet.fullScroll(NestedScrollView.FOCUS_UP);
        bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void LimitHeight()
    {
        CoordinatorLayout.LayoutParams param = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();

        if(bottomSheet.getHeight() > SHEET_HEIGHT)
            param.height = (int) GlobalVariables.DpToPx(SHEET_HEIGHT);




        bottomSheet.setLayoutParams(param);
    }
private void SetBottomSheetHeightAccordingToContent()
{
    if(IPDatabase.getInstance().GetAllPoints().size()==0)
        bsb.setPeekHeight(0);
    else
        bsb.setPeekHeight(PEEK_HEIGHT);

    CoordinatorLayout.LayoutParams param = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();

    param.height = (int) GlobalVariables.DpToPx(SHEET_HEIGHT);

    bottomSheet.setLayoutParams(param);
}
    public void Show()
    {



       // bottomSheet.fullScroll(NestedScrollView.FOCUS_UP);

        bsb.setState(BottomSheetBehavior.STATE_EXPANDED);


    }

    public int GetState()
    {
        return bsb.getState();
    }

    public void Disable()
    {

        bottomSheet.setVisibility(View.GONE);
    }

    public void Enable()
    {

        bottomSheet.setVisibility(View.VISIBLE);
    }
    @Override
    public void OnDatabaseChange(DATABASE_OPERATION operation) {

        SetBottomSheetHeightAccordingToContent();

        if(operation == DATABASE_OPERATION.ADD ) {
            Hide();
            adapter = new RecyclerAdapter(act);
            recView.setAdapter(adapter);
            currentHeight = (int) GlobalVariables.convertPixelsToDp(bottomSheet.getHeight(),act);
            GlobalVariables.LogWithTag("Current height in dp " + currentHeight);

        }
        else
            adapter.notifyDataSetChanged();

        if(IPDatabase.getInstance().GetAllPoints().size()==0)
            Hide();



    }
}
