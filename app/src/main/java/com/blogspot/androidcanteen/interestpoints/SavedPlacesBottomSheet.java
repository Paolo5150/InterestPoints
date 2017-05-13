package com.blogspot.androidcanteen.interestpoints;

import android.app.Activity;
import android.graphics.Point;
import android.support.annotation.FractionRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paolo on 7/04/2017.
 */

public class SavedPlacesBottomSheet implements IDatabaseListener {

    private static final float SINGLE_CARD_HEIGHT = 245 ;
    int PEEK_HEIGHT = 150;
    int SHEET_HEIGHT = 550; //Reassigned in constructor

    MainActivity act;

    FrameLayout bottomSheet;
    BottomSheetBehavior bsb;

    RecyclerView recView;
    EditText searchSavedPlace;
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

        //Get screen dimenstions
        Display display = act.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        int heightDp = (int)GlobalVariables.convertPixelsToDp((float)height,act);

        SHEET_HEIGHT = heightDp - heightDp/4;

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
        searchSavedPlace = (EditText) bottomSheet.findViewById(R.id.searcSavedPlace);
        adapter = new RecyclerAdapter(act);

        searchSavedPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Show();
            }
        });

        searchSavedPlace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                //Fill recycler view with items which name matches sequence
            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
    GlobalVariables.LogWithTag("Text changed -> " + s);

                if(!searchSavedPlace.getText().toString().equalsIgnoreCase(""))
                {
                    List<InterestPoint> points = IPDatabase.getInstance().GetAllPoints();
                    List<InterestPoint> matchingPoints = new ArrayList<InterestPoint>();

                    for(InterestPoint p : points)
                    {
                        if(p.title.toLowerCase().contains(s.toString().toLowerCase())) {
                            matchingPoints.add(p);
                            GlobalVariables.LogWithTag("Added " + p.title);
                        }
                    }

                    adapter = new RecyclerAdapter(act,matchingPoints);
                    recView.setAdapter(adapter);

                }
                else {
                    adapter = new RecyclerAdapter(act);

                }

                recView.setAdapter(adapter);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        recView.setFocusable(false); //Very important to display the bottom sheet on the top!
      //  recView.setNestedScrollingEnabled(false);


        layoutManager = new LinearLayoutManager(act);
        recView.setLayoutManager(layoutManager);




        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {




                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                RecyclerAdapter.MyViewholder viewHolder2 = (RecyclerAdapter.MyViewholder) viewHolder;

               // GlobalVariables.LogWithTag("Swiped " + viewHolder2.title.getText() + " direction " + direction);

                int viewPos = viewHolder.getAdapterPosition();
                adapter.allPoints.remove(viewPos);
                GlobalVariables.LogWithTag("Adapter has " + adapter.allPoints.size());
               // adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                IPDatabase.getInstance().DeleteInterestPointByTitle(viewHolder2.title.getText().toString(),viewPos);

            }
        };



        touchHelper = new ItemTouchHelper(callback);

        touchHelper.attachToRecyclerView(recView);



        recView.setAdapter(adapter);


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
    public void OnDatabaseChange(DATABASE_OPERATION operation, int itemPos) {




        SetBottomSheetHeightAccordingToContent();

        if(operation == DATABASE_OPERATION.ADD ) {
            Hide();
            adapter = new RecyclerAdapter(act);
            recView.setAdapter(adapter);
            currentHeight = (int) GlobalVariables.convertPixelsToDp(bottomSheet.getHeight(),act);
            GlobalVariables.LogWithTag("Current height in dp " + currentHeight);

        }
        else if(operation == DATABASE_OPERATION.EDIT_DESC ) {

            //Need to create new adapter as it needs to pull new data from the database
            adapter = new RecyclerAdapter(act);
            recView.setAdapter(adapter);

        }
        else if(operation ==  DATABASE_OPERATION.DELETE) {
            adapter.notifyItemRemoved(itemPos);
          // adapter.notifyItemRangeChanged(itemPos, adapter.allPoints.size());


            GlobalVariables.LogWithTag("All point in listener: " + adapter.allPoints.size());
            GlobalVariables.LogWithTag("Adapter count: " + adapter.getItemCount());

       //     adapter.notifyDataSetChanged();


        }

        if(IPDatabase.getInstance().GetAllPoints().size()==0)
            Hide();



    }
}
