package com.blogspot.androidcanteen.interestpoints;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Paolo on 24/03/2017.
 */

public class WarningPanel {

    RelativeLayout panel;

    public String message;
    public boolean isShowing = false;



    public WarningPanel(RelativeLayout panelView)
    {
        panel = panelView;
    }

    public void setMessage(String text)
    {
        TextView t = (TextView) panel.findViewById(R.id.warningPanel_message);

        t.setText(text);

    }

    public void Show()
    {
        panel.setAlpha(0.85f);
        panel.setClickable(true);
        isShowing = true;
    }

    public void Hide()
    {
        panel.setAlpha(0.0f);
        panel.setClickable(false);
        isShowing = false;
    }


}
