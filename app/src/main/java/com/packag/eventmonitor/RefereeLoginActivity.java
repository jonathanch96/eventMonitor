package com.packag.eventmonitor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Adapter.AdapterListReferee;
import com.packag.eventmonitor.Data.Events;
import com.packag.eventmonitor.Data.Referee;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.Util.Session;
import com.packag.eventmonitor.Util.Setting;

import java.sql.Ref;
import java.util.Vector;

public class RefereeLoginActivity extends AppCompatActivity {
    EditText et_arl_kode;
    EditText et_arl_nama;
    Button btn_arl_login;
    ImageView iv_arl_scan;
    Session session;
    Boolean flagFound = false;
    final int REQUEST_CODE = 999;
    FirestoreController fc = new FirestoreController();
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
        iv_arl_scan = findViewById(R.id.iv_arl_scan);
        session = new Session(getApplicationContext());
        Setting.checkAppVersion(RefereeLoginActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE  && resultCode  == RESULT_OK) {

                String code = data.getStringExtra("code");
                et_arl_kode.setText(code);
                et_arl_nama.requestFocus();
            }
        } catch (Exception ex) {
            Toast.makeText(RefereeLoginActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setListener(){
        iv_arl_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(RefereeLoginActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted

                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(RefereeLoginActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                1);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.

                } else {
                    // Permission has already been granted
                    Intent i = new Intent(RefereeLoginActivity.this,SimpleScannerActivity.class);
                    startActivityForResult(i,REQUEST_CODE);
                }


            }
        });
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


                                session.setData("loginType","referee");
                                session.setData("eventId",events.getKey());
                                fc.refereeNumbering(events.getKey());


                                if(session.getData("refereeId").equals("")) {


                                    DocumentReference refereeRef=  db.collection("events").document(events.getKey())
                                            .collection("referee").document();
                                    String refereeKey = refereeRef.getId();
                                    refereeRef.set(new Referee(et_arl_nama.getText().toString()));
                                    session.setData("refereeId", refereeKey);
                                }else{
                                    DocumentReference refereeRef=  db.collection("events").document(events.getKey())
                                            .collection("referee")
                                            .document(session.getData("refereeId"))
                                            ;
                                    refereeRef.set(new Referee(et_arl_nama.getText().toString()));

                                }


                                //new login
                                //validate limit juri
//                                final Vector<Referee> tempReferee = new Vector<Referee>();
//                                final int maxReferee = events.getTotal_referee();
//                                db.collection("events").document(events.getKey())
//                                        .collection("referee").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                        if (task.isSuccessful()) {
//                                            for (QueryDocumentSnapshot d2 : task.getResult()) {
//                                                Referee tempRefereeClass = d2.toObject(Referee.class);
//
//                                                tempReferee.add(tempRefereeClass);
//                                            }
//                                            Log.d("Debug",tempReferee.toString());
//                                            if(maxReferee>=tempReferee.size()){
//                                                Intent i = new Intent(RefereeLoginActivity.this,RefereeActivity.class);
//                                                startActivity(i);
//                                                finish();
//                                            }else{
//                                                new KAlertDialog(RefereeLoginActivity.this, KAlertDialog.ERROR_TYPE)
//                                                        .setTitleText("Error!")
//                                                        .setContentText("Limit Juri sudah penuh!")
//                                                        .show();
//                                            }
//
//
//                                        }
//                                    }
//                                });
                                Intent i = new Intent(RefereeLoginActivity.this,RefereeActivity.class);
                                startActivity(i);
                                finish();


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
