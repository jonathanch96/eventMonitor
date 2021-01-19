package com.packag.eventmonitor.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.BarcodeActivity;
import com.packag.eventmonitor.ChatRoomAdminActivity;
import com.packag.eventmonitor.Data.Events;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.FirestoreController;
import com.packag.eventmonitor.R;

import java.util.Vector;

import javax.annotation.Nullable;

public class AdapterListEvent extends BaseAdapter {
    private Context ctx;
    Vector<Events> dataEvent;
    FirestoreController fc = new FirestoreController();
    boolean no_urut_validation = true;

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
        FrameLayout fl_amel_chat = v.findViewById(R.id.fl_amel_chat);
        TextView tv_amel_total_team = v.findViewById(R.id.tv_amel_total_team);
        Button btn_amel_assign_team = v.findViewById(R.id.btn_amel_assign_team);
        Button btn_amel_lihat_team = v.findViewById(R.id.btn_amel_lihat_team);
        ImageView iv_amel_barcode = v.findViewById(R.id.iv_amel_barcode);

        iv_amel_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, BarcodeActivity.class);
                intent.putExtra("code",dataEvent.get(i).getCode());
                ctx.startActivity(intent);
            }
        });

        final Button btn_amel_event_finish = v.findViewById(R.id.btn_amel_event_finish);
        String[] temp_date = dataEvent.get(i).getDate().split("-");
        tv_amel_date.setText(temp_date[2]+"/"+temp_date[1]+"/"+temp_date[0]);
        tv_amel_event_code.setText("Kode Event : "+dataEvent.get(i).getCode());
        tv_amel_event_themes.setText(dataEvent.get(i).getThemes());
        tv_amel_total_judges.setText("Jumlah Juri : "+Integer.toString(dataEvent.get(i).getTotal_referee()));
        tv_amel_total_team.setText("Jumlah Team : "+Integer.toString(dataEvent.get(i).getTotal_team()));
        if(dataEvent.get(i).getStatus()==1){
            btn_amel_event_finish.setText(R.string.btn_event_finish);
        }else{
            btn_amel_event_finish.setText(R.string.btn_event_active);
        }
        fl_amel_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, ChatRoomAdminActivity.class);
                intent.putExtra("eventId",dataEvent.get(i).getKey());
                intent.putExtra("userId","admin");
                intent.putExtra("name","Admin");
                ctx.startActivity(intent);
            }
        });
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

                                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        final Vector<Team> teams =new Vector<Team>();
                                        Log.d("debug","Team List : "+teams.toString());
                                        if(et_amlat_team_name.getText().toString().equals("")){
                                            new KAlertDialog(ctx, KAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Error!")
                                                    .setContentText("Nama Team harus diisi!")
                                                    .show();
                                        }else if(et_amlat_no_urut.getText().toString().equals("")){
                                            new KAlertDialog(ctx, KAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Error!")
                                                    .setContentText("No urut harus diisi!")
                                                    .show();
                                        }else{
                                            no_urut_validation = true;
                                            //validate no urut
                                            db.collection("events")
                                                    .document(dataEvent.get(i).getKey())
                                                    .collection("team").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot d2 : task.getResult()) {
                                                            Team temp_team = d2.toObject(Team.class);
                                                            if(Integer.parseInt(et_amlat_no_urut.getText().toString())==temp_team.getNo_urut()){
                                                                no_urut_validation = false;
                                                            }
                                                        }
                                                        if(no_urut_validation == true){
                                                            fc.addTeam(dataEvent.get(i).getKey(),new Team(et_amlat_team_name.getText().toString(),
                                                                    Integer.parseInt(et_amlat_no_urut.getText().toString())));

                                                            fc.recalculateTotalTeam(dataEvent.get(i).getKey());
                                                        }else{
                                                            new KAlertDialog(ctx, KAlertDialog.ERROR_TYPE)
                                                                    .setTitleText("Error!")
                                                                    .setContentText("No urut sudah ada!")
                                                                    .show();
                                                        }

                                                    }
                                                }
                                            });


                                        }





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
        btn_amel_lihat_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
                LayoutInflater inflater =(LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogView = inflater.inflate(R.layout.adapter_model_dialog_team_list_admin, null);
                final RecyclerView rv_amdtla_list_team = dialogView.findViewById(R.id.rv_amdtla_list_team);
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                rv_amdtla_list_team.setHasFixedSize(true);

                // use a linear layout manager
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ctx);
                rv_amdtla_list_team.setLayoutManager(layoutManager);


                FirebaseFirestore db = FirebaseFirestore.getInstance();

                final CollectionReference teamRef = db.collection("events")
                        .document(dataEvent.get(i).getKey())
                        .collection("team")
                        ;
                teamRef.orderBy("no_urut").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                         Vector<Team> dataTeam = new Vector<Team>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            final Team teamClass = document.toObject(Team.class);
                            teamClass.setKey(document.getId());
                            dataTeam.add(teamClass);
                        }
                        RecyclerView.Adapter adapterListTeamAdmin =
                                new AdapterListTeamAdmin(ctx,dataTeam,dataEvent.get(i).getKey());
                        rv_amdtla_list_team.setAdapter(adapterListTeamAdmin);
                    }
                });
                dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setView(dialogView);
                dialog.show();
            }
        });

        btn_amel_event_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final KAlertDialog dialog = new KAlertDialog(ctx, KAlertDialog.WARNING_TYPE)
                        .setTitleText("Konfirmasi")
                        .setContentText("Apakah Anda yakin untuk mengubah status event ?")
                        .setConfirmText("Yes")
                        .setCancelText("Cancel");
                dialog.setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog sDialog) {
                        dialog.dismiss();
                        int status = 0;
                        if(dataEvent.get(i).getStatus()==1){
                            status = 2;
                        }else{
                            status=1;
                        }
                        fc.updateEventStatus(dataEvent.get(i).getKey(),status);
                    }
                }).setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        dialog.dismiss();

                    }
                })
                        .show();
            }
        });

        return v;
    }


}
