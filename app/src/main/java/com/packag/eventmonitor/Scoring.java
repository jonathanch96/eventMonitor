package com.packag.eventmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.Util.Session;

public class Scoring extends AppCompatActivity {
    Intent intent;
    String teamId;
    String errorMsg;
    FirebaseFirestore db;
    Team team;
    Session session;
    TextView tv_as_team_name;
    TextView tv_as_no_urut;
    EditText et_as_n1;
    EditText et_as_n2;
    EditText et_as_n3;
    EditText et_as_n4;
    EditText et_as_n5;
    EditText et_as_n6;
    EditText et_as_n7;
    EditText et_as_n8;
    EditText et_as_n9;
    EditText et_as_n10;
    EditText et_as_ks1;
    EditText et_as_ks2;
    EditText et_as_ks3;
    EditText et_as_ks4;
    TextView tv_as_total_kotor;
    TextView tv_as_total_pengurangan;
    TextView tv_as_total_bersih;
    Button btn_as_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring);
        initializeComponent();
        setListener();
    }

    private void initializeComponent() {
        intent = getIntent();
        teamId = intent.getStringExtra("teamId");
        session = new Session(this.getApplicationContext());
        tv_as_no_urut = findViewById(R.id.tv_as_no_urut);
        tv_as_team_name = findViewById(R.id.tv_as_team_name);
        et_as_n1 = findViewById(R.id.et_as_n1);
        et_as_n2 = findViewById(R.id.et_as_n2);
        et_as_n3 = findViewById(R.id.et_as_n3);
        et_as_n4 = findViewById(R.id.et_as_n4);
        et_as_n5 = findViewById(R.id.et_as_n5);
        et_as_n6 = findViewById(R.id.et_as_n6);
        et_as_n7 = findViewById(R.id.et_as_n7);
        et_as_n8 = findViewById(R.id.et_as_n8);
        et_as_n9 = findViewById(R.id.et_as_n9);
        et_as_n10 = findViewById(R.id.et_as_n10);
        et_as_ks1 = findViewById(R.id.et_as_ks1);
        et_as_ks2 = findViewById(R.id.et_as_ks2);
        et_as_ks3 = findViewById(R.id.et_as_ks3);
        et_as_ks4 = findViewById(R.id.et_as_ks4);
        tv_as_total_kotor = findViewById(R.id.tv_as_total_kotor);
        tv_as_total_pengurangan=findViewById(R.id.tv_as_total_pengurangan);
        tv_as_total_bersih = findViewById(R.id.tv_as_total_bersih);
        btn_as_submit = findViewById(R.id.btn_as_submit);
        db = FirebaseFirestore.getInstance();
        db.collection("events")
                .document(session.getData("eventId"))
                .collection("team")
                .document(teamId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        team = document.toObject(Team.class);
                        tv_as_no_urut.setText("Nomor Urut : " + team.getNo_urut());
                        tv_as_team_name.setText("Nama Team : " + team.getTeam_name());
                    }
                }
            }
        });
    }
    private boolean validateData(){
        boolean flag = false;
        if(et_as_n1.getText().toString().equals("")){
            errorMsg="Judul dan Alur Cerita Harus diisi";
        }else if(Double.parseDouble(et_as_n1.getText().toString())>1){
            errorMsg="Judul dan Alur Cerita Tidak boleh lebih dari 1.0";
        }
        return flag;
    }
    private void setListener() {
        btn_as_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateData()){

                }else{
                    new KAlertDialog(Scoring.this, KAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(errorMsg)
                            .show();
                }
            }
        });
    }
}
