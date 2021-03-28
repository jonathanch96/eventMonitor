package com.packag.eventmonitor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.packag.eventmonitor.Data.Penilaian;
import com.packag.eventmonitor.Data.RefereePenilaian;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.Util.Session;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ScoringTraditionalActivity extends AppCompatActivity {
    Intent intent;
    String teamId;
    String errorMsg;
    FirebaseFirestore db;
    Team team;
    Session session;
    TextView tv_as_team_name;//
    TextView tv_as_no_urut;//
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

    EditText et_as_kesulitan;


    TextView tv_ap_traditional_total_penilaian;
    TextView tv_ap_traditional_nilai_total_pengurangan;
    TextView tv_ap_traditional_grand_total;
    Button btn_ap_traditional_as_submit;
    DocumentReference teamRef;
    ProgressDialog loadingDialog;
    Vector<Penilaian> penilaians = new Vector<Penilaian>();

    Button btn_adp_ks1;
    Button btn_adp_ks2;
    Button btn_adp_ks3;
    Button btn_adp_ks4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penilaian_traditional);
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
        btn_adp_ks1 = findViewById(R.id.btn_adp_ks1);
        btn_adp_ks2 = findViewById(R.id.btn_adp_ks2);
        btn_adp_ks3 = findViewById(R.id.btn_adp_ks3);
        btn_adp_ks4 = findViewById(R.id.btn_adp_ks4);
        // et_as_kesulitan = findViewById(R.id.et_as_kesulitan);

        et_as_ks1 = findViewById(R.id.et_as_ks1);
        et_as_ks2 = findViewById(R.id.et_as_ks2);
        et_as_ks3 = findViewById(R.id.et_as_ks3);
        et_as_ks4 = findViewById(R.id.et_as_ks4);

        tv_ap_traditional_total_penilaian = findViewById(R.id.tv_as_total_kotor);
        tv_ap_traditional_nilai_total_pengurangan = findViewById(R.id.tv_as_total_pengurangan);
        tv_ap_traditional_grand_total = findViewById(R.id.tv_as_total_bersih);
        btn_ap_traditional_as_submit = findViewById(R.id.btn_as_submit);
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
                    if (document.exists()) {
                        team = document.toObject(Team.class);
                        tv_as_no_urut.setText("Nomor Urut : " + team.getNo_urut());
                        tv_as_team_name.setText("Nama Team : " + team.getTeam_name());
                        teamRef.collection("penilaian")
                                .document(session.getData("refereeId")).collection("field").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot d2 : task.getResult()) {
                                                Penilaian p = d2.toObject(Penilaian.class);
                                                p.setKey(d2.getId());
                                                penilaians.add(p);
                                            }
                                            set_first_data();
                                        }
                                    }
                                });

                    }
                }
            }
        });
    }

    private void set_first_data() {
        for (Penilaian p : penilaians) {
            if (p.getForm_id().equals("et_as_n1")) {
                et_as_n1.setText(p.getNilai() + "");

            } else if (p.getForm_id().equals("et_as_n2")) {
                et_as_n2.setText(p.getNilai() + "");

            } else if (p.getForm_id().equals("et_as_n3")) {
                et_as_n3.setText(p.getNilai() + "");

            } else if (p.getForm_id().equals("et_as_n4")) {
                et_as_n4.setText(p.getNilai() + "");

            } else if (p.getForm_id().equals("et_as_n5")) {
                et_as_n5.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_as_n6")) {
                et_as_n6.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_as_n7")) {
                et_as_n7.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_as_n8")) {
                et_as_n8.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_as_n9")) {
                et_as_n9.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_as_n10")) {
                et_as_n10.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_as_ks1")) {
                et_as_ks1.setText(p.getNilai() + "");
                BigDecimal tbd = new BigDecimal(p.getNilai());
                BigDecimal multiplier = new BigDecimal(0.1);
                tbd = tbd.divide(multiplier, MathContext.DECIMAL32);
                tbd = tbd.setScale(0, RoundingMode.HALF_EVEN);
                btn_adp_ks1.setText((int)(tbd.doubleValue())+"");
            } else if (p.getForm_id().equals("et_as_ks2")) {
                et_as_ks2.setText(p.getNilai() + "");
                BigDecimal tbd = new BigDecimal(p.getNilai());
                BigDecimal multiplier = new BigDecimal(0.3);
                tbd = tbd.divide(multiplier, MathContext.DECIMAL32);
                tbd = tbd.setScale(0, RoundingMode.HALF_EVEN);
                btn_adp_ks2.setText((int)(tbd.doubleValue())+"");
            } else if (p.getForm_id().equals("et_as_ks3")) {
                et_as_ks3.setText(p.getNilai() + "");
                BigDecimal tbd = new BigDecimal(p.getNilai());
                BigDecimal multiplier = new BigDecimal(0.5);
                tbd = tbd.divide(multiplier, MathContext.DECIMAL32);
                tbd = tbd.setScale(0, RoundingMode.HALF_EVEN);
                btn_adp_ks3.setText((int)(tbd.doubleValue())+"");
            } else if (p.getForm_id().equals("et_as_ks4")) {
                et_as_ks4.setText(p.getNilai() + "");
                BigDecimal tbd = new BigDecimal(p.getNilai());
                BigDecimal multiplier = new BigDecimal(1.0);
                tbd = tbd.divide(multiplier, MathContext.DECIMAL32);
                tbd = tbd.setScale(0, RoundingMode.HALF_EVEN);
                btn_adp_ks4.setText((int)(tbd.doubleValue())+"");
            }

        }
        recalculateTotal();


    }

    private boolean validateData() {
        //TODO NELSON kerjain validasi untuk input nilai

        boolean flag = false;
        if (et_as_n1.getText().toString().equals("")) {
            et_as_n1.requestFocus();
            errorMsg = getString(R.string.n1_sopan_santun) + " harus di isi";
        } else if (Double.parseDouble(et_as_n1.getText().toString()) < 0.5) {
            et_as_n1.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.n1_sopan_santun) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_as_n1.getText().toString()) > 1) {
            et_as_n1.requestFocus();
            errorMsg = getString(R.string.n1_sopan_santun) + " tidak boleh lebih dari 1.0";

        } else if (et_as_n2.getText().toString().equals("")) {
            et_as_n2.requestFocus();
            errorMsg = getString(R.string.n2_judul_dan_alur_cerita) + " harus di isi";
        } else if (Double.parseDouble(et_as_n2.getText().toString()) < 0.5) {
            et_as_n2.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.n2_judul_dan_alur_cerita) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_as_n2.getText().toString()) > 1) {
            et_as_n2.requestFocus();
            errorMsg = getString(R.string.n2_judul_dan_alur_cerita) + " tidak boleh lebih dari 1.0";

        } else if (et_as_n3.getText().toString().equals("")) {
            et_as_n3.requestFocus();
            errorMsg = getString(R.string.n3_bentuk_barongsai) + " harus diisi";
        } else if (Double.parseDouble(et_as_n3.getText().toString()) < 0.5) {
            et_as_n3.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.n3_bentuk_barongsai) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_as_n3.getText().toString()) > 1) {
            et_as_n3.requestFocus();
            errorMsg = getString(R.string.n3_bentuk_barongsai) + " tidak boleh lebih dari 1.0";

        } else if (et_as_n4.getText().toString().equals("")) {
            et_as_n4.requestFocus();
            errorMsg = getString(R.string.n4_ekspresi) + " harus diisi";
        } else if (Double.parseDouble(et_as_n4.getText().toString()) < 0.5) {
            et_as_n4.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.n4_ekspresi) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_as_n4.getText().toString()) > 1) {
            et_as_n4.requestFocus();
            errorMsg = getString(R.string.n4_ekspresi) + " tidak boleh lebih dari 1.0";

        } else if (et_as_n5.getText().toString().equals("")) {
            et_as_n5.requestFocus();
            errorMsg = getString(R.string.n5_musik) + " harus diisi";
        } else if (Double.parseDouble(et_as_n5.getText().toString()) < 0.5) {
            et_as_n5.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.n5_musik) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_as_n5.getText().toString()) > 1) {
            et_as_n5.requestFocus();
            errorMsg = getString(R.string.n5_musik) + " tidak boleh lebih dari 1.0";

        } else if (et_as_n6.getText().toString().equals("")) {
            et_as_n6.requestFocus();
            errorMsg = getString(R.string.n6_cirikhas_traditional) + " harus diisi";
        } else if (Double.parseDouble(et_as_n6.getText().toString()) < 0.5) {
            et_as_n6.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.n6_cirikhas_traditional) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_as_n6.getText().toString()) > 1) {
            et_as_n6.requestFocus();
            errorMsg = getString(R.string.n6_cirikhas_traditional) + " tidak boleh lebih dari 1.0";


        } else if (et_as_n7.getText().toString().equals("")) {
            et_as_n7.requestFocus();
            errorMsg = getString(R.string.n7_komposisi_permainan) + " harus diisi";
        } else if (Double.parseDouble(et_as_n7.getText().toString()) < 0.5) {
            et_as_n7.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.n7_komposisi_permainan) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_as_n7.getText().toString()) > 1) {
            et_as_n7.requestFocus();
            errorMsg = getString(R.string.n7_komposisi_permainan) + " tidak boleh lebih dari 1.0";

        } else if (et_as_n8.getText().toString().equals("")) {
            et_as_n8.requestFocus();
            errorMsg = getString(R.string.n8_hasil_dan_permainan) + " harus diisi";
        } else if (Double.parseDouble(et_as_n8.getText().toString()) < 0.5) {
            et_as_n8.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.n8_hasil_dan_permainan) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_as_n8.getText().toString()) > 1) {
            et_as_n8.requestFocus();
            errorMsg = getString(R.string.n8_hasil_dan_permainan) + " tidak boleh lebih dari 1.0";

        } else if (et_as_n9.getText().toString().equals("")) {
            et_as_n9.requestFocus();
            errorMsg = getString(R.string.n9_keterampilan) + " harus diisi";
        } else if (Double.parseDouble(et_as_n9.getText().toString()) < 0.5) {
            et_as_n9.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.n9_keterampilan) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_as_n9.getText().toString()) > 1) {
            et_as_n9.requestFocus();
            errorMsg = getString(R.string.n9_keterampilan) + " tidak boleh lebih dari 1.0";

        } else if (et_as_n10.getText().toString().equals("")) {
            et_as_n10.requestFocus();
            errorMsg = getString(R.string.n10_seragam_dan_peralatan) + " harus diisi";
        } else if (Double.parseDouble(et_as_n1.getText().toString()) < 0.5) {
            et_as_n10.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.n10_seragam_dan_peralatan) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_as_n10.getText().toString()) > 1) {
            et_as_n10.requestFocus();
            errorMsg = getString(R.string.n10_seragam_dan_peralatan) + " tidak boleh lebih dari 1.0";


        } else if (et_as_ks1.getText().toString().equals("")) {
            et_as_ks1.setText("0");
        } else if (et_as_ks2.getText().toString().equals("")) {
            et_as_ks2.setText("0");
        } else if (et_as_ks3.getText().toString().equals("")) {
            et_as_ks3.setText("0");
        } else if (et_as_ks4.getText().toString().equals("")) {
            et_as_ks4.setText("0");


        } else {
            flag = true;
        }


        return flag;
    }

    private double roundTo2Decs(double value) {
        /*BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();*/
        double roundOff = Math.round(value * 100.0) / 100.0;
        return roundOff;
    }

    private void recalculateTotal() {

        double n1 = et_as_n1.getText().toString().equals("") || et_as_n1.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_n1.getText().toString());
        double n2 = et_as_n2.getText().toString().equals("") || et_as_n2.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_n2.getText().toString());
        double n3 = et_as_n3.getText().toString().equals("") || et_as_n3.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_n3.getText().toString());
        double n4 = et_as_n4.getText().toString().equals("") || et_as_n4.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_n4.getText().toString());
        double n5 = et_as_n5.getText().toString().equals("") || et_as_n5.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_n5.getText().toString());
        double n6 = et_as_n6.getText().toString().equals("") || et_as_n6.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_n6.getText().toString());
        double n7 = et_as_n7.getText().toString().equals("") || et_as_n7.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_n7.getText().toString());
        double n8 = et_as_n8.getText().toString().equals("") || et_as_n8.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_n8.getText().toString());
        double n9 = et_as_n9.getText().toString().equals("") || et_as_n9.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_n9.getText().toString());
        double n10 = et_as_n10.getText().toString().equals("") || et_as_n10.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_n10.getText().toString());
        double p1 = et_as_ks1.getText().toString().equals("") || et_as_ks1.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_ks1.getText().toString());
        double p2 = et_as_ks2.getText().toString().equals("") || et_as_ks2.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_ks2.getText().toString());
        double p3 = et_as_ks3.getText().toString().equals("") || et_as_ks3.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_ks3.getText().toString());
        double p4 = et_as_ks4.getText().toString().equals("") || et_as_ks4.getText().toString().equals(".") ? 0 : Double.parseDouble(et_as_ks4.getText().toString());

        BigDecimal tb = new BigDecimal(0);
        BigDecimal tk = new BigDecimal(0);
        BigDecimal p = new BigDecimal(0);
        //double tb = 0, tk = 0, p = 0;

        tk = tk.add(new BigDecimal(n1));
        tk = tk.add(new BigDecimal(n2));
        tk = tk.add(new BigDecimal(n3));
        tk = tk.add(new BigDecimal(n4));
        tk = tk.add(new BigDecimal(n5));
        tk = tk.add(new BigDecimal(n6));
        tk = tk.add(new BigDecimal(n7));
        tk = tk.add(new BigDecimal(n8));
        tk = tk.add(new BigDecimal(n9));
        tk = tk.add(new BigDecimal(n10));

        p = p.add(new BigDecimal(p1));
        p = p.add(new BigDecimal(p2));
        p = p.add(new BigDecimal(p3));
        p = p.add(new BigDecimal(p4));

        tb = tk.subtract(p);

        //tk = n1 + n2 + n3 + n4 + n5 + n6 + n7 + n8 + n9 + n10;
        //p = p1 + p2 + p3 + p4;
        //tb = tk - p;

//        tk = roundTo2Decs(tk);
//        p = roundTo2Decs(p);
//        tb = roundTo2Decs(tb);


        tk = tk.setScale(2, RoundingMode.HALF_EVEN);
        p = p.setScale(2, RoundingMode.HALF_EVEN);
        tb=tb.setScale(2, RoundingMode.HALF_EVEN);

        Log.d("debug", "tk = " + tk + " tb = " + tb + " p = " + p);
        tv_ap_traditional_grand_total.setText(Double.toString(tb.doubleValue()));
        tv_ap_traditional_total_penilaian.setText(Double.toString(tk.doubleValue()));
        tv_ap_traditional_nilai_total_pengurangan.setText(Double.toString(p.doubleValue()));
    }

    private void setListener() {
        btn_adp_ks1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(btn_adp_ks1, "ks1", et_as_ks1);
            }
        });
        btn_adp_ks2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(btn_adp_ks2, "ks2", et_as_ks2);
            }
        });
        btn_adp_ks3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(btn_adp_ks3, "ks3", et_as_ks3);
            }
        });
        btn_adp_ks4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(btn_adp_ks4, "ks4", et_as_ks4);
            }
        });
        et_as_n1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_n1.getText().toString().equals("")) {
                        et_as_n1.setText("0");
                    }
                } else {
                    if (et_as_n1.getText().toString().equals("0") || et_as_n1.getText().toString().equals("0.0")) {
                        et_as_n1.setText("");
                    }
                }
            }
        });
        et_as_n2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_n2.getText().toString().equals("")) {
                        et_as_n2.setText("0");
                    }
                } else {
                    if (et_as_n2.getText().toString().equals("0") || et_as_n2.getText().toString().equals("0.0")) {
                        et_as_n2.setText("");
                    }
                }
            }
        });
        et_as_n3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_n3.getText().toString().equals("")) {
                        et_as_n3.setText("0");
                    }
                } else {
                    if (et_as_n3.getText().toString().equals("0") || et_as_n3.getText().toString().equals("0.0")) {
                        et_as_n3.setText("");
                    }
                }
            }
        });
        et_as_n4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_n4.getText().toString().equals("")) {
                        et_as_n4.setText("0");
                    }
                } else {
                    if (et_as_n4.getText().toString().equals("0") || et_as_n4.getText().toString().equals("0.0")) {
                        et_as_n4.setText("");
                    }
                }
            }
        });
        et_as_n5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_n5.getText().toString().equals("")) {
                        et_as_n5.setText("0");
                    }
                } else {
                    if (et_as_n5.getText().toString().equals("0") || et_as_n5.getText().toString().equals("0.0")) {
                        et_as_n5.setText("");
                    }
                }
            }
        });
        et_as_n6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_n6.getText().toString().equals("")) {
                        et_as_n6.setText("0");
                    }
                } else {
                    if (et_as_n6.getText().toString().equals("0") || et_as_n6.getText().toString().equals("0.0")) {
                        et_as_n6.setText("");
                    }
                }
            }
        });
        et_as_n7.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_n7.getText().toString().equals("")) {
                        et_as_n7.setText("0");
                    }
                } else {
                    if (et_as_n7.getText().toString().equals("0") || et_as_n7.getText().toString().equals("0.0")) {
                        et_as_n7.setText("");
                    }
                }
            }
        });
        et_as_n8.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_n8.getText().toString().equals("")) {
                        et_as_n8.setText("0");
                    }
                } else {
                    if (et_as_n8.getText().toString().equals("0") || et_as_n8.getText().toString().equals("0.0")) {
                        et_as_n8.setText("");
                    }
                }
            }
        });
        et_as_n9.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_n9.getText().toString().equals("")) {
                        et_as_n9.setText("0");
                    }
                } else {
                    if (et_as_n9.getText().toString().equals("0") || et_as_n9.getText().toString().equals("0.0")) {
                        et_as_n9.setText("");
                    }
                }
            }
        });
        et_as_n10.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_n10.getText().toString().equals("")) {
                        et_as_n10.setText("0");
                    }
                } else {
                    if (et_as_n10.getText().toString().equals("0") || et_as_n10.getText().toString().equals("0.0")) {
                        et_as_n10.setText("");
                    }
                }
            }
        });
        et_as_ks1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_ks1.getText().toString().equals("")) {
                        et_as_ks1.setText("0");
                    }
                } else {
                    if (et_as_ks1.getText().toString().equals("0") || et_as_ks1.getText().toString().equals("0.0")) {
                        et_as_ks1.setText("");
                    }
                }
            }
        });
        et_as_ks2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_ks2.getText().toString().equals("")) {
                        et_as_ks2.setText("0");
                    }
                } else {
                    if (et_as_ks2.getText().toString().equals("0") || et_as_ks2.getText().toString().equals("0.0")) {
                        et_as_ks2.setText("");
                    }
                }
            }
        });
        et_as_ks3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_ks3.getText().toString().equals("")) {
                        et_as_ks3.setText("0");
                    }
                } else {
                    if (et_as_ks3.getText().toString().equals("0") || et_as_ks3.getText().toString().equals("0.0")) {
                        et_as_ks3.setText("");
                    }
                }
            }
        });
        et_as_ks4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_as_ks4.getText().toString().equals("")) {
                        et_as_ks4.setText("0");
                    }
                } else {
                    if (et_as_ks4.getText().toString().equals("0") || et_as_ks4.getText().toString().equals("0.0")) {
                        et_as_ks4.setText("");
                    }
                }
            }
        });


//        et_as_kesulitan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if(!b){
//                    recalculateTotal();
//                    if(et_as_kesulitan.getText().toString().equals("")) {
//                        et_as_kesulitan.setText("0");
//                    }
//                }else{
//                    if(et_as_kesulitan.getText().toString().equals("0")||et_as_kesulitan.getText().toString().equals("0.0")){
//                        et_as_kesulitan.setText("");
//                    }
//                }
//            }
//        });


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
        btn_ap_traditional_as_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KAlertDialog(ScoringTraditionalActivity.this, KAlertDialog.WARNING_TYPE)
                        .setTitleText("Menyimpan penilaian ?")
                        .setContentText("Apakah Anda yakin untuk menyimpan penilaian ini!")
                        .setConfirmText("Ya")
                        .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                saveAction();
                                sDialog.dismissWithAnimation();
                            }
                        })
                        .setCancelText("Tidak")
                        .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                            @Override
                            public void onClick(KAlertDialog sDialog) {
                                sDialog.cancel();
                            }
                        })
                        .show();

            }
        });

    }

    private void saveAction() {
        loadingDialog.show();
        if (validateData()) {
            penilaians = new Vector<Penilaian>();
            Penilaian data = new Penilaian(
                    Double.parseDouble(et_as_n1.getText().toString()),
                    "+", null, "et_as_n1");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_as_n2.getText().toString()),
                    "+", null, "et_as_n2");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_as_n3.getText().toString()),
                    "+", null, "et_as_n3");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_as_n4.getText().toString()),
                    "+", null, "et_as_n4");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_as_n5.getText().toString()),
                    "+", null, "et_as_n5");
            penilaians.add(data);
            data = new Penilaian(
                    Double.parseDouble(et_as_n6.getText().toString()),
                    "+", null, "et_as_n6");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_as_n7.getText().toString()),
                    "+", null, "et_as_n7");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_as_n8.getText().toString()),
                    "+", null, "et_as_n8");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_as_n9.getText().toString()),
                    "+", null, "et_as_n9");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_as_n10.getText().toString()),
                    "+", null, "et_as_n10");
            penilaians.add(data);


            data = new Penilaian(
                    Double.parseDouble(et_as_ks1.getText().toString()),
                    "-", null, "et_as_ks1");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_as_ks2.getText().toString()),
                    "-", null, "et_as_ks2");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_as_ks3.getText().toString()),
                    "-", null, "et_as_ks3");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_as_ks4.getText().toString()),
                    "-", null, "et_as_ks4");

            penilaians.add(data);


            saveData();

            Intent return_i = new Intent();
            return_i.putExtra("msg", "Berhasil Memberi Nilai!");
            setResult(Activity.RESULT_OK, return_i);
            loadingDialog.hide();

            finish();
        } else {
            loadingDialog.hide();
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
//            new KAlertDialog(ScoringTraditionalActivity.this, KAlertDialog.ERROR_TYPE)
//                    .setTitleText("Oops...")
//                    .setContentText(errorMsg)
//                    .show();

        }
    }

    private void saveData() {
        double total_nilai = 0;
        double total_pengurangan = 0;
        double grand_total = 0;
        for (Penilaian p : penilaians) {
            teamRef.collection("penilaian")
                    .document(session.getData("refereeId"))
                    .collection("field")
                    .document(p.getForm_id())
                    .set(p, SetOptions.merge());
//            if(p.getType().equals("+")){
//                total_nilai+=p.getNilai();
//            }else if(p.getType().equals("-")){
//                total_pengurangan+=p.getNilai();
//            }
        }
        final Map<String, Integer> dataToSave = new HashMap<>();
        dataToSave.put("isEditable", 0);
//        grand_total = total_nilai - total_pengurangan;
//        RefereePenilaian rp = new RefereePenilaian();
//        rp.setIsEditable(0);
//        rp.setTotal_nilai(total_nilai);
//        rp.setTotal_potongan(total_pengurangan);
//        rp.setGrand_total(grand_total);
        teamRef.collection("penilaian")
                .document(session.getData("refereeId"))
                .set(dataToSave, SetOptions.merge());
        FirestoreController fc = new FirestoreController();
        fc.updateRefereePenilaianSummary
                (session.getData("eventId"), teamId, session.getData("refereeId"));
//        fc.recalculateNilaiBersih(session.getData("eventId"),teamId);


    }

    public void showDialog(final Button btn, final String type, final EditText target) {
        LayoutInflater li = LayoutInflater.from(ScoringTraditionalActivity.this);
        View promptsView = li.inflate(R.layout.adapter_model_dialog_pengurangan_taolu, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ScoringTraditionalActivity.this);
        alertDialogBuilder.setView(promptsView);
        final EditText et_amd_pengurangan_taolu_value = (EditText) promptsView
                .findViewById(R.id.et_amd_pengurangan_taolu_value);
        final TextView tv_amd_pengurangan_taolu_title = (TextView) promptsView
                .findViewById(R.id.tv_amd_pengurangan_taolu_title);
        String title = "";
        double multiplier = 1;

        if (type.equals("ks1")) {
            title = "Pengurangan nilai ";
            multiplier = 0.1;

        } else if (type.equals("ks2")) {
            title = "Pengurangan nilai ";
            multiplier = 0.3;
        } else if (type.equals("ks3")) {
            title = "Pengurangan nilai";
            multiplier = 0.5;
        } else if (type.equals("ks4")) {
            title = "Pengurangan nilai ";
            multiplier = 1;
        }

        final double passed_multiplier = multiplier;


        tv_amd_pengurangan_taolu_title.setText(title);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (et_amd_pengurangan_taolu_value.getText().toString().equals("")) {
                            et_amd_pengurangan_taolu_value.setText("0");
                        }
                        BigDecimal val = new BigDecimal(passed_multiplier);
                        val = val.multiply(new BigDecimal(
                                Double.parseDouble(et_amd_pengurangan_taolu_value.getText().toString())));
                        val = val.setScale(2, RoundingMode.HALF_EVEN);
                        target.setText(val.doubleValue() + "");

                        btn.setText(et_amd_pengurangan_taolu_value.getText().toString());
//                        new KAlertDialog(ScoringTraditionalActivity.this, KAlertDialog.SUCCESS_TYPE)
//                                .setTitleText("Success!")
//                                .setContentText("Berhasil menambah pengurangan nilai juri!")
//                                .show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }
                );
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
}
