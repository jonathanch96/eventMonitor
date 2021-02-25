package com.packag.eventmonitor;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.packag.eventmonitor.Adapter.AdapterListReferee;
import com.packag.eventmonitor.Data.Penilaian;
import com.packag.eventmonitor.Data.Referee;
import com.packag.eventmonitor.Data.RefereePenilaian;
import com.packag.eventmonitor.Data.Team;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Vector;

public class FirestoreController {
    FirebaseFirestore db;
    int counter;

    public FirestoreController() {
        db = FirebaseFirestore.getInstance();
    }

    public void deleteTeam(String eventId, String teamId) {
        db.collection("events").document(eventId)
                .collection("team")
                .document(teamId).delete();
    }

    public void recalculateNilaiBersih(final String eventId, final String teamId) {
        final Vector<RefereePenilaian> dataReferee = new Vector<RefereePenilaian>();
        db.collection("events").document(eventId).collection("team")
                .document(teamId).collection("penilaian").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d2 : task.getResult()) {
                        if (d2.exists()) {
                            RefereePenilaian rp = d2.toObject(RefereePenilaian.class);
                            rp.setKey(d2.getId());
                            dataReferee.add(rp);
                        }
                    }
                    double nilai_bersih = 0;
                    int jumlah_juri = 0;
                    double total_nilai = 0;
                    int division = 1;
                    int total_data = dataReferee.size();
                    double [] nilai_perjuri  = new double[total_data];
                    for(RefereePenilaian trp:dataReferee){
                        total_nilai+=trp.getGrand_total();
                        nilai_perjuri[jumlah_juri]=trp.getGrand_total();
                        jumlah_juri++;
                    }
                    Arrays.sort(nilai_perjuri);
                    if(total_data==9){
                        division = total_data-2;
                        total_nilai-=nilai_perjuri[0];
                        total_nilai-=nilai_perjuri[1];
                        total_nilai-=nilai_perjuri[total_data-1];
                        total_nilai-=nilai_perjuri[total_data-2];
                    }else if(total_data==7||total_data==5){
                        division= total_data-1;
                        total_nilai-=nilai_perjuri[0];
                        total_nilai-=nilai_perjuri[total_data-1];
                    }
                    nilai_bersih = Math.round(total_nilai/division);
                    Team temp_team = new Team();
                    temp_team.setNilai_bersih(nilai_bersih);
                    db.collection("events").document(teamId)
                            .set(temp_team, SetOptions.merge());
                }
            }
        });

    }

    public void refereeNumbering(final String eventId) {
        final Vector<Referee> dataReferee = new Vector<Referee>();
        counter = 1;
        db.collection("events").document(eventId).collection("referee")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d2 : task.getResult()) {
                        if (d2.exists()) {
                            Referee referee = d2.toObject(Referee.class);
                            referee.setKey(d2.getId());
                            Log.d("debug", referee.getNumber() + " TEST");
                            if (d2.get("number") != null && referee.getNumber() != 0) {
                                counter = referee.getNumber();

                            } else {
                                counter++;
                                referee.setNumber(counter);

                                db.collection("events").document(eventId).
                                        collection("referee").document(d2.getId()).set(referee);
                            }
                            dataReferee.add(referee);


                        }

                    }
                }
            }
        });


    }

    public void recalculateTotalTeam(final String eventId) {
        final Vector<Team> teams = new Vector<Team>();
        CollectionReference teamRef = db.collection("events")
                .document(eventId)
                .collection("team");
        teamRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot d2 : task.getResult()) {
                        Team team = d2.toObject(Team.class);

                        teams.add(team);
                    }
                    db.collection("events")
                            .document(eventId)
                            .update("total_team", teams.size());
                }
            }
        });

    }

    public void addTeam(String eventId, Team team) {
        CollectionReference teamRef = db.collection("events")
                .document(eventId)
                .collection("team");
        teamRef.add(team);
    }

    public void updateTeam(String eventId, String teamId, Team team) {
        DocumentReference teamRef = db.collection("events")
                .document(eventId)
                .collection("team")
                .document(teamId);
        teamRef.set(team);
    }

    public void updateEventStatus(String eventId, int status) {
        db.collection("events")
                .document(eventId)
                .update("status", status);
    }
}
