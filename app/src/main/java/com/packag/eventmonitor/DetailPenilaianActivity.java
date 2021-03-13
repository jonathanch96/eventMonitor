package com.packag.eventmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.packag.eventmonitor.Data.Events;
import com.packag.eventmonitor.Data.Penilaian;
import com.packag.eventmonitor.Data.Referee;
import com.packag.eventmonitor.Data.RefereePenilaian;
import com.packag.eventmonitor.Data.Team;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Nullable;


public class DetailPenilaianActivity extends AppCompatActivity {
    String eventId;
    String teamId;
    RecyclerView rv_adp_list_referee;
    TextView tv_adp_team_name;
    TextView tv_adp_no_urut;
    TextView tv_adp_nilai_bersih;
    TextView tv_adp_potongan_admin;
    TextView tv_adp_total_nilai;
    TextView tv_adp_edit_button;
    TextView tv_adp_tingkat_kesulitan;
    TextView tv_adp_edit_kesulitan_button;
    Button btn_adp_ks1;
    Button btn_adp_ks2;
    Button btn_adp_ks3;
    Button btn_adp_ks4;
    Team team;
    Events events;
    Vector<Referee> dataReferee;
    Vector<RefereePenilaian> refereePenilaians;
    FirebaseFirestore db;
    Vector<Penilaian> vPenilaians = new Vector<Penilaian>();
    LinearLayout ll_adp_tingkat_kesulitan_container;

    boolean getData1 = false;
    boolean getData2 = false;
    boolean getData3 = false;

    FloatingActionButton fab_adp;
    //FloatingActionButton fab_adp_pengurangan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_penilaian);
        System.setProperty("org.apache.poi.javax.xml.stream.XMLInputFactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLOutputFactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        System.setProperty("org.apache.poi.javax.xml.stream.XMLEventFactory", "com.fasterxml.aalto.stax.EventFactoryImpl");
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        getIntentData();
        initData();
        getData();
        setListener();
    }

    public void initData() {

        rv_adp_list_referee = findViewById(R.id.rv_adp_list_referee);
        tv_adp_team_name = findViewById(R.id.tv_adp_team_name);
        tv_adp_no_urut = findViewById(R.id.tv_adp_no_urut);
        tv_adp_nilai_bersih = findViewById(R.id.tv_adp_nilai_bersih);
        tv_adp_potongan_admin = findViewById(R.id.tv_adp_potongan_admin);
        tv_adp_total_nilai = findViewById(R.id.tv_adp_total_nilai);
        fab_adp = findViewById(R.id.fab_adp);
        // fab_adp_pengurangan = findViewById(R.id.fab_adp_pengurangan);
        tv_adp_edit_button = findViewById(R.id.tv_adp_edit_button);
        dataReferee = new Vector<Referee>();
        refereePenilaians = new Vector<RefereePenilaian>();
        btn_adp_ks1 = findViewById(R.id.btn_adp_ks1);
        btn_adp_ks2 = findViewById(R.id.btn_adp_ks2);
        btn_adp_ks3 = findViewById(R.id.btn_adp_ks3);
        btn_adp_ks4 = findViewById(R.id.btn_adp_ks4);
        db = FirebaseFirestore.getInstance();
        team = new Team();
        btn_adp_ks1.setVisibility(View.GONE);
        btn_adp_ks2.setVisibility(View.GONE);
        btn_adp_ks3.setVisibility(View.GONE);
        btn_adp_ks4.setVisibility(View.GONE);

        tv_adp_tingkat_kesulitan = findViewById(R.id.tv_adp_tingkat_kesulitan);
        tv_adp_edit_kesulitan_button = findViewById(R.id.tv_adp_edit_kesulitan_button);
        ll_adp_tingkat_kesulitan_container = findViewById(R.id.ll_adp_tingkat_kesulitan_container);

    }

    public int getOrder(String name) {
        int order = 999;
        if (getData1) {
            if (events.getThemes().equals("Naga")) {
                if (name.equals("et_amdp_naga_n1")) {
                    order = 1;
                } else if (name.equals("et_amdp_naga_n2")) {
                    order = 2;
                } else if (name.equals("et_amdp_naga_n3")) {
                    order = 3;
                } else if (name.equals("et_amdp_naga_n4")) {
                    order = 4;
                } else if (name.equals("et_amdp_naga_n5")) {
                    order = 5;
                } else if (name.equals("et_amdp_naga_p1")) {
                    order = 6;
                } else if (name.equals("et_amdp_naga_p2")) {
                    order = 7;
                } else if (name.equals("et_amdp_naga_p3")) {
                    order = 8;
                } else if (name.equals("et_amdp_naga_p4")) {
                    order = 9;
                } else if (name.equals("et_amdp_naga_p5")) {
                    order = 10;
                }

            } else if (events.getThemes().equals("Barongsai Taolu Bebas")) {
                if (name.equals("et_amdp_taolu_n1")) {
                    order = 1;
                } else if (name.equals("et_amdp_taolu_n2")) {
                    order = 2;
                } else if (name.equals("et_amdp_taolu_n3")) {
                    order = 3;
                } else if (name.equals("et_amdp_taolu_n4")) {
                    order = 4;
                } else if (name.equals("et_amdp_taolu_n5")) {
                    order = 5;
                } else if (name.equals("et_amdp_taolu_n6")) {
                    order = 6;
                } else if (name.equals("et_amdp_taolu_n7")) {
                    order = 7;
                } else if (name.equals("et_amdp_taolu_n8")) {
                    order = 8;
                } else if (name.equals("et_amdp_taolu_n9")) {
                    order = 9;
                } else if (name.equals("et_ap_taolu_p1")) {
                    order = 10;
                } else if (name.equals("et_ap_taolu_p2")) {
                    order = 11;
                } else if (name.equals("et_ap_taolu_p3")) {
                    order = 12;
                } else if (name.equals("et_ap_taolu_p4")) {
                    order = 13;
                }

            } else if (events.getThemes().equals("Pekingsai")) {
                if (name.equals("et_amdp_pekingsai_n1")) {
                    order = 1;
                } else if (name.equals("et_amdp_pekingsai_n2")) {
                    order = 2;
                } else if (name.equals("et_amdp_pekingsai_n3")) {
                    order = 3;
                } else if (name.equals("et_amdp_pekingsai_n4")) {
                    order = 4;
                } else if (name.equals("et_amdp_pekingsai_n5")) {
                    order = 5;
                } else if (name.equals("et_amdp_pekingsai_n6")) {
                    order = 6;
                } else if (name.equals("et_amdp_pekingsai_n7")) {
                    order = 7;
                } else if (name.equals("et_amdp_pekingsai_n8")) {
                    order = 8;
                } else if (name.equals("et_amdp_pekingsai_n9")) {
                    order = 9;
                } else if (name.equals("et_ap_pekingsai_p1")) {
                    order = 10;
                } else if (name.equals("et_ap_pekingsai_p2")) {
                    order = 11;
                } else if (name.equals("et_ap_pekingsai_p3")) {
                    order = 12;
                } else if (name.equals("et_ap_pekingsai_p4")) {
                    order = 13;
                }
            } else {
                if (name.equals("et_as_n1")) {
                    order = 1;
                } else if (name.equals("et_as_n2")) {
                    order = 2;
                } else if (name.equals("et_as_n3")) {
                    order = 3;
                } else if (name.equals("et_as_n4")) {
                    order = 4;
                } else if (name.equals("et_as_n5")) {
                    order = 5;
                } else if (name.equals("et_as_n6")) {
                    order = 6;
                } else if (name.equals("et_as_n7")) {
                    order = 7;
                } else if (name.equals("et_as_n8")) {
                    order = 8;
                } else if (name.equals("et_as_n9")) {
                    order = 9;
                } else if (name.equals("et_as_n10")) {
                    order = 10;
                } else if (name.equals("et_as_ks1")) {
                    order = 11;
                } else if (name.equals("et_as_ks2")) {
                    order = 12;
                } else if (name.equals("et_as_ks3")) {
                    order = 13;
                } else if (name.equals("et_as_ks4")) {
                    order = 14;
                }
            }
            return order;
        }
        return 0;


    }

    public void getData() {
        final DocumentReference eventRef =
                db.collection("events").document(eventId);
        ;
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    events = task.getResult().toObject(Events.class);
                    events.setKey(task.getResult().getId());
                    if (events.getThemes().equals("Barongsai Taolu Bebas")) {
                        btn_adp_ks1.setVisibility(View.VISIBLE);
                        btn_adp_ks2.setVisibility(View.VISIBLE);
                        btn_adp_ks3.setVisibility(View.VISIBLE);
                        btn_adp_ks4.setVisibility(View.VISIBLE);
                    } else {
                        btn_adp_ks1.setVisibility(View.GONE);
                        btn_adp_ks2.setVisibility(View.GONE);
                        btn_adp_ks3.setVisibility(View.GONE);
                        btn_adp_ks4.setVisibility(View.GONE);
                    }
                    getData1 = true;
                }
            }
        });
        eventRef.collection("team").document(teamId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    team = value.toObject(Team.class);
                    tv_adp_team_name.setText("Team : " + team.getTeam_name());
                    tv_adp_no_urut.setText("No Urut : " + team.getNo_urut());
                    tv_adp_nilai_bersih.setText("Nilai Bersih : " + String.format("%.2f", team.getNilai_bersih()) + "");
                    tv_adp_potongan_admin.setText("Potongan Admin : " + String.format("%.2f", team.getPengurangan_nb()) + "");
                    tv_adp_total_nilai.setText("Nilai Akhir : " + String.format("%.2f", team.getTotal_nilai()) + "");
                    if(team.getNilai_bersih()==0){
                        btn_adp_ks1.setVisibility(View.GONE);
                        btn_adp_ks2.setVisibility(View.GONE);
                        btn_adp_ks3.setVisibility(View.GONE);
                        btn_adp_ks4.setVisibility(View.GONE);
                        ll_adp_tingkat_kesulitan_container.setVisibility(View.GONE);
                    }else{
                        btn_adp_ks1.setVisibility(View.VISIBLE);
                        btn_adp_ks2.setVisibility(View.VISIBLE);
                        btn_adp_ks3.setVisibility(View.VISIBLE);
                        btn_adp_ks4.setVisibility(View.VISIBLE);
                        ll_adp_tingkat_kesulitan_container.setVisibility(View.VISIBLE);

                    }
                    getData2 = true;
                }
            }
        });
        eventRef.collection("team").document(teamId)
                .collection("penilaian").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                dataReferee = new Vector<Referee>();
                refereePenilaians = new Vector<RefereePenilaian>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                    final PenilaianTraditional penilaian = document.toObject(PenilaianTraditional.class);
//                    penilaian.setKey(document.getId());
//                    dataPenilaian.add(penilaian);
                    final RefereePenilaian rp = document.toObject(RefereePenilaian.class);
                    rp.setKey(document.getId());
                    eventRef.collection("team")
                            .document(teamId).collection("penilaian")
                            .document(rp.getKey()).collection("field").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Vector<Penilaian> temp_penilaians = new Vector<Penilaian>();
                                for (QueryDocumentSnapshot ds : task.getResult()) {
                                    Penilaian tp = ds.toObject(Penilaian.class);
                                    tp.setKey(ds.getId());
                                    tp.setOrder(getOrder(tp.getForm_id()));
                                    if (events.getThemes().equals("Barongsai Taolu Bebas")
                                    ) {
                                        if(tp.getForm_id().equals("et_amdp_taolu_kesulitan")){
                                            tv_adp_tingkat_kesulitan.setText
                                                    (String.format("%.0f", tp.getNilai()));
                                        }else if(tp.getForm_id().equals("et_ap_taolu_p1")){
                                            int value = (int)(tp.getNilai()/0.1);
                                            btn_adp_ks1.setText(value+"");
                                        }else if(tp.getForm_id().equals("et_ap_taolu_p2")){
                                            int value = (int)(tp.getNilai()/0.3);
                                            btn_adp_ks2.setText(value+"");
                                        }else if(tp.getForm_id().equals("et_ap_taolu_p3")){
                                            int value = (int)(tp.getNilai()/0.5);
                                            btn_adp_ks3.setText(value+"");
                                        }else if(tp.getForm_id().equals("et_ap_taolu_p4")){
                                            int value = (int)(tp.getNilai()/1);
                                            btn_adp_ks4.setText(value+"");
                                        }

                                    }

                                    temp_penilaians.add(tp);
                                }
                                Collections.sort(temp_penilaians);

                                rp.setPenilaians(temp_penilaians);
                                eventRef.collection("referee").document(rp.getKey()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    final Referee refereeClass = task.getResult().toObject(Referee.class);
                                                    refereeClass.setKey(task.getResult().getId());
                                                    rp.setOrder(refereeClass.getNumber());
                                                    refereePenilaians.add(rp);
                                                    getData3 = true;

                                                }
                                            }
                                        });

                            }
                        }
                    });

                    eventRef.collection("referee").document(document.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (documentSnapshot.exists()) {
                                final Referee refereeClass = documentSnapshot.toObject(Referee.class);
                                refereeClass.setKey(documentSnapshot.getId());
                                dataReferee.add(refereeClass);
                                Collections.sort(dataReferee);
                                rv_adp_list_referee.setAdapter(new AdapterListReferee(DetailPenilaianActivity.this, dataReferee, eventId, teamId));

                            }
                        }
                    });


                }
                // use this setting to improve performance if you know that changes
                // in content do not change the layout size of the RecyclerView
                rv_adp_list_referee.setHasFixedSize(true);

                // use a linear layout manager
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailPenilaianActivity.this);
                rv_adp_list_referee.setLayoutManager(layoutManager);
                rv_adp_list_referee.setAdapter(new AdapterListReferee(DetailPenilaianActivity.this, dataReferee, eventId, teamId));
            }
        });
    }

    public void getIntentData() {
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        teamId = intent.getStringExtra("teamId");
    }



    public void showDialog(final Button btn, final String type) {
        LayoutInflater li = LayoutInflater.from(DetailPenilaianActivity.this);
        View promptsView = li.inflate(R.layout.adapter_model_dialog_pengurangan_taolu, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                DetailPenilaianActivity.this);
        alertDialogBuilder.setView(promptsView);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
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
        final CollectionReference teamRef = db.collection("events").document(eventId)
                .collection("team").document(teamId).collection("penilaian");

        teamRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    RefereePenilaian rp = new RefereePenilaian();
                    boolean flagfirst = true;
                    for (QueryDocumentSnapshot qds : task.getResult()) {
                        if (flagfirst) {
                            flagfirst = false;
                            rp = qds.toObject(RefereePenilaian.class);
                            rp.setKey(qds.getId());
                        }
                        if (rp.getTotal_potongan() != 0) {
                            rp = qds.toObject(RefereePenilaian.class);
                            rp.setKey(qds.getId());
                        }
                    }
                    if (rp.getKey() != null) {
                        teamRef.document(rp.getKey()).collection("field").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                                        if (task2.isSuccessful()) {
                                            int value = 0;
                                            for (QueryDocumentSnapshot qds : task2.getResult()) {
                                                Penilaian tp = qds.toObject(Penilaian.class);
                                                tp.setKey(qds.getId());
                                                vPenilaians.add(tp);
                                                if (tp.getForm_id().equals("et_ap_taolu_p1") && type.equals("ks1")) {
                                                    value = (int) (tp.getNilai() / passed_multiplier);
                                                } else if (tp.getForm_id().equals("et_ap_taolu_p2") && type.equals("ks2")) {
                                                    value = (int) (tp.getNilai() / passed_multiplier);
                                                } else if (tp.getForm_id().equals("et_ap_taolu_p3") && type.equals("ks3")) {
                                                    value = (int) (tp.getNilai() / passed_multiplier);
                                                } else if (tp.getForm_id().equals("et_ap_taolu_p4") && type.equals("ks4")) {
                                                    value = (int) (tp.getNilai() / passed_multiplier);
                                                }

                                            }

                                            et_amd_pengurangan_taolu_value.setText(value + "");
                                            et_amd_pengurangan_taolu_value.setSelection(et_amd_pengurangan_taolu_value.getText().length());
                                        }
                                    }
                                });
                    }


                }
            }
        });


        tv_amd_pengurangan_taolu_title.setText(title);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        double value = passed_multiplier * Double.parseDouble(et_amd_pengurangan_taolu_value.getText().toString());
                        FirestoreController fc = new FirestoreController();
                        if (type.equals("ks1")) {
                            fc.updateSingleNilai(eventId, teamId, "et_ap_taolu_p1", value,"-");
                        } else if (type.equals("ks2")) {
                            fc.updateSingleNilai(eventId, teamId, "et_ap_taolu_p2", value,"-");
                        } else if (type.equals("ks3")) {
                            fc.updateSingleNilai(eventId, teamId, "et_ap_taolu_p3", value,"-");
                        } else if (type.equals("ks4")) {
                            fc.updateSingleNilai(eventId, teamId, "et_ap_taolu_p4", value,"-");
                        }
                        btn.setText(et_amd_pengurangan_taolu_value.getText().toString());
                        new KAlertDialog(DetailPenilaianActivity.this, KAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success!")
                                .setContentText("Berhasil menambah pengurangan nilai juri!")
                                .show();
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

    public void setListener() {
        btn_adp_ks1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(btn_adp_ks1, "ks1");
            }
        });
        btn_adp_ks2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(btn_adp_ks2, "ks2");
            }
        });
        btn_adp_ks3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(btn_adp_ks3, "ks3");
            }
        });
        btn_adp_ks4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(btn_adp_ks4, "ks4");
            }
        });
        tv_adp_edit_kesulitan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(DetailPenilaianActivity.this);
                View promptsView = li.inflate(R.layout.adapter_model_dialog_tingkat_kesulitan, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        DetailPenilaianActivity.this);
                alertDialogBuilder.setView(promptsView);
                final EditText et_amltp_tingkat_kesulitan = (EditText) promptsView
                        .findViewById(R.id.et_amltp_tingkat_kesulitan);
                et_amltp_tingkat_kesulitan.setText(tv_adp_tingkat_kesulitan.getText().toString());
                et_amltp_tingkat_kesulitan.setSelection(et_amltp_tingkat_kesulitan.getText().toString().length());

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {


                                        if (et_amltp_tingkat_kesulitan.getText().toString().equals("")
                                                || et_amltp_tingkat_kesulitan.getText().toString().equals(".")) {
                                            et_amltp_tingkat_kesulitan.setText("0");
                                        }
                                        FirestoreController fc = new FirestoreController();
                                        tv_adp_tingkat_kesulitan.setText(et_amltp_tingkat_kesulitan.getText().toString());
                                        fc.updateSingleNilai(eventId,teamId,"et_amdp_taolu_kesulitan"
                                                ,Double.parseDouble(et_amltp_tingkat_kesulitan.getText().toString()),"=");

                                        new KAlertDialog(DetailPenilaianActivity.this, KAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Success!")
                                                .setContentText("Berhasil mengganti tingkat kesulitan!")
                                                .show();

                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
        tv_adp_edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(DetailPenilaianActivity.this);
                View promptsView = li.inflate(R.layout.adapter_model_dialog_tambah_pengurangan, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        DetailPenilaianActivity.this);
                alertDialogBuilder.setView(promptsView);
                final EditText et_amltp_pengurangan_nb = (EditText) promptsView
                        .findViewById(R.id.et_amltp_pengurangan_nb);
                et_amltp_pengurangan_nb.setText(String.format("%.2f", team.getPengurangan_nb()) + "");

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {


                                        if (et_amltp_pengurangan_nb.getText().toString().equals("")
                                                || et_amltp_pengurangan_nb.getText().toString().equals(".")) {
                                            et_amltp_pengurangan_nb.setText("0");

                                        }

                                        db.collection("events").document(eventId)
                                                .collection("team").document(teamId).get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            if (task.getResult().exists()) {
                                                                Team team = task.getResult().toObject(Team.class);
                                                                team.setKey(task.getResult().getId());
                                                                double pengurangan = Double.parseDouble
                                                                        (et_amltp_pengurangan_nb.getText().toString());
                                                                double total = team.getNilai_bersih() - pengurangan;
                                                                if (total < 0) {
                                                                    new KAlertDialog(DetailPenilaianActivity.this, KAlertDialog.ERROR_TYPE)
                                                                            .setTitleText("Error!")
                                                                            .setContentText("Pengurangan lebih dari nilai bersih!")
                                                                            .show();
                                                                } else {
                                                                    Map<String, Double> dataToSave = new HashMap<>();
                                                                    dataToSave.put("pengurangan_nb", pengurangan);
                                                                    dataToSave.put("total_nilai", total);
                                                                    db.collection("events").document(eventId)
                                                                            .collection("team").document(teamId)
                                                                            .set(dataToSave, SetOptions.merge());
                                                                    new KAlertDialog(DetailPenilaianActivity.this, KAlertDialog.SUCCESS_TYPE)
                                                                            .setTitleText("Success!")
                                                                            .setContentText("Berhasil menambah pengurangan nilai admin!")
                                                                            .show();
                                                                }
                                                            }
                                                        }
                                                    }
                                                });


                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
        fab_adp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(DetailPenilaianActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(DetailPenilaianActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(DetailPenilaianActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                    if (ContextCompat.checkSelfPermission(DetailPenilaianActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {

                        // Permission is not granted
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(DetailPenilaianActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                        } else {
                            // No explanation needed; request the permission
                            ActivityCompat.requestPermissions(DetailPenilaianActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    1);

                            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                            // app-defined int constant. The callback method gets the
                            // result of the request.
                        }
                    } else {
                        // Permission has already been granted
                        exportPenilaianToExcel();

                    }
                }

            }
        });


    }


    private void exportPenilaianToExcel() {
        if (getData1 && getData2 && getData3) {
            Collections.sort(refereePenilaians);

            ArrayList<String> header = new ArrayList<String>();
            header.add("Keterangan");
            for (RefereePenilaian rp : refereePenilaians) {
                header.add("Juri - " + rp.getOrder());

                Vector<Penilaian> temp_penilaian_sorted = new Vector<Penilaian>();
                temp_penilaian_sorted = rp.getPenilaians();
                int index = 0;
                for (Penilaian tp : temp_penilaian_sorted) {

                    if (tp.getType().equals("=")) {
                        tp.setOrder(999);
                        temp_penilaian_sorted.set(index, tp);
                    }
                    index++;
                }
                Collections.sort(temp_penilaian_sorted);
                rp.setPenilaians(temp_penilaian_sorted);
            }

            Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

        /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
            CreationHelper createHelper = workbook.getCreationHelper();

            // Create a Sheet
            Sheet sheet = workbook.createSheet("Penilaian");

            // Create a Font for styling header cells
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.RED.getIndex());

            // Create a CellStyle with the font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Create a Row
            Row headerRow = sheet.createRow(0);

            // Create cells
            for (int i = 0; i < header.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(header.get(i));
                cell.setCellStyle(headerCellStyle);
            }

            // Create Cell Style for formatting Date
            CellStyle dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

            // Create Other rows and cells with employees data
            int rowNum = 0;
            int saveRowNum = 0;
            double multiplier = 1.0;

            for (Penilaian p : refereePenilaians.get(0).getPenilaians()) {
                Row row = sheet.createRow(rowNum + 1);
                //no
                boolean flag_not_print = false;
                if (p.getType().equals("+")) {
                    row.createCell(0)
                            .setCellValue("Nilai " + (rowNum + 1));
                    saveRowNum++;
                    multiplier = 1.0;
                } else if (p.getType().equals("-")) {
                    row.createCell(0)
                            .setCellValue("Pengurangan " + (rowNum - saveRowNum + 1));
                    multiplier = -1.0;

                } else {
                    row.createCell(0)
                            .setCellValue("Kesulitan");

                    multiplier = 1;
                    //flag_not_print = true;
                }

                if (!flag_not_print) {

                    int cellNum = 1;
                    for (RefereePenilaian rp : refereePenilaians) {
                        //nilai juri
                        row.createCell(cellNum)
                                .setCellValue(rp.getPenilaians().get(rowNum).getNilai() * multiplier);
                        cellNum++;
                    }
                    rowNum++;
                }

            }


            // Resize all columns to fit the content size
       /* for(int i = 0; i < columns.length; i++) {
            sheet.setColumnWidth(2, 250);
        }*/
            String path_folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Event Monitor/";
            File directory = new File(path_folder);
            /*check Dir*/
            if (!directory.exists()) {
                directory.mkdir();
                // If you require it to make the entire directory path including parents,
                // use directory.mkdirs(); here instead.
            }
            // Write the output to a file
            FileOutputStream fileOut = null;
            try {
                fileOut = new FileOutputStream
                        (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                + "/Event Monitor/Exported Data.xlsx");
                workbook.write(fileOut);
                fileOut.close();


                // Closing the workbook
                workbook.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + "/Event Monitor/Exported Data.xlsx");
            Uri path = Uri.fromFile(file);
            Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
            pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfOpenintent.setDataAndType(path, "application/vnd.ms-excel");
            try {
                startActivity(pdfOpenintent);
            } catch (ActivityNotFoundException e) {

            }

        } else {
            new KAlertDialog(DetailPenilaianActivity.this, KAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Data not loaded, Please try again in a minutes")
                    .show();
        }


    }

}
