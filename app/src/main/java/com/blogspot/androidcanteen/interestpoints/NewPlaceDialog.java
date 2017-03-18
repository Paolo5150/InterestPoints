package com.blogspot.androidcanteen.interestpoints;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

/**
 * Created by Paolo on 18/03/2017.
 */

public class NewPlaceDialog {

    Dialog dialog;

    Activity act;
    IDialogListener listener;

    public EditText desc;

    public NewPlaceDialog(Activity act, final IDialogListener listener)
    {

        this.act = act;
        this.listener = listener;

        LayoutInflater inflater = (LayoutInflater)act.getLayoutInflater();
        View view = inflater.inflate(R.layout.new_place_dialog, null);

        AlertDialog alertDialog = new AlertDialog.Builder(act)
                .create();

        alertDialog.setTitle("Your description");

        alertDialog.setView(view);

        desc = (EditText) view.findViewById(R.id.descriptionEditText);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        listener.OnOKButtonPressed(desc.getText().toString());
                        dialog.dismiss();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        alertDialog.show();



    }
}
