package com.packag.eventmonitor.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.packag.eventmonitor.Data.Events;
import com.packag.eventmonitor.Data.Penilaian;
import com.packag.eventmonitor.Data.PenilaianTraditional;
import com.packag.eventmonitor.Data.Referee;
import com.packag.eventmonitor.Data.RefereePenilaian;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Nullable;

public class AdapterListReferee extends RecyclerView.Adapter<AdapterListReferee.MyViewHolder> {
    Context ctx;
    Vector<Referee> dataReferee;
    String eventId;
    String teamId;
    FirebaseFirestore db;
    PenilaianTraditional penilaian;
    Team team;
    Events events;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //TODO ganti view
        return new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.adapter_model_referee_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindItem(ctx, dataReferee.get(position));
    }

    @Override
    public int getItemCount() {
        return dataReferee.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_amrl_refereeName;
        TextView tv_amrl_grand_total;
        Button btn_amrl_lihat_detail;
        Button btn_amrl_edit;
        Button btn_amrl_nonedit;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            tv_amrl_refereeName = itemView.findViewById(R.id.tv_amrl_refereeName);
            tv_amrl_grand_total = itemView.findViewById(R.id.tv_amrl_grand_total);
            btn_amrl_lihat_detail = itemView.findViewById(R.id.btn_amrl_lihat_detail);
            btn_amrl_edit = itemView.findViewById(R.id.btn_amrl_edit);
            btn_amrl_nonedit = itemView.findViewById(R.id.btn_amrl_nonedit);

        }

        public void getTaoluView(final Context ctx, final Referee referee) {
            LayoutInflater li = LayoutInflater.from(ctx);
            View promptsView = li.inflate(R.layout.adapter_model_dialog_penilaian_taolu, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    ctx);
            alertDialogBuilder.setView(promptsView);
            //initiate data

            final TextView tv_amdp_team_name_taolu = promptsView.findViewById(R.id.tv_amdp_team_name_taolu);
            final TextView tv_amdp_no_urut_taolu = promptsView.findViewById(R.id.tv_amdp_no_urut_taolu);
            final TextView tv_amdp_referee_taolu = promptsView.findViewById(R.id.tv_amdp_referee_taolu);
            final TextView tv_amdp_taolu_n1 = promptsView.findViewById(R.id.tv_amdp_taolu_n1);
            final TextView tv_amdp_taolu_n2 = promptsView.findViewById(R.id.tv_amdp_taolu_n2);
            final TextView tv_amdp_taolu_n3 = promptsView.findViewById(R.id.tv_amdp_taolu_n3);
            final TextView tv_amdp_taolu_n4 = promptsView.findViewById(R.id.tv_amdp_taolu_n4);
            final TextView tv_amdp_taolu_n5 = promptsView.findViewById(R.id.tv_amdp_taolu_n5);
            final TextView tv_amdp_taolu_n6 = promptsView.findViewById(R.id.tv_amdp_taolu_n6);
            final TextView tv_amdp_taolu_n7 = promptsView.findViewById(R.id.tv_amdp_taolu_n7);
            final TextView tv_amdp_taolu_n8 = promptsView.findViewById(R.id.tv_amdp_taolu_n8);
            final TextView tv_amdp_taolu_n9 = promptsView.findViewById(R.id.tv_amdp_taolu_n9);
            final TextView tv_amdp_taolu_p1 = promptsView.findViewById(R.id.tv_amdp_taolu_p1);
            final TextView tv_amdp_taolu_p2 = promptsView.findViewById(R.id.tv_amdp_taolu_p2);
            final TextView tv_amdp_taolu_p3 = promptsView.findViewById(R.id.tv_amdp_taolu_p3);
            final TextView tv_amdp_taolu_p4 = promptsView.findViewById(R.id.tv_amdp_taolu_p4);
            final TextView et_amdp_taolu_kesulitan = promptsView.findViewById(R.id.et_amdp_taolu_kesulitan);
            final TextView tv_ap_taolu_total_penilaian = promptsView.findViewById(R.id.tv_ap_taolu_total_penilaian);
            final TextView tv_ap_taolu_nilai_total_pengurangan = promptsView.findViewById(R.id.tv_ap_taolu_nilai_total_pengurangan);
            final TextView tv_ap_taolu_grand_total = promptsView.findViewById(R.id.tv_ap_taolu_grand_total);


            //set data
            tv_amdp_taolu_n1.setText("0");
            tv_amdp_taolu_n2.setText("0");
            tv_amdp_taolu_n3.setText("0");
            tv_amdp_taolu_n4.setText("0");
            tv_amdp_taolu_n5.setText("0");
            tv_amdp_taolu_n6.setText("0");
            tv_amdp_taolu_n7.setText("0");
            tv_amdp_taolu_n8.setText("0");
            tv_amdp_taolu_n9.setText("0");
            tv_amdp_taolu_p1.setText("0");
            tv_amdp_taolu_p2.setText("0");
            tv_amdp_taolu_p3.setText("0");
            tv_amdp_taolu_p4.setText("0");
            et_amdp_taolu_kesulitan.setText("");
            tv_ap_taolu_total_penilaian.setText("0");
            tv_ap_taolu_nilai_total_pengurangan.setText("0");
            tv_ap_taolu_grand_total.setText("0");
            DocumentReference teamref = db.collection("events").document(eventId)
                    .collection("team").document(teamId);
            teamref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        team = documentSnapshot.toObject(Team.class);
                        team.setKey(documentSnapshot.getId());
                        tv_amdp_team_name_taolu.setText(
                                ctx.getApplicationContext().getString(R.string.team_name) + " "
                                        + team.getTeam_name());
                        tv_amdp_no_urut_taolu.setText(
                                ctx.getApplicationContext().getString(R.string.no_urut) + " "
                                        + team.getNo_urut() + "");
                        tv_amdp_referee_taolu.setText(
                                ctx.getApplicationContext().getString(R.string.referee) + " Juri " + referee.getNumber() + " - "
                                        + referee.getName());

                    }
                }
            });

            teamref.collection("penilaian").document(referee.getKey())
                    .collection("field").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot d2 : task.getResult()) {
                        if (d2.exists()) {
                            Penilaian penilaian = d2.toObject(Penilaian.class);
                            penilaian.setKey(d2.getId());
                            if (d2.getId().equals("et_amdp_taolu_n1")) {
                                tv_amdp_taolu_n1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_taolu_n2")) {
                                tv_amdp_taolu_n2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_taolu_n3")) {
                                tv_amdp_taolu_n3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_taolu_n4")) {
                                tv_amdp_taolu_n4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_taolu_n5")) {
                                tv_amdp_taolu_n5.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_taolu_n6")) {
                                tv_amdp_taolu_n6.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_taolu_n7")) {
                                tv_amdp_taolu_n7.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_taolu_n8")) {
                                tv_amdp_taolu_n8.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_taolu_n9")) {
                                tv_amdp_taolu_n9.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_ap_taolu_p1")) {
                                tv_amdp_taolu_p1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_ap_taolu_p2")) {
                                tv_amdp_taolu_p2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_ap_taolu_p3")) {
                                tv_amdp_taolu_p3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_ap_taolu_p4")) {
                                tv_amdp_taolu_p4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_taolu_kesulitan")) {
                                et_amdp_taolu_kesulitan.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            }
                        }
                    }
                }
            });

            teamref.collection("penilaian").document(referee.getKey())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                            if (value.exists()) {
                                RefereePenilaian rp = value.toObject(RefereePenilaian.class);
                                rp.setKey(value.getId());

                                tv_ap_taolu_total_penilaian.setText(
                                        String.format("%.2f", rp.getTotal_nilai()) + "");
                                tv_ap_taolu_nilai_total_pengurangan.setText(
                                        String.format("%.2f", rp.getTotal_potongan()) + "");
                                tv_ap_taolu_grand_total.setText(
                                        String.format("%.2f", rp.getGrand_total()) + "");

                            }
                        }
                    });
            alertDialogBuilder.setCancelable(true).setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            ;
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }

        public void getTraditionalView(final Context ctx, final Referee referee) {
            LayoutInflater li = LayoutInflater.from(ctx);
            View promptsView = li.inflate(R.layout.adapter_model_dialog_penilaian_traditional, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    ctx);
            alertDialogBuilder.setView(promptsView);
            //initiate data
            final TextView tv_amdp_team_name = promptsView.findViewById(R.id.tv_amdp_team_name_naga);
            final TextView tv_amdp_no_urut = promptsView.findViewById(R.id.tv_amdp_no_urut_naga);
            final TextView tv_amdp_referee = promptsView.findViewById(R.id.tv_amdp_referee_naga);
            final TextView tv_amdp_n1 = promptsView.findViewById(R.id.tv_amdp_n1);
            final TextView tv_amdp_n2 = promptsView.findViewById(R.id.tv_amdp_n2);
            final TextView tv_amdp_n3 = promptsView.findViewById(R.id.tv_amdp_n3);
            final TextView tv_amdp_n4 = promptsView.findViewById(R.id.tv_amdp_n4);
            final TextView tv_amdp_n5 = promptsView.findViewById(R.id.tv_amdp_n5);
            final TextView tv_amdp_n6 = promptsView.findViewById(R.id.tv_amdp_n6);
            final TextView tv_amdp_n7 = promptsView.findViewById(R.id.tv_amdp_n7);
            final TextView tv_amdp_n8 = promptsView.findViewById(R.id.tv_amdp_n8);
            final TextView tv_amdp_n9 = promptsView.findViewById(R.id.tv_amdp_n9);
            final TextView tv_amdp_n10 = promptsView.findViewById(R.id.tv_amdp_n10);
            final TextView tv_amdp_ks1 = promptsView.findViewById(R.id.tv_amdp_ks1);
            final TextView tv_amdp_ks2 = promptsView.findViewById(R.id.tv_amdp_ks2);
            final TextView tv_amdp_ks3 = promptsView.findViewById(R.id.tv_amdp_ks3);
            final TextView tv_amdp_ks4 = promptsView.findViewById(R.id.tv_amdp_ks4);
            final TextView tv_amdp_total_nilai = promptsView.findViewById(R.id.tv_amdp_total_nilai);
            final TextView tv_amdp_total_pengurangan = promptsView.findViewById(R.id.tv_amdp_total_pengurangan);
            final TextView tv_amdp_grand_total = promptsView.findViewById(R.id.tv_amdp_grand_total);


            //set data
            tv_amdp_n1.setText("0");
            tv_amdp_n2.setText("0");
            tv_amdp_n3.setText("0");
            tv_amdp_n4.setText("0");
            tv_amdp_n5.setText("0");
            tv_amdp_n6.setText("0");
            tv_amdp_n7.setText("0");
            tv_amdp_n8.setText("0");
            tv_amdp_n9.setText("0");
            tv_amdp_n10.setText("0");
            tv_amdp_ks1.setText("0");
            tv_amdp_ks2.setText("0");
            tv_amdp_ks3.setText("0");
            tv_amdp_ks4.setText("0");
            tv_amdp_total_nilai.setText("0");
            tv_amdp_total_pengurangan.setText("0");
            tv_amdp_grand_total.setText("0");
            DocumentReference teamref = db.collection("events").document(eventId)
                    .collection("team").document(teamId);
            teamref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        team = documentSnapshot.toObject(Team.class);
                        team.setKey(documentSnapshot.getId());
                        tv_amdp_team_name.setText(
                                ctx.getApplicationContext().getString(R.string.team_name) + " "
                                        + team.getTeam_name());
                        tv_amdp_no_urut.setText(
                                ctx.getApplicationContext().getString(R.string.no_urut) + " "
                                        + team.getNo_urut() + "");
                        tv_amdp_referee.setText(
                                ctx.getApplicationContext().getString(R.string.referee) + " Juri " + referee.getNumber() + " - "
                                        + referee.getName());

                    }
                }
            });
            teamref.collection("penilaian").document(referee.getKey())
                    .collection("field").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot d2 : task.getResult()) {
                        if (d2.exists()) {
                            Penilaian penilaian = d2.toObject(Penilaian.class);
                            penilaian.setKey(d2.getId());
                            if (d2.getId().equals("et_as_n1")) {
                                tv_amdp_n1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_n2")) {
                                tv_amdp_n2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_n3")) {
                                tv_amdp_n3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_n4")) {
                                tv_amdp_n4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_n5")) {
                                tv_amdp_n5.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_n6")) {
                                tv_amdp_n6.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_n7")) {
                                tv_amdp_n7.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_n8")) {
                                tv_amdp_n8.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_n9")) {
                                tv_amdp_n9.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_n10")) {
                                tv_amdp_n10.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_ks1")) {
                                tv_amdp_ks1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_ks2")) {
                                tv_amdp_ks2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_ks3")) {
                                tv_amdp_ks3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_as_ks4")) {
                                tv_amdp_ks4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            }
                        }
                    }
                }
            });

            teamref.collection("penilaian").document(referee.getKey())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                            if (value.exists()) {
                                RefereePenilaian rp = value.toObject(RefereePenilaian.class);
                                rp.setKey(value.getId());

                                tv_amdp_total_nilai.setText(
                                        String.format("%.2f", rp.getTotal_nilai()) + "");
                                tv_amdp_total_pengurangan.setText(
                                        String.format("%.2f", rp.getTotal_potongan()) + "");

                                tv_amdp_grand_total.setText(
                                        String.format("%.2f", rp.getGrand_total()) + "");

                            }
                        }
                    });


            alertDialogBuilder.setCancelable(true).setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            ;
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }

        public void getNagaView(final Context ctx, final Referee referee) {
            LayoutInflater li = LayoutInflater.from(ctx);
            View promptsView = li.inflate(R.layout.adapter_model_dialog_penilaian_naga, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    ctx);
            alertDialogBuilder.setView(promptsView);
            //initiate data
            final TextView tv_amdp_team_name = promptsView.findViewById(R.id.tv_amdp_team_name_naga);
            final TextView tv_amdp_no_urut = promptsView.findViewById(R.id.tv_amdp_no_urut_naga);
            final TextView tv_amdp_referee = promptsView.findViewById(R.id.tv_amdp_referee_naga);
            final TextView tv_amdp_naga_n1 = promptsView.findViewById(R.id.tv_amdp_naga_n1);
            final TextView tv_amdp_naga_n2 = promptsView.findViewById(R.id.tv_amdp_naga_n2);
            final TextView tv_amdp_naga_n3 = promptsView.findViewById(R.id.tv_amdp_naga_n3);
            final TextView tv_amdp_naga_n4 = promptsView.findViewById(R.id.tv_amdp_naga_n4);
            final TextView tv_amdp_naga_n5 = promptsView.findViewById(R.id.tv_amdp_naga_n5);
            final TextView tv_amdp_naga_p1 = promptsView.findViewById(R.id.tv_amdp_naga_p1);
            final TextView tv_amdp_naga_p2 = promptsView.findViewById(R.id.tv_amdp_naga_p2);
            final TextView tv_amdp_naga_p3 = promptsView.findViewById(R.id.tv_amdp_naga_p3);
            final TextView tv_amdp_naga_p4 = promptsView.findViewById(R.id.tv_amdp_naga_p4);
            final TextView tv_amdp_naga_p5 = promptsView.findViewById(R.id.tv_amdp_naga_p5);
            final TextView tv_amdp_naga_kesulitan = promptsView.findViewById(R.id.tv_amdp_naga_kesulitan);

            final TextView tv_amdp_total_nilai_naga = promptsView.findViewById(R.id.tv_amdp_total_nilai_naga);
            final TextView tv_amdp_total_pengurangan_naga = promptsView.findViewById(R.id.tv_amdp_total_pengurangan_naga);
            final TextView tv_amdp_grand_total_naga = promptsView.findViewById(R.id.tv_amdp_grand_total_naga);


            //set data
            tv_amdp_naga_n1.setText("0");
            tv_amdp_naga_n2.setText("0");
            tv_amdp_naga_n3.setText("0");
            tv_amdp_naga_n4.setText("0");
            tv_amdp_naga_n5.setText("0");
            tv_amdp_naga_p1.setText("0");
            tv_amdp_naga_p2.setText("0");
            tv_amdp_naga_p3.setText("0");
            tv_amdp_naga_p4.setText("0");
            tv_amdp_naga_p5.setText("0");
            tv_amdp_naga_kesulitan.setText("");

            tv_amdp_total_nilai_naga.setText("0");
            tv_amdp_total_pengurangan_naga.setText("0");
            tv_amdp_grand_total_naga.setText("0");
            DocumentReference teamref = db.collection("events").document(eventId)
                    .collection("team").document(teamId);
            teamref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        team = documentSnapshot.toObject(Team.class);
                        team.setKey(documentSnapshot.getId());
                        tv_amdp_team_name.setText(
                                ctx.getApplicationContext().getString(R.string.team_name) + " "
                                        + team.getTeam_name());
                        tv_amdp_no_urut.setText(
                                ctx.getApplicationContext().getString(R.string.no_urut) + " "
                                        + team.getNo_urut() + "");
                        tv_amdp_referee.setText(
                                ctx.getApplicationContext().getString(R.string.referee) + " Juri " + referee.getNumber() + " - "
                                        + referee.getName());

                    }
                }
            });

            teamref.collection("penilaian").document(referee.getKey())
                    .collection("field").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot d2 : task.getResult()) {
                        if (d2.exists()) {
                            Penilaian penilaian = d2.toObject(Penilaian.class);
                            penilaian.setKey(d2.getId());
                            if (d2.getId().equals("et_amdp_naga_n1")) {
                                tv_amdp_naga_n1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_naga_n2")) {
                                tv_amdp_naga_n2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_naga_n3")) {
                                tv_amdp_naga_n3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_naga_n4")) {
                                tv_amdp_naga_n4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_naga_n5")) {
                                tv_amdp_naga_n5.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_naga_p1")) {
                                tv_amdp_naga_p1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_naga_p2")) {
                                tv_amdp_naga_p2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_naga_p3")) {
                                tv_amdp_naga_p3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_naga_p4")) {
                                tv_amdp_naga_p4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_naga_p5")) {
                                tv_amdp_naga_p5.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_naga_kesulitan")) {
                                tv_amdp_naga_kesulitan.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            }
                        }
                    }
                }
            });

            teamref.collection("penilaian").document(referee.getKey())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                            if (value.exists()) {
                                RefereePenilaian rp = value.toObject(RefereePenilaian.class);
                                rp.setKey(value.getId());

                                tv_amdp_total_nilai_naga.setText(
                                        String.format("%.2f", rp.getTotal_nilai()) + "");
                                tv_amdp_total_pengurangan_naga.setText(
                                        String.format("%.2f", rp.getTotal_potongan()) + "");
                                tv_amdp_grand_total_naga.setText(
                                        String.format("%.2f", rp.getGrand_total()) + "");

                            }
                        }
                    });

            alertDialogBuilder.setCancelable(true).setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            ;
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
        }

        public void getPekingsaiView(final Context ctx, final Referee referee) {
            LayoutInflater li = LayoutInflater.from(ctx);
            View promptsView = li.inflate(R.layout.adapter_model_dialog_penilaian_pekingsai, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    ctx);
            alertDialogBuilder.setView(promptsView);
            //initiate data
            final TextView tv_ap_pekingsai_team_name = promptsView.findViewById(R.id.tv_ap_pekingsai_team_name);
            final TextView tv_ap_pekingsai_no_urut = promptsView.findViewById(R.id.tv_ap_pekingsai_no_urut);
            final TextView tv_amdp_referee_pekingsai = promptsView.findViewById(R.id.tv_amdp_referee_pekingsai);
            final TextView tv_amdp_pekingsai_n1 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n1);
            final TextView tv_amdp_pekingsai_n2 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n2);
            final TextView tv_amdp_pekingsai_n3 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n3);
            final TextView tv_amdp_pekingsai_n4 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n4);
            final TextView tv_amdp_pekingsai_n5 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n5);
            final TextView tv_amdp_pekingsai_n6 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n6);
            final TextView tv_amdp_pekingsai_n7 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n7);
            final TextView tv_amdp_pekingsai_n8 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n8);
            final TextView tv_amdp_pekingsai_n9 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n9);
            final TextView tv_amdp_pekingsai_p1 = promptsView.findViewById(R.id.tv_amdp_pekingsai_p1);
            final TextView tv_amdp_pekingsai_p2 = promptsView.findViewById(R.id.tv_amdp_pekingsai_p2);
            final TextView tv_amdp_pekingsai_p3 = promptsView.findViewById(R.id.tv_amdp_pekingsai_p3);
            final TextView tv_amdp_pekingsai_p4 = promptsView.findViewById(R.id.tv_amdp_pekingsai_p4);
            final TextView tv_amdp_pekingsai_kesulitan = promptsView.findViewById(R.id.tv_amdp_pekingsai_kesulitan);
            final TextView tv_ap_pekingsai_total_penilaian = promptsView.findViewById(R.id.tv_ap_pekingsai_total_penilaian);
            final TextView tv_ap_pekingsai_nilai_total_pengurangan = promptsView.findViewById(R.id.tv_ap_pekingsai_nilai_total_pengurangan);
            final TextView tv_ap_pekingsai_grand_total = promptsView.findViewById(R.id.tv_ap_pekingsai_grand_total);


            //set data
            tv_amdp_pekingsai_n1.setText("0");
            tv_amdp_pekingsai_n2.setText("0");
            tv_amdp_pekingsai_n3.setText("0");
            tv_amdp_pekingsai_n4.setText("0");
            tv_amdp_pekingsai_n5.setText("0");
            tv_amdp_pekingsai_n6.setText("0");
            tv_amdp_pekingsai_n7.setText("0");
            tv_amdp_pekingsai_n8.setText("0");
            tv_amdp_pekingsai_n9.setText("0");
            tv_amdp_pekingsai_p1.setText("0");
            tv_amdp_pekingsai_p2.setText("0");
            tv_amdp_pekingsai_p3.setText("0");
            tv_amdp_pekingsai_p4.setText("0");
            tv_amdp_pekingsai_kesulitan.setText("");
            tv_ap_pekingsai_total_penilaian.setText("0");
            tv_ap_pekingsai_nilai_total_pengurangan.setText("0");
            tv_ap_pekingsai_grand_total.setText("0");
            DocumentReference teamref = db.collection("events").document(eventId)
                    .collection("team").document(teamId);
            teamref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (documentSnapshot.exists()) {
                        team = documentSnapshot.toObject(Team.class);
                        team.setKey(documentSnapshot.getId());
                        tv_ap_pekingsai_team_name.setText(
                                ctx.getApplicationContext().getString(R.string.team_name) + " "
                                        + team.getTeam_name());
                        tv_ap_pekingsai_no_urut.setText(
                                ctx.getApplicationContext().getString(R.string.no_urut) + " "
                                        + team.getNo_urut() + "");
                        tv_amdp_referee_pekingsai.setText(
                                ctx.getApplicationContext().getString(R.string.referee) + " Juri " + referee.getNumber() + " - "
                                        + referee.getName());

                    }
                }
            });

            teamref.collection("penilaian").document(referee.getKey())
                    .collection("field").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    for (QueryDocumentSnapshot d2 : task.getResult()) {
                        if (d2.exists()) {
                            Penilaian penilaian = d2.toObject(Penilaian.class);
                            penilaian.setKey(d2.getId());
                            if (d2.getId().equals("et_amdp_pekingsai_n1")) {
                                tv_amdp_pekingsai_n1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_pekingsai_n2")) {
                                tv_amdp_pekingsai_n2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_pekingsai_n3")) {
                                tv_amdp_pekingsai_n3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_pekingsai_n4")) {
                                tv_amdp_pekingsai_n4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_pekingsai_n5")) {
                                tv_amdp_pekingsai_n5.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_pekingsai_n6")) {
                                tv_amdp_pekingsai_n6.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_pekingsai_n7")) {
                                tv_amdp_pekingsai_n7.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_pekingsai_n8")) {
                                tv_amdp_pekingsai_n8.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_pekingsai_n9")) {
                                tv_amdp_pekingsai_n9.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_ap_pekingsai_p1")) {
                                tv_amdp_pekingsai_p1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_ap_pekingsai_p2")) {
                                tv_amdp_pekingsai_p2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_ap_pekingsai_p3")) {
                                tv_amdp_pekingsai_p3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_ap_pekingsai_p4")) {
                                tv_amdp_pekingsai_p4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            } else if (d2.getId().equals("et_amdp_pekingsai_kesulitan")) {
                                tv_amdp_pekingsai_kesulitan.setText(String.format("%.2f", penilaian.getNilai()) + "");
                            }
                        }
                    }
                }
            });

            teamref.collection("penilaian").document(referee.getKey())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                            if (value.exists()) {
                                RefereePenilaian rp = value.toObject(RefereePenilaian.class);
                                rp.setKey(value.getId());

                                tv_ap_pekingsai_total_penilaian.setText(
                                        String.format("%.2f", rp.getTotal_nilai()) + "");
                                tv_ap_pekingsai_nilai_total_pengurangan.setText(
                                        String.format("%.2f", rp.getTotal_potongan()) + "");
                                tv_ap_pekingsai_grand_total.setText(
                                        String.format("%.2f", rp.getGrand_total()) + "");

                            }
                        }
                    });
            alertDialogBuilder.setCancelable(true).setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            ;
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        }

        public void bindItem(final Context ctx, final Referee referee) {

            db.collection("events").document(eventId)
                    .collection("team").document(teamId).collection("penilaian").document(referee.getKey())
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                            if (value.exists()) {
                                RefereePenilaian rp = value.toObject(RefereePenilaian.class);
                                rp.setKey(value.getId());
                                tv_amrl_grand_total.setText("Grand Total : " + String.format("%.2f", rp.getGrand_total()) + "");
                                if(rp.getIsEditable()==1){
                                    btn_amrl_nonedit.setVisibility(View.VISIBLE);
                                    btn_amrl_edit.setVisibility(View.GONE);
                                }else{
                                    btn_amrl_edit.setVisibility(View.VISIBLE);
                                    btn_amrl_nonedit.setVisibility(View.GONE);
                                }

                            }
                        }
                    });
            tv_amrl_refereeName.setText("Juri " + referee.getNumber() + " - " + referee.getName());
            btn_amrl_lihat_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (events.getThemes().equals("Naga")) {
                        getNagaView(ctx, referee);
                    } else if (events.getThemes().equals("Barongsai Taolu Bebas")) {
                        getTaoluView(ctx, referee);
                    } else if (events.getThemes().equals("Pekingsai")) {
                        getPekingsaiView(ctx, referee);
                    } else {
                        getTraditionalView(ctx, referee);
                    }

                }
            });
            btn_amrl_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new KAlertDialog(ctx, KAlertDialog.WARNING_TYPE)
                            .setTitleText("Memberikan Akses Edit?")
                            .setContentText("Apakah Anda yakin ingin memberikan akses edit kepada juri ini")
                            .setConfirmText("Ya")
                            .setCancelText("Tidak")
                            .showCancelButton(true)
                            .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                @Override
                                public void onClick(KAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                @Override
                                public void onClick(KAlertDialog sDialog) {
                                    btn_amrl_edit.setVisibility(View.GONE);
                                    btn_amrl_nonedit.setVisibility(View.VISIBLE);
                                    final Map<String, Integer> dataToSave = new HashMap<>();
                                    dataToSave.put("isEditable", 1);
                                    db.collection("events").document(eventId)
                                            .collection("team").document(teamId)
                                            .collection("penilaian").document(referee.getKey())
                                            .set(dataToSave, SetOptions.merge());
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
            });
            btn_amrl_nonedit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new KAlertDialog(ctx, KAlertDialog.WARNING_TYPE)
                            .setTitleText("Mencabut Akses Edit?")
                            .setContentText("Apakah Anda yakin ingin mencabut akses edit dari juri ini")
                            .setConfirmText("Ya")
                            .setCancelText("Tidak")
                            .showCancelButton(true)
                            .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                                @Override
                                public void onClick(KAlertDialog sDialog) {
                                    sDialog.cancel();
                                }
                            })
                            .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                                @Override
                                public void onClick(KAlertDialog sDialog) {
                                    btn_amrl_edit.setVisibility(View.VISIBLE);
                                    btn_amrl_nonedit.setVisibility(View.GONE);
                                    final Map<String, Integer> dataToSave = new HashMap<>();
                                    dataToSave.put("isEditable", 0);
                                    db.collection("events").document(eventId)
                                            .collection("team").document(teamId)
                                            .collection("penilaian").document(referee.getKey())
                                            .set(dataToSave, SetOptions.merge());
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
            });
        }

    }

    public AdapterListReferee(Context ctx, Vector<Referee> dataReferee, final String eventId, String teamId) {
        this.ctx = ctx;
        this.dataReferee = dataReferee;
        this.eventId = eventId;
        this.teamId = teamId;

        db = FirebaseFirestore.getInstance();
        penilaian = new PenilaianTraditional();
        team = new Team();
        events = new Events();
        db.collection("events").document(eventId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    events = value.toObject(Events.class);
                    events.setCode(value.getId());
                }
            }
        });
    }
}
