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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class RefereeLoginActivity extends AppCompatActivity {
    EditText et_arl_kode;
    EditText et_arl_nama;
    Spinner sp_ae_no_urut;
    Button btn_arl_login;
    ImageView iv_arl_scan;
    Session session;
    Boolean flagFound = false;
    final int REQUEST_CODE = 999;
    FirestoreController fc = new FirestoreController();
    Events events;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee_login);
        initializeComponent();
        setListener();
        fetchSpinnerData();
    }
    private void fetchSpinnerData(){
        et_arl_kode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Log.d("debug","Edit Text et_arl_kode lost focus");
                    // code to execute when EditText loses focus
                    db.collection("events")
                            .whereEqualTo("code", et_arl_kode.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {


                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {


                                        events = document.toObject(Events.class);
                                        events.setKey(document.getId());


                                        flagFound = true;

                                        break;
                                    }
                                }
                                if (flagFound) {
                                    db.collection("events")
                                            .document(events.getKey())
                                            .collection("referee")
                                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                                    List<String> no_urut_existing = new ArrayList<String>();
                                                    for (QueryDocumentSnapshot doc : value) {
                                                        Referee temp_referee = doc.toObject(Referee.class);
                                                        temp_referee.setKey(doc.getId());
                                                        no_urut_existing.add(temp_referee.getNumber()+"");
                                                    }
                                                    final List<String> no_urut = new ArrayList<String>();
                                                    for (int i = 1;i<=events.getTotal_referee();i++){
                                                        boolean flag_no_urut_exists = false;
                                                        for(int j = 0 ; j<no_urut_existing.size();j++) {
                                                            if(no_urut_existing.get(j).equals(i+"")){
                                                                flag_no_urut_exists=true;
                                                            }
                                                        }
                                                        if(!flag_no_urut_exists){
                                                            no_urut.add(i + "");
                                                        }

                                                    }
                                                    

                                                    if (!session.getData("refereeId").equals("")) {
                                                        db.collection("events")
                                                                .document(events.getKey())
                                                                .collection("referee")
                                                                .document(session.getData("refereeId"))
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                        if (task.isSuccessful()) {
                                                                            DocumentSnapshot document = task.getResult();
                                                                            if (document.exists()) {
                                                                                Referee tempReferee = document.toObject(Referee.class);
                                                                                tempReferee.setKey(document.getId());
                                                                                List<String> no_urut_single = new ArrayList<String>();
                                                                                no_urut_single.add(tempReferee.getNumber()+"");

                                                                                ArrayAdapter<String> noUrutAdapter =
                                                                                        new ArrayAdapter<String>(RefereeLoginActivity.this,
                                                                                                android.R.layout.simple_spinner_item, no_urut_single);
                                                                                noUrutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                                                sp_ae_no_urut.setAdapter(noUrutAdapter);
                                                                            }else{
                                                                                ArrayAdapter<String> noUrutAdapter =
                                                                                        new ArrayAdapter<String>(RefereeLoginActivity.this,
                                                                                                android.R.layout.simple_spinner_item, no_urut);
                                                                                noUrutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                                                sp_ae_no_urut.setAdapter(noUrutAdapter);
                                                                            }

                                                                        }
                                                                    }
                                                                });
                                                    }else{
                                                        ArrayAdapter<String> noUrutAdapter =
                                                                new ArrayAdapter<String>(RefereeLoginActivity.this,
                                                                        android.R.layout.simple_spinner_item, no_urut);
                                                        noUrutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                                        sp_ae_no_urut.setAdapter(noUrutAdapter);
                                                    }


                                                }
                                            });

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
            }
        });

    }
    private void initializeComponent() {
        et_arl_kode = findViewById(R.id.et_arl_kode);
        et_arl_nama = findViewById(R.id.et_arl_nama);
        btn_arl_login = findViewById(R.id.btn_arl_login);
        iv_arl_scan = findViewById(R.id.iv_arl_scan);
        sp_ae_no_urut = findViewById(R.id.sp_ae_no_urut);
        session = new Session(getApplicationContext());
        Setting.checkAppVersion(RefereeLoginActivity.this);
        events = new Events();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {

                String code = data.getStringExtra("code");
                et_arl_kode.setText(code);
                et_arl_nama.requestFocus();
            }
        } catch (Exception ex) {
            Toast.makeText(RefereeLoginActivity.this, ex.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }
    private void setListener() {
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
                    Intent i = new Intent(RefereeLoginActivity.this, SimpleScannerActivity.class);
                    startActivityForResult(i, REQUEST_CODE);
                }


            }
        });
        btn_arl_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag_validate = true;
                if(et_arl_kode.getText().equals("")){
                    flag_validate = false;
                    new KAlertDialog(RefereeLoginActivity.this, KAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Kode Event harus diisi")
                            .show();
                }else if(et_arl_nama.getText().equals("")){
                    flag_validate = false;
                    new KAlertDialog(RefereeLoginActivity.this, KAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Nama harus diisi")
                            .show();
                }else if (sp_ae_no_urut.getSelectedItem().toString().equals("")){
                    flag_validate = false;
                    new KAlertDialog(RefereeLoginActivity.this, KAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("No urut harus dipilih")
                            .show();
                }

                if (flag_validate) {

                    CollectionReference eventsRef = db.collection("events");
                    eventsRef.whereEqualTo("status", 1).whereEqualTo("code", et_arl_kode.getText().toString())
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {


                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {


                                        events = document.toObject(Events.class);
                                        events.setKey(document.getId());


                                        flagFound = true;

                                        break;
                                    }
                                }
                                if (flagFound) { //event found


                                    session.setData("loginType", "referee");
                                    session.setData("eventId", events.getKey());
                                   // fc.refereeNumbering(events.getKey());


                                    if (session.getData("refereeId").equals("")) {
                                        //kalo udah ga pernah login (ga ada sessionnya) new data
                                        //new login
                                        //validate limit juri
                                        final Vector<Referee> tempReferee = new Vector<Referee>();
                                        final int maxReferee = events.getTotal_referee();
                                        db.collection("events").document(events.getKey())
                                                .collection("referee").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    boolean flag_validate_refereeNumber = true;
                                                    for (QueryDocumentSnapshot d2 : task.getResult()) {
                                                        Referee tempRefereeClass = d2.toObject(Referee.class);
                                                        if(tempRefereeClass.getNumber() == Integer.
                                                                parseInt(sp_ae_no_urut.getSelectedItem().toString())){
                                                            flag_validate_refereeNumber = false;
                                                        }
                                                        tempReferee.add(tempRefereeClass);
                                                    }
                                                    Log.d("Debug", tempReferee.toString());

                                                    if (maxReferee > tempReferee.size()&&flag_validate_refereeNumber) {
                                                        //maxreferee == max referee allowed temprefereesize = jumlah referee yg ud pernah login
                                                        //logic login
                                                        DocumentReference refereeRef = db.collection("events").document(events.getKey())
                                                                .collection("referee").document();
                                                        String refereeKey = refereeRef.getId();
                                                        Referee temp_referee = new Referee(et_arl_nama.getText().toString());
                                                        temp_referee.setNumber(Integer.parseInt(sp_ae_no_urut.getSelectedItem().toString()));
                                                        refereeRef.set(temp_referee);
                                                        session.setData("refereeId", refereeKey);
                                                        //end logic login

                                                        Intent i = new Intent(RefereeLoginActivity.this, RefereeActivity.class);
                                                        startActivity(i);
                                                        finish();
                                                    }else if(!flag_validate_refereeNumber){
                                                        new KAlertDialog(RefereeLoginActivity.this, KAlertDialog.ERROR_TYPE)
                                                                .setTitleText("Error!")
                                                                .setContentText("Nomer Juri sudah dipilih!")
                                                                .show();
                                                    } else {
                                                        new KAlertDialog(RefereeLoginActivity.this, KAlertDialog.ERROR_TYPE)
                                                                .setTitleText("Error!")
                                                                .setContentText("Limit Juri sudah penuh!")
                                                                .show();
                                                    }


                                                }
                                            }
                                        });

                                    } else {
                                        //kalo ada ga usah set session lg
                                        DocumentReference refereeRef = db.collection("events").document(events.getKey())
                                                .collection("referee")
                                                .document(session.getData("refereeId"));
                                        Referee temp_referee = new Referee(et_arl_nama.getText().toString());
                                        temp_referee.setNumber(Integer.parseInt(sp_ae_no_urut.getSelectedItem().toString()));
                                        refereeRef.set(temp_referee);

                                    }



//                                Intent i = new Intent(RefereeLoginActivity.this,RefereeActivity.class);
//                                startActivity(i);
//                                finish();


                                } else {
                                    new KAlertDialog(RefereeLoginActivity.this, KAlertDialog.ERROR_TYPE)
                                            .setTitleText("Oops...")
                                            .setContentText("Kode Event tidak ditemukan!")
                                            .show();
                                }


                            }
                        }
                    });
                }

            }
        });

    }
}
