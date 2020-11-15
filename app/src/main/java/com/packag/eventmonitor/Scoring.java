package com.packag.eventmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.packag.eventmonitor.Data.Penilaian;
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
    DocumentReference teamRef;
    ProgressDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoring);
        initializeComponent();
        setListener();
    }

    private void initializeComponent() {
        intent = getIntent();
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setTitle("Processing your Request");
        loadingDialog.setMessage("Please Wait a second...");
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
        teamRef = db.collection("events")
                .document(session.getData("eventId"))
                .collection("team")
                .document(teamId);
        teamRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()) {
                        team = document.toObject(Team.class);
                        tv_as_no_urut.setText("Nomor Urut : " + team.getNo_urut());
                        tv_as_team_name.setText("Nama Team : " + team.getTeam_name());
                        teamRef.collection("penilaian")
                                .document(session.getData("refereeId")).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    DocumentSnapshot d2 = task.getResult();
                                if(d2.exists()){
                                    Penilaian init_nilai = d2.toObject(Penilaian.class);
                                    team.setPenilaian(init_nilai);

                                    et_as_n1.setText(Double.toString(init_nilai.getN1()));
                                    et_as_n2.setText(Double.toString(init_nilai.getN2()));
                                    et_as_n3.setText(Double.toString(init_nilai.getN3()));
                                    et_as_n4.setText(Double.toString(init_nilai.getN4()));
                                    et_as_n5.setText(Double.toString(init_nilai.getN5()));
                                    et_as_n6.setText(Double.toString(init_nilai.getN6()));
                                    et_as_n7.setText(Double.toString(init_nilai.getN7()));
                                    et_as_n8.setText(Double.toString(init_nilai.getN8()));
                                    et_as_n9.setText(Double.toString(init_nilai.getN9()));
                                    et_as_n10.setText(Double.toString(init_nilai.getN10()));
                                    et_as_ks1.setText(Double.toString(init_nilai.getKs1()));
                                    et_as_ks2.setText(Double.toString(init_nilai.getKs2()));
                                    et_as_ks3.setText(Double.toString(init_nilai.getKs3()));
                                    et_as_ks4.setText(Double.toString(init_nilai.getKs4()));
                                    tv_as_total_kotor.setText(Double.toString(init_nilai.getTk()));
                                    tv_as_total_bersih.setText(Double.toString(init_nilai.getTb()));
                                    tv_as_total_pengurangan.setText(Double.toString(init_nilai.getP()));


                                }
                            }
                        });

                    }
                }
            }
        });
    }
    private boolean validateData() {
        //TODO NELSON kerjain validasi untuk input nilai

        boolean flag = false;
        if (et_as_n1.getText().toString().equals("")) {
            errorMsg = "Sopan Santun Harus diisi";
        } else if (Double.parseDouble(et_as_n1.getText().toString()) > 1) {
            errorMsg = "Sopan Santun Tidak boleh lebih dari 1.0";
        } else if (et_as_n2.getText().toString().equals("")) {
            errorMsg = "Judul dan Alur Cerita Harus diisi";
        } else if (Double.parseDouble(et_as_n2.getText().toString()) > 1) {
            errorMsg = "Judul dan Alur Cerita Tidak boleh lebih dari 1.0";
        } else if (et_as_n3.getText().toString().equals("")) {
            errorMsg = "Bentuk Barongsai Harus diisi";
        } else if (Double.parseDouble(et_as_n3.getText().toString()) > 1) {
            errorMsg = "Bentuk Barongsai Tidak boleh lebih dari 1.0";
        } else if (et_as_n4.getText().toString().equals("")) {
            errorMsg = "Ekspresi Harus diisi";
        } else if (Double.parseDouble(et_as_n4.getText().toString()) > 1) {
            errorMsg = "Ekspresi Tidak boleh lebih dari 1.0";
        } else if (et_as_n5.getText().toString().equals("")) {
            errorMsg = "Musik Cerita Harus diisi";
        } else if (Double.parseDouble(et_as_n5.getText().toString()) > 1) {
            errorMsg = "Musik Tidak boleh lebih dari 1.0";
        } else if (et_as_n6.getText().toString().equals("")) {
            errorMsg = "Cirikhas Traditional Harus diisi";
        } else if (Double.parseDouble(et_as_n6.getText().toString()) > 1) {
            errorMsg = "Cirikhas Traditional Tidak boleh lebih dari 1.0";
        } else if (et_as_n7.getText().toString().equals("")) {
            errorMsg = "Komposisi Permainan Harus diisi";
        } else if (Double.parseDouble(et_as_n7.getText().toString()) > 1) {
            errorMsg = "Komposisi Permainan Tidak boleh lebih dari 1.0";
        } else if (et_as_n8.getText().toString().equals("")) {
            errorMsg = "Hasil dari Permainan Harus diisi";
        } else if (Double.parseDouble(et_as_n8.getText().toString()) > 1) {
            errorMsg = "Hasil dari Permainan Tidak boleh lebih dari 1.0";
        } else if (et_as_n9.getText().toString().equals("")) {
            errorMsg = "Keterampilan Harus diisi";
        } else if (Double.parseDouble(et_as_n9.getText().toString()) > 1) {
            errorMsg = "Keterampilan Tidak boleh lebih dari 1.0";
        }else if(et_as_n10.getText().toString().equals("")){
            errorMsg="Seragam & Peralatan/Dekorasi Cerita Harus diisi";
        }else if(Double.parseDouble(et_as_n10.getText().toString())>1){
            errorMsg="Seragam & Peralatan/Dekorasi boleh lebih dari 1.0";
        }else if(et_as_ks1.getText().toString().equals("")){
            errorMsg="Kesalahan Lain Harus diisi (min:0)";
        }else if(et_as_ks2.getText().toString().equals("")){
            errorMsg="Kesalahan Kecil Harus diisi (min:0)";
        }else if(et_as_ks3.getText().toString().equals("")){
            errorMsg="Kesalahan Sedang Harus diisi (min:0)";
        }else if(et_as_ks4.getText().toString().equals("")){
            errorMsg="Kesalahan Besar Harus diisi (min:0)";
        } else {
            flag = true;
        }


        return flag;
    }
    private void recalculateTotal(){
        double n1 = et_as_n1.getText().toString().equals("")?0:Double.parseDouble(et_as_n1.getText().toString());
        double n2 = et_as_n2.getText().toString().equals("")?0:Double.parseDouble(et_as_n2.getText().toString());
        double n3 = et_as_n3.getText().toString().equals("")?0:Double.parseDouble(et_as_n3.getText().toString());
        double n4 = et_as_n4.getText().toString().equals("")?0:Double.parseDouble(et_as_n4.getText().toString());
        double n5 = et_as_n5.getText().toString().equals("")?0:Double.parseDouble(et_as_n5.getText().toString());
        double n6 = et_as_n6.getText().toString().equals("")?0:Double.parseDouble(et_as_n6.getText().toString());
        double n7 = et_as_n7.getText().toString().equals("")?0:Double.parseDouble(et_as_n7.getText().toString());
        double n8 = et_as_n8.getText().toString().equals("")?0:Double.parseDouble(et_as_n8.getText().toString());
        double n9 = et_as_n9.getText().toString().equals("")?0:Double.parseDouble(et_as_n9.getText().toString());
        double n10 = et_as_n10.getText().toString().equals("")?0:Double.parseDouble(et_as_n10.getText().toString());
        double ks1 = et_as_ks1.getText().toString().equals("")?0:Double.parseDouble(et_as_ks1.getText().toString());
        double ks2 = et_as_ks2.getText().toString().equals("")?0:Double.parseDouble(et_as_ks2.getText().toString());
        double ks3 = et_as_ks3.getText().toString().equals("")?0:Double.parseDouble(et_as_ks3.getText().toString());
        double ks4 = et_as_ks4.getText().toString().equals("")?0:Double.parseDouble(et_as_ks4.getText().toString());
        double tb=0,tk=0,p=0;
        tk = n1+n2+n3+n4+n5+n6+n7+n8+n9+n10;
        p = ks1+ks2+ks3+ks4;
        tb=tk-p;
        tv_as_total_bersih.setText(Double.toString(tb));
        tv_as_total_kotor.setText(Double.toString(tk));
        tv_as_total_pengurangan.setText(Double.toString(p));
    }
    private void setListener() {
        et_as_n1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_n1.getText().toString().equals("")) {
                        et_as_n1.setText("0");
                    }
                }else{
                    if(et_as_n1.getText().toString().equals("0")){
                        et_as_n1.setText("");
                    }
                }
            }
        });
        et_as_n2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_n2.getText().toString().equals("")) {
                        et_as_n2.setText("0");
                    }
                }else{
                    if(et_as_n2.getText().toString().equals("0")){
                        et_as_n2.setText("");
                    }
                }
            }
        });
        et_as_n3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_n3.getText().toString().equals("")) {
                        et_as_n3.setText("0");
                    }
                }else{
                    if(et_as_n3.getText().toString().equals("0")){
                        et_as_n3.setText("");
                    }
                }
            }
        });
        et_as_n4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_n4.getText().toString().equals("")) {
                        et_as_n4.setText("0");
                    }
                }else{
                    if(et_as_n4.getText().toString().equals("0")){
                        et_as_n4.setText("");
                    }
                }
            }
        });
        et_as_n5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_n5.getText().toString().equals("")) {
                        et_as_n5.setText("0");
                    }
                }else{
                    if(et_as_n5.getText().toString().equals("0")){
                        et_as_n5.setText("");
                    }
                }
            }
        });
        et_as_n6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_n6.getText().toString().equals("")) {
                        et_as_n6.setText("0");
                    }
                }else{
                    if(et_as_n6.getText().toString().equals("0")){
                        et_as_n6.setText("");
                    }
                }
            }
        });
        et_as_n7.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_n7.getText().toString().equals("")) {
                        et_as_n7.setText("0");
                    }
                }else{
                    if(et_as_n7.getText().toString().equals("0")){
                        et_as_n7.setText("");
                    }
                }
            }
        });
        et_as_n8.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_n8.getText().toString().equals("")) {
                        et_as_n8.setText("0");
                    }
                }else{
                    if(et_as_n8.getText().toString().equals("0")){
                        et_as_n8.setText("");
                    }
                }
            }
        });
        et_as_n9.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_n9.getText().toString().equals("")) {
                        et_as_n9.setText("0");
                    }
                }else{
                    if(et_as_n9.getText().toString().equals("0")){
                        et_as_n9.setText("");
                    }
                }
            }
        });
        et_as_n10.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_n10.getText().toString().equals("")) {
                        et_as_n10.setText("0");
                    }
                }else{
                    if(et_as_n10.getText().toString().equals("0")){
                        et_as_n10.setText("");
                    }
                }
            }
        });
        et_as_ks1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_ks1.getText().toString().equals("")) {
                        et_as_ks1.setText("0");
                    }
                }else{
                    if(et_as_ks1.getText().toString().equals("0")){
                        et_as_ks1.setText("");
                    }
                }
            }
        });
        et_as_ks2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_ks2.getText().toString().equals("")) {
                        et_as_ks2.setText("0");
                    }
                }else{
                    if(et_as_ks2.getText().toString().equals("0")){
                        et_as_ks2.setText("");
                    }
                }
            }
        });
        et_as_ks3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_ks3.getText().toString().equals("")) {
                        et_as_ks3.setText("0");
                    }
                }else{
                    if(et_as_ks3.getText().toString().equals("0")){
                        et_as_ks3.setText("");
                    }
                }
            }
        });
        et_as_ks4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    recalculateTotal();
                    if(et_as_ks4.getText().toString().equals("")) {
                        et_as_ks4.setText("0");
                    }
                }else{
                    if(et_as_ks4.getText().toString().equals("0")){
                        et_as_ks4.setText("");
                    }
                }
            }
        });

//        on change listener
        et_as_n1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_n2.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_n3.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_n4.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_n5.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_n6.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_n7.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_n8.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_n9.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_n10.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_ks1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_ks2.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_ks3.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });
        et_as_ks4.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                recalculateTotal();
            }
        });

        /*end on change listener*/
        btn_as_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                if(validateData()){
                    teamRef.collection("penilaian")
                            .document(session.getData("refereeId"))
                            .set(new Penilaian(
                                    Double.parseDouble(et_as_n1.getText().toString()),
                                    Double.parseDouble(et_as_n2.getText().toString()),
                                    Double.parseDouble(et_as_n3.getText().toString()),
                                    Double.parseDouble(et_as_n4.getText().toString()),
                                    Double.parseDouble(et_as_n5.getText().toString()),
                                    Double.parseDouble(et_as_n6.getText().toString()),
                                    Double.parseDouble(et_as_n7.getText().toString()),
                                    Double.parseDouble(et_as_n8.getText().toString()),
                                    Double.parseDouble(et_as_n9.getText().toString()),
                                    Double.parseDouble(et_as_n10.getText().toString()),
                                    Double.parseDouble(et_as_ks1.getText().toString()),
                                    Double.parseDouble(et_as_ks2.getText().toString()),
                                    Double.parseDouble(et_as_ks3.getText().toString()),
                                    Double.parseDouble(et_as_ks4.getText().toString())
                            ));
                    Intent return_i = new Intent();
                    return_i.putExtra("msg","Berhasil Memberi Nilai!");
                    setResult(Activity.RESULT_OK,return_i);
                    loadingDialog.hide();


                    loadingDialog.hide();
                    finish();
                }else{
                    loadingDialog.hide();
                    new KAlertDialog(Scoring.this, KAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(errorMsg)
                            .show();

                }
            }
        });
    }
}
