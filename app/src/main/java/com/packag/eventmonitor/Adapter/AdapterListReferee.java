package com.packag.eventmonitor.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.packag.eventmonitor.Data.Penilaian;
import com.packag.eventmonitor.Data.Referee;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.R;

import java.util.Vector;

import javax.annotation.Nullable;

public class AdapterListReferee extends RecyclerView.Adapter<AdapterListReferee.MyViewHolder> {
    Context ctx;
    Vector<Referee> dataReferee;
    String eventId;
    String teamId;
    FirebaseFirestore db;
    Penilaian penilaian;
    Team team;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //TODO ganti view
        return new MyViewHolder(LayoutInflater.from(ctx).inflate(R.layout.adapter_model_referee_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bindItem(ctx,dataReferee.get(position));
    }

    @Override
    public int getItemCount() {
        return dataReferee.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_amrl_refereeName;
        Button btn_amrl_lihat_detail;
        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            tv_amrl_refereeName = itemView.findViewById(R.id.tv_amrl_refereeName);
            btn_amrl_lihat_detail = itemView.findViewById(R.id.btn_amrl_lihat_detail);
        }
        public void bindItem(final Context ctx, final Referee referee){
            tv_amrl_refereeName.setText("Juri "+referee.getNumber()+" - "+referee.getName());
            btn_amrl_lihat_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LayoutInflater li = LayoutInflater.from(ctx);
                    View promptsView = li.inflate(R.layout.adapter_model_dialog_penilaian, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            ctx);
                    alertDialogBuilder.setView(promptsView);
                    //initiate data
                    final TextView tv_amdp_team_name = promptsView.findViewById(R.id.tv_amdp_team_name);
                    final TextView tv_amdp_no_urut = promptsView.findViewById(R.id.tv_amdp_no_urut);
                    final TextView tv_amdp_referee = promptsView.findViewById(R.id.tv_amdp_referee);
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
                    final TextView tv_amdp_total_kotor = promptsView.findViewById(R.id.tv_amdp_total_kotor);
                    final TextView tv_amdp_total_pengurangan = promptsView.findViewById(R.id.tv_amdp_total_pengurangan);
                    final TextView tv_amdp_total_bersih = promptsView.findViewById(R.id.tv_amdp_total_bersih);


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
                    tv_amdp_total_kotor.setText("0");
                    tv_amdp_total_pengurangan.setText("0");
                    tv_amdp_total_bersih.setText("0");
                    DocumentReference teamref = db.collection("events").document(eventId)
                            .collection("team").document(teamId);
                    teamref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if(documentSnapshot.exists()){
                                team = documentSnapshot.toObject(Team.class);
                                team.setKey(documentSnapshot.getId());
                                tv_amdp_team_name.setText(
                                        ctx.getApplicationContext().getString(R.string.team_name)+" "
                                                +team.getTeam_name());
                                tv_amdp_no_urut.setText(
                                        ctx.getApplicationContext().getString(R.string.no_urut)+" "
                                                +team.getNo_urut()+"");
                                tv_amdp_referee.setText(
                                        ctx.getApplicationContext().getString(R.string.referee)+" Juri "+referee.getNumber()+" - "
                                                +referee.getName());

                            }
                        }
                    });

                    teamref.collection("penilaian").document(referee.getKey())
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                    if(documentSnapshot.exists()){
                                        penilaian=documentSnapshot.toObject(Penilaian.class);
                                        penilaian.setKey(documentSnapshot.getId());

                                        tv_amdp_n1.setText( String.format("%.2f", penilaian.getN1())+"");
                                        tv_amdp_n2.setText( String.format("%.2f", penilaian.getN2())+"");
                                        tv_amdp_n3.setText( String.format("%.2f", penilaian.getN3())+"");
                                        tv_amdp_n4.setText( String.format("%.2f", penilaian.getN4())+"");
                                        tv_amdp_n5.setText( String.format("%.2f", penilaian.getN5())+"");
                                        tv_amdp_n6.setText( String.format("%.2f", penilaian.getN6())+"");
                                        tv_amdp_n7.setText( String.format("%.2f", penilaian.getN7())+"");
                                        tv_amdp_n8.setText( String.format("%.2f", penilaian.getN8())+"");
                                        tv_amdp_n9.setText( String.format("%.2f", penilaian.getN9())+"");
                                        tv_amdp_n10.setText( String.format("%.2f", penilaian.getN10())+"");
                                        tv_amdp_ks1.setText( String.format("%.2f", penilaian.getKs1())+"");
                                        tv_amdp_ks2.setText( String.format("%.2f", penilaian.getKs2())+"");
                                        tv_amdp_ks3.setText( String.format("%.2f", penilaian.getKs3())+"");
                                        tv_amdp_ks4.setText( String.format("%.2f", penilaian.getKs4())+"");
                                        tv_amdp_total_kotor.setText( String.format("%.2f", penilaian.getTk())+"");
                                        tv_amdp_total_pengurangan.setText( String.format("%.2f", penilaian.getP())+"");
                                        tv_amdp_total_bersih.setText (String.format("%.2f", penilaian.getTb())+"");
                                    }
                                }
                            });
                    alertDialogBuilder.setCancelable(true) .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });;
                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            });
        }
    }
    public AdapterListReferee(Context ctx, Vector<Referee> dataReferee,String eventId,String teamId){
        this.ctx = ctx;
        this.dataReferee = dataReferee;
        this.eventId = eventId;
        this.teamId = teamId;
        db = FirebaseFirestore.getInstance();
        penilaian = new Penilaian();
        team = new Team();
    }
}
