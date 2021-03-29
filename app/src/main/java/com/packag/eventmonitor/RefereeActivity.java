package com.packag.eventmonitor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.packag.eventmonitor.Adapter.AdapterListTeam;
import com.packag.eventmonitor.Data.Events;
import com.packag.eventmonitor.Data.Penilaian;
import com.packag.eventmonitor.Data.PenilaianTraditional;
import com.packag.eventmonitor.Data.Referee;
import com.packag.eventmonitor.Data.RefereePenilaian;
import com.packag.eventmonitor.Data.Team;
import com.packag.eventmonitor.Util.Session;
import com.packag.eventmonitor.Util.Setting;

import java.util.Vector;

import javax.annotation.Nullable;

public class    RefereeActivity extends AppCompatActivity {
    private Toolbar mTopToolbar;
    Session session;
    ListView lv_ar_listTeam;
    Vector<Team> dataTeam;
    FirebaseFirestore db;
    Events events;
    Referee referee;
    private static int REQUEST = 9;
    TextView tv_ar_event_code;
    TextView tv_ar_themes;
    TextView tv_ar_total_team;
    TextView tv_ar_status;
    TextView tv_ar_referee_name;
    FirestoreController fc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referee);

        initializeComponent();
        setListener();
    }

    private void initializeComponent() {
        dataTeam = new Vector<Team>();
        db = FirebaseFirestore.getInstance();
        fc = new FirestoreController();


        session = new Session(this.getApplicationContext());
        fc.generateToken("referee", session.getData("refereeId"));
        lv_ar_listTeam = findViewById(R.id.lv_ar_listTeam);
        events = new Events();
        tv_ar_event_code = findViewById(R.id.tv_ar_event_code);
        tv_ar_themes = findViewById(R.id.tv_ar_themes);
        tv_ar_total_team = findViewById(R.id.tv_ar_total_team);
        tv_ar_status = findViewById(R.id.tv_ar_status);
        tv_ar_referee_name = findViewById(R.id.tv_ar_referee_name);
        Setting.checkAppVersion(RefereeActivity.this);

    }

    private void fetchData() {
        final DocumentReference eventRef = db.collection("events")
                .document(session.getData("eventId"));
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        events = document.toObject(Events.class);
                        events.setKey(document.getId());
                        tv_ar_event_code.setText("Kode Event : " + events.getCode());
                        tv_ar_themes.setText("Tema : " + events.getThemes());
                        tv_ar_total_team.setText("Total Team : " + events.getTotal_team());
                        if (events.getStatus() == 1) {
                            tv_ar_status.setText("Status : Open");
                        }
                    }

                }
            }
        });

        final DocumentReference refereeRef = eventRef
                .collection("referee")
                .document(session.getData("refereeId"));
//        refereeRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        referee = document.toObject(Referee.class);
//                        referee.setKey(document.getId());
//                        tv_ar_referee_name.setText("Nama Juri : Juri "+referee.getNumber()+" - "+referee.getName());
//                        setupBadge();
//                    }
//
//                }
//            }
//        });
        //realtime
        refereeRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    referee = value.toObject(Referee.class);
                    referee.setKey(value.getId());
                    tv_ar_referee_name.setText("Nama Juri : Juri " + referee.getNumber() + " - " + referee.getName());
                    setupBadge();
                }
            }
        });

        final CollectionReference teamRef = eventRef
                .collection("team");
        teamRef.orderBy("no_urut").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                dataTeam = new Vector<Team>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    final Team teamClass = document.toObject(Team.class);
                    teamClass.setKey(document.getId());

                    teamRef.document(document.getId())
                            .collection("penilaian")
                            .document(session.getData("refereeId"))
                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                                    if (documentSnapshot.exists()) {
                                        RefereePenilaian penilaian = documentSnapshot.toObject(RefereePenilaian.class);
                                        //PenilaianTraditional penilaian = documentSnapshot.toObject(PenilaianTraditional.class);
                                        penilaian.setKey(documentSnapshot.getId());
                                        teamClass.setPenilaian(penilaian);
                                    } else {
                                        RefereePenilaian penilaian = null;
                                        teamClass.setPenilaian(penilaian);
                                    }

                                    lv_ar_listTeam.setAdapter(new AdapterListTeam(RefereeActivity.this, dataTeam));

                                }
                            });

                    dataTeam.add(teamClass);
                    lv_ar_listTeam.setAdapter(new AdapterListTeam(RefereeActivity.this, dataTeam));
                }


            }
        });

    }
    boolean flagPenilaianOpen=false;
    private void setListener() {
        lv_ar_listTeam.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                themes.add("Naga");
//                themes.add("Barongsai Tradisional");
//                themes.add("Barongsai Taolu Bebas");
//                themes.add("Pekingsai");
                if (dataTeam.get(i).getPenilaian() == null || dataTeam.get(i).getPenilaian().getIsEditable() == 1) {
                    Intent intent = new Intent(RefereeActivity.this, Scoring.class);
                    if (events.getThemes().equals("Naga")) {
                        intent = new Intent(RefereeActivity.this, ScoringNagaActivity.class);
                    } else if (events.getThemes().equals("Barongsai Taolu Bebas")) {
                        intent = new Intent(RefereeActivity.this, ScoringTaoluActivity.class);
                    } else if (events.getThemes().equals("Pekingsai")) {
                        intent = new Intent(RefereeActivity.this, ScoringPekingsaiActivity.class);
                    } else {
                        intent = new Intent(RefereeActivity.this, ScoringTraditionalActivity.class);
                    }
                    intent.putExtra("teamId", dataTeam.get(i).getKey());
                    if(!flagPenilaianOpen){
                        flagPenilaianOpen=true;
                        startActivityForResult(intent, REQUEST);
                    }

                } else {
//                    new KAlertDialog(RefereeActivity.this, KAlertDialog.ERROR_TYPE)
//                            .setTitleText("Oops...")
//                            .setContentText(getString(R.string.error_msg_tidak_dapat_mengedit))
//                            .show();
                    if (events.getThemes().equals("Naga")) {
                        //getNagaView(ctx, referee);
                        new KAlertDialog(RefereeActivity.this, KAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText(getString(R.string.error_msg_tidak_dapat_mengedit))
                                .show();
                    } else if (events.getThemes().equals("Barongsai Taolu Bebas")) {
                        getTaoluView(RefereeActivity.this, referee, dataTeam.get(i));
                    } else if (events.getThemes().equals("Pekingsai")) {
                        //getPekingsaiView(ctx, referee);
                        new KAlertDialog(RefereeActivity.this, KAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText(getString(R.string.error_msg_tidak_dapat_mengedit))
                                .show();
                    } else {
                        getTraditionalView(RefereeActivity.this, referee,dataTeam.get(i));
//                        new KAlertDialog(RefereeActivity.this, KAlertDialog.ERROR_TYPE)
//                                .setTitleText("Oops...")
//                                .setContentText(getString(R.string.error_msg_tidak_dapat_mengedit))
//                                .show();
                    }
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem menuItem = menu.findItem(R.id.action_chat);
        View actionView = menuItem.getActionView();
        textCartItemCount = (TextView) actionView.findViewById(R.id.cart_badge);


        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });
        fetchData();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            session.removeData("loginType");
            Intent i = new Intent(RefereeActivity.this, MainActivity.class);
            fc.removeToken(fc.getToken());
            startActivity(i);
            this.finish();
            return true;
        } else if (id == R.id.action_chat) {
            Intent i = new Intent(RefereeActivity.this, ChatActivity.class);
            i.putExtra("eventId", events.getKey());
            i.putExtra("userId", referee.getKey());
            i.putExtra("name", "Juri " + referee.getNumber() + " (" + events.getCode() + ")" + " - " + referee.getName());
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        flagPenilaianOpen=false;
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST) {
            new KAlertDialog(RefereeActivity.this, KAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Good job!")
                    .setContentText(data.getStringExtra("msg"))
                    .show();
        }
    }

    TextView textCartItemCount;

    private void setupBadge() {
        //get unread message
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Log.d("debug", "setupBadge eventId : " + events.getKey());
        Log.d("debug", "setupBadge destUserId  : " + referee.getKey());
        db.collection("chats")
                .whereEqualTo("eventId", events.getKey())
                .whereEqualTo("destUserId", referee.getKey())
                .whereEqualTo("userId", "admin")
                .whereEqualTo("is_read", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                int unread_message = 0;
                for (QueryDocumentSnapshot doc : value) {
                    unread_message++;

                }
                if (unread_message == 0) {
                    // textCartItemCount.setVisibility(View.GONE);
                    textCartItemCount.setText(unread_message + "");
                } else {
                    textCartItemCount.setVisibility(View.VISIBLE);
                    textCartItemCount.setText(unread_message + "");
                }
            }
        });


    }

    public void getTaoluView(final Context ctx, final Referee referee, final Team team) {
        LayoutInflater li = LayoutInflater.from(ctx);
        View promptsView = li.inflate(R.layout.adapter_model_dialog_penilaian_taolu, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ctx);
        alertDialogBuilder.setView(promptsView);
        //initiate data

        final TextView tv_amdp_team_name_taolu = promptsView.findViewById(R.id.tv_amdp_team_name_taolu);
        final TextView tv_amdp_no_urut_taolu = promptsView.findViewById(R.id.tv_amdp_no_urut_taolu);
        final TextView tv_amdp_referee_taolu = promptsView.findViewById(R.id.tv_amdp_referee_taolu);
        final TextView tv_amdp_taolu_n1 = promptsView.findViewById(R.id.tv_amdp_taolu_n1);
        final TextView tv_amdp_taolu_n2 = promptsView.findViewById(R.id.tv_amdp_taolu_n2);
        final TextView tv_amdp_taolu_n3 = promptsView.findViewById(R.id.tv_amdp_taolu_n3);
        final TextView tv_amdp_taolu_n4 = promptsView.findViewById(R.id.tv_amdp_taolu_n4);
        final TextView tv_amdp_taolu_n5 = promptsView.findViewById(R.id.tv_amdp_taolu_n5);
        final TextView tv_amdp_taolu_n6 = promptsView.findViewById(R.id.tv_amdp_taolu_n6);
        final TextView tv_amdp_taolu_n7 = promptsView.findViewById(R.id.tv_amdp_taolu_n7);
        final TextView tv_amdp_taolu_n8 = promptsView.findViewById(R.id.tv_amdp_taolu_n8);
        final TextView tv_amdp_taolu_n9 = promptsView.findViewById(R.id.tv_amdp_taolu_n9);
        final TextView tv_amdp_taolu_p1 = promptsView.findViewById(R.id.tv_amdp_taolu_p1);
        final TextView tv_amdp_taolu_p2 = promptsView.findViewById(R.id.tv_amdp_taolu_p2);
        final TextView tv_amdp_taolu_p3 = promptsView.findViewById(R.id.tv_amdp_taolu_p3);
        final TextView tv_amdp_taolu_p4 = promptsView.findViewById(R.id.tv_amdp_taolu_p4);
        final TextView et_amdp_taolu_kesulitan = promptsView.findViewById(R.id.et_amdp_taolu_kesulitan);
        final TextView tv_ap_taolu_total_penilaian = promptsView.findViewById(R.id.tv_ap_taolu_total_penilaian);
        final TextView tv_ap_taolu_nilai_total_pengurangan = promptsView.findViewById(R.id.tv_ap_taolu_nilai_total_pengurangan);
        final TextView tv_ap_taolu_grand_total = promptsView.findViewById(R.id.tv_ap_taolu_grand_total);


        //set data
        tv_amdp_taolu_n1.setText("0");
        tv_amdp_taolu_n2.setText("0");
        tv_amdp_taolu_n3.setText("0");
        tv_amdp_taolu_n4.setText("0");
        tv_amdp_taolu_n5.setText("0");
        tv_amdp_taolu_n6.setText("0");
        tv_amdp_taolu_n7.setText("0");
        tv_amdp_taolu_n8.setText("0");
        tv_amdp_taolu_n9.setText("0");
        tv_amdp_taolu_p1.setText("0");
        tv_amdp_taolu_p2.setText("0");
        tv_amdp_taolu_p3.setText("0");
        tv_amdp_taolu_p4.setText("0");
        et_amdp_taolu_kesulitan.setText("");
        tv_ap_taolu_total_penilaian.setText("0");
        tv_ap_taolu_nilai_total_pengurangan.setText("0");
        tv_ap_taolu_grand_total.setText("0");
        DocumentReference teamref = db.collection("events").document(events.getKey())
                .collection("team").document(team.getKey());
        teamref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
                    //team = documentSnapshot.toObject(Team.class);
                    //team.setKey(documentSnapshot.getId());
                    tv_amdp_team_name_taolu.setText(
                            ctx.getApplicationContext().getString(R.string.team_name) + " "
                                    + team.getTeam_name());
                    tv_amdp_no_urut_taolu.setText(
                            ctx.getApplicationContext().getString(R.string.no_urut) + " "
                                    + team.getNo_urut() + "");
                    tv_amdp_referee_taolu.setText(
                            ctx.getApplicationContext().getString(R.string.referee) + " Juri " + referee.getNumber() + " - "
                                    + referee.getName());

                }
            }
        });

        teamref.collection("penilaian").document(referee.getKey())
                .collection("field").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot d2 : task.getResult()) {
                    if (d2.exists()) {
                        Penilaian penilaian = d2.toObject(Penilaian.class);
                        penilaian.setKey(d2.getId());
                        if (d2.getId().equals("et_amdp_taolu_n1")) {
                            tv_amdp_taolu_n1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_taolu_n2")) {
                            tv_amdp_taolu_n2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_taolu_n3")) {
                            tv_amdp_taolu_n3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_taolu_n4")) {
                            tv_amdp_taolu_n4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_taolu_n5")) {
                            tv_amdp_taolu_n5.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_taolu_n6")) {
                            tv_amdp_taolu_n6.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_taolu_n7")) {
                            tv_amdp_taolu_n7.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_taolu_n8")) {
                            tv_amdp_taolu_n8.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_taolu_n9")) {
                            tv_amdp_taolu_n9.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_ap_taolu_p1")) {
                            tv_amdp_taolu_p1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_ap_taolu_p2")) {
                            tv_amdp_taolu_p2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_ap_taolu_p3")) {
                            tv_amdp_taolu_p3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_ap_taolu_p4")) {
                            tv_amdp_taolu_p4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_taolu_kesulitan")) {
                            et_amdp_taolu_kesulitan.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        }
                    }
                }
            }
        });

        teamref.collection("penilaian").document(referee.getKey())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            RefereePenilaian rp = value.toObject(RefereePenilaian.class);
                            rp.setKey(value.getId());

                            tv_ap_taolu_total_penilaian.setText(
                                    String.format("%.2f", rp.getTotal_nilai()) + "");
                            tv_ap_taolu_nilai_total_pengurangan.setText(
                                    String.format("%.2f", rp.getTotal_potongan()) + "");
                            tv_ap_taolu_grand_total.setText(
                                    String.format("%.2f", rp.getGrand_total()) + "");

                        }
                    }
                });
        alertDialogBuilder.setCancelable(true).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        ;
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    public void getTraditionalView(final Context ctx, final Referee referee, final Team team) {
        LayoutInflater li = LayoutInflater.from(ctx);
        View promptsView = li.inflate(R.layout.adapter_model_dialog_penilaian_traditional, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ctx);
        alertDialogBuilder.setView(promptsView);
        //initiate data
        final TextView tv_amdp_team_name = promptsView.findViewById(R.id.tv_amdp_team_name_naga);
        final TextView tv_amdp_no_urut = promptsView.findViewById(R.id.tv_amdp_no_urut_naga);
        final TextView tv_amdp_referee = promptsView.findViewById(R.id.tv_amdp_referee_naga);
        final TextView tv_amdp_n1 = promptsView.findViewById(R.id.tv_amdp_n1);
        final TextView tv_amdp_n2 = promptsView.findViewById(R.id.tv_amdp_n2);
        final TextView tv_amdp_n3 = promptsView.findViewById(R.id.tv_amdp_n3);
        final TextView tv_amdp_n4 = promptsView.findViewById(R.id.tv_amdp_n4);
        final TextView tv_amdp_n5 = promptsView.findViewById(R.id.tv_amdp_n5);
        final TextView tv_amdp_n6 = promptsView.findViewById(R.id.tv_amdp_n6);
        final TextView tv_amdp_n7 = promptsView.findViewById(R.id.tv_amdp_n7);
        final TextView tv_amdp_n8 = promptsView.findViewById(R.id.tv_amdp_n8);
        final TextView tv_amdp_n9 = promptsView.findViewById(R.id.tv_amdp_n9);
        final TextView tv_amdp_n10 = promptsView.findViewById(R.id.tv_amdp_n10);
        final TextView tv_amdp_ks1 = promptsView.findViewById(R.id.tv_amdp_ks1);
        final TextView tv_amdp_ks2 = promptsView.findViewById(R.id.tv_amdp_ks2);
        final TextView tv_amdp_ks3 = promptsView.findViewById(R.id.tv_amdp_ks3);
        final TextView tv_amdp_ks4 = promptsView.findViewById(R.id.tv_amdp_ks4);
        final TextView tv_amdp_total_nilai = promptsView.findViewById(R.id.tv_amdp_total_nilai);
        final TextView tv_amdp_total_pengurangan = promptsView.findViewById(R.id.tv_amdp_total_pengurangan);
        final TextView tv_amdp_grand_total = promptsView.findViewById(R.id.tv_amdp_grand_total);


        //set data
        tv_amdp_n1.setText("0");
        tv_amdp_n2.setText("0");
        tv_amdp_n3.setText("0");
        tv_amdp_n4.setText("0");
        tv_amdp_n5.setText("0");
        tv_amdp_n6.setText("0");
        tv_amdp_n7.setText("0");
        tv_amdp_n8.setText("0");
        tv_amdp_n9.setText("0");
        tv_amdp_n10.setText("0");
        tv_amdp_ks1.setText("0");
        tv_amdp_ks2.setText("0");
        tv_amdp_ks3.setText("0");
        tv_amdp_ks4.setText("0");
        tv_amdp_total_nilai.setText("0");
        tv_amdp_total_pengurangan.setText("0");
        tv_amdp_grand_total.setText("0");
        DocumentReference teamref = db.collection("events").document(events.getKey())
                .collection("team").document(team.getKey());
        teamref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
//                    team = documentSnapshot.toObject(Team.class);
//                    team.setKey(documentSnapshot.getId());
                    tv_amdp_team_name.setText(
                            ctx.getApplicationContext().getString(R.string.team_name) + " "
                                    + team.getTeam_name());
                    tv_amdp_no_urut.setText(
                            ctx.getApplicationContext().getString(R.string.no_urut) + " "
                                    + team.getNo_urut() + "");
                    tv_amdp_referee.setText(
                            ctx.getApplicationContext().getString(R.string.referee) + " Juri " + referee.getNumber() + " - "
                                    + referee.getName());

                }
            }
        });
        teamref.collection("penilaian").document(referee.getKey())
                .collection("field").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot d2 : task.getResult()) {
                    if (d2.exists()) {
                        Penilaian penilaian = d2.toObject(Penilaian.class);
                        penilaian.setKey(d2.getId());
                        if (d2.getId().equals("et_as_n1")) {
                            tv_amdp_n1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_n2")) {
                            tv_amdp_n2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_n3")) {
                            tv_amdp_n3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_n4")) {
                            tv_amdp_n4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_n5")) {
                            tv_amdp_n5.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_n6")) {
                            tv_amdp_n6.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_n7")) {
                            tv_amdp_n7.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_n8")) {
                            tv_amdp_n8.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_n9")) {
                            tv_amdp_n9.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_n10")) {
                            tv_amdp_n10.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_ks1")) {
                            tv_amdp_ks1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_ks2")) {
                            tv_amdp_ks2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_ks3")) {
                            tv_amdp_ks3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_as_ks4")) {
                            tv_amdp_ks4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        }
                    }
                }
            }
        });

        teamref.collection("penilaian").document(referee.getKey())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            RefereePenilaian rp = value.toObject(RefereePenilaian.class);
                            rp.setKey(value.getId());

                            tv_amdp_total_nilai.setText(
                                    String.format("%.2f", rp.getTotal_nilai()) + "");
                            tv_amdp_total_pengurangan.setText(
                                    String.format("%.2f", rp.getTotal_potongan()) + "");

                            tv_amdp_grand_total.setText(
                                    String.format("%.2f", rp.getGrand_total()) + "");

                        }
                    }
                });


        alertDialogBuilder.setCancelable(true).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        ;
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void getNagaView(final Context ctx, final Referee referee, final Team team) {
        LayoutInflater li = LayoutInflater.from(ctx);
        View promptsView = li.inflate(R.layout.adapter_model_dialog_penilaian_naga, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ctx);
        alertDialogBuilder.setView(promptsView);
        //initiate data
        final TextView tv_amdp_team_name = promptsView.findViewById(R.id.tv_amdp_team_name_naga);
        final TextView tv_amdp_no_urut = promptsView.findViewById(R.id.tv_amdp_no_urut_naga);
        final TextView tv_amdp_referee = promptsView.findViewById(R.id.tv_amdp_referee_naga);
        final TextView tv_amdp_naga_n1 = promptsView.findViewById(R.id.tv_amdp_naga_n1);
        final TextView tv_amdp_naga_n2 = promptsView.findViewById(R.id.tv_amdp_naga_n2);
        final TextView tv_amdp_naga_n3 = promptsView.findViewById(R.id.tv_amdp_naga_n3);
        final TextView tv_amdp_naga_n4 = promptsView.findViewById(R.id.tv_amdp_naga_n4);
        final TextView tv_amdp_naga_n5 = promptsView.findViewById(R.id.tv_amdp_naga_n5);
        final TextView tv_amdp_naga_p1 = promptsView.findViewById(R.id.tv_amdp_naga_p1);
        final TextView tv_amdp_naga_p2 = promptsView.findViewById(R.id.tv_amdp_naga_p2);
        final TextView tv_amdp_naga_p3 = promptsView.findViewById(R.id.tv_amdp_naga_p3);
        final TextView tv_amdp_naga_p4 = promptsView.findViewById(R.id.tv_amdp_naga_p4);
        final TextView tv_amdp_naga_p5 = promptsView.findViewById(R.id.tv_amdp_naga_p5);
        final TextView tv_amdp_naga_kesulitan = promptsView.findViewById(R.id.tv_amdp_naga_kesulitan);

        final TextView tv_amdp_total_nilai_naga = promptsView.findViewById(R.id.tv_amdp_total_nilai_naga);
        final TextView tv_amdp_total_pengurangan_naga = promptsView.findViewById(R.id.tv_amdp_total_pengurangan_naga);
        final TextView tv_amdp_grand_total_naga = promptsView.findViewById(R.id.tv_amdp_grand_total_naga);


        //set data
        tv_amdp_naga_n1.setText("0");
        tv_amdp_naga_n2.setText("0");
        tv_amdp_naga_n3.setText("0");
        tv_amdp_naga_n4.setText("0");
        tv_amdp_naga_n5.setText("0");
        tv_amdp_naga_p1.setText("0");
        tv_amdp_naga_p2.setText("0");
        tv_amdp_naga_p3.setText("0");
        tv_amdp_naga_p4.setText("0");
        tv_amdp_naga_p5.setText("0");
        tv_amdp_naga_kesulitan.setText("");

        tv_amdp_total_nilai_naga.setText("0");
        tv_amdp_total_pengurangan_naga.setText("0");
        tv_amdp_grand_total_naga.setText("0");
        DocumentReference teamref = db.collection("events").document(events.getKey())
                .collection("team").document(team.getKey());
        teamref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
//                    team = documentSnapshot.toObject(Team.class);
//                    team.setKey(documentSnapshot.getId());
                    tv_amdp_team_name.setText(
                            ctx.getApplicationContext().getString(R.string.team_name) + " "
                                    + team.getTeam_name());
                    tv_amdp_no_urut.setText(
                            ctx.getApplicationContext().getString(R.string.no_urut) + " "
                                    + team.getNo_urut() + "");
                    tv_amdp_referee.setText(
                            ctx.getApplicationContext().getString(R.string.referee) + " Juri " + referee.getNumber() + " - "
                                    + referee.getName());

                }
            }
        });

        teamref.collection("penilaian").document(referee.getKey())
                .collection("field").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot d2 : task.getResult()) {
                    if (d2.exists()) {
                        Penilaian penilaian = d2.toObject(Penilaian.class);
                        penilaian.setKey(d2.getId());
                        if (d2.getId().equals("et_amdp_naga_n1")) {
                            tv_amdp_naga_n1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_naga_n2")) {
                            tv_amdp_naga_n2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_naga_n3")) {
                            tv_amdp_naga_n3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_naga_n4")) {
                            tv_amdp_naga_n4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_naga_n5")) {
                            tv_amdp_naga_n5.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_naga_p1")) {
                            tv_amdp_naga_p1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_naga_p2")) {
                            tv_amdp_naga_p2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_naga_p3")) {
                            tv_amdp_naga_p3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_naga_p4")) {
                            tv_amdp_naga_p4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_naga_p5")) {
                            tv_amdp_naga_p5.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_naga_kesulitan")) {
                            tv_amdp_naga_kesulitan.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        }
                    }
                }
            }
        });

        teamref.collection("penilaian").document(referee.getKey())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            RefereePenilaian rp = value.toObject(RefereePenilaian.class);
                            rp.setKey(value.getId());

                            tv_amdp_total_nilai_naga.setText(
                                    String.format("%.2f", rp.getTotal_nilai()) + "");
                            tv_amdp_total_pengurangan_naga.setText(
                                    String.format("%.2f", rp.getTotal_potongan()) + "");
                            tv_amdp_grand_total_naga.setText(
                                    String.format("%.2f", rp.getGrand_total()) + "");

                        }
                    }
                });

        alertDialogBuilder.setCancelable(true).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        ;
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void getPekingsaiView(final Context ctx, final Referee referee, final Team team) {
        LayoutInflater li = LayoutInflater.from(ctx);
        View promptsView = li.inflate(R.layout.adapter_model_dialog_penilaian_pekingsai, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ctx);
        alertDialogBuilder.setView(promptsView);
        //initiate data
        final TextView tv_ap_pekingsai_team_name = promptsView.findViewById(R.id.tv_ap_pekingsai_team_name);
        final TextView tv_ap_pekingsai_no_urut = promptsView.findViewById(R.id.tv_ap_pekingsai_no_urut);
        final TextView tv_amdp_referee_pekingsai = promptsView.findViewById(R.id.tv_amdp_referee_pekingsai);
        final TextView tv_amdp_pekingsai_n1 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n1);
        final TextView tv_amdp_pekingsai_n2 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n2);
        final TextView tv_amdp_pekingsai_n3 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n3);
        final TextView tv_amdp_pekingsai_n4 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n4);
        final TextView tv_amdp_pekingsai_n5 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n5);
        final TextView tv_amdp_pekingsai_n6 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n6);
        final TextView tv_amdp_pekingsai_n7 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n7);
        final TextView tv_amdp_pekingsai_n8 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n8);
        final TextView tv_amdp_pekingsai_n9 = promptsView.findViewById(R.id.tv_amdp_pekingsai_n9);
        final TextView tv_amdp_pekingsai_p1 = promptsView.findViewById(R.id.tv_amdp_pekingsai_p1);
        final TextView tv_amdp_pekingsai_p2 = promptsView.findViewById(R.id.tv_amdp_pekingsai_p2);
        final TextView tv_amdp_pekingsai_p3 = promptsView.findViewById(R.id.tv_amdp_pekingsai_p3);
        final TextView tv_amdp_pekingsai_p4 = promptsView.findViewById(R.id.tv_amdp_pekingsai_p4);
        final TextView tv_amdp_pekingsai_kesulitan = promptsView.findViewById(R.id.tv_amdp_pekingsai_kesulitan);
        final TextView tv_ap_pekingsai_total_penilaian = promptsView.findViewById(R.id.tv_ap_pekingsai_total_penilaian);
        final TextView tv_ap_pekingsai_nilai_total_pengurangan = promptsView.findViewById(R.id.tv_ap_pekingsai_nilai_total_pengurangan);
        final TextView tv_ap_pekingsai_grand_total = promptsView.findViewById(R.id.tv_ap_pekingsai_grand_total);


        //set data
        tv_amdp_pekingsai_n1.setText("0");
        tv_amdp_pekingsai_n2.setText("0");
        tv_amdp_pekingsai_n3.setText("0");
        tv_amdp_pekingsai_n4.setText("0");
        tv_amdp_pekingsai_n5.setText("0");
        tv_amdp_pekingsai_n6.setText("0");
        tv_amdp_pekingsai_n7.setText("0");
        tv_amdp_pekingsai_n8.setText("0");
        tv_amdp_pekingsai_n9.setText("0");
        tv_amdp_pekingsai_p1.setText("0");
        tv_amdp_pekingsai_p2.setText("0");
        tv_amdp_pekingsai_p3.setText("0");
        tv_amdp_pekingsai_p4.setText("0");
        tv_amdp_pekingsai_kesulitan.setText("");
        tv_ap_pekingsai_total_penilaian.setText("0");
        tv_ap_pekingsai_nilai_total_pengurangan.setText("0");
        tv_ap_pekingsai_grand_total.setText("0");
        DocumentReference teamref = db.collection("events").document(events.getKey())
                .collection("team").document(team.getKey());
        teamref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {
//                    team = documentSnapshot.toObject(Team.class);
//                    team.setKey(documentSnapshot.getId());
                    tv_ap_pekingsai_team_name.setText(
                            ctx.getApplicationContext().getString(R.string.team_name) + " "
                                    + team.getTeam_name());
                    tv_ap_pekingsai_no_urut.setText(
                            ctx.getApplicationContext().getString(R.string.no_urut) + " "
                                    + team.getNo_urut() + "");
                    tv_amdp_referee_pekingsai.setText(
                            ctx.getApplicationContext().getString(R.string.referee) + " Juri " + referee.getNumber() + " - "
                                    + referee.getName());

                }
            }
        });

        teamref.collection("penilaian").document(referee.getKey())
                .collection("field").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for (QueryDocumentSnapshot d2 : task.getResult()) {
                    if (d2.exists()) {
                        Penilaian penilaian = d2.toObject(Penilaian.class);
                        penilaian.setKey(d2.getId());
                        if (d2.getId().equals("et_amdp_pekingsai_n1")) {
                            tv_amdp_pekingsai_n1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_pekingsai_n2")) {
                            tv_amdp_pekingsai_n2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_pekingsai_n3")) {
                            tv_amdp_pekingsai_n3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_pekingsai_n4")) {
                            tv_amdp_pekingsai_n4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_pekingsai_n5")) {
                            tv_amdp_pekingsai_n5.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_pekingsai_n6")) {
                            tv_amdp_pekingsai_n6.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_pekingsai_n7")) {
                            tv_amdp_pekingsai_n7.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_pekingsai_n8")) {
                            tv_amdp_pekingsai_n8.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_pekingsai_n9")) {
                            tv_amdp_pekingsai_n9.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_ap_pekingsai_p1")) {
                            tv_amdp_pekingsai_p1.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_ap_pekingsai_p2")) {
                            tv_amdp_pekingsai_p2.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_ap_pekingsai_p3")) {
                            tv_amdp_pekingsai_p3.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_ap_pekingsai_p4")) {
                            tv_amdp_pekingsai_p4.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        } else if (d2.getId().equals("et_amdp_pekingsai_kesulitan")) {
                            tv_amdp_pekingsai_kesulitan.setText(String.format("%.2f", penilaian.getNilai()) + "");
                        }
                    }
                }
            }
        });

        teamref.collection("penilaian").document(referee.getKey())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable DocumentSnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                        if (value.exists()) {
                            RefereePenilaian rp = value.toObject(RefereePenilaian.class);
                            rp.setKey(value.getId());

                            tv_ap_pekingsai_total_penilaian.setText(
                                    String.format("%.2f", rp.getTotal_nilai()) + "");
                            tv_ap_pekingsai_nilai_total_pengurangan.setText(
                                    String.format("%.2f", rp.getTotal_potongan()) + "");
                            tv_ap_pekingsai_grand_total.setText(
                                    String.format("%.2f", rp.getGrand_total()) + "");

                        }
                    }
                });
        alertDialogBuilder.setCancelable(true).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        ;
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

}
