package com.packag.eventmonitor.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.DetailPenilaianActivity;
import com.packag.eventmonitor.FirestoreController;
import com.packag.eventmonitor.R;

import java.util.Vector;

public class AdapterListTeamAdmin extends RecyclerView.Adapter<AdapterListTeamAdmin.MyViewHolder> {
    String eventId;
    Vector<Team> dataTeam;
    private Context ctx;
    boolean no_urut_validation = true;
    FirestoreController fc = new FirestoreController();
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       return new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.adapter_model_team_list_admin,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindItem(ctx,dataTeam.get(position));
    }

    @Override
    public int getItemCount() {
        return dataTeam.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_amtl_no_urut;
        TextView tv_amtl_team_name;
        TextView tv_amtl_nilai_bersih;
        Button btn_amtla_penilaian;
        Button btn_amtla_edit;
        Button btn_amtla_delete;
        public MyViewHolder(View v) {
            super(v);
            tv_amtl_no_urut = v.findViewById(R.id.tv_amtl_no_urut);
            tv_amtl_team_name = v.findViewById(R.id.tv_amtl_team_name);
            btn_amtla_penilaian = v.findViewById(R.id.btn_amtla_penilaian);
            tv_amtl_nilai_bersih = v.findViewById(R.id.tv_amtl_nilai_bersih);
            btn_amtla_edit = v.findViewById(R.id.btn_amtla_edit);
            btn_amtla_delete = v.findViewById(R.id.btn_amtla_delete);
        }
        public void bindItem(final Context ctx, final Team team){
            //modif data disini

            tv_amtl_no_urut.setText(team.getNo_urut()+"");
            tv_amtl_team_name.setText(team.getTeam_name());
            tv_amtl_nilai_bersih.setText("Nilai Bersih : "+team.getNilai_bersih());
            btn_amtla_penilaian.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ctx, DetailPenilaianActivity.class);
                    intent.putExtra("eventId",eventId);
                    intent.putExtra("teamId",team.getKey());
                    ctx.startActivity(intent);
                }
            });
            btn_amtla_edit.setOnClickListener(new View.OnClickListener() {
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
                    et_amlat_no_urut.setText(team.getNo_urut()+"");
                    et_amlat_team_name.setText(team.getTeam_name());

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {

                                            final FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            final Vector<Team> teams =new Vector<Team>();

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
                                                        .document(eventId)
                                                        .collection("team").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot d2 : task.getResult()) {
                                                                Team temp_team = d2.toObject(Team.class);
                                                                if(Integer.parseInt(et_amlat_no_urut.getText().toString())
                                                                        ==temp_team.getNo_urut()&&temp_team.getNo_urut()!=team.getNo_urut()){
                                                                    no_urut_validation = false;
                                                                }
                                                            }
                                                            if(no_urut_validation == true){
                                                                fc.updateTeam(eventId,team.getKey(),new Team(et_amlat_team_name.getText().toString(),
                                                                        Integer.parseInt(et_amlat_no_urut.getText().toString())));
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
            btn_amtla_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final KAlertDialog confirm_dialog = new KAlertDialog(ctx, KAlertDialog.WARNING_TYPE)
                            .setTitleText("Are you sure?")
                            .setContentText("Won't be able to recover team data!")
                            .setConfirmText("Yes,delete it!")
                            .setCancelText("Cancel");
                            confirm_dialog.setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                @Override
                                public void onClick(KAlertDialog sDialog) {
                                    confirm_dialog.dismiss();

                                    fc.deleteTeam(eventId,team.getKey());
                                    fc.recalculateTotalTeam(eventId);
                                    new KAlertDialog(ctx, KAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("Good job!")
                                            .setContentText("Success Deleting data!")
                                            .show();
                                }
                            })
                            .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                @Override
                                public void onClick(KAlertDialog kAlertDialog) {
                                    confirm_dialog.dismiss();
                                }
                            }).show();
                }
            });

        }
    }
    public AdapterListTeamAdmin(Context ctx,Vector<Team> dataTeam,String eventId){
        this.ctx = ctx;
        this.dataTeam = dataTeam;
        this.eventId = eventId;
    }
}
