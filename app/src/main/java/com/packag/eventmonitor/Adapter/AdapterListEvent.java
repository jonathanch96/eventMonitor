package com.packag.eventmonitor.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.packag.eventmonitor.Data.Events;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.R;

import java.util.Vector;

public class AdapterListEvent extends BaseAdapter {
    private Context ctx;
    Vector<Events> dataEvent;


    public AdapterListEvent(Context ctx, Vector<Events> dataEvent) {
        this.ctx = ctx;
        this.dataEvent = dataEvent;
    }

    @Override
    public int getCount() {
        return dataEvent.size();
    }

    @Override
    public Object getItem(int i) {
        return dataEvent.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v = View.inflate(ctx, R.layout.adapter_model_event_list,null);
        TextView tv_amel_date = v.findViewById(R.id.tv_amel_date);
        TextView tv_amel_event_code = v.findViewById(R.id.tv_amel_event_code);
        TextView tv_amel_event_themes = v.findViewById(R.id.tv_amel_event_themes);
        TextView tv_amel_total_judges = v.findViewById(R.id.tv_amel_total_judges);
        TextView tv_amel_total_team = v.findViewById(R.id.tv_amel_total_team);
        Button btn_amel_assign_team = v.findViewById(R.id.btn_amel_assign_team);

        tv_amel_date.setText(dataEvent.get(i).getDate());
        tv_amel_event_code.setText("Kode Event : "+dataEvent.get(i).getCode());
        tv_amel_event_themes.setText(dataEvent.get(i).getThemes());
        tv_amel_total_judges.setText("Jumlah Juri : "+Integer.toString(dataEvent.get(i).getTotal_referee()));
        tv_amel_total_team.setText("Jumlah Team : "+Integer.toString(dataEvent.get(i).getTotal_team()));

        btn_amel_assign_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(ctx);
                View promptsView = li.inflate(R.layout.adapter_model_dialog_assign_team, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        ctx);
                alertDialogBuilder.setView(promptsView);
                final EditText et_amlat_team_name = (EditText) promptsView
                        .findViewById(R.id.et_amlat_team_name);
                final EditText et_amlat_no_urut = (EditText) promptsView
                        .findViewById(R.id.et_amlat_no_urut);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {

                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("events")
                                                .document(dataEvent.get(i).getKey())
                                                .update("total_team",dataEvent.get(i).getTeam().size()+1);
                                        db.collection("events")
                                                .document(dataEvent.get(i).getKey())
                                                .collection("team")
                                                .add(new Team(et_amlat_team_name.getText().toString(),
                                                        Integer.parseInt(et_amlat_no_urut.getText().toString())));



                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });


        return v;
    }


}
