package com.blogspot.androidcanteen.interestpoints;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Paolo on 20/03/2017.
 */

public class Infodialog {

    SharedPreferences sp;
    SharedPreferences.Editor spEditor;

    AlertDialog dialog;
    AlertDialog.Builder builder;

    Activity act;

    Handler handler;



    int ID;

    public Infodialog(Activity act)
    {

        this.act = act;
        sp = act.getSharedPreferences("InfoDialog", Context.MODE_PRIVATE);
        spEditor = sp.edit();




    }

    public boolean create(String message,IDialogListener listener)
    {
        ID = message.hashCode();

        if(!canShowAgain())
            return false;

        CreateDialog(act,message,listener);

        dialog.show();

        return true;

    }

    public boolean create(String message,IDialogListener listener,long delay)
    {
        ID = message.hashCode();

        if(!canShowAgain())
            return false;

        CreateDialog(act,message,listener);



         handler = new Handler(Looper.getMainLooper());


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        },delay);

        return true;
    }


public void StopShowDelay()
{
    if(handler!=null)
        handler.removeCallbacksAndMessages(null);
}



  void CreateDialog(Activity act, String message, final IDialogListener list)
  {
      builder = new AlertDialog.Builder(act);

      View v = act.getLayoutInflater().inflate(R.layout.info_dialog_layout,null);
      builder.setView(v);

      builder.setTitle("Info");

      final CheckBox check = (CheckBox) v.findViewById(R.id.showAgain);
      check.setChecked(true);

      TextView mes = (TextView) v.findViewById(R.id.infoMessage);

      mes.setText(message);


      dialog = builder.create();

      dialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {

              if(list!=null)
                  list.OnOKButtonPressed("");

              GlobalVariables.LogWithTag("Show again: " + check.isChecked());

              spEditor.putBoolean("ShowAgain"+ID,check.isChecked());
              spEditor.commit();
              dialog.dismiss();
          }
      });
  }

    public boolean canShowAgain()
    {
        boolean can = sp.getBoolean("ShowAgain"+ID,true);

        return can;
    }


}
