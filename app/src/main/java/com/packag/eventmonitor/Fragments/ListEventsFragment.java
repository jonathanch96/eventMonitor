package com.packag.eventmonitor.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Adapter.AdapterListEvent;
import com.packag.eventmonitor.AddEventActivity;
import com.packag.eventmonitor.Data.Events;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.R;

import java.util.Vector;

public class ListEventsFragment extends Fragment {
    View myView;
    ListView lv_fle;
    FloatingActionButton fab_fle;
    Vector<Events> dataEvent;
    Vector<Team> dataTeam;
    FirebaseFirestore db;
    Context ctx;
    private static int REQUEST = 9;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView =  inflater.inflate(R.layout.fragment_list_event_container,container,false);
        initiateComponent();

        setListener();
        return  myView;
    }
    private void initiateComponent(){
        lv_fle = myView.findViewById(R.id.ll_fle);
        fab_fle = myView.findViewById(R.id.fab_fle);
        db = FirebaseFirestore.getInstance();
        ctx = this.getContext();
        dataEvent=new Vector<Events>();
        fetchData();
    }
    private void fetchData(){
        CollectionReference events = db.collection("events");
        events.orderBy("created_at", Query.Direction.DESCENDING);
        //events.whereEqualTo("status",1);
        events.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                dataEvent=new Vector<Events>();

                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    dataTeam=new Vector<Team>();
                    final Events eventClass = document.toObject(Events.class);
                    eventClass.setKey(document.getId());
                    db.collection("events").document(document.getId())
                            .collection("team").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot d2 : task.getResult()) {
                                    Team team = d2.toObject(Team.class);

                                    dataTeam.add(team);
                                }

                                eventClass.setTeam(dataTeam);
                            }
                        }
                    });

                    dataEvent.add(eventClass);
                }
                lv_fle.setAdapter(new AdapterListEvent(ctx,dataEvent));
            }
        });




    }
    private void setListener() {
        fab_fle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddEventActivity.class);
                startActivityForResult(i, REQUEST);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == Activity.RESULT_OK&&requestCode== REQUEST){
            new KAlertDialog(getActivity(), KAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Good job!")
                    .setContentText(data.getStringExtra("msg"))
                    .show();
        }
    }
}
