package com.packag.eventmonitor;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.packag.eventmonitor.Data.Penilaian;
import com.packag.eventmonitor.Data.PenilaianTraditional;
import com.packag.eventmonitor.Data.RefereePenilaian;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.Util.Session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Vector;

public class ScoringNagaActivity extends AppCompatActivity {
    Intent intent;
    String teamId;
    String errorMsg;
    FirebaseFirestore db;
    Team team;
    Session session;
    TextView tv_ap_naga_team_name;//
    TextView tv_ap_naga_no_urut;//
    EditText et_amdp_naga_n1;
    EditText et_amdp_naga_n2;
    EditText et_amdp_naga_n3;
    EditText et_amdp_naga_n4;
    EditText et_amdp_naga_n5;
    EditText et_amdp_naga_p1;
    EditText et_amdp_naga_p2;
    EditText et_amdp_naga_p3;
    EditText et_amdp_naga_p4;
    EditText et_amdp_naga_p5;
    EditText et_amdp_naga_kesulitan;


    TextView tv_ap_naga_total_penilaian;
    TextView tv_ap_naga_nilai_total_pengurangan;
    TextView tv_ap_naga_grand_total;
    Button btn_ap_naga_as_submit;
    DocumentReference teamRef;
    ProgressDialog loadingDialog;
    Vector<Penilaian> penilaians = new Vector<Penilaian>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penilaian_naga);
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
        tv_ap_naga_no_urut = findViewById(R.id.tv_ap_naga_no_urut);
        tv_ap_naga_team_name = findViewById(R.id.tv_ap_naga_team_name);
        et_amdp_naga_n1 = findViewById(R.id.et_amdp_naga_n1);
        et_amdp_naga_n2 = findViewById(R.id.et_amdp_naga_n2);
        et_amdp_naga_n3 = findViewById(R.id.et_amdp_naga_n3);
        et_amdp_naga_n4 = findViewById(R.id.et_amdp_naga_n4);
        et_amdp_naga_n5 = findViewById(R.id.et_amdp_naga_n5);
        et_amdp_naga_p1 = findViewById(R.id.et_amdp_naga_p1);
        et_amdp_naga_p2 = findViewById(R.id.et_amdp_naga_p2);
        et_amdp_naga_p3 = findViewById(R.id.et_amdp_naga_p3);
        et_amdp_naga_p4 = findViewById(R.id.et_amdp_naga_p4);
        et_amdp_naga_p5 = findViewById(R.id.et_amdp_naga_p5);
        et_amdp_naga_kesulitan = findViewById(R.id.et_amdp_naga_kesulitan);


        tv_ap_naga_total_penilaian = findViewById(R.id.tv_ap_naga_total_penilaian);
        tv_ap_naga_nilai_total_pengurangan = findViewById(R.id.tv_ap_naga_nilai_total_pengurangan);
        tv_ap_naga_grand_total = findViewById(R.id.tv_ap_naga_grand_total);
        btn_ap_naga_as_submit = findViewById(R.id.btn_ap_naga_as_submit);
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
                        tv_ap_naga_no_urut.setText("Nomor Urut : " + team.getNo_urut());
                        tv_ap_naga_team_name.setText("Nama Team : " + team.getTeam_name());
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
            if (p.getForm_id().equals("et_amdp_naga_n1")) {
                et_amdp_naga_n1.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_amdp_naga_n2")) {
                et_amdp_naga_n2.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_amdp_naga_n3")) {
                et_amdp_naga_n3.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_amdp_naga_n4")) {
                et_amdp_naga_n4.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_amdp_naga_n5")) {
                et_amdp_naga_n5.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_amdp_naga_p1")) {
                et_amdp_naga_p1.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_amdp_naga_p2")) {
                et_amdp_naga_p2.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_amdp_naga_p3")) {
                et_amdp_naga_p3.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_amdp_naga_p4")) {
                et_amdp_naga_p4.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_amdp_naga_p5")) {
                et_amdp_naga_p5.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("et_amdp_naga_kesulitan")) {
                et_amdp_naga_kesulitan.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("tv_ap_naga_total_penilaian")) {
                tv_ap_naga_total_penilaian.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("tv_ap_naga_nilai_total_pengurangan")) {
                tv_ap_naga_nilai_total_pengurangan.setText(p.getNilai() + "");
            } else if (p.getForm_id().equals("tv_ap_naga_grand_total")) {
                tv_ap_naga_grand_total.setText(p.getNilai() + "");
            }

        }
    }

    private boolean validateData() {
        //TODO NELSON kerjain validasi untuk input nilai

        boolean flag = false;
        if (et_amdp_naga_n1.getText().toString().equals("")) {
            et_amdp_naga_n1.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_1) + " harus di isi";
        } else if (Double.parseDouble(et_amdp_naga_n1.getText().toString()) < 0.5) {
            et_amdp_naga_n1.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_1) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_amdp_naga_n1.getText().toString()) > 5) {
            et_amdp_naga_n1.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_1) + " Tidak boleh lebih dari 5.0";
        } else if (et_amdp_naga_p1.getText().toString().equals("")) {
            et_amdp_naga_p1.setText("0");
        } else if (Double.parseDouble(et_amdp_naga_p1.getText().toString()) > 5) {
            et_amdp_naga_p1.requestFocus();
            errorMsg = "Pengurangan nilai " + getResources().getString(R.string.ap_naga_type_1) + " Tidak boleh lebih dari 5.0";

        } else if (et_amdp_naga_n2.getText().toString().equals("")) {
            et_amdp_naga_n2.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_2_det_1_1) + " harus diisi";
        } else if (Double.parseDouble(et_amdp_naga_n2.getText().toString()) < 0.5) {
            et_amdp_naga_n2.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_2_det_1_1) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_amdp_naga_n2.getText().toString()) > 1) {
            et_amdp_naga_n2.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_2_det_1_1) + " tidak boleh lebih dari 1.0";
        } else if (et_amdp_naga_p2.getText().toString().equals("")) {
            et_amdp_naga_p2.setText("0");
        } else if (Double.parseDouble(et_amdp_naga_p2.getText().toString()) > 1) {
            et_amdp_naga_p2.requestFocus();
            errorMsg = "Pengurangan " + getResources().getString(R.string.ap_naga_type_2_det_1_1) + " tidak boleh lebih dari 1.0";

        } else if (et_amdp_naga_n3.getText().toString().equals("")) {
            et_amdp_naga_n3.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_2_det_1_2) + " harus diisi";
        } else if (Double.parseDouble(et_amdp_naga_n3.getText().toString()) < 0.5) {
            et_amdp_naga_n3.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_2_det_1_2) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_amdp_naga_n3.getText().toString()) > 1) {
            et_amdp_naga_n3.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_2_det_1_2) + " tidak boleh lebih dari 1.0";
        } else if (et_amdp_naga_p3.getText().toString().equals("")) {
            et_amdp_naga_p3.setText("0");
        } else if (Double.parseDouble(et_amdp_naga_p3.getText().toString()) > 1) {
            et_amdp_naga_p3.requestFocus();
            errorMsg = "Pengurangan Nilai " + getResources().getString(R.string.ap_naga_type_2_det_1_2) + " tidak boleh lebih dari 1.0";


        } else if (et_amdp_naga_n4.getText().toString().equals("")) {
            et_amdp_naga_n4.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_2_det_1_3) + " harus diisi";
        } else if (Double.parseDouble(et_amdp_naga_n4.getText().toString()) < 0.5) {
            et_amdp_naga_n4.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_2_det_1_3) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_amdp_naga_n4.getText().toString()) > 1) {
            et_amdp_naga_n4.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_2_det_1_3) + " tidak boleh lebih dari 1.0";
        } else if (et_amdp_naga_p4.getText().toString().equals("")) {
            et_amdp_naga_p4.setText("0");
        } else if (Double.parseDouble(et_amdp_naga_p4.getText().toString()) > 1) {
            et_amdp_naga_p4.requestFocus();
            errorMsg = "Pengurangan Nilai " + getResources().getString(R.string.ap_naga_type_2_det_1_3) + " tidak boleh lebih dari 1.0";


        } else if (et_amdp_naga_n5.getText().toString().equals("")) {
            et_amdp_naga_n5.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_3) + " harus diisi";
        } else if (Double.parseDouble(et_amdp_naga_n5.getText().toString()) < 0.5) {
            et_amdp_naga_n5.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_3) + " tidak boleh kurang dari 0.5";
        } else if (Double.parseDouble(et_amdp_naga_n5.getText().toString()) > 2) {
            et_amdp_naga_n5.requestFocus();
            errorMsg = "Nilai " + getResources().getString(R.string.ap_naga_type_3) + " tidak boleh lebih dari 2.0";
        } else if (et_amdp_naga_p5.getText().toString().equals("")) {
            et_amdp_naga_p5.setText("0");
        } else if (Double.parseDouble(et_amdp_naga_p5.getText().toString()) > 2) {
            et_amdp_naga_p5.requestFocus();
            errorMsg = "Pengurangan " + getResources().getString(R.string.ap_naga_type_3) + " tidak boleh lebih dari 2.0";
        } else {
            flag = true;
        }
        return flag;
    }

    private double roundTo2Decs(double value) {
        double roundOff = Math.round(value * 100.0) / 100.0;
        return roundOff;
    }

    private void recalculateTotal() {

        double n1 = et_amdp_naga_n1.getText().toString().equals("") || et_amdp_naga_n1.getText().toString().equals(".") ? 0 : Double.parseDouble(et_amdp_naga_n1.getText().toString());
        double n2 = et_amdp_naga_n2.getText().toString().equals("") || et_amdp_naga_n2.getText().toString().equals(".") ? 0 : Double.parseDouble(et_amdp_naga_n2.getText().toString());
        double n3 = et_amdp_naga_n3.getText().toString().equals("") || et_amdp_naga_n3.getText().toString().equals(".") ? 0 : Double.parseDouble(et_amdp_naga_n3.getText().toString());
        double n4 = et_amdp_naga_n4.getText().toString().equals("") || et_amdp_naga_n4.getText().toString().equals(".") ? 0 : Double.parseDouble(et_amdp_naga_n4.getText().toString());
        double n5 = et_amdp_naga_n5.getText().toString().equals("") || et_amdp_naga_n5.getText().toString().equals(".") ? 0 : Double.parseDouble(et_amdp_naga_n5.getText().toString());
        double p1 = et_amdp_naga_p1.getText().toString().equals("") || et_amdp_naga_p1.getText().toString().equals(".") ? 0 : Double.parseDouble(et_amdp_naga_p1.getText().toString());
        double p2 = et_amdp_naga_p2.getText().toString().equals("") || et_amdp_naga_p2.getText().toString().equals(".") ? 0 : Double.parseDouble(et_amdp_naga_p2.getText().toString());
        double p3 = et_amdp_naga_p3.getText().toString().equals("") || et_amdp_naga_p3.getText().toString().equals(".") ? 0 : Double.parseDouble(et_amdp_naga_p3.getText().toString());
        double p4 = et_amdp_naga_p4.getText().toString().equals("") || et_amdp_naga_p4.getText().toString().equals(".") ? 0 : Double.parseDouble(et_amdp_naga_p4.getText().toString());
        double p5 = et_amdp_naga_p5.getText().toString().equals("") || et_amdp_naga_p5.getText().toString().equals(".") ? 0 : Double.parseDouble(et_amdp_naga_p5.getText().toString());


        BigDecimal tb = BigDecimal.valueOf(0);
        BigDecimal tk = BigDecimal.valueOf(0);
        BigDecimal p = BigDecimal.valueOf(0);

        tk = tk.add(BigDecimal.valueOf(n1));
        tk = tk.add(BigDecimal.valueOf(n2));
        tk = tk.add(BigDecimal.valueOf(n3));
        tk = tk.add(BigDecimal.valueOf(n4));
        tk = tk.add(BigDecimal.valueOf(n5));

        p = p.add(BigDecimal.valueOf(p1));
        p = p.add(BigDecimal.valueOf(p2));
        p = p.add(BigDecimal.valueOf(p3));
        p = p.add(BigDecimal.valueOf(p4));
        p = p.add(BigDecimal.valueOf(p5));

        tb = tk.subtract(p);

        tk = tk.setScale(2, RoundingMode.HALF_EVEN);
        p = p.setScale(2, RoundingMode.HALF_EVEN);
        tb = tb.setScale(2, RoundingMode.HALF_EVEN);

        Log.d("debug", "tk = " + tk + " tb = " + tb + " p = " + p);
        tv_ap_naga_grand_total.setText(Double.toString(tb.doubleValue()));
        tv_ap_naga_total_penilaian.setText(Double.toString(tk.doubleValue()));
        tv_ap_naga_nilai_total_pengurangan.setText(Double.toString(p.doubleValue()));
    }

    private void setListener() {
        et_amdp_naga_n1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_amdp_naga_n1.getText().toString().equals("")) {
                        et_amdp_naga_n1.setText("0");
                    }
                } else {
                    if (et_amdp_naga_n1.getText().toString().equals("0") || et_amdp_naga_n1.getText().toString().equals("0.0")) {
                        et_amdp_naga_n1.setText("");
                    }
                }
            }
        });
        et_amdp_naga_p1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_amdp_naga_p1.getText().toString().equals("")) {
                        et_amdp_naga_p1.setText("0");
                    }
                } else {
                    if (et_amdp_naga_p1.getText().toString().equals("0") || et_amdp_naga_p1.getText().toString().equals("0.0")) {
                        et_amdp_naga_p1.setText("");
                    }
                }
            }
        });
        et_amdp_naga_n2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_amdp_naga_n2.getText().toString().equals("")) {
                        et_amdp_naga_n2.setText("0");
                    }
                } else {
                    if (et_amdp_naga_n2.getText().toString().equals("0") || et_amdp_naga_n2.getText().toString().equals("0.0")) {
                        et_amdp_naga_n2.setText("");
                    }
                }
            }
        });
        et_amdp_naga_p2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_amdp_naga_p2.getText().toString().equals("")) {
                        et_amdp_naga_p2.setText("0");
                    }
                } else {
                    if (et_amdp_naga_p2.getText().toString().equals("0") || et_amdp_naga_p2.getText().toString().equals("0.0")) {
                        et_amdp_naga_p2.setText("");
                    }
                }
            }
        });
        et_amdp_naga_n3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_amdp_naga_n3.getText().toString().equals("")) {
                        et_amdp_naga_n3.setText("0");
                    }
                } else {
                    if (et_amdp_naga_n3.getText().toString().equals("0") || et_amdp_naga_n3.getText().toString().equals("0.0")) {
                        et_amdp_naga_n3.setText("");
                    }
                }
            }
        });
        et_amdp_naga_p3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_amdp_naga_p2.getText().toString().equals("")) {
                        et_amdp_naga_p2.setText("0");
                    }
                } else {
                    if (et_amdp_naga_p2.getText().toString().equals("0") || et_amdp_naga_p3.getText().toString().equals("0.0")) {
                        et_amdp_naga_p2.setText("");
                    }
                }
            }
        });
        et_amdp_naga_n4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_amdp_naga_n4.getText().toString().equals("")) {
                        et_amdp_naga_n4.setText("0");
                    }
                } else {
                    if (et_amdp_naga_n4.getText().toString().equals("0") || et_amdp_naga_n4.getText().toString().equals("0.0")) {
                        et_amdp_naga_n4.setText("");
                    }
                }
            }
        });
        et_amdp_naga_p4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_amdp_naga_p4.getText().toString().equals("")) {
                        et_amdp_naga_p4.setText("0");
                    }
                } else {
                    if (et_amdp_naga_p4.getText().toString().equals("0") || et_amdp_naga_p4.getText().toString().equals("0.0")) {
                        et_amdp_naga_p4.setText("");
                    }
                }
            }
        });
        et_amdp_naga_n5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_amdp_naga_n5.getText().toString().equals("")) {
                        et_amdp_naga_n5.setText("0");
                    }
                } else {
                    if (et_amdp_naga_n5.getText().toString().equals("0") || et_amdp_naga_n5.getText().toString().equals("0.0")) {
                        et_amdp_naga_n5.setText("");
                    }
                }
            }
        });
        et_amdp_naga_p5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_amdp_naga_p5.getText().toString().equals("")) {
                        et_amdp_naga_p5.setText("0");
                    }
                } else {
                    if (et_amdp_naga_p5.getText().toString().equals("0") || et_amdp_naga_p5.getText().toString().equals("0.0")) {
                        et_amdp_naga_p5.setText("");
                    }
                }
            }
        });
        et_amdp_naga_kesulitan.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    recalculateTotal();
                    if (et_amdp_naga_kesulitan.getText().toString().equals("")) {
                        et_amdp_naga_kesulitan.setText("0");
                    }
                } else {
                    if (et_amdp_naga_kesulitan.getText().toString().equals("0") || et_amdp_naga_kesulitan.getText().toString().equals("0.0")) {
                        et_amdp_naga_kesulitan.setText("");
                    }
                }
            }
        });


//        on change listener
        et_amdp_naga_n1.addTextChangedListener(new TextWatcher() {

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
        et_amdp_naga_p1.addTextChangedListener(new TextWatcher() {

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
        et_amdp_naga_n2.addTextChangedListener(new TextWatcher() {

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
        et_amdp_naga_p2.addTextChangedListener(new TextWatcher() {

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
        et_amdp_naga_n3.addTextChangedListener(new TextWatcher() {

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
        et_amdp_naga_p3.addTextChangedListener(new TextWatcher() {

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
        et_amdp_naga_n4.addTextChangedListener(new TextWatcher() {

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
        et_amdp_naga_p4.addTextChangedListener(new TextWatcher() {

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

        et_amdp_naga_n5.addTextChangedListener(new TextWatcher() {

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
        et_amdp_naga_p5.addTextChangedListener(new TextWatcher() {

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
        btn_ap_naga_as_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new KAlertDialog(ScoringNagaActivity.this, KAlertDialog.WARNING_TYPE)
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
                    Double.parseDouble(et_amdp_naga_n1.getText().toString()),
                    "+", null, "et_amdp_naga_n1");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_amdp_naga_n2.getText().toString()),
                    "+", null, "et_amdp_naga_n2");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_amdp_naga_n3.getText().toString()),
                    "+", null, "et_amdp_naga_n3");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_amdp_naga_n4.getText().toString()),
                    "+", null, "et_amdp_naga_n4");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_amdp_naga_n5.getText().toString()),
                    "+", null, "et_amdp_naga_n5");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_amdp_naga_p1.getText().toString()),
                    "-", null, "et_amdp_naga_p1");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_amdp_naga_p2.getText().toString()),
                    "-", null, "et_amdp_naga_p2");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_amdp_naga_p3.getText().toString()),
                    "-", null, "et_amdp_naga_p3");
            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_amdp_naga_p4.getText().toString()),
                    "-", null, "et_amdp_naga_p4");

            penilaians.add(data);

            data = new Penilaian(
                    Double.parseDouble(et_amdp_naga_p5.getText().toString()),
                    "-", null, "et_amdp_naga_p5");
            penilaians.add(data);

            if (!et_amdp_naga_kesulitan.getText().toString().equals("")) {
                data = new Penilaian(
                        Double.parseDouble(et_amdp_naga_kesulitan.getText().toString()),
                        "=", null, "et_amdp_naga_kesulitan");
                penilaians.add(data);
            }


            saveData();

            Intent return_i = new Intent();
            return_i.putExtra("msg", "Berhasil Memberi Nilai!");
            setResult(Activity.RESULT_OK, return_i);
            loadingDialog.hide();

            finish();
        } else {
            loadingDialog.hide();
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
//            new KAlertDialog(ScoringNagaActivity.this, KAlertDialog.ERROR_TYPE)
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
            if (p.getType().equals("+")) {
                total_nilai += p.getNilai();
            } else if (p.getType().equals("-")) {
                total_pengurangan += p.getNilai();
            }
        }
        grand_total = total_nilai - total_pengurangan;
        RefereePenilaian rp = new RefereePenilaian();
        rp.setIsEditable(0);
        rp.setTotal_nilai(total_nilai);
        rp.setTotal_potongan(total_pengurangan);
        rp.setGrand_total(grand_total);
        teamRef.collection("penilaian")
                .document(session.getData("refereeId"))
                .set(rp, SetOptions.merge());
        FirestoreController fc = new FirestoreController();
        fc.recalculateNilaiBersih(session.getData("eventId"), teamId);


    }
}
