package com.packag.eventmonitor;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Data.Team;

import java.util.Vector;

public class FirestoreController {
    FirebaseFirestore db;
    public FirestoreController() {
        db=FirebaseFirestore.getInstance();
    }
    public void deleteTeam(String eventId,String teamId){
        db.collection("events").document(eventId)
                .collection("team")
                .document(teamId).delete();
    }
    public void recalculateTotalTeam(final String eventId){
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
                            .update("total_team",teams.size());
                }
            }
        });

    }
    public void addTeam(String eventId,Team team){
        CollectionReference teamRef = db.collection("events")
                .document(eventId)
                .collection("team");
        teamRef .add(team);
    }

    public void updateTeam(String eventId,String teamId,Team team){
        DocumentReference teamRef = db.collection("events")
                .document(eventId)
                .collection("team")
                .document(teamId);
        teamRef .set(team);
    }
    public void updateEventStatus(String eventId,int status){
        db.collection("events")
                .document(eventId)
                .update("status",status);
    }
}