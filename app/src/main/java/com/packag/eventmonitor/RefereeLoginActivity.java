package com.packag.eventmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Data.Events;
import com.packag.eventmonitor.Data.Referee;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.Util.Session;

import java.sql.Ref;
import java.util.Vector;

public class RefereeLoginActivity extends AppCompatActivity {
    EditText et_arl_kode;
    EditText et_arl_nama;
    Button btn_arl_login;
    Session session;
    Boolean flagFound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee_login);
        initializeComponent();
        setListener();
    }
    private void initializeComponent(){
        et_arl_kode = findViewById(R.id.et_arl_kode);
        et_arl_nama = findViewById(R.id.et_arl_nama);
        btn_arl_login = findViewById(R.id.btn_arl_login);
        session = new Session(getApplicationContext());
    }
    private void setListener(){
        btn_arl_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference events = db.collection("events");
                events.whereEqualTo("status",1).whereEqualTo("code",et_arl_kode.getText().toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            Events events = new Events();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {


                                    events = document.toObject(Events.class);
                                    events.setKey(document.getId());


                                    flagFound = true;

                                    break;
                                }
                            }
                            if(flagFound){
                                boolean flagPermittedLogin=true;

                                session.setData("loginType","referee");
                                session.setData("eventId",events.getKey());
                                if(session.getData("refereeId").equals("")) {
                                    //new login
                                    //validate limit juri
                                    final Vector<Referee> tempReferee = new Vector<Referee>();
                                    db.collection("events").document(events.getKey())
                                            .collection("referee").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot d2 : task.getResult()) {
                                                    Referee tempRefereeClass = d2.toObject(Referee.class);

                                                    tempReferee.add(tempRefereeClass);
                                                }

                                            }
                                        }
                                    });
                                    events.setReferee(tempReferee);
                                    if(events.getTotal_referee()>=tempReferee.size()){
                                        flagPermittedLogin=false;
                                    }


                                    DocumentReference refereeRef=  db.collection("events").document(events.getKey())
                                            .collection("referee").document();
                                    String refereeKey = refereeRef.getId();
                                    refereeRef.set(new Referee(et_arl_nama.getText().toString()));
                                    session.setData("refereeId", refereeKey);
                                }else{
                                    //old login
                                    DocumentReference refereeRef=  db.collection("events").document(events.getKey())
                                            .collection("referee")
                                            .document(session.getData("refereeId"))
                                            ;
                                    refereeRef.set(new Referee(et_arl_nama.getText().toString()));
                                }
                                if(flagPermittedLogin){
                                    Intent i = new Intent(RefereeLoginActivity.this,RefereeActivity.class);
                                    startActivity(i);
                                    finish();
                                }else{
                                    new KAlertDialog(RefereeLoginActivity.this, KAlertDialog.ERROR_TYPE)
                                            .setTitleText("Error!")
                                            .setContentText("Limit Juri sudah penuh!")
                                            .show();
                                }

                            }else{
                                new KAlertDialog(RefereeLoginActivity.this, KAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Kode Event tidak ditemukan!")
                                        .show();
                            }


                        }
                    }
                });
            }
        });
    }
}
